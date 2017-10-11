package com.finflux.vouchers.data;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

public class VoucherTemplateData {

    private final Collection<OfficeData> officeOptions;
    private final Collection<CurrencyData> currencyOptions;
    private final List<GLAccountData> debitAccountingOptions;
    private final List<GLAccountData> creditAccountingOptions;
    private final Collection<EnumOptionData> voucherTypeOptions;
    private final Collection<PaymentTypeData> paymentOptions;

    private VoucherTemplateData(final Collection<OfficeData> officeOptions, final Collection<CurrencyData> currencyOptions,
            final List<GLAccountData> debitAccountingOptions, final List<GLAccountData> creditAccountingOptions,
            final Collection<EnumOptionData> voucherTypeOptions, final Collection<PaymentTypeData> paymentOptions) {
        this.officeOptions = officeOptions;
        this.currencyOptions = currencyOptions;
        this.debitAccountingOptions = debitAccountingOptions;
        this.creditAccountingOptions = creditAccountingOptions;
        this.voucherTypeOptions = voucherTypeOptions;
        this.paymentOptions = paymentOptions;
    }

    public static VoucherTemplateData template(final Collection<OfficeData> officeOptions, final Collection<CurrencyData> currencyOptions,
            final List<GLAccountData> debitAccountingOptions, final List<GLAccountData> creditAccountingOptions,
            final Collection<EnumOptionData> voucherTypeOptions, final Collection<PaymentTypeData> paymentOptions) {
        return new VoucherTemplateData(officeOptions, currencyOptions, debitAccountingOptions, creditAccountingOptions, voucherTypeOptions,paymentOptions);
    }

    public static VoucherTemplateData accountOptions(final List<GLAccountData> debitAccountingOptions, final List<GLAccountData> creditAccountingOptions,
            final Collection<PaymentTypeData> paymentOptions) {
        final Collection<OfficeData> officeOptions = null ;
        final Collection<CurrencyData> currencyOptions = null ;
        final Collection<EnumOptionData> voucherTypeOptions = null ;
        return new VoucherTemplateData(officeOptions, currencyOptions, debitAccountingOptions, creditAccountingOptions, voucherTypeOptions, paymentOptions) ;
    }
    public Collection<OfficeData> getOfficeOptions() {
        return this.officeOptions;
    }

    public Collection<CurrencyData> getCurrencyOptions() {
        return this.currencyOptions;
    }

    public List<GLAccountData> getDebitAccountingOptions() {
        return this.debitAccountingOptions;
    }

    public List<GLAccountData> getCreditAccountingOptions() {
        return this.creditAccountingOptions;
    }

    public Collection<EnumOptionData> getVoucherTypeOptions() {
        return this.voucherTypeOptions;
    }
    
    public Collection<PaymentTypeData> getPaymentOptions() {
        return this.paymentOptions ;
    }
}