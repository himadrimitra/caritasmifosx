package com.finflux.portfolio.loan.purpose.domain;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;

@Entity
@Table(name = "f_loan_purpose", uniqueConstraints = { @UniqueConstraint(columnNames = { "system_code" }, name = "UQ_f_loan_purpose_system_code") })
public class LoanPurpose extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "system_code", length = 50, nullable = false, unique = true)
    private String systemCode;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "loan_purpose_id", referencedColumnName = "id", nullable = false)
    private Set<LoanPurposeGroupMapping> loanPurposeGroupMapping = new LinkedHashSet();

    protected LoanPurpose() {}

    private LoanPurpose(final String name, final String systemCode, final String description, final Boolean isActive) {
        this.name = name;
        this.systemCode = systemCode;
        this.description = description;
        this.isActive = isActive;
    }

    public static LoanPurpose create(final String name, final String systemCode, final String description, final Boolean isActive) {
        return new LoanPurpose(name, systemCode, description, isActive);
    }

    public void addLoanPurposeGroupMapping(final LoanPurposeGroupMapping loanPurposeGroupMapping) {
        if (loanPurposeGroupMapping != null) {
            this.loanPurposeGroupMapping.add(loanPurposeGroupMapping);
        }
    }

    public void addAllLoanPurposeGroupMapping(final List<LoanPurposeGroupMapping> loanPurposeGroupMappings) {
        if (loanPurposeGroupMappings != null) {
            this.loanPurposeGroupMapping.addAll(loanPurposeGroupMappings);
        }
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

        if (command.isChangeInBooleanParameterNamed(LoanPurposeGroupApiConstants.isActiveParamName, this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(LoanPurposeGroupApiConstants.isActiveParamName);
            actualChanges.put(LoanPurposeGroupApiConstants.isActiveParamName, newValue);
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

    public void updateAllLoanPurposeGroupMapping(final Set<LoanPurposeGroupMapping> loanPurposeGroupMappings) {
        this.loanPurposeGroupMapping.clear();
        if (loanPurposeGroupMappings != null) {
            this.loanPurposeGroupMapping.addAll(loanPurposeGroupMappings);
        }
    }
}
