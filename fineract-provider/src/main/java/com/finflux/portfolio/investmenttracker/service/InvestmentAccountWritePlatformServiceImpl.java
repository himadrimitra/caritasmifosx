package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import org.apache.fineract.portfolio.account.service.AccountTransfersReadPlatformService;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountCharge;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargePaidBy;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargeRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionSummaryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsHelper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.portfolio.investmenttracker.Exception.FutureDateTransactionException;
import com.finflux.portfolio.investmenttracker.Exception.InvalidDateException;
import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountSavingsLinkagesDuplicationException;
import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountSavingsLinkagesNotActiveException;
import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountDataValidator;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccount;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountCharge;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountDataAssembler;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepository;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepositoryWrapper;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsCharge;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsChargeRepositoryWrapper;
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
    private final InvestmentAccountSavingsLinkagesRepository investmentAccountSavingsLinkagesRepository;
    private final InvestmentAccountSavingsChargeRepositoryWrapper investmentAccountSavingsChargeRepositoryWrapper;
    private final ChargeRepositoryWrapper chargeRepositoryWrapper;
    private final SavingsAccountChargeRepositoryWrapper savingsAccountChargeRepositoryWrapper;
    private final SavingsAccountTransactionSummaryWrapper savingsAccountTransactionSummaryWrapper;
    private final SavingsHelper savingsHelper;
   
    
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
            final InvestmentAccountSavingsLinkagesRepository investmentAccountSavingsLinkagesRepository,
            final InvestmentAccountSavingsChargeRepositoryWrapper investmentAccountSavingsChargeRepositoryWrapper,
            final ChargeRepositoryWrapper chargeRepositoryWrapper, final SavingsAccountChargeRepositoryWrapper savingsAccountChargeRepositoryWrapper,
            final SavingsAccountTransactionSummaryWrapper savingsAccountTransactionSummaryWrapper,
            final AccountTransfersReadPlatformService accountTransfersReadPlatformService) {
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
        this.investmentAccountSavingsLinkagesRepository = investmentAccountSavingsLinkagesRepository;
        this.investmentAccountSavingsChargeRepositoryWrapper = investmentAccountSavingsChargeRepositoryWrapper;
        this.chargeRepositoryWrapper = chargeRepositoryWrapper;
        this.savingsAccountChargeRepositoryWrapper = savingsAccountChargeRepositoryWrapper;
        this.savingsAccountTransactionSummaryWrapper = savingsAccountTransactionSummaryWrapper;
        this.savingsHelper = new SavingsHelper(accountTransfersReadPlatformService);;
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
    
    @Override
    public CommandProcessingResult modifyInvestmentAccount(final Long investmentAccountId, final JsonCommand command) {
        try {
        	this.context.authenticatedUser();
            this.fromApiJsonDataValidator.validateForUpdate(command.json());
            InvestmentAccount accountForUpdate = investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
            final Map<String, Object> changes = new LinkedHashMap<>(20);
            accountForUpdate.modifyApplication(command, changes);
            this.investmentAccountDataAssembler.updateAssemble(command, accountForUpdate, changes);
            this.investmentAccountRepository.save(accountForUpdate);
            return new CommandProcessingResultBuilder().withEntityId(investmentAccountId).build();
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
            this.fromApiJsonDataValidator.validateForInvestmentAccountToApprove(investmentAccount,command.json());
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.APPROVED.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
               if(savingsLinkage.getStatus().equals(investmentAccount.getStatus())){
                    savingsLinkage.setStatus(InvestmentAccountStatus.APPROVED.getValue());
                }
            }
            investmentAccount.setStatus(InvestmentAccountStatus.APPROVED.getValue());
            investmentAccount.setApprovedBy(appUser);
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
            this.fromApiJsonDataValidator.validateForInvestmentAccountToActivate(investmentAccount,command.json());
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.ACTIVE.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            Date activationDate = investmentAccount.getActivatedOnDate().toDate();
            BigDecimal cumulativeInterestAmount = BigDecimal.ZERO;
            BigDecimal totalInterest = investmentAccount.getMaturityAmount().subtract(investmentAccount.getInvestmentAmount());
            totalInterest = Money.of(investmentAccount.getCurrency(), totalInterest).getAmount();
            updateSavingLinkageAccountOnActivate(investmentAccountId, appUser,
					investmentAccount, investmentAccountSavingsLinkages,
					activationDate, cumulativeInterestAmount, totalInterest);
            final Set<Long> existingTransactionIds = new HashSet<>();
            final Set<Long> existingReversedTransactionIds = new HashSet<>();
            updateExistingTransactionsDetails(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
            investmentAccount.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
            investmentAccount.setActivatedBy(appUser);            
            updateSavingAccountCharges(investmentAccount, investmentAccountSavingsLinkages);
            processTransactions(appUser, investmentAccount, activationDate);            
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

	private void updateSavingLinkageAccountOnActivate(
			Long investmentAccountId,
			AppUser appUser,
			InvestmentAccount investmentAccount,
			Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages,
			Date activationDate, BigDecimal cumulativeInterestAmount,
			BigDecimal totalInterest) {
		int i= 0;
		for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
		    ++i;
		    if(savingsLinkage.getStatus().equals(investmentAccount.getStatus())){
		        final SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsLinkage.getSavingsAccount().getId());
		        this.fromApiJsonDataValidator.validateSavingsAccountBalanceForInvestment(savingsAccount, savingsLinkage.getInvestmentAmount(), LocalDate.fromDateFields(activationDate));
		        final PaymentDetail paymentDetail =  null;
		        savingsAccount.holdAmount(savingsLinkage.getInvestmentAmount());
		        SavingsAccountTransaction holdTransaction = SavingsAccountTransaction.holdAmount(savingsAccount, appUser.getOffice(), paymentDetail, new LocalDate(activationDate), Money.of(savingsAccount.getCurrency(), savingsLinkage.getInvestmentAmount()), DateUtils.getLocalDateOfTenant().toDate(), appUser);
		        this.savingsAccountTransactionRepository.save(holdTransaction);
		        this.savingsAccountRepository.save(savingsAccount);
		        InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(savingsAccount.getId(), investmentAccountId, holdTransaction.getId(), getMessage(InvestmentAccountApiConstants.holdAmountMessage, investmentAccountId));
		        this.investmentSavingsTransactionRepository.save(investmentSavingsTransaction);
		        savingsLinkage.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
		        savingsLinkage.setActiveFromDate(investmentAccount.getInvestmentOnDate().toDate());
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
	}
    public void updateSavingAccountCharges(InvestmentAccount investmentAccount,Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages){
        Set<InvestmentAccountCharge> charges = investmentAccount.getInvestmentAccountCharges();
        if(charges != null && !charges.isEmpty()){
            List<InvestmentAccountSavingsCharge> investmentAccountCharges =  new ArrayList<>();
            for(InvestmentAccountCharge investmentCharge: charges){
            	if(investmentCharge.getCharge().isApplyToSavings()){
            		int i= 0;
                    BigDecimal cumulativeAmount = BigDecimal.ZERO;
                    for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
                        i++;
                        BigDecimal amount = null;
                        if(i!=investmentAccountSavingsLinkages.size()){
                            amount = MathUtility.getShare(investmentCharge.getAmount(), savingsLinkage.getInvestmentAmount(), investmentAccount.getInvestmentAmount(), investmentAccount.getCurrency());
                            cumulativeAmount = cumulativeAmount.add(amount);
                        }else{
                            amount = MathUtility.subtract(investmentCharge.getAmount(), cumulativeAmount);
                        }
                        savingsLinkage.updateExpectedCharge(amount);
                        investmentAccountCharges.add(InvestmentAccountSavingsCharge.create(investmentCharge, savingsLinkage, amount));
                        
                        
                    }
            	}
                
            }
            
            this.investmentAccountSavingsChargeRepositoryWrapper.save(investmentAccountCharges);
        }
        
    }
    

    private void updateExistingTransactionsDetails(final InvestmentAccount account, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(account.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
    }

    private void processTransactions(AppUser appUser, InvestmentAccount investmentAccount, Date date) {
        processDeposit(appUser, investmentAccount, date);
        processAccrualInterest(appUser, investmentAccount, date);
        processCharge(appUser, investmentAccount, date);
       
    }
    
    private void processCharge(AppUser appUser, InvestmentAccount investmentAccount, Date date) {
        if(investmentAccount.getInvestmentAccountCharges().size()>0){
        	BigDecimal activationCharge = BigDecimal.ZERO;
            for(InvestmentAccountCharge charge : investmentAccount.getInvestmentAccountCharges()){
                if(charge.isAcivationCharge() && charge.getCharge().isApplyToInvestment()){
                	activationCharge = activationCharge.add(charge.getAmount());
                    InvestmentTransaction investmentTransaction = InvestmentTransaction.payCharge(investmentAccount, investmentAccount.getOfficeId(), date, charge.getAmount(), investmentAccount.getInvestmentAmount(), date, appUser.getId());
                    investmentAccount.getTransactions().add(investmentTransaction);
                }
                
            } 
            investmentAccount.setMaturityAmount(investmentAccount.getMaturityAmount().subtract(activationCharge));
                      
            
        }        
    }

    private void processDeposit(AppUser appUser, InvestmentAccount investmentAccount, Date date) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.deposit(investmentAccount, investmentAccount.getOfficeId(), date, investmentAccount.getInvestmentAmount(), investmentAccount.getInvestmentAmount(), date, appUser.getId());
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
            this.fromApiJsonDataValidator.validateForInvestmentAccountToReject(investmentAccount,command.json());
            Map<String, Object> changes = new HashMap<>();
            changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.REJECTED.name());
            Set<InvestmentAccountSavingsLinkages> investmentAccountSavingsLinkages = investmentAccount.getInvestmentAccountSavingsLinkages();
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
                if(savingsLinkage.getStatus().equals(investmentAccount.getStatus())){
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
    
    private void postJournalEntries(final SavingsAccount savingsAccount, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);
        final boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }
    
    @Override
    public CommandProcessingResult releaseSavingLinkageAccount(Long investmentAccountId, Long savingLinkageAccountId, JsonCommand command) {
        
        InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(investmentAccountId);
        final LocalDate releaseDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.dateParamName);
        InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage = this.investmentAccountSavingsLinkagesRepositoryWrapper.findOneWithNotFoundDetection(savingLinkageAccountId);
        if(!investmentAccountSavingsLinkage.isActive()){
            throw new InvestmentAccountSavingsLinkagesNotActiveException();
        }
        if(releaseDate.isAfter(DateUtils.getLocalDateOfTenant())){
        	throw new FutureDateTransactionException("release");
        }
        if(releaseDate.isBefore(investmentAccount.getActivatedOnDate())){
        	throw new InvalidDateException("release", "activation");
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
        investmentAccountSavingsLinkage.setMaturityAmount(investmentAccountSavingsLinkage.getInvestmentAmount().add(interestEarned));
        investmentAccountSavingsLinkage.setStatus(InvestmentAccountStatus.CLOSED.getValue());
        processReleaseTransaction(investmentAccountSavingsLinkage, investmentAccount, releaseDate);
    }
    
    public int getNumberOfDays(LocalDate startDate, LocalDate endDate){
        return Days.daysBetween(startDate, endDate).getDays();
    }
    
    public void processReleaseTransaction(InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage,InvestmentAccount investmentAccount,
            final LocalDate releaseDate){
        AppUser user = this.context.authenticatedUser();
        Long investmentId = investmentAccount.getId();
        SavingsAccount  savingAccount = investmentAccountSavingsLinkage.getSavingsAccount();
        LocalDate date = DateUtils.getLocalDateOfTenant();
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        existingTransactionIds.addAll(savingAccount.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(savingAccount.findExistingReversedTransactionIds());
        //release hold amount
        
        final PaymentDetail paymentDetail =  null;
        List<SavingsAccountTransaction> transactions = new ArrayList<>();
        savingAccount.releaseAmount(investmentAccountSavingsLinkage.getInvestmentAmount());
        SavingsAccountTransaction releaseTransaction = SavingsAccountTransaction.releaseAmount(savingAccount, user.getOffice(), paymentDetail, releaseDate, Money.of(savingAccount.getCurrency(), investmentAccountSavingsLinkage.getInvestmentAmount()), date.toDate(), user);
        transactions.add(releaseTransaction);
        //process savings account charges
        if(MathUtility.isGreaterThanZero(investmentAccountSavingsLinkage.getExpectedChargeAmount())){
            processSavingsAccountCharge(investmentAccountSavingsLinkage, investmentAccount, savingAccount, releaseDate, transactions, releaseDate);
            
        }
        SavingsAccountTransaction depositTransaction = null;
        if(MathUtility.isGreaterThanZero(investmentAccountSavingsLinkage.getInterestAmount())){
            final boolean isManualTransaction = false;
            depositTransaction = SavingsAccountTransaction.interestPosting(savingAccount, user.getOffice(), releaseDate, Money.of(savingAccount.getCurrency(),investmentAccountSavingsLinkage.getInterestAmount()),isManualTransaction);
            transactions.add(depositTransaction);
        }
        
        //deposit (interest earned - paid charge)
        this.savingsAccountTransactionRepository.save(transactions);
        savingAccount.getTransactions().addAll(transactions);
        this.updateRunningBalance(savingAccount);
        updateInvestmentSavingTransactions(investmentId, savingAccount, transactions);
        
        postJournalEntries(savingAccount, existingTransactionIds, existingReversedTransactionIds);
    }
    
    public void updateRunningBalance(SavingsAccount savingAccount){
    	LocalDate today = DateUtils.getLocalDateOfTenant();  
    	savingAccount.setHelpers(this.savingsAccountTransactionSummaryWrapper, this.savingsHelper);
        savingAccount.recalculateDailyBalances(Money.zero(savingAccount.getCurrency()), today, savingAccount.getTransactions());
        savingAccount.getSummary().updateSummary(savingAccount.getCurrency(), this.savingsAccountTransactionSummaryWrapper, savingAccount.getTransactions());
        this.savingsAccountRepository.save(savingAccount);
    }

    private void updateInvestmentSavingTransactions(Long investmentId, SavingsAccount savingAccount,List<SavingsAccountTransaction> transactions) {
        List<InvestmentSavingsTransaction> investmentTransactions = new ArrayList<>();
        for(SavingsAccountTransaction transaction: transactions){
            String description = "";
            if(transaction.getTypeOf().equals(SavingsAccountTransactionType.AMOUNT_RELEASE.getValue())){
                description = getMessage(InvestmentAccountApiConstants.releaseAmountMessage, investmentId);
            }else if(transaction.getTypeOf().equals(SavingsAccountTransactionType.PAY_CHARGE.getValue())){
                description = getMessage(InvestmentAccountApiConstants.managementAmountMessage, investmentId);
            }else if(transaction.getTypeOf().equals(SavingsAccountTransactionType.INTEREST_POSTING.getValue())){
                description = getMessage(InvestmentAccountApiConstants.interestEarnedAmountMessage, investmentId);
            }else if(transaction.getTypeOf().equals(SavingsAccountTransactionType.AMOUNT_HOLD.getValue())){
                description = getMessage(InvestmentAccountApiConstants.holdAmountMessage, investmentId);
            }
            InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(savingAccount.getId(), 
                    investmentId, transaction.getId(), description);
            investmentTransactions.add(investmentSavingsTransaction);
        }
        this.investmentSavingsTransactionRepository.save(investmentTransactions);
    }

    private void processSavingsAccountCharge(InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage,
            InvestmentAccount investmentAccount, SavingsAccount  savingAccount, final LocalDate releaseDate, List<SavingsAccountTransaction> transactions, LocalDate date) {
        List<InvestmentAccountSavingsCharge> charges = this.investmentAccountSavingsChargeRepositoryWrapper.findBySavingLinkedAccount(investmentAccountSavingsLinkage.getId());
        List<Charge> externalCharges = this.chargeRepositoryWrapper.findByChargeTimeType(ChargeTimeType.EXTERNAL_INVESTMENT.getValue());
        Collection<SavingsAccountCharge> savingsAccountCharges = new ArrayList<>();
        //add charge to saving accounts
        BigDecimal paidTotalChargeBySavingsAccount = BigDecimal.ZERO;
        if(externalCharges != null && !externalCharges.isEmpty()){
            Integer totalNumberOfDays = getNumberOfDays(investmentAccountSavingsLinkage.getActiveFromDate(),investmentAccountSavingsLinkage.getActiveToDate());
            Integer numberOfDays = getNumberOfDays(new LocalDate(investmentAccountSavingsLinkage.getActiveFromDate()),releaseDate);
            Charge externalCharge = externalCharges.get(0);
            for(InvestmentAccountSavingsCharge charge :charges){
            	if(numberOfDays!=0){
            		BigDecimal paidAmount = MathUtility.getShare(charge.getAmount(), numberOfDays, totalNumberOfDays, investmentAccount.getCurrency());
                    paidTotalChargeBySavingsAccount = paidTotalChargeBySavingsAccount.add(paidAmount);
                    SavingsAccountCharge savingsAccountCharge =  new SavingsAccountCharge(savingAccount, externalCharge, paidAmount, date);
                    charge.setPaidAmount(paidAmount);
                    savingsAccountCharges.add(savingsAccountCharge);  
            	}
            	              
            }

            investmentAccountSavingsLinkage.setActiveToDate(releaseDate.toDate());
            investmentAccountSavingsLinkage.setChargeAmount(paidTotalChargeBySavingsAccount);
            this.savingsAccountChargeRepositoryWrapper.save(savingsAccountCharges);
        }
        // pay Charges

        AppUser user = this.context.authenticatedUser();                
        final PaymentDetail paymentDetail = null;
        for (SavingsAccountCharge savingsAccountCharge : savingsAccountCharges) {
            Money chargeAmount = savingsAccountCharge.getAmount(investmentAccount.getCurrency());
            savingsAccountCharge.pay(savingAccount.getCurrency(), chargeAmount);
            SavingsAccountTransaction chargeTransaction = SavingsAccountTransaction.charge(savingAccount, investmentAccount.getOffice() ,
                    paymentDetail, date, chargeAmount, user);
            transactions.add(chargeTransaction);            
            final SavingsAccountChargePaidBy chargePaidBy = SavingsAccountChargePaidBy.instance(chargeTransaction, savingsAccountCharge, chargeAmount.getAmount());
            chargeTransaction.getSavingsAccountChargesPaid().add(chargePaidBy);
            
        }
        
    }
    
    public static String getMessage(String message, Long investmentAccountId){
        return message+" # "+investmentAccountId;
    }

    @Override
    public CommandProcessingResult transferSavingLinkageAccount(Long investmentAccountId, Long savingLinkageAccountId, JsonCommand command) {
        InvestmentAccount investmentAccount = this.investmentAccountRepository.findOne(investmentAccountId);
        final LocalDate transferDate = command.localDateValueOfParameterNamed(InvestmentAccountApiConstants.dateParamName);
        if(transferDate.isAfter(DateUtils.getLocalDateOfTenant())){
        	throw new FutureDateTransactionException("transfer");
        }
        if(transferDate.isBefore(investmentAccount.getActivatedOnDate())){
        	throw new InvalidDateException("transfer", "activation");
        }
        InvestmentAccountSavingsLinkages investmentAccountSavingsLinkage = this.investmentAccountSavingsLinkagesRepositoryWrapper.findOneWithNotFoundDetection(savingLinkageAccountId);
        final Long savingsId = command.longValueOfParameterNamed(InvestmentAccountApiConstants.savingsAccountIdParamName);
        validateForExistingSavingLinkageAccount(savingsId, investmentAccount.getInvestmentAccountSavingsLinkages());
        processRelease(investmentAccount, transferDate, investmentAccountSavingsLinkage);
        SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsId);
  
        InvestmentAccountSavingsLinkages newInvestmentAccountSavingsLinkage = new InvestmentAccountSavingsLinkages(investmentAccountSavingsLinkage, savingsAccount, transferDate.toDate());
        investmentAccount.getInvestmentAccountSavingsLinkages().add(newInvestmentAccountSavingsLinkage);
        
        List<InvestmentAccountSavingsCharge> charges = this.investmentAccountSavingsChargeRepositoryWrapper.findBySavingLinkedAccount(investmentAccountSavingsLinkage.getId());
        List<InvestmentAccountSavingsCharge> chargesForTransferedSavingAccount = new ArrayList<>();
        for (InvestmentAccountSavingsCharge charge : charges) {
            BigDecimal amount = MathUtility.subtract(charge.getAmount(), charge.getPaidAmount());
            chargesForTransferedSavingAccount.add(InvestmentAccountSavingsCharge.create(charge.getInvestmentAccountCharge(), newInvestmentAccountSavingsLinkage, amount));
        }
        this.investmentAccountRepository.save(investmentAccount);
        this.investmentAccountSavingsChargeRepositoryWrapper.save(chargesForTransferedSavingAccount);
        
        final PaymentDetail paymentDetail =  null;
        savingsAccount.holdAmount(newInvestmentAccountSavingsLinkage.getInvestmentAmount());
        SavingsAccountTransaction holdTransaction = SavingsAccountTransaction.holdAmount(savingsAccount, this.context.authenticatedUser().getOffice(), paymentDetail, transferDate, Money.of(savingsAccount.getCurrency(), newInvestmentAccountSavingsLinkage.getInvestmentAmount()), transferDate.toDate(), this.context.authenticatedUser());
        this.savingsAccountTransactionRepository.save(holdTransaction);
        this.savingsAccountRepository.save(savingsAccount);
        InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(savingsAccount.getId(), investmentAccountId, holdTransaction.getId(), getMessage(InvestmentAccountApiConstants.holdAmountMessage, investmentAccountId));
        this.investmentSavingsTransactionRepository.save(investmentSavingsTransaction);
        return new CommandProcessingResultBuilder() //
        .withEntityId(investmentAccountId).build();
    }
    
    public void validateForExistingSavingLinkageAccount(Long savingsId, Set<InvestmentAccountSavingsLinkages> savingLinakgeAccount){
    	for (InvestmentAccountSavingsLinkages investmentAccountSavingsLinkages : savingLinakgeAccount) {
			if(savingsId.equals(investmentAccountSavingsLinkages.getSavingsAccount().getId())){
				throw new InvestmentAccountSavingsLinkagesDuplicationException();
			}
		}
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
        BigDecimal investmentAccountMaturityCharge = BigDecimal.ZERO;
        for(InvestmentAccountCharge investmentAccountCharge : investmentAccount.getInvestmentAccountCharges()){
        	if(investmentAccountCharge.isMaturityCharge() && investmentAccountCharge.getCharge().isApplyToInvestment() && MathUtility.isGreaterThanZero(investmentAccountCharge.getAmount())){
        		investmentAccountMaturityCharge = investmentAccountMaturityCharge.add(investmentAccountCharge.getAmount());
        		InvestmentTransaction maturityTransaction = InvestmentTransaction.payCharge(investmentAccount, investmentAccount.getOfficeId(), currentDate, investmentAccountCharge.getAmount() , investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
        		investmentAccount.getTransactions().add(maturityTransaction);
        	}
        }
        investmentAccount.setMaturityAmount(investmentAccount.getMaturityAmount().subtract(investmentAccountMaturityCharge));
    	BigDecimal amount = MathUtility.subtract(investmentAccount.getMaturityAmount(), investmentAccount.getInvestmentAmount());
        InvestmentTransaction investmentTransaction = InvestmentTransaction.interestPosting(investmentAccount, investmentAccount.getOfficeId(), currentDate, amount , investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
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
        investmentAccount.setExternalId(investmentAccount.getExternalId() + "_R_"+investmentAccount.getId());
        for(InvestmentAccountSavingsLinkages linkageData : investmentAccount.getInvestmentAccountSavingsLinkages()){
            if(investmentSavingsAccountLinkageIds.contains(linkageData.getId())){
                linkageData.setStatus(reinvestStatus);
            }
        }
        this.investmentAccountRepository.saveAndFlush(investmentAccount);
    }

    @Override
    public CommandProcessingResult closeInvestmentAccount(Long investmentAccountId, JsonCommand command) {
    	InvestmentAccount investmentAccount = this.investmentAccountRepositoryWrapper.findOneWithNotFoundDetection(investmentAccountId);
        this.fromApiJsonDataValidator.validateForInvestmentAccountToClose(investmentAccount,command.json());
        Map<String, Object> changes = new HashMap<>();
        changes.put(InvestmentAccountApiConstants.statusParamName, InvestmentAccountStatus.ACTIVE.name());        
        processCloseAction(investmentAccount);
        return new CommandProcessingResultBuilder() //
        .withEntityId(investmentAccount.getId()).build();
    }
    
    private void processCloseAction(InvestmentAccount investmentAccount){
        AppUser appUser = this.context.authenticatedUser();
        Integer matureStatus = InvestmentAccountStatus.MATURED.getValue();
        final LocalDate closedOnDate = investmentAccount.getCloseOnDate();
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
        processWithDrawl(appUser, investmentAccount, closedOnDate.toDate());
        this.investmentAccountRepository.save(investmentAccount);
        postJournalEntries(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
        for(InvestmentAccountSavingsLinkages savingsAccountLinkage : investmentAccount.getInvestmentAccountSavingsLinkages()){
            if(savingsAccountLinkage.getStatus().equals(matureStatus)){
                SavingsAccount  savingAccount = savingsAccountLinkage.getSavingsAccount();
                final Set<Long> savingsAccExistingTransactionIds = new HashSet<>();
                final Set<Long> savingsAccExistingReversedTransactionIds = new HashSet<>();
                savingsAccExistingTransactionIds.addAll(savingAccount.findExistingTransactionIds());
                savingsAccExistingReversedTransactionIds.addAll(savingAccount.findExistingReversedTransactionIds());
                //release hold amount
                final PaymentDetail paymentDetail =  null;
                List<SavingsAccountTransaction> transactions = new ArrayList<>();
                SavingsAccountTransaction releaseTransaction = SavingsAccountTransaction.releaseAmount(savingAccount, appUser.getOffice(), paymentDetail, closedOnDate, Money.of(savingAccount.getCurrency(), savingsAccountLinkage.getInvestmentAmount()), DateUtils.getLocalDateOfTenant().toDate(), appUser);
                transactions.add(releaseTransaction);
                savingAccount.releaseAmount(savingsAccountLinkage.getInvestmentAmount());
                
                //pay charge amount
                if(MathUtility.isGreaterThanZero(savingsAccountLinkage.getExpectedChargeAmount())){
                    processSavingsAccountCharge(savingsAccountLinkage, investmentAccount, savingAccount, savingsAccountLinkage.getActiveToDate(), transactions, closedOnDate);
                    
                }

                //Deposit Earnings
                SavingsAccountTransaction depositTransaction = null;
                if(MathUtility.isGreaterThanZero(savingsAccountLinkage.getInterestAmount())){
                    final boolean isManualTransaction = false;
                    depositTransaction = SavingsAccountTransaction.interestPosting(savingAccount, appUser.getOffice(), closedOnDate, Money.of(savingAccount.getCurrency(),savingsAccountLinkage.getInterestAmount()),isManualTransaction);
                    transactions.add(depositTransaction);
                }
                
                //deposit (interest earned)
                this.savingsAccountTransactionRepository.save(transactions);
                savingAccount.getTransactions().addAll(transactions);
                this.updateRunningBalance(savingAccount);
                updateInvestmentSavingAccountTransactions(investmentAccount,transactions);
                postJournalEntries(savingAccount, savingsAccExistingTransactionIds, savingsAccExistingReversedTransactionIds);
                savingsAccountLinkage.setStatus(InvestmentAccountStatus.CLOSED.getValue());
                savingsAccountLinkage.setMaturityAmount(savingsAccountLinkage.getExpectedMaturityAmount());
                savingsAccountLinkage.setInterestAmount(savingsAccountLinkage.getExpectedInterestAmount());
                savingsAccountLinkage.setChargeAmount(savingsAccountLinkage.getExpectedChargeAmount());
            }

        }
        investmentAccount.setStatus(InvestmentAccountStatus.CLOSED.getValue());
        this.investmentAccountRepository.saveAndFlush(investmentAccount);
    }

	private void updateInvestmentSavingAccountTransactions(
			InvestmentAccount investmentAccount,List<SavingsAccountTransaction> transactions) {
		List<InvestmentSavingsTransaction> investmentTransactions = new ArrayList<>();
		for(SavingsAccountTransaction transaction: transactions){
			String msg = "";
			if(transaction.getTypeOf().equals(SavingsAccountTransactionType.AMOUNT_RELEASE.getValue())){
				msg =  getMessage(InvestmentAccountApiConstants.releaseAmountMessage, investmentAccount.getId());
			}else if(transaction.getTypeOf().equals(SavingsAccountTransactionType.AMOUNT_HOLD.getValue())){
				msg =  getMessage(InvestmentAccountApiConstants.holdAmountMessage, investmentAccount.getId());
			}else if(transaction.getTypeOf().equals(SavingsAccountTransactionType.INTEREST_POSTING.getValue())){
				msg =  getMessage(InvestmentAccountApiConstants.interestEarnedAmountMessage, investmentAccount.getId());
			}else if(transaction.getTypeOf().equals(SavingsAccountTransactionType.PAY_CHARGE.getValue())){
				msg =  getMessage(InvestmentAccountApiConstants.managementAmountMessage, investmentAccount.getId());
			}
			InvestmentSavingsTransaction investmentSavingsTransaction = InvestmentSavingsTransaction.create(transaction.getSavingsAccount().getId(), investmentAccount.getId(), transaction.getId(),msg);
			investmentTransactions.add(investmentSavingsTransaction);
		}          
		this.investmentSavingsTransactionRepository.save(investmentTransactions);
	}
    private void processWithDrawl(AppUser appUser, InvestmentAccount investmentAccount, Date date) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.withDrawal(investmentAccount, investmentAccount.getOfficeId(), date, investmentAccount.getMaturityAmount(), investmentAccount.getInvestmentAmount(), DateUtils.getLocalDateOfTenant().toDate(), appUser.getId());
        investmentAccount.getTransactions().add(investmentTransaction);        
    }
    
    private void processAccrualInterest(AppUser appUser, InvestmentAccount investmentAccount, Date date) {
        InvestmentTransaction investmentTransaction = InvestmentTransaction.accrualInterest(investmentAccount, investmentAccount.getOfficeId(), date, MathUtility.subtract(investmentAccount.getMaturityAmount(), investmentAccount.getInvestmentAmount()), investmentAccount.getInvestmentAmount(), DateUtils.getLocalDateOfTenant().toDate(), appUser.getId());
        investmentAccount.getTransactions().add(investmentTransaction);        
    }
    
}
