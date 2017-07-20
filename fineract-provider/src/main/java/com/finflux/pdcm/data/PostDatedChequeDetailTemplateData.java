package com.finflux.pdcm.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

public class PostDatedChequeDetailTemplateData {

    private final Collection<EnumOptionData> pdcTypeOptions;
    private final Collection<PaymentTypeData> paymentOptions;
    private final Collection<LoanSchedulePeriodData> loanSchedulePeriods;

    private PostDatedChequeDetailTemplateData(final Collection<EnumOptionData> pdcTypeOptions,
            final Collection<PaymentTypeData> paymentOptions, final Collection<LoanSchedulePeriodData> loanSchedulePeriods) {
        this.pdcTypeOptions = pdcTypeOptions;
        this.paymentOptions = paymentOptions;
        this.loanSchedulePeriods = loanSchedulePeriods;
    }

    public static PostDatedChequeDetailTemplateData template(final Collection<EnumOptionData> pdcTypeOptions,
            final Collection<PaymentTypeData> paymentOptions, final Collection<LoanSchedulePeriodData> loanSchedulePeriods) {
        return new PostDatedChequeDetailTemplateData(pdcTypeOptions, paymentOptions, loanSchedulePeriods);
    }
}