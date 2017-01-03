package com.finflux.loanapplicationreference.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanApplicationReferenceRepository extends JpaRepository<LoanApplicationReference, Long>,
        JpaSpecificationExecutor<LoanApplicationReference> {

    @Query("from LoanApplicationReference loan where loan.client.id = :clientId")
    List<LoanApplicationReference> findLoanByClientId(@Param("clientId") Long clientId);

    @Query("SELECT lar FROM LoanApplicationReference lar WHERE lar.loan.id = :loanId")
    LoanApplicationReference findOneByLoanId(@Param("loanId") Long loanId);
    
    @Query("from LoanApplicationReference loan where loan.client.id = :clientId and loan.group.id = :groupId")
    List<LoanApplicationReference> findAllByClientIdAndGroupId(@Param("clientId") Long clientId, @Param("groupId") Long groupId);
}