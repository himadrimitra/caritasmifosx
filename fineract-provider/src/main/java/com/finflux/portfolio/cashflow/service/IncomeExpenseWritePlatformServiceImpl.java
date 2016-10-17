package com.finflux.portfolio.cashflow.service;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.api.CashFlowCategoryApiConstants;
import com.finflux.portfolio.cashflow.data.IncomeExpenseDataValidator;
import com.finflux.portfolio.cashflow.domain.IncomeExpense;
import com.finflux.portfolio.cashflow.domain.IncomeExpenseRepositoryWrapper;
import com.finflux.portfolio.cashflow.exception.IncomeExpenseNotFoundException;

@Service
public class IncomeExpenseWritePlatformServiceImpl implements IncomeExpenseWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(IncomeExpenseWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
    private final IncomeExpenseDataValidator validator;
    private final IncomeExpenseDataAssembler assembler;
    private final IncomeExpenseRepositoryWrapper repository;

    @Autowired
    public IncomeExpenseWritePlatformServiceImpl(final PlatformSecurityContext context, final IncomeExpenseDataValidator validator,
            final IncomeExpenseDataAssembler assembler, final IncomeExpenseRepositoryWrapper loanPurposeGroupRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.repository = loanPurposeGroupRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForCreate(command.json());
            final IncomeExpense incomeExpense = this.assembler.assembleCreateForm(command);
            this.repository.save(incomeExpense);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(incomeExpense.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult update(final Long incomeExpenseId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final IncomeExpense incomeExpense = this.repository.findOneWithNotFoundDetection(incomeExpenseId);
            this.validator.validateForUpdate(command.json());
            final Map<String, Object> changes = this.assembler.assembleUpdateForm(incomeExpense, command);
            if (!changes.isEmpty()) {
                this.repository.save(incomeExpense);
            }
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(incomeExpenseId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult activate(final Long incomeExpenseId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final IncomeExpense incomeExpense = this.repository.findOneWithNotFoundDetection(incomeExpenseId);
            if (incomeExpense.isActive()) { throw new IncomeExpenseNotFoundException(incomeExpenseId, "activated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(CashFlowCategoryApiConstants.isActiveParamName, true);
            incomeExpense.activate();
            this.repository.save(incomeExpense);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(incomeExpenseId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActivate(final Long incomeExpenseId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final IncomeExpense incomeExpense = this.repository.findOneWithNotFoundDetection(incomeExpenseId);
            if (!incomeExpense.isActive()) { throw new IncomeExpenseNotFoundException(incomeExpenseId, "inactivated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(CashFlowCategoryApiConstants.isActiveParamName, false);
            incomeExpense.inActivate();
            this.repository.save(incomeExpense);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(incomeExpenseId)//
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
        if (realCause.getMessage().contains("UQ_f_income_expense_name")) { throw new PlatformDataIntegrityException(
                "error.msg.income.expense.name.already.exist", "Income or expense name already exist", "name"); }
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.income.expense.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}