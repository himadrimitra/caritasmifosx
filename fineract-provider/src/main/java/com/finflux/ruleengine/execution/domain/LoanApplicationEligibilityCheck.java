package com.finflux.ruleengine.execution.domain;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "f_loan_application_eligibility_check")
public class LoanApplicationEligibilityCheck extends AbstractPersistable< Long> {


    @Column(name = "loan_application_id", nullable = false)
    private Long loanApplicationId;//Criteria,Dimension,Factor


    @Column(name = "eligibility_status", nullable = false)
    private Integer eligibilityStatus;

    @Column(name = "eligibility_result", nullable = false)
    private String eligibilityResult;

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

    public LoanApplicationEligibilityCheck() {}

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

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(Integer eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getEligibilityResult() {
        return eligibilityResult;
    }

    public void setEligibilityResult(String eligibilityResult) {
        this.eligibilityResult = eligibilityResult;
    }
}