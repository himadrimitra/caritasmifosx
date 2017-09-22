package org.apache.fineract.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.common.domain.LoanPeriodFrequencyType;
import org.joda.time.LocalDate;

public class PenaltyPeriod {

    private double percentageOrAmount;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private Money outstanding;
    private LocalDate startDate;
    private LocalDate postingDate;
    private LoanPeriodFrequencyType frequencyType;

    public PenaltyPeriod(double percentage, LocalDate actualStartDate, LocalDate actualEndDate, Money outstanding, LocalDate startDate,
            LocalDate postingDate, LoanPeriodFrequencyType frequencyType) {
        this.percentageOrAmount = percentage;
        this.actualEndDate = actualEndDate;
        this.actualStartDate = actualStartDate;
        this.outstanding = outstanding;
        this.startDate = startDate;
        this.postingDate = postingDate;
        this.frequencyType = frequencyType;
    }

    public BigDecimal calcualteCharge() {

        return null;
    }

    public double getPercentageOrAmount() {
        return this.percentageOrAmount;
    }

    public LocalDate getActualStartDate() {
        return this.actualStartDate;
    }

    public Money getOutstanding() {
        return this.outstanding;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getPostingDate() {
        return this.postingDate;
    }

    public LoanPeriodFrequencyType getFrequencyType() {
        return this.frequencyType;
    }

    public LocalDate getActualEndDate() {
        return this.actualEndDate;
    }

}
