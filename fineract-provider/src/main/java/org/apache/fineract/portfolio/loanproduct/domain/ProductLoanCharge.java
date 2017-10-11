package org.apache.fineract.portfolio.loanproduct.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.charge.domain.Charge;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_product_loan_charge")
public class ProductLoanCharge extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_loan_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "charge_id", nullable = false)
    private Long chargeId;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    protected ProductLoanCharge() {}

    private ProductLoanCharge(final LoanProduct loanProduct, final Charge charge, final Boolean isMandatory) {
        this.loanProduct = loanProduct;
        this.chargeId = charge.getId();
        this.isMandatory = isMandatory;
    }

    public static ProductLoanCharge create(final LoanProduct loanProduct, final Charge charge, final Boolean isMandatory) {
        return new ProductLoanCharge(loanProduct, charge, isMandatory);
    }
}
