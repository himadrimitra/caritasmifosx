package com.finflux.risk.existingloans.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
@Service
public interface ExistingLoanRepository  extends JpaRepository<ExistingLoan, Long>, JpaSpecificationExecutor<ExistingLoan> {
    // no added behaviour{

}
