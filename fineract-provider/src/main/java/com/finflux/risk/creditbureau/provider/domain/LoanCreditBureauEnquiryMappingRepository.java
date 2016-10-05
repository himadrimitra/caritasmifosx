package com.finflux.risk.creditbureau.provider.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanCreditBureauEnquiryMappingRepository extends JpaRepository<LoanCreditBureauEnquiryMapping, Long> {

    @Query("from LoanCreditBureauEnquiryMapping ccrd where ccrd.creditBureauEnquiry.id = :enquiryId")
    List<LoanCreditBureauEnquiryMapping> findWithEnquiryId(@Param("enquiryId") Long enquiryId);

    @Query("from LoanCreditBureauEnquiryMapping ccrd where ccrd.loanId = :loanId and ccrd.creditBureauEnquiry.status = :status")
    List<LoanCreditBureauEnquiryMapping> findWithLoanIdAndCreditBureauEnquiryStatusOrderByCreditBureauEnquiryIdDesc(@Param("loanId") Long loanId,@Param("status") Long status);

    @Query("from LoanCreditBureauEnquiryMapping ccrd where ccrd.loanApplicationId = :loanApplicationId and ccrd.creditBureauEnquiry.status = :status")
    List<LoanCreditBureauEnquiryMapping> findWithLoanApplicationId(@Param("loanApplicationId") Long loanApplicationId);

}
