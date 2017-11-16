package com.finflux.portfolio.investmenttracker.domain;

import static org.apache.fineract.portfolio.savings.SavingsApiConstants.chargesParamName;
import static org.apache.fineract.portfolio.savings.SavingsApiConstants.idParamName;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.exception.ChargeCannotBeAppliedToException;
import org.apache.fineract.portfolio.loanproduct.exception.InvalidCurrencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.api.InvestmentProductApiconstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component
public class InvestmentProductDataAssembler {

    private final ChargeRepositoryWrapper chargeRepository;

    @Autowired
    public InvestmentProductDataAssembler(final ChargeRepositoryWrapper chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    public InvestmentProduct createAssemble(final JsonCommand command) {

        final String name = command.stringValueOfParameterNamed(InvestmentProductApiconstants.nameParamName);
        final String shortName = command.stringValueOfParameterNamed(InvestmentProductApiconstants.shortNameParamName);
        final String description = command.stringValueOfParameterNamed(InvestmentProductApiconstants.descriptionParamName);

        final String currencyCode = command.stringValueOfParameterNamed(InvestmentProductApiconstants.currencyCodeParamName);
        final Integer digitsAfterDecimal = command.integerValueOfParameterNamed(InvestmentProductApiconstants.digitsAfterDecimalParamName);
        final Integer inMultiplesOf = command.integerValueOfParameterNamed(InvestmentProductApiconstants.inMultiplesOfParamName);
        final MonetaryCurrency currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal, inMultiplesOf);

        final BigDecimal defaultNominalInterestRate = command
                .bigDecimalValueOfParameterNamed(InvestmentProductApiconstants.defaultNominalInterestRateParamName);
        final BigDecimal minNominalInterestRate = command
                .bigDecimalValueOfParameterNamed(InvestmentProductApiconstants.minNominalInterestRateParamName);
        final BigDecimal maxNominalInterestRate = command
                .bigDecimalValueOfParameterNamed(InvestmentProductApiconstants.maxNominalInterestRateParamName);
        final Integer nominalInterestRateEnum = command
                .integerValueOfParameterNamed(InvestmentProductApiconstants.nominalInterestRateEnumParamName);

        final Integer interestCompoundingPeriodEnum = command
                .integerValueOfParameterNamed(InvestmentProductApiconstants.interestCompoundingPeriodEnumParamName);

        final Integer defaultInvestmentTermPeriod = command
                .integerValueOfParameterNamed(InvestmentProductApiconstants.defaultInvestmentTermPeriodParamName);
        final Integer minInvestmentTermPeriod = command
                .integerValueOfParameterNamed(InvestmentProductApiconstants.minInvestmentTermPeriodParamName);
        final Integer maxInvestmentTermPeriod = command
                .integerValueOfParameterNamed(InvestmentProductApiconstants.maxInvestmentTermPeriodEnumParamName);
        final Integer investmentTermEnum = command.integerValueOfParameterNamed(InvestmentProductApiconstants.investmentTermEnumParamName);

        final boolean overrideTermsInInvestmentAccounts = command
                .booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.overrideTermsParamName);
        final boolean nominalInterestRate = command
                .booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.nominalInterestRateParamName);
        final boolean interestCompoundingPeriod = command
                .booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.interestCompoundingPeriodParamName);
        final boolean investmentTerm = command.booleanPrimitiveValueOfParameterNamed(InvestmentProductApiconstants.investmentTermParamName);

        final Integer accountingType = command.integerValueOfParameterNamed(InvestmentProductApiconstants.accountingTypeParamName);

        // Investment product charges
        final Set<Charge> charges = assembleListOfInvestmentProductCharges(command, currencyCode);

        final InvestmentProduct investmentProduct = InvestmentProduct.createInvestmentProduct(name, shortName, description, currency,
                minNominalInterestRate, defaultNominalInterestRate, maxNominalInterestRate, nominalInterestRateEnum,
                interestCompoundingPeriodEnum, minInvestmentTermPeriod, defaultInvestmentTermPeriod, maxInvestmentTermPeriod,
                investmentTermEnum, overrideTermsInInvestmentAccounts, nominalInterestRate, interestCompoundingPeriod, investmentTerm,
                accountingType, charges);

        return investmentProduct;
    }

    public Set<Charge> assembleListOfInvestmentProductCharges(final JsonCommand command, final String investmentProductCurrencyCode) {

        final Set<Charge> charges = new HashSet<>();

        if (command.parameterExists(chargesParamName)) {
            final JsonArray chargesArray = command.arrayOfParameterNamed(chargesParamName);
            if (chargesArray != null) {
                for (int i = 0; i < chargesArray.size(); i++) {

                    final JsonObject jsonObject = chargesArray.get(i).getAsJsonObject();
                    if (jsonObject.has(idParamName)) {
                        final Long id = jsonObject.get(idParamName).getAsLong();

                        final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(id);

                        if (!charge.isExternalInvestmentCharge()) {
                            final String errorMessage = "Charge with identifier " + charge.getId()
                                    + " cannot be applied to Investment product.";
                            throw new ChargeCannotBeAppliedToException("investment.product", errorMessage, charge.getId());
                        }

                        if (!investmentProductCurrencyCode.equals(charge.getCurrencyCode())) {
                            final String errorMessage = "Charge and Investment Product must have the same currency.";
                            throw new InvalidCurrencyException("charge", "attach.to.investment.product", errorMessage);
                        }
                        charges.add(charge);
                    }
                }
            }
        }

        return charges;
    }
}
