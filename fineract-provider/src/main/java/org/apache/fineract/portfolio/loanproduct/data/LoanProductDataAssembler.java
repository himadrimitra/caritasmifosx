package org.apache.fineract.portfolio.loanproduct.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductEntityProfileMapping;
import org.apache.fineract.portfolio.loanproduct.domain.ValueEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanProductDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanProductDataAssembler(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public Set<LoanProductEntityProfileMapping> assembleLoanProductEntityProfileMappings(final LoanProduct loanProduct,
            final JsonCommand command) {
        @SuppressWarnings("unused")
        final Set<LoanProductEntityProfileMapping> loanProductEntityProfileMappings = new LinkedHashSet<LoanProductEntityProfileMapping>();
        if (command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.isEnableRestrictionForClientProfileParamName)) {
            final Integer profileType = command.integerValueOfParameterNamed(LoanProductConstants.profileTypeParamName);
            final Integer[] selectedProfileTypes = command
                    .arrayIntegerValueOfParameterNamed(LoanProductConstants.selectedProfileTypeValuesParamName);
            final ValueEntityType valueEntityType = ValueEntityType.getValueEntityTypeByClientProfileType(profileType);
            for (final Integer value : selectedProfileTypes) {
                final LoanProductEntityProfileMapping loanProductEntityProfileMapping = LoanProductEntityProfileMapping.create(loanProduct,
                        profileType, value.longValue(), valueEntityType.getValue());
                loanProductEntityProfileMappings.add(loanProductEntityProfileMapping);
            }
        }
        return loanProductEntityProfileMappings;
    }
}
