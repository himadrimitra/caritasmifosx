package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class LoanOverdueChargeData {

    private BigDecimal penaltyPostedAsOnDate;
    private BigDecimal penaltyToBePostedAsOnDate;
    private BigDecimal penaltyPostedTillDate;
    private LocalDate penaltyCalculatedOnDate;
    private LocalDate lastRunOnDate;
    private LocalDate lastChargeAppliedOnDate;
    private boolean canApplyBrokenPeriodChargeAsOnCurrentDate;

    public LoanOverdueChargeData(final LocalDate penaltyCalculatedOnDate) {
        this.penaltyCalculatedOnDate = penaltyCalculatedOnDate;
        this.penaltyPostedAsOnDate = BigDecimal.ZERO;
        this.penaltyToBePostedAsOnDate = BigDecimal.ZERO;
        this.penaltyPostedTillDate = BigDecimal.ZERO;
    }

    public LoanOverdueChargeData(LocalDate lastRunOnDate, LocalDate lastChargeAppliedOnDate,
            boolean canApplyBrokenPeriodChargeAsOnCurrentDate) {
        this.lastRunOnDate = lastRunOnDate;
        this.lastChargeAppliedOnDate = lastChargeAppliedOnDate;
        this.canApplyBrokenPeriodChargeAsOnCurrentDate = canApplyBrokenPeriodChargeAsOnCurrentDate;
    }

    public BigDecimal getPenaltyPostedAsOnDate() {
        return this.penaltyPostedAsOnDate;
    }

    public BigDecimal getPenaltyToBePostedAsOnDate() {
        return this.penaltyToBePostedAsOnDate;
    }

    public LocalDate getPenaltyCalculatedOnDate() {
        return this.penaltyCalculatedOnDate;
    }

    public void setPenaltyPostedAsOnDate(BigDecimal penaltyPostedAsOnDate) {
        this.penaltyPostedAsOnDate = penaltyPostedAsOnDate;
    }

    public void setPenaltyToBePostedAsOnDate(BigDecimal penaltyToBePostedAsOnDate) {
        this.penaltyToBePostedAsOnDate = penaltyToBePostedAsOnDate;
    }

    public LocalDate getLastRunOnDate() {
        return this.lastRunOnDate;
    }

    public void setLastRunOnDate(LocalDate lastRunOnDate) {
        this.lastRunOnDate = lastRunOnDate;
    }

    public LocalDate getLastChargeAppliedOnDate() {
        return this.lastChargeAppliedOnDate;
    }

    public void setLastChargeAppliedOnDate(LocalDate lastChargeAppliedOnDate) {
        this.lastChargeAppliedOnDate = lastChargeAppliedOnDate;
    }

    public void setPenaltyCalculatedOnDate(LocalDate penaltyCalculatedOnDate) {
        this.penaltyCalculatedOnDate = penaltyCalculatedOnDate;
    }

    public boolean isCanApplyBrokenPeriodChargeAsOnCurrentDate() {
        return this.canApplyBrokenPeriodChargeAsOnCurrentDate;
    }

    public void setCanApplyBrokenPeriodChargeAsOnCurrentDate(boolean canApplyBrokenPeriodChargeAsOnCurrentDate) {
        this.canApplyBrokenPeriodChargeAsOnCurrentDate = canApplyBrokenPeriodChargeAsOnCurrentDate;
    }

    
    public BigDecimal getPenaltyPostedTillDate() {
        return this.penaltyPostedTillDate;
    }

    
    public void setPenaltyPostedTillDate(BigDecimal penaltyPostedTillDate) {
        this.penaltyPostedTillDate = penaltyPostedTillDate;
    }

}