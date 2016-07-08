package com.finflux.loanapplicationreference.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.exception.LoanApplicationChargeNotFoundException;

@Service
public class LoanApplicationChargeRepositoryWrapper {

    private final LoanApplicationChargeRepository repository;

    @Autowired
    public LoanApplicationChargeRepositoryWrapper(final LoanApplicationChargeRepository repository) {
        this.repository = repository;
    }

    public LoanApplicationCharge findOneWithNotFoundDetection(final Long loanAppChargrId) {
        final LoanApplicationCharge loanApplicationCharge = this.repository.findOne(loanAppChargrId);
        if (loanApplicationCharge == null) { throw new LoanApplicationChargeNotFoundException(loanAppChargrId); }
        return loanApplicationCharge;
    }

    public void save(final LoanApplicationCharge loanApplicationCharge) {
        this.repository.save(loanApplicationCharge);
    }

    public void save(final List<LoanApplicationCharge> loanApplicationCharge) {
        this.repository.save(loanApplicationCharge);
    }

    public void saveAndFlush(final LoanApplicationCharge loanApplicationCharge) {
        this.repository.saveAndFlush(loanApplicationCharge);
    }

    public void delete(final LoanApplicationCharge loanApplicationCharge) {
        this.repository.delete(loanApplicationCharge);
    }
}
