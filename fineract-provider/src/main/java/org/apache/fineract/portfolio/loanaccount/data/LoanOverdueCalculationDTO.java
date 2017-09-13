package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.joda.time.LocalDate;

public class LoanOverdueCalculationDTO {

    Long loanId;
    Money principalOutstingAsOnDate;
    Money interestOutstingAsOnDate;
    Money chargeOutstingAsOnDate;
    Money principalPaidAfterOnDate;
    Money interestPaidAfterOnDate;
    Money chargePaidAfterOnDate;
    Money onlyInterestPeriodPaidAmount;
    Money onlyInterestPeriodOverdueAmount;
    Money onlyFeePeriodPaidAmount;
    Money onlyFeePeriodOverdueAmount;
    final LocalDate runOnDate;
    LocalDate interestOnlyPeriodStartDate;
    LocalDate firstOverdueDate;
    LocalDate applyChargeFromDate;
    Map<LocalDate,LoanTransaction> paymentTransactions = new HashMap<>();;
    MonetaryCurrency currency;
    List<LoanRepaymentScheduleInstallment> installments;
    Map<LocalDate,LoanRepaymentScheduleInstallment> overdueInstallments = new HashMap<>();
    Set<LocalDate> datesForOverdueAmountChange = new TreeSet<>(Collections.reverseOrder());

    public LoanOverdueCalculationDTO(final Long loanId,final LocalDate runOnDate, MonetaryCurrency currency, List<LoanTransaction> paymentTransactions,
            List<LoanRepaymentScheduleInstallment> installments) {
        this.loanId = loanId;
        this.principalOutstingAsOnDate = Money.zero(currency);
        this.interestOutstingAsOnDate = Money.zero(currency);
        this.chargeOutstingAsOnDate = Money.zero(currency);
        this.principalPaidAfterOnDate = Money.zero(currency);
        this.interestPaidAfterOnDate = Money.zero(currency);
        this.chargePaidAfterOnDate = Money.zero(currency);
        this.onlyInterestPeriodPaidAmount = Money.zero(currency);
        this.onlyInterestPeriodOverdueAmount = Money.zero(currency);
        this.onlyFeePeriodPaidAmount = Money.zero(currency);
        this.onlyFeePeriodOverdueAmount = Money.zero(currency);
        this.runOnDate = runOnDate;
        this.interestOnlyPeriodStartDate = runOnDate;
        this.firstOverdueDate = runOnDate;
        this.currency = currency;
        this.installments = installments;
        for(LoanTransaction transaction : paymentTransactions){
            this.paymentTransactions.put(transaction.getTransactionDate(), transaction);
        }
    }

    public Money getPrincipalOutstingAsOnDate() {
        return this.principalOutstingAsOnDate;
    }

    public void setPrincipalOutstingAsOnDate(Money principalOutstingAsOnDate) {
        this.principalOutstingAsOnDate = principalOutstingAsOnDate;
    }

    public void plusPrincipalOutstingAsOnDate(Money principalOutstingAsOnDate) {
        this.principalOutstingAsOnDate = this.principalOutstingAsOnDate.plus(principalOutstingAsOnDate);
    }

    public void minusPrincipalOutstingAsOnDate(Money principalOutstingAsOnDate) {
        this.principalOutstingAsOnDate = this.principalOutstingAsOnDate.minus(principalOutstingAsOnDate);
    }

    public Money getInterestOutstingAsOnDate() {
        return this.interestOutstingAsOnDate;
    }

    public void setInterestOutstingAsOnDate(Money interestOutstingAsOnDate) {
        this.interestOutstingAsOnDate = interestOutstingAsOnDate;
    }

    public void plusInterestOutstingAsOnDate(Money interestOutstingAsOnDate) {
        this.interestOutstingAsOnDate = this.interestOutstingAsOnDate.plus(interestOutstingAsOnDate);
    }

    public void minusInterestOutstingAsOnDate(Money interestOutstingAsOnDate) {
        this.interestOutstingAsOnDate = this.interestOutstingAsOnDate.minus(interestOutstingAsOnDate);
    }

    public Money getChargeOutstingAsOnDate() {
        return this.chargeOutstingAsOnDate;
    }

    public void setChargeOutstingAsOnDate(Money chargeOutstingAsOnDate) {
        this.chargeOutstingAsOnDate = chargeOutstingAsOnDate;
    }

    public void plusChargeOutstingAsOnDate(Money chargeOutstingAsOnDate) {
        this.chargeOutstingAsOnDate = this.chargeOutstingAsOnDate.plus(chargeOutstingAsOnDate);
    }

    public void minusChargeOutstingAsOnDate(Money chargeOutstingAsOnDate) {
        this.chargeOutstingAsOnDate = this.chargeOutstingAsOnDate.minus(chargeOutstingAsOnDate);
    }

    public Money getPrincipalPaidAfterOnDate() {
        return this.principalPaidAfterOnDate;
    }

    public void setPrincipalPaidAfterOnDate(Money principalPaidAfterOnDate) {
        this.principalPaidAfterOnDate = principalPaidAfterOnDate;
    }

    public void plusPrincipalPaidAfterOnDate(Money principalPaidAfterOnDate) {
        this.principalPaidAfterOnDate = this.principalPaidAfterOnDate.plus(principalPaidAfterOnDate);
    }

    public void minusPrincipalPaidAfterOnDate(Money principalPaidAfterOnDate) {
        this.principalPaidAfterOnDate = this.principalPaidAfterOnDate.minus(principalPaidAfterOnDate);
    }

    public void minusPrincipalPaidAfterOnDate(BigDecimal principalPaidAfterOnDate) {
        this.principalPaidAfterOnDate = this.principalPaidAfterOnDate.minus(principalPaidAfterOnDate);
    }

    public void resetPrincipalPaidAfterOnDate() {
        this.principalPaidAfterOnDate = this.principalPaidAfterOnDate.zero();
    }

    public Money getInterestPaidAfterOnDate() {
        return this.interestPaidAfterOnDate;
    }

    public void resetInterestPaidAfterOnDate() {
        this.interestPaidAfterOnDate = this.interestPaidAfterOnDate.zero();
    }

    public void plusInterestPaidAfterOnDate(Money interestPaidAfterOnDate) {
        this.interestPaidAfterOnDate = this.interestPaidAfterOnDate.plus(interestPaidAfterOnDate);
    }

    public void minusInterestPaidAfterOnDate(Money interestPaidAfterOnDate) {
        this.interestPaidAfterOnDate = this.interestPaidAfterOnDate.minus(interestPaidAfterOnDate);
    }

    public void minusInterestPaidAfterOnDate(BigDecimal interestPaidAfterOnDate) {
        this.interestPaidAfterOnDate = this.interestPaidAfterOnDate.minus(interestPaidAfterOnDate);
    }

    public Money getChargePaidAfterOnDate() {
        return this.chargePaidAfterOnDate;
    }

    public void plusChargePaidAfterOnDate(Money chargePaidAfterOnDate) {
        this.chargePaidAfterOnDate = this.chargePaidAfterOnDate.plus(chargePaidAfterOnDate);
    }

    public void resetChargePaidAfterOnDate() {
        this.chargePaidAfterOnDate = this.chargePaidAfterOnDate.zero();
    }

    public void minusChargePaidAfterOnDate(Money chargePaidAfterOnDate) {
        this.chargePaidAfterOnDate = this.chargePaidAfterOnDate.minus(chargePaidAfterOnDate);
    }

    public Money getOnlyInterestPeriodPaidAmount() {
        return this.onlyInterestPeriodPaidAmount;
    }

    public Money getOnlyInterestPeriodOverdueAmount() {
        return this.onlyInterestPeriodOverdueAmount;
    }

    public Money getOnlyFeePeriodPaidAmount() {
        return this.onlyFeePeriodPaidAmount;
    }

    public Money getOnlyFeePeriodOverdueAmount() {
        return this.onlyFeePeriodOverdueAmount;
    }

    public LocalDate getInterestOnlyPeriodStartDate() {
        return this.interestOnlyPeriodStartDate;
    }

    public void setInterestOnlyPeriodStartDate(LocalDate interestOnlyPeriodStartDate) {
        this.interestOnlyPeriodStartDate = interestOnlyPeriodStartDate;
    }

    public LocalDate getFirstOverdueDate() {
        return this.firstOverdueDate;
    }

    public void setFirstOverdueDate(LocalDate firstOverdueDate) {
        this.firstOverdueDate = firstOverdueDate;
    }

    public  Map<LocalDate,LoanTransaction> getPaymentTransactions() {
        return this.paymentTransactions;
    }

    public MonetaryCurrency getCurrency() {
        return this.currency;
    }

    public void setCurrency(MonetaryCurrency currency) {
        this.currency = currency;
    }

    public List<LoanRepaymentScheduleInstallment> getInstallments() {
        return this.installments;
    }

    public void setInstallments(List<LoanRepaymentScheduleInstallment> installments) {
        this.installments = installments;
    }

    public LocalDate getRunOnDate() {
        return this.runOnDate;
    }

    public LocalDate getApplyChargeFromDate() {
        return this.applyChargeFromDate;
    }

    public void setApplyChargeFromDate(LocalDate applyChargeFromDate) {
        this.applyChargeFromDate = applyChargeFromDate;
    }

    
    public Map<LocalDate, LoanRepaymentScheduleInstallment> getOverdueInstallments() {
        return this.overdueInstallments;
    }

    public void createDatesForOverdueChange() {
         this.datesForOverdueAmountChange.addAll(this.paymentTransactions.keySet());
         this.datesForOverdueAmountChange.addAll(this.overdueInstallments.keySet());
    }

    
    public Set<LocalDate> getDatesForOverdueAmountChange() {
        return this.datesForOverdueAmountChange;
    }

    
    public Long getLoanId() {
        return this.loanId;
    }
    
}
