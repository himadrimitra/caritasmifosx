package com.finflux.ruleengine.eligibility.service;

import com.finflux.ruleengine.eligibility.domain.LoanProductEligibility;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibilityCriteria;
import com.finflux.ruleengine.eligibility.form.LoanProductEligibilityCriteriaForm;
import com.finflux.ruleengine.eligibility.form.LoanProductEligibilityForm;
import com.finflux.ruleengine.lib.data.ExpressionNode;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanProductEligibilityDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final ToApiJsonSerializer<ExpressionNode> toApiJsonSerializer;

    @Autowired
    public LoanProductEligibilityDataAssembler(final FromJsonHelper fromApiJsonHelper,
                                               ToApiJsonSerializer<ExpressionNode> toApiJsonSerializer) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    public LoanProductEligibility assembleCreateLoanProductEligibility(final JsonCommand command) {
        LoanProductEligibilityForm form = fromApiJsonHelper.fromJson(command.json(),LoanProductEligibilityForm.class);
        LoanProductEligibility loanProductEligibility = new LoanProductEligibility();
        loanProductEligibility.setActive(form.getActive());
        loanProductEligibility.setLoanProductId(form.getLoanProductId());
        List<LoanProductEligibilityCriteria> criterias = new ArrayList<>();
        populateEligibilityCriterias(form, loanProductEligibility, criterias);
        return loanProductEligibility;
    }

    public void assembleUpdateLoanProductEligibility(LoanProductEligibility loanProductEligibility, JsonCommand command) {
        LoanProductEligibilityForm form = fromApiJsonHelper.fromJson(command.json(),LoanProductEligibilityForm.class);
        loanProductEligibility.setActive(form.getActive());

    }

    private void populateEligibilityCriterias(LoanProductEligibilityForm form, LoanProductEligibility loanProductEligibility, List<LoanProductEligibilityCriteria> criterias) {
        for(LoanProductEligibilityCriteriaForm criteriaForm: form.getCriterias()){
            String approvalLogic = toApiJsonSerializer.serialize(criteriaForm.getApprovalLogic());
            String rejectionLogic = toApiJsonSerializer.serialize(criteriaForm.getRejectionLogic());
            LoanProductEligibilityCriteria criteria = new LoanProductEligibilityCriteria(criteriaForm.getMinAmount(),
                    criteriaForm.getMaxAmount(),criteriaForm.getRiskCriteriaId(),approvalLogic,rejectionLogic);
            criterias.add(criteria);
        }
        loanProductEligibility.setEligibilityCriterias(criterias);
    }

    public List<LoanProductEligibilityCriteria> assembleUpdateLoanProductEligibilityCriterias(LoanProductEligibility loanProductEligibility, JsonCommand command) {
        LoanProductEligibilityForm form = fromApiJsonHelper.fromJson(command.json(),LoanProductEligibilityForm.class);
        List<LoanProductEligibilityCriteria> criterias = new ArrayList<>();
        for(LoanProductEligibilityCriteriaForm criteriaForm: form.getCriterias()){
            String approvalLogic = toApiJsonSerializer.serialize(criteriaForm.getApprovalLogic());
            String rejectionLogic = toApiJsonSerializer.serialize(criteriaForm.getRejectionLogic());
            LoanProductEligibilityCriteria criteria = new LoanProductEligibilityCriteria(criteriaForm.getMinAmount(),
                    criteriaForm.getMaxAmount(),criteriaForm.getRiskCriteriaId(),approvalLogic,rejectionLogic);
            criterias.add(criteria);
        }
        return criterias;
    }
}