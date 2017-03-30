package com.finflux.risk.creditbureau.provider.data;

import java.util.Date;

public class CreditBureauExistingLoanPaymentDetail {

    private Date date;
    private Integer dpd;

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getDpd() {
        return this.dpd;
    }

    public void setDpd(Integer dpd) {
        this.dpd = dpd;
    }

    public CreditBureauExistingLoanPaymentDetail(final Date date, final Integer dpd) {
        this.date = date;
        this.dpd = dpd;
    }

}
