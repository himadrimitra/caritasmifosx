package com.finflux.transaction.execution.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.data.BankTransactionDataAssembler;
import com.finflux.transaction.execution.data.BankTransactionDataValidator;
import com.finflux.transaction.execution.data.BankTransactionPermissionConstants;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepository;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepositoryWrapper;

@Service
public class BankTransactionWriteServiceImpl implements BankTransactionWriteService {

    private final BankAccountTransactionRepository repository;
    private final PlatformSecurityContext context;
    private final BankAccountTransactionRepositoryWrapper repositoryWrapper;
    private final BankTransactionDataValidator bankTransactionDataValidator;
    private final BankTransactionDataAssembler bankTransactionDataAssembler;
    private final DateTimeFormatter dateFmt = DateTimeFormat.forPattern("YYYYMMdd");

    @Autowired
    public BankTransactionWriteServiceImpl(final BankAccountTransactionRepository repository,
            final BankAccountTransactionRepositoryWrapper repositoryWrapper,
            final BankTransactionDataValidator bankTransactionDataValidator,
            final BankTransactionDataAssembler bankTransactionDataAssembler,
            final PlatformSecurityContext context) {
        this.repository = repository;
        this.repositoryWrapper = repositoryWrapper;
        this.bankTransactionDataValidator = bankTransactionDataValidator;
        this.bankTransactionDataAssembler = bankTransactionDataAssembler;
        this.context=context;
    }

    @Override public CommandProcessingResult initiateTransaction(Long transactionId) {
        this.context.authenticatedUser().validateHasPermissionTo("INITIATE_BANK_TRANSACTION");
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
            bankTransactionDataValidator.validateForSubmitTransaction(bankTransaction, command.json());
            bankTransactionDataAssembler.assembleSubmitBankTransction(bankTransaction,command);
            bankTransaction.setStatus(TransactionStatus.SUBMITTED.getValue());
            this.repository.save(bankTransaction);
        }
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }

    @Override
    public Long createTransactionEntry(BankAccountTransaction txn) {
        //checkIfthereANyActiveTransactionisthere
        List<Integer> activeStatuses = new ArrayList<>(Arrays.asList(TransactionStatus.DRAFTED.getValue(),
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

    @Override
    public CommandProcessingResult rejectTransaction(Long transactionId) {
        this.context.authenticatedUser().validateHasPermissionTo(BankTransactionPermissionConstants.rejectPermissionParam);
        final BankAccountTransaction bankTransaction = this.repositoryWrapper.findOneWithNotFoundDetection(transactionId);
        TransactionStatus status = TransactionStatus.fromInt(bankTransaction.getStatus());
        if (TransactionStatus.DRAFTED.equals(status)) {
            bankTransaction.setStatus(TransactionStatus.REJECTED.getValue());
        } else if (TransactionStatus.SUBMITTED.equals(status)) {
            bankTransaction.setStatus(TransactionStatus.REJECTED.getValue());
        }else if (TransactionStatus.FAILED.equals(status)) {
            bankTransaction.setStatus(TransactionStatus.REJECTED.getValue());
        }
        this.repository.save(bankTransaction);
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }

    @Override
    public CommandProcessingResult closeTransaction(Long transactionId) {
        this.context.authenticatedUser().validateHasPermissionTo(BankTransactionPermissionConstants.rejectPermissionParam);
        final BankAccountTransaction bankTransaction = this.repositoryWrapper.findOneWithNotFoundDetection(transactionId);
        TransactionStatus status = TransactionStatus.fromInt(bankTransaction.getStatus());
        if (TransactionStatus.SUCCESS.equals(status)) {
            bankTransaction.setStatus(TransactionStatus.CLOSED.getValue());
        }
        this.repository.save(bankTransaction);
        return new CommandProcessingResultBuilder().withEntityId(transactionId).build();
    }
}