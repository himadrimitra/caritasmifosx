package com.finflux.risk.creditbureau.provider.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditBureauEnquiryRepository extends JpaRepository<CreditBureauEnquiry,Long> {
    
    @Query("from CreditBureauEnquiry ccr where ccr.acknowledgementNumber = :acknowledgementNumber")
    public CreditBureauEnquiry findWithAcknowledgmentNumber(@Param("acknowledgementNumber") String acknowledgementNumber);

}
