package com.finflux.portfolio.loanproduct.creditbureau.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.provider.api.CreditBureauApiConstants;

@Entity
@Table(name = "f_creditbureau_loanproduct_mapping")
public class CreditBureauLoanProductMapping extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "creditbureau_product_id")
    private CreditBureauProduct creditBureauProduct;

    @Column(name = "is_creditcheck_mandatory")
    private Boolean isCreditcheckMandatory;

    @Column(name = "skip_creditcheck_in_failure")
    private Boolean skipCreditcheckInFailure;

    @Column(name = "stale_period")
    private Integer stalePeriod;

    @Column(name = "is_active")
    private Boolean isActive;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creditBureauLoanProductMapping", orphanRemoval = true)
    private Set<CreditBureauLoanProductOfficeMapping> creditBureauLoanProductOfficeMapping = new HashSet<>();

    protected CreditBureauLoanProductMapping() {

    }

    private CreditBureauLoanProductMapping(final CreditBureauProduct creditBureauProduct, final Boolean isCreditcheckMandatory,
            final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive) {
        this.creditBureauProduct = creditBureauProduct;
        this.isCreditcheckMandatory = isCreditcheckMandatory;
        this.skipCreditcheckInFailure = skipCreditcheckInFailure;
        this.stalePeriod = stalePeriod;
        this.isActive = isActive;
    }

    public static CreditBureauLoanProductMapping create(final CreditBureauProduct creditBureauProduct, final Boolean isCreditcheckMandatory,
            final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive) {
        return new CreditBureauLoanProductMapping(creditBureauProduct, isCreditcheckMandatory, skipCreditcheckInFailure, stalePeriod,
                isActive);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        if (command.isChangeInLongParameterNamed(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID, this.creditBureauProduct.getId())) {
            final Long newValue = command.longValueOfParameterNamed(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID);
            actualChanges.put(CreditBureauApiConstants.CREDIT_BUREAU_PRODUCTID, newValue);
        }

        if (command.isChangeInBooleanParameterNamed(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY, this.isCreditcheckMandatory)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY);
            actualChanges.put(CreditBureauApiConstants.IS_CREDIT_CHECK_MANDATORY, newValue);
            this.isCreditcheckMandatory = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE, this.skipCreditcheckInFailure)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE);
            actualChanges.put(CreditBureauApiConstants.SKIP_CREDIT_CHECK_IN_FAILURE, newValue);
            this.skipCreditcheckInFailure = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(CreditBureauApiConstants.STALE_PERIOD, this.stalePeriod)) {
            final Integer newValue = command.integerValueOfParameterNamed(CreditBureauApiConstants.STALE_PERIOD);
            actualChanges.put(CreditBureauApiConstants.STALE_PERIOD, newValue);
            this.stalePeriod = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(CreditBureauApiConstants.IS_ACTIVE, this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(CreditBureauApiConstants.IS_ACTIVE);
            actualChanges.put(CreditBureauApiConstants.IS_ACTIVE, newValue);
            this.isActive = newValue;
        }

        if (command.isChangeInArrayParameterNamed(CreditBureauApiConstants.OFFICES, getOfficeIdsAsStringArray())) {
            final String[] newValue = command.arrayValueOfParameterNamed(CreditBureauApiConstants.OFFICES);
            actualChanges.put(CreditBureauApiConstants.OFFICES, newValue);
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

    public Integer getStalePeriod() {
        return this.stalePeriod;
    }

    public Set<CreditBureauLoanProductOfficeMapping> getCreditBureauLoanProductOfficeMapping() {
        return this.creditBureauLoanProductOfficeMapping;
    }

    public void setOffices(final Set<CreditBureauLoanProductOfficeMapping> creditBureauLoanProductOfficeMapping) {
        this.creditBureauLoanProductOfficeMapping = creditBureauLoanProductOfficeMapping;
    }

    private String[] getOfficeIdsAsStringArray() {
        final List<String> officeIds = new ArrayList<>();

        for (final CreditBureauLoanProductOfficeMapping creditBureauLoanProductOfficeMapping : this
                .getCreditBureauLoanProductOfficeMapping()) {
            if (creditBureauLoanProductOfficeMapping.getOfficeId() != null) {
                officeIds.add(creditBureauLoanProductOfficeMapping.getOfficeId().toString());
            }
        }
        return officeIds.toArray(new String[officeIds.size()]);
    }

    public void updateOffices(final Set<CreditBureauLoanProductOfficeMapping> creditBureauLoanProductOfficeMappingList) {
        this.creditBureauLoanProductOfficeMapping.clear();
        if (creditBureauLoanProductOfficeMappingList != null && creditBureauLoanProductOfficeMappingList.size() > 0) {
            this.creditBureauLoanProductOfficeMapping.addAll(creditBureauLoanProductOfficeMappingList);
        }
    }
}