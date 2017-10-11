package org.apache.fineract.portfolio.loanproduct.domain;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class LoanProductBusinessRuleValidator {

    private final FromJsonHelper fromJsonHelper;

    @Autowired
    public LoanProductBusinessRuleValidator(final FromJsonHelper fromJsonHelper) {
        this.fromJsonHelper = fromJsonHelper;
    }

    public void validateLoanProductMandatoryCharges(final List<Map<String, Object>> chargeIdList, final JsonElement element) {
        if (chargeIdList != null && chargeIdList.size() > 0) {
            final JsonArray chargesArray = this.fromJsonHelper.extractJsonArrayNamed("charges", element);
            if (chargesArray != null && chargesArray.size() > 0) {
                for (int i = 0; i < chargesArray.size(); i++) {
                    final JsonObject obj = chargesArray.get(i).getAsJsonObject();
                    final String chargeId = this.fromJsonHelper.extractStringNamed("chargeId", obj);
                    for (Map<String, Object> map : chargeIdList) {
                        if (map.get("charge_id").toString().equalsIgnoreCase(chargeId)) {
                            chargeIdList.remove(map);
                            break;
                        }
                    }
                }
            }
            if (chargeIdList.size() > 0) {
                final String defaultUserMessage = "Please add all the loan product mandatory charges.";
                throw new LoanApplicationDateException("add.all.loan.product.mandatory.charges", defaultUserMessage,
                        chargeIdList.toString(), chargeIdList.toString());
            }
        }
    }
    
    public void validateLoanProductChargeMandatoryOrNot(final List<Map<String, Object>> chargeIdList, final Long chargeId) {
        for (final Map<String, Object> map : chargeIdList) {
            if (map.get("charge_id").toString().equalsIgnoreCase(chargeId.toString())) {
                final String defaultUserMessage = "Charge is mandatory for selected loan product.";
                throw new GeneralPlatformDomainRuleException("error.msg.loan.charge.is.mandatory.for.selected.loan.product",
                        defaultUserMessage, chargeId.toString(), chargeId.toString());
            }
        }
    }

    public void validateLoanProductApplicableForLoanType(final LoanProduct loanProduct, final AccountType accountType, final Client client) {
        if (!accountType.isInvalid()) {
            final Integer applicableForLoanType = loanProduct.getApplicableForLoanType();
            if (accountType.isIndividualAccount() || accountType.isJLGAccount()) {
                if (!(applicableForLoanType.equals(LoanProductApplicableForLoanType.ALL_TYPES.getValue()) || applicableForLoanType
                        .equals(LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT.getValue()))) {
                    throwValidationErrorForLoanProductApplicableForLoanType(loanProduct.getId(), accountType.getName());
                }
                validateLoanProductWithClientProfileType(loanProduct, client);
            } else if (accountType.isGroupAccount() || accountType.isGLIMAccount()) {
                if (!(applicableForLoanType.equals(LoanProductApplicableForLoanType.ALL_TYPES.getValue()) || applicableForLoanType
                        .equals(LoanProductApplicableForLoanType.GROUP.getValue()))) {
                    throwValidationErrorForLoanProductApplicableForLoanType(loanProduct.getId(), accountType.getName());
                }
            }
        }
    }

    private void validateLoanProductWithClientProfileType(final LoanProduct loanProduct, final Client client) {
        if (!loanProduct.getLoanProductEntityProfileMapping().isEmpty()
                && loanProduct.getApplicableForLoanType().equals(LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT.getValue())
                && client != null) {
            boolean isProductBelongsToClientProfileType = false;
            if (client.getLegalForm() != null) {
                for (final LoanProductEntityProfileMapping mapping : loanProduct.getLoanProductEntityProfileMapping()) {
                    if (mapping.getProfileType().equals(ClientProfileType.LEGAL_FORM.getValue())) {
                        if (client.getLegalForm().toString().equals(mapping.getValue().toString())) {
                            isProductBelongsToClientProfileType = true;
                            break;
                        }
                    }
                }
            }
            if (!isProductBelongsToClientProfileType && client.getClientType() != null) {
                for (final LoanProductEntityProfileMapping mapping : loanProduct.getLoanProductEntityProfileMapping()) {
                    if (mapping.getProfileType().equals(ClientProfileType.CLIENT_TYPE.getValue())) {
                        if (client.getClientType().getId().toString().equals(mapping.getValue().toString())) {
                            isProductBelongsToClientProfileType = true;
                            break;
                        }
                    }
                }
            }
            if (!isProductBelongsToClientProfileType && client.getClientClassification() != null) {
                for (final LoanProductEntityProfileMapping mapping : loanProduct.getLoanProductEntityProfileMapping()) {
                    if (mapping.getProfileType().equals(ClientProfileType.CLIENT_CLASSIFICATION.getValue())) {
                        if (client.getClientClassification().getId().toString().equals(mapping.getValue().toString())) {
                            isProductBelongsToClientProfileType = true;
                            break;
                        }
                    }
                }
            }
            if (!isProductBelongsToClientProfileType) {
                throwValidationErrorForLoanProductWithClientProfileType(loanProduct.getId(), client.getId());
            }
        }
    }

    private void throwValidationErrorForLoanProductWithClientProfileType(final Long loanProductId, final Long clientId) {
        final String defaultUserMessage = "Client does not belongs to selected loan product.";
        throw new GeneralPlatformDomainRuleException("error.msg.client.does.not.belongs.to.selected.loan.product", defaultUserMessage,
                loanProductId, clientId);
    }

    private void throwValidationErrorForLoanProductApplicableForLoanType(final Long loanProductId, final String loanType) {
        final String defaultUserMessage = "Loan product not belongs to " + loanType + " loan.";
        throw new GeneralPlatformDomainRuleException("error.msg.loan.product.not.belongs.to.loanType.loan", defaultUserMessage,
                loanProductId, loanType);
    }
}