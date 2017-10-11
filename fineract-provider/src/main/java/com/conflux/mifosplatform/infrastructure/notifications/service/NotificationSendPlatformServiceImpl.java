package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetRowData;
import org.apache.fineract.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conflux.mifosplatform.infrastructure.notifications.api.NotificationApiConstants;
import com.conflux.mifosplatform.infrastructure.notifications.data.NotificationDataValidator;
import com.conflux.mifosplatform.infrastructure.notifications.domain.SmsNotification;
import com.conflux.mifosplatform.infrastructure.notifications.domain.SmsNotificationDetails;
import com.conflux.mifosplatform.infrastructure.notifications.repository.SmsNotificationDetailRepository;
import com.conflux.mifosplatform.infrastructure.notifications.repository.SmsNotificationRepository;
import com.conflux.mifosplatform.infrastructure.notifications.service.email.EmailSender;

@Service
public class NotificationSendPlatformServiceImpl implements NotificationSendPlatformService {

    private final PlatformSecurityContext context;
    private final NotificationDataValidator fromApiJsonDeserializer;
    private final EmailSender emailSender;
    private final SmsNotificationRepository smsNotificationRepository;
    private final SmsNotificationDetailRepository smsNotificationDetailRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationSendPlatformServiceImpl.class);
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private ExecutorService executorService;

    @Autowired
    public NotificationSendPlatformServiceImpl(final PlatformSecurityContext context,
            final NotificationDataValidator fromApiJsonDeserializer, final EmailSender emailSender,
            final SmsNotificationRepository smsNotificationRepository,
            final SmsNotificationDetailRepository smsNotificationDetailRepository,
            final ReadWriteNonCoreDataService readWriteNonCoreDataService) {

        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.emailSender = emailSender;
        this.smsNotificationRepository = smsNotificationRepository;
        this.smsNotificationDetailRepository = smsNotificationDetailRepository;
        this.readWriteNonCoreDataService = readWriteNonCoreDataService;
    }

    @PostConstruct
    public void init() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public CommandProcessingResult sendNotification(final JsonCommand command) {
        this.context.authenticatedUser();
        this.fromApiJsonDeserializer.validateForSend(command.json());

        final String type = command.stringValueOfParameterNamed(NotificationApiConstants.type);
        final String targetValueFromJson = command.stringValueOfParameterNamed(NotificationApiConstants.target);
        final String subject = command.stringValueOfParameterNamed(NotificationApiConstants.subject);
        final String message = command.stringValueOfParameterNamed(NotificationApiConstants.message);
        final Long officeId = command.longValueOfParameterNamed(NotificationApiConstants.entitiyId);
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();

        // This is added to remove new line from the number list.
        final String target = targetValueFromJson.replaceAll("\\r\\n|\\r|\\n", "");
        Boolean enabled = false;
        if (type.equalsIgnoreCase("email")) {
            this.emailSender.sendEmail(target, subject, message);
        } else if (type.equalsIgnoreCase("sms")) {
            if (officeId != null) {
                final GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet("OfficeDetails",
                        officeId.toString(), null, null);
                if (results != null) {
                    final ArrayList<ResultsetColumnHeaderData> header = (ArrayList<ResultsetColumnHeaderData>) results.getColumnHeaders();
                    final ArrayList<ResultsetRowData> data = (ArrayList<ResultsetRowData>) results.getData();
                    for (int i = 0; i < header.size(); i++) {
                        if (header.get(i).getColumnName().equalsIgnoreCase("sms_enabled")) {
                            if (data.get(0).getRow().get(i).equals("true")) {
                                enabled = true;
                                break;
                            }

                        }
                    }
                }
            }
            if (enabled) {
                final Date now = new Date();
                final SmsNotification smsNotification = new SmsNotification();
                smsNotification.setEntity("Notification");
                smsNotification.setAction("Send");
                smsNotification.setTenantId(tenantIdentifier);
                smsNotification.setPayload("manual sms");
                smsNotification.setProcessed(Boolean.TRUE);
                smsNotification.setCreatedOn(now);
                smsNotification.setLastModifiedOn(now);
                final Long eventId = this.smsNotificationRepository.save(smsNotification).getId();
                this.executorService.execute(new SMSTask(eventId, target, message, ThreadLocalContextUtil.getTenant()));
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .build();
    }

    class SMSTask implements Runnable {

        private final Long eventId;
        private final String targetJson;
        private final String message;
        private final FineractPlatformTenant tenant;

        SMSTask(final Long eventId, final String targetJson, final String message, final FineractPlatformTenant tenant) {
            this.eventId = eventId;
            this.targetJson = targetJson;
            this.message = message;
            this.tenant = tenant;
        }

        @Override
        public void run() {
            logger.info("Trying to sent messages by executor----");

            final Date now = new Date();
            // Setting the tenant to thread context before starting any activity
            ThreadLocalContextUtil.setTenant(this.tenant);
            final SMSSender smsSender = NotificationsConfiguration.getInstance().getSenderForSMSProvider();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
            final String[] clientDetails = this.targetJson.split(",");
            for (int i = 0; i < clientDetails.length; i++) {
                String clientName = " ";
                String clientId = " ";
                final String[] clientMobileNoAndName = clientDetails[i].split("-");
                final int length = clientMobileNoAndName.length;
                if (length >= 2) {
                    clientName = clientMobileNoAndName[1];
                    clientId = clientMobileNoAndName[2];
                }
                final String mobileNo = clientMobileNoAndName[0];
                logger.info("clientName-" + clientName + "," + "mobileNo" + mobileNo);
                final SmsNotificationDetails SmsNotificationDetails = new SmsNotificationDetails();
                SmsNotificationDetails.setAction("Send");
                SmsNotificationDetails.setEntity("Notification");
                SmsNotificationDetails.setEntity_Mobile_No(mobileNo);
                SmsNotificationDetails.setEventId(this.eventId);
                SmsNotificationDetails.setPayload("clientName:" + clientName + " " + "MobileNo:" + mobileNo);
                SmsNotificationDetails.setMessage(this.message);
                SmsNotificationDetails.setEntityName("Manual");
                SmsNotificationDetails.setEntitydescription("clientId:" + clientId + " " + "clientName:" + clientName);
                SmsNotificationDetails.setTenantId(tenantIdentifier);
                SmsNotificationDetails.setProcessed(Boolean.FALSE);
                SmsNotificationDetails.setCreatedOn(now);
                SmsNotificationDetails.setLastModifiedOn(now);
                if (mobileNo.equals("null") || mobileNo.equalsIgnoreCase("NA") || mobileNo.equalsIgnoreCase("  ")
                        || mobileNo.length() <= 0) {
                    SmsNotificationDetails.setErrorMessage("Mobile number is not Valid");
                    NotificationSendPlatformServiceImpl.this.smsNotificationDetailRepository.save(SmsNotificationDetails);
                } else {
                    try {
                        final JSONArray response = smsSender.sendmsg(mobileNo, this.message);
                        if (response != null && response.length() != 0) {
                            final JSONObject result = response.getJSONObject(0);
                            logger.info(result.getString("status") + ",");
                            logger.info(result.getString("number") + ",");
                            logger.info(result.getString("messageId") + ",");
                            logger.info(result.getString("cost"));

                            if (result.getString("status").equals("success") || result.getString("status").equalsIgnoreCase("success")) {
                                SmsNotificationDetails.setProcessed(Boolean.TRUE);
                            }
                            SmsNotificationDetails.setCreatedOn(now);
                            SmsNotificationDetails.setLastModifiedOn(now);
                            NotificationSendPlatformServiceImpl.this.smsNotificationDetailRepository.save(SmsNotificationDetails);
                        }
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

}
