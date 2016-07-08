package com.finflux.loanapplicationreference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanApplicationReferenceRepository extends JpaRepository<LoanApplicationReference, Long>, JpaSpecificationExecutor<LoanApplicationReference> {

}