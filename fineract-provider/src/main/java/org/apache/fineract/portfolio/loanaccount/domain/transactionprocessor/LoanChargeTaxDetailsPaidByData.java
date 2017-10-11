package org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.tax.data.TaxComponentData;

public class LoanChargeTaxDetailsPaidByData {

    private final TaxComponentData taxComponentData;
    private final BigDecimal amount;

    private LoanChargeTaxDetailsPaidByData(final TaxComponentData taxComponentData, final BigDecimal amount) {
        this.taxComponentData = taxComponentData;
        this.amount = amount;
    }

    public static LoanChargeTaxDetailsPaidByData instance(final TaxComponentData taxComponentData, final BigDecimal amount) {
        return new LoanChargeTaxDetailsPaidByData(taxComponentData, amount);
    }

    public TaxComponentData getTaxComponentData() {
        return this.taxComponentData;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}
