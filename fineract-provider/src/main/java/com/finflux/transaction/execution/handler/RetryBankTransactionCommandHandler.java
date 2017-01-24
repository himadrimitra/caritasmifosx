package com.finflux.transaction.execution.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.transaction.execution.service.RetryBankTransactionServiceFactory;

@Service
@CommandType(entity = "BANK_TRANSACTION", action = "RETRY")
public class RetryBankTransactionCommandHandler implements NewCommandSourceHandler {

    private final RetryBankTransactionServiceFactory retryBankTransactionServiceFactory;

    @Autowired
    public RetryBankTransactionCommandHandler(final RetryBankTransactionServiceFactory retryBankTransactionServiceFactory) {
        this.retryBankTransactionServiceFactory = retryBankTransactionServiceFactory;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.retryBankTransactionServiceFactory.retryBankTransaction(command.entityId());
    }
}