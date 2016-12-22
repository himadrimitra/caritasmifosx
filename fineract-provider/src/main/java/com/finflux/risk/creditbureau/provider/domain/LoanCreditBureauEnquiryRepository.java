package com.finflux.risk.creditbureau.provider.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanCreditBureauEnquiryRepository extends JpaRepository<LoanCreditBureauEnquiry, Long> {

    @Query("from LoanCreditBureauEnquiry ccrd where ccrd.creditBureauEnquiry.id = :enquiryId")
    List<LoanCreditBureauEnquiry> findWithEnquiryId(@Param("enquiryId") Long enquiryId);

    @Query("from LoanCreditBureauEnquiry ccrd where ccrd.loanId = :loanId and ccrd.creditBureauEnquiry.status = :status")
    List<LoanCreditBureauEnquiry> findWithLoanIdAndCreditBureauEnquiryStatusOrderByCreditBureauEnquiryIdDesc(@Param("loanId") Long loanId,
            @Param("status") Long status);

    @Query("from LoanCreditBureauEnquiry ccrd where ccrd.loanApplicationId = :loanApplicationId and ccrd.creditBureauEnquiry.status = :status")
    List<LoanCreditBureauEnquiry> findWithLoanApplicationId(@Param("loanApplicationId") Long loanApplicationId);

    LoanCreditBureauEnquiry findOneByLoanApplicationIdAndLoanIdAndTrancheDisbursalId(Long loanApplicationId, Long loanId,
            Long trancheDisbursalId);

}