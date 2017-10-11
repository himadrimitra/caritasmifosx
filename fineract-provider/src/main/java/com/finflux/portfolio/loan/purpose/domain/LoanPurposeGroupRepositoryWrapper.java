package com.finflux.portfolio.loan.purpose.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.exception.LoanPurposeGroupNotFoundException;

@Service
public class LoanPurposeGroupRepositoryWrapper {

    private final LoanPurposeGroupRepository repository;

    @Autowired
    public LoanPurposeGroupRepositoryWrapper(final LoanPurposeGroupRepository repository) {
        this.repository = repository;
    }

    public LoanPurposeGroup findOneWithNotFoundDetection(final Long id) {
        final LoanPurposeGroup loanPurposeGroup = this.repository.findOne(id);
        if (loanPurposeGroup == null) { throw new LoanPurposeGroupNotFoundException(id); }
        return loanPurposeGroup;
    }

    public void save(final LoanPurposeGroup loanPurposeGroup) {
        this.repository.save(loanPurposeGroup);
    }

    public void save(final List<LoanPurposeGroup> loanPurposeGroup) {
        this.repository.save(loanPurposeGroup);
    }

    public void saveAndFlush(final LoanPurposeGroup loanPurposeGroup) {
        this.repository.saveAndFlush(loanPurposeGroup);
    }

    public void delete(final LoanPurposeGroup loanPurposeGroup) {
        this.repository.delete(loanPurposeGroup);
    }
}
