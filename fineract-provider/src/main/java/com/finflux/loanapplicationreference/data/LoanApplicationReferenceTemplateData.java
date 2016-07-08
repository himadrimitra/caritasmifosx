package com.finflux.loanapplicationreference.data;

import java.util.Collection;

import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

public class LoanApplicationReferenceTemplateData {

    // template
    private final Collection<LoanProductData> productOptions;
    private final Collection<PaymentTypeData> paymentOptions;

    private LoanApplicationReferenceTemplateData(final Collection<LoanProductData> productOptions,
            final Collection<PaymentTypeData> paymentOptions) {
        this.productOptions = productOptions;
        this.paymentOptions = paymentOptions;
    }

    public static LoanApplicationReferenceTemplateData template(final Collection<LoanProductData> productOptions,
            final Collection<PaymentTypeData> paymentOptions) {
        return new LoanApplicationReferenceTemplateData(productOptions, paymentOptions);
    }

    public Collection<LoanProductData> getProductOptions() {
        return this.productOptions;
    }

    public Collection<PaymentTypeData> getPaymentOptions() {
        return this.paymentOptions;
    }
}
