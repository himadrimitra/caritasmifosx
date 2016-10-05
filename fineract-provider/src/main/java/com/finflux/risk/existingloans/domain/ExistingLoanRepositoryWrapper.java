package com.finflux.risk.existingloans.domain;

import java.util.List;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public List<ExistingLoan> findByLoanApplicationIdAndSourceCvId(final Long loanApplicationId, final CodeValue sourceCvId) {
        return this.repository.findByLoanApplicationIdAndSourceCvId(loanApplicationId, sourceCvId);
    }
}
