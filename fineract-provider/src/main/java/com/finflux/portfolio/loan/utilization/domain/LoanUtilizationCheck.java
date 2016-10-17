package com.finflux.portfolio.loan.utilization.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.portfolio.loan.utilization.api.LoanUtilizationCheckApiConstants;

@Entity
@Table(name = "f_loan_utilization_check")
public class LoanUtilizationCheck extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_be_audited_by", nullable = true)
    private AppUser toBeAuditedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "audite_scheduled_on", nullable = true)
    private Date auditeScheduledOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_done_by", nullable = true)
    private Staff auditDoneBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "audit_done_on", nullable = true)
    private Date auditDoneOn;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loanUtilizationCheck", orphanRemoval = true)
    private Set<LoanUtilizationCheckDetail> loanUtilizationCheckDetails = new LinkedHashSet<>();

    protected LoanUtilizationCheck() {}

    private LoanUtilizationCheck(final AppUser toBeAuditedBy, final Date auditeScheduledOn, final Staff auditDoneBy, final Date auditDoneOn) {
        this.toBeAuditedBy = toBeAuditedBy;
        this.auditeScheduledOn = auditeScheduledOn;
        this.auditDoneBy = auditDoneBy;
        this.auditDoneOn = auditDoneOn;
    }

    public static LoanUtilizationCheck create(final AppUser toBeAuditedBy, final Date auditeScheduledOn, final Staff auditDoneBy,
            final Date auditDoneOn) {
        return new LoanUtilizationCheck(toBeAuditedBy, auditeScheduledOn, auditDoneBy, auditDoneOn);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInLongParameterNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName, toBeAuditedById())) {
            final Long newValue = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName);
            actualChanges.put(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName, auditDoneById())) {
            final Long newValue = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName);
            actualChanges.put(LoanUtilizationCheckApiConstants.auditDoneByIdParamName, newValue);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName,
                auditeScheduledOnParamNameLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName);
            actualChanges.put(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName, valueAsInput);
            actualChanges.put(LoanUtilizationCheckApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(LoanUtilizationCheckApiConstants.localeParamName, localeAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditeScheduledOnParamName);
            this.auditeScheduledOn = newValue.toDate();
        }

        if (command.isChangeInLocalDateParameterNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName,
                auditDoneOnParamNameLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName);
            actualChanges.put(LoanUtilizationCheckApiConstants.auditDoneOnParamName, valueAsInput);
            actualChanges.put(LoanUtilizationCheckApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(LoanUtilizationCheckApiConstants.localeParamName, localeAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName);
            this.auditDoneOn = newValue.toDate();
        }

        return actualChanges;
    }

    public Long toBeAuditedById() {
        Long toBeAuditedById = null;
        if (this.toBeAuditedBy != null) {
            toBeAuditedById = this.toBeAuditedBy.getId();
        }
        return toBeAuditedById;
    }

    public Long auditDoneById() {
        Long auditDoneById = null;
        if (this.auditDoneBy != null) {
            auditDoneById = this.auditDoneBy.getId();
        }
        return auditDoneById;
    }

    private LocalDate auditeScheduledOnParamNameLocalDate() {
        LocalDate auditeScheduledOn = null;
        if (this.auditeScheduledOn != null) {
            auditeScheduledOn = LocalDate.fromDateFields(this.auditeScheduledOn);
        }
        return auditeScheduledOn;
    }

    private LocalDate auditDoneOnParamNameLocalDate() {
        LocalDate auditDoneOn = null;
        if (this.auditDoneOn != null) {
            auditDoneOn = LocalDate.fromDateFields(this.auditDoneOn);
        }
        return auditDoneOn;
    }

    public void addAllLoanUtilizationCheckDetails(List<LoanUtilizationCheckDetail> loanUtilizationCheckDetails) {
        this.loanUtilizationCheckDetails.clear();
        if (loanUtilizationCheckDetails != null && loanUtilizationCheckDetails.size() > 0) {
            this.loanUtilizationCheckDetails.addAll(loanUtilizationCheckDetails);
        }
    }

    public void updateToBeAuditedBy(final AppUser toBeAuditedBy) {
        this.toBeAuditedBy = toBeAuditedBy;
    }

    public void updateAuditDoneBy(final Staff auditDoneBy) {
        this.auditDoneBy = auditDoneBy;
    }
}
