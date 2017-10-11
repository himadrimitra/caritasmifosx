/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.sms.scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.reportmailingjob.helper.IPv4Helper;
import org.apache.fineract.infrastructure.sms.data.SmsConfigurationData;
import org.apache.fineract.infrastructure.sms.data.SmsData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageApiQueueResourceData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageApiReportResourceData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageApiResponseData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageDeliveryReportData;
import org.apache.fineract.infrastructure.sms.data.TenantSmsConfiguration;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageStatusType;
import org.apache.fineract.infrastructure.sms.service.SmsReadPlatformService;
import org.apache.fineract.infrastructure.sms.domain.SmsConfigurationRepository;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobServiceImpl;
import org.apache.fineract.infrastructure.sms.service.SmsConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.sms.domain.SmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Scheduled job services that send SMS messages and get delivery reports for the sent SMS messages
 **/
@Service
public class SmsMessageScheduledJobServiceImpl implements SmsMessageScheduledJobService {
	
	private final SmsMessageRepository smsMessageRepository;
	private final SmsConfigurationRepository smsConfigurationRepository;
	private final SmsReadPlatformService smsReadPlatformService;
	private final SmsConfigurationReadPlatformService configurationReadPlatformService;
	private static final Logger logger = LoggerFactory.getLogger(SmsMessageScheduledJobServiceImpl.class);
	private final RestTemplate restTemplate = new RestTemplate();
    
    /** 
	 * SmsMessageScheduledJobServiceImpl constructor
	 **/
	@Autowired
	public SmsMessageScheduledJobServiceImpl(SmsMessageRepository smsMessageRepository, 
			SmsConfigurationReadPlatformService readPlatformService, 
			SmsReadPlatformService smsReadPlatformService,
			SmsConfigurationRepository smsConfigurationRepository) {
		this.smsMessageRepository = smsMessageRepository;
		this.smsConfigurationRepository = smsConfigurationRepository;
		this.configurationReadPlatformService = readPlatformService;
		this.smsReadPlatformService = smsReadPlatformService;
	}
	
	/** 
     * get the tenant's sms configuration 
     * 
     * @return {@link TenantSmsConfiguration} object
     **/
    @Override
    public TenantSmsConfiguration getTenantSmsConfiguration() {
        Collection<SmsConfigurationData> configurationDataCollection = configurationReadPlatformService.retrieveAll();
        return TenantSmsConfiguration.instance(configurationDataCollection);
    }
	
	/** 
	 * get a new HttpEntity with the provided body
	 **/
	@SuppressWarnings("unused")
	private HttpEntity<String> getHttpEntity(String body, String apiAuthUsername, String apiAuthPassword) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String authorization = apiAuthUsername + ":" + apiAuthPassword;

        byte[] encodedAuthorisation = Base64.encode(authorization.getBytes());
        //headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
		
		return new HttpEntity<String>(body, headers);
	}
	
	/** 
	 * prevents the SSL security certificate check 
	 **/
    private void trustAllSSLCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @SuppressWarnings("unused")
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @SuppressWarnings("unused")
            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };
		
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {

                @SuppressWarnings("unused")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        }

        catch (Exception e) {
            // do nothing
        }
    }
	
	/** 
     * Format destination phone number so it is in international format without the leading
     * "0" or "+", example: 31612345678 
     * 
     * @param phoneNumber the phone number to be formated
     * @param countryCallingCode the country calling code
     * @return phone number in international format
     **/
    private String formatDestinationPhoneNumber(String phoneNumber, String countryCallingCode) {
    	String formatedPhoneNumber = "";
    	
    	try {
    		Long phoneNumberToLong = Long.parseLong(phoneNumber);
    		Long countryCallingCodeToLong = Long.parseLong(countryCallingCode);
    		formatedPhoneNumber = Long.toString(countryCallingCodeToLong) + Long.toString(phoneNumberToLong);
    	}
    	
    	catch(Exception e) {
    		logger.error("Invalid phone number or country calling code, must contain only numbers", e);
    	}
    	
    	return formatedPhoneNumber;
    }

	/**
	 * Send batches of SMS messages to the SMS gateway (or intermediate gateway)
	 **/
	@Override
	@Transactional
	@CronTarget(jobName = JobName.SEND_MESSAGES_TO_SMS_GATEWAY)
	public void sendMessages() {
		if (IPv4Helper.applicationIsNotRunningOnLocalMachine()) {
	        final TenantSmsConfiguration tenantSmsConfiguration = this.getTenantSmsConfiguration();
	        Integer smsCredits = tenantSmsConfiguration.getSmsCredits();
	        Integer smsSqlLimit = 5000;
	        if(smsCredits > 0) {
	            try{
	                smsSqlLimit = (smsSqlLimit > smsCredits) ? smsCredits : smsSqlLimit;
	                final Collection<SmsData> pendingMessages = this.smsReadPlatformService.retrieveAllPending(smsSqlLimit);
	                sendMessagesProcess(tenantSmsConfiguration, pendingMessages);
	            }
	            catch(Exception e) {
	                logger.error(e.getMessage(), e);
	            }
	        }
	    }
	}
	
    @Override
    public void sendMessagesProcess(TenantSmsConfiguration tenantSmsConfiguration, final Collection<SmsData> pendingMessages) {
        if(tenantSmsConfiguration == null){
            tenantSmsConfiguration = this.getTenantSmsConfiguration();
        }
        final String apiAuthUsername = tenantSmsConfiguration.getApiAuthUsername();
        final String apiAuthPassword = tenantSmsConfiguration.getApiAuthPassword();
        final String apiBaseUrl = tenantSmsConfiguration.getApiBaseUrl();
        final String sourceAddress = tenantSmsConfiguration.getSourceAddress();
        final String countryCallingCode = tenantSmsConfiguration.getCountryCallingCode();
        final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        Integer smsCredits = tenantSmsConfiguration.getSmsCredits();
        final int httpEntityLimit = 500;
        final int httpEntityLimitMinusOne = httpEntityLimit - 1;
        if (pendingMessages.size() > 0) {
            Iterator<SmsData> pendingMessageIterator = pendingMessages.iterator();
            int index = 0;

            // ====================== start point json string
            // ======================================
            StringBuilder httpEntity = new StringBuilder("[");

            while (pendingMessageIterator.hasNext()) {
                SmsData smsData = pendingMessageIterator.next();
                SmsMessageApiQueueResourceData apiQueueResourceData = SmsMessageApiQueueResourceData.instance(smsData.getId(),
                        tenant.getTenantIdentifier(), null, sourceAddress,
                        formatDestinationPhoneNumber(smsData.getMobileNo(), countryCallingCode), smsData.getMessage());

                httpEntity.append(apiQueueResourceData.toJsonString());

                index++;

                if (index == httpEntityLimitMinusOne) {
                    httpEntity.append("]");

                    smsCredits = this.sendMessages(httpEntity, apiAuthUsername, apiAuthPassword, apiBaseUrl, smsCredits, sourceAddress);

                    index = 0;
                    httpEntity = new StringBuilder("[");
                }

                // add comma separation if iterator has more elements
                if (pendingMessageIterator.hasNext() && (index > 0)) {
                    httpEntity.append(", ");
                }
            }

            httpEntity.append("]");
            // ====================== end point json string
            // ====================================

            smsCredits = this.sendMessages(httpEntity, apiAuthUsername, apiAuthPassword, apiBaseUrl, smsCredits, sourceAddress);

            logger.info(pendingMessages.size() + " pending message(s) successfully sent to the intermediate gateway - mlite-sms");

            SmsConfiguration smsConfiguration = this.smsConfigurationRepository.findByName("SMS_CREDITS");
            smsConfiguration.setValue(smsCredits.toString());

            // save the SmsConfiguration entity
            this.smsConfigurationRepository.save(smsConfiguration);
        }
    }

	/**
	 * handles the sending of messages to the intermediate gateway and updating of the external ID, status and sources address
	 * of each message
	 * 
	 * @param httpEntity
	 * @param apiAuthUsername
	 * @param apiAuthPassword
	 * @param apiBaseUrl
	 * @param smsCredits
	 * @param sourceAddress
	 * @return the number of SMS credits left
	 */
	private Integer sendMessages(final StringBuilder httpEntity, final String apiAuthUsername, 
	        final String apiAuthPassword, final String apiBaseUrl, Integer smsCredits, final String sourceAddress) {
	    // trust all SSL certificates
        trustAllSSLCertificates();
        
        // make request
        HttpEntity<String> s = getHttpEntity(httpEntity.toString(), apiAuthUsername, apiAuthPassword);
		final ResponseEntity<SmsMessageApiResponseData> entity = restTemplate
				.postForEntity(apiBaseUrl + "/queue", s,
						SmsMessageApiResponseData.class);
        
        final List<SmsMessageDeliveryReportData> smsMessageDeliveryReportDataList = entity.getBody().getData();
        final Iterator<SmsMessageDeliveryReportData> deliveryReportIterator = smsMessageDeliveryReportDataList.iterator();
        
        while(deliveryReportIterator.hasNext()) {
            SmsMessageDeliveryReportData smsMessageDeliveryReportData = deliveryReportIterator.next();
            
            if(!smsMessageDeliveryReportData.getHasError()) {
                SmsMessage smsMessage = this.smsMessageRepository.findOne(smsMessageDeliveryReportData.getId());
                
                // initially set the status type enum to 100
                Integer statusType = SmsMessageStatusType.PENDING.getValue();
                
                switch(smsMessageDeliveryReportData.getDeliveryStatus()) {
                    case 100:
                    case 200:
                        statusType = SmsMessageStatusType.SENT.getValue();
                        break;
                        
                    case 300:
                        statusType = SmsMessageStatusType.DELIVERED.getValue();
                        break;
                        
                    case 400:
                        statusType = SmsMessageStatusType.FAILED.getValue();
                        break;
                        
                    default:
                        statusType = SmsMessageStatusType.INVALID.getValue();
                        break;
                }
                
                // update the externalId of the SMS message
                smsMessage.setExternalId(smsMessageDeliveryReportData.getExternalId());
                
                // update the SMS message sender
                smsMessage.setSourceAddress(sourceAddress);
                
                // update the status Type enum
                smsMessage.setStatusType(statusType);
                
                // save the SmsMessage entity
                this.smsMessageRepository.save(smsMessage);
                
                // deduct one credit from the tenant's SMS credits
                smsCredits--;
            }
        }
        
        return smsCredits;
	}

	/**
	 * get SMS message delivery reports from the SMS gateway (or intermediate gateway)
	 **/
	@Override
	@Transactional
	@CronTarget(jobName = JobName.GET_DELIVERY_REPORTS_FROM_SMS_GATEWAY)
	public void getDeliveryReports() {
		if (IPv4Helper.applicationIsNotRunningOnLocalMachine()) {
	        final TenantSmsConfiguration tenantSmsConfiguration = this.getTenantSmsConfiguration();
	        final String apiAuthUsername = tenantSmsConfiguration.getApiAuthUsername();
	        final String apiAuthPassword = tenantSmsConfiguration.getApiAuthPassword();
	        final String apiBaseUrl = tenantSmsConfiguration.getApiBaseUrl();
	        final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
	        
	        try{
	            List<Long> smsMessageExternalIds = this.smsReadPlatformService.retrieveExternalIdsOfAllSent(0);
	            SmsMessageApiReportResourceData smsMessageApiReportResourceData = 
	                    SmsMessageApiReportResourceData.instance(smsMessageExternalIds, tenant.getTenantIdentifier());
	            
	            // only proceed if there are sms message with status type enum 200
	            if(smsMessageExternalIds.size() > 0) {
	                // trust all SSL certificates
	                trustAllSSLCertificates();
	                
	                // make request
	                ResponseEntity<SmsMessageApiResponseData> entity = restTemplate.postForEntity(apiBaseUrl + "/report",
	                        getHttpEntity(smsMessageApiReportResourceData.toJsonString(), apiAuthUsername, apiAuthPassword), 
	                        SmsMessageApiResponseData.class);
	                
	                List<SmsMessageDeliveryReportData> smsMessageDeliveryReportDataList = entity.getBody().getData();
	                Iterator<SmsMessageDeliveryReportData> iterator1 = smsMessageDeliveryReportDataList.iterator();
	                
	                while(iterator1.hasNext()) {
	                    SmsMessageDeliveryReportData smsMessageDeliveryReportData = iterator1.next();
	                    Integer deliveryStatus = smsMessageDeliveryReportData.getDeliveryStatus();
	                    
	                    if(!smsMessageDeliveryReportData.getHasError() && (deliveryStatus != 100 && deliveryStatus != 200)) {
	                        SmsMessage smsMessage = this.smsMessageRepository.findOne(smsMessageDeliveryReportData.getId());
	                        Integer statusType = smsMessage.getStatusType();
	                        boolean statusChanged = false;
	                        
	                        switch(deliveryStatus) {
	                            case 0:
	                                statusType = SmsMessageStatusType.INVALID.getValue();
	                                break;
	                            case 300:
	                                statusType = SmsMessageStatusType.DELIVERED.getValue();
	                                break;
	                                
	                            case 400:
	                                statusType = SmsMessageStatusType.FAILED.getValue();
	                                break;
	                                
	                            default:
	                                statusType = smsMessage.getStatusType();
	                                break;
	                        }
	                        
	                        statusChanged = !statusType.equals(smsMessage.getStatusType());
	                        
	                        // update the status Type enum
	                        smsMessage.setStatusType(statusType);
	                        
	                        // save the SmsMessage entity
	                        this.smsMessageRepository.save(smsMessage);
	                        
	                        if (statusChanged) {
	                            logger.info("Status of SMS message id: " + smsMessage.getId() + " successfully changed to " + statusType);
	                        }
	                    }
	                }
	                
	                if(smsMessageDeliveryReportDataList.size() > 0) {
	                    logger.info(smsMessageDeliveryReportDataList.size() + " "
	                            + "delivery report(s) successfully received from the intermediate gateway - mlite-sms");
	                }
	            }
	        }
	        
	        catch(Exception e) {
	            logger.error(e.getMessage(), e);
	        }
	    }
	}
}
