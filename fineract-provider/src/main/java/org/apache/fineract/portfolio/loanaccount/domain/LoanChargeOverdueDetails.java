package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.data.ChargeOverdueData;
import org.apache.fineract.portfolio.charge.domain.PenaltyGraceType;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_overdue_charge_detail")
public class LoanChargeOverdueDetails extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "recurrence_charge_id")
    private LoanRecurringCharge loanRecurringCharge;

    @Column(name = "grace_period", nullable = false)
    private Integer gracePeriod;

    @Column(name = "penalty_free_period", nullable = false)
    private Integer penaltyFreePeriod;

    @Column(name = "grace_type_enum", nullable = false)
    private Integer graceType;

    @Column(name = "apply_charge_for_broken_period", nullable = false)
    private boolean applyChargeForBrokenPeriod;

    @Column(name = "is_based_on_original_schedule", nullable = false)
    private boolean isBasedOnOriginalSchedule;

    @Column(name = "consider_only_posted_interest", nullable = false)
    private boolean considerOnlyPostedInterest;

    @Column(name = "calculate_charge_on_current_overdue", nullable = false)
    private boolean calculateChargeOnCurrentOverdue;
    
    @Column(name = "stop_charge_on_npa", nullable = false)
    private boolean stopChargeOnNPA;

    @Temporal(TemporalType.DATE)
    @Column(name = "last_applied_on_date")
    private Date lastAppliedOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "last_run_on_date")
    private Date lastRunOnDate;

    @Column(name = "min_overdue_amount_required")
    private BigDecimal minOverdueAmountRequired;

    protected LoanChargeOverdueDetails() {

    }

    public LoanChargeOverdueDetails(final LoanRecurringCharge loanRecurringCharge, final ChargeOverdueData chargeOverueDetail) {
        this.loanRecurringCharge = loanRecurringCharge;
        this.gracePeriod = chargeOverueDetail.getGracePeriod();
        this.penaltyFreePeriod = chargeOverueDetail.getPenaltyFreePeriod();
        this.graceType = chargeOverueDetail.getGraceType();
        this.applyChargeForBrokenPeriod = chargeOverueDetail.isApplyChargeForBrokenPeriod();
        this.isBasedOnOriginalSchedule = chargeOverueDetail.isBasedOnOriginalSchedule();
        this.considerOnlyPostedInterest = chargeOverueDetail.isConsiderOnlyPostedInterest();
        this.calculateChargeOnCurrentOverdue = chargeOverueDetail.isCalculateChargeOnCurrentOverdue();
        this.minOverdueAmountRequired = chargeOverueDetail.getMinOverdueAmountRequired();
        this.stopChargeOnNPA = chargeOverueDetail.isStopChargeOnNPA();
    }

    public LoanChargeOverdueDetails(final Integer gracePeriod, final Integer penaltyFreePeriod, final Integer graceType,
            final boolean applyChargeForBrokenPeriod, final boolean isBasedOnOriginalSchedule, final boolean considerOnlyPostedInterest,
            final boolean calculateChargeOnCurrentOverdue, boolean stopChargeOnNPA, final BigDecimal minOverdueAmountRequired,
            final Date lastAppliedOnDate, final Date lastRunOnDate) {
        this.gracePeriod = gracePeriod;
        this.penaltyFreePeriod = penaltyFreePeriod;
        this.graceType = graceType;
        this.applyChargeForBrokenPeriod = applyChargeForBrokenPeriod;
        this.isBasedOnOriginalSchedule = isBasedOnOriginalSchedule;
        this.considerOnlyPostedInterest = considerOnlyPostedInterest;
        this.calculateChargeOnCurrentOverdue = calculateChargeOnCurrentOverdue;
        this.stopChargeOnNPA = stopChargeOnNPA;
        this.minOverdueAmountRequired = minOverdueAmountRequired;
        this.lastAppliedOnDate = lastAppliedOnDate;
        this.lastRunOnDate = lastRunOnDate;
    }
    
    private LoanChargeOverdueDetails(LoanChargeOverdueDetails chargeOverdueDetails){
        this.gracePeriod = chargeOverdueDetails.gracePeriod;
        this.penaltyFreePeriod = chargeOverdueDetails.penaltyFreePeriod;
        this.graceType = chargeOverdueDetails.graceType;
        this.applyChargeForBrokenPeriod = chargeOverdueDetails.applyChargeForBrokenPeriod;
        this.isBasedOnOriginalSchedule = chargeOverdueDetails.isBasedOnOriginalSchedule;
        this.considerOnlyPostedInterest = chargeOverdueDetails.considerOnlyPostedInterest;
        this.calculateChargeOnCurrentOverdue = chargeOverdueDetails.calculateChargeOnCurrentOverdue;
        this.stopChargeOnNPA = chargeOverdueDetails.stopChargeOnNPA;
        this.minOverdueAmountRequired = chargeOverdueDetails.minOverdueAmountRequired;
        this.lastAppliedOnDate = chargeOverdueDetails.lastAppliedOnDate;
        this.lastRunOnDate = chargeOverdueDetails.lastRunOnDate;
    }
    
    public static LoanChargeOverdueDetails copyFrom(final LoanChargeOverdueDetails chargeOverdueDetails){
     return new    LoanChargeOverdueDetails(chargeOverdueDetails);
    }

    public LoanRecurringCharge getLoanRecurringCharge() {
        return this.loanRecurringCharge;
    }

    public Integer getGracePeriod() {
        return this.gracePeriod;
    }

    public Integer getPenaltyFreePeriod() {
        return this.penaltyFreePeriod;
    }

    public PenaltyGraceType getGraceType() {
        return PenaltyGraceType.fromInt(this.graceType);
    }

    public boolean isApplyChargeForBrokenPeriod() {
        return this.applyChargeForBrokenPeriod;
    }

    public boolean isBasedOnOriginalSchedule() {
        return this.isBasedOnOriginalSchedule;
    }

    public boolean isConsiderOnlyPostedInterest() {
        return this.considerOnlyPostedInterest;
    }

    public boolean isCalculateChargeOnCurrentOverdue() {
        return this.calculateChargeOnCurrentOverdue;
    }

    public LocalDate getLastAppliedOnDate() {
        return this.lastAppliedOnDate == null ? null : new LocalDate(this.lastAppliedOnDate);
    }

    public Money getMinOverdueAmountRequired(final MonetaryCurrency currency) {
        return Money.of(currency, this.minOverdueAmountRequired);
    }

    public void setLastAppliedOnDate(Date lastAppliedOnDate) {
        this.lastAppliedOnDate = lastAppliedOnDate;
    }

    public void resetToOriginal() {
        this.lastAppliedOnDate = null;
        this.lastRunOnDate = null;
    }

    public LocalDate getLastRunOnDate() {
        return this.lastRunOnDate == null ? null : new LocalDate(this.lastRunOnDate);
    }

    public void setLastRunOnDate(Date lastRunOnDate) {
        this.lastRunOnDate = lastRunOnDate;
    }

    
    public boolean isStopChargeOnNPA() {
        return this.stopChargeOnNPA;
    }

}
