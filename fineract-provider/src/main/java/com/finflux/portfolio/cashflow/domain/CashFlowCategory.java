package com.finflux.portfolio.cashflow.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.portfolio.cashflow.api.CashFlowCategoryApiConstants;

@Entity
@Table(name = "f_cashflow_category", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name" }, name = "UQ_f_cashflow_category_name"),
        @UniqueConstraint(columnNames = { "short_name" }, name = "UQ_f_cashflow_category_short_name") })
public class CashFlowCategory extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "short_name", length = 30, nullable = false, unique = true)
    private String shortName;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @Column(name = "category_enum_id", length = 2, nullable = false)
    private Integer categoryEnumId;

    @Column(name = "type_enum_id", length = 2, nullable = false)
    private Integer typeEnumId;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    protected CashFlowCategory() {}

    private CashFlowCategory(final String name, final String shortName, final String description, final Integer categoryEnumId,
            final Integer typeEnumId, final Boolean isActive) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.categoryEnumId = categoryEnumId;
        this.typeEnumId = typeEnumId;
        this.isActive = isActive;
    }

    public static CashFlowCategory create(final String name, final String shortName, final String description,
            final Integer categoryEnumId, final Integer typeEnumId, final Boolean isActive) {
        return new CashFlowCategory(name, shortName, description, categoryEnumId, typeEnumId, isActive);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);
        if (command.isChangeInStringParameterNamed(CashFlowCategoryApiConstants.nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(CashFlowCategoryApiConstants.nameParamName);
            actualChanges.put(CashFlowCategoryApiConstants.nameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(CashFlowCategoryApiConstants.descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(CashFlowCategoryApiConstants.descriptionParamName);
            actualChanges.put(CashFlowCategoryApiConstants.descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInIntegerParameterNamed(CashFlowCategoryApiConstants.categoryEnumIdParamName, this.categoryEnumId)) {
            final Integer newValue = command.integerValueOfParameterNamed(CashFlowCategoryApiConstants.categoryEnumIdParamName);
            actualChanges.put(CashFlowCategoryApiConstants.categoryEnumIdParamName, newValue);
            this.categoryEnumId = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(CashFlowCategoryApiConstants.typeEnumIdParamName, this.typeEnumId)) {
            final Integer newValue = command.integerValueOfParameterNamed(CashFlowCategoryApiConstants.typeEnumIdParamName);
            actualChanges.put(CashFlowCategoryApiConstants.typeEnumIdParamName, newValue);
            this.typeEnumId = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(CashFlowCategoryApiConstants.isActiveParamName, this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(CashFlowCategoryApiConstants.isActiveParamName);
            actualChanges.put(CashFlowCategoryApiConstants.isActiveParamName, newValue);
            this.isActive = newValue;
        }
        return actualChanges;
    }
    
    public Boolean isActive() {
        return this.isActive;
    }

    public void inActivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}