package org.apache.fineract.infrastructure.dataqueries.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum DataTableScopes {

    INVALID(0, "scopingCriteria.invalid"),
    LOAN_PRODUCT(1, "scopingCriteria.loanProductType"),
    SAVINGS_PRODUCT(2,"scopingCriteria.savingsProductType"),
    CLIENT_LEGAL_FORM(3, "scopingCriteria.clientLegalFormType"),
    CLIENT_TYPE(4, "scopingCriteria.clientType"),
    CLIENT_CLASSIFICATION(5, "scopingCriteria.clientType");
    
    private final Integer id;
    private final String code;
    
    
    public static DataTableScopes fromInt(final Integer scopeValue) {

        DataTableScopes enumeration = DataTableScopes.INVALID;
        switch (scopeValue) {
            case 1:
                enumeration = DataTableScopes.LOAN_PRODUCT;
            break;
            case 2:
                enumeration = DataTableScopes.SAVINGS_PRODUCT;
            break;
            case 3:
                enumeration = DataTableScopes.CLIENT_LEGAL_FORM;
            break;
            case 4:
                enumeration = DataTableScopes.CLIENT_TYPE;
            break;
            case 5:
                enumeration = DataTableScopes.CLIENT_CLASSIFICATION;
            break;
        }
        return enumeration;
    }
    
    private DataTableScopes(final Integer id, final String code) {
        this.id = id;
        this.code = code;
    }
    
    public Integer getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }
    
    public static EnumOptionData dataTableScopes(final int id) {
        return dataTableScopeType(DataTableScopes.fromInt(id));
    }
    
    public static EnumOptionData dataTableScopeType(final DataTableScopes type) {
        EnumOptionData optionData = null;
        switch (type) {
            case LOAN_PRODUCT:
                optionData = new EnumOptionData(type.getId().longValue(), type.getCode(), "Loan Product");
            break;
            case SAVINGS_PRODUCT:
                optionData = new EnumOptionData(type.getId().longValue(), type.getCode(), "Savings Product");
            break;
            case CLIENT_LEGAL_FORM:
                optionData = new EnumOptionData(type.getId().longValue(), type.getCode(), "Client Legal Form");
            break;
            case CLIENT_TYPE:
                optionData = new EnumOptionData(type.getId().longValue(), type.getCode(), "Client Type");
            break;
            case CLIENT_CLASSIFICATION:
                optionData = new EnumOptionData(type.getId().longValue(), type.getCode(), "Client Classification");
            break;
            default:
            break;
        }
        return optionData;
    }
    
    
    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> entityTypeOptions = new ArrayList<>();
        for (final DataTableScopes enumType : values()) {
            final EnumOptionData enumOptionData = dataTableScopes(enumType.getId());
            if (enumOptionData != null) {
                entityTypeOptions.add(enumOptionData);
            }
        }
        return entityTypeOptions;
    }
    
    public boolean isLoanProduct() {
        return this.id.equals(DataTableScopes.LOAN_PRODUCT.getId());
    }

    public boolean isSavingsProduct() {
        return this.id.equals(DataTableScopes.SAVINGS_PRODUCT.getId());
    }

    public boolean isClientLegalForm() {
        return this.id.equals(DataTableScopes.CLIENT_LEGAL_FORM.getId());
    }

    public boolean isClientType() {
        return this.id.equals(DataTableScopes.CLIENT_TYPE.getId());
    }

    public boolean isClientClassification() {
        return this.id.equals(DataTableScopes.CLIENT_CLASSIFICATION.getId());
    }
    
    
}
