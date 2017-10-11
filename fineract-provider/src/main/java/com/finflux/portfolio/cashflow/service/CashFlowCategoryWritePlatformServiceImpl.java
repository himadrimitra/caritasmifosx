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
import com.finflux.portfolio.cashflow.data.CashFlowCategoryDataValidator;
import com.finflux.portfolio.cashflow.domain.CashFlowCategory;
import com.finflux.portfolio.cashflow.domain.CashFlowCategoryRepositoryWrapper;
import com.finflux.portfolio.cashflow.exception.CashFlowCategoryNotFoundException;

@Service
public class CashFlowCategoryWritePlatformServiceImpl implements CashFlowCategoryWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CashFlowCategoryWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final CashFlowCategoryDataValidator validator;
    private final CashFlowCategoryDataAssembler assembler;
    private final CashFlowCategoryRepositoryWrapper repository;

    @Autowired
    public CashFlowCategoryWritePlatformServiceImpl(final PlatformSecurityContext context, final CashFlowCategoryDataValidator validator,
            final CashFlowCategoryDataAssembler assembler, final CashFlowCategoryRepositoryWrapper loanPurposeGroupRepository) {
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
            final CashFlowCategory cashFlowCategory = this.assembler.assembleCreateForm(command);
            this.repository.save(cashFlowCategory);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cashFlowCategory.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult update(final Long cashFlowCategoryId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final CashFlowCategory cashFlowCategory = this.repository.findOneWithNotFoundDetection(cashFlowCategoryId);
            this.validator.validateForUpdate(command.json());
            final Map<String, Object> changes = this.assembler.assembleUpdateForm(cashFlowCategory, command);
            if (!changes.isEmpty()) {
                this.repository.save(cashFlowCategory);
            }
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cashFlowCategoryId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult activate(final Long cashFlowId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final CashFlowCategory cashFlowCategory = this.repository.findOneWithNotFoundDetection(cashFlowId);
            if (cashFlowCategory.isActive()) { throw new CashFlowCategoryNotFoundException(cashFlowId, "activated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(CashFlowCategoryApiConstants.isActiveParamName, true);
            cashFlowCategory.activate();
            this.repository.save(cashFlowCategory);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cashFlowId)//
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActivate(final Long cashFlowId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final CashFlowCategory cashFlowCategory = this.repository.findOneWithNotFoundDetection(cashFlowId);
            if (!cashFlowCategory.isActive()) { throw new CashFlowCategoryNotFoundException(cashFlowId, "inactivated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(CashFlowCategoryApiConstants.isActiveParamName, false);
            cashFlowCategory.inActivate();
            this.repository.save(cashFlowCategory);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cashFlowId)//
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
        if (realCause.getMessage().contains("UQ_f_cashflow_category_name")) {
            throw new PlatformDataIntegrityException("error.msg.cash.flow.category.name.already.exist",
                    "Cash flow category name already exist", "name");
        } else if (realCause.getMessage().contains("UQ_f_cashflow_category_short_name")) { throw new PlatformDataIntegrityException(
                "error.msg.cash.flow.category.short.name.already.exist", "Cash flow category short name already exist", "shortName"); }

        logAsErrorUnexpectedDataIntegrityException(dve);

        throw new PlatformDataIntegrityException("error.msg.cash.flow.category.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}