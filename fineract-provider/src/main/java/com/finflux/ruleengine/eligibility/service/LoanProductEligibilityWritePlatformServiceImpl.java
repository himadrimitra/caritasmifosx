package com.finflux.ruleengine.eligibility.service;

import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityDataValidator;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibility;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibilityCriteria;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibilityCriteriaRepository;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibilityRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanProductEligibilityWritePlatformServiceImpl implements LoanProductEligibilityWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanProductEligibilityWritePlatformService.class);

    private final PlatformSecurityContext context;
    private final LoanProductEligibilityDataValidator validator;
    private final LoanProductEligibilityDataAssembler assembler;
    private final LoanProductEligibilityRepository loanProductEligibilityRepository;
    private final LoanProductEligibilityCriteriaRepository loanProductEligibilityCriteriaRepository;

    @Autowired
    public LoanProductEligibilityWritePlatformServiceImpl(final PlatformSecurityContext context,
                                                          final LoanProductEligibilityDataValidator validator,
                                                          final LoanProductEligibilityDataAssembler assembler,
                                                          final LoanProductEligibilityRepository loanProductEligibilityRepository,
                                                          final LoanProductEligibilityCriteriaRepository loanProductEligibilityCriteriaRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.loanProductEligibilityRepository = loanProductEligibilityRepository;
        this.loanProductEligibilityCriteriaRepository = loanProductEligibilityCriteriaRepository;
    }

    @javax.transaction.Transactional
    @Override
    public CommandProcessingResult createLoanProductEligibility(final Long loanProductId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForCreateLoanProductEligibility(command.json());
            final LoanProductEligibility loanProductEligibility = this.assembler.assembleCreateLoanProductEligibility(command);
            loanProductEligibility.setLoanProductId(loanProductId);
            loanProductEligibility.setCreatedBy(this.context.authenticatedUser());
            loanProductEligibility.setUpdatedBy(this.context.authenticatedUser());
            loanProductEligibility.setCreatedOn(new Date());
            loanProductEligibility.setUpdatedOn(new Date());
            this.loanProductEligibilityRepository.save(loanProductEligibility);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanProductEligibility.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
//            handleDataIntegrityIssues(command, dve);
//            return CommandProcessingResult.empty();
        }
        return null;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateLoanProductEligibility(Long loanProductId, final JsonCommand command) {
        try {
            AppUser  appUser= this.context.authenticatedUser();
            LoanProductEligibility loanProductEligibility = this.loanProductEligibilityRepository.findOneByLoanProductId(loanProductId);
            this.validator.validateForUpdateRulePurpose(command.json());

            this.assembler.assembleUpdateLoanProductEligibility(loanProductEligibility,command);

            loanProductEligibility.setUpdatedBy(appUser);
            loanProductEligibility.setUpdatedOn(new Date());
            LoanProductEligibility loanProductEligibility1 = this.loanProductEligibilityRepository.save(loanProductEligibility);

            List<LoanProductEligibilityCriteria> criteriaList = this.assembler.assembleUpdateLoanProductEligibilityCriterias(loanProductEligibility,command);
            loanProductEligibility1.setEligibilityCriterias(criteriaList);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanProductEligibility.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
//            handleDataIntegrityIssues(command, dve);
//            return CommandProcessingResult.empty();
        }
        return null;
    }
}