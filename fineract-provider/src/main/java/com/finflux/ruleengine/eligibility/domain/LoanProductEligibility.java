package com.finflux.ruleengine.eligibility.domain;

import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "f_loan_product_eligibility")
public class LoanProductEligibility extends AbstractPersistable< Long> {

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "loan_product_eligibility_id", referencedColumnName="id", nullable = false)
    List<LoanProductEligibilityCriteria> eligibilityCriterias = new ArrayList<>();

    @Column(name = "loan_product_id", nullable = false)
    private Long loanProductId;//Criteria,Dimension,Factor


    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private AppUser updatedBy;

    @Column(name = "created_on_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    public LoanProductEligibility() {}

    public Long getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Long loanProductId) {
        this.loanProductId = loanProductId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<LoanProductEligibilityCriteria> getEligibilityCriterias() {
        return eligibilityCriterias;
    }

    public void setEligibilityCriterias(List<LoanProductEligibilityCriteria> eligibilityCriterias) {
        if(this.eligibilityCriterias == null){
            this.eligibilityCriterias = new ArrayList<>();
        }else {
            this.eligibilityCriterias.clear();
        }
        if (eligibilityCriterias != null) {
            this.eligibilityCriterias.addAll(eligibilityCriterias);
        }
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    public AppUser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(AppUser updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }
}