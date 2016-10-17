package com.finflux.ruleengine.eligibility.form;

import com.finflux.ruleengine.lib.data.ExpressionNode;

/**
 * Created by dhirendra on 19/09/16.
 */
public class LoanProductEligibilityCriteriaForm {
    private Double minAmount;
    private Double maxAmount;
    private Long riskCriteriaId;
    private ExpressionNode approvalLogic;
    private ExpressionNode rejectionLogic;

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

    public ExpressionNode getApprovalLogic() {
        return approvalLogic;
    }

    public void setApprovalLogic(ExpressionNode approvalLogic) {
        this.approvalLogic = approvalLogic;
    }

    public ExpressionNode getRejectionLogic() {
        return rejectionLogic;
    }

    public void setRejectionLogic(ExpressionNode rejectionLogic) {
        this.rejectionLogic = rejectionLogic;
    }
}
