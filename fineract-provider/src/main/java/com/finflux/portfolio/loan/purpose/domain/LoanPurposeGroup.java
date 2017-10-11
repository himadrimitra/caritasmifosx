package com.finflux.portfolio.loan.purpose.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;

@Entity
@Table(name = "f_loan_purpose_group", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "type_cv_id" }, name = "UQ_f_loan_purpose_group"),
        @UniqueConstraint(columnNames = { "system_code" }, name = "UQ_f_loan_purpose_group_system_code") })
public class LoanPurposeGroup extends AbstractPersistable<Long> {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "system_code", length = 50, nullable = false, unique = true)
    private String systemCode;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_cv_id", nullable = false)
    private CodeValue loanPurposeGroupType;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;
    
    @Column(name = "is_system_defined", length = 1, nullable = false)
    private final boolean isSystemDefined = false;

    protected LoanPurposeGroup() {}

    private LoanPurposeGroup(final String name, final String systemCode, final String description, final CodeValue loanPurposeGroupType,
            final Boolean isActive) {
        this.name = name;
        this.systemCode = systemCode;
        this.description = description;
        this.loanPurposeGroupType = loanPurposeGroupType;
        this.isActive = isActive;
    }

    public static LoanPurposeGroup create(final String name, final String systemCode, final String description,
            final CodeValue loanPurposeGroupType, final Boolean isActive) {
        return new LoanPurposeGroup(name, systemCode, description, loanPurposeGroupType, isActive);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);
        if (command.isChangeInStringParameterNamed(LoanPurposeGroupApiConstants.nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(LoanPurposeGroupApiConstants.nameParamName);
            actualChanges.put(LoanPurposeGroupApiConstants.nameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(LoanPurposeGroupApiConstants.descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(LoanPurposeGroupApiConstants.descriptionParamName);
            actualChanges.put(LoanPurposeGroupApiConstants.descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInIntegerParameterNamed(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName,
                getloanPurposeGroupTypeId())) {
            final Integer newValue = command.integerValueOfParameterNamed(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName);
            actualChanges.put(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName, newValue);
        }

        if (command.isChangeInBooleanParameterNamed(LoanPurposeGroupApiConstants.isActiveParamName, this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(LoanPurposeGroupApiConstants.isActiveParamName);
            actualChanges.put(LoanPurposeGroupApiConstants.isActiveParamName, newValue);
            this.isActive = newValue;
        }

        return actualChanges;
    }

    private Integer getloanPurposeGroupTypeId() {
        if (this.loanPurposeGroupType != null) { return this.loanPurposeGroupType.getId().intValue(); }
        return null;
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

    public void updateLoanPurposeGroupType(final CodeValue loanPurposeGroupType) {
        this.loanPurposeGroupType = loanPurposeGroupType;
    }
}