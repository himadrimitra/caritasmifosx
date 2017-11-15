package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_INVESTMENT;
import org.apache.fineract.accounting.common.AccountingConstants.INVESTMENT_PRODUCT_ACCOUNTING_PARAMS;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMapping;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMappingRepository;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class InvestmentProductToGLAccountMappingHelper extends ProductToGLAccountMappingHelper {

    @Autowired
    public InvestmentProductToGLAccountMappingHelper(final GLAccountRepository glAccountRepository,
            final ProductToGLAccountMappingRepository glAccountMappingRepository, final FromJsonHelper fromApiJsonHelper,
            final ChargeRepositoryWrapper chargeRepositoryWrapper, final GLAccountRepositoryWrapper accountRepositoryWrapper,
            final PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper, final CodeValueRepositoryWrapper codeValueRepository) {
        super(glAccountRepository, glAccountMappingRepository, fromApiJsonHelper, chargeRepositoryWrapper, accountRepositoryWrapper,
                paymentTypeRepositoryWrapper, codeValueRepository);
    }

    public void saveInvestmentToAssetAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.ASSET, PortfolioProductType.INVESTMENT);
    }

    public void saveInvestmentToIncomeAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.INCOME, PortfolioProductType.INVESTMENT);
    }

    public void saveInvestmentToExpenseAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.EXPENSE,
                PortfolioProductType.INVESTMENT);
    }

    public void savePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        savePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.INVESTMENT);
    }

    public void saveChargesToExpenseAccountMappings(final JsonCommand command, final JsonElement element, final Long productId) {
        saveChargesToExpenseAccountMappings(command, element, productId, PortfolioProductType.INVESTMENT);
    }

    public void deleteInvestmentProductToGLAccountMapping(final Long investmentProductId) {
        deleteProductToGLAccountMapping(investmentProductId, PortfolioProductType.INVESTMENT);
    }

    public Map<String, Object> populateChangesForInvestmentProductToGLAccountMappingCreation(final JsonElement element,
            final AccountingRuleType accountingRuleType) {
        final Map<String, Object> changes = new HashMap<>();

        final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
                element);
        final Long investmentAccountId = this.fromApiJsonHelper.extractLongNamed(
                INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(), element);
        final Long incomeFromInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(), element);
        final Long feeExpenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(), element);

        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                changes.put(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), fundAccountId);
                changes.put(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(), investmentAccountId);
                changes.put(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(), incomeFromInterestAccountId);
                changes.put(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(), feeExpenseAccountId);
            break;
            default:
            break;
        }
        return changes;
    }

    public void handleChangesToInvestmentProductToGLAccountMappings(final Long investmentProductId, final Map<String, Object> changes,
            final JsonElement element, final AccountingRuleType accountingRuleType) {
        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                // asset
                mergeInvestmentToAssetAccountMappingChanges(element, INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
                        investmentProductId, CASH_ACCOUNTS_FOR_INVESTMENT.FUND_SOURCE.getValue(),
                        CASH_ACCOUNTS_FOR_INVESTMENT.FUND_SOURCE.toString(), changes);

                mergeInvestmentToAssetAccountMappingChanges(element, INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INVESTMENT_ACCOUNT.getValue(),
                        investmentProductId, CASH_ACCOUNTS_FOR_INVESTMENT.INVESTMENT_PARTNER_ACCOUNT.getValue(),
                        CASH_ACCOUNTS_FOR_INVESTMENT.INVESTMENT_PARTNER_ACCOUNT.toString(), changes);

                // income
                mergeInvestmentToIncomeAccountMappingChanges(element, INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_INTEREST.getValue(),
                        investmentProductId, CASH_ACCOUNTS_FOR_INVESTMENT.INCOME_FROM_INVESTMENT_INTEREST.getValue(),
                        CASH_ACCOUNTS_FOR_INVESTMENT.INCOME_FROM_INVESTMENT_INTEREST.toString(), changes);

                // expenses
                mergeInvestmentToExpenseAccountMappingChanges(element, INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(),
                        investmentProductId, CASH_ACCOUNTS_FOR_INVESTMENT.BANK_FEE_EXPENSE.getValue(),
                        CASH_ACCOUNTS_FOR_INVESTMENT.BANK_FEE_EXPENSE.toString(), changes);

            break;
            default:
            break;
        }
    }

    public void mergeInvestmentToAssetAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.ASSET,
                PortfolioProductType.INVESTMENT);
    }

    public void mergeInvestmentToIncomeAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.INCOME,
                PortfolioProductType.INVESTMENT);
    }

    public void mergeInvestmentToExpenseAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.EXPENSE,
                PortfolioProductType.INVESTMENT);
    }

    public void updatePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        updatePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.INVESTMENT);
    }

    public void updateChargesToExpenseAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // update both fee and penalty charges
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.SAVING, true);
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.SAVING, false);
    }

    public void updateChargesToExpenseAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes, final PortfolioProductType portfolioProductType) {

        List<ProductToGLAccountMapping> existingChargeToExpenseAccountMappings = this.accountMappingRepository
                .findAllPenaltyToIncomeAccountMappings(productId, portfolioProductType.getValue());
        String arrayFragmentName = INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue();

        final JsonArray chargeToExpenseAccountMappingArray = this.fromApiJsonHelper.extractJsonArrayNamed(arrayFragmentName, element);
        final Map<Long, Long> inputChargeToExpenseAccountMap = new HashMap<>();
        final Set<Long> existingCharges = new HashSet<>();
        if (chargeToExpenseAccountMappingArray != null) {
            if (changes != null) {
                changes.put(arrayFragmentName, command.jsonFragment(arrayFragmentName));
            }

            for (int i = 0; i < chargeToExpenseAccountMappingArray.size(); i++) {
                final JsonObject jsonObject = chargeToExpenseAccountMappingArray.get(i).getAsJsonObject();
                final Long chargeId = jsonObject.get(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.CHARGE_ID.getValue()).getAsLong();
                final Long incomeAccountId = jsonObject.get(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue()).getAsLong();
                inputChargeToExpenseAccountMap.put(chargeId, incomeAccountId);
            }

            // If input map is empty, delete all existing mappings
            if (inputChargeToExpenseAccountMap.size() == 0) {
                this.accountMappingRepository.deleteInBatch(existingChargeToExpenseAccountMappings);
            } else {
                for (final ProductToGLAccountMapping chargeToExpenseAccountMapping : existingChargeToExpenseAccountMappings) {
                    final Long currentCharge = chargeToExpenseAccountMapping.getCharge().getId();
                    existingCharges.add(currentCharge);
                    // update existing mappings (if required)
                    if (inputChargeToExpenseAccountMap.containsKey(currentCharge)) {
                        final Long newGLAccountId = inputChargeToExpenseAccountMap.get(currentCharge);
                        if (newGLAccountId != chargeToExpenseAccountMapping.getGlAccount().getId()) {
                            GLAccount glAccount = getAccountByIdAndType(INVESTMENT_PRODUCT_ACCOUNTING_PARAMS.FEE_EXPENSE.getValue(),
                                    GLAccountType.EXPENSE, newGLAccountId);
                            chargeToExpenseAccountMapping.setGlAccount(glAccount);
                            this.accountMappingRepository.save(chargeToExpenseAccountMapping);
                        }
                    } // deleted payment type
                    else {
                        this.accountMappingRepository.delete(chargeToExpenseAccountMapping);
                    }
                }
                // create new mappings
                final Set<Long> incomingCharges = inputChargeToExpenseAccountMap.keySet();
                incomingCharges.removeAll(existingCharges);
                for (final Long newCharge : incomingCharges) {
                    final Long newGLAccountId = inputChargeToExpenseAccountMap.get(newCharge);
                    saveChargeToExpenseAccountMapping(productId, newCharge, newGLAccountId, portfolioProductType);
                }
            }
        }
    }
}
