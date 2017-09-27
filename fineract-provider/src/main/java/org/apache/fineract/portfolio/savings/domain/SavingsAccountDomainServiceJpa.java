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
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.collectionsheet.domain.CollectionSheetTransactionDetails;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepository;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionDTO;
import org.apache.fineract.portfolio.savings.exception.DepositAccountTransactionNotAllowedException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavingsAccountDomainServiceJpa implements SavingsAccountDomainService {

    private final PlatformSecurityContext context;
    private final SavingsAccountRepositoryWrapper savingsAccountRepository;
    private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final HolidayRepositoryWrapper holidayRepository;
    private final WorkingDaysRepositoryWrapper workingDaysRepository;
    private final CalendarInstanceRepository calendarInstanceRepository;

    @Autowired
    public SavingsAccountDomainServiceJpa(final SavingsAccountRepositoryWrapper savingsAccountRepository,
            final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final ConfigurationDomainService configurationDomainService, final PlatformSecurityContext context,
            final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository,
            final SavingsAccountAssembler savingsAccountAssembler, final PaymentTypeRepository paymentTypeRepository,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService, final HolidayRepositoryWrapper holidayRepository,
            final WorkingDaysRepositoryWrapper workingDaysRepository, final CalendarInstanceRepository calendarInstanceRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.configurationDomainService = configurationDomainService;
        this.context = context;
        this.depositAccountOnHoldTransactionRepository = depositAccountOnHoldTransactionRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.paymentTypeRepository = paymentTypeRepository;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.holidayRepository = holidayRepository;
        this.workingDaysRepository = workingDaysRepository;
        this.calendarInstanceRepository = calendarInstanceRepository;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleWithdrawal(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final SavingsTransactionBooleanValues transactionBooleanValues) {

        AppUser user = getAppUserIfPresent();
        account.validateForAccountBlock();
        account.validateForDebitBlock();
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

        if (transactionBooleanValues.isRegularTransaction() && !account.allowWithdrawal()) { throw new DepositAccountTransactionNotAllowedException(
				"error.msg.withdraw.for.account." + account.getId() + ".not.allowed",
				"withdraw for account " + account.getId() + " not allowed", account.getId()+account.accountType); }
        final Set<Long> existingTransactionIds = new HashSet<>();
        final LocalDate postInterestOnDate = null;
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, DateUtils.getLocalDateTimeOfTenant().toDate(), user);
        final SavingsAccountTransaction withdrawal = account.withdraw(transactionDTO, transactionBooleanValues.isApplyWithdrawFee());

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, transactionBooleanValues.isInterestTransfer(), isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth, postInterestOnDate);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, transactionBooleanValues.isInterestTransfer(),
                    isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth, postInterestOnDate);
        }
        List<DepositAccountOnHoldTransaction> depositAccountOnHoldTransactions = null;
        if (account.getOnHoldFunds().compareTo(BigDecimal.ZERO) == 1) {
            depositAccountOnHoldTransactions = this.depositAccountOnHoldTransactionRepository
                    .findBySavingsAccountAndReversedFalseOrderByCreatedDateAsc(account);
        }
        
        final CalendarInstance calendarInstance = getCalendarInstance(account);
        
        account.validateAccountBalanceDoesNotBecomeNegative(withdrawal, transactionBooleanValues.isExceptionForBalanceCheck(),
                depositAccountOnHoldTransactions,calendarInstance);
        saveTransactionToGenerateTransactionId(account.getTransactions());
        this.savingsAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, transactionBooleanValues.isAccountTransfer());

        return withdrawal;
    }
    
    private CalendarInstance getCalendarInstance(final SavingsAccount account) {
        if (account.getSavingsAccountDpDetails() == null) { return null; }
        return calendarInstanceRepository.findCalendarInstaneByEntityId(account.getSavingsAccountDpDetails().getId(),
                CalendarEntityType.SAVINGS_DP_DETAILS.getValue());
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction) {
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType);
    }
    
    @Transactional
    @Override
    public SavingsAccountTransaction handleDeposit(final String savingsAccountNumber, final LocalDate transactionDate,
            final BigDecimal transactionAmount, final String paymentTypeName, final String paymentDetailAccountNumber,
            final String paymentDetailChequeNumber, final String routingCode, final String paymentDetailBankNumber,
            final String receiptNumber, final String note, final DateTimeFormatter fmt) {
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        final boolean isAccountTransfer = false;
        final boolean isRegularTransaction = true;
        final SavingsAccount savings = this.savingsAccountAssembler.assembleFromAccountNumber(savingsAccountNumber);
        PaymentDetail paymentDetail = null;
        if (paymentTypeName != null) {
            paymentDetail = PaymentDetail.instance(this.paymentTypeRepository.findByPaymentTypeName(paymentTypeName),
                    paymentDetailAccountNumber, paymentDetailChequeNumber, routingCode, receiptNumber, paymentDetailBankNumber);
            this.paymentDetailWritePlatformService.persistPaymentDetail(paymentDetail);
        }
        return handleDeposit(savings, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType);
    }

    private SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction,
            final SavingsAccountTransactionType savingsAccountTransactionType) {
        AppUser user = getAppUserIfPresent();
        account.validateForAccountBlock();
        account.validateForCreditBlock();
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

        if (isRegularTransaction && !account.allowDeposit()) { throw new DepositAccountTransactionNotAllowedException(
				"error.msg.deposit.for.account." + account.getId() + ".not.allowed",
				"deposit for account " + account.getId() + " not allowed", account.getId()+account.accountType); }

        boolean isInterestTransfer = false;
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, DateUtils.getLocalDateTimeOfTenant().toDate(), user);
        final SavingsAccountTransaction deposit = account.deposit(transactionDTO, savingsAccountTransactionType);
        final LocalDate postInterestOnDate = null;
        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth,
                    postInterestOnDate);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth, postInterestOnDate);
        }

        saveTransactionToGenerateTransactionId(account.getTransactions());

        this.savingsAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);

        return deposit;
    }

    @Override
    public SavingsAccountTransaction handleDividendPayout(final SavingsAccount account, final LocalDate transactionDate,
            final BigDecimal transactionAmount) {
        final DateTimeFormatter fmt = null;
        final PaymentDetail paymentDetail = null;
        final boolean isAccountTransfer = false;
        final boolean isRegularTransaction = true;
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DIVIDEND_PAYOUT;
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType);
    }

	private void saveTransactionToGenerateTransactionId(final List<SavingsAccountTransaction> transactions) {
		for (SavingsAccountTransaction transaction : transactions) {
			if (transaction.getId() == null) {
				this.savingsAccountTransactionRepository.save(transaction);
			}
		}
	}

    private void updateExistingTransactionsDetails(SavingsAccount account, Set<Long> existingTransactionIds,
            Set<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(account.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
    }

    private void postJournalEntries(final SavingsAccount savingsAccount, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds, boolean isAccountTransfer) {

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);

        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }

    @Transactional
    @Override
    public void postJournalEntries(final SavingsAccount account, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final boolean isAccountTransfer = false;
        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
    }
    
    @Override
    @Transactional
    public List<Long> handleDepositAndwithdrawal(final Long accountId, final List<SavingsAccountTransactionDTO> savingstransactions,
            final SavingsTransactionBooleanValues transactionBooleanValues, final boolean isSavingsInterestPostingAtCurrentPeriodEnd,
            final Integer financialYearBeginningMonth, final boolean isSavingAccountsInculdedInCollectionSheet,
            final boolean isWithDrawForSavingsIncludedInCollectionSheet,
            final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetailsList) {
    	LocalDate postAsInterestOn = null;
    	final List<DepositAccountOnHoldTransaction> depositAccountOnHoldTransactions = null;
        final SavingsAccount account = this.savingsAccountAssembler.assembleFrom(accountId);
        List<Long> savingsTreansactionIds = new ArrayList<>();
        final MathContext mc = MathContext.DECIMAL64;
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        for (SavingsAccountTransactionDTO transactionDTO : savingstransactions) {
            if (transactionDTO.getIsDeposit()) {
                if (account.depositAccountType().isSavingsDeposit()
                        && !isSavingAccountsInculdedInCollectionSheet) { throw new DepositAccountTransactionNotAllowedException(
                                "error.msg.deposit.for.account." + account.getId() + ".not.allowed.due.to.configiration",
                                "deposit for account " + account.getId() + " not allowed due to configuration",
                                account.getId() + account.accountType); }
                if (transactionBooleanValues.isRegularTransaction()
                        && !account.allowDeposit()) { throw new DepositAccountTransactionNotAllowedException(
                                "error.msg.deposit.for.account." + account.getId() + ".not.allowed",
                                "deposit for account " + account.getId() + " not allowed", account.getId() + account.accountType); }
                final SavingsAccountTransaction deposit = account.deposit(transactionDTO);

                if (account.depositAccountType().isRecurringDeposit()) {
                    RecurringDepositAccount rd = (RecurringDepositAccount) account;
                    rd.handleScheduleInstallments(deposit);
                    updateMaturityDateAndAmount(rd);
                    rd.updateOverduePayments(DateUtils.getLocalDateOfTenant());
                }
                saveTransactionToGenerateTransactionId(account.getTransactions());
                savingsTreansactionIds.add(deposit.getId());
                final Boolean transactionStatus = true;
                final String errorMessage = null;
                CollectionSheetTransactionDetails collectionSheetTransactionDetails = CollectionSheetTransactionDetails
                        .formCollectionSheetTransactionDetails(accountId, deposit.getId(), transactionStatus, errorMessage,
                                EntityType.SAVINGS.getValue());
                collectionSheetTransactionDetailsList.add(collectionSheetTransactionDetails);
            } else {
                if (isWithDrawForSavingsIncludedInCollectionSheet) {
                    if (transactionBooleanValues.isRegularTransaction()
                            && !account.allowWithdrawal()) { throw new DepositAccountTransactionNotAllowedException(
                                    "error.msg.withdraw.for.account." + account.getId() + ".not.allowed",
                                    "withdraw for account " + account.getId() + " not allowed", account.getId() + account.accountType); }

                    final SavingsAccountTransaction withdrawal = account.withdraw(transactionDTO,
                            transactionBooleanValues.isApplyWithdrawFee());
                    
                    final CalendarInstance calendarInstance = getCalendarInstance(account);
                    
                    account.validateAccountBalanceDoesNotBecomeNegative(withdrawal, transactionBooleanValues.isExceptionForBalanceCheck(),
                            depositAccountOnHoldTransactions, calendarInstance);
                    saveTransactionToGenerateTransactionId(account.getTransactions());
                    savingsTreansactionIds.add(withdrawal.getId());
                    final Boolean transactionStatus = true;
                    final String errorMessage = null;
                    CollectionSheetTransactionDetails collectionSheetTransactionDetails = CollectionSheetTransactionDetails
                            .formCollectionSheetTransactionDetails(accountId, withdrawal.getId(), transactionStatus, errorMessage,
                                    EntityType.SAVINGS.getValue());
                    collectionSheetTransactionDetailsList.add(collectionSheetTransactionDetails);
                } else {
                    throw new DepositAccountTransactionNotAllowedException(
                            "error.msg.withdraw.for.account." + account.getId() + ".not.allowed.due.to.configiration",
                            "withdraw for account " + account.getId() + " not allowed due to configuration",
                            account.getId() + account.accountType);
                }
            }
            if (account.isBeforeLastPostingPeriod(transactionDTO.getTransactionDate())) {
                final LocalDate today = DateUtils.getLocalDateOfTenant();

                account.postInterest(mc, today, transactionBooleanValues.isInterestTransfer(), isSavingsInterestPostingAtCurrentPeriodEnd,
                        financialYearBeginningMonth, postAsInterestOn);
            } else {
                final LocalDate today = DateUtils.getLocalDateOfTenant();
                account.calculateInterestUsing(mc, today, transactionBooleanValues.isInterestTransfer(),
                        isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth, postAsInterestOn);
            }
        }

        this.savingsAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, transactionBooleanValues.isAccountTransfer());

        return savingsTreansactionIds;
    }

    @Override
    public HolidayDetailDTO getHolidayDetails(final RecurringDepositAccount account) {
        final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
        final Long officeId = account.officeId();
        final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(officeId, account.getSubmittedOnDate()
                .toDate());
        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final List<WorkingDayExemptionsData> workingDayExemptions = null;
        return new HolidayDetailDTO(isHolidayEnabled, holidays, workingDays, workingDayExemptions);
    }
    
    @Override
    public void updateMaturityDateAndAmount(final RecurringDepositAccount account) {
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();
        final boolean isPreMatureClosure = false;
        final MathContext mc = MathContext.DECIMAL64;
        PeriodFrequencyType frequencyType = null;
        Integer recurringEvery = null;
        final List<CalendarInstance> calendarInstances = (List<CalendarInstance>) this.calendarInstanceRepository
                .findByEntityIdAndEntityTypeId(account.getId(), CalendarEntityType.SAVINGS.getValue());
        if (calendarInstances != null && !calendarInstances.isEmpty()) {
            final CalendarInstance calendarInstance = calendarInstances.get(0);
            final Calendar calendar = calendarInstance.getCalendar();
            CalendarFrequencyType calendarFrequencyType = CalendarUtils.getFrequency(calendar.getRecurrence());
            frequencyType = CalendarFrequencyType.from(calendarFrequencyType);
            recurringEvery = CalendarUtils.getInterval(calendar.getRecurrence());
        }
        final HolidayDetailDTO holidayDetails = getHolidayDetails(account);
        account.updateMaturityDateAndAmount(mc, isPreMatureClosure, isSavingsInterestPostingAtCurrentPeriodEnd,
                financialYearBeginningMonth, frequencyType, recurringEvery, holidayDetails);
    }
    
}