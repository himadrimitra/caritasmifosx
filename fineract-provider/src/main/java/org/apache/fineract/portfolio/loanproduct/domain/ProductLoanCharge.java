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

    
    @Column(name = "is_amount_non_editable", nullable = false)
    private Boolean isAmountNonEditable = false;
    
    protected ProductLoanCharge() {}

    private ProductLoanCharge(final LoanProduct loanProduct, final Charge charge, final Boolean isMandatory, final Boolean isAmountNonEditable) {
        this.loanProduct = loanProduct;
        this.chargeId = charge.getId();
        this.isMandatory = isMandatory;
        if (isAmountNonEditable != null) {
            this.isAmountNonEditable = isAmountNonEditable;
        }
       
    }

    public static ProductLoanCharge create(final LoanProduct loanProduct, final Charge charge, final Boolean isMandatory, final Boolean isAmountNonEditable) {
        return new ProductLoanCharge(loanProduct, charge, isMandatory, isAmountNonEditable);
    }
}
