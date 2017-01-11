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
package org.apache.fineract.portfolio.calendar.service;

import static org.apache.fineract.portfolio.calendar.CalendarConstants.CALENDAR_RESOURCE_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.portfolio.calendar.CalendarConstants.CALENDAR_SUPPORTED_PARAMETERS;
import org.apache.fineract.portfolio.calendar.data.CalendarHistoryDataWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistoryRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarType;
import org.apache.fineract.portfolio.calendar.exception.CalendarNotFoundException;
import org.apache.fineract.portfolio.calendar.serialization.CalendarCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepositoryWrapper;
import org.apache.fineract.portfolio.group.exception.CenterNotActiveException;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.portfolio.savings.service.DepositAccountWritePlatformService;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CalendarWritePlatformServiceJpaRepositoryImpl implements CalendarWritePlatformService {

    private final CalendarRepository calendarRepository;
    private final CalendarHistoryRepository calendarHistoryRepository;
    private final CalendarCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final LoanWritePlatformService loanWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final GroupRepositoryWrapper groupRepository;
    private final LoanRepository loanRepository;
    private final ClientRepositoryWrapper clientRepository;
	private final DepositAccountWritePlatformService depositAccountWritePlatformService;
    @Autowired
    public CalendarWritePlatformServiceJpaRepositoryImpl(final CalendarRepository calendarRepository,
            final CalendarHistoryRepository calendarHistoryRepository,
            final CalendarCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CalendarInstanceRepository calendarInstanceRepository, final LoanWritePlatformService loanWritePlatformService,
            final ConfigurationDomainService configurationDomainService, final GroupRepositoryWrapper groupRepository,
            final LoanRepository loanRepository, final ClientRepositoryWrapper clientRepository, final DepositAccountWritePlatformService depositAccountWritePlatformService) {
        this.calendarRepository = calendarRepository;
        this.calendarHistoryRepository = calendarHistoryRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.loanWritePlatformService = loanWritePlatformService;
        this.configurationDomainService = configurationDomainService;
        this.groupRepository = groupRepository;
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
        this.depositAccountWritePlatformService = depositAccountWritePlatformService;
    }

    @Override
    public CommandProcessingResult createCalendar(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForCreate(command.json());
        Long entityId = null;
        CalendarEntityType entityType = CalendarEntityType.INVALID;
        LocalDate entityActivationDate = null;
        Group centerOrGroup = null;
        if (command.getGroupId() != null) {
            centerOrGroup = this.groupRepository.findOneWithNotFoundDetection(command.getGroupId());
            entityActivationDate = centerOrGroup.getActivationLocalDate();
            entityType = centerOrGroup.isCenter() ? CalendarEntityType.CENTERS : CalendarEntityType.GROUPS;
            entityId = command.getGroupId();
        } else if (command.getLoanId() != null) {
            final Loan loan = this.loanRepository.findOne(command.getLoanId());
            entityActivationDate = (loan.getApprovedOnDate() == null) ? loan.getSubmittedOnDate() : loan.getApprovedOnDate();
            entityType = CalendarEntityType.LOANS;
            entityId = command.getLoanId();
        } else if (command.getClientId() != null) {
            final Client client = this.clientRepository.findOneWithNotFoundDetection(command.getClientId());
            entityActivationDate = client.getActivationLocalDate();
            entityType = CalendarEntityType.CLIENTS;
            entityId = command.getClientId();
        }

        final Integer entityTypeId = entityType.getValue();
        final Calendar newCalendar = Calendar.fromJson(command);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("calendar");
        if (entityActivationDate == null || newCalendar.getStartDateLocalDate().isBefore(entityActivationDate)) {
            final DateTimeFormatter formatter = DateTimeFormat.forPattern(command.dateFormat()).withLocale(command.extractLocale());
            String dateAsString = "";
            if (entityActivationDate != null) dateAsString = formatter.print(entityActivationDate);

            final String errorMessage = "cannot.be.before." + entityType.name().toLowerCase() + ".activation.date";
            baseDataValidator.reset().parameter(CALENDAR_SUPPORTED_PARAMETERS.START_DATE.getValue()).value(dateAsString)
                    .failWithCodeNoParameterAddedToErrorCode(errorMessage);
        }

        if (centerOrGroup != null) {
            Long centerOrGroupId = centerOrGroup.getId();
            Integer centerOrGroupEntityTypeId = entityType.getValue();

            final Group parent = centerOrGroup.getParent();
            if (parent != null) {
                centerOrGroupId = parent.getId();
                centerOrGroupEntityTypeId = CalendarEntityType.CENTERS.getValue();
            }

            final CalendarInstance collectionCalendarInstance = this.calendarInstanceRepository
                    .findByEntityIdAndEntityTypeIdAndCalendarTypeId(centerOrGroupId, centerOrGroupEntityTypeId,
                            CalendarType.COLLECTION.getValue());
            if (collectionCalendarInstance != null) {
                final String errorMessage = "multiple.collection.calendar.not.supported";
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(errorMessage);
            }
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

        this.calendarRepository.save(newCalendar);

        final CalendarInstance newCalendarInstance = CalendarInstance.from(newCalendar, entityId, entityTypeId);
        this.calendarInstanceRepository.save(newCalendarInstance);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newCalendar.getId()) //
                .withClientId(command.getClientId()) //
                .withGroupId(command.getGroupId()) //
                .withLoanId(command.getLoanId()) //
                .build();

    }


	public void validateIsEditMeetingAllowed(Long groupId) {

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("calendar");
		Group centerOrGroup = null;

		if (groupId != null) {
			centerOrGroup = this.groupRepository
					.findOneWithNotFoundDetection(groupId);
			if(centerOrGroup.isNotActive()){
			    if(centerOrGroup.isCenter()){
			        throw new CenterNotActiveException(centerOrGroup.getId());
			    }
                            throw new GroupNotActiveException(centerOrGroup.getId());
			}
			final Group parent = centerOrGroup.getParent();
			/* Check if it is a Group and belongs to a center */
			if (centerOrGroup.isGroup() && parent != null) {
				
				Integer centerEntityTypeId = CalendarEntityType.CENTERS
						.getValue();
				/* Check if calendar is created at center */
				final CalendarInstance collectionCalendarInstance = this.calendarInstanceRepository
						.findByEntityIdAndEntityTypeIdAndCalendarTypeId(
								parent.getId(), centerEntityTypeId,
								CalendarType.COLLECTION.getValue());
				/* If calendar is created by parent group, then it cannot be edited by the child group */
				if (collectionCalendarInstance != null) {
					final String errorMessage = "meeting.created.at.center.cannot.be.edited.at.group.level";
					baseDataValidator.reset()
							.failWithCodeNoParameterAddedToErrorCode(
									errorMessage);
				}
			}

		}
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

	}
    
    @Override
    public CommandProcessingResult updateCalendar(final JsonCommand command) {

        /** Validate to check if Edit is Allowed **/
    	this.validateIsEditMeetingAllowed(command.getGroupId());
        /*
         * Validate all the data for updating the calendar
         */
        this.fromApiJsonDeserializer.validateForUpdate(command.json());
        
        Boolean areActiveEntitiesSynced = false;
        final Long calendarId = command.entityId();

        final Collection<Integer> loanStatuses = new ArrayList<>(Arrays.asList(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(),
                LoanStatus.APPROVED.getValue(), LoanStatus.ACTIVE.getValue()));

        final Integer numberOfActiveLoansSyncedWithThisCalendar = this.calendarInstanceRepository.countOfLoansSyncedWithCalendar(
                calendarId, loanStatuses);

        /*
         * areActiveEntitiesSynced is set to true, if there are any active loans
         * synced to this calendar.
         */
        
        if(numberOfActiveLoansSyncedWithThisCalendar > 0){
            areActiveEntitiesSynced = true;
        }

        
        final Calendar calendarForUpdate = this.calendarRepository.findOne(calendarId);
        if (calendarForUpdate == null) { throw new CalendarNotFoundException(calendarId); }
        
        final Date oldStartDate = calendarForUpdate.getStartDate();
        // create calendar history before updating calendar
        final CalendarHistory calendarHistory = new CalendarHistory(calendarForUpdate, oldStartDate);

        Map<String, Object> changes = null;
        
        final Boolean reschedulebasedOnMeetingDates = command
                .booleanPrimitiveValueOfParameterNamed(CALENDAR_SUPPORTED_PARAMETERS.RESCHEDULE_BASED_ON_MEETING_DATES.getValue());
        
        /*
         * System allows to change the meeting date by two means,
         * 
         * Option 1: reschedulebasedOnMeetingDates = false or reschedulebasedOnMeetingDates is not passed 
         * By directly editing the recurring day with effective from
         * date and system decides the next meeting date based on some sensible
         * logic (i.e., number of minimum days between two repayments)
         * 
         * 
         * Option 2: reschedulebasedOnMeetingDates = true 
         * By providing alternative meeting date for one of future
         * meeting date and derive the day of recurrence from the new meeting
         * date. Ex: User proposes new meeting date say "14/Nov/2014" for
         * present meeting date "12/Nov/2014", based on this input other values
         * re derived and loans are rescheduled
         * 
         */
        
        LocalDate newMeetingDate = null;
        LocalDate presentMeetingDate = null;
        
        if (reschedulebasedOnMeetingDates) {
            if (calendarForUpdate.isNthDayFrequency()) {
                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(CALENDAR_RESOURCE_NAME);
                baseDataValidator.parameter(CALENDAR_SUPPORTED_PARAMETERS.RESCHEDULE_BASED_ON_MEETING_DATES.getValue()).failWithCode(
                        "not.supported.for.this.meeting");
                throw new PlatformApiDataValidationException(dataValidationErrors);
            }
            newMeetingDate = command.localDateValueOfParameterNamed(CALENDAR_SUPPORTED_PARAMETERS.NEW_MEETING_DATE.getValue());
            presentMeetingDate = command.localDateValueOfParameterNamed(CALENDAR_SUPPORTED_PARAMETERS.PRESENT_MEETING_DATE.getValue());

            /*
             * New meeting date proposed will become the new start date for the
             * updated calendar
             */

            changes = calendarForUpdate.updateStartDateAndDerivedFeilds(newMeetingDate);

        } else {
            changes = calendarForUpdate.update(command, areActiveEntitiesSynced);
        }
        
        if (!changes.isEmpty()) {
            // update calendar history table only if there is a change in
            // calendar start date.
            Set<CalendarHistory> historys = calendarForUpdate.getCalendarHistory();
            CalendarHistoryDataWrapper calendarHistoryDataWrapper = new CalendarHistoryDataWrapper(historys);
            LocalDate endDate = null;
            if (!reschedulebasedOnMeetingDates) {
                newMeetingDate = command.localDateValueOfParameterNamed(CALENDAR_SUPPORTED_PARAMETERS.START_DATE.getValue());
                presentMeetingDate = new LocalDate(oldStartDate);
                if (newMeetingDate.isBefore(new LocalDate(oldStartDate)) || newMeetingDate.equals(new LocalDate(oldStartDate))) {
                    endDate = newMeetingDate.minusDays(1);
                    updateCalendarHistory(calendarHistory, calendarHistoryDataWrapper, endDate);
                    if(calendarHistoryDataWrapper.getCalendarHistoryList().isEmpty()){
                        calendarHistory.updateEndDate(null);
                        calendarHistory.updateIsActive(false);
                    }
                } else {
                    endDate = newMeetingDate.minusDays(1);
                    calendarHistory.updateEndDate(endDate.toDate());
                }
            } else {
                if (newMeetingDate != null && newMeetingDate.isBefore(presentMeetingDate) || newMeetingDate.equals(new LocalDate(presentMeetingDate))) {
                    endDate = newMeetingDate.minusDays(1);
                    calendarHistory.updateEndDate(endDate.toDate());
                    updateCalendarHistory(calendarHistory, calendarHistoryDataWrapper, endDate);
                } else {
                    endDate = presentMeetingDate.minusDays(1);
                    if(calendarHistory.getStartDateLocalDate().isAfter(endDate)){
                        calendarHistory.updateIsActive(false);
                        calendarHistory.updateEndDate(null);
                    } else {
                        calendarHistory.updateEndDate(endDate.toDate());
                    }
                }
            }

            this.calendarHistoryRepository.save(calendarHistory);
            historys.add(calendarHistory);
            calendarForUpdate.updateCalendarHistory(historys);
            this.calendarRepository.saveAndFlush(calendarForUpdate);

            if (this.configurationDomainService.isRescheduleFutureRepaymentsEnabled() && calendarForUpdate.isRepeating()) {
                // fetch all loan calendar instances associated with modifying
                // calendar.
                final Collection<CalendarInstance> loanCalendarInstances = this.calendarInstanceRepository.findByCalendarIdAndEntityTypeId(
                        calendarId, CalendarEntityType.LOANS.getValue());

                if (!CollectionUtils.isEmpty(loanCalendarInstances)) {
                    // update all loans associated with modifying calendar
                    this.loanWritePlatformService.applyMeetingDateChanges(calendarForUpdate, loanCalendarInstances,
                            reschedulebasedOnMeetingDates, presentMeetingDate, newMeetingDate);

                }
            }
            final Collection<CalendarInstance> savingCalendarInstances = this.calendarInstanceRepository
					.findByCalendarIdAndEntityTypeId(calendarId,
							CalendarEntityType.SAVINGS.getValue());
			
			if (!CollectionUtils.isEmpty(savingCalendarInstances)) {
				this.depositAccountWritePlatformService.applyMeetingDateChanges(
						calendarForUpdate, savingCalendarInstances,
						reschedulebasedOnMeetingDates, presentMeetingDate,
						newMeetingDate);
}
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(calendarForUpdate.getId()) //
                .with(changes) //
                .build();
    }

    private void updateCalendarHistory(final CalendarHistory calendarHistory, CalendarHistoryDataWrapper calendarHistoryDataWrapper,
            LocalDate endDate) {
        CalendarHistory calendarHistoryData = getCalendarHistoryByEndDate(calendarHistoryDataWrapper.getCalendarHistoryList(), endDate);
        for (CalendarHistory history : calendarHistoryDataWrapper.getCalendarHistoryList()) {
            LocalDate calendarHistoryEndDate = history.getEndDateLocalDate();
            if (history.isActive() && endDate.isBefore(calendarHistoryEndDate) || endDate.equals(calendarHistoryEndDate)) {
                if (calendarHistoryData != null && history.getStartDateLocalDate().equals(calendarHistoryData.getStartDateLocalDate())) {
                    history.updateEndDate(endDate.toDate());
                    calendarHistory.updateEndDate(null);
                    calendarHistory.updateIsActive(false);
                } else {
                    history.updateEndDate(null);
                    history.updateIsActive(false);
                }
            } else if (history.isActive() && calendarHistoryEndDate == null) {
                calendarHistory.updateEndDate(null);
                calendarHistory.updateIsActive(false);
            }
        }
    }
    
    public CalendarHistory getCalendarHistoryByEndDate(List<CalendarHistory> calendarHistory, final LocalDate endDate) {
        CalendarHistory calendarHistoryData = null;
        for (CalendarHistory history : calendarHistory) {
            if (history.isActive() && history.getStartDateLocalDate().isBefore(endDate)) {
                calendarHistoryData = history;
            }
        }
        return calendarHistoryData;
    }


    @Override
    public CommandProcessingResult deleteCalendar(final Long calendarId) {
        final Calendar calendarForDelete = this.calendarRepository.findOne(calendarId);
        if (calendarForDelete == null) { throw new CalendarNotFoundException(calendarId); }

        this.calendarRepository.delete(calendarForDelete);
        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .withEntityId(calendarId) //
                .build();
    }

    @Override
    public CommandProcessingResult createCalendarInstance(final Long calendarId, final Long entityId, final Integer entityTypeId) {

        final Calendar calendarForUpdate = this.calendarRepository.findOne(calendarId);
        if (calendarForUpdate == null) { throw new CalendarNotFoundException(calendarId); }

        final CalendarInstance newCalendarInstance = new CalendarInstance(calendarForUpdate, entityId, entityTypeId);
        this.calendarInstanceRepository.save(newCalendarInstance);

        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .withEntityId(calendarForUpdate.getId()) //
                .build();
    }

    @Override
    public CommandProcessingResult updateCalendarInstance(final Long calendarId, final Long entityId, final Integer entityTypeId) {
        final Calendar calendarForUpdate = this.calendarRepository.findOne(calendarId);
        if (calendarForUpdate == null) { throw new CalendarNotFoundException(calendarId); }

        final CalendarInstance calendarInstanceForUpdate = this.calendarInstanceRepository.findByCalendarIdAndEntityIdAndEntityTypeId(
                calendarId, entityId, entityTypeId);
        this.calendarInstanceRepository.saveAndFlush(calendarInstanceForUpdate);
        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .withEntityId(calendarForUpdate.getId()) //
                .build();
    }
}
