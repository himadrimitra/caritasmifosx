package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.conflux.mifosplatform.infrastructure.notifications.api.NotificationApiConstants;
import com.conflux.mifosplatform.infrastructure.notifications.data.NotificationDataValidator;
import com.conflux.mifosplatform.infrastructure.notifications.service.email.EmailSender;

@Service
public class NotificationSendPlatformServiceImpl implements NotificationSendPlatformService {

    private final PlatformSecurityContext context;
    private final NotificationDataValidator fromApiJsonDeserializer;
    private final EmailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(NotificationSendPlatformServiceImpl.class);
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private ExecutorService executorService;
    private final SmsMessageScheduledJobService smsMessageScheduledJobService;
    private final ClientRepositoryWrapper clientRepository;

    @Autowired
    public NotificationSendPlatformServiceImpl(final PlatformSecurityContext context,
            final NotificationDataValidator fromApiJsonDeserializer, final EmailSender emailSender,
            final ReadWriteNonCoreDataService readWriteNonCoreDataService,
            final SmsMessageScheduledJobService smsMessageScheduledJobService, final ClientRepositoryWrapper clientRepository) {

        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.emailSender = emailSender;
        this.readWriteNonCoreDataService = readWriteNonCoreDataService;
        this.smsMessageScheduledJobService = smsMessageScheduledJobService;
        this.clientRepository = clientRepository;
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
        final Long providerId = command.longValueOfParameterNamed(NotificationApiConstants.providerId);

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
                this.executorService.execute(new SMSTask(target, message, ThreadLocalContextUtil.getTenant(),
                        this.smsMessageScheduledJobService, providerId, this.clientRepository));
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .build();
    }

    class SMSTask implements Runnable {

        private final String targetJson;
        private final String message;
        private final FineractPlatformTenant tenant;
        private final SmsMessageScheduledJobService smsMessageScheduledJobService;
        private final Long providerId;
        private final ClientRepositoryWrapper clientRepository;

        SMSTask(final String targetJson, final String message, final FineractPlatformTenant tenant,
                final SmsMessageScheduledJobService smsMessageScheduledJobService, final Long providerId,
                final ClientRepositoryWrapper clientRepository) {
            this.targetJson = targetJson;
            this.message = message;
            this.tenant = tenant;
            this.smsMessageScheduledJobService = smsMessageScheduledJobService;
            this.providerId = providerId;
            this.clientRepository = clientRepository;
        }

        @Override
        public void run() {
            logger.info("Trying to sent messages by executor----");

            // Setting the tenant to thread context before starting any activity
            ThreadLocalContextUtil.setTenant(this.tenant);
            final String[] clientDetails = this.targetJson.split(",");
            final List<SmsMessage> smsMessages = new ArrayList<>();
            for (int i = 0; i < clientDetails.length; i++) {
                String clientName = " ";
                String clientId = " ";
                final String[] clientMobileNoAndName = clientDetails[i].split("-");
                final int length = clientMobileNoAndName.length;
                Client client = null;
                if (length >= 2) {
                    clientName = clientMobileNoAndName[1];
                    clientId = clientMobileNoAndName[2];
                    client = this.clientRepository.findOneWithNotFoundDetection(Long.valueOf(clientId));
                }
                final String mobileNo = clientMobileNoAndName[0];
                logger.info("clientName-" + clientName + "," + "mobileNo" + mobileNo);
                if (StringUtils.isNotBlank(mobileNo) && !mobileNo.equals("null") && !mobileNo.equalsIgnoreCase("NA")) {
                    final SmsMessage smsMessage = SmsMessage.pendingSms(null, null, client, null, this.message, mobileNo, null);
                    smsMessages.add(smsMessage);
                }
            }
            if (!CollectionUtils.isEmpty(smsMessages)) {
                this.smsMessageScheduledJobService.sendTriggeredMessage(smsMessages, this.providerId);
            }
        }

    }

}
