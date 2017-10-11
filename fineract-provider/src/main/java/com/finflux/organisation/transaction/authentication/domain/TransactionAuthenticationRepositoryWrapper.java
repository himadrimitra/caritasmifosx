package com.finflux.organisation.transaction.authentication.domain;

import java.util.List;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.organisation.transaction.authentication.exception.TransactionAuthenticationNotFoundException;

@Service
public class TransactionAuthenticationRepositoryWrapper {

    private final TransactionAuthenticationRepository repository;

    @Autowired
    public TransactionAuthenticationRepositoryWrapper(final TransactionAuthenticationRepository repository) {
        this.repository = repository;
    }

    public void save(final TransactionAuthentication transactionAuthentication) {
        this.repository.save(transactionAuthentication);
    }

    public TransactionAuthentication findOneWithNotFoundDetection(final Long id) {
        TransactionAuthentication transactionAuthentication = this.repository.findOne(id);
        if (transactionAuthentication == null) { throw new TransactionAuthenticationNotFoundException(id); }
        return this.repository.findOne(id);
    }

    public CommandProcessingResult deletTransactionAuthentication(final Long transactionAuthenticationId) {
        this.repository.delete(transactionAuthenticationId);
        return new CommandProcessingResultBuilder().withEntityId(transactionAuthenticationId).build();
    }

    public Integer getPortfolioTypeAndTransactionType(final Integer portfolioTypeId, final Integer transactionTypeId) {
        List<TransactionAuthentication> transactionAuthentications = this.repository.findByPortfolioTypeAndTransactionTypeId(
                portfolioTypeId, transactionTypeId);
        return transactionAuthentications.size();
    }

    public List<TransactionAuthentication> findByPortfolioTypeAndTransactionTypeId(final Integer portfolioType,
            final Integer transactionTypeId) {
        return this.repository.findByPortfolioTypeAndTransactionTypeId(portfolioType, transactionTypeId);
    }

}
