package com.finflux.loanapplicationreference.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanApplicationReferenceRepository extends JpaRepository<LoanApplicationReference, Long>, JpaSpecificationExecutor<LoanApplicationReference> {
	
	@Query("from LoanApplicationReference loan where loan.client.id = :clientId")
    List<LoanApplicationReference> findLoanByClientId(@Param("clientId") Long clientId);

}