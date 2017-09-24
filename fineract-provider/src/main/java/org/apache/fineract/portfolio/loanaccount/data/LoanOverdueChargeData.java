package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class LoanOverdueChargeData {

    private BigDecimal penaltyPostedAsOnDate;
    private BigDecimal penaltyToBePostedAsOnDate;
    private LocalDate penaltyCalculatedOnDate;

    public LoanOverdueChargeData(BigDecimal penaltyPostedAsOnDate, final BigDecimal penaltyToBePostedAsOnDate,
            final LocalDate penaltyCalculatedOnDate) {
        this.penaltyCalculatedOnDate = penaltyCalculatedOnDate;
        this.penaltyPostedAsOnDate = penaltyPostedAsOnDate;
        this.penaltyToBePostedAsOnDate = penaltyToBePostedAsOnDate;
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

}
