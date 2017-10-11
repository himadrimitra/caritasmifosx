package org.apache.fineract.portfolio.loanproduct.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_product_entity_profile_mapping", uniqueConstraints = { @UniqueConstraint(columnNames = { "loan_product_id",
        "profile_type", "value", "value_entity_type" }, name = "f_loan_product_entity_profile_mapping_UNIQUE") })
public class LoanProductEntityProfileMapping extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "profile_type", nullable = false)
    private Integer profileType;

    @Column(name = "value", nullable = false)
    private Long value;

    @Column(name = "value_entity_type", nullable = false)
    private Integer valueEntityType;

    protected LoanProductEntityProfileMapping() {}

    private LoanProductEntityProfileMapping(final LoanProduct loanProduct, final Integer profileType, final Long value,
            final Integer valueEntityType) {
        this.loanProduct = loanProduct;
        this.profileType = profileType;
        this.value = value;
        this.valueEntityType = valueEntityType;
    }

    public static LoanProductEntityProfileMapping create(final LoanProduct loanProduct, final Integer profileType, final Long value,
            final Integer valueEntityType) {
        return new LoanProductEntityProfileMapping(loanProduct, profileType, value, valueEntityType);
    }

    public Integer getProfileType() {
        return this.profileType;
    }

    public Long getValue() {
        return this.value;
    }

    public Integer getValueEntityType() {
        return this.valueEntityType;
    }

}
