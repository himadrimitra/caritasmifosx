package com.finflux.portfolio.loan.purpose.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanPurposeRepository extends JpaRepository<LoanPurpose, Long>, JpaSpecificationExecutor<LoanPurpose> {

}