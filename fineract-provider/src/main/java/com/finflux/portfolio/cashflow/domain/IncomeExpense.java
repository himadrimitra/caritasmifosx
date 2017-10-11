package com.finflux.portfolio.cashflow.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.portfolio.cashflow.api.IncomeExpenseApiConstants;

@Entity
@Table(name = "f_income_expense", uniqueConstraints = { @UniqueConstraint(columnNames = { "cashflow_category_id", "name" }, name = "UQ_f_income_expense_name") })
public class IncomeExpense extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "cashflow_category_id", referencedColumnName = "id", nullable = false)
    private CashFlowCategory cashFlowCategory;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @Column(name = "is_quantifier_needed", length = 1, nullable = true)
    private Boolean isQuantifierNeeded;

    @Column(name = "quantifier_label", length = 100, nullable = true)
    private String quantifierLabel;

    @Column(name = "is_capture_month_wise_income", length = 1, nullable = true)
    private Boolean isCaptureMonthWiseIncome;

    @Column(name = "stability_enum_id", length = 2, nullable = true)
    private Integer stabilityEnumId;

    @Column(name = "default_income", scale = 6, precision = 19, nullable = true)
    private BigDecimal defaultIncome;

    @Column(name = "default_expense", scale = 6, precision = 19, nullable = true)
    private BigDecimal defaultExpense;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    protected IncomeExpense() {}

    private IncomeExpense(final CashFlowCategory cashFlowCategory, final String name, final String description,
            final Boolean isQuantifierNeeded, final String quantifierLabel, final Boolean isCaptureMonthWiseIncome,
            final Integer stabilityEnumId, final BigDecimal defaultIncome, final BigDecimal defaultExpense, final Boolean isActive) {
        this.cashFlowCategory = cashFlowCategory;
        this.name = name;
        this.description = description;
        this.isQuantifierNeeded = isQuantifierNeeded;
        this.quantifierLabel = quantifierLabel;
        this.isCaptureMonthWiseIncome = isCaptureMonthWiseIncome;
        this.stabilityEnumId = stabilityEnumId;
        this.defaultIncome = defaultIncome;
        this.defaultExpense = defaultExpense;
        this.isActive = isActive;
    }

    public static IncomeExpense create(final CashFlowCategory cashFlowCategory, final String name, final String description,
            final Boolean isQuantifierNeeded, final String quantifierLabel, final Boolean isCaptureMonthWiseIncome,
            final Integer stabilityEnumId, final BigDecimal defaultIncome, final BigDecimal defaultExpense, final Boolean isActive) {
        return new IncomeExpense(cashFlowCategory, name, description, isQuantifierNeeded, quantifierLabel, isCaptureMonthWiseIncome,
                stabilityEnumId, defaultIncome, defaultExpense, isActive);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);
        if (command.isChangeInStringParameterNamed(IncomeExpenseApiConstants.nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(IncomeExpenseApiConstants.nameParamName);
            actualChanges.put(IncomeExpenseApiConstants.nameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(IncomeExpenseApiConstants.descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(IncomeExpenseApiConstants.descriptionParamName);
            actualChanges.put(IncomeExpenseApiConstants.descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInBooleanParameterNamed(IncomeExpenseApiConstants.isQuantifierNeededParamName, this.isQuantifierNeeded)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(IncomeExpenseApiConstants.isQuantifierNeededParamName);
            actualChanges.put(IncomeExpenseApiConstants.isQuantifierNeededParamName, newValue);
            this.isQuantifierNeeded = newValue;
        }

        if (command.isChangeInStringParameterNamed(IncomeExpenseApiConstants.quantifierLabelParamName, this.quantifierLabel)) {
            final String newValue = command.stringValueOfParameterNamed(IncomeExpenseApiConstants.quantifierLabelParamName);
            actualChanges.put(IncomeExpenseApiConstants.quantifierLabelParamName, newValue);
            this.quantifierLabel = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInBooleanParameterNamed(IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName,
                this.isCaptureMonthWiseIncome)) {
            final Boolean newValue = command
                    .booleanObjectValueOfParameterNamed(IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName);
            actualChanges.put(IncomeExpenseApiConstants.isCaptureMonthWiseIncomeParamName, newValue);
            this.isCaptureMonthWiseIncome = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(IncomeExpenseApiConstants.stabilityEnumIdParamName, this.stabilityEnumId)) {
            final Integer newValue = command.integerValueOfParameterNamed(IncomeExpenseApiConstants.stabilityEnumIdParamName);
            actualChanges.put(IncomeExpenseApiConstants.stabilityEnumIdParamName, newValue);
            this.stabilityEnumId = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(IncomeExpenseApiConstants.defaultIncomeParamName, this.defaultIncome)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(IncomeExpenseApiConstants.defaultIncomeParamName);
            actualChanges.put(IncomeExpenseApiConstants.defaultIncomeParamName, newValue);
            this.defaultIncome = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(IncomeExpenseApiConstants.defaultExpenseParamName, this.defaultExpense)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(IncomeExpenseApiConstants.defaultExpenseParamName);
            actualChanges.put(IncomeExpenseApiConstants.defaultExpenseParamName, newValue);
            this.defaultExpense = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(IncomeExpenseApiConstants.isActiveParamName, this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(IncomeExpenseApiConstants.isActiveParamName);
            actualChanges.put(IncomeExpenseApiConstants.isActiveParamName, newValue);
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
    
    public BigDecimal getDefaultIncome() {
        return this.defaultIncome;
    }

    public BigDecimal getDefaultExpense() {
        return this.defaultExpense;
    }
}