package com.finflux.portfolio.loanproduct.creditbureau.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditBureauLoanProductMappingRepository extends JpaRepository<CreditBureauLoanProductMapping, Long>,
        JpaSpecificationExecutor<CreditBureauLoanProductMapping> {

    @Query("from CreditBureauLoanProductMapping cbm where cbm.loanProduct.id = :loanProductId")
    public CreditBureauLoanProductMapping findWithLoanProductId(@Param("loanProductId") Long loanProductId);
}
