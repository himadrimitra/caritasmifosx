package com.finflux.transaction.execution.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepository;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepositoryWrapper;

@Service
public class BankTransactionWriteServiceImpl implements BankTransactionWriteService {

    private final BankAccountTransactionRepository repository;
    private final BankAccountTransactionRepositoryWrapper repositoryWrapper;

    @Autowired
    public BankTransactionWriteServiceImpl(final BankAccountTransactionRepository repository,
            final BankAccountTransactionRepositoryWrapper repositoryWrapper) {
        this.repository = repository;
        this.repositoryWrapper = repositoryWrapper;
    }

    @Override public CommandProcessingResult initiateTransaction(Long transactionId) {
        final BankAccountTransaction bankTransaction = this.repositoryWrapper.findOneWithNotFoundDetection(transactionId);
        TransactionStatus status = TransactionStatus.fromInt(bankTransaction.getStatus());
        if(TransactionStatus.SUBMITTED.equals(status)){
            bankTransaction.setStatus(TransactionStatus.INITIATED.getValue());
            this.repository.save(bankTransaction);
        }
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }

    @Override public CommandProcessingResult submitTransaction(Long transactionId) {
        final BankAccountTransaction bankTransaction = this.repositoryWrapper.findOneWithNotFoundDetection(transactionId);
        TransactionStatus status = TransactionStatus.fromInt(bankTransaction.getStatus());
        if(TransactionStatus.DRAFTED.equals(status)){
            bankTransaction.setStatus(TransactionStatus.SUBMITTED.getValue());
            this.repository.save(bankTransaction);
        }else if(TransactionStatus.FAILED.equals(status)){
            BankAccountTransaction newTransaction = new BankAccountTransaction(bankTransaction.getEntityType(),
                    bankTransaction.getEntityId(), bankTransaction.getEntityTransactionId(),
                    TransactionStatus.SUBMITTED.getValue(), bankTransaction.getDebitAccount(),
                    bankTransaction.getDebitAccount(), bankTransaction.getAmount(), bankTransaction.getTransferType(),
                    bankTransaction.getExternalServiceId(),bankTransaction.getReason());
            bankTransaction.setStatus(TransactionStatus.SUBMITTED.getValue());
            this.repository.save(newTransaction);
            transactionId = newTransaction.getId();
        }
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }

    @Override
    public Long createTransactionEntry(BankAccountTransaction bankAccountTransaction) {
        this.repository.save(bankAccountTransaction);
        return bankAccountTransaction.getId();
    }
}
