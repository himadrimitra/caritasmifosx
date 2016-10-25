package com.finflux.workflow.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanProductWorkflowRepository extends JpaRepository<LoanProductWorkflow, Long>,
        JpaSpecificationExecutor<LoanProductWorkflow> {

    LoanProductWorkflow findOneByLoanProductId(Long loanProductId);

}
