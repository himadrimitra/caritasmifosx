package com.finflux.ruleengine.eligibility.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_loan_product_eligibility_criteria")
public class LoanProductEligibilityCriteria extends AbstractPersistable< Long> {

    @Column(name = "min_amount", nullable = false)
    private Double minAmount;

    @Column(name = "max_amount", nullable = false)
    private Double maxAmount;

    @Column(name = "risk_criteria_id", nullable = false)
    private Long riskCriteriaId;

    @Column(name = "approval_logic", length = 512)
    private String approvalLogic;

    @Column(name = "rejection_logic", length = 512)
    private String rejectionLogic;

    public LoanProductEligibilityCriteria() {}

    public LoanProductEligibilityCriteria( Double minAmount, Double maxAmount, Long riskCriteriaId, String approvalLogic, String rejectionLogic) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.riskCriteriaId = riskCriteriaId;
        this.approvalLogic = approvalLogic;
        this.rejectionLogic = rejectionLogic;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Long getRiskCriteriaId() {
        return riskCriteriaId;
    }

    public void setRiskCriteriaId(Long riskCriteriaId) {
        this.riskCriteriaId = riskCriteriaId;
    }

    public String getApprovalLogic() {
        return approvalLogic;
    }

    public void setApprovalLogic(String approvalLogic) {
        this.approvalLogic = approvalLogic;
    }

    public String getRejectionLogic() {
        return rejectionLogic;
    }

    public void setRejectionLogic(String rejectionLogic) {
        this.rejectionLogic = rejectionLogic;
    }
}