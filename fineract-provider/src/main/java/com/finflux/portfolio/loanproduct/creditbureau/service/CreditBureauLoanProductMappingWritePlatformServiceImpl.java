package com.finflux.portfolio.loanproduct.creditbureau.service;

import java.util.LinkedHashMap;
import java.util.Map;

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

import com.finflux.portfolio.loanproduct.creditbureau.data.CreditBureauLoanProductMappingDataValidator;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMapping;
import com.finflux.portfolio.loanproduct.creditbureau.domain.CreditBureauLoanProductMappingRepositoryWrapper;
import com.finflux.portfolio.loanproduct.creditbureau.exception.CreditBureauLoanProductMappingNotFoundException;

@Service
public class CreditBureauLoanProductMappingWritePlatformServiceImpl implements CreditBureauLoanProductMappingWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CreditBureauLoanProductMappingWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
    private final CreditBureauLoanProductMappingDataValidator validator;
    private final CreditBureauLoanProductMappingDataAssembler assembler;
    private final CreditBureauLoanProductMappingRepositoryWrapper repository;

    @Autowired
    public CreditBureauLoanProductMappingWritePlatformServiceImpl(final PlatformSecurityContext context,
            final CreditBureauLoanProductMappingDataValidator validator, final CreditBureauLoanProductMappingDataAssembler assembler,
            final CreditBureauLoanProductMappingRepositoryWrapper repository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.repository = repository;
    }

    @Override
    public CommandProcessingResult create(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForCreate(command.json());
            final CreditBureauLoanProductMapping creditBureauLoanProductMapping = this.assembler.assembleCreateForm(command);
            this.repository.save(creditBureauLoanProductMapping);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(creditBureauLoanProductMapping.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult update(final Long cblpMappingId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final CreditBureauLoanProductMapping creditBureauLoanProductMapping = this.repository
                    .findOneWithNotFoundDetection(cblpMappingId);
            this.validator.validateForUpdate(command.json());
            final Map<String, Object> changes = this.assembler.assembleUpdateForm(creditBureauLoanProductMapping, command);
            if (!changes.isEmpty()) {
                this.repository.save(creditBureauLoanProductMapping);
            }
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cblpMappingId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult active(JsonCommand command, final Long cblpMappingId) {
        try {
            this.context.authenticatedUser();
            final CreditBureauLoanProductMapping creditBureauLoanProductMapping = this.repository
                    .findOneWithNotFoundDetection(cblpMappingId);
            if (creditBureauLoanProductMapping.isActive() != null && creditBureauLoanProductMapping.isActive()) { throw new CreditBureauLoanProductMappingNotFoundException(
                    cblpMappingId, "activated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put("isActive", true);
            creditBureauLoanProductMapping.activate();
            this.repository.save(creditBureauLoanProductMapping);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cblpMappingId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActive(JsonCommand command, final Long cblpMappingId) {
        try {
            this.context.authenticatedUser();
            final CreditBureauLoanProductMapping creditBureauLoanProductMapping = this.repository
                    .findOneWithNotFoundDetection(cblpMappingId);
            if (creditBureauLoanProductMapping.isActive() != null && !creditBureauLoanProductMapping.isActive()) { throw new CreditBureauLoanProductMappingNotFoundException(
                    cblpMappingId, "inactivated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put("isActive", false);
            creditBureauLoanProductMapping.inActivate();
            this.repository.save(creditBureauLoanProductMapping);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(cblpMappingId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        logAsErrorUnexpectedDataIntegrityException(dve);
        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("uk_f_creditbureau_loanproduct_mapping")) { throw new PlatformDataIntegrityException(
                "error.msg.loan.product.id.already.mapped.with.credit.bureau.product",
                "Loan product already mapped with credit bureau product", "loanProductId",
                command.longValueOfParameterNamed("loanProductId")); }
        throw new PlatformDataIntegrityException("error.msg.credit.bureau.loan.product.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}