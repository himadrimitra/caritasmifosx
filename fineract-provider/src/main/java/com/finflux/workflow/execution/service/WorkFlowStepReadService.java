package com.finflux.workflow.execution.service;

import java.util.List;

import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;

public interface WorkFlowStepReadService {

    List<LoanProductData> retrieveLoanProductWorkFlowSummary(final Long loanProductId, final Long officeId);
}