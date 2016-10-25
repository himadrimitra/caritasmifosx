package com.finflux.workflow.execution.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanApplicationWorkflowExecutionRepository extends JpaRepository<LoanApplicationWorkflowExecution, Long>,
        JpaSpecificationExecutor<LoanApplicationWorkflowExecution> {

    LoanApplicationWorkflowExecution findOneByLoanApplicationId(Long loanApplicationId);
    LoanApplicationWorkflowExecution findOneByWorkflowExecutionId(Long workflowExecutionId);
}