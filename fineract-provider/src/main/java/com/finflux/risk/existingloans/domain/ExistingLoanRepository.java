package com.finflux.risk.existingloans.domain;

import java.util.List;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public interface ExistingLoanRepository extends JpaRepository<ExistingLoan, Long>, JpaSpecificationExecutor<ExistingLoan> {

    List<ExistingLoan> findByLoanApplicationIdAndSourceCvId(Long loanApplicationId, CodeValue sourceCvId);
}