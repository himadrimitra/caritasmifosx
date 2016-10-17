package com.finflux.portfolio.client.cashflow.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.client.cashflow.data.ClientIncomeExpenseDataValidator;
import com.finflux.portfolio.client.cashflow.domain.ClientIncomeExpenseRepositoryWrapper;
import com.finflux.portfolio.client.cashflow.domain.ClientIncomeExpense;

@Service
public class ClientIncomeExpenseWritePlatformServiceImpl implements ClientIncomeExpenseWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ClientIncomeExpenseWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final ClientIncomeExpenseDataValidator validator;
    private final ClientIncomeExpenseDataAssembler assembler;
    private final ClientIncomeExpenseRepositoryWrapper repository;
    private final ClientRepositoryWrapper clientRepository;

    @Autowired
    public ClientIncomeExpenseWritePlatformServiceImpl(final PlatformSecurityContext context,
            final ClientIncomeExpenseDataValidator validator, final ClientIncomeExpenseDataAssembler assembler,
            final ClientIncomeExpenseRepositoryWrapper loanPurposeGroupRepository, final ClientRepositoryWrapper clientRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.repository = loanPurposeGroupRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final Long clientId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            this.validator.validateForCreate(command.json());

            final ClientIncomeExpense clientIncomeExpense = this.assembler.assembleCreateForm(client, command);

            this.repository.save(clientIncomeExpense);

            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(clientIncomeExpense.getId())//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult update(final Long clientIncomeExpenseId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            final ClientIncomeExpense clientIncomeExpense = this.repository.findOneWithNotFoundDetection(clientIncomeExpenseId);

            this.validator.validateForUpdate(command.json());

            final Map<String, Object> changes = this.assembler.assembleUpdateForm(clientIncomeExpense, command);

            if (!changes.isEmpty()) {
                this.repository.save(clientIncomeExpense);
            }

            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(clientIncomeExpenseId)//
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("unused")
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

        logAsErrorUnexpectedDataIntegrityException(dve);

        throw new PlatformDataIntegrityException("error.msg.client.income.expense.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}