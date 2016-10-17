package com.finflux.ruleengine.eligibility.data;

import com.finflux.ruleengine.lib.data.*;

import java.util.List;

/**
 * Created by dhirendra on 16/09/16.
 */
public class LoanProductEligibilityCriteriaData {

    private Double minAmount;
    private Double maxAmount;

    private Long riskCriteriaId;
    private String riskCriteriaName;

    private ExpressionNode approvalLogic;

    private ExpressionNode rejectionLogic;

    public LoanProductEligibilityCriteriaData(Double minAmount, Double maxAmount, Long riskCriteriaId, String riskCriteriaName,
                                              ExpressionNode approvalLogic, ExpressionNode rejectionLogic) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.riskCriteriaId = riskCriteriaId;
        this.riskCriteriaName = riskCriteriaName;
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

    public String getRiskCriteriaName() {
        return riskCriteriaName;
    }

    public void setRiskCriteriaName(String riskCriteriaName) {
        this.riskCriteriaName = riskCriteriaName;
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
