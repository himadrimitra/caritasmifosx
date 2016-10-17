package com.finflux.ruleengine.execution.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanApplicationEligibilityCheckRepository extends JpaRepository<LoanApplicationEligibilityCheck, Long>, JpaSpecificationExecutor<LoanApplicationEligibilityCheck> {

    LoanApplicationEligibilityCheck findOneByLoanApplicationId(Long loanApplicationId);
}