package org.apache.fineract.portfolio.loanproduct.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductEntityProfileMapping;
import org.apache.fineract.portfolio.loanproduct.domain.ValueEntityType;
import org.springframework.stereotype.Service;

@Service
public class LoanProductDataAssembler {

    public Set<LoanProductEntityProfileMapping> assembleLoanProductEntityProfileMappings(final LoanProduct loanProduct,
            final JsonCommand command) {
        final Set<LoanProductEntityProfileMapping> existingLoanProductEntityProfileMappings = new LinkedHashSet<>(loanProduct.getLoanProductEntityProfileMapping());
        loanProduct.clearLoanProductEntityProfileMapping();
        final Set<LoanProductEntityProfileMapping> newLoanProductEntityProfileMappings = new LinkedHashSet<>();
        if (command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.isEnableRestrictionForClientProfileParamName)) {
            final Integer profileType = command.integerValueOfParameterNamed(LoanProductConstants.profileTypeParamName);
            final Integer[] selectedProfileTypes = command
                    .arrayIntegerValueOfParameterNamed(LoanProductConstants.selectedProfileTypeValuesParamName);
            final ValueEntityType valueEntityType = ValueEntityType.getValueEntityTypeByClientProfileType(profileType);
            for (final Integer value : selectedProfileTypes) {
                final LoanProductEntityProfileMapping loanProductEntityProfileMapping = LoanProductEntityProfileMapping.create(loanProduct,
                        profileType, value.longValue(), valueEntityType.getValue());
                newLoanProductEntityProfileMappings.add(loanProductEntityProfileMapping);
            }
            if (!newLoanProductEntityProfileMappings.isEmpty() && !existingLoanProductEntityProfileMappings.isEmpty()) {
                final Set<LoanProductEntityProfileMapping> removeFromNewLoanProductEntityProfileMappings = new LinkedHashSet<>();
                for (final LoanProductEntityProfileMapping existingLoanProductEntityProfileMapping : existingLoanProductEntityProfileMappings) {
                    for (final LoanProductEntityProfileMapping newLoanProductEntityProfileMapping : newLoanProductEntityProfileMappings) {
                        if (existingLoanProductEntityProfileMapping.getProfileType()
                                .equals(newLoanProductEntityProfileMapping.getProfileType())
                                && existingLoanProductEntityProfileMapping.getValue().equals(newLoanProductEntityProfileMapping.getValue())
                                && existingLoanProductEntityProfileMapping.getValueEntityType().equals(
                                        newLoanProductEntityProfileMapping.getValueEntityType())) {
                            removeFromNewLoanProductEntityProfileMappings.add(newLoanProductEntityProfileMapping);
                            loanProduct.addLoanProductEntityProfileMapping(existingLoanProductEntityProfileMapping);
                        }
                    }
                }
                if(!removeFromNewLoanProductEntityProfileMappings.isEmpty()){
                    newLoanProductEntityProfileMappings.removeAll(removeFromNewLoanProductEntityProfileMappings);
                }
            }
        }
        loanProduct.addAllLoanProductEntityProfileMapping(newLoanProductEntityProfileMappings);
        return newLoanProductEntityProfileMappings;
    }
}
