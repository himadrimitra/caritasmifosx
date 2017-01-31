package com.finflux.transaction.execution.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.data.BankTransactionDataAssembler;
import com.finflux.transaction.execution.data.BankTransactionDataValidator;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepository;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepositoryWrapper;

@Service
public class BankTransactionWriteServiceImpl implements BankTransactionWriteService {

    private final BankAccountTransactionRepository repository;
    private final BankAccountTransactionRepositoryWrapper repositoryWrapper;
    private final BankTransactionDataValidator bankTransactionDataValidator;
    private final BankTransactionDataAssembler bankTransactionDataAssembler;
    private final DateTimeFormatter dateFmt = DateTimeFormat.forPattern("YYYYMMdd");

    @Autowired
    public BankTransactionWriteServiceImpl(final BankAccountTransactionRepository repository,
            final BankAccountTransactionRepositoryWrapper repositoryWrapper,
            final BankTransactionDataValidator bankTransactionDataValidator,
            final BankTransactionDataAssembler bankTransactionDataAssembler) {
        this.repository = repository;
        this.repositoryWrapper = repositoryWrapper;
        this.bankTransactionDataValidator = bankTransactionDataValidator;
        this.bankTransactionDataAssembler = bankTransactionDataAssembler;
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

    @Override
    public CommandProcessingResult submitTransaction(Long transactionId, JsonCommand command) {
        final BankAccountTransaction bankTransaction = this.repositoryWrapper.findOneWithNotFoundDetection(transactionId);
        TransactionStatus status = TransactionStatus.fromInt(bankTransaction.getStatus());
        if(TransactionStatus.DRAFTED.equals(status)){
            bankTransactionDataValidator.validateForSubmitTransaction(command.json());
            bankTransactionDataAssembler.assembleSubmitBankTransction(bankTransaction,command);
            bankTransaction.setStatus(TransactionStatus.SUBMITTED.getValue());
            this.repository.save(bankTransaction);
        }
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }

    @Override
    public Long createTransactionEntry(BankAccountTransaction txn) {
        //checkIfthereANyActiveTransactionisthere
        List<Integer> activeStatuses = new ArrayList<Integer>(Arrays.asList(TransactionStatus.DRAFTED.getValue(),
                TransactionStatus.SUBMITTED.getValue(), TransactionStatus.INITIATED.getValue(),
                TransactionStatus.PENDING.getValue(),TransactionStatus.ERROR.getValue()));
        Long activeTransactionCount = repository.countByEntityTypeAndEntityIdAndEntityTransactionIdAndStatusIsIn(
                txn.getEntityType(), txn.getEntityId(), txn.getEntityTransactionId(),activeStatuses);
        if(activeTransactionCount == 0) {
            BankAccountTransaction transaction = this.repository.save(txn);
            String referenceId= generateUniqueReferenceId(transaction.getId());
            transaction.setInternalReferenceId(referenceId);
            return transaction.getId();
        }
        return null;
    }

    private String generateUniqueReferenceId(Long id) {
        return dateFmt.print(new DateTime())+"f"+id;
    }
}
