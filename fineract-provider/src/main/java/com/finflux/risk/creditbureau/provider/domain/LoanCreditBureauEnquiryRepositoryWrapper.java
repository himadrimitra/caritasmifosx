package com.finflux.risk.creditbureau.provider.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanCreditBureauEnquiryRepositoryWrapper {

    private final LoanCreditBureauEnquiryRepository repository;

    @Autowired
    public LoanCreditBureauEnquiryRepositoryWrapper(final LoanCreditBureauEnquiryRepository repository) {
        this.repository = repository;
    }

    public LoanCreditBureauEnquiry findOneWithNotFoundDetection(final Long id) {
        final LoanCreditBureauEnquiry loanCreditBureauEnquiry = this.repository.findOne(id);
        if (loanCreditBureauEnquiry == null) {}
        return loanCreditBureauEnquiry;
    }

    public void save(final LoanCreditBureauEnquiry loanCreditBureauEnquiry) {
        this.repository.save(loanCreditBureauEnquiry);
    }

    public void save(final List<LoanCreditBureauEnquiry> loanCreditBureauEnquiry) {
        this.repository.save(loanCreditBureauEnquiry);
    }

    public void saveAndFlush(final LoanCreditBureauEnquiry loanCreditBureauEnquiry) {
        this.repository.saveAndFlush(loanCreditBureauEnquiry);
    }

    public void delete(final LoanCreditBureauEnquiry loanCreditBureauEnquiry) {
        this.repository.delete(loanCreditBureauEnquiry);
    }
}
