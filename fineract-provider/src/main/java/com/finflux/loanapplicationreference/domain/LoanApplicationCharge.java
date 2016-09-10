package com.finflux.loanapplicationreference.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.charge.domain.Charge;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_application_charge")
public class LoanApplicationCharge extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_app_ref_id", referencedColumnName = "id", nullable = false)
    private LoanApplicationReference loanApplicationReference;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_id", referencedColumnName = "id", nullable = false)
    private Charge charge;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_for_collection_as_of_date")
    private Date dueDate;

    @Column(name = "charge_amount_or_percentage", scale = 6, precision = 19, nullable = false)
    private BigDecimal amountOrPercentage;
    
    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    protected LoanApplicationCharge() {}

    LoanApplicationCharge(final LoanApplicationReference loanApplicationReference, final Charge charge, final Date dueDate,
            final BigDecimal amountOrPercentage, final Boolean isMandatory) {
        this.loanApplicationReference = loanApplicationReference;
        this.charge = charge;
        this.dueDate = dueDate;
        this.amountOrPercentage = amountOrPercentage;
        this.isMandatory = isMandatory;
    }

    public static LoanApplicationCharge create(final LoanApplicationReference loanApplicationReference, final Charge charge,
            final Date dueDate, final BigDecimal amountOrPercentage, final Boolean isMandatory) {
        return new LoanApplicationCharge(loanApplicationReference, charge, dueDate, amountOrPercentage, isMandatory);
    }

    public void update(final LoanApplicationReference loanApplicationReference, final Charge charge, final Date dueDate,
            final BigDecimal amountOrPercentage, final Boolean isMandatory) {
        this.loanApplicationReference = loanApplicationReference;
        this.charge = charge;
        this.dueDate = dueDate;
        this.amountOrPercentage = amountOrPercentage;
        this.isMandatory = isMandatory;
    }
}
