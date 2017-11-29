package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountSavingsLinkagesNotActiveException;
import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountDataValidator;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccount;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountCharge;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountDataAssembler;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepository;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepositoryWrapper;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsLinkages;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsLinkagesRepository;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsLinkagesRepositoryWrapper;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountStatus;
import com.finflux.portfolio.investmenttracker.domain.InvestmentSavingsTransaction;
import com.finflux.portfolio.investmenttracker.domain.InvestmentSavingsTransactionRepository;
import com.finflux.portfolio.investmenttracker.domain.InvestmentTransaction;

@Service
public class InvestmentAccountWritePlatformServiceImpl implements InvestmentAccountWritePlatformService {

    private final InvestmentAccountDataValidator fromApiJsonDataValidator;
    private final  InvestmentAccountDataAssembler investmentAccountDataAssembler;
    private final InvestmentAccountRepository investmentAccountRepository;
    private final PlatformSecurityContext context;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;
    private final AccountNumberGenerator accountNumberGenerator; 
    private final InvestmentAccountRepositoryWrapper investmentAccountRepositoryWrapper;
    private final SavingsAccountRepositoryWrapper savingsAccountRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final SavingsAccountTransactionRepository  savingsAccountTransactionRepository;
    private final InvestmentSavingsTransactionRepository investmentSavingsTransactionRepository;
    private final InvestmentAccountSavingsLinkagesRepositoryWrapper investmentAccountSavingsLinkagesRepositoryWrapper;
    private final InvestmentAccountReadService investmentAccountReadService;
    private final InvestmentAccountSavingsLinkagesRepository investmentAccountSavingsLinkagesRepository;
    
    
    @Autowired
    public InvestmentAccountWritePlatformServiceImpl(InvestmentAccountDataValidator fromApiJsonDataValidator,
            InvestmentAccountDataAssembler investmentAccountDataAssembler, InvestmentAccountRepository investmentAccountRepository,
            PlatformSecurityContext context, AccountNumberFormatRepositoryWrapper accountNumberFormatRepository,
            AccountNumberGenerator accountNumberGenerator,
            InvestmentAccountRepositoryWrapper investmentAccountRepositoryWrapper,
            SavingsAccountRepositoryWrapper savingsAccountRepository, final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final SavingsAccountTransactionRepository  savingsAccountTransactionRepository,
            final InvestmentSavingsTransactionRepository investmentSavingsTransactionRepository,
            final InvestmentAccountSavingsLinkagesRepositoryWrapper investmentAccountSavingsLinkagesRepositoryWrapper,
            final InvestmentAccountReadService investmentAccountReadService,
            final InvestmentAccountSavingsLinkagesRepository investmentAccountSavingsLinkagesRepository) {
        this.fromApiJsonDataValidator = fromApiJsonDataValidator;
        this.investmentAccountDataAssembler = investmentAccountDataAssembler;
        this.investmentAccountRepository = investmentAccountRepository;
        this.context = context;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.investmentAccountRepositoryWrapper = investmentAccountRepositoryWrapper;
        this.savingsAccountRepository = savingsAccountRepository;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
        this.investmentSavingsTransactionRepository = investmentSavingsTransactionRepository;
        this.investmentAccountSavingsLinkagesRepositoryWrapper = investmentAccountSavingsLinkagesRepositoryWrapper;
        this.investmentAccountReadService = investmentAccountReadService;
        this.investmentAccountSavingsLinkagesRepository = investmentAccountSavingsLinkagesRepository;
    }

    @Override
    public CommandProcessingResult createInvestmentAccount(JsonCommand command) {
        try {
            this.fromApiJsonDataValidator.validateForCreate(command.json());
            AppUser appUser = this.context.authenticatedUser();   

            final InvestmentAccount investmentAccount = this.investmentAccountDataAssembler.createAssemble(command, appUser);
            
            this.investmentAccountRepository.save(investmentAccount);
            
            if (investmentAccount.isAccountNumberRequiresAutoGeneration()) {
                AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository.findByAccountType(EntityAccountType.INVESTMENT);
                investmentAccount.updateAccountNo(accountNumberGenerator.generateInvestmentAccountNumber(investmentAccount, accountNumberFormat));
                this.investmentAccountRepository.save(investmentAccount);
            }

            return new CommandProcessingResultBuilder() //
                    .withEntityId(investmentAccount.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("account_no")) {
            final String accountNumber = command.stringValueOfParameterNamed("accountNumber");
            throw new PlatformDataIntegrityException("error.msg.investmentaccount.duplicate.accountnumber", "InvesetmentAccount  with account number `" + accountNumber
                    + "` already exists", "accountNumber", accountNumber);
        } else if (realCause.getMessage().contains("ia_externalid_UNIQUE")) {
            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.investmentaccount.duplicate.externalId", "InvesetmentAccount with externalId `" + externalId
                    + "` already exists");
        }
        throw new PlatformDataIntegrityException("error.msg.charge.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

    @Override
    public CommandProcessingResult approveInvestmentAccount(Long investmentAccountId, JsonCommand command) {
        try {
            AppUser appUser = this.context.authenticatedUser();
            InvestmentAccount investmentAccount = this.investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
            this.fromApiJsonDataValidator.validateForInvestmentAccountToApprove(investmentAccount);
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.APPROVED.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
               if(savingsLinkage.getStatus().compareTo(investmentAccount.getStatus()) == 0){
                    savingsLinkage.setStatus(InvestmentAccountStatus.APPROVED.getValue());
                }
            }
            investmentAccount.setStatus(InvestmentAccountStatus.APPROVED.getValue());
            investmentAccount.setApprovedBy(appUser);
            investmentAccount.setApprovedOnDate(DateUtils.getLocalDateOfTenant().toDate());
            this.investmentAccountRepository.save(investmentAccount);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(investmentAccount.getId()) //
                    .with(changes).build();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult activateInvestmentAccount(Long investmentAccountId, JsonCommand command) {
        try {
            AppUser appUser = this.context.authenticatedUser();
            InvestmentAccount investmentAccount = this.investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
            this.fromApiJsonDataValidator.validateForInvestmentAccountToActivate(investmentAccount);
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.ACTIVE.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            Date currentDate = DateUtils.getLocalDateOfTenant().toDate();
            BigDecimal cumulativeInterestAmount = BigDecimal.ZERO;
            BigDecimal totalInterest = investmentAccount.getMaturityAmount().subtract(investmentAccount.getInvestmentAmount());
            int i= 0;
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
                ++i;
                if(savingsLinkage.getStatus().compareTo(investmentAccount.getStatus()) == 0){
                    final SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsLinkage.getSavingsAccount().getId());
                    this.fromApiJsonDataValidator.validateSavingsAccountBalanceForInvestment(savingsAccount, savingsLinkage.getInvestmentAmount());
                    final PaymentDetail paymentDetail =  null;
                    SavingsAccountTransaction holdTransaction = SavingsAccountTransaction.holdAmount(savingsAccount, appUser.getOffice(), paymentDetail, DateUtils.getLocalDateOfTenant(), Money.of(savingsAccount.getCurrency(), savingsLinkage.getInvestmentAmount()), currentDate, appUser);
                    this.savingsAccountTransactionRepository.save(holdTransaction);
                    InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(savingsAccount.getId(), investmentAccountId, holdTransaction.getId(), getMessage(InvestmentAccountApiConstants.holdAmountMessage, investmentAccountId));
                    this.investmentSavingsTransactionRepository.save(investmentSavingsTransaction);
                    savingsLinkage.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
                    savingsLinkage.setActiveFromDate(currentDate);
                    savingsLinkage.setActiveToDate(investmentAccount.getMaturityOnDate());
                    BigDecimal expectedInterestAmount = null;
                    if(i!=investmentAccountSavingsLinkages.size()){
                        expectedInterestAmount = MathUtility.getShare(totalInterest, savingsLinkage.getInvestmentAmount(), investmentAccount.getInvestmentAmount(), investmentAccount.getCurrency());
                        cumulativeInterestAmount = cumulativeInterestAmount.add(expectedInterestAmount);
                    }else{
                        expectedInterestAmount = totalInterest.subtract(cumulativeInterestAmount);
                    }
                    savingsLinkage.setExpectedInterestAmount(expectedInterestAmount);
                    savingsLinkage.setExpectedMaturityAmount(MathUtility.add(savingsLinkage.getInvestmentAmount().add(expectedInterestAmount)));
                    
                }
            }
            final Set<Long> existingTransactionIds = new HashSet<>();
            final Set<Long> existingReversedTransactionIds = new HashSet<>();
            updateExistingTransactionsDetails(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
            investmentAccount.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
            investmentAccount.setActivatedBy(appUser);
            
            investmentAccount.setActivatedOnDate(currentDate);
            processTransactions(appUser, investmentAccount, currentDate);            
            this.investmentAccountRepository.save(investmentAccount);
            postJournalEntries(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(investmentAccount.getId()) //
                    .with(changes).build();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }
    
    private void updateExistingTransactionsDetails(final InvestmentAccount account, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(account.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
    }

    private void processTransactions(AppUser appUser, InvestmentAccount investmentAccount, Date currentDate) {
        processDeposit(appUser, investmentAccount, currentDate);
        processInterest(appUser, investmentAccount, currentDate);
        processCharge(appUser, investmentAccount, currentDate);
       
    }
    
    private void processCharge(AppUser appUser, InvestmentAccount investmentAccount, Date currentDate) {
        if(investmentAccount.getInvestmentAccountCharges().size()>0){
            for(InvestmentAccountCharge charge : investmentAccount.getInvestmentAccountCharges()){
                InvestmentTransaction investmentTransaction = InvestmentTransaction.payCharge(investmentAccount, investmentAccount.getOfficeId(), currentDate, charge.getAmount(), investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
                investmentAccount.getTransactions().add(investmentTransaction);
            }
        }        
    }
    
    private void processInterest(AppUser appUser, InvestmentAccount investmentAccount, Date currentDate) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.interestPosting(investmentAccount, investmentAccount.getOfficeId(), currentDate, MathUtility.subtract(investmentAccount.getMaturityAmount(), investmentAccount.getInvestmentAmount()), investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
        investmentAccount.getTransactions().add(investmentTransaction);        
    }

    private void processDeposit(AppUser appUser, InvestmentAccount investmentAccount, Date currentDate) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.deposit(investmentAccount, investmentAccount.getOfficeId(), currentDate, investmentAccount.getInvestmentAmount(), investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
        investmentAccount.getTransactions().add(investmentTransaction);
    }
    
    private void postJournalEntries(final InvestmentAccount investmentAccount, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {
        final MonetaryCurrency currency = investmentAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);
        final Map<String, Object> accountingBridgeData = investmentAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds);
        this.journalEntryWritePlatformService.createJournalEntriesForInvestment(accountingBridgeData);
    }

    @Override
    public CommandProcessingResult rejectInvestmentAccount(Long investmentAccountId, JsonCommand command) {
        try {
            AppUser appUser = this.context.authenticatedUser();
            InvestmentAccount investmentAccount = this.investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
            this.fromApiJsonDataValidator.validateForInvestmentAccountToReject(investmentAccount);
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.REJECTED.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
                if(savingsLinkage.getStatus().compareTo(investmentAccount.getStatus()) == 0){
                    savingsLinkage.setStatus(InvestmentAccountStatus.REJECTED.getValue());
                }
            }
            investmentAccount.setStatus(InvestmentAccountStatus.REJECTED.getValue());
            investmentAccount.setRejectBy(appUser);
            investmentAccount.setRejectOnDate(DateUtils.getLocalDateOfTenant().toDate());
            this.investmentAccountRepository.save(investmentAccount);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(investmentAccount.getId()) //
                    .with(changes).build();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult undoInvestmentAccountApproval(Long investmentAccountId, JsonCommand command) {
        try {
            InvestmentAccount investmentAccount = this.investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
            this.fromApiJsonDataValidator.validateForInvestmentAccountToUndoApproval(investmentAccount);
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.PENDING_APPROVAL.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
                if(savingsLinkage.getStatus().compareTo(investmentAccount.getStatus()) == 0){
                    savingsLinkage.setStatus(InvestmentAccountStatus.PENDING_APPROVAL.getValue());
                }
            }
            investmentAccount.setStatus(InvestmentAccountStatus.PENDING_APPROVAL.getValue());
            investmentAccount.setApprovedBy(null);
            investmentAccount.setApprovedOnDate(null);
            this.investmentAccountRepository.save(investmentAccount);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(investmentAccount.getId()) //
                    .with(changes).build();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult releaseSavingLinkageAccount(Long investmentAccountId, Long savingLinkageAccountId, JsonCommand command) {
        
        InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(investmentAccountId);
        final LocalDate releaseDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.dateParamName);
        InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage = this.investmentAccountSavingsLinkagesRepositoryWrapper.findOneWithNotFoundDetection(savingLinkageAccountId);
        if(!investmentAccountSavingsLinkage.isActive()){
            throw new InvestmentAccountSavingsLinkagesNotActiveException();
        }
        processRelease(investmentAccount, releaseDate, investmentAccountSavingsLinkage);
        this.investmentAccountSavingsLinkagesRepositoryWrapper.save(investmentAccountSavingsLinkage);
        return new CommandProcessingResultBuilder() //
        .withEntityId(investmentAccountSavingsLinkage.getId()).build();
    }

    private void processRelease(InvestmentAccount investmentAccount, final LocalDate releaseDate,
            InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage) {
        
        Integer totalNumberOfDays = getNumberOfDays(investmentAccountSavingsLinkage.getActiveFromDate(),investmentAccountSavingsLinkage.getActiveToDate());
        Integer numberOfDaysForInterest = getNumberOfDays(new LocalDate(investmentAccountSavingsLinkage.getActiveFromDate()),releaseDate);
        
        BigDecimal interestEarned = MathUtility.getShare(investmentAccountSavingsLinkage.getExpectedInterestAmount(),numberOfDaysForInterest,totalNumberOfDays, investmentAccount.getCurrency());
        investmentAccountSavingsLinkage.setInterestAmount(interestEarned);
        investmentAccountSavingsLinkage.setActiveToDate(releaseDate.toDate());
        investmentAccountSavingsLinkage.setMaturityAmount(investmentAccountSavingsLinkage.getInvestmentAmount().add(interestEarned));
        investmentAccountSavingsLinkage.setStatus(InvestmentAccountStatus.CLOSED.getValue());
        processReleaseTransaction(investmentAccountSavingsLinkage);
    }
    
    public int getNumberOfDays(LocalDate startDate, LocalDate endDate){
        return Days.daysBetween(startDate, endDate).getDays();
    }
    
    public void processReleaseTransaction(InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage){
        AppUser user = this.context.authenticatedUser();
        Long investmentId = investmentAccountSavingsLinkage.getInvestmentAccount().getId();
        SavingsAccount  savingAccount = investmentAccountSavingsLinkage.getSavingsAccount();
        LocalDate date = DateUtils.getLocalDateOfTenant();
        
        //release hold amount
        final PaymentDetail paymentDetail =  null;
        List<SavingsAccountTransaction> transactions = new ArrayList<>();
        SavingsAccountTransaction releaseTransaction = SavingsAccountTransaction.releaseAmount(savingAccount, user.getOffice(), paymentDetail, date, Money.of(savingAccount.getCurrency(), investmentAccountSavingsLinkage.getInvestmentAmount()), date.toDate(), user);
        transactions.add(releaseTransaction);
        //pay charge amount
        if(MathUtility.isGreaterThanZero(investmentAccountSavingsLinkage.getChargeAmount())){
            
        }
        SavingsAccountTransaction depositTransaction = null;
        if(MathUtility.isGreaterThanZero(investmentAccountSavingsLinkage.getInterestAmount())){
            final boolean isManualTransaction = false;
            depositTransaction = SavingsAccountTransaction.interestPosting(savingAccount, user.getOffice(), date, Money.of(savingAccount.getCurrency(),investmentAccountSavingsLinkage.getInterestAmount()),isManualTransaction);
            transactions.add(depositTransaction);
        }
        
        //deposit (interest earned - paid charge)
        this.savingsAccountTransactionRepository.save(transactions);
        
        List<InvestmentSavingsTransaction> investmentTransactions = new ArrayList<>();
        InvestmentSavingsTransaction investmentReleaseSavingsTransaction = InvestmentSavingsTransaction.create(savingAccount.getId(), investmentId, releaseTransaction.getId(), getMessage(InvestmentAccountApiConstants.releaseAmountMessage, investmentId));
        investmentTransactions.add(investmentReleaseSavingsTransaction);
        if(depositTransaction != null){
            InvestmentSavingsTransaction investmentDepositSavingsTransaction = InvestmentSavingsTransaction.create(savingAccount.getId(), investmentId, depositTransaction.getId(), getMessage(InvestmentAccountApiConstants.interestEarnedAmountMessage, investmentId));
            investmentTransactions.add(investmentDepositSavingsTransaction);
        }
        
        this.investmentSavingsTransactionRepository.save(investmentTransactions);
    }
    
    public static String getMessage(String message, Long investmentAccountId){
        return message+" # "+investmentAccountId;
    }

    @Override
    public CommandProcessingResult transferSavingLinkageAccount(Long investmentAccountId, Long savingLinkageAccountId, JsonCommand command) {
        InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(investmentAccountId);
        final LocalDate transferDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.dateParamName);
        InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage = this.investmentAccountSavingsLinkagesRepositoryWrapper.findOneWithNotFoundDetection(savingLinkageAccountId);
        processRelease(investmentAccount, transferDate, investmentAccountSavingsLinkage);
        final Long savingsId = command.longValueOfParameterNamed(InvestmentAccountApiConstants.savingsAccountIdParamName);
        SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsId);
        
        InvestmentAccountSavingsLinkages newInvestmentAccountSavingsLinkage = new InvestmentAccountSavingsLinkages(investmentAccountSavingsLinkage, savingsAccount, transferDate.toDate());
        investmentAccount.getInvestmentAccountSavingsLinkages().add(newInvestmentAccountSavingsLinkage);
        this.investmentAccountRepository.save(investmentAccount);
        final PaymentDetail paymentDetail =  null;
        SavingsAccountTransaction holdTransaction = SavingsAccountTransaction.holdAmount(savingsAccount, this.context.authenticatedUser().getOffice(), paymentDetail, transferDate, Money.of(savingsAccount.getCurrency(), newInvestmentAccountSavingsLinkage.getInvestmentAmount()), transferDate.toDate(), this.context.authenticatedUser());
        this.savingsAccountTransactionRepository.save(holdTransaction);
        InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(savingsAccount.getId(), investmentAccountId, holdTransaction.getId(), getMessage(InvestmentAccountApiConstants.holdAmountMessage, investmentAccountId));
        this.investmentSavingsTransactionRepository.save(investmentSavingsTransaction);
        return new CommandProcessingResultBuilder() //
        .withEntityId(investmentAccountId).build();
    }
    
    @Override
    @Transactional
    @CronTarget(jobName = JobName.MATURE_INVESTMENT_ACCOUNTS)
    public void matureInvestmentAccounts() {
        
        Integer activeStatus = InvestmentAccountStatus.ACTIVE.getValue();
        Date currentDate = DateUtils.getLocalDateOfTenant().toDate();
        Collection<InvestmentAccount> readyToMatureAccounts = this.investmentAccountRepository.findByStatusAndMaturityOnDate(activeStatus, currentDate);
        for(InvestmentAccount investmentAccount : readyToMatureAccounts){
            processMaturityOperation(investmentAccount, currentDate);
        }
    }
    
    private void processMaturityOperation(InvestmentAccount investmentAccount, Date currentDate){
        AppUser appUser = this.context.getAuthenticatedUserIfPresent();
        Set<InvestmentAccountSavingsLinkages> investmentSavingsAccountLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
        for(InvestmentAccountSavingsLinkages savingsAccountLinkage : investmentSavingsAccountLinkages){
            if(savingsAccountLinkage.getStatus().compareTo(InvestmentAccountStatus.ACTIVE.getValue()) == 0){
                savingsAccountLinkage.setStatus(InvestmentAccountStatus.MATURED.getValue());
                savingsAccountLinkage.setMaturityAmount(savingsAccountLinkage.getExpectedMaturityAmount());
                savingsAccountLinkage.setInterestAmount(savingsAccountLinkage.getExpectedInterestAmount());
            }
        }
        investmentAccount.setStatus(InvestmentAccountStatus.MATURED.getValue());
        investmentAccount.setMaturityBy(appUser);
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
        processMaturityTransaction(appUser, investmentAccount, currentDate);            
        this.investmentAccountRepository.save(investmentAccount);
        postJournalEntries(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
    }
    
    private void processMaturityTransaction(AppUser appUser,InvestmentAccount investmentAccount, Date currentDate){
        processPostInterest(appUser, investmentAccount, currentDate);
    }
    
    private void processPostInterest(AppUser appUser, InvestmentAccount investmentAccount, Date currentDate) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.interestPosting(investmentAccount, investmentAccount.getOfficeId(), currentDate, MathUtility.subtract(investmentAccount.getMaturityAmount(), investmentAccount.getInvestmentAmount()), investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
        investmentAccount.getTransactions().add(investmentTransaction);        
    }

    @Override
    public CommandProcessingResult reinvestInvestmentAccount(Long investmentAccountId, JsonCommand command) {
        try {
            //Before doing InvestmentAccountClose Operation get the InvestmentSavingsAccountLinkages Ids whose status is Mature 
            Integer matureStatus = InvestmentAccountStatus.MATURED.getValue();
            Collection<Long> linkageAccountIdsInMatureStatus = this.investmentAccountSavingsLinkagesRepository.findIdsByInvestmentAccountIdAndStatus(investmentAccountId, matureStatus);
            
            InvestmentAccount investmentAccount = this.investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
            //Closing Investment Account
            processCloseAction(investmentAccount);
            
            //Change Investment Account to Reinvest
            changeStatusToReinvest(investmentAccount, linkageAccountIdsInMatureStatus);
            
            //New Investment Account Creation
            this.fromApiJsonDataValidator.validateForCreate(command.json());
            AppUser appUser = this.context.authenticatedUser();   

            final InvestmentAccount newInvestmentAccount = this.investmentAccountDataAssembler.createAssemble(command, appUser);
            
            this.investmentAccountRepository.save(newInvestmentAccount);
            
            if (newInvestmentAccount.isAccountNumberRequiresAutoGeneration()) {
                AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository.findByAccountType(EntityAccountType.INVESTMENT);
                newInvestmentAccount.updateAccountNo(accountNumberGenerator.generateInvestmentAccountNumber(newInvestmentAccount, accountNumberFormat));
                this.investmentAccountRepository.save(newInvestmentAccount);
            }

            return new CommandProcessingResultBuilder() //
                    .withEntityId(newInvestmentAccount.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException e) {
            handleDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }
    
    private void changeStatusToReinvest(final InvestmentAccount investmentAccount, final Collection<Long> investmentSavingsAccountLinkageIds){
        
        Integer reinvestStatus = InvestmentAccountStatus.REINVESTED.getValue();
        investmentAccount.setStatus(reinvestStatus);
        investmentAccount.setExternalId(investmentAccount.getExternalId() + "-R");
        for(InvestmentAccountSavingsLinkages linkageData : investmentAccount.getInvestmentAccountSavingsLinkages()){
            if(investmentSavingsAccountLinkageIds.contains(linkageData.getId())){
                linkageData.setStatus(reinvestStatus);
            }
        }
        this.investmentAccountRepository.save(investmentAccount);
    }

    @Override
    public CommandProcessingResult closeInvestmentAccount(Long investmentAccountId, JsonCommand command) {
        InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(investmentAccountId);
        processCloseAction(investmentAccount);
        return new CommandProcessingResultBuilder() //
        .withEntityId(investmentAccount.getId()).build();
    }
    
    private void processCloseAction(InvestmentAccount investmentAccount){
        AppUser appUser = this.context.authenticatedUser();
        Integer matureStatus = InvestmentAccountStatus.MATURED.getValue();
        final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
        processWithDrawl(appUser, investmentAccount, currentDate.toDate());
        this.investmentAccountRepository.save(investmentAccount);
        postJournalEntries(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
        for(InvestmentAccountSavingsLinkages savingsAccountLinkage : investmentAccount.getInvestmentAccountSavingsLinkages()){
            if(savingsAccountLinkage.getStatus().compareTo(matureStatus) == 0 ){
                SavingsAccount  savingAccount = savingsAccountLinkage.getSavingsAccount();
                LocalDate date = DateUtils.getLocalDateOfTenant();
                
                //release hold amount
                final PaymentDetail paymentDetail =  null;
                List<SavingsAccountTransaction> transactions = new ArrayList<>();
                SavingsAccountTransaction releaseTransaction = SavingsAccountTransaction.releaseAmount(savingAccount, appUser.getOffice(), paymentDetail, date, Money.of(savingAccount.getCurrency(), savingsAccountLinkage.getInvestmentAmount()), date.toDate(), appUser);
                transactions.add(releaseTransaction);

                //Deposit Earnings
                SavingsAccountTransaction depositTransaction = null;
                if(MathUtility.isGreaterThanZero(savingsAccountLinkage.getInterestAmount())){
                    final boolean isManualTransaction = false;
                    depositTransaction = SavingsAccountTransaction.interestPosting(savingAccount, appUser.getOffice(), date, Money.of(savingAccount.getCurrency(),savingsAccountLinkage.getInterestAmount()),isManualTransaction);
                    transactions.add(depositTransaction);
                }
                
                //deposit (interest earned)
                this.savingsAccountTransactionRepository.save(transactions);
                
                List<InvestmentSavingsTransaction> investmentTransactions = new ArrayList<>();
                InvestmentSavingsTransaction investmentReleaseSavingsTransaction = InvestmentSavingsTransaction.create(savingAccount.getId(), investmentAccount.getId(), releaseTransaction.getId(), getMessage(InvestmentAccountApiConstants.releaseAmountMessage, investmentAccount.getId()));
                investmentTransactions.add(investmentReleaseSavingsTransaction);
                if(depositTransaction != null){
                    InvestmentSavingsTransaction investmentDepositSavingsTransaction = InvestmentSavingsTransaction.create(savingAccount.getId(), investmentAccount.getId(), depositTransaction.getId(), getMessage(InvestmentAccountApiConstants.interestEarnedAmountMessage, investmentAccount.getId()));
                    investmentTransactions.add(investmentDepositSavingsTransaction);
                }           
                this.investmentSavingsTransactionRepository.save(investmentTransactions);
            }

        }
    }
    private void processWithDrawl(AppUser appUser, InvestmentAccount investmentAccount, Date currentDate) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.withDrawal(investmentAccount, investmentAccount.getOfficeId(), currentDate, investmentAccount.getMaturityAmount(), investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
        investmentAccount.getTransactions().add(investmentTransaction);        
    }
    
}
