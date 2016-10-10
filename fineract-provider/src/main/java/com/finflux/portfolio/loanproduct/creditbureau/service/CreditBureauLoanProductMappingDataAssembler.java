package com.finflux.portfolio.loanproduct.creditbureau.service;

import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMapping;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProductRepositoryWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class CreditBureauLoanProductMappingDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CreditBureauProductRepositoryWrapper creditBureauProductRepository;
    private final LoanProductRepository loanProductRepository;

    @Autowired
    public CreditBureauLoanProductMappingDataAssembler(final FromJsonHelper fromApiJsonHelper,
            final CreditBureauProductRepositoryWrapper creditBureauProductRepository, final LoanProductRepository loanProductRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.creditBureauProductRepository = creditBureauProductRepository;
        this.loanProductRepository = loanProductRepository;
    }

    public CreditBureauLoanProductMapping assembleCreateForm(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed("creditBureauProductId", element);
        final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository.findOneWithNotFoundDetection(creditBureauProductId);
        final Long loanProductId = this.fromApiJsonHelper.extractLongNamed("loanProductId", element);
        final LoanProduct loanProduct = this.loanProductRepository.findOne(loanProductId);
        final Boolean isCreditcheckMandatory = this.fromApiJsonHelper.extractBooleanNamed("isCreditcheckMandatory", element);
        final Boolean skipCreditcheckInFailure = this.fromApiJsonHelper.extractBooleanNamed("skipCreditcheckInFailure", element);
        final Integer stalePeriod = this.fromApiJsonHelper.extractIntegerNamed("stalePeriod", element, locale);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed("isActive", element);
        if (isActive == null) {
            isActive = false;
        }
        return CreditBureauLoanProductMapping.create(creditBureauProduct, loanProduct, isCreditcheckMandatory, skipCreditcheckInFailure,
                stalePeriod, isActive);
    }

    public Map<String, Object> assembleUpdateForm(final CreditBureauLoanProductMapping creditBureauLoanProductMapping,
            final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final Map<String, Object> actualChanges = creditBureauLoanProductMapping.update(command);
        if (actualChanges.containsKey("creditBureauProductId")) {
            final Long creditBureauProductId = this.fromApiJsonHelper.extractLongNamed("creditBureauProductId", element);
            final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository.findOneWithNotFoundDetection(creditBureauProductId);
            creditBureauLoanProductMapping.updateCreditBureauProduct(creditBureauProduct);
        }
        if (actualChanges.containsKey("loanProductId")) {
            final Long loanProductId = this.fromApiJsonHelper.extractLongNamed("loanProductId", element);
            final LoanProduct loanProduct = this.loanProductRepository.findOne(loanProductId);
            creditBureauLoanProductMapping.updateLoanProduct(loanProduct);
        }
        return actualChanges;
    }
}