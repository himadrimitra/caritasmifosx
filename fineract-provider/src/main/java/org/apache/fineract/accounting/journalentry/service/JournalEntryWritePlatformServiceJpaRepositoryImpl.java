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
package org.apache.fineract.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.closure.domain.GLClosureRepository;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccount;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.glaccount.exception.GLAccountNotFoundException;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.api.JournalEntryJsonInputParams;
import org.apache.fineract.accounting.journalentry.command.JournalEntryCommand;
import org.apache.fineract.accounting.journalentry.command.SingleDebitOrCreditEntryCommand;
import org.apache.fineract.accounting.journalentry.data.ClientTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.LoanDTO;
import org.apache.fineract.accounting.journalentry.data.SavingsDTO;
import org.apache.fineract.accounting.journalentry.data.SharesDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryDetail;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepository;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryType;
import org.apache.fineract.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryCnnotReverseException;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryInvalidException;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryInvalidException.GL_JOURNAL_ENTRY_INVALID_REASON;
import org.apache.fineract.accounting.journalentry.serialization.JournalEntryCommandFromApiJsonDeserializer;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.provisioning.domain.LoanProductProvisioningEntry;
import org.apache.fineract.accounting.provisioning.domain.ProvisioningEntry;
import org.apache.fineract.accounting.rule.domain.AccountingRule;
import org.apache.fineract.accounting.rule.domain.AccountingRuleRepository;
import org.apache.fineract.accounting.rule.exception.AccountingRuleNotFoundException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.domain.OrganisationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.office.exception.OfficeNotFoundException;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class JournalEntryWritePlatformServiceJpaRepositoryImpl implements JournalEntryWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(JournalEntryWritePlatformServiceJpaRepositoryImpl.class);

    private final GLClosureRepository glClosureRepository;
    private final GLAccountRepository glAccountRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryRepositoryWrapper journalEntryRepositoryWrapper;
    private final OfficeRepository officeRepository;
    private final AccountingProcessorForLoanFactory accountingProcessorForLoanFactory;
    private final AccountingProcessorForSavingsFactory accountingProcessorForSavingsFactory;
    private final AccountingProcessorForSharesFactory accountingProcessorForSharesFactory;
    private final AccountingProcessorHelper helper;
    private final JournalEntryCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final AccountingRuleRepository accountingRuleRepository;
    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository;
    private final PlatformSecurityContext context;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepositoryWrapper;
    private final CashBasedAccountingProcessorForClientTransactions accountingProcessorForClientTransactions;
    private final JournalEntryReadPlatformService journalEntryReadPlatformService;

    @Autowired
    public JournalEntryWritePlatformServiceJpaRepositoryImpl(final GLClosureRepository glClosureRepository,
            final JournalEntryRepository journalEntryRepository, final OfficeRepository officeRepository,
            final GLAccountRepository glAccountRepository, final JournalEntryCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final AccountingProcessorHelper accountingProcessorHelper, final AccountingRuleRepository accountingRuleRepository,
            final AccountingProcessorForLoanFactory accountingProcessorForLoanFactory,
            final AccountingProcessorForSavingsFactory accountingProcessorForSavingsFactory,
            final AccountingProcessorForSharesFactory accountingProcessorForSharesFactory,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository, final PlatformSecurityContext context,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepositoryWrapper,
            final CashBasedAccountingProcessorForClientTransactions accountingProcessorForClientTransactions,
            final JournalEntryReadPlatformService journalEntryReadPlatformService, final JournalEntryRepositoryWrapper journalEntryRepositoryWrapper) {
        this.glClosureRepository = glClosureRepository;
        this.officeRepository = officeRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryRepositoryWrapper = journalEntryRepositoryWrapper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.glAccountRepository = glAccountRepository;
        this.accountingProcessorForLoanFactory = accountingProcessorForLoanFactory;
        this.accountingProcessorForSavingsFactory = accountingProcessorForSavingsFactory;
        this.accountingProcessorForSharesFactory = accountingProcessorForSharesFactory;
        this.helper = accountingProcessorHelper;
        this.accountingRuleRepository = accountingRuleRepository;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.organisationCurrencyRepository = organisationCurrencyRepository;
        this.context = context;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.financialActivityAccountRepositoryWrapper = financialActivityAccountRepositoryWrapper;
        this.accountingProcessorForClientTransactions = accountingProcessorForClientTransactions;
        this.journalEntryReadPlatformService = journalEntryReadPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult createJournalEntry(final JsonCommand command) {
        try {
            final JournalEntryCommand journalEntryCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            journalEntryCommand.validateForCreate();

            // check office is valid
            final Long officeId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.OFFICE_ID.getValue());
            final Office office = this.officeRepository.findOne(officeId);
            if (office == null) { throw new OfficeNotFoundException(officeId); }

            final Long accountRuleId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.ACCOUNTING_RULE.getValue());
            final String currencyCode = command.stringValueOfParameterNamed(JournalEntryJsonInputParams.CURRENCY_CODE.getValue());

            validateBusinessRulesForJournalEntries(journalEntryCommand);

            /** Capture payment details **/
            final Map<String, Object> changes = new LinkedHashMap<>();
            final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

            /** Set a transaction Id and save these Journal entries **/
            final Date transactionDate = command.DateValueOfParameterNamed(JournalEntryJsonInputParams.TRANSACTION_DATE.getValue());
            final String transactionId = generateTransactionId(officeId);
            final String referenceNumber = command.stringValueOfParameterNamed(JournalEntryJsonInputParams.REFERENCE_NUMBER.getValue());
            final boolean manualEntry = true;
            String comments = journalEntryCommand.getComments();
            Integer entityType = null;
            Long entityId = null;
            Long entityTransactionId = null;
            Long paymentDetailId = null;
            if(paymentDetail != null){
                paymentDetailId = paymentDetail.getId();
            }
            /** Validate current code is appropriate **/
            this.organisationCurrencyRepository.findOneWithNotFoundDetection(currencyCode);
            JournalEntry journalEntry = JournalEntry.createNew(office.getId(),paymentDetailId , currencyCode,
                    transactionId, manualEntry, transactionDate, transactionDate, transactionDate, comments, entityType, entityId,
                    referenceNumber, entityTransactionId);

            if (accountRuleId != null) {

                final AccountingRule accountingRule = this.accountingRuleRepository.findOne(accountRuleId);
                if (accountingRule == null) { throw new AccountingRuleNotFoundException(accountRuleId); }

                if (accountingRule.getAccountToCredit() == null) {
                    if (journalEntryCommand.getCredits() == null) { throw new JournalEntryInvalidException(
                            GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }
                    if (journalEntryCommand.getDebits() != null) {
                        checkDebitOrCreditAccountsAreValid(accountingRule, journalEntryCommand.getCredits(),
                                journalEntryCommand.getDebits());
                        checkDebitAndCreditAmounts(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                    }

                    saveAllDebitOrCreditEntries(journalEntryCommand.getCredits(), JournalEntryType.CREDIT, journalEntry);
                } else {
                    final GLAccount creditAccountHead = accountingRule.getAccountToCredit();
                    validateGLAccountForTransaction(creditAccountHead);
                    validateDebitOrCreditArrayForExistingGLAccount(creditAccountHead, journalEntryCommand.getCredits());
                    saveAllDebitOrCreditEntries(journalEntryCommand.getCredits(), JournalEntryType.CREDIT, journalEntry);
                }

                if (accountingRule.getAccountToDebit() == null) {
                    if (journalEntryCommand.getDebits() == null) { throw new JournalEntryInvalidException(
                            GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }
                    if (journalEntryCommand.getCredits() != null) {
                        checkDebitOrCreditAccountsAreValid(accountingRule, journalEntryCommand.getCredits(),
                                journalEntryCommand.getDebits());
                        checkDebitAndCreditAmounts(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                    }

                    saveAllDebitOrCreditEntries(journalEntryCommand.getDebits(), JournalEntryType.DEBIT, journalEntry);
                } else {
                    final GLAccount debitAccountHead = accountingRule.getAccountToDebit();
                    validateGLAccountForTransaction(debitAccountHead);
                    validateDebitOrCreditArrayForExistingGLAccount(debitAccountHead, journalEntryCommand.getDebits());
                    saveAllDebitOrCreditEntries(journalEntryCommand.getDebits(), JournalEntryType.DEBIT, journalEntry);
                }
            } else {

                saveAllDebitOrCreditEntries(journalEntryCommand.getDebits(), JournalEntryType.DEBIT, journalEntry);

                saveAllDebitOrCreditEntries(journalEntryCommand.getCredits(), JournalEntryType.CREDIT, journalEntry);

            }
            
            if(!journalEntry.getJournalEntryDetails().isEmpty()){
                this.journalEntryRepositoryWrapper.save(journalEntry);
            }
            
            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(officeId)
                    .withTransactionId(transactionId).withEntityId(journalEntry.getId()).build();
        } catch (final DataIntegrityViolationException dve) {
            handleJournalEntryDataIntegrityIssues(dve);
            return null;
        }
    }

    private void validateDebitOrCreditArrayForExistingGLAccount(final GLAccount glaccount,
            final SingleDebitOrCreditEntryCommand[] creditOrDebits) {
        /**
         * If a glaccount is assigned for a rule the credits or debits array
         * should have only one entry and it must be same as existing account
         */
        if (creditOrDebits.length != 1) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.INVALID_DEBIT_OR_CREDIT_ACCOUNTS, null, null, null); }
        for (final SingleDebitOrCreditEntryCommand creditOrDebit : creditOrDebits) {
            if (!glaccount.getId().equals(creditOrDebit.getGlAccountId())) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.INVALID_DEBIT_OR_CREDIT_ACCOUNTS, null, null, null); }
        }
    }

    @SuppressWarnings("null")
    private void checkDebitOrCreditAccountsAreValid(final AccountingRule accountingRule, final SingleDebitOrCreditEntryCommand[] credits,
            final SingleDebitOrCreditEntryCommand[] debits) {
        // Validate the debit and credit arrays are appropriate accounts
        List<GLAccountDataForLookup> allowedCreditGLAccounts = new ArrayList<>();
        List<GLAccountDataForLookup> allowedDebitGLAccounts = new ArrayList<>();
        final SingleDebitOrCreditEntryCommand[] validCredits = new SingleDebitOrCreditEntryCommand[credits.length];
        final SingleDebitOrCreditEntryCommand[] validDebits = new SingleDebitOrCreditEntryCommand[debits.length];

        if (credits != null && credits.length > 0) {
            allowedCreditGLAccounts = this.glAccountReadPlatformService.retrieveAccountsByTagId(accountingRule.getId(),
                    JournalEntryType.CREDIT.getValue());
            for (final GLAccountDataForLookup accountDataForLookup : allowedCreditGLAccounts) {
                for (int i = 0; i < credits.length; i++) {
                    final SingleDebitOrCreditEntryCommand credit = credits[i];
                    if (credit.getGlAccountId().equals(accountDataForLookup.getId())) {
                        validCredits[i] = credit;
                    }
                }
            }
            if (credits.length != validCredits.length) { throw new RuntimeException("Invalid credits"); }
        }

        if (debits != null && debits.length > 0) {
            allowedDebitGLAccounts = this.glAccountReadPlatformService.retrieveAccountsByTagId(accountingRule.getId(),
                    JournalEntryType.DEBIT.getValue());
            for (final GLAccountDataForLookup accountDataForLookup : allowedDebitGLAccounts) {
                for (int i = 0; i < debits.length; i++) {
                    final SingleDebitOrCreditEntryCommand debit = debits[i];
                    if (debit.getGlAccountId().equals(accountDataForLookup.getId())) {
                        validDebits[i] = debit;
                    }
                }
            }
            if (debits.length != validDebits.length) { throw new RuntimeException("Invalid debits"); }
        }
    }

    private void checkDebitAndCreditAmounts(final SingleDebitOrCreditEntryCommand[] credits, final SingleDebitOrCreditEntryCommand[] debits) {
        // sum of all debits must be = sum of all credits
        BigDecimal creditsSum = BigDecimal.ZERO;
        BigDecimal debitsSum = BigDecimal.ZERO;
        for (final SingleDebitOrCreditEntryCommand creditEntryCommand : credits) {
            if (creditEntryCommand.getAmount() == null || creditEntryCommand.getGlAccountId() == null) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_ACCOUNT_OR_AMOUNT_EMPTY, null, null, null); }
            creditsSum = creditsSum.add(creditEntryCommand.getAmount());
        }
        for (final SingleDebitOrCreditEntryCommand debitEntryCommand : debits) {
            if (debitEntryCommand.getAmount() == null || debitEntryCommand.getGlAccountId() == null) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_ACCOUNT_OR_AMOUNT_EMPTY, null, null, null); }
            debitsSum = debitsSum.add(debitEntryCommand.getAmount());
        }
        if (creditsSum.compareTo(debitsSum) != 0) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_SUM_MISMATCH, null, null, null); }
    }

    private void validateGLAccountForTransaction(final GLAccount creditOrDebitAccountHead) {
        /***
         * validate that the account allows manual adjustments and is not
         * disabled
         **/
        if (creditOrDebitAccountHead.isDisabled()) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.GL_ACCOUNT_DISABLED, null,
                    creditOrDebitAccountHead.getName(), creditOrDebitAccountHead.getGlCode());
        } else if (!creditOrDebitAccountHead.isManualEntriesAllowed()) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.GL_ACCOUNT_MANUAL_ENTRIES_NOT_PERMITTED, null, creditOrDebitAccountHead.getName(),
                creditOrDebitAccountHead.getGlCode()); }
    }

    @Transactional
    @Override
    public CommandProcessingResult revertJournalEntry(final JsonCommand command) {
        // is the transaction Id valid
        final List<JournalEntry> journalEntry = this.journalEntryRepository
                .findUnReversedManualJournalEntriesByTransactionId(command.getTransactionId());
        String reversalComment = command.stringValueOfParameterNamed("comments");

        if (journalEntry == null) { throw new JournalEntriesNotFoundException(command.getTransactionId()); }
        final JournalEntry reversalTransaction = revertJournalEntry(journalEntry, reversalComment);
		return new CommandProcessingResultBuilder().withTransactionId(reversalTransaction.getTransactionIdentifier())
				.withEntityId(reversalTransaction.getId()).build();
    }

    @Override
    public JournalEntry revertJournalEntry(final List<JournalEntry> journalEntries, String reversalComment) {

        String reversalTransactionId = null;
        JournalEntry reversalJournalEntryDetail = null;
        for (JournalEntry journalEntry : journalEntries) {
            if (journalEntry.isReversalEntry()) { throw new JournalEntryCnnotReverseException(journalEntry.getId()); }
            final Long officeId = journalEntry.getOfficeId();
            reversalTransactionId = generateTransactionId(officeId);
            final boolean manualEntry = true;

            final boolean useDefaultComment = StringUtils.isBlank(reversalComment);

            validateCommentForReversal(reversalComment);
            if (useDefaultComment) {
                reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId() + " and transaction Id "
                        + journalEntry.getTransactionIdentifier();
            }

            final Date transactionDate = journalEntry.getEffectiveDate();
            final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(officeId);
            if (latestGLClosure != null) {
                if (latestGLClosure.getClosingDate().after(transactionDate) || latestGLClosure.getClosingDate().equals(transactionDate)) { throw new JournalEntryInvalidException(
                        GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
            }

            reversalJournalEntryDetail = JournalEntry.createNew(journalEntry.getOfficeId(), journalEntry.getPaymentDetailId(),
                    journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry, journalEntry.getTransactionDate(),
                    journalEntry.getValueDate(), journalEntry.getEffectiveDate(), reversalComment, journalEntry.getEntityType(),
                    journalEntry.getEntityId(), journalEntry.getReferenceNumber(), journalEntry.getEntityTransactionId());

            List<JournalEntryDetail> journalEntryDetails = journalEntry.getJournalEntryDetails();

            for (final JournalEntryDetail journalEntryDetail : journalEntryDetails) {
                JournalEntryDetail reversalJournalEntry = journalEntryDetail.reversalJournalEntry();
                reversalJournalEntryDetail.addJournalEntryDetail(reversalJournalEntry);
            }
            reversalJournalEntryDetail.setIsReversalEntry(true);
            this.journalEntryRepositoryWrapper.save(reversalJournalEntryDetail);
            journalEntry.setReversed(true);
            journalEntry.setReversalJournalEntry(reversalJournalEntryDetail);
            this.journalEntryRepositoryWrapper.save(journalEntry);
        }
        return reversalJournalEntryDetail;
    }

    @Override
    public String revertProvisioningJournalEntries(final Date reversalTransactionDate, final Long entityId, final Integer entityType) {
        final List<JournalEntry> journalEntryDetails = this.journalEntryRepository.findProvisioningJournalEntriesByEntityId(
                entityId, entityType);
        final boolean manualEntry = false;
        for (final JournalEntry journalEntry : journalEntryDetails) {
            final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(journalEntry.getOfficeId());
            if (latestGLClosure != null) {
                if (!latestGLClosure.getClosingDate().before(journalEntry.getEffectiveDate())) { throw new JournalEntryInvalidException(
                        GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
            }
            String reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId()
                    + " and transaction Id " + journalEntry.getTransactionIdentifier();
            JournalEntry reversalJournalEntryDetail = JournalEntry.createNew(journalEntry.getOfficeId(),
                    journalEntry.getPaymentDetailId(), journalEntry.getCurrencyCode(),
                    journalEntry.getTransactionIdentifier(), manualEntry, reversalTransactionDate, reversalTransactionDate,
                    reversalTransactionDate, reversalComment, journalEntry.getEntityType(), journalEntry.getEntityId(),
                    journalEntry.getReferenceNumber(), journalEntry.getEntityTransactionId());

            List<JournalEntryDetail> journalEntries = journalEntry.getJournalEntryDetails();

            for (final JournalEntryDetail journalEntryDetail : journalEntries) {
                JournalEntryDetail reversalJournalEntry = journalEntryDetail.reversalJournalEntry();
                reversalJournalEntryDetail.addJournalEntryDetail(reversalJournalEntry);
            }
            // save the reversal entry
            this.journalEntryRepositoryWrapper.save(reversalJournalEntryDetail);
            journalEntry.setReversalJournalEntry(reversalJournalEntryDetail);
            journalEntry.setReversed(true);
            // save the updated journal entry
            this.journalEntryRepositoryWrapper.save(journalEntry);
        }
        return null;

    }

    @Override
    public String createProvisioningJournalEntries(ProvisioningEntry provisioningEntry) {
        Collection<LoanProductProvisioningEntry> provisioningEntries = provisioningEntry.getLoanProductProvisioningEntries();
        Map<OfficeCurrencyKey, List<LoanProductProvisioningEntry>> officeMap = new HashMap<>();

        for (LoanProductProvisioningEntry entry : provisioningEntries) {
            final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(entry.getOffice().getId());
            if (latestGLClosure != null) {
                if (!latestGLClosure.getClosingDate().before(provisioningEntry.getCreatedDate())) { throw new JournalEntryInvalidException(
                        GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
            }
            OfficeCurrencyKey key = new OfficeCurrencyKey(entry.getOffice(), entry.getCurrencyCode());
            if (officeMap.containsKey(key)) {
                List<LoanProductProvisioningEntry> list = officeMap.get(key);
                list.add(entry);
            } else {
                List<LoanProductProvisioningEntry> list = new ArrayList<>();
                list.add(entry);
                officeMap.put(key, list);
            }
        }

        Set<OfficeCurrencyKey> officeSet = officeMap.keySet();
        Map<GLAccount, BigDecimal> liabilityMap = new HashMap<>();
        Map<GLAccount, BigDecimal> expenseMap = new HashMap<>();

        for (OfficeCurrencyKey key : officeSet) {
            liabilityMap.clear();
            expenseMap.clear();
            List<LoanProductProvisioningEntry> entries = officeMap.get(key);
            for (LoanProductProvisioningEntry entry : entries) {
                if (liabilityMap.containsKey(entry.getLiabilityAccount())) {
                    BigDecimal amount = liabilityMap.get(entry.getLiabilityAccount());
                    amount = amount.add(entry.getReservedAmount());
                    liabilityMap.put(entry.getLiabilityAccount(), amount);
                } else {
                    BigDecimal amount = BigDecimal.ZERO.add(entry.getReservedAmount());
                    liabilityMap.put(entry.getLiabilityAccount(), amount);
                }

                if (expenseMap.containsKey(entry.getExpenseAccount())) {
                    BigDecimal amount = expenseMap.get(entry.getExpenseAccount());
                    amount = amount.add(entry.getReservedAmount());
                    expenseMap.put(entry.getExpenseAccount(), amount);
                } else {
                    BigDecimal amount = BigDecimal.ZERO.add(entry.getReservedAmount());
                    expenseMap.put(entry.getExpenseAccount(), amount);
                }
            }
            createJournalEnry(provisioningEntry.getCreatedDate(), provisioningEntry.getId(), key.office, key.currency, liabilityMap,
                    expenseMap);
        }
        return "P" + provisioningEntry.getId();
    }

    private void createJournalEnry(Date transactionDate, Long entryId, Office office, String currencyCode,
            Map<GLAccount, BigDecimal> liabilityMap, Map<GLAccount, BigDecimal> expenseMap) {
        JournalEntry journalEntry = this.helper.createProvioningJournalEntry(currencyCode, transactionDate,
                transactionDate, transactionDate, office.getId(), entryId);
        Set<GLAccount> liabilityAccounts = liabilityMap.keySet();
        boolean isReversal = false;
        for (GLAccount account : liabilityAccounts) {
            this.helper.createCreditJournalEntryOrReversal(liabilityMap.get(account), isReversal, account, journalEntry);
        }
        Set<GLAccount> expenseAccounts = expenseMap.keySet();
        for (GLAccount account : expenseAccounts) {
            this.helper.createDebitJournalEntryOrReversal(expenseMap.get(account), isReversal, account, journalEntry);
        }
        this.journalEntryRepositoryWrapper.save(journalEntry);
    }

    private void validateCommentForReversal(final String reversalComment) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("GLJournalEntry");

        baseDataValidator.reset().parameter("comments").value(reversalComment).notExceedingLengthOf(500);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    @Transactional
    @Override
    public void createJournalEntriesForLoan(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean upfrontAccrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("upfrontAccrualBasedAccountingEnabled");
        final boolean periodicAccrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("periodicAccrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled || upfrontAccrualBasedAccountingEnabled || periodicAccrualBasedAccountingEnabled) {
            final LoanDTO loanDTO = this.helper.populateLoanDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    upfrontAccrualBasedAccountingEnabled, periodicAccrualBasedAccountingEnabled);
            final AccountingProcessorForLoan accountingProcessorForLoan = this.accountingProcessorForLoanFactory
                    .determineProcessor(loanDTO);
            accountingProcessorForLoan.createJournalEntriesForLoan(loanDTO);
        }
    }

    @Transactional
    @Override
    public void createJournalEntriesForSavings(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean accrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("accrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled || accrualBasedAccountingEnabled) {
            final SavingsDTO savingsDTO = this.helper.populateSavingsDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    accrualBasedAccountingEnabled);
            final AccountingProcessorForSavings accountingProcessorForSavings = this.accountingProcessorForSavingsFactory
                    .determineProcessor(savingsDTO);
            accountingProcessorForSavings.createJournalEntriesForSavings(savingsDTO);
        }

    }

    @Transactional
    @Override
    public void createJournalEntriesForShares(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean accrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("accrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled) {
            final SharesDTO sharesDTO = this.helper.populateSharesDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    accrualBasedAccountingEnabled);
            final AccountingProcessorForShares accountingProcessorForShares = this.accountingProcessorForSharesFactory
                    .determineProcessor(sharesDTO);
            accountingProcessorForShares.createJournalEntriesForShares(sharesDTO);
        }

    }

    @Override
    public void revertShareAccountJournalEntries(final ArrayList<Long> transactionIds, final Date transactionDate) {
        for (Long shareTransactionId : transactionIds) {
            String transactionId = AccountingProcessorHelper.SHARE_TRANSACTION_IDENTIFIER + shareTransactionId.longValue();
            JournalEntry journalEntry = this.journalEntryRepository.findJournalEntries(transactionId,
                    PortfolioProductType.SHARES.getValue());
            if (journalEntry == null) continue;
            final Long officeId = journalEntry.getOfficeId();
            final String reversalTransactionId = generateTransactionId(officeId);
            String reversalComment = "Reversal entry for Journal Entry with id  :" + journalEntry.getId() + " and transaction Id "
                    + journalEntry.getTransactionIdentifier();

            JournalEntry reversalJournalEntryDetail = JournalEntry.createNew(journalEntry.getOfficeId(),
                    journalEntry.getPaymentDetailId(), journalEntry.getCurrencyCode(), reversalTransactionId,
                    journalEntry.isManualEntry(), transactionDate, transactionDate, transactionDate, reversalComment,
                    journalEntry.getEntityType(), journalEntry.getEntityId(), journalEntry.getReferenceNumber(),
                    journalEntry.getEntityTransactionId());

            List<JournalEntryDetail> journalEntries = journalEntry.getJournalEntryDetails();

            for (final JournalEntryDetail journalEntryDetail : journalEntries) {
                JournalEntryDetail reversalJournalEntry = journalEntryDetail.reversalJournalEntry();
                reversalJournalEntryDetail.addJournalEntryDetail(reversalJournalEntry);
            }

            this.journalEntryRepositoryWrapper.save(reversalJournalEntryDetail);
            journalEntry.setReversalJournalEntry(reversalJournalEntryDetail);
            journalEntry.setReversed(true);
            // save the updated journal entry
            this.journalEntryRepositoryWrapper.save(journalEntry);

        }
    }

    private void validateBusinessRulesForJournalEntries(final JournalEntryCommand command) {
        /** check if date of Journal entry is valid ***/
        final LocalDate entryLocalDate = command.getTransactionDate();
        final Date transactionDate = entryLocalDate.toDateTimeAtStartOfDay().toDate();
        // shouldn't be in the future
        final Date todaysDate = new Date();
        if (transactionDate.after(todaysDate)) { throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.FUTURE_DATE,
                transactionDate, null, null); }
        // shouldn't be before an accounting closure
        final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(command.getOfficeId());
        if (latestGLClosure != null) {
            if (latestGLClosure.getClosingDate().after(transactionDate) || latestGLClosure.getClosingDate().equals(transactionDate)) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
        }

        /*** check if credits and debits are valid **/
        final SingleDebitOrCreditEntryCommand[] credits = command.getCredits();
        final SingleDebitOrCreditEntryCommand[] debits = command.getDebits();

        // atleast one debit or credit must be present
        if (credits == null || credits.length <= 0 || debits == null || debits.length <= 0) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }

        checkDebitAndCreditAmounts(credits, debits);
    }

    private void saveAllDebitOrCreditEntries(final SingleDebitOrCreditEntryCommand[] singleDebitOrCreditEntryCommands, final JournalEntryType type, JournalEntry journalEntry) {
        
        for (final SingleDebitOrCreditEntryCommand singleDebitOrCreditEntryCommand : singleDebitOrCreditEntryCommands) {
            final GLAccount glAccount = this.glAccountRepository.findOne(singleDebitOrCreditEntryCommand.getGlAccountId());
            if (glAccount == null) { throw new GLAccountNotFoundException(singleDebitOrCreditEntryCommand.getGlAccountId()); }

            validateGLAccountForTransaction(glAccount);
            final JournalEntryDetail glJournalEntry = JournalEntryDetail.createNew(glAccount, type, singleDebitOrCreditEntryCommand.getAmount());
            journalEntry.addJournalEntryDetail(glJournalEntry);
        }
    }

    /**
     * TODO: Need a better implementation with guaranteed uniqueness (but not a
     * long UUID)...maybe something tied to system clock..
     */
    private String generateTransactionId(final Long officeId) {
        final AppUser user = this.context.authenticatedUser();
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + user.getId() + officeId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
    }

    private void handleJournalEntryDataIntegrityIssues(final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.glJournalEntry.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource Journal Entry: " + realCause.getMessage());
    }

    @Transactional
    @Override
    public CommandProcessingResult defineOpeningBalance(final JsonCommand command) {
        try {
            final JournalEntryCommand journalEntryCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            journalEntryCommand.validateForCreate();

            final FinancialActivityAccount financialActivityAccountId = this.financialActivityAccountRepositoryWrapper
                    .findByFinancialActivityTypeWithNotFoundDetection(FINANCIAL_ACTIVITY.OPENING_BALANCES_TRANSFER_CONTRA.getValue());
            final Long contraId = financialActivityAccountId.getGlAccount().getId();
            if (contraId == null) { throw new GeneralPlatformDomainRuleException(
                    "error.msg.financial.activity.mapping.opening.balance.contra.account.cannot.be.null",
                    "office-opening-balances-contra-account value can not be null", "office-opening-balances-contra-account"); }

            // check office is valid
            final Long officeId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.OFFICE_ID.getValue());
            final Office office = this.officeRepository.findOne(officeId);
            if (office == null) { throw new OfficeNotFoundException(officeId); }

            validateJournalEntriesArePostedBefore(contraId, officeId);

            final String currencyCode = command.stringValueOfParameterNamed(JournalEntryJsonInputParams.CURRENCY_CODE.getValue());

            validateBusinessRulesForJournalEntries(journalEntryCommand);

            /**
             * revert old journal entries
             */
            final List<String> transactionIdsToBeReversed = this.journalEntryReadPlatformService.findNonReversedContraTansactionIds(
                    contraId, officeId);
            for (String transactionId : transactionIdsToBeReversed) {
                final List<JournalEntry> journalEntry = this.journalEntryRepository
                        .findUnReversedManualJournalEntriesByTransactionId(transactionId);
                revertJournalEntry(journalEntry, "defining opening balance");
            }

            /** Set a transaction Id and save these Journal entries **/
            final Date transactionDate = command.DateValueOfParameterNamed(JournalEntryJsonInputParams.TRANSACTION_DATE.getValue());
            final String transactionId = generateTransactionId(officeId);

            saveAllDebitOrCreditOpeningBalanceEntries(journalEntryCommand, office, currencyCode, transactionDate,
                    journalEntryCommand.getDebits(), transactionId, JournalEntryType.DEBIT, contraId);

            saveAllDebitOrCreditOpeningBalanceEntries(journalEntryCommand, office, currencyCode, transactionDate,
                    journalEntryCommand.getCredits(), transactionId, JournalEntryType.CREDIT, contraId);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(officeId)
                    .withTransactionId(transactionId).build();
        } catch (final DataIntegrityViolationException dve) {
            handleJournalEntryDataIntegrityIssues(dve);
            return null;
        }
    }

    private void saveAllDebitOrCreditOpeningBalanceEntries(final JournalEntryCommand command, final Office office,
            final String currencyCode, final Date transactionDate,
            final SingleDebitOrCreditEntryCommand[] singleDebitOrCreditEntryCommands, final String transactionId,
            final JournalEntryType type, final Long contraAccountId) {

        final boolean manualEntry = true;
        final GLAccount contraAccount = this.glAccountRepository.findOne(contraAccountId);
        if (contraAccount == null) { throw new GLAccountNotFoundException(contraAccountId); }
        if (!GLAccountType.fromInt(contraAccount.getType()).isEquityType()) { throw new GeneralPlatformDomainRuleException(
                "error.msg.configuration.opening.balance.contra.account.value.is.invalid.account.type",
                "Global configuration 'office-opening-balances-contra-account' value is not an equity type account", contraAccountId); }
        validateGLAccountForTransaction(contraAccount);
        final JournalEntryType contraType = getContraType(type);
        String comments = command.getComments();

        /** Validate current code is appropriate **/
        this.organisationCurrencyRepository.findOneWithNotFoundDetection(currencyCode);

        Long paymentDetailId = null;
        Integer entityType = null;
        Long entityId = null;
        Long entityTransactionId = null;
        String referenceNumber = null;

        JournalEntry journalEntry = JournalEntry.createNew(office.getId(), paymentDetailId, currencyCode, transactionId,
                manualEntry, transactionDate, transactionDate, transactionDate, comments, entityType, entityId, referenceNumber,
                entityTransactionId);

        for (final SingleDebitOrCreditEntryCommand singleDebitOrCreditEntryCommand : singleDebitOrCreditEntryCommands) {
            final GLAccount glAccount = this.glAccountRepository.findOne(singleDebitOrCreditEntryCommand.getGlAccountId());
            if (glAccount == null) { throw new GLAccountNotFoundException(singleDebitOrCreditEntryCommand.getGlAccountId()); }

            validateGLAccountForTransaction(glAccount);

            final JournalEntryDetail glJournalEntry = JournalEntryDetail.createNew(glAccount, type, singleDebitOrCreditEntryCommand.getAmount());
            journalEntry.addJournalEntryDetail(glJournalEntry);

            final JournalEntryDetail contraEntry = JournalEntryDetail.createNew(contraAccount, contraType, singleDebitOrCreditEntryCommand.getAmount());
            journalEntry.addJournalEntryDetail(contraEntry);

        }

        this.journalEntryRepositoryWrapper.save(journalEntry);
    }

    private JournalEntryType getContraType(final JournalEntryType type) {
        final JournalEntryType contraType;
        if (type.isCreditType()) {
            contraType = JournalEntryType.DEBIT;
        } else {
            contraType = JournalEntryType.CREDIT;
        }
        return contraType;
    }

    private void validateJournalEntriesArePostedBefore(final Long contraId, final Long officeId) {
        final List<String> transactionIds = this.journalEntryReadPlatformService.findNonContraTansactionIds(contraId, officeId);
        if (!CollectionUtils.isEmpty(transactionIds)) { throw new GeneralPlatformDomainRuleException(
                "error.msg.journalentry.defining.openingbalance.not.allowed",
                "Defining Opening balances not allowed after journal entries posted", transactionIds); }
    }

    @Override
    public void createJournalEntriesForClientTransactions(Map<String, Object> accountingBridgeData) {
        final ClientTransactionDTO clientTransactionDTO = this.helper.populateClientTransactionDtoFromMap(accountingBridgeData);
        accountingProcessorForClientTransactions.createJournalEntriesForClientTransaction(clientTransactionDTO);
    }

    private class OfficeCurrencyKey {

        Office office;
        String currency;

        OfficeCurrencyKey(Office office, String currency) {
            this.office = office;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object obj) {
            if (!obj.getClass().equals(this.getClass())) return false;
            OfficeCurrencyKey copy = (OfficeCurrencyKey) obj;
            return this.office.getId() == copy.office.getId() && this.currency.equals(copy.currency);
        }

        @Override
        public int hashCode() {
            return this.office.hashCode() + this.currency.hashCode();
        }
    }

}
