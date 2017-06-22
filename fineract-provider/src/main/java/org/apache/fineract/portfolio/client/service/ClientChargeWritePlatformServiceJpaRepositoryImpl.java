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
package org.apache.fineract.portfolio.client.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.HolidayRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.exception.ChargeCannotBeAppliedToException;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientChargeDataValidator;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientCharge;
import org.apache.fineract.portfolio.client.domain.ClientChargePaidBy;
import org.apache.fineract.portfolio.client.domain.ClientChargeRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.ClientRecurringCharge;
import org.apache.fineract.portfolio.client.domain.ClientRecurringChargeRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.ClientTransaction;
import org.apache.fineract.portfolio.client.domain.ClientTransactionRepository;
import org.apache.fineract.portfolio.client.exception.ClientHasNoGroupAssociationException;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.client.exception.DuedateIsNotMeetingDateException;
import org.apache.fineract.portfolio.client.exception.GroupAndClientChargeNotInSynWithMeeting;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetClientChargeRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.CollectionSheetConstants;
import org.apache.fineract.portfolio.collectionsheet.command.ClientChargeRepaymentCommand;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformServiceImpl;
import org.apache.fineract.portfolio.meeting.exception.MeetingNotFoundException;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientChargeWritePlatformServiceJpaRepositoryImpl implements ClientChargeWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ClientChargeWritePlatformServiceJpaRepositoryImpl.class);


    private final PlatformSecurityContext context;
    private final ChargeRepositoryWrapper chargeRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final ClientChargeDataValidator clientChargeDataValidator;
    private final ConfigurationDomainService configurationDomainService;
    private final HolidayRepositoryWrapper holidayRepository;
    private final WorkingDaysRepositoryWrapper workingDaysRepository;
    private final ClientChargeRepositoryWrapper clientChargeRepository;
    private final ClientTransactionRepository clientTransactionRepository;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final ClientReadPlatformServiceImpl clientReadPlatformServiceImpl;
    private final GroupReadPlatformServiceImpl groupReadPlatformServiceImpl;
    private final ClientRecurringChargeRepository clientRecurringChargeRepository;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;

    @Autowired
    public ClientChargeWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final ChargeRepositoryWrapper chargeRepository, final ClientChargeDataValidator clientChargeDataValidator,
            final ClientRepositoryWrapper clientRepository, final HolidayRepositoryWrapper holidayRepositoryWrapper,
            final ConfigurationDomainService configurationDomainService, final ClientChargeRepositoryWrapper clientChargeRepository,
            final WorkingDaysRepositoryWrapper workingDaysRepository, final ClientTransactionRepository clientTransactionRepository,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final CalendarInstanceRepository calendarInstanceRepository,
            final ClientReadPlatformServiceImpl clientReadPlatformServiceImpl,
            final GroupReadPlatformServiceImpl groupReadPlatformServiceImpl,
            final ClientRecurringChargeRepository clientRecurringChargeRepository,
            final FineractEntityAccessUtil fineractEntityAccessUtil) {
        this.context = context;
        this.chargeRepository = chargeRepository;
        this.clientChargeDataValidator = clientChargeDataValidator;
        this.clientRepository = clientRepository;
        this.holidayRepository = holidayRepositoryWrapper;
        this.configurationDomainService = configurationDomainService;
        this.clientChargeRepository = clientChargeRepository;
        this.workingDaysRepository = workingDaysRepository;
        this.clientTransactionRepository = clientTransactionRepository;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.clientReadPlatformServiceImpl = clientReadPlatformServiceImpl;
        this.groupReadPlatformServiceImpl = groupReadPlatformServiceImpl;
        this.clientRecurringChargeRepository = clientRecurringChargeRepository;
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
    }

    @Override
	public CommandProcessingResult addCharge(Long clientId, JsonCommand command) {
		try {
			this.clientChargeDataValidator.validateAdd(command.json());

			final Client client = clientRepository.getActiveClientInUserScope(clientId);
                        if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
			final Long chargeDefinitionId = command.longValueOfParameterNamed(ClientApiConstants.chargeIdParamName);
			this.fineractEntityAccessUtil
                    .checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(
                            FineractEntityAccessType.OFFICE_ACCESS_TO_CHARGES, chargeDefinitionId);
			LocalDate dueDate = command.localDateValueOfParameterNamed(ClientApiConstants.dueAsOfDateParamName);
			final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
			if (dueDate != null && client.getActivationLocalDate() != null
					&& client.getActivationLocalDate().isAfter(dueDate)) {

				final String defaultUserMessage = "client activation date cannot be after the charge due date";
				final ApiParameterError error = ApiParameterError.parameterError(
						"error.msg.clients.activationOnDate.after.charge.due.date", defaultUserMessage,
						ClientApiConstants.dueAsOfDateParamName, dueDate);

				dataValidationErrors.add(error);
			}
			if (!dataValidationErrors.isEmpty()) {
				throw new PlatformApiDataValidationException(dataValidationErrors);
			}
			final Boolean isSynchMeeting = command
					.booleanPrimitiveValueOfParameterNamed(ClientApiConstants.chargesynchmeetingParamName);

			final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeDefinitionId);

			// validate for client charge
			if (!charge.isClientCharge()) {
				final String errorMessage = "Charge with identifier " + charge.getId()
						+ " cannot be applied to a Client";
				throw new ChargeCannotBeAppliedToException("client", errorMessage, charge.getId());
			}
			if (charge.isMonthlyFee() || charge.isAnnualFee() || charge.isWeeklyFee()) {

				CalendarInstance calendarInstance = null;
				Calendar calendar = null;
				final ClientRecurringCharge clientRecurringCharge = ClientRecurringCharge.createNew(client, charge,
						command);

				// if the charge synchronized with meeting validate the dueDate
				// entered
				if (isSynchMeeting) {
					ClientData clientData = this.clientReadPlatformServiceImpl.retrieveOne(clientId);
					Collection<GroupGeneralData> groupData = clientData.getGroups();
					if (groupData.isEmpty()) {
						throw new ClientHasNoGroupAssociationException(clientId);
					}
					for (GroupGeneralData group : groupData) {
						Long groupId = group.getId();
						CalendarEntityType calendarEntityType = CalendarEntityType.GROUPS;
						if (groupId != null) {
							calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(groupId,
									CalendarEntityType.GROUPS.getValue());
							
							if (calendarInstance == null) {
								calendarEntityType = CalendarEntityType.CENTERS;
								GroupGeneralData groupGeneralData = this.groupReadPlatformServiceImpl
										.retrieveOne(groupId);
								Long centreId = groupGeneralData.getParentId();
								calendarInstance = this.calendarInstanceRepository
										.findCalendarInstaneByEntityId(centreId, calendarEntityType.getValue());
								if (calendarInstance == null) {
									throw new MeetingNotFoundException(groupId);
								}
								
							}
							calendar = calendarInstance.getCalendar();
							if (!charge.fetchChargeTimeType().isSameFrequency(CalendarUtils.getFrequency(calendar.getRecurrence()))) {
								throw new GroupAndClientChargeNotInSynWithMeeting(groupId);
							}
							if (!CalendarUtils.isValidRedurringDate(calendar.getRecurrence(), new LocalDate(calendar.getStartDate()), dueDate)) {
								throw new DuedateIsNotMeetingDateException(dueDate);
							}
						}
					}
				}
				this.clientRecurringChargeRepository.save(clientRecurringCharge);

				// if it is synchronized with meeting and date entered is one of
				// the meeting date save to calendar instance table
				if (isSynchMeeting && calendarInstance != null) {
					 calendar = calendarInstance.getCalendar();
					final CalendarInstance newCalendarInstance = CalendarInstance.from(calendar,
							clientRecurringCharge.getId(), CalendarEntityType.CHARGES.getValue());
					this.calendarInstanceRepository.save(newCalendarInstance);
				}

				final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat());
				validateActivityDateFallOnAWorkingDay(clientRecurringCharge.getDueLocalDate(),
						clientRecurringCharge.getClient().officeId(), ClientApiConstants.dueAsOfDateParamName,
						"charge.due.date.is.on.holiday", "charge.due.date.is.a.non.workingday", fmt);

				return new CommandProcessingResultBuilder() //
						.withEntityId(clientRecurringCharge.getId()) //
						.withOfficeId(clientRecurringCharge.getClient().getOffice().getId()) //
						.withClientId(clientRecurringCharge.getClient().getId()) //
						.build();
			}
			final ClientCharge clientCharge = ClientCharge.createNew(client, charge, command);

			this.clientChargeRepository.save(clientCharge);
			final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat());
			validateActivityDateFallOnAWorkingDay(clientCharge.getDueLocalDate(), clientCharge.getOfficeId(),
					ClientApiConstants.dueAsOfDateParamName, "charge.due.date.is.on.holiday",
					"charge.due.date.is.a.non.workingday", fmt);

			return new CommandProcessingResultBuilder() //
					.withEntityId(clientCharge.getId()) //
					.withOfficeId(clientCharge.getClient().getOffice().getId()) //
					.withClientId(clientCharge.getClient().getId()) //
					.build();
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(clientId, null, dve);
			return CommandProcessingResult.empty();
		}
}

    @Override
    public CommandProcessingResult payCharge(Long clientId, Long clientChargeId, JsonCommand command) {
        try {
            this.clientChargeDataValidator.validatePayCharge(command.json());

            final Client client = this.clientRepository.getActiveClientInUserScope(clientId);

            final ClientCharge clientCharge = this.clientChargeRepository.findOneWithNotFoundDetection(clientChargeId);

            final Locale locale = command.extractLocale();
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
            final LocalDate transactionDate = command.localDateValueOfParameterNamed(ClientApiConstants.transactionDateParamName);
            final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed(ClientApiConstants.amountParamName);
            final Money chargePaid = Money.of(clientCharge.getCurrency(), amountPaid);

            // Validate business rules for payment
            validatePaymentTransaction(client, clientCharge, fmt, transactionDate, amountPaid);

            // pay the charge
            clientCharge.pay(chargePaid);

            // create Payment Transaction
            final Map<String, Object> changes = new LinkedHashMap<>();
            final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

            ClientTransaction clientTransaction = ClientTransaction.payCharge(client, client.getOffice(), paymentDetail, transactionDate,
                    chargePaid, clientCharge.getCurrency().getCode());
            this.clientTransactionRepository.save(clientTransaction);

            // update charge paid by associations
            final ClientChargePaidBy chargePaidBy = ClientChargePaidBy.instance(clientTransaction, clientCharge, amountPaid);
            clientTransaction.getClientChargePaidByCollection().add(chargePaidBy);

            // generate accounting entries
            generateAccountingEntries(clientTransaction);

            return new CommandProcessingResultBuilder() //
                    .withTransactionId(clientTransaction.getId().toString())//
                    .withEntityId(clientCharge.getId()) //
                    .withOfficeId(clientCharge.getClient().getOffice().getId()) //
                    .withClientId(clientCharge.getClient().getId()).build();
        } catch (DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(clientId, clientChargeId, dve);
            return CommandProcessingResult.empty();
        }

    }

    private void generateAccountingEntries(ClientTransaction clientTransaction) {
        Map<String, Object> accountingBridgeData = clientTransaction.toMapData();
        journalEntryWritePlatformService.createJournalEntriesForClientTransactions(accountingBridgeData);
    }

    @Override
    public CommandProcessingResult waiveCharge(Long clientId, Long clientChargeId) {
        try {
            final Client client = this.clientRepository.getActiveClientInUserScope(clientId);
            final ClientCharge clientCharge = this.clientChargeRepository.findOneWithNotFoundDetection(clientChargeId);
            final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();

            // Validate business rules for payment
            validateWaiverTransaction(client, clientCharge);

            // waive the charge
            Money waivedAmount = clientCharge.waive();

            // create Waiver Transaction
            ClientTransaction clientTransaction = ClientTransaction.waiver(client, client.getOffice(), transactionDate, waivedAmount,
                    clientCharge.getCurrency().getCode());
            this.clientTransactionRepository.save(clientTransaction);

            // update charge paid by associations
            final ClientChargePaidBy chargePaidBy = ClientChargePaidBy.instance(clientTransaction, clientCharge, waivedAmount.getAmount());
            clientTransaction.getClientChargePaidByCollection().add(chargePaidBy);

            return new CommandProcessingResultBuilder().withTransactionId(clientTransaction.getId().toString())//
                    .withEntityId(clientCharge.getId()) //
                    .withOfficeId(clientCharge.getClient().getOffice().getId()) //
                    .withClientId(clientCharge.getClient().getId()) //
                    .build();
        } catch (DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(clientId, clientChargeId, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult deleteCharge(Long clientId, Long clientChargeId) {
        try {
            final Client client = this.clientRepository.getActiveClientInUserScope(clientId);
            final ClientCharge clientCharge = this.clientChargeRepository.findOneWithNotFoundDetection(clientChargeId);

            // Validate business rules for charge deletion
            validateChargeDeletion(client, clientCharge);

            // delete the charge
            clientChargeRepository.delete(clientCharge);

            return new CommandProcessingResultBuilder() //
                    .withEntityId(clientCharge.getId()) //
                    .withOfficeId(clientCharge.getClient().getOffice().getId()) //
                    .withClientId(clientCharge.getClient().getId()) //
                    .build();
        } catch (DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(clientId, clientChargeId, dve);
            return CommandProcessingResult.empty();
        }
    }

    /**
     * Validates transaction to ensure that <br>
     * charge is active <br>
     * transaction date is valid (between client activation and todays date)
     * <br>
     * charge is not already paid or waived <br>
     * amount is not more than total due
     * 
     * @param client
     * @param clientCharge
     * @param fmt
     * @param transactionDate
     * @param amountPaid
     * @param requiresTransactionDateValidation
     *            if set to false, transaction date specific validation is
     *            skipped
     * @param requiresTransactionAmountValidation
     *            if set to false transaction amount validation is skipped
     * @return
     */
    private void validatePaymentDateAndAmount(final Client client, final ClientCharge clientCharge, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal amountPaid, final boolean requiresTransactionDateValidation,
            final boolean requiresTransactionAmountValidation) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);

        if (clientCharge.isNotActive()) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("charge.is.not.active");
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        }

        if (requiresTransactionDateValidation) {
            validateTransactionDateOnWorkingDay(transactionDate, clientCharge, fmt);

            if (client.getActivationLocalDate() != null && transactionDate.isBefore(client.getActivationLocalDate())) {
                baseDataValidator.reset().parameter(ClientApiConstants.transactionDateParamName).value(transactionDate.toString(fmt))
                        .failWithCodeNoParameterAddedToErrorCode("transaction.before.activationDate");
                throw new PlatformApiDataValidationException(dataValidationErrors);
            }

            if (DateUtils.isDateInTheFuture(transactionDate)) {
                baseDataValidator.reset().parameter(ClientApiConstants.transactionDateParamName).value(transactionDate.toString(fmt))
                        .failWithCodeNoParameterAddedToErrorCode("transaction.is.futureDate");
                throw new PlatformApiDataValidationException(dataValidationErrors);
            }
        }

        // validate charge is not already paid or waived
        if (clientCharge.isWaived()) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("transaction.invalid.account.charge.is.already.waived");
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        } else if (clientCharge.isPaid()) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("transaction.invalid.account.charge.is.paid");
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        }

        if (requiresTransactionAmountValidation) {
            final Money chargePaid = Money.of(clientCharge.getCurrency(), amountPaid);
            if (!clientCharge.getAmountOutstanding().isGreaterThanOrEqualTo(chargePaid)) {
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("transaction.invalid.charge.amount.paid.in.access");
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
            }
        }
    }

    public void validateWaiverTransaction(final Client client, final ClientCharge clientCharge) {
        DateTimeFormatter fmt = null;
        LocalDate transactionDate = null;
        BigDecimal amountPaid = null;
        boolean requiresTransactionDateValidation = false;
        boolean requiresTransactionAmountValidation = false;
        validatePaymentDateAndAmount(client, clientCharge, fmt, transactionDate, amountPaid, requiresTransactionDateValidation,
                requiresTransactionAmountValidation);
    }

    public void validatePaymentTransaction(final Client client, final ClientCharge clientCharge, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal amountPaid) {
        boolean requiresTransactionDateValidation = true;
        boolean requiresTransactionAmountValidation = true;
        validatePaymentDateAndAmount(client, clientCharge, fmt, transactionDate, amountPaid, requiresTransactionDateValidation,
                requiresTransactionAmountValidation);
    }

    public void validateChargeDeletion(final Client client, final ClientCharge clientCharge) {
        DateTimeFormatter fmt = null;
        LocalDate transactionDate = null;
        BigDecimal amountPaid = null;
        boolean requiresTransactionDateValidation = false;
        boolean requiresTransactionAmountValidation = false;
        validatePaymentDateAndAmount(client, clientCharge, fmt, transactionDate, amountPaid, requiresTransactionDateValidation,
                requiresTransactionAmountValidation);
    }

    /**
     * @param clientId
     * @return
     */
    @Override
    public CommandProcessingResult updateCharge(@SuppressWarnings("unused") Long clientId,
            @SuppressWarnings("unused") JsonCommand command) {
        // functionality not yet supported
        return null;
    }

    @Override
    @SuppressWarnings("unused")
    public CommandProcessingResult inactivateCharge(Long clientId, Long clientChargeId) {
        // functionality not yet supported
        return null;
    }

    /**
     * Ensures that the charge transaction date (for payments) is not on a
     * holiday or a non working day
     * 
     * @param savingsAccountCharge
     * @param fmt
     */
    private void validateTransactionDateOnWorkingDay(final LocalDate transactionDate, final ClientCharge clientCharge,
            final DateTimeFormatter fmt) {
        validateActivityDateFallOnAWorkingDay(transactionDate, clientCharge.getOfficeId(), ClientApiConstants.transactionDateParamName,
                "transaction.not.allowed.transaction.date.is.on.holiday", "transaction.not.allowed.transaction.date.is.a.non.workingday",
                fmt);
    }

    /**
     * @param date
     * @param officeId
     * @param jsonPropertyName
     * @param errorMessageFragment
     * @param fmt
     */
    private void validateActivityDateFallOnAWorkingDay(final LocalDate date, final Long officeId, final String jsonPropertyName,
            final String errorMessageFragmentForActivityOnHoliday, final String errorMessageFragmentForActivityOnNonWorkingDay,
            final DateTimeFormatter fmt) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);
        if (date != null) {
            // transaction date should not be on a holiday or non working day
            if (!this.configurationDomainService.allowTransactionsOnHolidayEnabled() && this.holidayRepository.isHoliday(officeId, date)) {
                baseDataValidator.reset().parameter(jsonPropertyName).value(date.toString(fmt))
                        .failWithCodeNoParameterAddedToErrorCode(errorMessageFragmentForActivityOnHoliday);
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
            }

            if (!this.configurationDomainService.allowTransactionsOnNonWorkingDayEnabled()
                    && !this.workingDaysRepository.isWorkingDay(date)) {
                baseDataValidator.reset().parameter(jsonPropertyName).value(date.toString(fmt))
                        .failWithCodeNoParameterAddedToErrorCode(errorMessageFragmentForActivityOnNonWorkingDay);
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
            }
        }
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private void handleDataIntegrityIssues(@SuppressWarnings("unused") final Long clientId, final Long clientChargeId,
            final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("FK_m_client_charge_paid_by_m_client_charge")) {

        throw new PlatformDataIntegrityException("error.msg.client.charge.cannot.be.deleted",
                "Client charge with id `" + clientChargeId + "` cannot be deleted as transactions have been made on the same",
                "clientChargeId", clientChargeId); }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.client.charges.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
    
	@Transactional
	@Override
	public Map<String, Object> payChargeFromCollectionsheet(CollectionSheetClientChargeRepaymentCommand chargeRepaymentCommand, PaymentDetail paymentDetail){
		
		final ClientChargeRepaymentCommand[] payChargeCommand = chargeRepaymentCommand.getChargeTransactions();
		final Locale locale=chargeRepaymentCommand.getLocale();
		final LocalDate transactionDate = chargeRepaymentCommand.getTransactionDate();
		final String dateFormat = chargeRepaymentCommand.getDateFormat();
        final Map<String, Object> changes = new LinkedHashMap<>();
        List<Long> transactionIds = new ArrayList<>();
        
        if (payChargeCommand == null) { return changes; }
        for(int i=0;i<payChargeCommand.length;i++){
        	ClientChargeRepaymentCommand singlePayChargeCommand = payChargeCommand[i];
        	if(singlePayChargeCommand != null){
        	final Long clientChargeId = singlePayChargeCommand.getChargeId();
        	final BigDecimal clientChargeAmountPaid = singlePayChargeCommand.getTransactionAmount();
        	final DateTimeFormatter fmt = DateTimeFormat.forPattern(
        			dateFormat).withLocale(locale);

			final ClientCharge clientCharge = this.clientChargeRepository
					.findOneWithNotFoundDetection(clientChargeId);
        	
        	final Money chargePaid = Money.of(clientCharge.getCurrency(),
					clientChargeAmountPaid);
        	
        	validatePaymentTransaction(clientCharge.getClient(), clientCharge, fmt,
					transactionDate, clientChargeAmountPaid);

			// pay the charge
			clientCharge.pay(chargePaid);
			
		// create Payment Transaction
					//final Map<String, Object> changes = new LinkedHashMap<>();
					/*final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService
							.createAndPersistPaymentDetail(command, changes);*/

			ClientTransaction clientTransaction = ClientTransaction.payCharge(
					clientCharge.getClient(), clientCharge.getClient().getOffice(), paymentDetail, transactionDate,
					chargePaid, clientCharge.getCurrency().getCode());
			this.clientTransactionRepository.save(clientTransaction);

			// update charge paid by associations
			final ClientChargePaidBy chargePaidBy = ClientChargePaidBy
					.instance(clientTransaction, clientCharge, clientChargeAmountPaid);
			clientTransaction.getClientChargePaidByCollection().add(
					chargePaidBy);

			// generate accounting entries
			generateAccountingEntries(clientTransaction);
			transactionIds.add(clientTransaction.getId());
        	}

        }
        changes.put(CollectionSheetConstants.clientChargeTransactionsParamName, transactionIds);
		return changes;
			
	}
}
