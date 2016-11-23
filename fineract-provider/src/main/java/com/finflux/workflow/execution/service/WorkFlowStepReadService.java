package com.finflux.workflow.execution.service;

import java.util.List;

import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;

import com.finflux.workflow.execution.data.WorkFlowStepActionData;

public interface WorkFlowStepReadService {

    List<LoanProductData> retrieveLoanProductWorkFlowSummary(final Long loanProductId, final Long officeId);

    List<WorkFlowStepActionData> retrieveWorkFlowStepActions(final String filterBy);
}