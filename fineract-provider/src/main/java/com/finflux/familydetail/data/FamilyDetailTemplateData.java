package com.finflux.familydetail.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

import com.finflux.portfolio.cashflow.data.CashFlowCategoryData;

@SuppressWarnings("unused")
public class FamilyDetailTemplateData {

    private final Collection<CodeValueData> salutationOptions;
    private final Collection<CodeValueData> relationshipOptions;
    private final Collection<CodeValueData> genderOptions;
    private final Collection<CodeValueData> educationOptions;
    private final Collection<CashFlowCategoryData> occupationOptions;

    private FamilyDetailTemplateData(Collection<CodeValueData> salutationOptions, Collection<CodeValueData> relationshipOptions,
            Collection<CodeValueData> genderOptions, Collection<CodeValueData> educationOptions,
            Collection<CashFlowCategoryData> occupationOptions) {
        this.salutationOptions = salutationOptions;
        this.relationshipOptions = relationshipOptions;
        this.genderOptions = genderOptions;
        this.educationOptions = educationOptions;
        this.occupationOptions = occupationOptions;
    }

    public static FamilyDetailTemplateData template(Collection<CodeValueData> salutationOptions,
            Collection<CodeValueData> relationshipOptions, Collection<CodeValueData> genderOptions,
            Collection<CodeValueData> educationOptions, Collection<CashFlowCategoryData> occupationOptions) {
        return new FamilyDetailTemplateData(salutationOptions, relationshipOptions, genderOptions, educationOptions, occupationOptions);
    }

}