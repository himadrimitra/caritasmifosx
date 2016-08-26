package org.apache.fineract.portfolio.loanproduct.domain;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
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
}