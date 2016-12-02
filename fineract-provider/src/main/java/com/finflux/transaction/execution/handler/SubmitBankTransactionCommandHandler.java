package com.finflux.transaction.execution.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.transaction.execution.service.BankTransactionWriteService;

@Service
@CommandType(entity = "BANK_TRANSACTION", action = "SUBMIT")
public class SubmitBankTransactionCommandHandler implements NewCommandSourceHandler {

    private final BankTransactionWriteService bankTransactionWriteService;

    @Autowired
    public SubmitBankTransactionCommandHandler(final BankTransactionWriteService bankTransactionWriteService) {
        this.bankTransactionWriteService = bankTransactionWriteService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.bankTransactionWriteService.submitTransaction(command.entityId());
    }
}