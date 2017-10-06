package org.apache.fineract.portfolio.loanproduct.data;

import org.apache.fineract.portfolio.charge.data.ChargeData;

public class ProductLoanChargeData {

    private final Long id;
    private final Long productLoanId;
    private final ChargeData chargeData;
    private final Boolean isMandatory;
    private final Boolean isAmountNonEditable;

    private ProductLoanChargeData(final Long id, final Long productLoanId, final ChargeData chargeData, final Boolean isMandatory,
    		final Boolean isAmountNonEditable) {
        this.id = id;
        this.productLoanId = productLoanId;
        this.chargeData = chargeData;
        this.isMandatory = isMandatory;
        this.isAmountNonEditable = isAmountNonEditable;
    }

    public static ProductLoanChargeData instance(final Long id, final Long productLoanId, final ChargeData chargeData,
            final Boolean isMandatory, final Boolean isAmountNonEditable) {
        return new ProductLoanChargeData(id, productLoanId, chargeData, isMandatory, isAmountNonEditable);
    }

    public ChargeData chargeData() {
        return this.chargeData;
    }
}
