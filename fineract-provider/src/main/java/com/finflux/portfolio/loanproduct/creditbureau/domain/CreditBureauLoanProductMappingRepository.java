package com.finflux.portfolio.loanproduct.creditbureau.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditBureauLoanProductMappingRepository extends JpaRepository<CreditBureauLoanProductMapping, Long>,
        JpaSpecificationExecutor<CreditBureauLoanProductMapping> {

}
