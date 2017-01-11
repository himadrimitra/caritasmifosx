package com.finflux.portfolio.loan.purpose.service;

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

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupDataValidator;
import com.finflux.portfolio.loan.purpose.domain.LoanPurpose;
import com.finflux.portfolio.loan.purpose.domain.LoanPurposeGroup;
import com.finflux.portfolio.loan.purpose.domain.LoanPurposeGroupRepositoryWrapper;
import com.finflux.portfolio.loan.purpose.domain.LoanPurposeRepositoryWrapper;
import com.finflux.portfolio.loan.purpose.exception.LoanPurposeGroupNotFoundException;
import com.finflux.portfolio.loan.purpose.exception.LoanPurposeNotFoundException;

@Service
public class LoanPurposeGroupWritePlatformServiceImpl implements LoanPurposeGroupWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanPurposeGroupWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final LoanPurposeGroupDataValidator validator;
    private final LoanPurposeGroupDataAssembler assembler;
    private final LoanPurposeGroupRepositoryWrapper loanPurposeGroupRepository;
    private final LoanPurposeRepositoryWrapper loanPurposeRepository;

    @Autowired
    public LoanPurposeGroupWritePlatformServiceImpl(final PlatformSecurityContext context, final LoanPurposeGroupDataValidator validator,
            final LoanPurposeGroupDataAssembler assembler, final LoanPurposeGroupRepositoryWrapper loanPurposeGroupRepository,
            final LoanPurposeRepositoryWrapper loanPurposeRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.loanPurposeGroupRepository = loanPurposeGroupRepository;
        this.loanPurposeRepository = loanPurposeRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createLoanPurposeGroup(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForCreateLoanPurposeGroup(command.json());
            final LoanPurposeGroup loanPurposeGroup = this.assembler.assembleCreateLoanPurposeGroupForm(command);
            this.loanPurposeGroupRepository.save(loanPurposeGroup);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeGroup.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult updateLoanPurposeGroup(final Long loanPurposeGroupId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForUpdateLoanPurposeGroup(command.json());
            final LoanPurposeGroup loanPurposeGroup = this.loanPurposeGroupRepository.findOneWithNotFoundDetection(loanPurposeGroupId);
            final Map<String, Object> changes = this.assembler.assembleUpdateLoanPurposeGroupForm(loanPurposeGroup, command);
            if (!changes.isEmpty()) {
                this.loanPurposeGroupRepository.save(loanPurposeGroup);
            }
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeGroupId)//
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult activateLoanPurposeGroup(final Long loanPurposeGroupId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final LoanPurposeGroup loanPurposeGroup = this.loanPurposeGroupRepository.findOneWithNotFoundDetection(loanPurposeGroupId);
            if (loanPurposeGroup.isActive()) { throw new LoanPurposeGroupNotFoundException(loanPurposeGroupId, "activated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(LoanPurposeGroupApiConstants.isActiveParamName, true);
            loanPurposeGroup.activate();
            this.loanPurposeGroupRepository.save(loanPurposeGroup);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeGroupId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActivateLoanPurposeGroup(final Long loanPurposeGroupId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final LoanPurposeGroup loanPurposeGroup = this.loanPurposeGroupRepository.findOneWithNotFoundDetection(loanPurposeGroupId);
            if (!loanPurposeGroup.isActive()) { throw new LoanPurposeGroupNotFoundException(loanPurposeGroupId, "inactivated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(LoanPurposeGroupApiConstants.isActiveParamName, false);
            loanPurposeGroup.inActivate();
            this.loanPurposeGroupRepository.save(loanPurposeGroup);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeGroupId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult createLoanPurpose(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForCreateLoanPurpose(command.json());
            final LoanPurpose loanPurpose = this.assembler.assembleCreateLoanPurposeForm(command);
            this.loanPurposeRepository.save(loanPurpose);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurpose.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult updateLoanPurpose(final Long loanPurposeId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final LoanPurpose loanPurpose = this.loanPurposeRepository.findOneWithNotFoundDetection(loanPurposeId);
            this.validator.validateForUpdateLoanPurpose(command.json());
            final Map<String, Object> changes = this.assembler.assembleUpdateLoanPurposeForm(loanPurpose, command);
            this.loanPurposeRepository.save(loanPurpose);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult activateLoanPurpose(final Long loanPurposeId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final LoanPurpose loanPurpose = this.loanPurposeRepository.findOneWithNotFoundDetection(loanPurposeId);
            if (loanPurpose.isActive()) { throw new LoanPurposeNotFoundException(loanPurposeId, "activated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(LoanPurposeGroupApiConstants.isActiveParamName, true);
            loanPurpose.activate();
            this.loanPurposeRepository.save(loanPurpose);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeId)//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActivateLoanPurpose(final Long loanPurposeId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final LoanPurpose loanPurpose = this.loanPurposeRepository.findOneWithNotFoundDetection(loanPurposeId);
            if (!loanPurpose.isActive()) { throw new LoanPurposeNotFoundException(loanPurposeId, "inactivated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(LoanPurposeGroupApiConstants.isActiveParamName, false);
            loanPurpose.inActivate();
            this.loanPurposeRepository.save(loanPurpose);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanPurposeId)//
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
        if (realCause.getMessage().contains("UQ_f_loan_purpose_group_system_code")) {
            throw new PlatformDataIntegrityException("error.msg.loan.purpose.group.system.code.already.exist",
                    "Loan purpose group system code already exist", "systemCode");
        } else if (realCause.getMessage().contains("UQ_f_loan_purpose_group")) {
            throw new PlatformDataIntegrityException("error.msg.loan.purpose.group.name.and.type.already.exist",
                    "Loan purpose group name and type already exist", "name", "typeEnumId");
        } else if (realCause.getMessage().contains("UQ_f_loan_purpose_system_code")) { throw new PlatformDataIntegrityException(
                "error.msg.loan.purpose.system.code.already.exist", "Loan purpose system code already exist", "systemCode"); }
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.loan.purpose.groupping.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}