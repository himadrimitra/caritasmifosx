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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformServiceUnavailableException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepository;
import org.apache.fineract.organisation.holiday.domain.HolidayStatusType;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.apache.fineract.organisation.workingdays.service.WorkingDayExemptionsReadPlatformService;
import org.apache.fineract.portfolio.account.domain.AccountTransferRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferStandingInstruction;
import org.apache.fineract.portfolio.account.domain.AccountTransferTransaction;
import org.apache.fineract.portfolio.account.domain.StandingInstructionRepository;
import org.apache.fineract.portfolio.account.domain.StandingInstructionStatus;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.AdjustedLoanTransactionDetails;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanScheduleAccrualData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanForeclosureException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.apache.fineract.portfolio.loanaccount.service.LoanAccrualPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanOverdueChargeService;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanAccountDomainServiceJpa implements LoanAccountDomainService {

    private final LoanAssembler loanAccountAssembler;
    private final LoanRepository loanRepository;
    private final LoanTransactionRepositoryWrapper loanTransactionRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final HolidayRepository holidayRepository;
    private final WorkingDaysRepositoryWrapper workingDaysRepository;

    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final NoteRepository noteRepository;
    private final AccountTransferRepository accountTransferRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final LoanAccrualPlatformService loanAccrualPlatformService;
    private final PlatformSecurityContext context;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final LoanUtilService loanUtilService;
    private final WorkingDayExemptionsReadPlatformService workingDayExcumptionsReadPlatformService;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final StandingInstructionRepository standingInstructionRepository;
    private final LoanScheduleHistoryWritePlatformService loanScheduleHistoryWritePlatformService;
    private final PaymentTypeRepository paymentTypeRepository;
    private final LoanOverdueChargeService loanOverdueChargeService;

    @Autowired
    public LoanAccountDomainServiceJpa(final LoanAssembler loanAccountAssembler, final LoanRepository loanRepository,
            final LoanTransactionRepositoryWrapper loanTransactionRepository, final NoteRepository noteRepository,
            final ConfigurationDomainService configurationDomainService, final HolidayRepository holidayRepository,
            final WorkingDaysRepositoryWrapper workingDaysRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final AccountTransferRepository accountTransferRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final LoanAccrualPlatformService loanAccrualPlatformService, final PlatformSecurityContext context,
            final BusinessEventNotifierService businessEventNotifierService, final LoanUtilService loanUtilService,
            final WorkingDayExemptionsReadPlatformService workingDayExcumptionsReadPlatformService,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService, 
            final StandingInstructionRepository standingInstructionRepository,
            final LoanScheduleHistoryWritePlatformService loanScheduleHistoryWritePlatformService, final PaymentTypeRepository paymentTypeRepository,
            final LoanOverdueChargeService loanOverdueChargeService) {
        this.loanAccountAssembler = loanAccountAssembler;
        this.loanRepository = loanRepository;
        this.loanTransactionRepository = loanTransactionRepository;
        this.noteRepository = noteRepository;
        this.configurationDomainService = configurationDomainService;
        this.holidayRepository = holidayRepository;
        this.workingDaysRepository = workingDaysRepository;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.accountTransferRepository = accountTransferRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.loanAccrualPlatformService = loanAccrualPlatformService;
        this.context = context;
        this.businessEventNotifierService = businessEventNotifierService;
        this.loanUtilService = loanUtilService;
        this.workingDayExcumptionsReadPlatformService = workingDayExcumptionsReadPlatformService;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.standingInstructionRepository = standingInstructionRepository;
        this.loanScheduleHistoryWritePlatformService = loanScheduleHistoryWritePlatformService;
        this.paymentTypeRepository=paymentTypeRepository;
        this.loanOverdueChargeService = loanOverdueChargeService;
    }

    @Override
    public LoanTransaction makeRepayment(final Loan loan, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText,
            final String txnExternalId, final boolean isRecoveryRepayment, boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto,
            Boolean isHolidayValidationDone) {
        return makeRepayment(loan, builderResult, transactionDate, transactionAmount, paymentDetail, noteText,
                txnExternalId, isRecoveryRepayment, isAccountTransfer, holidayDetailDto, isHolidayValidationDone, false);
    }

    @Override
    public LoanTransaction makeRepayment(final Loan loan, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText,
            final String txnExternalId, final boolean isRecoveryRepayment, boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto,
            Boolean isHolidayValidationDone, final boolean isLoanToLoanTransfer) {
        
        final boolean isPrepayment = false;
        return makeRepayment(loan, builderResult, transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId,
                isRecoveryRepayment, isAccountTransfer, holidayDetailDto, isHolidayValidationDone, isLoanToLoanTransfer, isPrepayment);
    }
    
    @Override
    @Transactional
    public LoanTransaction makeRepayment(final String loanAccountNumber, final LocalDate transactionDate,
            final BigDecimal transactionAmount, final String paymentTypeName, final String paymentDetailAccountNumber,
            final String paymentDetailChequeNumber, final String routingCode, final String paymentDetailBankNumber,
            final String receiptNumber, final String note) {
        final CommandProcessingResultBuilder builderResult = new CommandProcessingResultBuilder();
        final String txnExternalId = null;
        final boolean isRecoveryRepayment = false;
        final boolean isAccountTransfer = false;
        final HolidayDetailDTO holidayDetailDto = null;
        final Boolean isHolidayValidationDone = false;
        final boolean isLoanToLoanTransfer = false;
        final boolean isPrepayment = false;
        final Loan loan = this.loanAccountAssembler.assembleFromAccountNumber(loanAccountNumber);
        PaymentDetail paymentDetail = null;
        if (paymentTypeName != null) {
            paymentDetail = PaymentDetail.instance(this.paymentTypeRepository.findByPaymentTypeName(paymentTypeName),
                    paymentDetailAccountNumber, paymentDetailChequeNumber, routingCode, receiptNumber, paymentDetailBankNumber);
            this.paymentDetailWritePlatformService.persistPaymentDetail(paymentDetail);
        }
        return makeRepayment(loan, builderResult, transactionDate, transactionAmount, paymentDetail, note, txnExternalId,
                isRecoveryRepayment, isAccountTransfer, holidayDetailDto, isHolidayValidationDone, isLoanToLoanTransfer, isPrepayment);
    }

    @Override
    @Transactional
    public LoanTransaction makeRepayment(final Long loanId, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText,
            final String txnExternalId, final boolean isRecoveryRepayment, boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto,
            Boolean isHolidayValidationDone, final boolean isLoanToLoanTransfer, final boolean isPrepayment) {
        final Loan loan = this.loanAccountAssembler.assembleFrom(loanId);
        return makeRepayment(loan, builderResult, transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId,
                isRecoveryRepayment, isAccountTransfer, holidayDetailDto, isHolidayValidationDone, isLoanToLoanTransfer, isPrepayment);
    }
    
    @Override
    public LoanTransaction makeRepayment(final Loan loan, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText,
            final String txnExternalId, final boolean isRecoveryRepayment, boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto,
            Boolean isHolidayValidationDone, final boolean isLoanToLoanTransfer, final boolean isPrepayment) {
        AppUser currentUser = getAppUserIfPresent();
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        // TODO: Is it required to validate transaction date with meeting dates
        // if repayments is synced with meeting?
        /*
         * if(loan.isSyncDisbursementWithMeeting()){ // validate actual
         * disbursement date against meeting date CalendarInstance
         * calendarInstance =
         * this.calendarInstanceRepository.findCalendarInstaneByLoanId
         * (loan.getId(), CalendarEntityType.LOANS.getValue());
         * this.loanEventApiJsonValidator
         * .validateRepaymentDateWithMeetingDate(transactionDate,
         * calendarInstance); }
         */

        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        

        final Money repaymentAmount = Money.of(loan.getCurrency(), transactionAmount);
        LoanTransaction newRepaymentTransaction = null;
        if (isRecoveryRepayment) {
            newRepaymentTransaction = LoanTransaction.recoveryRepayment(loan.getOffice(), repaymentAmount, paymentDetail, transactionDate,
                    txnExternalId);
        } else if (isPrepayment) {
            newRepaymentTransaction = LoanTransaction.prepayment(loan.getOffice(), repaymentAmount, paymentDetail, transactionDate,
                    txnExternalId);
        } else if (loan.isNpa()) {
            newRepaymentTransaction = LoanTransaction.repaymentForNPALoan(loan.getOffice(), repaymentAmount, paymentDetail, transactionDate,
                    txnExternalId);
        } else {
            newRepaymentTransaction = LoanTransaction.repayment(loan.getOffice(), repaymentAmount, paymentDetail, transactionDate,
                    txnExternalId);
        }

        LocalDate recalculateFrom = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = transactionDate;
        }
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                holidayDetailDto);

        final boolean allowPaymentsOnClosedLoans = configurationDomainService.isAllowPaymentsOnClosedLoansEnabled();
        
        // to recompute overdue charges
        boolean isOriginalScheduleNeedsUpdate = this.loanOverdueChargeService.updateOverdueChargesOnPayment(loan, transactionDate);
        
        final ChangedTransactionDetail changedTransactionDetail = loan.makeRepayment(newRepaymentTransaction,
                defaultLoanLifecycleStateMachine(), isRecoveryRepayment, scheduleGeneratorDTO, currentUser,
                isHolidayValidationDone, allowPaymentsOnClosedLoans);
        Collection<LoanTransaction> accrualTransactions = null;
        final MonetaryCurrency currency = loan.getCurrency();
        if (loan.isInAccrualSuspense() && newRepaymentTransaction.getTransactionSubTye().isTransactionInNpaState()
                && (newRepaymentTransaction.getInterestPortion(currency).isGreaterThanZero()
                        || newRepaymentTransaction.getFeeChargesPortion(currency).isGreaterThanZero() || newRepaymentTransaction
                        .getPenaltyChargesPortion(currency).isGreaterThanZero())) {
            accrualTransactions = createAccrualSuspenseReverseTransaction(loan, newRepaymentTransaction);
        }
        saveLoanTransactionWithDataIntegrityViolationChecks(newRepaymentTransaction);
        if (accrualTransactions != null && !accrualTransactions.isEmpty()) {
            for(LoanTransaction transaction : accrualTransactions){
                saveLoanTransactionWithDataIntegrityViolationChecks(transaction);
            }
        }
        
        /***
         * TODO Vishwas Batch save is giving me a
         * HibernateOptimisticLockingFailureException, looping and saving for
         * the time being, not a major issue for now as this loop is entered
         * only in edge cases (when a payment is made before the latest payment
         * recorded against the loan)
         ***/
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
        
        if (!isRecoveryRepayment && loan.isSubsidyApplicable()
                && isRealizationTransactionApplicable(loan, transactionDate, scheduleGeneratorDTO)) {
            if(!loan.isOpen()){
            	loan.activateLoan();
            }
        	final LoanTransaction newrealizationLoanSubsidyTransaction = LoanTransaction.realizationLoanSubsidy(loan.getOffice(),
                    loan.getTotalSubsidyAmount(), paymentDetail, transactionDate, txnExternalId);
	        final ChangedTransactionDetail changedTransactionDetailAfterRealization = loan.makeRepayment(
                    newrealizationLoanSubsidyTransaction, defaultLoanLifecycleStateMachine(), isRecoveryRepayment,
                    scheduleGeneratorDTO, currentUser, isHolidayValidationDone, allowPaymentsOnClosedLoans);
             saveLoanTransactionWithDataIntegrityViolationChecks(newrealizationLoanSubsidyTransaction);
            if (changedTransactionDetail != null && changedTransactionDetailAfterRealization != null) {
                changedTransactionDetail.getNewTransactionMappings().putAll(
                	changedTransactionDetailAfterRealization.getNewTransactionMappings());
	    }
            saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
        }
        
        if (changedTransactionDetail != null) {
            for (Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                saveLoanTransactionWithDataIntegrityViolationChecks(mapEntry.getValue());
                // update loan with references to the newly created transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        if (StringUtils.isNotBlank(noteText)) {
            final Note note = Note.loanTransactionNote(loan, newRepaymentTransaction, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer, isLoanToLoanTransfer);

        //Fix to recalculate Accruals only in case of back dated entries.
        if (!DateUtils.getLocalDateOfTenant().isEqual(transactionDate) || (loan.status().isClosedObligationsMet() || loan.status().isOverpaid())) {
        	recalculateAccruals(loan);
        }
        
        if(isOriginalScheduleNeedsUpdate && loan.isInterestRecalculationEnabled()){
            this.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
        }

        if(changedTransactionDetail != null){
            Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail); 
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT, changedTransactionEntityMap);        	
        }
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, newRepaymentTransaction));

        // disable all active standing orders linked to this loan if status changes to closed
        disableStandingInstructionsLinkedToClosedLoan(loan);

        builderResult.withEntityId(newRepaymentTransaction.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()); //

        return newRepaymentTransaction;
    }
    
    public Boolean isRealizationTransactionApplicable(final Loan loan, final LocalDate transactionDate,
            final ScheduleGeneratorDTO scheduleGeneratorDTO) {
        if (loan.getTotalSubsidyAmount().isGreaterThanZero() && loan.getTotalSubsidyAmount().plus(loan.getTotalTransactionAmountPaid())
                .isGreaterThanOrEqualTo(loan.getPrincpal().plus(loan.getTotalInterestAmountTillDate(transactionDate)))) {
            final MonetaryCurrency currency = loan.getCurrency();
            boolean calcualteInterestTillDate = false;
            final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = loan.fetchPrepaymentDetail(scheduleGeneratorDTO,
                    transactionDate, calcualteInterestTillDate);
            Money totalOutstandingLoanBalance = loanRepaymentScheduleInstallment.getTotalOutstanding(currency).minus(
                    loan.getTotalSubsidyAmount().getAmount());
            if (totalOutstandingLoanBalance.isLessThan(Money.of(currency, BigDecimal.ONE))) { return true; }
        }
        return false;
    }
    
    @Transactional
    @Override
    public LoanTransaction addOrRevokeLoanSubsidy(final Loan loan, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final Money transactionAmount, final PaymentDetail paymentDetail, final String txnExternalId,
            boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto, LoanTransactionType loanTransactionType) {
        AppUser currentUser = getAppUserIfPresent();
        checkClientOrGroupActive(loan);
        BUSINESS_EVENTS businessEvent = null;
        LoanEvent loanEvent = null;
        if (loanTransactionType.isAddSubsidy()) {
            businessEvent = BUSINESS_EVENTS.LOAN_ADD_SUBSIDY;
            loanEvent = LoanEvent.LOAN_ADD_SUBSIDY;
        } else {
            businessEvent = BUSINESS_EVENTS.LOAN_REVOKE_SUBSIDY;
            loanEvent = LoanEvent.LOAN_REVOKE_SUBSIDY;
        }
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(businessEvent, constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        existingTransactionIds.addAll(loan.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());

        LoanTransaction newSubsidyTransaction = LoanTransaction.addOrRevokeLoanSubsidy(loan.getOffice(), transactionAmount, paymentDetail,
                transactionDate, txnExternalId, loanTransactionType);

        LocalDate recalculateFrom = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = transactionDate;
        }
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                holidayDetailDto);
        
        newSubsidyTransaction.updateLoan(loan);

        final ChangedTransactionDetail changedTransactionDetail = loan.addOrRevokeSubsidyTransaction(newSubsidyTransaction,
                scheduleGeneratorDTO, currentUser, loanEvent);

        saveLoanTransactionWithDataIntegrityViolationChecks(newSubsidyTransaction);

        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

        if (changedTransactionDetail != null) {
            for (Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                saveLoanTransactionWithDataIntegrityViolationChecks(mapEntry.getValue());
                // update loan with references to the newly created transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
            Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail); 
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(businessEvent, changedTransactionEntityMap);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(businessEvent,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, newSubsidyTransaction));

        builderResult.withEntityId(newSubsidyTransaction.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()); //

        return newSubsidyTransaction;
    }
    
    private void saveLoanTransactionWithDataIntegrityViolationChecks(LoanTransaction newRepaymentTransaction) {
        try {
            this.loanTransactionRepository.save(newRepaymentTransaction);
        } catch (DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").value(newRepaymentTransaction.getExternalId())
                        .failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    private void saveAndFlushLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                }
            }
            this.loanRepository.saveAndFlush(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    @Override
    public void saveLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                }
            }
            this.loanRepository.save(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    @Override
    @Transactional
    public LoanTransaction makeChargePayment(final Loan loan, final Long chargeId, final LocalDate transactionDate,
            final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText, final String txnExternalId,
            final Integer transactionType, Integer installmentNumber) {
        boolean isAccountTransfer = true;
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_CHARGE_PAYMENT,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        final Money paymentAmout = Money.of(loan.getCurrency(), transactionAmount);
        final LoanTransactionType loanTransactionType = LoanTransactionType.fromInt(transactionType);
        LoanTransactionSubType subType = null;
        if (loan.isNpa()) {
            subType = LoanTransactionSubType.TRANSACTION_IN_NPA_STATE;
        }
        final LoanTransaction newPaymentTransaction = LoanTransaction.loanPayment(null, loan.getOffice(), paymentAmout, paymentDetail,
                transactionDate, txnExternalId, loanTransactionType, subType);

        if (loanTransactionType.isRepaymentAtDisbursement()) {
            loan.handlePayDisbursementTransaction(chargeId, newPaymentTransaction, existingTransactionIds, existingReversedTransactionIds);
        } else {
            final boolean allowTransactionsOnHoliday = this.configurationDomainService.allowTransactionsOnHolidayEnabled();
            final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loan.getOfficeId(),
                    transactionDate.toDate(), HolidayStatusType.ACTIVE.getValue());
            final WorkingDays workingDays = this.workingDaysRepository.findOne();
            final boolean allowTransactionsOnNonWorkingDay = this.configurationDomainService.allowTransactionsOnNonWorkingDayEnabled();
            final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
            final List<WorkingDayExemptionsData> workingDayExemptions = this.workingDayExcumptionsReadPlatformService.getWorkingDayExemptionsForEntityType(EntityAccountType.LOAN.getValue());
            HolidayDetailDTO holidayDetailDTO = new HolidayDetailDTO(isHolidayEnabled, holidays, workingDays, allowTransactionsOnHoliday,
                    allowTransactionsOnNonWorkingDay, workingDayExemptions);

            loan.makeChargePayment(chargeId, defaultLoanLifecycleStateMachine(), existingTransactionIds, existingReversedTransactionIds,
                    holidayDetailDTO, newPaymentTransaction, installmentNumber);
        }
        
        Collection<LoanTransaction> accrualTransactions = null;
        final MonetaryCurrency currency = loan.getCurrency();
        if (loan.isInAccrualSuspense() && newPaymentTransaction.getTransactionSubTye().isTransactionInNpaState()
                && (newPaymentTransaction.getFeeChargesPortion(currency).isGreaterThanZero() || newPaymentTransaction
                        .getPenaltyChargesPortion(currency).isGreaterThanZero())) {
            accrualTransactions = createAccrualSuspenseReverseTransaction(loan, newPaymentTransaction);
        }
        saveLoanTransactionWithDataIntegrityViolationChecks(newPaymentTransaction);
        if (accrualTransactions != null && accrualTransactions.isEmpty()) {
            for(LoanTransaction transaction : accrualTransactions){
                saveLoanTransactionWithDataIntegrityViolationChecks(transaction);
            }
        }
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

        if (StringUtils.isNotBlank(noteText)) {
            final Note note = Note.loanTransactionNote(loan, newPaymentTransaction, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_CHARGE_PAYMENT,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, newPaymentTransaction));
        return newPaymentTransaction;
    }

    private void postJournalEntries(final Loan loanAccount, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, boolean isAccountTransfer) {
        postJournalEntries(loanAccount,existingTransactionIds,existingReversedTransactionIds,isAccountTransfer, false);
    }

    private void postJournalEntries(final Loan loanAccount, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, boolean isAccountTransfer, boolean isLoanToLoanTransfer) {

        final MonetaryCurrency currency = loanAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);

        final Map<String, Object> accountingBridgeData = loanAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        accountingBridgeData.put("isLoanToLoanTransfer", isLoanToLoanTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
    }

    private LoanLifecycleStateMachine defaultLoanLifecycleStateMachine() {
        final List<LoanStatus> allowedLoanStatuses = Arrays.asList(LoanStatus.values());
        return new DefaultLoanLifecycleStateMachine(allowedLoanStatuses);
    }

    private void checkClientOrGroupActive(final Loan loan) {
        final Client client = loan.client();
        if (client != null) {
            if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
        }
        final Group group = loan.group();
        if (group != null) {
            if (group.isNotActive()) { throw new GroupNotActiveException(group.getId()); }
        }
    }

    @Override
    public LoanTransaction makeRefund(final Long accountId, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText,
            final String txnExternalId,final Boolean isAccountTransfer) {
        
        final Loan loan = this.loanAccountAssembler.assembleFrom(accountId);
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_REFUND,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        Money refundAmount = Money.of(loan.getCurrency(), transactionAmount);
        if(refundAmount.isZero()){
        	refundAmount = refundAmount.plus(loan.getTotalOverpaid());
        }
        final LoanTransaction newRefundTransaction = LoanTransaction.refund(loan.getOffice(), refundAmount, paymentDetail, transactionDate,
                txnExternalId);
        final boolean allowTransactionsOnHoliday = this.configurationDomainService.allowTransactionsOnHolidayEnabled();
        final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loan.getOfficeId(),
                transactionDate.toDate(), HolidayStatusType.ACTIVE.getValue());
        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final boolean allowTransactionsOnNonWorkingDay = this.configurationDomainService.allowTransactionsOnNonWorkingDayEnabled();

        loan.makeRefund(newRefundTransaction, defaultLoanLifecycleStateMachine(), existingTransactionIds, existingReversedTransactionIds,
                allowTransactionsOnHoliday, holidays, workingDays, allowTransactionsOnNonWorkingDay);

        saveLoanTransactionWithDataIntegrityViolationChecks(newRefundTransaction);
        this.loanRepository.save(loan);

        if (StringUtils.isNotBlank(noteText)) {
            final Note note = Note.loanTransactionNote(loan, newRefundTransaction, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_REFUND,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, newRefundTransaction));
        builderResult.withEntityId(newRefundTransaction.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()); //

        return newRefundTransaction;
    }
    

    @Transactional
    @Override
    public LoanTransaction makeDisburseTransaction(final Long loanId, final LocalDate transactionDate, final BigDecimal transactionAmount,
            final PaymentDetail paymentDetail, final String noteText, final String txnExternalId) {
        return makeDisburseTransaction(loanId, transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId, false);
    }

    @Transactional
    @Override
    public LoanTransaction makeDisburseTransaction(final Long loanId, final LocalDate transactionDate, final BigDecimal transactionAmount,
            final PaymentDetail paymentDetail, final String noteText, final String txnExternalId, final boolean isLoanToLoanTransfer) {
        final Loan loan = this.loanAccountAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        boolean isAccountTransfer = true;
        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        
        final Money amount = Money.of(loan.getCurrency(), transactionAmount);
        LoanTransaction disbursementTransaction = LoanTransaction.disbursement(loan.getOffice(), amount, paymentDetail, transactionDate,
                txnExternalId);
        disbursementTransaction.updateLoan(loan);
        loan.getLoanTransactions().add(disbursementTransaction);
        saveLoanTransactionWithDataIntegrityViolationChecks(disbursementTransaction);
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

        if (StringUtils.isNotBlank(noteText)) {
            final Note note = Note.loanTransactionNote(loan, disbursementTransaction, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer, isLoanToLoanTransfer);
        return disbursementTransaction;
    }

    @Override
    public void reverseTransfer(final LoanTransaction loanTransaction) {
        loanTransaction.reverse();
        saveLoanTransactionWithDataIntegrityViolationChecks(loanTransaction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService
     * #recalculateAccruals(org.apache.fineract.portfolio.loanaccount.domain.Loan)
     */
    @Override
    public void recalculateAccruals(Loan loan) {
        boolean isInterestCalcualtionHappened = loan.repaymentScheduleDetail().isInterestRecalculationEnabled();
        recalculateAccruals(loan, isInterestCalcualtionHappened);
    }

    @Override
    public void recalculateAccruals(Loan loan, boolean isInterestCalcualtionHappened) {
        LocalDate accruedTill = loan.getAccruedTill();
        if (!loan.isPeriodicAccrualAccountingEnabledOnLoanProduct()
        		|| (loan.loanInterestRecalculationDetails() != null && loan.loanInterestRecalculationDetails().isCompoundingToBePostedAsTransaction())
                || ((accruedTill == null || !isInterestCalcualtionHappened ) && loan.isOpen())
                || (!loan.isOpen() && !(loan.status().isOverpaid() || loan.status().isClosedObligationsMet()))) { return; }
        boolean accrueAllInstallments = false;
        if(loan.status().isClosedObligationsMet() || loan.status().isOverpaid()){
            accrueAllInstallments = true;
        }
        boolean isOrganisationDateEnabled = this.configurationDomainService.isOrganisationstartDateEnabled();
        Date organisationStartDate = new Date();
        if(isOrganisationDateEnabled){
            organisationStartDate = this.configurationDomainService.retrieveOrganisationStartDate(); 
        }
        Collection<LoanScheduleAccrualData> loanScheduleAccrualDatas = new ArrayList<>();
        List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
        Long loanId = loan.getId();
        Long officeId = loan.getOfficeId();
        LocalDate accrualStartDate = null;
        PeriodFrequencyType repaymentFrequency = loan.repaymentScheduleDetail().getRepaymentPeriodFrequencyType();
        Integer repayEvery = loan.repaymentScheduleDetail().getRepayEvery();
        LocalDate interestCalculatedFrom = loan.getInterestChargedFromDate();
        Long loanProductId = loan.productId();
        MonetaryCurrency currency = loan.getCurrency();
        ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        CurrencyData currencyData = applicationCurrency.toData();
        Set<LoanCharge> loanCharges = loan.charges();
        if(accrueAllInstallments){
            accruedTill = loan.getLastUserTransactionDate();
        }
        boolean changeAccrualDate = true;
        for (LoanRepaymentScheduleInstallment installment : installments) {
            
            if (changeAccrualDate && accruedTill != null && accruedTill.isAfter(loan.getMaturityDate())) {
                if (accruedTill.isEqual(installment.getDueDate())) {
                    changeAccrualDate = false;
                }else if (installment.getDueDate().isAfter(loan.getMaturityDate())) {
                    accruedTill = DateUtils.getLocalDateOfTenant();
                    changeAccrualDate = false;
                }
            }
            if(!isOrganisationDateEnabled || new LocalDate(organisationStartDate).isBefore(installment.getDueDate())){
                generateLoanScheduleAccrualData(accruedTill, loanScheduleAccrualDatas, loanId, officeId, accrualStartDate, repaymentFrequency, 
                        repayEvery, interestCalculatedFrom, loanProductId, currency, currencyData, loanCharges, installment, accrueAllInstallments,
                        loan.isNpa());
            }
        }
       

        if (!loanScheduleAccrualDatas.isEmpty()) {
            String error = this.loanAccrualPlatformService.addPeriodicAccruals(accruedTill, loanScheduleAccrualDatas, accrueAllInstallments);
            if (error.length() > 0) {
                String globalisationMessageCode = "error.msg.accrual.exception";
                throw new GeneralPlatformDomainRuleException(globalisationMessageCode, error, error);
            }
        }
    }

    private void generateLoanScheduleAccrualData(final LocalDate accruedTill, final Collection<LoanScheduleAccrualData> loanScheduleAccrualDatas, 
            final Long loanId, Long officeId, final LocalDate accrualStartDate, final PeriodFrequencyType repaymentFrequency, final Integer repayEvery, 
            final LocalDate interestCalculatedFrom, final Long loanProductId, final MonetaryCurrency currency, final CurrencyData currencyData, 
            final Set<LoanCharge> loanCharges, final LoanRepaymentScheduleInstallment installment, final boolean includeAllInstallments, 
            final boolean isNpa) {
        
        if (includeAllInstallments || !accruedTill.isBefore(installment.getDueDate())
               /* || (accruedTill.isAfter(installment.getFromDate()) && !accruedTill.isAfter(installment.getDueDate()))*/) {
            BigDecimal dueDateFeeIncome = BigDecimal.ZERO;
            BigDecimal dueDatePenaltyIncome = BigDecimal.ZERO;
            LocalDate chargesTillDate = installment.getDueDate();
            if (!accruedTill.isAfter(installment.getDueDate())) {
                chargesTillDate = accruedTill;
            }

            for (final LoanCharge loanCharge : loanCharges) {
                if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(installment.getFromDate(), chargesTillDate)) {
                    if (loanCharge.isFeeCharge()) {
                        dueDateFeeIncome = dueDateFeeIncome.add(loanCharge.amount());
                    } else if (loanCharge.isPenaltyCharge()) {
                        dueDatePenaltyIncome = dueDatePenaltyIncome.add(loanCharge.amount());
                    }
                }
            }
            LoanScheduleAccrualData accrualData = new LoanScheduleAccrualData(loanId, officeId, installment.getInstallmentNumber(),
                    accrualStartDate, repaymentFrequency, repayEvery, installment.getDueDate(), installment.getFromDate(),
                    installment.getId(), loanProductId, installment.getInterestCharged(currency).getAmount(), installment
                            .getFeeChargesCharged(currency).getAmount(), installment.getPenaltyChargesCharged(currency).getAmount(),
                    installment.getInterestAccrued(currency).getAmount(), installment.getFeeAccrued(currency).getAmount(), installment
                            .getPenaltyAccrued(currency).getAmount(), currencyData, interestCalculatedFrom, installment
                            .getInterestWaived(currency).getAmount(), isNpa);
            loanScheduleAccrualDatas.add(accrualData);

        }
    }

    private void updateLoanTransaction(final Long loanTransactionId, final LoanTransaction newLoanTransaction) {
        final AccountTransferTransaction transferTransaction = this.accountTransferRepository.findByToLoanTransactionId(loanTransactionId);
        if (transferTransaction != null) {
            transferTransaction.updateToLoanTransaction(newLoanTransaction);
            this.accountTransferRepository.save(transferTransaction);
        }
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    @Override
    public LoanTransaction makeRefundForActiveLoan(Long accountId, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId) {
        final Loan loan = this.loanAccountAssembler.assembleFrom(accountId);
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_REFUND,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        final Money refundAmount = Money.of(loan.getCurrency(), transactionAmount);
        final LoanTransaction newRefundTransaction = LoanTransaction.refundForActiveLoan(loan.getOffice(), refundAmount, paymentDetail,
                transactionDate, txnExternalId);
        final boolean allowTransactionsOnHoliday = this.configurationDomainService.allowTransactionsOnHolidayEnabled();
        final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loan.getOfficeId(),
                transactionDate.toDate(), HolidayStatusType.ACTIVE.getValue());
        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final boolean allowTransactionsOnNonWorkingDay = this.configurationDomainService.allowTransactionsOnNonWorkingDayEnabled();

        loan.makeRefundForActiveLoan(newRefundTransaction, defaultLoanLifecycleStateMachine(), existingTransactionIds,
                existingReversedTransactionIds, allowTransactionsOnHoliday, holidays, workingDays, allowTransactionsOnNonWorkingDay);

        this.loanTransactionRepository.save(newRefundTransaction);
        this.loanRepository.save(loan);

        if (StringUtils.isNotBlank(noteText)) {
            final Note note = Note.loanTransactionNote(loan, newRefundTransaction, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, false);
        recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_REFUND,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, newRefundTransaction));

        builderResult.withEntityId(newRefundTransaction.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()); //

        return newRefundTransaction;
    }

    @Override
    public LoanTransaction foreCloseLoan(final Loan loan, final LocalDate foreClosureDate, final String noteText,
            final boolean isAccountTransfer, final boolean isLoanToLoanTransfer, Map<String, Object> changes) {
        LoanRescheduleRequest loanRescheduleRequest = null;
        if (loan.hasUndisbursedTranches()) {
            final String defaultUserMessage = "The loan with undisbrsed tranche cannot be foreclosed.";
            throw new LoanForeclosureException("loan.with.undisbursed.tranche.cannot.be.foreclosured",
                    defaultUserMessage, foreClosureDate);
        }
        this.loanScheduleHistoryWritePlatformService.createAndSaveLoanScheduleArchive(loan.getRepaymentScheduleInstallments(),
                loan, loanRescheduleRequest);
        
        final HolidayDetailDTO holidayDetailDTO = this.loanUtilService.constructHolidayDTO(loan);
        String errorMessage = "Foreclosure date should not be a holiday.";
        String errorCode = "foreclosure.date.on.holiday";
        loan.validateDateIsOnHoliday(foreClosureDate, holidayDetailDTO.isAllowTransactionsOnHoliday(), holidayDetailDTO.getHolidays(),
                errorMessage, errorCode);
        errorMessage = "Foreclosure date should not be a non working day.";
        errorCode = "foreclosure.date.on.non.working.day";
        loan.validateDateIsOnNonWorkingDay(foreClosureDate, holidayDetailDTO.getWorkingDays(),
                holidayDetailDTO.isAllowTransactionsOnNonWorkingDay(), errorMessage, errorCode);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_FORECLOSURE,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        
        MonetaryCurrency currency = loan.getCurrency();
        LocalDateTime createdDate = DateUtils.getLocalDateTimeOfTenant();
        List<LoanTransaction> newTransactions = new ArrayList<>();

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        existingTransactionIds.addAll(loan.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
        final ScheduleGeneratorDTO scheduleGeneratorDTO = null;
        AppUser appUser = getAppUserIfPresent();
        final LoanRepaymentScheduleInstallment foreCloseDetail = loan.fetchLoanForeclosureDetail(foreClosureDate);

        Money interestPayable = foreCloseDetail.getInterestCharged(currency);
        Money feePayable = foreCloseDetail.getFeeChargesCharged(currency);
        Money penaltyPayable = foreCloseDetail.getPenaltyChargesCharged(currency);
        Money payPrincipal = foreCloseDetail.getPrincipal(currency);        
        loan.updateInstallmentsPostDate(foreClosureDate);

        LoanTransaction payment = null;
        Collection<LoanTransaction> suspenseReverse = null;
        
         
        if (payPrincipal.plus(interestPayable).plus(feePayable).plus(penaltyPayable).isGreaterThanZero()) {
            final PaymentDetail paymentDetail = null;
            String externalId = null;            
            payment = LoanTransaction.repayment(loan.getOffice(), payPrincipal.plus(interestPayable).plus(feePayable).plus(penaltyPayable),
                    paymentDetail, foreClosureDate, externalId);
            createdDate = createdDate.plusSeconds(1);
            payment.updateCreatedDate(createdDate.toDate());
            payment.updateLoan(loan);
            newTransactions.add(payment);
           
        }

        List<Long> transactionIds = new ArrayList<>();
        final ChangedTransactionDetail changedTransactionDetail = loan.handleForeClosureTransactions(payment,
                defaultLoanLifecycleStateMachine(), scheduleGeneratorDTO, appUser);
        
        if (loan.isInAccrualSuspense() && interestPayable.plus(feePayable).plus(penaltyPayable).isGreaterThanZero()) {
            suspenseReverse = createAccrualSuspenseReverseTransaction(loan, payment);
            if(suspenseReverse != null){
                newTransactions.addAll(suspenseReverse);
            }
        }

        /***
         * TODO Vishwas Batch save is giving me a
         * HibernateOptimisticLockingFailureException, looping and saving for
         * the time being, not a major issue for now as this loop is entered
         * only in edge cases (when a payment is made before the latest payment
         * recorded against the loan)
         ***/

        for (LoanTransaction newTransaction : newTransactions) {
            saveLoanTransactionWithDataIntegrityViolationChecks(newTransaction);
            transactionIds.add(newTransaction.getId());
        }
        changes.put("transactions", transactionIds);
        changes.put("eventAmount", payPrincipal.getAmount().negate());
        
        if (changedTransactionDetail != null) {
            for (Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                saveLoanTransactionWithDataIntegrityViolationChecks(mapEntry.getValue());
                // update loan with references to the newly created transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            final Note note = Note.loanNote(loan, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer,isLoanToLoanTransfer);
        recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_FORECLOSURE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, payment));
        return payment;

    }
    
    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }
    
    @Override
    public AdjustedLoanTransactionDetails reverseLoanTransactions(final Loan loan, final Long transactionId,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final String txnExternalId, final Locale locale,
            final DateTimeFormatter dateFormat, final String noteText, final PaymentDetail paymentDetail, final boolean isAccountTransfer, 
            final boolean isLoanToLoanTransfer) {

        AppUser currentUser = getAppUserIfPresent();
        final Map<String, Object> changes = new LinkedHashMap<>();
        checkClientOrGroupActive(loan);
        final LoanTransaction transactionToAdjust = this.loanTransactionRepository.findOneWithNotFoundDetection(transactionId);

        if (loan.status().isClosed() && loan.getLoanSubStatus() != null
                && loan.getLoanSubStatus().equals(LoanSubStatus.FORECLOSED.getValue()) && transactionAmount != null
                && transactionAmount.compareTo(BigDecimal.ZERO) == 1) {
            final String defaultUserMessage = "The loan cannot adjusted as it is foreclosed.";
            throw new LoanForeclosureException("loan.cannot.be.adjusted.as.it.is.foreclosured", defaultUserMessage, loan.getId());
        }
        if (loan.isClosedWrittenOff()
                && !transactionToAdjust.isRecoveryRepaymentTransaction()) { throw new PlatformServiceUnavailableException(
                        "error.msg.loan.written.off.update.not.allowed",
                        "Loan transaction:" + transactionId + " update not allowed as loan status is written off", transactionId); }

        changes.put("transactionDate", transactionDate);
        changes.put("transactionAmount", transactionAmount);
        changes.put("locale", locale);
        changes.put("dateFormat", dateFormat);
        this.loanAccountAssembler.setHelpers(loan);
        if (paymentDetail != null) changes.put("paymentTypeId", paymentDetail.getId());

        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());

        final Money transactionAmountAsMoney = Money.of(loan.getCurrency(), transactionAmount);
        LoanTransaction newTransactionDetail = null;
        if(transactionToAdjust.getTransactionSubTye().isPrePayment()){
             newTransactionDetail = LoanTransaction.prepayment(loan.getOffice(), transactionAmountAsMoney, paymentDetail,
                    transactionDate, txnExternalId);
        } else if (transactionToAdjust.isRecoveryRepaymentTransaction()) {
            newTransactionDetail = LoanTransaction.recoveryRepayment(loan.getOffice(), transactionAmountAsMoney, paymentDetail,
                    transactionDate, txnExternalId);
        } else if (loan.isNpa()) {
            newTransactionDetail = LoanTransaction.repaymentForNPALoan(loan.getOffice(), transactionAmountAsMoney, paymentDetail,
                    transactionDate, txnExternalId);
        } else {
            newTransactionDetail = LoanTransaction.repayment(loan.getOffice(), transactionAmountAsMoney, paymentDetail,
                    transactionDate, txnExternalId);
        }
        if (transactionToAdjust.isInterestWaiver()) {
            Money unrecognizedIncome = transactionAmountAsMoney.zero();
            Money interestComponent = transactionAmountAsMoney;
            if (loan.isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
                Money receivableInterest = loan.getReceivableInterest(transactionDate);
                if (transactionAmountAsMoney.isGreaterThan(receivableInterest)) {
                    interestComponent = receivableInterest;
                    unrecognizedIncome = transactionAmountAsMoney.minus(receivableInterest);
                }
                
                if (loan.isInAccrualSuspense() && !transactionToAdjust.getTransactionSubTye().isTransactionInNpaState()) {
                    MonetaryCurrency currency = loan.getCurrency();
                    Money interestComponentOnAdjustedTransaction = transactionToAdjust.getInterestPortion(currency);
                    if (interestComponentOnAdjustedTransaction.isGreaterThanZero()) {
                        LoanTransaction accrualSuspenseTransaction = LoanTransaction.accrualSuspense(loan, loan.getOffice(),
                                interestComponentOnAdjustedTransaction, interestComponentOnAdjustedTransaction,
                                interestComponentOnAdjustedTransaction.zero(), interestComponentOnAdjustedTransaction.zero(),
                                transactionToAdjust.getTransactionDate());
                        loan.getLoanTransactions().add(accrualSuspenseTransaction);
                        this.loanTransactionRepository.save(accrualSuspenseTransaction);
                    }
                }

            }
            newTransactionDetail = LoanTransaction.waiver(loan.getOffice(), loan, transactionAmountAsMoney, transactionDate,
                    interestComponent, unrecognizedIncome);
        }
        LocalDate recalculateFrom = null;

        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = transactionToAdjust.getTransactionDate().isAfter(transactionDate) ? transactionDate : transactionToAdjust
                    .getTransactionDate();
        }
        final boolean considerFutureDisbursmentsInSchedule = loan.loanProduct().considerFutureDisbursementsInSchedule();
        final boolean considerAllDisbursmentsInSchedule =loan.loanProduct().considerAllDisbursementsInSchedule();

        ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                considerFutureDisbursmentsInSchedule, considerAllDisbursmentsInSchedule);
        
        // to recompute overdue charges
        boolean isOriginalScheduleNeedsUpdate = this.loanOverdueChargeService.updateOverdueChargesOnAdjustPayment(loan, transactionToAdjust, newTransactionDetail);

        final ChangedTransactionDetail changedTransactionDetail = loan.adjustExistingTransaction(newTransactionDetail,
                defaultLoanLifecycleStateMachine(), transactionToAdjust, scheduleGeneratorDTO, currentUser);

        if (newTransactionDetail.isGreaterThanZero(loan.getPrincpal().getCurrency())) {
            if (paymentDetail != null) {
                this.paymentDetailWritePlatformService.persistPaymentDetail(paymentDetail);
            }
            this.loanTransactionRepository.save(newTransactionDetail);
            if(newTransactionDetail.isWaiver()){
                handleWaiverForAccrualSuspense(loan, newTransactionDetail);
            }
        }
        
        MonetaryCurrency currency = loan.getCurrency();
        if (newTransactionDetail.isPaymentTransaction() && !newTransactionDetail.isInterestWaiver() && newTransactionDetail.getTransactionSubTye().isTransactionInNpaState()
                && loan.isInAccrualSuspense()) {
            if (transactionToAdjust.getInterestPortion(currency).isNotEqualTo(newTransactionDetail.getInterestPortion(currency))
                    || transactionToAdjust.getFeeChargesPortion(currency).isNotEqualTo(newTransactionDetail.getFeeChargesPortion(currency))
                    || transactionToAdjust.getPenaltyChargesPortion(currency).isNotEqualTo(
                            newTransactionDetail.getPenaltyChargesPortion(currency))) {
                LoanTransaction accrualSuspenseTransaction = createAccrualSuspenseTransaction(loan, transactionToAdjust);
                if (accrualSuspenseTransaction != null) {
                    this.loanTransactionRepository.save(accrualSuspenseTransaction);
                    loan.getLoanTransactions().add(accrualSuspenseTransaction);
                }
                if (newTransactionDetail.isGreaterThanZero(loan.getPrincpal().getCurrency())) {
                    Collection<LoanTransaction> accrualTransactions = createAccrualSuspenseReverseTransaction(loan, newTransactionDetail);
                    
                    if (accrualTransactions != null && accrualTransactions.isEmpty()) {
                        for(LoanTransaction transaction : accrualTransactions){
                            saveLoanTransactionWithDataIntegrityViolationChecks(transaction);
                        }
                    }

                }
            }
        }

        /***
         * TODO Vishwas Batch save is giving me a
         * HibernateOptimisticLockingFailureException, looping and saving for
         * the time being, not a major issue for now as this loop is entered
         * only in edge cases (when a adjustment is made before the latest
         * payment recorded against the loan)
         ***/
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                // update loan with references to the newly created transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                this.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            Note note = null;
            /**
             * If a new transaction is not created, associate note with the
             * transaction to be adjusted
             **/
            if (newTransactionDetail.isGreaterThanZero(loan.getPrincpal().getCurrency())) {
                note = Note.loanTransactionNote(loan, newTransactionDetail, noteText);
            } else {
                note = Note.loanTransactionNote(loan, transactionToAdjust, noteText);
            }
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer, isLoanToLoanTransfer);

        recalculateAccruals(loan);
        
        if(isOriginalScheduleNeedsUpdate){
            this.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
        }

        return new AdjustedLoanTransactionDetails(changes, transactionToAdjust, newTransactionDetail);

    }

    @Override
    @Transactional
    public void disableStandingInstructionsLinkedToClosedLoan(Loan loan) {
        if ((loan != null) && (loan.status() != null) && loan.status().isClosed()) {
            final Integer standingInstructionStatus = StandingInstructionStatus.ACTIVE.getValue();
            final Collection<AccountTransferStandingInstruction> accountTransferStandingInstructions = this.standingInstructionRepository
                    .findByLoanAccountAndStatus(loan, standingInstructionStatus);
            
            if (!accountTransferStandingInstructions.isEmpty()) {
                for (AccountTransferStandingInstruction accountTransferStandingInstruction : accountTransferStandingInstructions) {
                    accountTransferStandingInstruction.updateStatus(StandingInstructionStatus.DISABLED.getValue());
                    this.standingInstructionRepository.save(accountTransferStandingInstruction);
                }
            }
        }
    }
    
    @Override
    public LoanTransaction waiveInterest(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, String noteText, Map<String, Object> changes) {
        
        AppUser currentUser = getAppUserIfPresent();
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        
        final Money transactionAmountAsMoney = Money.of(loan.getCurrency(), transactionAmount);
        Money unrecognizedIncome = transactionAmountAsMoney.zero();
        Money interestComponent = transactionAmountAsMoney;
        if (loan.isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
            Money receivableInterest = loan.getReceivableInterest(transactionDate);
            if (transactionAmountAsMoney.isGreaterThan(receivableInterest)) {
                interestComponent = receivableInterest;
                unrecognizedIncome = transactionAmountAsMoney.minus(receivableInterest);
            }
        }
        
        final LoanTransaction waiveInterestTransaction = LoanTransaction.waiver(loan.getOffice(), loan, transactionAmountAsMoney,
                transactionDate, interestComponent, unrecognizedIncome);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_WAIVE_INTEREST,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, waiveInterestTransaction));
        LocalDate recalculateFrom = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = transactionDate;
        }

        ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
        final ChangedTransactionDetail changedTransactionDetail = loan.waiveInterest(waiveInterestTransaction,
                defaultLoanLifecycleStateMachine(), existingTransactionIds, existingReversedTransactionIds, scheduleGeneratorDTO,
                currentUser);

        this.loanTransactionRepository.save(waiveInterestTransaction);
        handleWaiverForAccrualSuspense(loan, waiveInterestTransaction);

        /***
         * TODO Vishwas Batch save is giving me a
         * HibernateOptimisticLockingFailureException, looping and saving for
         * the time being, not a major issue for now as this loop is entered
         * only in edge cases (when a adjustment is made before the latest
         * payment recorded against the loan)
         ***/
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                // update loan with references to the newly created transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                this.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
            Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail); 
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WAIVE_INTEREST, changedTransactionEntityMap);
        }

        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            final Note note = Note.loanTransactionNote(loan, waiveInterestTransaction, noteText);
            this.noteRepository.save(note);
        }
        final boolean isAccountTransfer = false;
        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WAIVE_INTEREST,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, waiveInterestTransaction));
        
        builderResult.withEntityId(waiveInterestTransaction.getId()).withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId());
        
        return waiveInterestTransaction;
    }

    @Override
    public void handleWaiverForAccrualSuspense(Loan loan, final LoanTransaction waiveInterestTransaction) {
        if (loan.isInAccrualSuspense()) {
            MonetaryCurrency currency = loan.getCurrency();
            LoanTransaction accrualWriteOff = createAccrualWriteOffTransaction(loan, waiveInterestTransaction);
            waiveInterestTransaction.resetDerivedComponents(true);
            waiveInterestTransaction.updateComponentsAndTotal(Money.zero(currency),Money.zero(currency), Money.zero(currency), Money.zero(currency),
                    waiveInterestTransaction.getAmount(currency));
            if (accrualWriteOff != null) {
                accrualWriteOff.setAssociatedTransactionId(waiveInterestTransaction.getId());
                this.loanTransactionRepository.save(accrualWriteOff);
            }
        }
    }

    @Override
    public LoanTransaction writeOffForGlimLoan(JsonCommand command, Loan loan, CommandProcessingResultBuilder builderResult,
            String noteText, Map<String, Object> changes) {

        AppUser currentUser = getAppUserIfPresent();
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        checkClientOrGroupActive(loan);

        final LocalDate writtenOffOnLocalDate = command.localDateValueOfParameterNamed("transactionDate");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");

        LoanTransaction writeoffTransaction = LoanTransaction.writeOffForGlimLoan(loan, loan.getOffice(), writtenOffOnLocalDate,
                txnExternalId,LoanTransactionSubType.PARTIAL_WRITEOFF.getValue());
        
        if (MathUtility.isZero(transactionAmount)) {
            final String errorMessage = "The transaction amount must be greater then zero";
            throw new InvalidLoanStateTransitionException("writeoff", "transaction.amount.must.be.greater.than.zero", errorMessage,
                    writtenOffOnLocalDate);
        }

        final ChangedTransactionDetail changedTransactionDetail = loan.GlimLoanCloseAsWrittenOff(command, writtenOffOnLocalDate,
                writeoffTransaction, changes, existingTransactionIds, existingReversedTransactionIds, currentUser);

        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                // update loan with references to the newly created transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
            Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail); 
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WRITTEN_OFF, changedTransactionEntityMap);
        }

        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            final Note note = Note.loanTransactionNote(loan, writeoffTransaction, noteText);
            this.noteRepository.save(note);
        }
        final boolean isAccountTransfer = false;
        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WRITTEN_OFF,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, writeoffTransaction));        
        builderResult.withEntityId(writeoffTransaction.getId()).withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId());

        return writeoffTransaction;
    }
    
    private Collection<LoanTransaction> createAccrualSuspenseReverseTransaction(final Loan loan, final LoanTransaction transaction) {
        MonetaryCurrency currency = loan.getCurrency();

        Collection<LoanTransaction> accrualTransactions = new ArrayList<>();
        LoanTransaction accrualSuspenseReverseTransaction = null;
        if (transaction.getInterestPortion(currency).isGreaterThanZero() || transaction.getFeeChargesPortion(currency).isGreaterThanZero()
                || transaction.getPenaltyChargesPortion(currency).isGreaterThanZero()) {
            LoanTransaction accrualSuspenceTransaction = LoanTransaction.accrual(loan, loan.getOffice(), Money.zero(currency),
                    Money.zero(currency), Money.zero(currency), Money.zero(currency), transaction.getTransactionDate());

            Collection<Integer> typeForAddReceivale = new ArrayList<>();
            typeForAddReceivale.add(LoanTransactionType.ACCRUAL_SUSPENSE.getValue());

            Collection<Integer> typeForSubtractReceivale = new ArrayList<>();
            typeForSubtractReceivale.add(LoanTransactionType.ACCRUAL_SUSPENSE_REVERSE.getValue());
            typeForSubtractReceivale.add(LoanTransactionType.ACCRUAL_WRITEOFF.getValue());
            final boolean addToTransactions = false;
            loan.updateTransactionDetails(typeForAddReceivale, typeForSubtractReceivale, accrualSuspenceTransaction, addToTransactions);

            if (accrualSuspenceTransaction.getInterestPortion(currency).isGreaterThanOrEqualTo(transaction.getInterestPortion(currency))
                    && accrualSuspenceTransaction.getFeeChargesPortion(currency).isGreaterThanOrEqualTo(
                            transaction.getFeeChargesPortion(currency))
                    && accrualSuspenceTransaction.getPenaltyChargesPortion(currency).isGreaterThanOrEqualTo(
                            transaction.getPenaltyChargesPortion(currency))) {
                accrualSuspenseReverseTransaction = LoanTransaction.accrualSuspenseReverse(loan, loan.getOffice(),
                        transaction.getAmount(currency).minus(transaction.getPrincipalPortion(currency)),
                        transaction.getInterestPortion(currency), transaction.getFeeChargesPortion(currency),
                        transaction.getPenaltyChargesPortion(currency), transaction.getTransactionDate());
                accrualSuspenseReverseTransaction.copyChargesPaidByFrom(transaction);
                if (accrualSuspenseReverseTransaction.getAmount(currency).isGreaterThanZero()) {
                    loan.getLoanTransactions().add(accrualSuspenseReverseTransaction);
                    accrualTransactions.add(accrualSuspenseReverseTransaction);
                }
            } else {
                Money interestPortionForSuspense = transaction.getInterestPortion(currency);
                Money interestPortionForAccrual = Money.zero(currency);
                Money feePortionForSuspense = transaction.getFeeChargesPortion(currency);
                Money feePortionForAccrual = Money.zero(currency);
                Money penaltyPortionForSuspense = transaction.getPenaltyChargesPortion(currency);
                Money penaltyPortionForAccrual = Money.zero(currency);

                if (!accrualSuspenceTransaction.getInterestPortion(currency).isGreaterThanOrEqualTo(
                        transaction.getInterestPortion(currency))) {
                    interestPortionForSuspense = accrualSuspenceTransaction.getInterestPortion(currency);
                    interestPortionForAccrual = transaction.getInterestPortion(currency).minus(
                            accrualSuspenceTransaction.getInterestPortion(currency));
                }

                if (!accrualSuspenceTransaction.getFeeChargesPortion(currency).isGreaterThanOrEqualTo(
                        transaction.getFeeChargesPortion(currency))) {
                    feePortionForSuspense = accrualSuspenceTransaction.getFeeChargesPortion(currency);
                    feePortionForAccrual = transaction.getFeeChargesPortion(currency).minus(
                            accrualSuspenceTransaction.getFeeChargesPortion(currency));
                }

                if (!accrualSuspenceTransaction.getPenaltyChargesPortion(currency).isGreaterThanOrEqualTo(
                        transaction.getPenaltyChargesPortion(currency))) {
                    penaltyPortionForSuspense = accrualSuspenceTransaction.getPenaltyChargesPortion(currency);
                    penaltyPortionForAccrual = transaction.getPenaltyChargesPortion(currency).minus(
                            accrualSuspenceTransaction.getPenaltyChargesPortion(currency));
                }

                Map<String, LoanChargePaidBy> chargesPaidAccrualMap = new HashMap<>();
                Map<String, LoanChargePaidBy> chargesPaidSuspenceMap = new HashMap<>();
                if (transaction.getFeeChargesPortion(currency).isGreaterThanZero()
                        || transaction.getPenaltyChargesPortion(currency).isGreaterThanZero()) {
                    List<String> paidByList = new ArrayList<>();
                    for (LoanChargePaidBy paidBy : transaction.getLoanChargesPaid()) {
                        chargesPaidAccrualMap.put(loan.chargespaidByKey(paidBy), paidBy);
                    }
                    chargesPaidSuspenceMap.putAll(chargesPaidAccrualMap);

                    for (LoanTransaction loanTransaction : loan.getLoanTransactions()) {
                        if (loanTransaction.isAccrualSuspense()) {
                            for (LoanChargePaidBy paidBy : loanTransaction.getLoanChargesPaid()) {
                                paidByList.add(loan.chargespaidByKey(paidBy));
                            }
                        }
                    }
                    chargesPaidAccrualMap.keySet().removeAll(paidByList);
                    chargesPaidSuspenceMap.keySet().retainAll(paidByList);
                }

                if (interestPortionForSuspense.isGreaterThanZero() || feePortionForSuspense.isGreaterThanZero()
                        || penaltyPortionForSuspense.isGreaterThanZero()) {
                    accrualSuspenseReverseTransaction = LoanTransaction.accrualSuspenseReverse(loan, loan.getOffice(),
                            interestPortionForSuspense.plus(feePortionForSuspense).plus(penaltyPortionForSuspense),
                            interestPortionForSuspense, feePortionForSuspense, penaltyPortionForSuspense, transaction.getTransactionDate());
                    for (LoanChargePaidBy chargePaidByfrom : chargesPaidSuspenceMap.values()) {
                        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(accrualSuspenseReverseTransaction,
                                chargePaidByfrom.getLoanCharge(), chargePaidByfrom.getAmount(), chargePaidByfrom.getInstallmentNumber());
                        accrualSuspenseReverseTransaction.getLoanChargesPaid().add(loanChargePaidBy);
                    }
                    loan.getLoanTransactions().add(accrualSuspenseReverseTransaction);
                    accrualTransactions.add(accrualSuspenseReverseTransaction);

                }

                if (interestPortionForAccrual.isGreaterThanZero() || feePortionForAccrual.isGreaterThanZero()
                        || penaltyPortionForAccrual.isGreaterThanZero()) {
                    Money feeDiff = Money.zero(currency);
                    Money penalDiff = Money.zero(currency);
                    LoanTransaction accrualTransaction = LoanTransaction.accrual(loan, loan.getOffice(),
                            interestPortionForAccrual.plus(feePortionForAccrual).plus(penaltyPortionForAccrual), interestPortionForAccrual,
                            feePortionForAccrual, penaltyPortionForAccrual, transaction.getTransactionDate());
                    LoanTransaction accrualSuspenseTransaction = LoanTransaction.accrualSuspense(loan, loan.getOffice(), transaction
                            .getAmount(currency).minus(transaction.getPrincipalPortion(currency)),
                            transaction.getInterestPortion(currency), transaction.getFeeChargesPortion(currency), transaction
                                    .getPenaltyChargesPortion(currency), transaction.getTransactionDate());
                    for (LoanChargePaidBy chargePaidByfrom : chargesPaidAccrualMap.values()) {

                        Money outstanding = chargePaidByfrom.getLoanCharge().getAmountOutstanding(currency,
                                chargePaidByfrom.getInstallmentNumber());
                        if (outstanding.isGreaterThanZero()) {
                            if (chargePaidByfrom.getLoanCharge().isPenaltyCharge()) {
                                penalDiff = penalDiff.plus(outstanding);
                            } else {
                                feeDiff = feeDiff.plus(outstanding);
                            }
                            final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(accrualTransaction,
                                    chargePaidByfrom.getLoanCharge(), outstanding.plus(chargePaidByfrom.getAmount()).getAmount(),
                                    chargePaidByfrom.getInstallmentNumber());
                            accrualTransaction.getLoanChargesPaid().add(loanChargePaidBy);

                            final LoanChargePaidBy loanChargePaidByForSuspense = new LoanChargePaidBy(accrualSuspenseTransaction,
                                    chargePaidByfrom.getLoanCharge(), outstanding.getAmount(), chargePaidByfrom.getInstallmentNumber());
                            accrualSuspenseTransaction.getLoanChargesPaid().add(loanChargePaidByForSuspense);

                        } else {
                            final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(accrualTransaction,
                                    chargePaidByfrom.getLoanCharge(), chargePaidByfrom.getAmount(), chargePaidByfrom.getInstallmentNumber());
                            accrualTransaction.getLoanChargesPaid().add(loanChargePaidBy);
                        }
                    }
                    loan.getLoanTransactions().add(accrualTransaction);
                    accrualTransactions.add(accrualTransaction);
                    if (feeDiff.isGreaterThanZero() || penalDiff.isGreaterThanZero()) {
                        accrualTransaction.updateChargesComponents(feeDiff, penalDiff);
                        accrualSuspenseTransaction.updateChargesComponents(feeDiff, penalDiff);
                        loan.getLoanTransactions().add(accrualSuspenseTransaction);
                        accrualTransactions.add(accrualSuspenseTransaction);
                    }
                    loan.applyAccurals(getAppUserIfPresent());
                }

            }

        }

        return accrualTransactions;
    }
    
    private LoanTransaction createAccrualSuspenseTransaction(final Loan loan, final LoanTransaction transaction) {
        MonetaryCurrency currency = loan.getCurrency();
        LoanTransaction accrualSuspenseTransaction = LoanTransaction.accrualSuspense(loan, loan.getOffice(), transaction
                .getAmount(currency).minus(transaction.getPrincipalPortion(currency)), transaction.getInterestPortion(currency),
                transaction.getFeeChargesPortion(currency), transaction.getPenaltyChargesPortion(currency), transaction
                        .getTransactionDate());
        accrualSuspenseTransaction.copyChargesPaidByFrom(transaction);
        if(accrualSuspenseTransaction.getAmount(currency).isGreaterThanZero()){
            loan.getLoanTransactions().add(accrualSuspenseTransaction);
        }

        return accrualSuspenseTransaction.getAmount(currency).isGreaterThanZero() ? accrualSuspenseTransaction : null;
    }
    
    private LoanTransaction createAccrualWriteOffTransaction(final Loan loan, final LoanTransaction transaction) {
        MonetaryCurrency currency = loan.getCurrency();
        LoanTransaction accrualWriteOff = LoanTransaction.accrualWriteOff(loan, loan.getOffice(), transaction.getInterestPortion(currency)
                .plus(transaction.getFeeChargesPortion(currency)).plus(transaction.getPenaltyChargesPortion(currency)),
                transaction.getInterestPortion(currency), transaction.getFeeChargesPortion(currency),
                transaction.getPenaltyChargesPortion(currency), transaction.getTransactionDate());
        accrualWriteOff.copyChargesPaidByFrom(transaction);
        if (accrualWriteOff.getAmount(currency).isGreaterThanZero()) {
            loan.getLoanTransactions().add(accrualWriteOff);
        }

        return accrualWriteOff.getAmount(currency).isGreaterThanZero() ? accrualWriteOff : null;
    }

    @Override
    public void createAndSaveLoanScheduleArchive(final Loan loan, ScheduleGeneratorDTO scheduleGeneratorDTO) {
        LoanRescheduleRequest loanRescheduleRequest = null;
        LoanScheduleModel loanScheduleModel = loan.regenerateScheduleModel(scheduleGeneratorDTO);
        List<LoanRepaymentScheduleInstallment> installments = retrieveRepaymentScheduleFromModel(loanScheduleModel);
        this.loanScheduleHistoryWritePlatformService.createAndSaveLoanScheduleArchive(installments, loan, loanRescheduleRequest);
    }

    private List<LoanRepaymentScheduleInstallment> retrieveRepaymentScheduleFromModel(LoanScheduleModel model) {
        final List<LoanRepaymentScheduleInstallment> installments = new ArrayList<>();
        for (final LoanScheduleModelPeriod scheduledLoanInstallment : model.getPeriods()) {
            if (scheduledLoanInstallment.isRepaymentPeriod()) {
                final LoanRepaymentScheduleInstallment installment = new LoanRepaymentScheduleInstallment(null,
                        scheduledLoanInstallment.periodNumber(), scheduledLoanInstallment.periodFromDate(),
                        scheduledLoanInstallment.periodDueDate(), scheduledLoanInstallment.principalDue(),
                        scheduledLoanInstallment.interestDue(), scheduledLoanInstallment.feeChargesDue(),
                        scheduledLoanInstallment.penaltyChargesDue(), scheduledLoanInstallment.isRecalculatedInterestComponent(),
                        scheduledLoanInstallment.getLoanCompoundingDetails());
                installments.add(installment);
            }
        }
        return installments;
    }
}
