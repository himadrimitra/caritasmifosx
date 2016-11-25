package com.finflux.loanapplicationreference.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.exception.LoanApplicationReferenceNotFoundException;

@Service
public class LoanApplicationReferenceRepositoryWrapper {

    private final LoanApplicationReferenceRepository repository;

    @Autowired
    public LoanApplicationReferenceRepositoryWrapper(final LoanApplicationReferenceRepository repository) {
        this.repository = repository;
    }

    public LoanApplicationReference findOneWithNotFoundDetection(final Long loanApplicationReferenceId) {
        final LoanApplicationReference loanApplicationReference = this.repository.findOne(loanApplicationReferenceId);
        if (loanApplicationReference == null) { throw new LoanApplicationReferenceNotFoundException(loanApplicationReferenceId); }
        return loanApplicationReference;
    }

    public void save(final LoanApplicationReference loanApplicationReference) {
        this.repository.save(loanApplicationReference);
    }

    public void save(final List<LoanApplicationReference> loanApplicationReference) {
        this.repository.save(loanApplicationReference);
    }

    public void saveAndFlush(final LoanApplicationReference loanApplicationReference) {
        this.repository.saveAndFlush(loanApplicationReference);
    }

    public void delete(final LoanApplicationReference loanApplicationReference) {
        this.repository.delete(loanApplicationReference);
    }
}
