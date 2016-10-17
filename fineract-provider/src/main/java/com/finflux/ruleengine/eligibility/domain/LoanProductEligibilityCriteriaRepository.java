package com.finflux.ruleengine.eligibility.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanProductEligibilityCriteriaRepository extends JpaRepository<LoanProductEligibilityCriteria, Long>,
        JpaSpecificationExecutor<LoanProductEligibilityCriteria> {

}