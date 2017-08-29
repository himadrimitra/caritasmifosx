package com.finflux.loanapplicationreference.data;

import java.util.Collection;

import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

import com.finflux.fingerprint.data.FingerPrintData;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;

@SuppressWarnings("unused")
public class LoanApplicationReferenceTemplateData {

    // template
    private final Collection<LoanProductData> productOptions;
    private final Collection<PaymentTypeData> paymentOptions;
    private final Collection<TransactionAuthenticationData> transactionAuthenticationOptions;
    private final Collection<FingerPrintData> fingerPrintData;
    private final Boolean isWorkflowEnabled;

    private LoanApplicationReferenceTemplateData(final Collection<LoanProductData> productOptions,
            final Collection<PaymentTypeData> paymentOptions, final Collection<TransactionAuthenticationData> transactionAuthenticationOptions,
            final Collection<FingerPrintData> fingerPrintData, final Boolean isWorkflowEnabled) {
        this.productOptions = productOptions;
        this.paymentOptions = paymentOptions;
        this.transactionAuthenticationOptions = transactionAuthenticationOptions;
        this.fingerPrintData = fingerPrintData;
        this.isWorkflowEnabled = isWorkflowEnabled;
    }

    public static LoanApplicationReferenceTemplateData template(final Collection<LoanProductData> productOptions,
            final Collection<PaymentTypeData> paymentOptions, final Boolean isWorkflowEnabled) {
        final Collection<TransactionAuthenticationData> transactionAuthenticationOptions = null;
        final Collection<FingerPrintData> fingerPrintData = null;
        return new LoanApplicationReferenceTemplateData(productOptions, paymentOptions, transactionAuthenticationOptions, fingerPrintData,
                isWorkflowEnabled);
    }

    public static LoanApplicationReferenceTemplateData template(final Collection<LoanProductData> productOptions,
            final Collection<PaymentTypeData> paymentOptions,
            final Collection<TransactionAuthenticationData> transactionAuthenticationOptions,
            final Collection<FingerPrintData> fingerPrintData, final Boolean isWorkflowEnabled) {
        return new LoanApplicationReferenceTemplateData(productOptions, paymentOptions, transactionAuthenticationOptions, fingerPrintData,
                isWorkflowEnabled);
    }

    public Collection<LoanProductData> getProductOptions() {
        return this.productOptions;
    }

    public Collection<PaymentTypeData> getPaymentOptions() {
        return this.paymentOptions;
    }
}
