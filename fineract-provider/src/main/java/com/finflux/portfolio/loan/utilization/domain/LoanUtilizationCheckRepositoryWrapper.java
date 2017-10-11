package com.finflux.portfolio.loan.utilization.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.utilization.exception.LoanUtilizationCheckNotFoundException;

@Service
public class LoanUtilizationCheckRepositoryWrapper {

    private final LoanUtilizationCheckRepository repository;

    @Autowired
    public LoanUtilizationCheckRepositoryWrapper(final LoanUtilizationCheckRepository repository) {
        this.repository = repository;
    }

    public LoanUtilizationCheck findOneWithNotFoundDetection(final Long id) {
        final LoanUtilizationCheck address = this.repository.findOne(id);
        if (address == null) { throw new LoanUtilizationCheckNotFoundException(id); }
        return address;
    }

    public void save(final LoanUtilizationCheck loanUtilizationCheck) {
        this.repository.save(loanUtilizationCheck);
    }

    public void save(final List<LoanUtilizationCheck> loanUtilizationCheck) {
        this.repository.save(loanUtilizationCheck);
    }

    public void saveAndFlush(final LoanUtilizationCheck loanUtilizationCheck) {
        this.repository.saveAndFlush(loanUtilizationCheck);
    }

    public void delete(final LoanUtilizationCheck loanUtilizationCheck) {
        this.repository.delete(loanUtilizationCheck);
    }
}
