package com.finflux.portfolio.investmenttracker.service;

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
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.api.InvestmentAccountApiConstants;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountDataValidator;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccount;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountDataAssembler;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepository;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountRepositoryWrapper;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountSavingsLinkages;
import com.finflux.portfolio.investmenttracker.domain.InvestmentAccountStatus;
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
    
    
    @Autowired
    public InvestmentAccountWritePlatformServiceImpl(InvestmentAccountDataValidator fromApiJsonDataValidator,
            InvestmentAccountDataAssembler investmentAccountDataAssembler, InvestmentAccountRepository investmentAccountRepository,
            PlatformSecurityContext context, AccountNumberFormatRepositoryWrapper accountNumberFormatRepository,
            AccountNumberGenerator accountNumberGenerator,
            InvestmentAccountRepositoryWrapper investmentAccountRepositoryWrapper,
            SavingsAccountRepositoryWrapper savingsAccountRepository, final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService) {
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
            for(InvestmentAccountSavingsLinkages savingsLinkage : investmentAccountSavingsLinkages){
                if(savingsLinkage.getStatus().compareTo(investmentAccount.getStatus()) == 0){
                    final SavingsAccount savingsAccount = this.savingsAccountRepository.findOneWithNotFoundDetection(savingsLinkage.getSavingsAccount().getId());
                    this.fromApiJsonDataValidator.validateSavingsAccountBalanceForInvestment(savingsAccount, savingsLinkage.getInvestmentAmount());
                    savingsAccount.holdAmount(savingsLinkage.getInvestmentAmount());
                    this.savingsAccountRepository.saveAndFlush(savingsAccount);
                    savingsLinkage.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
                    savingsLinkage.setActiveFromDate(DateUtils.getLocalDateOfTenant().toDate());
                }
            }
            final Set<Long> existingTransactionIds = new HashSet<>();
            final Set<Long> existingReversedTransactionIds = new HashSet<>();
            updateExistingTransactionsDetails(investmentAccount, existingTransactionIds, existingReversedTransactionIds);
            investmentAccount.setStatus(InvestmentAccountStatus.ACTIVE.getValue());
            investmentAccount.setActivatedBy(appUser);
            Date currentDate = DateUtils.getLocalDateOfTenant().toDate();
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
        InvestmentTransaction investmentTransaction = InvestmentTransaction.deposit(investmentAccount, investmentAccount.getOffice().getId(), currentDate, investmentAccount.getInvestmentAmount(), investmentAccount.getInvestmentAmount(), currentDate, appUser.getId());
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
}
