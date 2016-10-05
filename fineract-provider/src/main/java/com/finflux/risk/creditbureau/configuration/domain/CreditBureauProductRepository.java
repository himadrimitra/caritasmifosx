package com.finflux.risk.creditbureau.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditBureauProductRepository extends JpaRepository<CreditBureauProduct, Long>,
        JpaSpecificationExecutor<CreditBureauProduct> {

    @Query("from CreditBureauEnquiry ccr where ccr.acknowledgementNumber = :acknowledgementNumber")
    public CreditBureauProduct findWithAcknowledgementNumber(@Param("acknowledgementNumber") String acknowledgementNumber);
}