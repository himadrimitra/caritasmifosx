package com.finflux.portfolio.loan.purpose.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;
import com.finflux.portfolio.loan.purpose.domain.LoanPurpose;
import com.finflux.portfolio.loan.purpose.domain.LoanPurposeGroup;
import com.finflux.portfolio.loan.purpose.domain.LoanPurposeGroupMapping;
import com.finflux.portfolio.loan.purpose.domain.LoanPurposeGroupRepositoryWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanPurposeGroupDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final LoanPurposeGroupRepositoryWrapper loanPurposeGroupRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;

    @Autowired
    public LoanPurposeGroupDataAssembler(final FromJsonHelper fromApiJsonHelper,
            final LoanPurposeGroupRepositoryWrapper loanPurposeGroupRepository, final CodeValueRepositoryWrapper codeValueRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.loanPurposeGroupRepository = loanPurposeGroupRepository;
        this.codeValueRepository = codeValueRepository;
    }

    public LoanPurposeGroup assembleCreateLoanPurposeGroupForm(final JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final String name = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.nameParamName, element);
        final String systemCode = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.systemCodeParamName, element);
        final String description = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.descriptionParamName, element);
        final Long loanPurposeGroupTypeId = this.fromApiJsonHelper.extractLongNamed(
                LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName, element);
        final CodeValue loanPurposeGroupType = this.codeValueRepository.findOneWithNotFoundDetection(loanPurposeGroupTypeId);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanPurposeGroupApiConstants.isActiveParamName, element);
        if (isActive == null) {
            isActive = true;
        }
        return LoanPurposeGroup.create(name, systemCode, description, loanPurposeGroupType, isActive);
    }

    public Map<String, Object> assembleUpdateLoanPurposeGroupForm(final LoanPurposeGroup loanPurposeGroup, final JsonCommand command) {
        final Map<String, Object> actualChanges = loanPurposeGroup.update(command);
        if (!actualChanges.isEmpty()) {
            if (actualChanges.containsKey(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName)) {
                final Long loanPurposeGroupTypeId = command
                        .longValueOfParameterNamed(LoanPurposeGroupApiConstants.loanPurposeGroupTypeIdParamName);
                final CodeValue loanPurposeGroupType = this.codeValueRepository.findOneWithNotFoundDetection(loanPurposeGroupTypeId);
                loanPurposeGroup.updateLoanPurposeGroupType(loanPurposeGroupType);
            }
        }
        return actualChanges;
    }

    @SuppressWarnings("unused")
    public LoanPurpose assembleCreateLoanPurposeForm(final JsonCommand command) {

        final JsonElement element = command.parsedJson();
        final String name = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.nameParamName, element);
        final String systemCode = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.systemCodeParamName, element);
        final String description = this.fromApiJsonHelper.extractStringNamed(LoanPurposeGroupApiConstants.descriptionParamName, element);
        Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(LoanPurposeGroupApiConstants.isActiveParamName, element);
        if (isActive == null) {
            isActive = true;
        }
        final LoanPurpose loanPurpose = LoanPurpose.create(name, systemCode, description, isActive);

        if (this.fromApiJsonHelper.parameterExists(LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName, element)) {
            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
                    LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName, element);
            if (loanPurpose != null && loanPurposeGroupIds != null) {
                final Set<String> loanPurposeGroupIdsSet = Arrays.stream(loanPurposeGroupIds).collect(Collectors.toSet());
                final List<LoanPurposeGroupMapping> loanPurposeGroupMappings = new ArrayList<LoanPurposeGroupMapping>();
                for (final String id : loanPurposeGroupIdsSet) {
                    final Long loanPurposeGroupId = Long.parseLong(id);
                    final LoanPurposeGroup loanPurposeGroup = this.loanPurposeGroupRepository
                            .findOneWithNotFoundDetection(loanPurposeGroupId);
                    final LoanPurposeGroupMapping loanPurposeGroupMapping = LoanPurposeGroupMapping.create(loanPurposeGroup, isActive);
                    loanPurposeGroupMappings.add(loanPurposeGroupMapping);
                }
                loanPurpose.addAllLoanPurposeGroupMapping(loanPurposeGroupMappings);
            }
        }

        return loanPurpose;
    }

    @SuppressWarnings("unused")
    public Map<String, Object> assembleUpdateLoanPurposeForm(final LoanPurpose loanPurpose, final JsonCommand command) {
        final Map<String, Object> changes = loanPurpose.update(command);
        final JsonElement element = command.parsedJson();
        final Set<LoanPurposeGroupMapping> loanPurposeGroupMappings = new LinkedHashSet<LoanPurposeGroupMapping>();
        if (this.fromApiJsonHelper.parameterExists(LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName, element)) {
            final String[] loanPurposeGroupIds = this.fromApiJsonHelper.extractArrayNamed(
                    LoanPurposeGroupApiConstants.loanPurposeGroupIdsParamName, element);
            if (loanPurposeGroupIds != null) {
                final Set<String> loanPurposeGroupIdsSet = Arrays.stream(loanPurposeGroupIds).collect(Collectors.toSet());
                for (final String id : loanPurposeGroupIdsSet) {
                    final Long loanPurposeGroupId = Long.parseLong(id);
                    final LoanPurposeGroup loanPurposeGroup = this.loanPurposeGroupRepository
                            .findOneWithNotFoundDetection(loanPurposeGroupId);
                    final LoanPurposeGroupMapping loanPurposeGroupMapping = LoanPurposeGroupMapping.create(loanPurposeGroup,
                            loanPurpose.isActive());
                    loanPurposeGroupMappings.add(loanPurposeGroupMapping);
                }
            }
        }
        loanPurpose.updateAllLoanPurposeGroupMapping(loanPurposeGroupMappings);
        return changes;
    }
}