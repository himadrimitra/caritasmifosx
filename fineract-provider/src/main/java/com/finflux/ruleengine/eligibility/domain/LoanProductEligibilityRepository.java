package com.finflux.ruleengine.eligibility.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanProductEligibilityRepository extends JpaRepository<LoanProductEligibility, Long>, JpaSpecificationExecutor<LoanProductEligibility> {

    LoanProductEligibility findOneByLoanProductId(Long loanProductId);
}