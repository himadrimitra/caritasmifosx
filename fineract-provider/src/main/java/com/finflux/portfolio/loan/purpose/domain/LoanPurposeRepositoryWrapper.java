package com.finflux.portfolio.loan.purpose.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.exception.LoanPurposeNotFoundException;

@Service
public class LoanPurposeRepositoryWrapper {

    private final LoanPurposeRepository repository;

    @Autowired
    public LoanPurposeRepositoryWrapper(final LoanPurposeRepository repository) {
        this.repository = repository;
    }

    public LoanPurpose findOneWithNotFoundDetection(final Long id) {
        final LoanPurpose loanPurpose = this.repository.findOne(id);
        if (loanPurpose == null) { throw new LoanPurposeNotFoundException(id); }
        return loanPurpose;
    }

    public void save(final LoanPurpose loanPurpose) {
        this.repository.save(loanPurpose);
    }

    public void save(final List<LoanPurpose> loanPurpose) {
        this.repository.save(loanPurpose);
    }

    public void saveAndFlush(final LoanPurpose loanPurpose) {
        this.repository.saveAndFlush(loanPurpose);
    }

    public void delete(final LoanPurpose loanPurpose) {
        this.repository.delete(loanPurpose);
    }
}
