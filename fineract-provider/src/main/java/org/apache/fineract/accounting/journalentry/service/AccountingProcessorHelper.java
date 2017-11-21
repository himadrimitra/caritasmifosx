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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.closure.domain.GLClosureRepository;
import org.apache.fineract.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_INVESTMENT;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SAVINGS;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SHARES;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccount;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountPaymentTypeMapping;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountPaymentTypeRepositoryWrapper;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.ClientChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.ClientTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.InvestmentDTO;
import org.apache.fineract.accounting.journalentry.data.InvestmentTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.LoanDTO;
import org.apache.fineract.accounting.journalentry.data.LoanTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.SavingsDTO;
import org.apache.fineract.accounting.journalentry.data.SavingsTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.SharesDTO;
import org.apache.fineract.accounting.journalentry.data.SharesTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.TaxPaymentDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryDetail;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryType;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryInvalidException;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryInvalidException.GL_JOURNAL_ENTRY_INVALID_REASON;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMapping;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMappingRepository;
import org.apache.fineract.accounting.producttoaccountmapping.exception.ProductToGLAccountMappingNotFoundException;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.service.AccountTransfersReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountTransactionEnumData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionEnumData;

@Service
public class AccountingProcessorHelper {

    public static final String LOAN_TRANSACTION_IDENTIFIER = "L";
    public static final String SAVINGS_TRANSACTION_IDENTIFIER = "S";
    public static final String INVESTMENT_TRANSACTION_IDENTIFIER = "I";
    public static final String CLIENT_TRANSACTION_IDENTIFIER = "C";
    public static final String PROVISIONING_TRANSACTION_IDENTIFIER = "P";
    public static final String SHARE_TRANSACTION_IDENTIFIER = "SH";
    private final ProductToGLAccountMappingRepository accountMappingRepository;
    private final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository;
    private final GLClosureRepository closureRepository;
    private final GLAccountRepositoryWrapper accountRepositoryWrapper;
    private final OfficeRepository officeRepository;
    private final AccountTransfersReadPlatformService accountTransfersReadPlatformService;
    private final FinancialActivityAccountPaymentTypeRepositoryWrapper financialActivityAccountPaymentTypeRepository;
    private final PaymentTypeRepositoryWrapper paymentTypeRepository;

    @Autowired
    public AccountingProcessorHelper(final ProductToGLAccountMappingRepository accountMappingRepository,
            final GLClosureRepository closureRepository, final OfficeRepository officeRepository,
            final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository,
            final AccountTransfersReadPlatformService accountTransfersReadPlatformService,
            final GLAccountRepositoryWrapper accountRepositoryWrapper,
            final FinancialActivityAccountPaymentTypeRepositoryWrapper financialActivityAccountPaymentTypeRepository,
            final PaymentTypeRepositoryWrapper paymentTypeRepository) {
        this.accountMappingRepository = accountMappingRepository;
        this.closureRepository = closureRepository;
        this.officeRepository = officeRepository;
        this.financialActivityAccountRepository = financialActivityAccountRepository;
        this.accountTransfersReadPlatformService = accountTransfersReadPlatformService;
        this.accountRepositoryWrapper = accountRepositoryWrapper;
        this.financialActivityAccountPaymentTypeRepository = financialActivityAccountPaymentTypeRepository;
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public LoanDTO populateLoanDtoFromMap(final Map<String, Object> accountingBridgeData, final boolean cashBasedAccountingEnabled,
            final boolean upfrontAccrualBasedAccountingEnabled, final boolean periodicAccrualBasedAccountingEnabled) {
        final Long loanId = (Long) accountingBridgeData.get("loanId");
        final Long loanProductId = (Long) accountingBridgeData.get("loanProductId");
        final Long officeId = (Long) accountingBridgeData.get("officeId");
        final CurrencyData currencyData = (CurrencyData) accountingBridgeData.get("currency");
        final List<LoanTransactionDTO> newLoanTransactions = new ArrayList<>();
        boolean isAccountTransfer = (Boolean) accountingBridgeData.get("isAccountTransfer");
        final Long writeOffReasonId = (Long) accountingBridgeData.get("writeOffReasonId");
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> newTransactionsMap = (List<Map<String, Object>>) accountingBridgeData.get("newLoanTransactions");

        for (final Map<String, Object> map : newTransactionsMap) {
            final Long transactionOfficeId = (Long) map.get("officeId");
            final String transactionId = ((Long) map.get("id")).toString();
            final Date transactionDate = ((LocalDate) map.get("date")).toDate();
            final LoanTransactionEnumData transactionType = (LoanTransactionEnumData) map.get("type");
            final LoanTransactionEnumData transactionSubType = (LoanTransactionEnumData) map.get("subType");
            final BigDecimal amount = (BigDecimal) map.get("amount");
            final BigDecimal principal = (BigDecimal) map.get("principalPortion");
            final BigDecimal interest = (BigDecimal) map.get("interestPortion");
            final BigDecimal fees = (BigDecimal) map.get("feeChargesPortion");
            final BigDecimal penalties = (BigDecimal) map.get("penaltyChargesPortion");
            final BigDecimal overPayments = (BigDecimal) map.get("overPaymentPortion");
            final boolean reversed = (Boolean) map.get("reversed");
            final Long paymentTypeId = (Long) map.get("paymentTypeId");

            final List<ChargePaymentDTO> feePaymentDetails = new ArrayList<>();
            final List<ChargePaymentDTO> penaltyPaymentDetails = new ArrayList<>();
            final List<TaxPaymentDTO> taxPayments = new ArrayList<>();
            // extract charge payment details (if exists)
            if (map.containsKey("loanChargesPaid")) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> loanChargesPaidData = (List<Map<String, Object>>) map.get("loanChargesPaid");
                for (final Map<String, Object> loanChargePaid : loanChargesPaidData) {
                    final Long chargeId = (Long) loanChargePaid.get("chargeId");
                    final Long loanChargeId = (Long) loanChargePaid.get("loanChargeId");
                    final boolean isPenalty = (Boolean) loanChargePaid.get("isPenalty");
                    final BigDecimal chargeAmountPaid = (BigDecimal) loanChargePaid.get("amount");
                    final boolean isCapitalized = (Boolean) loanChargePaid.get("isCapitalized");
                    final ChargePaymentDTO chargePaymentDTO = new ChargePaymentDTO(chargeId, loanChargeId, chargeAmountPaid, isCapitalized);
                    if (isPenalty) {
                        penaltyPaymentDetails.add(chargePaymentDTO);
                    } else {
                        feePaymentDetails.add(chargePaymentDTO);
                    }
                    if (loanChargePaid.containsKey("taxDetails")) {
                        @SuppressWarnings("unchecked")
                        final List<Map<String, Object>> taxDatas = (List<Map<String, Object>>) loanChargePaid.get("taxDetails");
                        for (final Map<String, Object> taxData : taxDatas) {
                            final BigDecimal taxAmount = (BigDecimal) taxData.get("amount");
                            final Long creditAccountId = (Long) taxData.get("creditAccountId");
                            Long debitAccountId = null;
                            if (taxData.get("debitAccountId") != null) {
                                debitAccountId = (Long) taxData.get("debitAccountId");
                            }
                            chargePaymentDTO
                                    .updateTaxPaymentDTO(new TaxPaymentDTO(debitAccountId, creditAccountId, taxAmount, loanChargeId));
                        }

                    }
                }
            }
            if (!isAccountTransfer) {
                isAccountTransfer = this.accountTransfersReadPlatformService.isAccountTransfer(Long.parseLong(transactionId),
                        PortfolioAccountType.LOAN);
            }
            final LoanTransactionDTO transaction = new LoanTransactionDTO(transactionOfficeId, paymentTypeId, transactionId,
                    transactionDate, transactionType, amount, principal, interest, fees, penalties, overPayments, reversed,
                    feePaymentDetails, penaltyPaymentDetails, isAccountTransfer, taxPayments, transactionSubType);

            final Boolean isLoanToLoanTransfer = (Boolean) accountingBridgeData.get("isLoanToLoanTransfer");
            if (isLoanToLoanTransfer != null && isLoanToLoanTransfer) {
                transaction.setIsLoanToLoanTransfer(true);
            } else {
                transaction.setIsLoanToLoanTransfer(false);
            }
            newLoanTransactions.add(transaction);

        }

        return new LoanDTO(loanId, loanProductId, officeId, currencyData.code(), cashBasedAccountingEnabled,
                upfrontAccrualBasedAccountingEnabled, periodicAccrualBasedAccountingEnabled, newLoanTransactions, writeOffReasonId);
    }

    public SavingsDTO populateSavingsDtoFromMap(final Map<String, Object> accountingBridgeData, final boolean cashBasedAccountingEnabled,
            final boolean accrualBasedAccountingEnabled) {
        final Long loanId = (Long) accountingBridgeData.get("savingsId");
        final Long loanProductId = (Long) accountingBridgeData.get("savingsProductId");
        final Long officeId = (Long) accountingBridgeData.get("officeId");
        final CurrencyData currencyData = (CurrencyData) accountingBridgeData.get("currency");
        final List<SavingsTransactionDTO> newSavingsTransactions = new ArrayList<>();
        boolean isAccountTransfer = (Boolean) accountingBridgeData.get("isAccountTransfer");

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> newTransactionsMap = (List<Map<String, Object>>) accountingBridgeData.get("newSavingsTransactions");

        for (final Map<String, Object> map : newTransactionsMap) {
            final Long transactionOfficeId = (Long) map.get("officeId");
            final String transactionId = ((Long) map.get("id")).toString();
            final Date transactionDate = ((LocalDate) map.get("date")).toDate();
            final SavingsAccountTransactionEnumData transactionType = (SavingsAccountTransactionEnumData) map.get("type");
            final BigDecimal amount = (BigDecimal) map.get("amount");
            final boolean reversed = (Boolean) map.get("reversed");
            final Long paymentTypeId = (Long) map.get("paymentTypeId");
            final BigDecimal overdraftAmount = (BigDecimal) map.get("overdraftAmount");

            final List<ChargePaymentDTO> feePayments = new ArrayList<>();
            final List<ChargePaymentDTO> penaltyPayments = new ArrayList<>();
            // extract charge payment details (if exists)
            if (map.containsKey("savingsChargesPaid")) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> savingsChargesPaidData = (List<Map<String, Object>>) map.get("savingsChargesPaid");
                for (final Map<String, Object> savingsChargePaid : savingsChargesPaidData) {
                    final Long chargeId = (Long) savingsChargePaid.get("chargeId");
                    final Long savingsChargeId = (Long) savingsChargePaid.get("savingsChargeId");
                    final boolean isPenalty = (Boolean) savingsChargePaid.get("isPenalty");
                    final BigDecimal chargeAmountPaid = (BigDecimal) savingsChargePaid.get("amount");
                    final boolean isCapitalized = false;
                    final ChargePaymentDTO chargePaymentDTO = new ChargePaymentDTO(chargeId, savingsChargeId, chargeAmountPaid,
                            isCapitalized);
                    if (isPenalty) {
                        penaltyPayments.add(chargePaymentDTO);
                    } else {
                        feePayments.add(chargePaymentDTO);
                    }
                }
            }

            final List<TaxPaymentDTO> taxPayments = new ArrayList<>();
            if (map.containsKey("taxDetails")) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> taxDatas = (List<Map<String, Object>>) map.get("taxDetails");
                for (final Map<String, Object> taxData : taxDatas) {
                    final BigDecimal taxAmount = (BigDecimal) taxData.get("amount");
                    final Long creditAccountId = (Long) taxData.get("creditAccountId");
                    final Long debitAccountId = (Long) taxData.get("debitAccountId");
                    final Long loanChargeId = null;
                    taxPayments.add(new TaxPaymentDTO(debitAccountId, creditAccountId, taxAmount, loanChargeId));
                }
            }

            if (!isAccountTransfer) {
                isAccountTransfer = this.accountTransfersReadPlatformService.isAccountTransfer(Long.parseLong(transactionId),
                        PortfolioAccountType.SAVINGS);
            }
            final SavingsTransactionDTO transaction = new SavingsTransactionDTO(transactionOfficeId, paymentTypeId, transactionId,
                    transactionDate, transactionType, amount, reversed, feePayments, penaltyPayments, overdraftAmount, isAccountTransfer,
                    taxPayments);

            newSavingsTransactions.add(transaction);

        }

        return new SavingsDTO(loanId, loanProductId, officeId, currencyData.code(), cashBasedAccountingEnabled,
                accrualBasedAccountingEnabled, newSavingsTransactions);
    }

    public SharesDTO populateSharesDtoFromMap(final Map<String, Object> accountingBridgeData, final boolean cashBasedAccountingEnabled,
            final boolean accrualBasedAccountingEnabled) {
        final Long shareAccountId = (Long) accountingBridgeData.get("shareAccountId");
        final Long shareProductId = (Long) accountingBridgeData.get("shareProductId");
        final Long officeId = (Long) accountingBridgeData.get("officeId");
        final CurrencyData currencyData = (CurrencyData) accountingBridgeData.get("currency");
        final List<SharesTransactionDTO> newTransactions = new ArrayList<>();

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> newTransactionsMap = (List<Map<String, Object>>) accountingBridgeData.get("newTransactions");

        for (final Map<String, Object> map : newTransactionsMap) {
            final Long transactionOfficeId = (Long) map.get("officeId");
            final String transactionId = ((Long) map.get("id")).toString();
            final Date transactionDate = ((LocalDate) map.get("date")).toDate();
            final ShareAccountTransactionEnumData transactionType = (ShareAccountTransactionEnumData) map.get("type");
            final ShareAccountTransactionEnumData transactionStatus = (ShareAccountTransactionEnumData) map.get("status");
            final BigDecimal amount = (BigDecimal) map.get("amount");
            final BigDecimal chargeAmount = (BigDecimal) map.get("chargeAmount");
            final Long paymentTypeId = (Long) map.get("paymentTypeId");

            final List<ChargePaymentDTO> feePayments = new ArrayList<>();
            // extract charge payment details (if exists)
            if (map.containsKey("chargesPaid")) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> chargesPaidData = (List<Map<String, Object>>) map.get("chargesPaid");
                for (final Map<String, Object> chargePaid : chargesPaidData) {
                    final Long chargeId = (Long) chargePaid.get("chargeId");
                    final Long loanChargeId = (Long) chargePaid.get("sharesChargeId");
                    final BigDecimal chargeAmountPaid = (BigDecimal) chargePaid.get("amount");
                    final boolean isCapitalized = false;
                    final ChargePaymentDTO chargePaymentDTO = new ChargePaymentDTO(chargeId, loanChargeId, chargeAmountPaid, isCapitalized);
                    feePayments.add(chargePaymentDTO);
                }
            }
            final SharesTransactionDTO transaction = new SharesTransactionDTO(transactionOfficeId, paymentTypeId, transactionId,
                    transactionDate, transactionType, transactionStatus, amount, chargeAmount, feePayments);

            newTransactions.add(transaction);

        }

        return new SharesDTO(shareAccountId, shareProductId, officeId, currencyData.code(), cashBasedAccountingEnabled,
                accrualBasedAccountingEnabled, newTransactions);
    }

    public ClientTransactionDTO populateClientTransactionDtoFromMap(final Map<String, Object> accountingBridgeData) {

        final Long transactionOfficeId = (Long) accountingBridgeData.get("officeId");
        final Long clientId = (Long) accountingBridgeData.get("clientId");
        final Long transactionId = (Long) accountingBridgeData.get("id");
        final Date transactionDate = ((LocalDate) accountingBridgeData.get("date")).toDate();
        final EnumOptionData transactionType = (EnumOptionData) accountingBridgeData.get("type");
        final BigDecimal amount = (BigDecimal) accountingBridgeData.get("amount");
        final boolean reversed = (Boolean) accountingBridgeData.get("reversed");
        final Long paymentTypeId = (Long) accountingBridgeData.get("paymentTypeId");
        final String currencyCode = (String) accountingBridgeData.get("currencyCode");
        final Boolean accountingEnabled = (Boolean) accountingBridgeData.get("accountingEnabled");

        final List<ClientChargePaymentDTO> clientChargePaymentDTOs = new ArrayList<>();
        // extract client charge payment details (if exists)
        if (accountingBridgeData.containsKey("clientChargesPaid")) {
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> clientChargesPaidData = (List<Map<String, Object>>) accountingBridgeData
                    .get("clientChargesPaid");
            for (final Map<String, Object> clientChargePaid : clientChargesPaidData) {
                final Long chargeId = (Long) clientChargePaid.get("chargeId");
                final Long clientChargeId = (Long) clientChargePaid.get("clientChargeId");
                final boolean isPenalty = (Boolean) clientChargePaid.get("isPenalty");
                final BigDecimal chargeAmountPaid = (BigDecimal) clientChargePaid.get("amount");
                final Long incomeAccountId = (Long) clientChargePaid.get("incomeAccountId");
                final ClientChargePaymentDTO clientChargePaymentDTO = new ClientChargePaymentDTO(chargeId, chargeAmountPaid, clientChargeId,
                        isPenalty, incomeAccountId);
                clientChargePaymentDTOs.add(clientChargePaymentDTO);
            }
        }

        final ClientTransactionDTO clientTransactionDTO = new ClientTransactionDTO(clientId, transactionOfficeId, paymentTypeId,
                transactionId, transactionDate, transactionType, currencyCode, amount, reversed, accountingEnabled,
                clientChargePaymentDTOs);

        return clientTransactionDTO;

    }

    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Accrual Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param loanProductId
     * @param paymentTypeId
     * @param amount
     * @param isReversal
     * @param journalEntry
     *            TODO
     */
    public void createAccrualBasedJournalEntriesAndReversalsForLoan(final Integer accountTypeToBeDebited,
            final Integer accountTypeToBeCredited, final Long loanProductId, final Long paymentTypeId, final BigDecimal amount,
            final Boolean isReversal, final Long writeOffReasonId, final JournalEntry journalEntry) {
        int accountTypeToDebitId = accountTypeToBeDebited;
        int accountTypeToCreditId = accountTypeToBeCredited;
        // reverse debits and credits for reversals
        if (isReversal) {
            accountTypeToDebitId = accountTypeToBeCredited;
            accountTypeToCreditId = accountTypeToBeDebited;
        }
        createJournalEntriesForLoan(accountTypeToDebitId, accountTypeToCreditId, loanProductId, paymentTypeId, amount, writeOffReasonId,
                journalEntry);
    }

    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Accrual Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param loanProductId
     * @param isReversal
     * @param journalEntry
     *            TODO
     * @param paymentTypeId
     * @param amount
     */
    public void createAccrualBasedJournalEntriesAndReversalsForLoanCharges(final Integer accountTypeToBeDebited,
            final Integer accountTypeToBeCredited, final Long loanProductId, final BigDecimal totalAmount, final Boolean isReversal,
            final List<ChargePaymentDTO> chargePaymentDTOs, final JournalEntry journalEntry) {

        final GLAccount receivableAccount = getLinkedGLAccountForLoanCharges(loanProductId, accountTypeToBeDebited, null);
        final Long paymentTypeId = null;
        final Long writeOffReasonId = null;
        final GLAccount debitLoanPortfolioAccount = getLinkedGLAccountForLoanProduct(loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), paymentTypeId, writeOffReasonId);
        final Map<GLAccount, BigDecimal> creditDetailsMap = new LinkedHashMap<>();
        final Map<GLAccount, BigDecimal> creditDetailsForCapitalizedChargesMap = new LinkedHashMap<>();
        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        for (final ChargePaymentDTO chargePaymentDTO : chargePaymentDTOs) {
            final Long chargeId = chargePaymentDTO.getChargeId();
            final GLAccount chargeSpecificAccount = getLinkedGLAccountForLoanCharges(loanProductId, accountTypeToBeCredited, chargeId);
            BigDecimal chargeSpecificAmount = chargePaymentDTO.getAmount();
            BigDecimal totalTaxAmount = BigDecimal.ZERO;
            final List<TaxPaymentDTO> taxPaymentDTOs = chargePaymentDTO.getTaxPaymentDTO();
            // adjust net credit amount if the account is already present in the
            // map
            if (taxPaymentDTOs != null) {
                for (final TaxPaymentDTO taxPaymentDTO : taxPaymentDTOs) {
                    if (taxPaymentDTO.getAmount() != null) {
                        final BigDecimal taxAmount = taxPaymentDTO.getAmount();
                        GLAccount taxGLAccount = chargeSpecificAccount;
                        if (taxPaymentDTO.getCreditAccountId() != null) {
                            taxGLAccount = getGLAccountById(taxPaymentDTO.getCreditAccountId());
                        }
                        totalCreditedAmount = totalCreditedAmount.add(taxAmount);
                        if (chargePaymentDTO.isCapitalized()) {
                            addOrUpdateAccountMapWithAmount(creditDetailsForCapitalizedChargesMap, taxGLAccount, taxAmount);
                        } else {
                            addOrUpdateAccountMapWithAmount(creditDetailsMap, taxGLAccount, taxAmount);
                        }
                        totalTaxAmount = totalTaxAmount.add(taxPaymentDTO.getAmount());
                    }
                }
            }
            chargeSpecificAmount = chargeSpecificAmount.subtract(totalTaxAmount);
            totalCreditedAmount = totalCreditedAmount.add(chargeSpecificAmount);
            if (chargePaymentDTO.isCapitalized()) {
                addOrUpdateAccountMapWithAmount(creditDetailsForCapitalizedChargesMap, chargeSpecificAccount, chargeSpecificAmount);
            } else {
                addOrUpdateAccountMapWithAmount(creditDetailsMap, chargeSpecificAccount, chargeSpecificAmount);
            }
        }
        if (!creditDetailsMap.isEmpty()) {
            createAccrualBasedJournalEntriesAndReversalsForLoanCharges(isReversal, creditDetailsMap, receivableAccount, journalEntry);
        }
        if (!creditDetailsForCapitalizedChargesMap.isEmpty()) {
            createAccrualBasedJournalEntriesAndReversalsForLoanCharges(isReversal, creditDetailsForCapitalizedChargesMap,
                    debitLoanPortfolioAccount, journalEntry);
        }
        if (totalAmount.compareTo(totalCreditedAmount) != 0) { throw new PlatformDataIntegrityException(
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                totalCreditedAmount, totalAmount); }
    }

    public void createAccrualBasedJournalEntriesAndReversalsForLoanChargesWithNPA(final Integer accountTypeToBeDebited,
            final Integer accountTypeToBeCredited, final Long loanProductId, final BigDecimal totalAmount, final Boolean isReversal,
            final List<ChargePaymentDTO> chargePaymentDTOs, final JournalEntry journalEntry) {

        final GLAccount receivableAccount = getLinkedGLAccountForLoanCharges(loanProductId, accountTypeToBeDebited, null);
        final Map<GLAccount, BigDecimal> creditDetailsMap = new LinkedHashMap<>();
        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        for (final ChargePaymentDTO chargePaymentDTO : chargePaymentDTOs) {
            final Long chargeId = chargePaymentDTO.getChargeId();
            final GLAccount chargeSpecificAccount = getLinkedGLAccountForLoanCharges(loanProductId, accountTypeToBeCredited, chargeId);
            BigDecimal chargeSpecificAmount = chargePaymentDTO.getAmount();
            BigDecimal totalTaxAmount = BigDecimal.ZERO;
            final List<TaxPaymentDTO> taxPaymentDTOs = chargePaymentDTO.getTaxPaymentDTO();
            // adjust net credit amount if the account is already present in the
            // map
            if (taxPaymentDTOs != null) {
                for (final TaxPaymentDTO taxPaymentDTO : taxPaymentDTOs) {
                    if (taxPaymentDTO.getAmount() != null) {
                        final BigDecimal taxAmount = taxPaymentDTO.getAmount();
                        totalCreditedAmount = totalCreditedAmount.add(taxAmount);
                        totalTaxAmount = totalTaxAmount.add(taxPaymentDTO.getAmount());
                    }
                }
            }
            chargeSpecificAmount = chargeSpecificAmount.subtract(totalTaxAmount);
            totalCreditedAmount = totalCreditedAmount.add(chargeSpecificAmount);
            addOrUpdateAccountMapWithAmount(creditDetailsMap, chargeSpecificAccount, chargeSpecificAmount);
        }
        if (!creditDetailsMap.isEmpty()) {
            createAccrualBasedJournalEntriesAndReversalsForLoanCharges(isReversal, creditDetailsMap, receivableAccount, journalEntry);
        }
        if (totalAmount.compareTo(totalCreditedAmount) != 0) { throw new PlatformDataIntegrityException(
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                totalCreditedAmount, totalAmount); }
    }

    private void createAccrualBasedJournalEntriesAndReversalsForLoanCharges(final Boolean isReversal,
            final Map<GLAccount, BigDecimal> creditDetailsMap, final GLAccount receivableAccount, final JournalEntry journalEntry) {
        final Map<GLAccount, BigDecimal> creditJournalEntriesMap = new LinkedHashMap<>();
        final Map<GLAccount, BigDecimal> receivableAccountMap = new LinkedHashMap<>();
        for (final Map.Entry<GLAccount, BigDecimal> entry : creditDetailsMap.entrySet()) {
            final GLAccount account = entry.getKey();
            final BigDecimal amount = entry.getValue();
            final BigDecimal receivableAmount = entry.getValue();
            addOrUpdateAccountMapWithAmount(creditJournalEntriesMap, account, amount);
            addOrUpdateAccountMapWithAmount(receivableAccountMap, receivableAccount, receivableAmount);
        }
        for (final Map.Entry<GLAccount, BigDecimal> entry : creditJournalEntriesMap.entrySet()) {
            final GLAccount account = entry.getKey();
            final BigDecimal amount = entry.getValue();
            if (isReversal) {
                createDebitJournalEntry(account, amount, journalEntry);
            } else {
                createCreditJournalEntry(account, amount, journalEntry);
            }
        }
        for (final Map.Entry<GLAccount, BigDecimal> entry : receivableAccountMap.entrySet()) {
            final BigDecimal amount = entry.getValue();
            if (isReversal) {
                createCreditJournalEntry(receivableAccount, amount, journalEntry);
            } else {
                createDebitJournalEntry(receivableAccount, amount, journalEntry);
            }
        }
    }

    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Cash Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param savingsProductId
     * @param paymentTypeId
     * @param amount
     * @param isReversal
     * @param journalEntry
     *            TODO
     */
    public void createCashBasedJournalEntriesAndReversalsForSavings(final Integer accountTypeToBeDebited,
            final Integer accountTypeToBeCredited, final Long savingsProductId, final Long paymentTypeId, final BigDecimal amount,
            final Boolean isReversal, final JournalEntry journalEntry) {
        int accountTypeToDebitId = accountTypeToBeDebited;
        int accountTypeToCreditId = accountTypeToBeCredited;
        // reverse debits and credits for reversals
        if (isReversal) {
            accountTypeToDebitId = accountTypeToBeCredited;
            accountTypeToCreditId = accountTypeToBeDebited;
        }
        createJournalEntriesForSavings(accountTypeToDebitId, accountTypeToCreditId, savingsProductId, paymentTypeId, amount, journalEntry);
    }
    
    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Cash Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param investmentProductId
     * @param amount
     * @param isReversal
     * @param journalEntry
     *            TODO
     */
    public void createCashBasedJournalEntriesAndReversalsForInvestment(final Integer accountTypeToBeDebited,
            final Integer accountTypeToBeCredited, final Long investmentProductId, final BigDecimal amount,
            final Boolean isReversal, final JournalEntry journalEntry) {
        int accountTypeToDebitId = accountTypeToBeDebited;
        int accountTypeToCreditId = accountTypeToBeCredited;
        // reverse debits and credits for reversals
        if (isReversal) {
            accountTypeToDebitId = accountTypeToBeCredited;
            accountTypeToCreditId = accountTypeToBeDebited;
        }
        createJournalEntriesForInvestment(accountTypeToDebitId, accountTypeToCreditId, investmentProductId, amount, journalEntry);
    }
    
    private void createJournalEntriesForInvestment(final int accountTypeToDebitId, final int accountTypeToCreditId,
            final Long investmentProductId, final BigDecimal amount, final JournalEntry journalEntry) {
        final GLAccount debitAccount = getLinkedGLAccountForInvestmentProduct(investmentProductId, accountTypeToDebitId);
        final GLAccount creditAccount = getLinkedGLAccountForInvestmentProduct(investmentProductId, accountTypeToCreditId);
        createDebitJournalEntry(debitAccount, amount, journalEntry);
        createCreditJournalEntry(creditAccount, amount, journalEntry);
    }

    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Cash Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param loanProductId
     * @param paymentTypeId
     * @param amount
     * @param isReversal
     * @param journalEntry
     *            TODO
     */
    public void createCashBasedJournalEntriesAndReversalsForLoan(final Integer accountTypeToBeDebited,
            final Integer accountTypeToBeCredited, final Long loanProductId, final Long paymentTypeId, final BigDecimal amount,
            final Boolean isReversal, final Long writeOffReasonId, final JournalEntry journalEntry) {
        int accountTypeToDebitId = accountTypeToBeDebited;
        int accountTypeToCreditId = accountTypeToBeCredited;
        // reverse debits and credits for reversals
        if (isReversal) {
            accountTypeToDebitId = accountTypeToBeCredited;
            accountTypeToCreditId = accountTypeToBeDebited;
        }
        createJournalEntriesForLoan(accountTypeToDebitId, accountTypeToCreditId, loanProductId, paymentTypeId, amount, writeOffReasonId,
                journalEntry);
    }

    public void createCreditJournalEntryOrReversalForLoan(final CASH_ACCOUNTS_FOR_LOAN accountMappingType, final Long loanProductId,
            final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal, final Long writeOffReasonId,
            final JournalEntry journalEntry) {
        final int accountMappingTypeId = accountMappingType.getValue();
        createCreditJournalEntryOrReversalForLoan(accountMappingTypeId, loanProductId, paymentTypeId, amount, isReversal, writeOffReasonId,
                journalEntry);
    }

    public void createCreditJournalEntryOrReversalForLoan(final ACCRUAL_ACCOUNTS_FOR_LOAN accountMappingType, final Long loanProductId,
            final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal, final Long writeOffReasonId,
            final JournalEntry journalEntry) {
        final int accountMappingTypeId = accountMappingType.getValue();
        createCreditJournalEntryOrReversalForLoan(accountMappingTypeId, loanProductId, paymentTypeId, amount, isReversal, writeOffReasonId,
                journalEntry);
    }

    /**
     * @param latestGLClosure
     * @param transactionDate
     */
    public void checkForBranchClosures(final GLClosure latestGLClosure, final Date transactionDate) {
        /**
         * check if an accounting closure has happened for this branch after the
         * transaction Date
         **/
        if (latestGLClosure != null) {
            if (latestGLClosure.getClosingDate().after(transactionDate)
                    || latestGLClosure.getClosingDate().equals(transactionDate)) { throw new JournalEntryInvalidException(
                            GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
        }
    }

    public GLClosure getLatestClosureByBranch(final long officeId) {
        return this.closureRepository.getLatestGLClosureByBranch(officeId);
    }

    public Office getOfficeById(final long officeId) {
        return this.officeRepository.findOne(officeId);
    }

    private void createJournalEntriesForLoan(final int accountTypeToDebitId, final int accountTypeToCreditId, final Long loanProductId,
            final Long paymentTypeId, final BigDecimal amount, final Long writeOFFReasonId, final JournalEntry journalEntry) {
        final GLAccount debitAccount = getLinkedGLAccountForLoanProduct(loanProductId, accountTypeToDebitId, paymentTypeId,
                writeOFFReasonId);
        final GLAccount creditAccount = getLinkedGLAccountForLoanProduct(loanProductId, accountTypeToCreditId, paymentTypeId,
                writeOFFReasonId);
        createDebitJournalEntry(debitAccount, amount, journalEntry);
        createCreditJournalEntry(creditAccount, amount, journalEntry);
    }

    private void createJournalEntriesForSavings(final int accountTypeToDebitId, final int accountTypeToCreditId,
            final Long savingsProductId, final Long paymentTypeId, final BigDecimal amount, final JournalEntry journalEntry) {
        final GLAccount debitAccount = getLinkedGLAccountForSavingsProduct(savingsProductId, accountTypeToDebitId, paymentTypeId);
        final GLAccount creditAccount = getLinkedGLAccountForSavingsProduct(savingsProductId, accountTypeToCreditId, paymentTypeId);
        createDebitJournalEntry(debitAccount, amount, journalEntry);
        createCreditJournalEntry(creditAccount, amount, journalEntry);
    }

    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Cash Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param savingsProductId
     * @param paymentTypeId
     * @param amount
     * @param isReversal
     * @param journalEntry
     *            TODO
     * @param loanId
     */
    public void createCashBasedJournalEntriesAndReversalsForSavingsTax(final CASH_ACCOUNTS_FOR_SAVINGS accountTypeToBeDebited,
            final CASH_ACCOUNTS_FOR_SAVINGS accountTypeToBeCredited, final Long savingsProductId, final Long paymentTypeId,
            final BigDecimal amount, final Boolean isReversal, final List<TaxPaymentDTO> taxDetails, final JournalEntry journalEntry) {

        for (final TaxPaymentDTO taxPaymentDTO : taxDetails) {
            if (taxPaymentDTO.getAmount() != null) {
                if (taxPaymentDTO.getCreditAccountId() == null) {
                    createCashBasedCreditJournalEntriesAndReversalsForSavings(accountTypeToBeCredited.getValue(), savingsProductId,
                            paymentTypeId, taxPaymentDTO.getAmount(), isReversal, journalEntry);
                } else {
                    createCashBasedCreditJournalEntriesAndReversalsForSavings(taxPaymentDTO.getCreditAccountId(), taxPaymentDTO.getAmount(),
                            isReversal, journalEntry);
                }
            }
        }
        createCashBasedDebitJournalEntriesAndReversalsForSavings(accountTypeToBeDebited.getValue(), savingsProductId, paymentTypeId, amount,
                isReversal, journalEntry);
    }

    public void createCashBasedDebitJournalEntriesAndReversalsForSavings(final Integer accountTypeToBeDebited, final Long savingsProductId,
            final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal, final JournalEntry journalEntry) {
        // reverse debits and credits for reversals
        if (isReversal) {
            createCreditJournalEntriesForSavings(accountTypeToBeDebited, savingsProductId, paymentTypeId, amount, journalEntry);
        } else {
            createDebitJournalEntriesForSavings(accountTypeToBeDebited, savingsProductId, paymentTypeId, amount, journalEntry);
        }
    }

    public void createCashBasedCreditJournalEntriesAndReversalsForSavings(final Integer accountTypeToBeCredited,
            final Long savingsProductId, final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal,
            final JournalEntry journalEntry) {
        // reverse debits and credits for reversals
        if (isReversal) {
            createDebitJournalEntriesForSavings(accountTypeToBeCredited, savingsProductId, paymentTypeId, amount, journalEntry);
        } else {
            createCreditJournalEntriesForSavings(accountTypeToBeCredited, savingsProductId, paymentTypeId, amount, journalEntry);
        }
    }

    public void createDebitJournalEntriesAndReversals(final Long debitAccountId, final BigDecimal amount, final Boolean isReversal,
            final JournalEntry journalEntry) {
        // reverse debits and credits for reversals
        final GLAccount debitAccount = getGLAccountById(debitAccountId);
        if (isReversal) {
            createCreditJournalEntry(debitAccount, amount, journalEntry);
        } else {
            createDebitJournalEntry(debitAccount, amount, journalEntry);
        }
    }

    public void createCashBasedCreditJournalEntriesAndReversalsForSavings(final Long creditAccountId, final BigDecimal amount,
            final Boolean isReversal, final JournalEntry journalEntry) {
        // reverse debits and credits for reversals
        final GLAccount creditAccount = getGLAccountById(creditAccountId);
        if (isReversal) {
            createDebitJournalEntry(creditAccount, amount, journalEntry);
        } else {
            createCreditJournalEntry(creditAccount, amount, journalEntry);
        }
    }

    private void createDebitJournalEntriesForSavings(final int accountTypeToDebitId, final Long savingsProductId, final Long paymentTypeId,
            final BigDecimal amount, final JournalEntry journalEntry) {
        final GLAccount debitAccount = getLinkedGLAccountForSavingsProduct(savingsProductId, accountTypeToDebitId, paymentTypeId);
        createDebitJournalEntry(debitAccount, amount, journalEntry);
    }

    private void createCreditJournalEntriesForSavings(final int accountTypeToCreditId, final Long savingsProductId,
            final Long paymentTypeId, final BigDecimal amount, final JournalEntry journalEntry) {
        final GLAccount creditAccount = getLinkedGLAccountForSavingsProduct(savingsProductId, accountTypeToCreditId, paymentTypeId);
        createCreditJournalEntry(creditAccount, amount, journalEntry);
    }

    public void createDebitJournalEntryOrReversalForLoan(final int accountMappingTypeId, final Long loanProductId, final Long paymentTypeId,
            final BigDecimal amount, final Boolean isReversal, final Long writeOffReasonId, final JournalEntry journalEntry) {
        final GLAccount account = getLinkedGLAccountForLoanProduct(loanProductId, accountMappingTypeId, paymentTypeId, writeOffReasonId);
        if (isReversal) {
            createCreditJournalEntry(account, amount, journalEntry);
        } else {
            createDebitJournalEntry(account, amount, journalEntry);
        }
    }

    public void createCreditJournalEntryOrReversalForLoanCharges(final int accountMappingTypeId, final Long loanProductId,
            final BigDecimal totalAmount, final Boolean isReversal, final List<ChargePaymentDTO> chargePaymentDTOs,
            final JournalEntry journalEntry) {
        /***
         * Map to track each account and the net credit to be made for a
         * particular account
         ***/
        final Map<GLAccount, BigDecimal> creditDetailsMap = constructCreditJournalEntryOrReversalForLoanChargesAccountMap(
                accountMappingTypeId, loanProductId, totalAmount, chargePaymentDTOs);
        for (final Map.Entry<GLAccount, BigDecimal> entry : creditDetailsMap.entrySet()) {
            final GLAccount account = entry.getKey();
            final BigDecimal amount = entry.getValue();
            if (isReversal) {
                createDebitJournalEntry(account, amount, journalEntry);
            } else {
                createCreditJournalEntry(account, amount, journalEntry);
            }
        }
    }

    /**
     * As discussed with Pramod Nuthakki : This method was refactored from the
     * method createCreditJournalEntryOrReversalForLoanCharges
     *
     * @param accountMappingTypeId
     * @param loanProductId
     * @param totalAmount
     * @param chargePaymentDTOs
     * @return
     */
    public Map<GLAccount, BigDecimal> constructCreditJournalEntryOrReversalForLoanChargesAccountMap(final int accountMappingTypeId,
            final Long loanProductId, final BigDecimal totalAmount, final List<ChargePaymentDTO> chargePaymentDTOs) {
        final Map<GLAccount, BigDecimal> creditDetailsMap = new LinkedHashMap<>();
        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        for (final ChargePaymentDTO chargePaymentDTO : chargePaymentDTOs) {
            final Long chargeId = chargePaymentDTO.getChargeId();
            final GLAccount chargeSpecificAccount = getLinkedGLAccountForLoanCharges(loanProductId, accountMappingTypeId, chargeId);
            BigDecimal chargeSpecificAmount = chargePaymentDTO.getAmount();
            BigDecimal totalTaxAmount = BigDecimal.ZERO;
            final List<TaxPaymentDTO> taxPaymentDTOs = chargePaymentDTO.getTaxPaymentDTO();
            // adjust net credit amount if the account is already present in the
            // map
            if (taxPaymentDTOs != null) {
                for (final TaxPaymentDTO taxPaymentDTO : taxPaymentDTOs) {
                    if (taxPaymentDTO.getAmount() != null) {
                        final BigDecimal taxAmount = taxPaymentDTO.getAmount();
                        GLAccount taxGLAccount = chargeSpecificAccount;
                        if (taxPaymentDTO.getCreditAccountId() != null) {
                            taxGLAccount = getGLAccountById(taxPaymentDTO.getCreditAccountId());
                        }
                        totalCreditedAmount = totalCreditedAmount.add(taxAmount);
                        addOrUpdateAccountMapWithAmount(creditDetailsMap, taxGLAccount, taxAmount);
                        totalTaxAmount = totalTaxAmount.add(taxPaymentDTO.getAmount());
                    }
                }
            }
            chargeSpecificAmount = chargeSpecificAmount.subtract(totalTaxAmount);
            totalCreditedAmount = totalCreditedAmount.add(chargeSpecificAmount);
            addOrUpdateAccountMapWithAmount(creditDetailsMap, chargeSpecificAccount, chargeSpecificAmount);
        }

        // TODO: Vishwas Temporary validation to be removed before moving to
        // release branch
        if (totalAmount.compareTo(totalCreditedAmount) != 0) { throw new PlatformDataIntegrityException(
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                totalCreditedAmount, totalAmount); }
        return creditDetailsMap;
    }

    /**
     * Convenience method that creates a pair of related Debits and Credits for
     * Cash Based accounting.
     *
     * The target accounts for debits and credits are switched in case of a
     * reversal
     *
     * @param accountTypeToBeDebited
     *            Enum of the placeholder GLAccount to be debited
     * @param accountTypeToBeCredited
     *            Enum of the placeholder of the GLAccount to be credited
     * @param savingsProductId
     * @param paymentTypeId
     * @param isReversal
     * @param journalEntry
     *            TODO
     * @param amount
     */
    public void createCashBasedJournalEntriesAndReversalsForSavingsCharges(final CASH_ACCOUNTS_FOR_SAVINGS accountTypeToBeDebited,
            final CASH_ACCOUNTS_FOR_SAVINGS accountTypeToBeCredited, final Long savingsProductId, final Long paymentTypeId,
            final BigDecimal totalAmount, final Boolean isReversal, final List<ChargePaymentDTO> chargePaymentDTOs,
            final JournalEntry journalEntry) {
        // TODO Vishwas: Remove this validation, as and when appropriate Junit
        // tests are written for accounting
        /**
         * Accounting module currently supports a single charge per transaction,
         * throw an error if this is not the case here so any developers
         * changing the expected portfolio behavior would also take care of
         * modifying the accounting code appropriately
         **/
        if (chargePaymentDTOs.size() != 1) { throw new PlatformDataIntegrityException(
                "Recent Portfolio changes w.r.t Charges for Savings have Broken the accounting code",
                "Recent Portfolio changes w.r.t Charges for Savings have Broken the accounting code"); }
        final ChargePaymentDTO chargePaymentDTO = chargePaymentDTOs.get(0);

        final GLAccount chargeSpecificAccount = getLinkedGLAccountForSavingsCharges(savingsProductId, accountTypeToBeCredited.getValue(),
                chargePaymentDTO.getChargeId());
        final GLAccount savingsControlAccount = getLinkedGLAccountForSavingsProduct(savingsProductId, accountTypeToBeDebited.getValue(),
                paymentTypeId);
        if (isReversal) {
            createDebitJournalEntry(chargeSpecificAccount, totalAmount, journalEntry);
            createCreditJournalEntry(savingsControlAccount, totalAmount, journalEntry);
        } else {
            createDebitJournalEntry(savingsControlAccount, totalAmount, journalEntry);
            createCreditJournalEntry(chargeSpecificAccount, totalAmount, journalEntry);
        }
    }

    private void createCreditJournalEntryOrReversalForLoan(final int accountMappingTypeId, final Long loanProductId,
            final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal, final Long writeOffReasonId,
            final JournalEntry journalEntry) {
        final GLAccount account = getLinkedGLAccountForLoanProduct(loanProductId, accountMappingTypeId, paymentTypeId, writeOffReasonId);
        createCreditJournalEntryOrReversal(amount, isReversal, account, journalEntry);
    }

    public void createCreditJournalEntryOrReversal(final BigDecimal amount, final Boolean isReversal, final GLAccount account,
            final JournalEntry journalEntry) {
        if (isReversal) {
            createDebitJournalEntry(account, amount, journalEntry);
        } else {
            createCreditJournalEntry(account, amount, journalEntry);
        }
    }

    public void createDebitJournalEntryOrReversal(final BigDecimal amount, final Boolean isReversal, final GLAccount account,
            final JournalEntry journalEntry) {
        if (isReversal) {
            createCreditJournalEntry(account, amount, journalEntry);
        } else {
            createDebitJournalEntry(account, amount, journalEntry);
        }
    }

    private void createCreditJournalEntry(final GLAccount account, final BigDecimal amount, final JournalEntry journalEntry) {
        final JournalEntryDetail journalEntryDetail1 = JournalEntryDetail.createNew(account, JournalEntryType.CREDIT, amount);
        journalEntry.addJournalEntryDetail(journalEntryDetail1);
    }

    private void createDebitJournalEntry(final GLAccount account, final BigDecimal amount, final JournalEntry journalEntry) {
        final JournalEntryDetail journalEntryDetail1 = JournalEntryDetail.createNew(account, JournalEntryType.DEBIT, amount);
        journalEntry.addJournalEntryDetail(journalEntryDetail1);
    }

    public void createJournalEntriesForShares(final int accountTypeToDebitId, final int accountTypeToCreditId, final Long shareProductId,
            final Long paymentTypeId, final BigDecimal amount, final JournalEntry journalEntry) {
        createDebitJournalEntryForShares(accountTypeToDebitId, shareProductId, paymentTypeId, amount, journalEntry);
        createCreditJournalEntryForShares(accountTypeToCreditId, shareProductId, paymentTypeId, amount, journalEntry);
    }

    public void createDebitJournalEntryForShares(final int accountTypeToDebitId, final Long shareProductId, final Long paymentTypeId,
            final BigDecimal amount, final JournalEntry journalEntry) {
        final GLAccount debitAccount = getLinkedGLAccountForShareProduct(shareProductId, accountTypeToDebitId, paymentTypeId);
        createDebitJournalEntry(debitAccount, amount, journalEntry);
    }

    public void createCreditJournalEntryForShares(final int accountTypeToCreditId, final Long shareProductId, final Long paymentTypeId,
            final BigDecimal amount, final JournalEntry journalEntry) {
        final GLAccount creditAccount = getLinkedGLAccountForShareProduct(shareProductId, accountTypeToCreditId, paymentTypeId);
        createCreditJournalEntry(creditAccount, amount, journalEntry);
    }

    public void createCashBasedJournalEntriesForSharesCharges(final CASH_ACCOUNTS_FOR_SHARES accountTypeToBeDebited,
            final CASH_ACCOUNTS_FOR_SHARES accountTypeToBeCredited, final Long shareProductId, final Long paymentTypeId,
            final BigDecimal totalAmount, final List<ChargePaymentDTO> chargePaymentDTOs, final JournalEntry journalEntry) {

        createDebitJournalEntryForShares(accountTypeToBeDebited.getValue(), shareProductId, paymentTypeId, totalAmount, journalEntry);
        createCashBasedJournalEntryForSharesCharges(accountTypeToBeCredited, shareProductId, totalAmount, chargePaymentDTOs, journalEntry);
    }

    public void createCashBasedJournalEntryForSharesCharges(final CASH_ACCOUNTS_FOR_SHARES accountTypeToBeCredited,
            final Long shareProductId, final BigDecimal totalAmount, final List<ChargePaymentDTO> chargePaymentDTOs,
            final JournalEntry journalEntry) {
        final Map<GLAccount, BigDecimal> creditDetailsMap = new LinkedHashMap<>();
        for (final ChargePaymentDTO chargePaymentDTO : chargePaymentDTOs) {
            final GLAccount chargeSpecificAccount = getLinkedGLAccountForShareCharges(shareProductId, accountTypeToBeCredited.getValue(),
                    chargePaymentDTO.getChargeId());
            final BigDecimal chargeSpecificAmount = chargePaymentDTO.getAmount();
            // adjust net credit amount if the account is already present in the
            // map
            addOrUpdateAccountMapWithAmount(creditDetailsMap, chargeSpecificAccount, chargeSpecificAmount);
        }

        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        for (final Map.Entry<GLAccount, BigDecimal> entry : creditDetailsMap.entrySet()) {
            final GLAccount account = entry.getKey();
            final BigDecimal amount = entry.getValue();
            totalCreditedAmount = totalCreditedAmount.add(amount);
            createCreditJournalEntry(account, amount, journalEntry);
        }
        if (totalAmount.compareTo(totalCreditedAmount) != 0) { throw new PlatformDataIntegrityException(
                "Recent Portfolio changes w.r.t Charges for shares have Broken the accounting code",
                "Recent Portfolio changes w.r.t Charges for shares have Broken the accounting code"); }
    }

    public void revertCashBasedJournalEntryForSharesCharges(final CASH_ACCOUNTS_FOR_SHARES accountTypeToBeCredited,
            final Long shareProductId, final BigDecimal totalAmount, final List<ChargePaymentDTO> chargePaymentDTOs,
            final JournalEntry journalEntry) {
        final Map<GLAccount, BigDecimal> creditDetailsMap = new LinkedHashMap<>();
        for (final ChargePaymentDTO chargePaymentDTO : chargePaymentDTOs) {
            final GLAccount chargeSpecificAccount = getLinkedGLAccountForShareCharges(shareProductId, accountTypeToBeCredited.getValue(),
                    chargePaymentDTO.getChargeId());
            final BigDecimal chargeSpecificAmount = chargePaymentDTO.getAmount();
            // adjust net credit amount if the account is already present in the
            // map
            addOrUpdateAccountMapWithAmount(creditDetailsMap, chargeSpecificAccount, chargeSpecificAmount);
        }
        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        for (final Map.Entry<GLAccount, BigDecimal> entry : creditDetailsMap.entrySet()) {
            final GLAccount account = entry.getKey();
            final BigDecimal amount = entry.getValue();
            totalCreditedAmount = totalCreditedAmount.add(amount);
            createDebitJournalEntry(account, amount, journalEntry);
        }
        if (totalAmount.compareTo(totalCreditedAmount) != 0) { throw new PlatformDataIntegrityException(
                "Recent Portfolio changes w.r.t Charges for shares have Broken the accounting code",
                "Recent Portfolio changes w.r.t Charges for shares have Broken the accounting code"); }
    }

    public GLAccount getLinkedGLAccountForLoanProduct(final Long loanProductId, final int accountMappingTypeId, final Long paymentTypeId,
            final Long writeOffReasonId) {
        GLAccount glAccount = null;
        if (isOrganizationAccount(accountMappingTypeId)) {
            final FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                    .findByFinancialActivityTypeWithNotFoundDetection(accountMappingTypeId);
            glAccount = financialActivityAccount.getGlAccount();
        } else {
            ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(loanProductId,
                    PortfolioProductType.LOAN.getValue(), accountMappingTypeId);

            /****
             * Get more specific mapping for FUND source accounts (based on
             * payment channels). Note that fund source placeholder ID would be
             * same for both cash and accrual accounts
             ***/
            if (accountMappingTypeId == CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue()) {
                final ProductToGLAccountMapping paymentChannelSpecificAccountMapping = this.accountMappingRepository
                        .findByProductIdAndProductTypeAndFinancialAccountTypeAndPaymentTypeId(loanProductId,
                                PortfolioProductType.LOAN.getValue(), accountMappingTypeId, paymentTypeId);
                if (paymentChannelSpecificAccountMapping != null) {
                    accountMapping = paymentChannelSpecificAccountMapping;
                }
            }

            if (accountMappingTypeId == CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue() && writeOffReasonId != null
                    && writeOffReasonId > 0) {
                final ProductToGLAccountMapping paymentChannelSpecificAccountMapping = this.accountMappingRepository
                        .findByProductIdAndProductTypeAndFinancialAccountTypeAndCodeValueId(loanProductId,
                                PortfolioProductType.LOAN.getValue(), accountMappingTypeId, writeOffReasonId);
                if (paymentChannelSpecificAccountMapping != null) {
                    accountMapping = paymentChannelSpecificAccountMapping;
                }
            }

            if (accountMapping == null) { throw new ProductToGLAccountMappingNotFoundException(PortfolioProductType.LOAN, loanProductId,
                    ACCRUAL_ACCOUNTS_FOR_LOAN.OVERPAYMENT.toString()); }
            glAccount = accountMapping.getGlAccount();
        }
        return glAccount;
    }

    private GLAccount getLinkedGLAccountForLoanCharges(final Long loanProductId, final int accountMappingTypeId, final Long chargeId) {
        ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(loanProductId,
                PortfolioProductType.LOAN.getValue(), accountMappingTypeId);
        /*****
         * Get more specific mappings for Charges and penalties (based on the
         * actual charge /penalty coupled with the loan product). Note the
         * income from fees and income from penalties placeholder ID would be
         * the same for both cash and accrual based accounts
         *****/

        // Vishwas TODO: remove this condition as it should always be true
        if (accountMappingTypeId == CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue()
                || accountMappingTypeId == CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue()) {
            final ProductToGLAccountMapping chargeSpecificIncomeAccountMapping = this.accountMappingRepository
                    .findByProductIdAndProductTypeAndFinancialAccountTypeAndChargeId(loanProductId, PortfolioProductType.LOAN.getValue(),
                            accountMappingTypeId, chargeId);
            if (chargeSpecificIncomeAccountMapping != null) {
                accountMapping = chargeSpecificIncomeAccountMapping;
            }
        }
        return accountMapping.getGlAccount();
    }

    private GLAccount getLinkedGLAccountForSavingsCharges(final Long savingsProductId, final int accountMappingTypeId,
            final Long chargeId) {
        ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(savingsProductId,
                PortfolioProductType.SAVING.getValue(), accountMappingTypeId);
        /*****
         * Get more specific mappings for Charges and penalties (based on the
         * actual charge /penalty coupled with the loan product). Note the
         * income from fees and income from penalties placeholder ID would be
         * the same for both cash and accrual based accounts
         *****/

        // Vishwas TODO: remove this condition as it should always be true
        if (accountMappingTypeId == CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_FEES.getValue()
                || accountMappingTypeId == CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue()) {
            final ProductToGLAccountMapping chargeSpecificIncomeAccountMapping = this.accountMappingRepository
                    .findByProductIdAndProductTypeAndFinancialAccountTypeAndChargeId(savingsProductId,
                            PortfolioProductType.SAVING.getValue(), accountMappingTypeId, chargeId);
            if (chargeSpecificIncomeAccountMapping != null) {
                accountMapping = chargeSpecificIncomeAccountMapping;
            }
        }
        return accountMapping.getGlAccount();
    }

    private GLAccount getLinkedGLAccountForSavingsProduct(final Long savingsProductId, final int accountMappingTypeId,
            final Long paymentTypeId) {
        GLAccount glAccount = null;
        if (isOrganizationAccount(accountMappingTypeId)) {
            final FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                    .findByFinancialActivityTypeWithNotFoundDetection(accountMappingTypeId);
            glAccount = financialActivityAccount.getGlAccount();
        } else {
            ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(savingsProductId,
                    PortfolioProductType.SAVING.getValue(), accountMappingTypeId);
            /****
             * Get more specific mapping for FUND source accounts (based on
             * payment channels). Note that fund source placeholder ID would be
             * same for both cash and accrual accounts
             ***/
            if (accountMappingTypeId == CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue()) {
                final ProductToGLAccountMapping paymentChannelSpecificAccountMapping = this.accountMappingRepository
                        .findByProductIdAndProductTypeAndFinancialAccountTypeAndPaymentTypeId(savingsProductId,
                                PortfolioProductType.SAVING.getValue(), accountMappingTypeId, paymentTypeId);
                if (paymentChannelSpecificAccountMapping != null) {
                    accountMapping = paymentChannelSpecificAccountMapping;
                }
            }
            glAccount = accountMapping.getGlAccount();
        }
        return glAccount;
    }
    
    private GLAccount getLinkedGLAccountForInvestmentProduct(final Long investmentProductId, final int accountMappingTypeId) {
        GLAccount glAccount = null;
        if (isOrganizationAccount(accountMappingTypeId)) {
            final FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                    .findByFinancialActivityTypeWithNotFoundDetection(accountMappingTypeId);
            glAccount = financialActivityAccount.getGlAccount();
        } else {
            ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(investmentProductId,
                    PortfolioProductType.INVESTMENT.getValue(), accountMappingTypeId);
            /****
             * Get more specific mapping for FUND source accounts (based on
             * payment channels). Note that fund source placeholder ID would be
             * same for both cash and accrual accounts
             ***/
            if (accountMappingTypeId == CASH_ACCOUNTS_FOR_INVESTMENT.FUND_SOURCE.getValue()) {
                final ProductToGLAccountMapping paymentChannelSpecificAccountMapping = this.accountMappingRepository
                        .findByProductIdAndProductTypeAndFinancialAccountTypeAndPaymentTypeId(investmentProductId,
                                PortfolioProductType.INVESTMENT.getValue(), accountMappingTypeId, null);
                if (paymentChannelSpecificAccountMapping != null) {
                    accountMapping = paymentChannelSpecificAccountMapping;
                }
            }
            glAccount = accountMapping.getGlAccount();
        }
        return glAccount;
    }

    private GLAccount getLinkedGLAccountForShareProduct(final Long shareProductId, final int accountMappingTypeId,
            final Long paymentTypeId) {
        GLAccount glAccount = null;
        if (isOrganizationAccount(accountMappingTypeId)) {
            final FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                    .findByFinancialActivityTypeWithNotFoundDetection(accountMappingTypeId);
            glAccount = financialActivityAccount.getGlAccount();
        } else {
            ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(shareProductId,
                    PortfolioProductType.SHARES.getValue(), accountMappingTypeId);

            if (accountMappingTypeId == CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE.getValue()) {
                final ProductToGLAccountMapping paymentChannelSpecificAccountMapping = this.accountMappingRepository
                        .findByProductIdAndProductTypeAndFinancialAccountTypeAndPaymentTypeId(shareProductId,
                                PortfolioProductType.SHARES.getValue(), accountMappingTypeId, paymentTypeId);
                if (paymentChannelSpecificAccountMapping != null) {
                    accountMapping = paymentChannelSpecificAccountMapping;
                }
            }
            glAccount = accountMapping.getGlAccount();
        }
        return glAccount;
    }

    private GLAccount getLinkedGLAccountForShareCharges(final Long shareProductId, final int accountMappingTypeId, final Long chargeId) {
        ProductToGLAccountMapping accountMapping = this.accountMappingRepository.findCoreProductToFinAccountMapping(shareProductId,
                PortfolioProductType.SHARES.getValue(), accountMappingTypeId);
        /*****
         * Get more specific mappings for Charges and penalties (based on the
         * actual charge /penalty coupled with the loan product). Note the
         * income from fees and income from penalties placeholder ID would be
         * the same for both cash and accrual based accounts
         *****/

        final ProductToGLAccountMapping chargeSpecificIncomeAccountMapping = this.accountMappingRepository
                .findByProductIdAndProductTypeAndFinancialAccountTypeAndChargeId(shareProductId, PortfolioProductType.SHARES.getValue(),
                        accountMappingTypeId, chargeId);
        if (chargeSpecificIncomeAccountMapping != null) {
            accountMapping = chargeSpecificIncomeAccountMapping;
        }
        return accountMapping.getGlAccount();
    }

    private boolean isOrganizationAccount(final int accountMappingTypeId) {
        boolean isOrganizationAccount = false;
        if (FINANCIAL_ACTIVITY.fromInt(accountMappingTypeId) != null) {
            isOrganizationAccount = true;
        }
        return isOrganizationAccount;
    }

    public BigDecimal createCreditJournalEntryOrReversalForClientPayments(final Boolean isReversal,
            final List<ClientChargePaymentDTO> clientChargePaymentDTOs, final JournalEntry journalEntry) {
        /***
         * Map to track each account affected and the net credit to be made for
         * a particular account
         ***/
        final Map<GLAccount, BigDecimal> creditDetailsMap = new LinkedHashMap<>();
        for (final ClientChargePaymentDTO clientChargePaymentDTO : clientChargePaymentDTOs) {
            if (clientChargePaymentDTO.getIncomeAccountId() != null) {
                final GLAccount chargeSpecificAccount = getGLAccountById(clientChargePaymentDTO.getIncomeAccountId());
                final BigDecimal chargeSpecificAmount = clientChargePaymentDTO.getAmount();
                // adjust net credit amount if the account is already present in
                // the map
                addOrUpdateAccountMapWithAmount(creditDetailsMap, chargeSpecificAccount, chargeSpecificAmount);
            }
        }
        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        for (final Map.Entry<GLAccount, BigDecimal> entry : creditDetailsMap.entrySet()) {
            final GLAccount account = entry.getKey();
            final BigDecimal amount = entry.getValue();
            totalCreditedAmount = totalCreditedAmount.add(amount);
            if (isReversal) {
                createDebitJournalEntry(account, amount, journalEntry);
            } else {
                createCreditJournalEntry(account, amount, journalEntry);
            }
        }
        return totalCreditedAmount;
    }

    public void createDebitJournalEntryOrReversalForClientChargePayments(final BigDecimal amount, final Boolean isReversal,
            final JournalEntry journalEntry, final Long paymentTypeId) {

        GLAccount account = this.financialActivityAccountRepository
                .findByFinancialActivityTypeWithNotFoundDetection(FINANCIAL_ACTIVITY.ASSET_FUND_SOURCE.getValue()).getGlAccount();
        if (paymentTypeId != null) {
            final PaymentType paymentType = this.paymentTypeRepository.findOneWithNotFoundDetection(paymentTypeId);
            final FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping = this.financialActivityAccountPaymentTypeRepository
                    .findByPaymentType(paymentType);
            GLAccount glAccount = null;
            if (financialActivityAccountPaymentTypeMapping != null) {
                glAccount = this.financialActivityAccountPaymentTypeRepository.findByPaymentType(paymentType).getGlAccount();
            }
            if (glAccount != null) {
                account = glAccount;
            }
        }

        if (isReversal) {
            createCreditJournalEntry(account, amount, journalEntry);
        } else {
            createDebitJournalEntry(account, amount, journalEntry);
        }
    }

    public GLAccount getGLAccountById(final Long accountId) {
        return this.accountRepositoryWrapper.findOneWithNotFoundDetection(accountId);
    }

    public void createCreditJournalEntryOrReversalForLoanFeeOrPenaltiesReceivable(final ACCRUAL_ACCOUNTS_FOR_LOAN accountMappingType,
            final Long loanProductId, final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal,
            final List<TaxPaymentDTO> taxPaymentDTOs, final Long loanChargeId, final JournalEntry journalEntry) {
        final int accountMappingTypeId = accountMappingType.getValue();
        createCreditJournalEntryOrReversalForLoanFeeOrPenaltiesReceivable(accountMappingTypeId, loanProductId, paymentTypeId, amount,
                isReversal, taxPaymentDTOs, loanChargeId, journalEntry);

    }

    private void createCreditJournalEntryOrReversalForLoanFeeOrPenaltiesReceivable(final int accountMappingTypeId, final Long loanProductId,
            final Long paymentTypeId, final BigDecimal amount, final Boolean isReversal, final List<TaxPaymentDTO> taxPaymentDTOs,
            final Long loanChargeId, final JournalEntry journalEntry) {
        final GLAccount account = getLinkedGLAccountForLoanProduct(loanProductId, accountMappingTypeId, paymentTypeId, null);
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        totalTaxAmount = createJournalEntryForTaxAndFetchTotalTaxAmount(isReversal, taxPaymentDTOs, account, totalTaxAmount, loanChargeId,
                journalEntry);
        if (isReversal) {
            createDebitJournalEntry(account, amount.subtract(totalTaxAmount), journalEntry);
        } else {
            createCreditJournalEntry(account, amount.subtract(totalTaxAmount), journalEntry);
        }
    }

    private BigDecimal createJournalEntryForTaxAndFetchTotalTaxAmount(final Boolean isReversal, final List<TaxPaymentDTO> taxPaymentDTOs,
            final GLAccount account, BigDecimal totalTaxAmount, final Long loanChargeId, final JournalEntry journalEntry) {
        if (taxPaymentDTOs != null) {
            for (final TaxPaymentDTO taxPaymentDTO : taxPaymentDTOs) {
                if (taxPaymentDTO.getAmount() != null && taxPaymentDTO.getLoanChargeId().equals(loanChargeId)) {
                    if (taxPaymentDTO.getCreditAccountId() != null) {
                        final GLAccount glAccount = getGLAccountById(taxPaymentDTO.getCreditAccountId());
                        createCreditJournalEntryOrReversal(taxPaymentDTO.getAmount(), isReversal, glAccount, journalEntry);
                    } else {
                        createDebitJournalEntryOrReversal(taxPaymentDTO.getAmount(), isReversal, account, journalEntry);
                    }
                    totalTaxAmount = totalTaxAmount.add(taxPaymentDTO.getAmount());
                }

            }
        }
        return totalTaxAmount;
    }

    public Map<GLAccount, BigDecimal> constructCreditJournalEntryOrReversalForLoanChargesAccountMap(final Long loanProductId,
            final Long paymentTypeId, final Long writeOffReasonId, final BigDecimal totalAmount,
            final Map<GLAccount, BigDecimal> accountMap, final boolean writeOff, final GLAccount feesReceivableAccount,
            final boolean ignoreAccountingForTax, final List<ChargePaymentDTO> chargedetails) {
        if (chargedetails == null || chargedetails.isEmpty()) { return accountMap; }
        final GLAccount loanPortfolioAccount = getLinkedGLAccountForLoanProduct(loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), paymentTypeId, writeOffReasonId);
        BigDecimal totalCreditedAmount = BigDecimal.ZERO;
        BigDecimal totalCreditedCapitalizedChargeAmount = BigDecimal.ZERO;
        BigDecimal totalCreditedNonCapitalizedChargeAmount = BigDecimal.ZERO;
        for (final ChargePaymentDTO chargePaymentDTO : chargedetails) {
            totalCreditedAmount = totalCreditedAmount.add(chargePaymentDTO.getAmount());
            BigDecimal totalTaxAmount = BigDecimal.ZERO;
            /**
             * Add tax splits for each GL accounts to accountMap. Do not
             * calculate the tax for waivers and write-offs
             **/
            if (!ignoreAccountingForTax && !writeOff && chargePaymentDTO.getTaxPaymentDTO() != null
                    && !chargePaymentDTO.getTaxPaymentDTO().isEmpty()) {
                totalTaxAmount = addTaxDetailsToGLAccount(chargePaymentDTO.getTaxPaymentDTO(), accountMap);
            }

            /***
             * Track charge amounts separately for capitalized and non
             * capitalized charges
             **/
            if (chargePaymentDTO.isCapitalized()) {
                totalCreditedCapitalizedChargeAmount = totalCreditedCapitalizedChargeAmount.add(chargePaymentDTO.getAmount())
                        .subtract(totalTaxAmount);
            } else {
                totalCreditedNonCapitalizedChargeAmount = totalCreditedNonCapitalizedChargeAmount.add(chargePaymentDTO.getAmount())
                        .subtract(totalTaxAmount);
            }
        }
        if (totalCreditedCapitalizedChargeAmount.compareTo(BigDecimal.ZERO) > 0) {
            addOrUpdateAccountMapWithAmount(accountMap, loanPortfolioAccount, totalCreditedCapitalizedChargeAmount);
        }
        if (totalCreditedNonCapitalizedChargeAmount.compareTo(BigDecimal.ZERO) > 0) {
            addOrUpdateAccountMapWithAmount(accountMap, feesReceivableAccount, totalCreditedNonCapitalizedChargeAmount);
        }
        if (totalAmount.compareTo(totalCreditedAmount) != 0) { throw new PlatformDataIntegrityException(
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                "Meltdown in advanced accounting...sum of all charges is not equal to the fee charge for a transaction",
                totalCreditedAmount, totalAmount); }
        return accountMap;
    }

    @SuppressWarnings("null")
    public BigDecimal addTaxDetailsToGLAccount(final List<TaxPaymentDTO> taxPaymentDetails, final Map<GLAccount, BigDecimal> accountMap) {
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        for (final TaxPaymentDTO taxPaymentDTO : taxPaymentDetails) {
            final GLAccount glAccount = getGLAccountById(taxPaymentDTO.getCreditAccountId());
            if (taxPaymentDTO != null) {
                addOrUpdateAccountMapWithAmount(accountMap, glAccount, taxPaymentDTO.getAmount());
                totalTaxAmount = totalTaxAmount.add(taxPaymentDTO.getAmount());
            }
        }
        return totalTaxAmount;
    }

    /**
     * Adding or updating the account map with Debit or Credit Amount
     *
     * @param accountMap
     * @param glAccount
     * @param amount
     * @return
     */
    public BigDecimal addOrUpdateAccountMapWithAmount(final Map<GLAccount, BigDecimal> accountMap, final GLAccount glAccount,
            BigDecimal amount) {
        if (accountMap.containsKey(glAccount)) {
            final BigDecimal existingAmount = accountMap.get(glAccount);
            amount = amount.add(existingAmount);
        }
        accountMap.put(glAccount, amount);
        return amount;
    }

    public JournalEntry createLoanJournalEntry(final String currencyCode, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final String transactionId, final Long officeId, final Long loanId) {
        final int entityType = PortfolioProductType.LOAN.getValue();
        String transactionIdentifier = null;
        Long entityTransactionId = null;
        if (StringUtils.isNumeric(transactionId)) {
            entityTransactionId = Long.parseLong(transactionId);
            transactionIdentifier = AccountingProcessorHelper.LOAN_TRANSACTION_IDENTIFIER + transactionId;
        }
        final JournalEntry journalEntry = JournalEntry.createNewForSystemEntries(officeId, currencyCode, transactionIdentifier,
                transactionDate, valueDate, effectiveDate, entityType, loanId, entityTransactionId);
        return journalEntry;
    }

    public JournalEntry createSavingsJournalEntry(final String currencyCode, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final String transactionId, final Long officeId, final Long savingsId) {
        final int entityType = PortfolioProductType.SAVING.getValue();
        String transactionIdentifier = null;
        Long entityTransactionId = null;
        if (StringUtils.isNumeric(transactionId)) {
            entityTransactionId = Long.parseLong(transactionId);
            transactionIdentifier = AccountingProcessorHelper.SAVINGS_TRANSACTION_IDENTIFIER + transactionId;
        }
        final JournalEntry journalEntry = JournalEntry.createNewForSystemEntries(officeId, currencyCode, transactionIdentifier,
                transactionDate, valueDate, effectiveDate, entityType, savingsId, entityTransactionId);
        return journalEntry;
    }
    
    public JournalEntry createInvestmentJournalEntry(final String currencyCode, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final String transactionId, final Long officeId, final Long investmentId) {
        final int entityType = PortfolioProductType.INVESTMENT.getValue();
        String transactionIdentifier = null;
        Long entityTransactionId = null;
        if (StringUtils.isNumeric(transactionId)) {
            entityTransactionId = Long.parseLong(transactionId);
            transactionIdentifier = AccountingProcessorHelper.INVESTMENT_TRANSACTION_IDENTIFIER + transactionId;
        }
        final JournalEntry journalEntry = JournalEntry.createNewForSystemEntries(officeId, currencyCode, transactionIdentifier,
                transactionDate, valueDate, effectiveDate, entityType, investmentId, entityTransactionId);
        return journalEntry;
    }

    public JournalEntry createShareJournalEntry(final String currencyCode, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final String transactionId, final Long officeId, final Long shareAccountId) {
        final int entityType = PortfolioProductType.SHARES.getValue();
        String transactionIdentifier = null;
        Long entityTransactionId = null;
        if (StringUtils.isNumeric(transactionId)) {
            entityTransactionId = Long.parseLong(transactionId);
            transactionIdentifier = AccountingProcessorHelper.SHARE_TRANSACTION_IDENTIFIER + transactionId;
        }
        final JournalEntry journalEntry = JournalEntry.createNewForSystemEntries(officeId, currencyCode, transactionIdentifier,
                transactionDate, valueDate, effectiveDate, entityType, shareAccountId, entityTransactionId);
        return journalEntry;
    }

    public JournalEntry createClientJournalEntry(final String currencyCode, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final Long transactionId, final Long officeId, final Long clientId) {
        final int entityType = PortfolioProductType.CLIENT.getValue();
        String transactionIdentifier = null;
        final Long entityTransactionId = transactionId;
        transactionIdentifier = AccountingProcessorHelper.CLIENT_TRANSACTION_IDENTIFIER + transactionId;
        final JournalEntry journalEntry = JournalEntry.createNewForSystemEntries(officeId, currencyCode, transactionIdentifier,
                transactionDate, valueDate, effectiveDate, entityType, clientId, entityTransactionId);
        return journalEntry;
    }

    public JournalEntry createProvioningJournalEntry(final String currencyCode, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final Long officeId, final Long provioningId) {
        final int entityType = PortfolioProductType.PROVISIONING.getValue();
        String transactionIdentifier = null;
        final Long entityTransactionId = null;
        transactionIdentifier = AccountingProcessorHelper.PROVISIONING_TRANSACTION_IDENTIFIER + provioningId;
        final JournalEntry journalEntry = JournalEntry.createNewForSystemEntries(officeId, currencyCode, transactionIdentifier,
                transactionDate, valueDate, effectiveDate, entityType, provioningId, entityTransactionId);
        return journalEntry;
    }
    
    public InvestmentDTO populateInvestmentDtoFromMap(final Map<String, Object> accountingBridgeData, final boolean cashBasedAccountingEnabled) {
        final Long investmentId = (Long) accountingBridgeData.get("investmentId");
        final Long investmentProductId = (Long) accountingBridgeData.get("investmentProductId");
        final Long officeId = (Long) accountingBridgeData.get("officeId");
        final CurrencyData currencyData = (CurrencyData) accountingBridgeData.get("currency");
        final List<InvestmentTransactionDTO> newInvestmentTransactions = new ArrayList<>();

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> newTransactionsMap = (List<Map<String, Object>>) accountingBridgeData.get("newInvestmentTransactions");

        for (final Map<String, Object> map : newTransactionsMap) {
            final Long transactionOfficeId = (Long) map.get("officeId");
            final String transactionId = ((Long) map.get("id")).toString();
            final Date transactionDate = ((LocalDate) map.get("date")).toDate();
            final InvestmentTransactionEnumData transactionType = (InvestmentTransactionEnumData) map.get("type");
            final BigDecimal amount = (BigDecimal) map.get("amount");
            final boolean reversed = (Boolean) map.get("reversed");

            final InvestmentTransactionDTO transaction = new InvestmentTransactionDTO(transactionOfficeId, transactionId, transactionDate, transactionType, amount, reversed);

            newInvestmentTransactions.add(transaction);

        }

        return new InvestmentDTO(investmentId, investmentProductId, officeId,currencyData.code(),cashBasedAccountingEnabled, newInvestmentTransactions );
    }
}
