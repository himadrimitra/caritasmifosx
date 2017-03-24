package com.finflux.risk.existingloans.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_existing_loan_cb_payment_details")
public class CreditBureauPaymentDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "existing_loan_id")
    private ExistingLoan existingLoan;

    @Column(name = "date")
    private Date date;

    @Column(name = "dpd", nullable = false)
    private Integer dpd;

    protected CreditBureauPaymentDetails() {}

    private CreditBureauPaymentDetails(final ExistingLoan existingLoan, final Date date, final int dpd) {
        this.existingLoan = existingLoan;
        this.date = date;
        this.dpd = dpd;
    }

    public static CreditBureauPaymentDetails create(final ExistingLoan existingLoan, final Date date, final int dpd) {
        return new CreditBureauPaymentDetails(existingLoan, date, dpd);
    }
}
