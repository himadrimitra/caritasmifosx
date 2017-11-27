package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountDataValidator;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccount;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountCharge;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountDataAssembler;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepository;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepositoryWrapper;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsLinkages;
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
            final InvestmentAccountSavingsLinkagesRepositoryWrapper investmentAccountSavingsLinkagesRepositoryWrapper) {
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
                    InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(savingsAccount.getId(), investmentAccountId, holdTransaction.getId(), "hold for new investment with id "+investmentAccountId);
                    this.investmentSavingsTransactionRepository.save(investmentSavingsTransaction);
                    savingsLinkage.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
                    savingsLinkage.setActiveFromDate(currentDate);
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
        System.out.println("investmentAccountId "+investmentAccountId);
        System.out.println("savingLinkageAccountId "+savingLinkageAccountId);
        InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(investmentAccountId);
        final LocalDate releaseDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.dateParamName);
        InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage = this.investmentAccountSavingsLinkagesRepositoryWrapper.findOneWithNotFoundDetection(savingLinkageAccountId);
        Integer totalNumberOfDays = getNumberOfDays(investmentAccountSavingsLinkage.getActiveFromDate(),investmentAccountSavingsLinkage.getActiveToDate());
        Integer numberOfDaysForInterest = getNumberOfDays(new LocalDate(investmentAccountSavingsLinkage.getActiveFromDate()),releaseDate);
        
        BigDecimal interestEarned = MathUtility.getShare(investmentAccountSavingsLinkage.getExpectedInterestAmount(),numberOfDaysForInterest,totalNumberOfDays, investmentAccount.getCurrency());
        investmentAccountSavingsLinkage.setInterestAmount(interestEarned);
        investmentAccountSavingsLinkage.setActiveToDate(releaseDate.toDate());
        investmentAccountSavingsLinkage.setMaturityAmount(investmentAccountSavingsLinkage.getInvestmentAmount().add(interestEarned));
        investmentAccountSavingsLinkage.setStatus(InvestmentAccountStatus.CLOSED.getValue());
        return null;
    }
    
    public int getNumberOfDays(LocalDate startDate, LocalDate endDate){
        return Days.daysBetween(startDate, endDate).getDays();
    }
    
    

    @Override
    public CommandProcessingResult transferSavingLinkageAccount(Long investmentAccountId, Long savingLinkageAccountId, JsonCommand command) {
        // TODO Auto-generated method stub
        return null;
    }
}
