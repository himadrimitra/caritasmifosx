package com.finflux.risk.profilerating.service;

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

import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;
import com.finflux.risk.profilerating.data.ProfileRatingConfigDataValidator;
import com.finflux.risk.profilerating.domain.ProfileRatingConfig;
import com.finflux.risk.profilerating.domain.ProfileRatingConfigRepositoryWrapper;
import com.finflux.risk.profilerating.exception.ProfileRatingConfigNotFoundException;

@Service
public class ProfileRatingConfigWritePlatformServiceImpl implements ProfileRatingConfigWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ProfileRatingConfigWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final ProfileRatingConfigDataValidator validator;
    private final ProfileRatingConfigDataAssembler assembler;
    private final ProfileRatingConfigRepositoryWrapper repository;

    @Autowired
    public ProfileRatingConfigWritePlatformServiceImpl(final PlatformSecurityContext context,
            final ProfileRatingConfigDataValidator validator, final ProfileRatingConfigDataAssembler assembler,
            final ProfileRatingConfigRepositoryWrapper repository) {
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
            final ProfileRatingConfig profileRatingConfig = this.assembler.assembleCreateForm(command);
            this.repository.save(profileRatingConfig);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(profileRatingConfig.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult update(final Long profileRatingConfigId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final ProfileRatingConfig profileRatingConfig = this.repository.findOneWithNotFoundDetection(profileRatingConfigId);
            this.validator.validateForUpdate(command.json());
            final Map<String, Object> changes = this.assembler.assembleUpdateForm(profileRatingConfig, command);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(profileRatingConfigId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult activate(final Long profileRatingConfigId, JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final ProfileRatingConfig profileRatingConfig = this.repository.findOneWithNotFoundDetection(profileRatingConfigId);
            if (profileRatingConfig.isActive()) { throw new ProfileRatingConfigNotFoundException(profileRatingConfigId, "activated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(ProfileRatingConfigApiConstants.isActiveParamName, true);
            profileRatingConfig.activate();
            this.repository.save(profileRatingConfig);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(profileRatingConfigId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActivate(final Long profileRatingConfigId, JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final ProfileRatingConfig profileRatingConfig = this.repository.findOneWithNotFoundDetection(profileRatingConfigId);
            if (!profileRatingConfig.isActive()) { throw new ProfileRatingConfigNotFoundException(profileRatingConfigId, "inactivated"); }
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            changes.put(ProfileRatingConfigApiConstants.isActiveParamName, false);
            profileRatingConfig.activate();
            this.repository.save(profileRatingConfig);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(profileRatingConfigId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    /**
     * Guaranteed to throw an exception no matter what the data integrity issues
     * 
     * @param command
     * @param dve
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("f_profile_rating_config_UNIQUE")) {
            final Integer typeId = command.integerValueOfParameterNamed(ProfileRatingConfigApiConstants.typeParamName);
            final Integer criteriaId = command.integerValueOfParameterNamed(ProfileRatingConfigApiConstants.criteriaIdParamName);
            throw new PlatformDataIntegrityException("error.msg.profile.rating.config.type.and.criteria.duplicated",
                    "Profile rating config type `" + typeId + "` and criteria id `" + criteriaId + "` already exists");
        }
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.profile.rating.config.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}