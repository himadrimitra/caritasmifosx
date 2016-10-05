package com.finflux.portfolio.loanproduct.creditbureau.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;

@Entity
@Table(name = "f_creditbureau_loanproduct_mapping")
public class CreditBureauLoanProductMapping extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "creditbureau_product_id")
    private CreditBureauProduct creditBureauProduct;

    @OneToOne
    @JoinColumn(name = "loan_product_id")
    private LoanProduct loanProduct;

    @Column(name = "is_creditcheck_mandatory")
    private Boolean isCreditcheckMandatory;

    @Column(name = "skip_creditcheck_in_failure")
    private Boolean skipCreditcheckInFailure;

    @Column(name = "stale_period")
    private Integer stalePeriod;

    @Column(name = "is_active")
    private Boolean isActive;

    protected CreditBureauLoanProductMapping() {

    }

    private CreditBureauLoanProductMapping(final CreditBureauProduct creditBureauProduct, final LoanProduct loanProduct,
            final Boolean isCreditcheckMandatory, final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive) {
        this.creditBureauProduct = creditBureauProduct;
        this.loanProduct = loanProduct;
        this.isCreditcheckMandatory = isCreditcheckMandatory;
        this.skipCreditcheckInFailure = skipCreditcheckInFailure;
        this.stalePeriod = stalePeriod;
        this.isActive = isActive;
    }

    public static CreditBureauLoanProductMapping create(final CreditBureauProduct creditBureauProduct, final LoanProduct loanProduct,
            final Boolean isCreditcheckMandatory, final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive) {
        return new CreditBureauLoanProductMapping(creditBureauProduct, loanProduct, isCreditcheckMandatory, skipCreditcheckInFailure,
                stalePeriod, isActive);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        if (command.isChangeInLongParameterNamed("creditBureauProductId", this.creditBureauProduct.getId())) {
            final Long newValue = command.longValueOfParameterNamed("creditBureauProductId");
            actualChanges.put("creditBureauProductId", newValue);
        }

        if (command.isChangeInLongParameterNamed("loanProductId", this.loanProduct.getId())) {
            final Long newValue = command.longValueOfParameterNamed("loanProductId");
            actualChanges.put("loanProductId", newValue);
        }

        if (command.isChangeInBooleanParameterNamed("isCreditcheckMandatory", this.isCreditcheckMandatory)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed("isCreditcheckMandatory");
            actualChanges.put("isCreditcheckMandatory", newValue);
            this.isCreditcheckMandatory = newValue;
        }

        if (command.isChangeInBooleanParameterNamed("skipCreditcheckInFailure", this.skipCreditcheckInFailure)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed("skipCreditcheckInFailure");
            actualChanges.put("skipCreditcheckInFailure", newValue);
            this.skipCreditcheckInFailure = newValue;
        }

        if (command.isChangeInIntegerParameterNamed("stalePeriod", this.stalePeriod)) {
            final Integer newValue = command.integerValueOfParameterNamed("stalePeriod");
            actualChanges.put("stalePeriod", newValue);
            this.stalePeriod = newValue;
        }

        if (command.isChangeInBooleanParameterNamed("isActive", this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed("isActive");
            actualChanges.put("isActive", newValue);
            this.isActive = newValue;
        }
        return actualChanges;
    }

    public Boolean isActive() {
        return this.isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void inActivate() {
        this.isActive = false;
    }

    public CreditBureauProduct getCreditBureauProduct() {
        return this.creditBureauProduct;
    }

    public void updateCreditBureauProduct(final CreditBureauProduct creditBureauProduct) {
        this.creditBureauProduct = creditBureauProduct;
    }

    public void updateLoanProduct(final LoanProduct loanProduct) {
        this.loanProduct = loanProduct;
    }

    public Integer getStalePeriod() {
        return this.stalePeriod;
    }

}