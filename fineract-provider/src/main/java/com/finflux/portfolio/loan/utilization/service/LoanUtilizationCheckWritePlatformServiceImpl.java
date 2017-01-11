package com.finflux.portfolio.loan.utilization.service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckDataValidator;
import com.finflux.portfolio.loan.utilization.domain.LoanUtilizationCheck;
import com.finflux.portfolio.loan.utilization.domain.LoanUtilizationCheckRepositoryWrapper;

@Service
public class LoanUtilizationCheckWritePlatformServiceImpl implements LoanUtilizationCheckWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanUtilizationCheckWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final LoanUtilizationCheckDataValidator validator;
    private final LoanUtilizationCheckDataAssembler assembler;
    private final LoanUtilizationCheckRepositoryWrapper repository;
    private final LoanRepositoryWrapper loanRepository;

    @Autowired
    public LoanUtilizationCheckWritePlatformServiceImpl(final PlatformSecurityContext context,
            final LoanUtilizationCheckDataValidator validator, final LoanUtilizationCheckDataAssembler assembler,
            final LoanUtilizationCheckRepositoryWrapper repository, final LoanRepositoryWrapper loanRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.repository = repository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final Long entityId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            this.validator.validateForCreate(command.json());

            final List<LoanUtilizationCheck> loanUtilizationChecks = this.assembler.assembleCreateForm(command);

            this.repository.save(loanUtilizationChecks);

            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanUtilizationChecks.get(0).getId())
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult update(final Long loanId, final Long loanUtilizationCheckId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            this.loanRepository.findOneWithNotFoundDetection(loanId);

            final LoanUtilizationCheck loanUtilizationCheck = this.repository.findOneWithNotFoundDetection(loanUtilizationCheckId);

            this.validator.validateForUpdate(command.json());

            final Map<String, Object> changes = this.assembler.assembleUpdateForm(loanUtilizationCheck, command);

            this.repository.save(loanUtilizationCheck);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanUtilizationCheckId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    /**
     * Guaranteed to throw an exception no matter what the data integrity issues
     * 
     * @param command
     * @param dve
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        logAsErrorUnexpectedDataIntegrityException(dve);

        throw new PlatformDataIntegrityException("error.msg.loan.utilization.check.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

}
