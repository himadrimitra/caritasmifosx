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
package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.LOAN_PRODUCT_ACCOUNTING_PARAMS;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMappingRepository;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class LoanProductToGLAccountMappingHelper extends ProductToGLAccountMappingHelper {

    @Autowired
    public LoanProductToGLAccountMappingHelper(final GLAccountRepository glAccountRepository,
            final ProductToGLAccountMappingRepository glAccountMappingRepository, final FromJsonHelper fromApiJsonHelper,
            final ChargeRepositoryWrapper chargeRepositoryWrapper, final GLAccountRepositoryWrapper accountRepositoryWrapper,
            final PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper, final CodeValueRepositoryWrapper codeValueRepository) {
        super(glAccountRepository, glAccountMappingRepository, fromApiJsonHelper, chargeRepositoryWrapper, accountRepositoryWrapper,
                paymentTypeRepositoryWrapper, codeValueRepository);
    }

    /*** Set of abstractions for saving Loan Products to GL Account Mappings ***/

    public void saveLoanToAssetAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.ASSET, PortfolioProductType.LOAN);
    }

    public void saveLoanToIncomeAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.INCOME, PortfolioProductType.LOAN);
    }

    public void saveLoanToExpenseAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.EXPENSE, PortfolioProductType.LOAN);
    }

    public void saveLoanToLiabilityAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.LIABILITY, PortfolioProductType.LOAN);
    }
    
    public void saveLoanToIncomeOrLiabilityAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, PortfolioProductType.LOAN, GLAccountType.LIABILITY,
                GLAccountType.INCOME);
    }

    /*** Set of abstractions for merging Savings Products to GL Account Mappings ***/
    public void mergeLoanToAssetAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.ASSET,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToIncomeAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.INCOME,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToExpenseAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.EXPENSE,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToLiabilityAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes,
                GLAccountType.LIABILITY, PortfolioProductType.LOAN);
    }
    
    public void mergeLoanToIncomeOrLiabilityAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, changes, PortfolioProductType.LOAN,
                GLAccountType.LIABILITY, GLAccountType.INCOME);
    }
    /*** Abstractions for payments channel related to loan products ***/

    public void savePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        savePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void updatePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        updatePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void updateCodeValueToAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
    	updateCodeValueToExpenseMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void saveChargesToIncomeAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // save both fee and penalty charges
        saveChargesToIncomeOrLiabilityAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, true);
        saveChargesToIncomeOrLiabilityAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, false);
    }

    public void saveCodeValueToExpenseAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
    	saveCodeValueToExpenseAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void updateChargesToIncomeAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // update both fee and penalty charges
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, true);
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, false);
    }

    public Map<String, Object> populateChangesForNewLoanProductToGLAccountMappingCreation(final JsonElement element,
            final AccountingRuleType accountingRuleType) {
        final Map<String, Object> changes = new HashMap<>();

        final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), element);
        final Long loanPortfolioAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), element);
        final Long incomeFromInterestId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), element);
        final Long incomeFromFeeId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(),
                element);
        final Long incomeFromPenaltyId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), element);

        final Long incomeFromRecoveryAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(), element);

        final Long writeOffAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), element);
        final Long overPaymentAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(),
                element);
        final Long transfersInSuspenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), element);

        final Long receivableInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), element);
        final Long receivableFeeAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), element);
        final Long receivablePenaltyAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), element);
        final Long npaFeeSuspenceAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_FEES_SUSPENSE.getValue(), element);
        final Long npaPenaltySuspenceAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_PENALTIES_SUSPENSE.getValue(), element);
        final Long npaInterestSuspenceAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_INTEREST_SUSPENSE.getValue(), element);

        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                populateChangesForCashBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId,
                        incomeFromFeeId, incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId,
                        incomeFromRecoveryAccountId);
            break;
            case ACCRUAL_PERIODIC:
                populateChangesForPeriodicAccrualBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId,
                        incomeFromFeeId, incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId,
                        incomeFromRecoveryAccountId, receivableInterestAccountId, receivableFeeAccountId, receivablePenaltyAccountId,
                        npaFeeSuspenceAccountId,npaPenaltySuspenceAccountId,npaInterestSuspenceAccountId);
            break;
            case ACCRUAL_UPFRONT:
                populateChangesForAccrualBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId,
                        incomeFromFeeId, incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId,
                        incomeFromRecoveryAccountId, receivableInterestAccountId, receivableFeeAccountId, receivablePenaltyAccountId);
            break;
        }

        return changes;
    }

    private void populateChangesForAccrualBasedAccounting(final Map<String, Object> changes, final Long fundAccountId,
            final Long loanPortfolioAccountId, final Long incomeFromInterestId, final Long incomeFromFeeId, final Long incomeFromPenaltyId,
            final Long writeOffAccountId, final Long overPaymentAccountId, final Long transfersInSuspenseAccountId,
            final Long incomeFromRecoveryAccountId, final Long receivableInterestAccountId, final Long receivableFeeAccountId,
            final Long receivablePenaltyAccountId) {

        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), receivableInterestAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), receivableFeeAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), receivablePenaltyAccountId);

        populateChangesForCashBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId, incomeFromFeeId,
                incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId, incomeFromRecoveryAccountId);

    }
    
    private void populateChangesForPeriodicAccrualBasedAccounting(final Map<String, Object> changes, final Long fundAccountId,
            final Long loanPortfolioAccountId, final Long incomeFromInterestId, final Long incomeFromFeeId, final Long incomeFromPenaltyId,
            final Long writeOffAccountId, final Long overPaymentAccountId, final Long transfersInSuspenseAccountId,
            final Long incomeFromRecoveryAccountId, final Long receivableInterestAccountId, final Long receivableFeeAccountId,
            final Long receivablePenaltyAccountId, final Long npaFeeSuspenceAccountId, final Long npaPenaltySuspenceAccountId,
            final Long npaInterestSuspenceAccountId) {

        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), receivableInterestAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), receivableFeeAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), receivablePenaltyAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_FEES_SUSPENSE.getValue(), npaFeeSuspenceAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_PENALTIES_SUSPENSE.getValue(), npaPenaltySuspenceAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_INTEREST_SUSPENSE.getValue(), npaInterestSuspenceAccountId);

        populateChangesForCashBasedAccounting(changes, fundAccountId, loanPortfolioAccountId, incomeFromInterestId, incomeFromFeeId,
                incomeFromPenaltyId, writeOffAccountId, overPaymentAccountId, transfersInSuspenseAccountId, incomeFromRecoveryAccountId);

    }

    private void populateChangesForCashBasedAccounting(final Map<String, Object> changes, final Long fundAccountId,
            final Long loanPortfolioAccountId, final Long incomeFromInterestId, final Long incomeFromFeeId, final Long incomeFromPenaltyId,
            final Long writeOffAccountId, final Long overPaymentAccountId, final Long transfersInSuspenseAccountId,
            final Long incomeFromRecoveryAccountId) {
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), fundAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanPortfolioAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), incomeFromInterestId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), incomeFromFeeId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), incomeFromPenaltyId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), writeOffAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), overPaymentAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), transfersInSuspenseAccountId);
        changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(), incomeFromRecoveryAccountId);

    }

    /**
     * Examines and updates each account mapping for given loan product with
     * changes passed in from the Json element
     * 
     * @param loanProductId
     * @param changes
     * @param element
     * @param accountingRuleType
     */
    public void handleChangesToLoanProductToGLAccountMappings(final Long loanProductId, final Map<String, Object> changes,
            final JsonElement element, final AccountingRuleType accountingRuleType) {
        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                // asset
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(), CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.toString(), changes);

                // income
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.toString(), changes);

                // expenses
                mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);

                // liabilities
                mergeLoanToLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(), CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.toString(), changes);
                
                // subsidy
                handleChangesToLoanProductToGLAccountSubsidyMappings(loanProductId, element, changes);
            break;
            case ACCRUAL_UPFRONT:
                updateAccrualAccountingGLAccountMappings(loanProductId, changes, element);
                break;
            case ACCRUAL_PERIODIC:
                // assets (including receivables)
                updateAccrualAccountingGLAccountMappings(loanProductId, changes, element);
                
                updatePeriodicAccrualAccountingGLAccountMappings(loanProductId, changes, element);
            break;
        }
    }

    public void updatePeriodicAccrualAccountingGLAccountMappings(final Long loanProductId, final Map<String, Object> changes,
            final JsonElement element) {
        mergeLoanToIncomeOrLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_INTEREST_SUSPENSE.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_INTEREST_SUSPENSE.getValue(), changes);
        mergeLoanToIncomeOrLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_FEES_SUSPENSE.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_FEES_SUSPENSE.getValue(), changes);
        mergeLoanToIncomeOrLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.NPA_PENALTIES_SUSPENSE.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_PENALTIES_SUSPENSE.getValue(), changes);
    }

    /**
     *  Method updates common accounting details for both periodic and upfront accounting 
     * @param loanProductId
     * @param changes
     * @param element
     */
    public void updateAccrualAccountingGLAccountMappings(final Long loanProductId, final Map<String, Object> changes,
            final JsonElement element) {
        mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
        mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);
        mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.toString(),
                changes);
        mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(),
                loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.toString(), changes);
        mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.toString(), changes);
        mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(),
                loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.toString(), changes);

        // income
        mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.toString(),
                changes);
        mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), loanProductId,
                ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.toString(),
                changes);
        mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(),
                loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.toString(), changes);
        mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(),
                loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.toString(), changes);
            

        // expenses
        mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(),
                loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);

        // liabilities
        mergeLoanToLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), loanProductId,
                CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(), CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.toString(), changes);
        
        // subsidy
        handleChangesToLoanProductToGLAccountSubsidyMappings(loanProductId, element, changes);
    }
    
    public void handleChangesToLoanProductToGLAccountSubsidyMappings(Long loanProductId, JsonElement element,
            final Map<String, Object> changes) {
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.isSubsidyApplicableParamName, element)
                && this.fromApiJsonHelper.extractBooleanNamed(LoanProductConstants.isSubsidyApplicableParamName, element)) {
            if (!this.fromApiJsonHelper.parameterExists("createSubsidyAccountMappings", element)) {
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.SUBSIDY_ACCOUNT.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.toString(), changes);
                mergeLoanToLiabilityAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.SUBSIDY_FUND_SOURCE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_FUND_SOURCE.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_FUND_SOURCE.toString(), changes);
            } else {
                this.saveLoanToAssetAccountMapping(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.SUBSIDY_ACCOUNT.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.getValue());
                this.saveLoanToLiabilityAccountMapping(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.SUBSIDY_FUND_SOURCE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_FUND_SOURCE.getValue());
            }
        } else {
            this.deleteLoanProductToGLAccountSubsidyMapping(loanProductId);
        }
    }
    
    public void deleteLoanProductToGLAccountSubsidyMapping(final Long loanProductId) {
        deleteProductToGLAccountSubsidyMapping(loanProductId, PortfolioProductType.LOAN);
    }

    public void deleteLoanProductToGLAccountMapping(final Long loanProductId) {
        deleteProductToGLAccountMapping(loanProductId, PortfolioProductType.LOAN);
    }

}
