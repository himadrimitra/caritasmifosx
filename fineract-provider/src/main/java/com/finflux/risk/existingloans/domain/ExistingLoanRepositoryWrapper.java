package com.finflux.risk.existingloans.domain;

import java.util.List;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.existingloans.exception.ExistingLoanNotFoundException;

@Service
public class ExistingLoanRepositoryWrapper {

    private final ExistingLoanRepository repository;

    @Autowired
    public ExistingLoanRepositoryWrapper(final ExistingLoanRepository repository) {
        this.repository = repository;
    }

    public ExistingLoan findOneWithNotFoundDetection(final Long id) {
        final ExistingLoan existingLoan = this.repository.findOne(id);
        if (existingLoan == null) { throw new ExistingLoanNotFoundException(id); }
        return existingLoan;
    }

    public void save(final ExistingLoan existingLoan) {
        this.repository.save(existingLoan);
    }

    public void save(final List<ExistingLoan> existingLoans) {
        this.repository.save(existingLoans);
    }

    public void saveAndFlush(final ExistingLoan existingLoan) {
        this.repository.saveAndFlush(existingLoan);
    }

    public void delete(final ExistingLoan existingLoan) {
        this.repository.delete(existingLoan);
    }

    public void delete(final List<ExistingLoan> existingLoans) {
        this.repository.delete(existingLoans);
    }

    public List<ExistingLoan> findByLoanApplicationIdAndSource(final Long loanApplicationId, final CodeValue source) {
        return this.repository.findByLoanApplicationIdAndSource(loanApplicationId, source);
    }

    public List<ExistingLoan> findByCreditBureauProductAndLoanApplicationIdAndSource(final CreditBureauProduct creditBureauProduct,
            final Long loanApplicationId, final CodeValue source, final Long loanCreditBureauEnquiryId) {
        return this.repository.findByCreditBureauProductAndLoanApplicationIdAndSourceAndLoanCreditBureauEnquiryId(creditBureauProduct,
                loanApplicationId, source, loanCreditBureauEnquiryId);
    }

    public List<ExistingLoan> findByLoanApplicationIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(
            final Long loanApplicationId, final CodeValue source, final CreditBureauProduct creditBureauProduct,
            final Long loanCreditBureauEnquiryId) {
        return this.repository.findByLoanApplicationIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(loanApplicationId,
                source, creditBureauProduct, loanCreditBureauEnquiryId);
    }

    public List<ExistingLoan> findByLoanApplicationIdAndLoanIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(
            Long loanApplicationId, Long loanId, CodeValue source, CreditBureauProduct creditBureauProduct, Long loanCreditBureauEnquiryId) {
        return this.repository.findByLoanApplicationIdAndLoanIdAndSourceAndCreditBureauProductAndLoanCreditBureauEnquiryId(
                loanApplicationId, loanId, source, creditBureauProduct, loanCreditBureauEnquiryId);
    }
}
