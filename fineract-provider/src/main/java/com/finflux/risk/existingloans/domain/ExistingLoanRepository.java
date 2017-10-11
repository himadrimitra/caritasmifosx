package com.finflux.risk.existingloans.domain;

import java.util.List;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;

@Service
public interface ExistingLoanRepository extends JpaRepository<ExistingLoan, Long>, JpaSpecificationExecutor<ExistingLoan> {

    List<ExistingLoan> findByLoanApplicationIdAndSource(Long loanApplicationId, CodeValue source);

    List<ExistingLoan> findByCreditBureauProductAndLoanApplicationIdAndSourceAndLoanCreditBureauEnquiryId(
            CreditBureauProduct creditBureauProduct, Long loanApplicationId, CodeValue source, Long loanCreditBureauEnquiryId);

    List<ExistingLoan> findByLoanApplicationIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(Long loanApplicationId,
            CodeValue source, CreditBureauProduct creditBureauProduct, Long loanCreditBureauEnquiryId);

    List<ExistingLoan> findByLoanApplicationIdAndLoanIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(Long loanApplicationId,
            Long loanId, CodeValue source, CreditBureauProduct creditBureauProduct, Long loanCreditBureauEnquiryId);
}