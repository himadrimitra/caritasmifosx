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
package org.apache.fineract.infrastructure.sms.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.domain.Report;
import org.apache.fineract.infrastructure.dataqueries.domain.ReportRepository;
import org.apache.fineract.infrastructure.dataqueries.exception.ReportNotFoundException;
import org.apache.fineract.infrastructure.dataqueries.service.GenericDataService;
import org.apache.fineract.infrastructure.dataqueries.service.ReadReportingService;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.sms.SmsApiConstants;
import org.apache.fineract.infrastructure.sms.data.PreviewCampaignMessage;
import org.apache.fineract.infrastructure.sms.data.SmsCampaignData;
import org.apache.fineract.infrastructure.sms.data.SmsCampaignValidator;
import org.apache.fineract.infrastructure.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.sms.domain.SmsCampaignRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.infrastructure.sms.exception.SmsCampaignMustBeClosedToBeDeletedException;
import org.apache.fineract.infrastructure.sms.exception.SmsCampaignMustBeClosedToEditException;
import org.apache.fineract.infrastructure.sms.exception.SmsCampaignNotFound;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Service
public class SmsCampaignWritePlatformCommandHandlerImpl implements SmsCampaignWritePlatformService {


    private final static Logger logger = LoggerFactory.getLogger(SmsCampaignWritePlatformCommandHandlerImpl.class);

    private final PlatformSecurityContext context;

    private final SmsCampaignRepository smsCampaignRepository;
    private final SmsCampaignValidator smsCampaignValidator;
    private final SmsCampaignReadPlatformService smsCampaignReadPlatformService;
    private final ReportRepository reportRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final ClientRepository clientRepository;
    private final ReadReportingService readReportingService;
    private final GenericDataService genericDataService;
    private final FromJsonHelper fromJsonHelper;



    @Autowired
    public SmsCampaignWritePlatformCommandHandlerImpl(final PlatformSecurityContext context,
            final SmsCampaignRepository smsCampaignRepository, final SmsCampaignValidator smsCampaignValidator,
            final SmsCampaignReadPlatformService smsCampaignReadPlatformService, final ReportRepository reportRepository,
            final SmsMessageRepository smsMessageRepository, final ClientRepository clientRepository,
            final ReadReportingService readReportingService, final GenericDataService genericDataService,
            final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.smsCampaignRepository = smsCampaignRepository;
        this.smsCampaignValidator = smsCampaignValidator;
        this.smsCampaignReadPlatformService = smsCampaignReadPlatformService;
        this.reportRepository = reportRepository;
        this.smsMessageRepository = smsMessageRepository;
        this.clientRepository = clientRepository;
        this.readReportingService = readReportingService;
        this.genericDataService = genericDataService;
        this.fromJsonHelper = fromJsonHelper;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();

        this.smsCampaignValidator.validateCreate(command.json());

        final Long runReportId = command.longValueOfParameterNamed(SmsCampaignValidator.runReportId);

        final Report report  = this.reportRepository.findOne(runReportId);
        if(report == null){
            throw new ReportNotFoundException(runReportId);
        }

        SmsCampaign smsCampaign = SmsCampaign.instance(currentUser,report,command);

        this.smsCampaignRepository.save(smsCampaign);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(smsCampaign.getId()) //
                .build();
    }
    @Transactional
    @Override
    public CommandProcessingResult update(final Long resourceId, final JsonCommand command) {
        try{
            this.context.authenticatedUser();

            this.smsCampaignValidator.validateForUpdate(command.json());
            final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(resourceId);

            if(smsCampaign == null){ throw new SmsCampaignNotFound(resourceId);}
            if(smsCampaign.isActive()){ throw new SmsCampaignMustBeClosedToEditException(smsCampaign.getId());}
            final Map<String, Object> changes = smsCampaign.update(command);

            if(changes.containsKey(SmsCampaignValidator.runReportId)){
                final Long newValue = command.longValueOfParameterNamed(SmsCampaignValidator.runReportId);
                final Report reportId = this.reportRepository.findOne(newValue);
                if(reportId == null){ throw new ReportNotFoundException(newValue);};
                smsCampaign.updateBusinessRuleId(reportId);
            }

            if(!changes.isEmpty()){
                this.smsCampaignRepository.saveAndFlush(smsCampaign);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(resourceId) //
                    .with(changes) //
                    .build();
        }catch(final DataIntegrityViolationException dve){
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }

    }
    @Transactional
    @Override
    public CommandProcessingResult delete(final Long resourceId) {
        this.context.authenticatedUser();

        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(resourceId);

        if(smsCampaign == null){ throw new SmsCampaignNotFound(resourceId);}
        if(smsCampaign.isActive()){ throw new SmsCampaignMustBeClosedToBeDeletedException(smsCampaign.getId());}

        /*
          Do not delete but set a boolean is_visible to zero
         */
        smsCampaign.delete();
        this.smsCampaignRepository.saveAndFlush(smsCampaign);

        return new CommandProcessingResultBuilder() //
                .withEntityId(smsCampaign.getId()) //
                .build();

    }


    private void insertDirectCampaignIntoSmsOutboundTable(final String smsParams,
                                                          final String textMessageTemplate,final String campaignName){
        try{
            HashMap<String,String> campaignParams = new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            HashMap<String,String> queryParamForRunReport =  new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            List<HashMap<String,Object>> runReportObject = this.getRunReportByServiceImpl(campaignParams.get("reportName"),queryParamForRunReport);

            if(runReportObject !=null){
                for(HashMap<String,Object> entry : runReportObject){
                    String textMessage = this.compileSmsTemplate(textMessageTemplate, campaignName, entry);
                    Integer clientId = (Integer)entry.get("id");
                    Object mobileNo = entry.get("mobileNo");

                    Client client =  this.clientRepository.findOne(clientId.longValue());
                    if (mobileNo != null) {
                        SmsMessage smsMessage = SmsMessage.pendingSms(null, null, null, null, client, null, textMessage, null,
                                mobileNo.toString(), campaignName);
                        this.smsMessageRepository.save(smsMessage);
                    }
                }
            }
        }catch(final IOException e){
            // TODO throw something here
        }

    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_SMS_OUTBOUND_WITH_CAMPAIGN_MESSAGE)
    public void storeTemplateMessageIntoSmsOutBoundTable() throws JobExecutionException {
        final Collection<SmsCampaignData>  smsCampaignDataCollection = this.smsCampaignReadPlatformService.retrieveAllScheduleActiveCampaign();
        if(smsCampaignDataCollection != null){
            for(SmsCampaignData  smsCampaignData : smsCampaignDataCollection){
                LocalDateTime tenantDateNow = tenantDateTime();
                LocalDateTime nextTriggerDate = smsCampaignData.getNextTriggerDate().toLocalDateTime();

                logger.info("tenant time " + tenantDateNow.toString() + " trigger time "+nextTriggerDate.toString());
                if(nextTriggerDate.isBefore(tenantDateNow)){
                    insertDirectCampaignIntoSmsOutboundTable(smsCampaignData.getParamValue(),smsCampaignData.getMessage(),smsCampaignData.getCampaignName());
                    this.updateTriggerDates(smsCampaignData.getId());
                }
            }
        }
    }

    private void updateTriggerDates(Long campaignId){
        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);
        if(smsCampaign == null){
            throw new SmsCampaignNotFound(campaignId);
        }
        LocalDateTime nextTriggerDate = smsCampaign.getNextTriggerDate();
        smsCampaign.setLastTriggerDate(nextTriggerDate.toDate());
        //calculate new trigger date and insert into next trigger date

        /**
         * next run time has to be in the future if not calculate a new future date
         */
        Map<String,Object> recurringRuleMap = createRecurringRuleMap(smsCampaign.getRecurrence());
    	String frequency = (String) recurringRuleMap.get("FREQ");
    	if(frequency.equalsIgnoreCase(SmsApiConstants.minutely) || frequency.equalsIgnoreCase(SmsApiConstants.secondly) || frequency.equalsIgnoreCase(SmsApiConstants.hourly)){
    		String recurrence = smsCampaign.getRecurrence();
            LocalDateTime nextTriggerDateTime = getNextTriggerDate(createRecurringRuleMap(recurrence), smsCampaign.getNextTriggerDate());
            if(nextTriggerDateTime.isBefore(new LocalDateTime())){ 
            	nextTriggerDateTime = getNextTriggerDate(createRecurringRuleMap(recurrence), new LocalDateTime());
            }
            final String dateString = nextTriggerDateTime.toLocalDate().toString() + " " + nextTriggerDateTime.getHourOfDay()+":"+nextTriggerDateTime.getMinuteOfHour()+":"+nextTriggerDateTime.getSecondOfMinute();
            final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            final LocalDateTime newTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);
            smsCampaign.setNextTriggerDate(newTriggerDateWithTime.toDate());
            this.smsCampaignRepository.saveAndFlush(smsCampaign);
    	}else{
    		LocalDate nextRuntime = CalendarUtils.getNextRecurringDate(smsCampaign.getRecurrence(), smsCampaign.getNextTriggerDate().toLocalDate(),nextTriggerDate.toLocalDate()) ;
	        if(nextRuntime.isBefore(DateUtils.getLocalDateOfTenant())){ // means next run time is in the past calculate a new future date
	            nextRuntime = CalendarUtils.getNextRecurringDate(smsCampaign.getRecurrence(), smsCampaign.getNextTriggerDate().toLocalDate(),DateUtils.getLocalDateOfTenant()) ;
	        }
	        LocalDateTime adjustTime = smsCampaign.getRecurrenceStartDateTime();
	        if(recurringRuleMap.get("BYHOUR") != null){
	        	int hour = Integer.parseInt((String)recurringRuleMap.get("BYHOUR"));
	        	adjustTime = adjustTime.withHourOfDay(hour);
	        }
	        if(recurringRuleMap.get("BYMINUTE") != null){
	        	int minute = Integer.parseInt((String)recurringRuleMap.get("BYMINUTE"));
	        	adjustTime = adjustTime.withMinuteOfHour(minute);
	        }
	        if(recurringRuleMap.get("BYSECOND") != null){
	        	int second = Integer.parseInt((String)recurringRuleMap.get("BYSECOND"));
	        	adjustTime = adjustTime.withSecondOfMinute(second);
	        }	        
	        final String dateString = nextRuntime.toString() + " " + adjustTime.getHourOfDay()+":"+adjustTime.getMinuteOfHour()+":"+adjustTime.getSecondOfMinute();
	        final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	        final LocalDateTime newTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);
	
	
	        smsCampaign.setNextTriggerDate(newTriggerDateWithTime.toDate());
	        this.smsCampaignRepository.saveAndFlush(smsCampaign);
    	}
    }

    @Transactional
    @Override
    public CommandProcessingResult activateSmsCampaign(Long campaignId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();

        this.smsCampaignValidator.validateActivation(command.json());

        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);

        if(smsCampaign == null){
            throw new SmsCampaignNotFound(campaignId);
        }



        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");

        smsCampaign.activate(currentUser,fmt,activationDate);

        this.smsCampaignRepository.saveAndFlush(smsCampaign);

        if(smsCampaign.isDirect()){
            insertDirectCampaignIntoSmsOutboundTable(smsCampaign.getParamValue(),smsCampaign.getMessage(),smsCampaign.getCampaignName());
        }else {
            if (smsCampaign.isSchedule()) {

                /**
                 * if recurrence start date is in the future calculate
                 * next trigger date if not use recurrence start date us next trigger
                 * date when activating
                 */
            	Map<String,Object> recurringRuleMap = createRecurringRuleMap(smsCampaign.getRecurrence());
            	String frequency = (String) recurringRuleMap.get("FREQ");
            	if(frequency.equalsIgnoreCase(SmsApiConstants.minutely) || frequency.equalsIgnoreCase(SmsApiConstants.secondly) || frequency.equalsIgnoreCase(SmsApiConstants.hourly)){
            		final LocalDateTime nextTriggerDateWithTime = getNextParsedLocalDateTime(smsCampaign.getRecurrence(), smsCampaign.getRecurrenceStartDateTime(), DateUtils.getLocalDateTimeOfTenant());
                    smsCampaign.setNextTriggerDate(nextTriggerDateWithTime.toDate());
                    this.smsCampaignRepository.saveAndFlush(smsCampaign);
            	}else{
            		LocalDate nextTriggerDate = null;
	                if(smsCampaign.getRecurrenceStartDateTime().isBefore(tenantDateTime())){
	                    nextTriggerDate = CalendarUtils.getNextRecurringDate(smsCampaign.getRecurrence(), smsCampaign.getRecurrenceStartDate(), DateUtils.getLocalDateOfTenant());
	                }else{
	                    nextTriggerDate = smsCampaign.getRecurrenceStartDate();
	                }
	    	        LocalDateTime adjustTime = smsCampaign.getRecurrenceStartDateTime();
	    	        if(recurringRuleMap.get("BYHOUR") != null){
	    	        	int hour = Integer.parseInt((String)recurringRuleMap.get("BYHOUR"));
	    	        	adjustTime = adjustTime.withHourOfDay(hour);
	    	        }
	    	        if(recurringRuleMap.get("BYMINUTE") != null){
	    	        	int minute = Integer.parseInt((String)recurringRuleMap.get("BYMINUTE"));
	    	        	adjustTime = adjustTime.withMinuteOfHour(minute);
	    	        }
	    	        if(recurringRuleMap.get("BYSECOND") != null){
	    	        	int second = Integer.parseInt((String)recurringRuleMap.get("BYSECOND"));
	    	        	adjustTime = adjustTime.withSecondOfMinute(second);
	    	        }	        
	    	        final String dateString = nextTriggerDate.toString() + " " + adjustTime.getHourOfDay()+":"+adjustTime.getMinuteOfHour()+":"+adjustTime.getSecondOfMinute();
	    	        final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	                final LocalDateTime nextTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);
	
	                smsCampaign.setNextTriggerDate(nextTriggerDateWithTime.toDate());
	                this.smsCampaignRepository.saveAndFlush(smsCampaign);
            	}
            }
        }

        /*
          if campaign is direct insert campaign message into sms outbound table
          else if its a schedule create a job process for it
         */
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(smsCampaign.getId()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult closeSmsCampaign(Long campaignId, JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();
        this.smsCampaignValidator.validateClosedDate(command.json());

        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);
        if(smsCampaign == null){
            throw new SmsCampaignNotFound(campaignId);
        }

        final LocalDate closureDate = command.localDateValueOfParameterNamed("closureDate");

        smsCampaign.close(currentUser,closureDate);

        this.smsCampaignRepository.saveAndFlush(smsCampaign);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(smsCampaign.getId()) //
                .build();
    }

    private String compileSmsTemplate(final String textMessageTemplate,final String campaignName , final Map<String, Object> smsParams)  {
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile(new StringReader(textMessageTemplate), campaignName);

        final StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, smsParams);

        return stringWriter.toString();
    }

    @SuppressWarnings("rawtypes")
    private List<HashMap<String,Object>> getRunReportByServiceImpl(final String reportName,final Map<String, String> queryParams) throws IOException {
        final String reportType ="report";

        List<HashMap<String, Object>> resultList = new ArrayList<>();
        final GenericResultsetData results = this.readReportingService.retrieveGenericResultSetForSmsCampaign(reportName,
                reportType, queryParams);
        final String response = this.genericDataService.generateJsonFromGenericResultsetData(results);
        resultList = new ObjectMapper().readValue(response, new TypeReference<List<HashMap<String,Object>>>(){});
        //loop changes array date to string date
        for(HashMap<String,Object> entry : resultList){
            for(Map.Entry<String,Object> map: entry.entrySet()){
                String key = map.getKey();
                Object ob  = map.getValue();
                if(ob instanceof ArrayList && ((ArrayList) ob).size() == 3){
                    String changeArrayDateToStringDate =  ((ArrayList) ob).get(2).toString() +"-"+((ArrayList) ob).get(1).toString() +"-"+((ArrayList) ob).get(0).toString();
                    entry.put(key,changeArrayDateToStringDate);
                }
            }
        }
        return resultList;
    }

    @Override
    public PreviewCampaignMessage previewMessage(final JsonQuery query) {
    	PreviewCampaignMessage campaignMessage = null;
        this.context.authenticatedUser();
        this.smsCampaignValidator.validatePreviewMessage(query.json());
        final String smsParams = this.fromJsonHelper.extractStringNamed("paramValue", query.parsedJson()) ;
        final String textMessageTemplate = this.fromJsonHelper.extractStringNamed("message", query.parsedJson());
        try{
        	
            HashMap<String,String> campaignParams = new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});
            HashMap<String,String> queryParamForRunReport =  new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            List<HashMap<String,Object>> runReportObject = this.getRunReportByServiceImpl(campaignParams.get("reportName"),queryParamForRunReport);

            if(runReportObject !=null){
                for(HashMap<String,Object> entry : runReportObject){
                    // add string object to campaignParam object
                    String textMessage = this.compileSmsTemplate(textMessageTemplate,"SmsCampaign", entry);
                    if(!textMessage.isEmpty()) {
                        final Integer totalMessage = runReportObject.size();
                        campaignMessage = new PreviewCampaignMessage(textMessage,totalMessage);
                        break;
                    }
                }
            }
        }catch(final IOException e){
            // TODO throw something here
        }

        return campaignMessage;

    }
    @Transactional
    @Override
    public CommandProcessingResult reactivateSmsCampaign(final Long campaignId, JsonCommand command) {

        this.smsCampaignValidator.validateActivation(command.json());

        final AppUser currentUser = this.context.authenticatedUser();

        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);

        if(smsCampaign == null){ throw new SmsCampaignNotFound(campaignId);}

        final LocalDate reactivationDate = command.localDateValueOfParameterNamed("activationDate");
        smsCampaign.reactivate(currentUser,reactivationDate);
        if (smsCampaign.isSchedule()) {

            /**
             * if recurrence start date is in the future calculate
             * next trigger date if not use recurrence start date us next trigger
             * date when activating
             */
        	Map<String,Object> recurringRuleMap = createRecurringRuleMap(smsCampaign.getRecurrence());
        	String frequency = (String) recurringRuleMap.get("FREQ");
        	if(frequency.equalsIgnoreCase(SmsApiConstants.minutely) || frequency.equalsIgnoreCase(SmsApiConstants.secondly) || frequency.equalsIgnoreCase(SmsApiConstants.hourly)){
        		final LocalDateTime nextTriggerDateWithTime = getNextParsedLocalDateTime(smsCampaign.getRecurrence(), smsCampaign.getRecurrenceStartDateTime(), DateUtils.getLocalDateTimeOfTenant());
                smsCampaign.setNextTriggerDate(nextTriggerDateWithTime.toDate());
                this.smsCampaignRepository.saveAndFlush(smsCampaign);
        	}else{
        		LocalDate nextTriggerDate = null;
                if(smsCampaign.getRecurrenceStartDateTime().isBefore(tenantDateTime())){
                    nextTriggerDate = CalendarUtils.getNextRecurringDate(smsCampaign.getRecurrence(), smsCampaign.getRecurrenceStartDate(), DateUtils.getLocalDateOfTenant());
                }else{
                    nextTriggerDate = smsCampaign.getRecurrenceStartDate();
                }
    	        LocalDateTime adjustTime = smsCampaign.getRecurrenceStartDateTime();
    	        if(recurringRuleMap.get("BYHOUR") != null){
    	        	int hour = Integer.parseInt((String)recurringRuleMap.get("BYHOUR"));
    	        	adjustTime = adjustTime.withHourOfDay(hour);
    	        }
    	        if(recurringRuleMap.get("BYMINUTE") != null){
    	        	int minute = Integer.parseInt((String)recurringRuleMap.get("BYMINUTE"));
    	        	adjustTime = adjustTime.withMinuteOfHour(minute);
    	        }
    	        if(recurringRuleMap.get("BYSECOND") != null){
    	        	int second = Integer.parseInt((String)recurringRuleMap.get("BYSECOND"));
    	        	adjustTime = adjustTime.withSecondOfMinute(second);
    	        }	        
    	        final String dateString = nextTriggerDate.toString() + " " + adjustTime.getHourOfDay()+":"+adjustTime.getMinuteOfHour()+":"+adjustTime.getSecondOfMinute();
    	        final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                final LocalDateTime nextTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);

                smsCampaign.setNextTriggerDate(nextTriggerDateWithTime.toDate());
                this.smsCampaignRepository.saveAndFlush(smsCampaign);
        	}
            
        }



        return new CommandProcessingResultBuilder() //
                .withEntityId(smsCampaign.getId()) //
                .build();

    }

    private void handleDataIntegrityIssues(@SuppressWarnings("unused") final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

        throw new PlatformDataIntegrityException("error.msg.sms.campaign.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

    private LocalDateTime tenantDateTime(){
        LocalDateTime today = new LocalDateTime();
        final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();

        if (tenant != null) {
            final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
            if (zone != null) {
                today = new LocalDateTime(zone);
            }
        }
        return  today;
    }
    
    public LocalDateTime getNextParsedLocalDateTime(String recurrence, LocalDateTime startDateTime, LocalDateTime tenantDateTime){
    	LocalDateTime nextTriggerDate = null;
        if(startDateTime.isBefore(tenantDateTime)){
            nextTriggerDate = getNextTriggerDate(createRecurringRuleMap(recurrence), tenantDateTime);        	
        }else{
            nextTriggerDate = startDateTime;
        }
        final String dateString = nextTriggerDate.toLocalDate().toString() + " " + nextTriggerDate.getHourOfDay()+":"+nextTriggerDate.getMinuteOfHour()+":"+nextTriggerDate.getSecondOfMinute();
        final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString,simpleDateFormat);
    }
    
    public LocalDateTime getNextTriggerDate(Map<String,Object> recurringRuleMap, LocalDateTime previousDateTime){
    	int interval = 1;
    	LocalDateTime nextRecurrenceDateTime = previousDateTime;
    	
    	String intervalAsString = (String) recurringRuleMap.get("INTERVAL");
    	if(intervalAsString!=null){
    		interval =  Integer.parseInt(intervalAsString);
    	}
    	String frequency = (String) recurringRuleMap.get("FREQ");
    	if(frequency.equalsIgnoreCase(SmsApiConstants.secondly)){
    		nextRecurrenceDateTime = previousDateTime.plusSeconds(interval);
    	}else if(frequency.equalsIgnoreCase(SmsApiConstants.minutely)){
    		nextRecurrenceDateTime = previousDateTime.plusMinutes(interval);
    	}else if(frequency.equalsIgnoreCase(SmsApiConstants.hourly)){
    		nextRecurrenceDateTime = previousDateTime.plusHours(interval);
    	}
    	
    	if(recurringRuleMap.get("BYHOUR") != null){
    		int hour = Integer.parseInt((String)recurringRuleMap.get("BYHOUR"));
    		nextRecurrenceDateTime = nextRecurrenceDateTime.withHourOfDay(hour);
    	}    	

    	if(recurringRuleMap.get("BYMINUTE") != null){
    		int minute = Integer.parseInt((String)recurringRuleMap.get("BYMINUTE"));
    		nextRecurrenceDateTime = nextRecurrenceDateTime.withMinuteOfHour(minute);
    	}
    	
    	if(recurringRuleMap.get("BYSECOND") != null){
    		int second = Integer.parseInt((String)recurringRuleMap.get("BYSECOND"));
    		nextRecurrenceDateTime = nextRecurrenceDateTime.withSecondOfMinute(second);
    	}
    	    	
    	return nextRecurrenceDateTime;
    }
    
    public Map<String,Object> createRecurringRuleMap(String ruleList){
    	Map<String,Object> recurringRuleMap = new HashMap<>();
    	String[] rules = ruleList.split(";");
    	for (int i = 0; i < rules.length; i++) {
    		String[] rule = rules[i].split("=");
    		recurringRuleMap.put(rule[0], rule[1]);
		}
    	return recurringRuleMap;
    }
}
