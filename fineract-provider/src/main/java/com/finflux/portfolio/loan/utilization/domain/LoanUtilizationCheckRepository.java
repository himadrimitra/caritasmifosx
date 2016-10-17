package com.finflux.portfolio.loan.utilization.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanUtilizationCheckRepository extends JpaRepository<LoanUtilizationCheck, Long>,
        JpaSpecificationExecutor<LoanUtilizationCheck> {

}