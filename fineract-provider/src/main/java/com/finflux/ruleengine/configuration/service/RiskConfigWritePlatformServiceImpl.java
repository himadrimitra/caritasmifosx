package com.finflux.ruleengine.configuration.service;

import com.finflux.ruleengine.configuration.data.RiskDataValidator;
import com.finflux.ruleengine.configuration.domain.RuleModel;
import com.finflux.ruleengine.configuration.domain.RuleRepository;
import com.finflux.ruleengine.lib.data.EntityRuleType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
public class RiskConfigWritePlatformServiceImpl implements RiskConfigWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(RiskConfigWritePlatformService.class);

    private final PlatformSecurityContext context;
    private final RiskDataValidator validator;
    private final RiskConfigDataAssembler assembler;
    private final RuleRepository ruleRepository;

    @Autowired
    public RiskConfigWritePlatformServiceImpl(final PlatformSecurityContext context, final RiskDataValidator validator,
                                              RiskConfigDataAssembler riskConfigDataAssembler,
                                              RuleRepository ruleRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = riskConfigDataAssembler;
        this.ruleRepository = ruleRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createRule(EntityRuleType ruleType, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForCreateRulePurpose(command.json());
            final RuleModel ruleModel = this.assembler.assembleCreateRule(ruleType,command);
            this.ruleRepository.save(ruleModel);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(ruleModel.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
//            handleDataIntegrityIssues(command, dve);
//            return CommandProcessingResult.empty();
        }
        return null;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateRule(Long ruleId, EntityRuleType ruleType, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final RuleModel ruleModel = this.ruleRepository.findOneByIdAndEntityType(ruleId,ruleType.getValue());
            this.validator.validateForUpdateRulePurpose(command.json());
            this.assembler.assembleUpdateRule(ruleModel,ruleType,command);
            this.ruleRepository.save(ruleModel);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(ruleModel.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
//            handleDataIntegrityIssues(command, dve);
//            return CommandProcessingResult.empty();
        }
        return null;
    }
}