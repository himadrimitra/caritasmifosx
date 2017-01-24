package com.finflux.transaction.execution.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.transaction.execution.data.BankTransactionEntityType;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepository;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepositoryWrapper;

@Component
public class RetryBankTransactionServiceFactory {

    private final LoanBankTransactionHandler loanBankTransactionHandler;
    private final BankAccountTransactionRepositoryWrapper transactionRepositoryWrapper;
    private final BankAccountTransactionRepository transactionRepository;

    @Autowired
    public RetryBankTransactionServiceFactory(final LoanBankTransactionHandler loanBankTransactionHandler,
                                              final BankAccountTransactionRepositoryWrapper transactionRepositoryWrapper,
                                              final BankAccountTransactionRepository transactionRepository){
        this.loanBankTransactionHandler = loanBankTransactionHandler;
        this.transactionRepositoryWrapper = transactionRepositoryWrapper;
        this.transactionRepository = transactionRepository;
    }

    public CommandProcessingResult retryBankTransaction(final Long transactionId) {
        BankAccountTransaction bankTransaction = this.transactionRepositoryWrapper.findOneWithNotFoundDetection(transactionId);
        if(bankTransaction!=null){
            TransactionStatus status = TransactionStatus.fromInt(bankTransaction.getStatus());
            if(TransactionStatus.FAILED.equals(status)){
                if(BankTransactionEntityType.LOANS.getValue().equals(bankTransaction.getEntityType())){
                    loanBankTransactionHandler.createBankTransferByEntity(bankTransaction.getEntityId(),bankTransaction.getEntityTransactionId());
                }
                bankTransaction.setStatus(TransactionStatus.RETRIED.getValue());
                this.transactionRepository.save(bankTransaction);
            }
        }
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }
}
