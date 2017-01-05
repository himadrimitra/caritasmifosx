package com.finflux.risk.profilerating.service;

import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;
import com.finflux.risk.profilerating.domain.ProfileRatingConfig;
import com.finflux.ruleengine.configuration.domain.RuleModel;
import com.finflux.ruleengine.configuration.domain.RuleRepositoryWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ProfileRatingConfigDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final RuleRepositoryWrapper ruleRepository;

    @Autowired
    public ProfileRatingConfigDataAssembler(final FromJsonHelper fromApiJsonHelper, final RuleRepositoryWrapper ruleRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.ruleRepository = ruleRepository;
    }

    public ProfileRatingConfig assembleCreateForm(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Integer type = this.fromApiJsonHelper.extractIntegerNamed(ProfileRatingConfigApiConstants.typeParamName, element, locale);
        final Long criteriaId = this.fromApiJsonHelper.extractLongNamed(ProfileRatingConfigApiConstants.criteriaIdParamName, element);
        final RuleModel criteria = this.ruleRepository.findOneWithNotFoundDetection(criteriaId);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(ProfileRatingConfigApiConstants.isActiveParamName, element);
        if (isActive == null) {
            isActive = true;
        }
        return ProfileRatingConfig.create(type, criteria, isActive);
    }

    public Map<String, Object> assembleUpdateForm(final ProfileRatingConfig profileRatingConfig, final JsonCommand command) {
        final Map<String, Object> actualChanges = profileRatingConfig.update(command);
        if (!actualChanges.isEmpty()) {
            if (actualChanges.containsKey(ProfileRatingConfigApiConstants.criteriaIdParamName)) {
                final Long criteriaId = command.longValueOfParameterNamed(ProfileRatingConfigApiConstants.criteriaIdParamName);
                final RuleModel criteria = this.ruleRepository.findOneWithNotFoundDetection(criteriaId);
                profileRatingConfig.updateCriteria(criteria);
            }
        }
        return actualChanges;
    }
}