package com.finflux.risk.profilerating.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.api.ComputeProfileRatingApiConstants;
import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;
import com.finflux.risk.profilerating.data.ComputeProfileRatingDataValidator;
import com.finflux.risk.profilerating.data.ProfileRatingDataLayer;
import com.finflux.risk.profilerating.data.ProfileRatingRunStatus;
import com.finflux.risk.profilerating.data.ProfileRatingType;
import com.finflux.risk.profilerating.data.ScopeEntityType;
import com.finflux.risk.profilerating.domain.ProfileRatingRun;
import com.finflux.risk.profilerating.domain.ProfileRatingRunRepositoryWrapper;
import com.finflux.risk.profilerating.domain.ProfileRatingScore;
import com.finflux.risk.profilerating.domain.ProfileRatingScoreHistory;
import com.finflux.risk.profilerating.domain.ProfileRatingScoreHistoryRepositoryWrapper;
import com.finflux.risk.profilerating.domain.ProfileRatingScoreRepositoryWrapper;
import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.task.data.TaskConfigKey;
import com.google.gson.Gson;

@Service
public class ComputeProfileRatingWritePlatformServiceImpl implements ComputeProfileRatingWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ComputeProfileRatingWritePlatformServiceImpl.class);

    private PlatformSecurityContext context;
    private final ComputeProfileRatingDataValidator validator;
    private final ComputeProfileRatingDataAssembler assembler;
    private final ProfileRatingRunRepositoryWrapper repository;
    private final ExecutorService executorService;
    private final ComputeProfileRatingReadPlatformService readPlatformService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final RuleExecutionService ruleExecutionService;
    private final Gson gson;
    private final ProfileRatingScoreRepositoryWrapper profileRatingScoreRepository;
    private final ProfileRatingScoreHistoryRepositoryWrapper profileRatingScoreHistoryRepository;

    @Autowired
    public ComputeProfileRatingWritePlatformServiceImpl(final PlatformSecurityContext context,
            final ComputeProfileRatingDataValidator validator, final ComputeProfileRatingDataAssembler assembler,
            final ProfileRatingRunRepositoryWrapper repository, final ComputeProfileRatingReadPlatformService readPlatformService,
            final DataLayerReadPlatformService dataLayerReadPlatformService, final RuleExecutionService ruleExecutionService,
            final ProfileRatingScoreRepositoryWrapper profileRatingScoreRepository,
            final ProfileRatingScoreHistoryRepositoryWrapper profileRatingScoreHistoryRepository) {
        this.context = context;
        this.validator = validator;
        this.assembler = assembler;
        this.repository = repository;
        this.executorService = Executors.newSingleThreadExecutor();
        this.readPlatformService = readPlatformService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.ruleExecutionService = ruleExecutionService;
        this.gson = new Gson();
        this.profileRatingScoreRepository = profileRatingScoreRepository;
        this.profileRatingScoreHistoryRepository = profileRatingScoreHistoryRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CommandProcessingResult computeProfileRating(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateForComputeProfileRating(command.json());
            final ProfileRatingRun profileRatingRun = this.assembler.assembleComputeProfileRating(command);
            this.repository.save(profileRatingRun);
            @SuppressWarnings("rawtypes")
            final List<ProfileRatingRun> profileRatingRuns = new ArrayList();
            profileRatingRuns.add(profileRatingRun);
            processComputeAllProfileRatings(profileRatingRuns, command);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(profileRatingRun.getId())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void processComputeAllProfileRatings(final List<ProfileRatingRun> profileRatingRuns, final JsonCommand command) {
        if (command.hasParameter(ComputeProfileRatingApiConstants.entityIdParamName)) {
            computeAllProfileRatings(profileRatingRuns, command);
        } else {
            /**
             * If we are execute multiple recodes then it should be in back
             * ground execution process
             */
            this.executorService.execute(new RunBackGroundProcessForProfileRatings(ThreadLocalContextUtil.getTenant(), profileRatingRuns,
                    command, SecurityContextHolder.getContext().getAuthentication()));
        }
    }

    class RunBackGroundProcessForProfileRatings implements Runnable, ApplicationListener<ContextClosedEvent> {

        private final FineractPlatformTenant tenant;
        private final List<ProfileRatingRun> profileRatingRuns;
        private final JsonCommand command;
        private final Authentication auth;

        public RunBackGroundProcessForProfileRatings(final FineractPlatformTenant tenant, final List<ProfileRatingRun> profileRatingRuns,
                final JsonCommand command, final Authentication auth) {
            this.tenant = tenant;
            this.profileRatingRuns = profileRatingRuns;
            this.command = command;
            this.auth = auth;
        }

        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(this.tenant);
            if (this.auth != null) {
                SecurityContextHolder.getContext().setAuthentication(this.auth);
            }
            computeAllProfileRatings(this.profileRatingRuns, this.command);
        }

        @SuppressWarnings("unused")
        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            executorService.shutdown();
            logger.info("Shutting down the ExecutorService");
        }
    }

    public void computeAllProfileRatings(final List<ProfileRatingRun> profileRatingRuns, final JsonCommand command) {
        if (profileRatingRuns != null && !profileRatingRuns.isEmpty()) {
            for (final ProfileRatingRun profileRatingRun : profileRatingRuns) {
                processEachProfileRatingRun(profileRatingRun, command);
            }
        }
    }

    private void processEachProfileRatingRun(final ProfileRatingRun profileRatingRun, final JsonCommand command) {
        try {
            final Integer entityType = profileRatingRun.getEntityType();
            Integer overriddenScore = null;
            TaskConfigKey taskConfigKey = null;
            String dataLayerKey = null;
            if (command.hasParameter(ComputeProfileRatingApiConstants.overriddenScoreParamName)) {
                overriddenScore = command.integerValueOfParameterNamed(ComputeProfileRatingApiConstants.overriddenScoreParamName);
            }
            Long entityId = null;
            if (command.hasParameter(ComputeProfileRatingApiConstants.entityIdParamName)) {
                entityId = command.longValueOfParameterNamed(ComputeProfileRatingApiConstants.entityIdParamName);
            }
            if (profileRatingRun.getScopeEntityType() != null && profileRatingRun.getScopeEntityId() != null) {
                final Map<String, Object> dataLayerKeyLongMap = new HashMap<>();
                List<Map<String, Object>> idMapList = new ArrayList<>();
                if (ProfileRatingType.CLIENT.getValue().equals(ProfileRatingType.fromInt(entityType).getValue())) {
                    dataLayerKey = DataLayerKey.CLIENT_ID.getValue();
                    taskConfigKey = TaskConfigKey.CLIENT_ID;
                    idMapList = getClientIds(profileRatingRun, entityId);
                } else if (ProfileRatingType.GROUP.getValue().equals(ProfileRatingType.fromInt(entityType).getValue())) {
                    dataLayerKey = DataLayerKey.GROUP_ID.getValue();
                    taskConfigKey = TaskConfigKey.GROUP_ID;
                    idMapList = getGroupIds(profileRatingRun, entityId);
                } else if (ProfileRatingType.CENTER.getValue().equals(ProfileRatingType.fromInt(entityType).getValue())) {
                    dataLayerKey = DataLayerKey.CENETR_ID.getValue();
                    taskConfigKey = TaskConfigKey.CENTER_ID;
                    idMapList = getCenterIds(profileRatingRun, entityId);
                } else if (ProfileRatingType.CENTER.getValue().equals(ProfileRatingType.fromInt(entityType).getValue())) {
                    dataLayerKey = DataLayerKey.VILLAGE_ID.getValue();
                    taskConfigKey = TaskConfigKey.VILLAGE_ID;
                    idMapList = getVillageIds(profileRatingRun, entityId);
                }
                if (dataLayerKey != null && taskConfigKey != null && !idMapList.isEmpty()) {
                    for (final Map<String, Object> idMap : idMapList) {
                        if (idMap.containsKey(taskConfigKey.getValue()) && idMap.get(taskConfigKey.getValue()) != null) {
                            dataLayerKeyLongMap.put(dataLayerKey, Long.valueOf(idMap.get(taskConfigKey.getValue()).toString()));
                            final ProfileRatingDataLayer dataLayer = new ProfileRatingDataLayer(this.dataLayerReadPlatformService);
                            dataLayer.build(dataLayerKeyLongMap);
                            final RuleResult ruleResult = this.ruleExecutionService.executeARule(profileRatingRun.getCriteria().getId(),
                                    dataLayer);
                            if (ruleResult != null) {
                                entityId = Long.valueOf(idMap.get(taskConfigKey.getValue()).toString());
                                saveProfileRatingScore(profileRatingRun, ruleResult, overriddenScore, entityId);
                            }
                        }
                    }
                }
            }
            profileRatingRun.setStatus(ProfileRatingRunStatus.COMPLETED.getValue());
            profileRatingRun.setEndTime(new Date());
            this.repository.save(profileRatingRun);
        } catch (Exception e) {
            logger.warn("Exception in processEachProfileRatingRun", e);
            profileRatingRun.setStatus(ProfileRatingRunStatus.ERROR.getValue());
            profileRatingRun.setEndTime(new Date());
            this.repository.save(profileRatingRun);
        }
    }

    private final List<Map<String, Object>> getClientIds(final ProfileRatingRun profileRatingRun, final Long entityId) {
        final List<Map<String, Object>> clientIdMapList = new ArrayList<>();
        if (entityId == null
                && ScopeEntityType.OFFICE.getValue().equals(
                        ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Long officeId = profileRatingRun.getScopeEntityId();
            clientIdMapList.addAll(this.readPlatformService.getAllClientIdsFromOffice(officeId));
        } else if (ScopeEntityType.OFFICE.getValue().equals(
                ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Map<String, Object> clientIdMap = new LinkedHashMap<>();
            clientIdMap.put("clientId", entityId);
            clientIdMapList.add(clientIdMap);
        }
        return clientIdMapList;
    }

    private final List<Map<String, Object>> getCenterIds(final ProfileRatingRun profileRatingRun, final Long entityId) {
        final List<Map<String, Object>> centerIdMapList = new ArrayList<>();
        if (entityId == null
                && ScopeEntityType.OFFICE.getValue().equals(
                        ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Long officeId = profileRatingRun.getScopeEntityId();
            centerIdMapList.addAll(this.readPlatformService.getAllCenterIdsFromOffice(officeId));
        } else if (ScopeEntityType.OFFICE.getValue().equals(
                ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Map<String, Object> centerIdMap = new LinkedHashMap<>();
            centerIdMap.put("centerId", entityId);
            centerIdMapList.add(centerIdMap);
        }
        return centerIdMapList;
    }

    private List<Map<String, Object>> getGroupIds(final ProfileRatingRun profileRatingRun, final Long entityId) {
        final List<Map<String, Object>> groupIdMapList = new ArrayList<>();
        if (entityId == null
                && ScopeEntityType.OFFICE.getValue().equals(
                        ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Long officeId = profileRatingRun.getScopeEntityId();
            groupIdMapList.addAll(this.readPlatformService.getAllGroupIdsFromOffice(officeId));
        } else if (ScopeEntityType.OFFICE.getValue().equals(
                ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Map<String, Object> groupIdMap = new LinkedHashMap<>();
            groupIdMap.put("groupId", entityId);
            groupIdMapList.add(groupIdMap);
        }
        return groupIdMapList;
    }

    private List<Map<String, Object>> getVillageIds(final ProfileRatingRun profileRatingRun, final Long entityId) {
        final List<Map<String, Object>> villageIdMapList = new ArrayList<>();
        if (entityId == null
                && ScopeEntityType.OFFICE.getValue().equals(
                        ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Long officeId = profileRatingRun.getScopeEntityId();
            villageIdMapList.addAll(this.readPlatformService.getAllVillageIdsFromOffice(officeId));
        } else if (ScopeEntityType.OFFICE.getValue().equals(
                ScopeEntityType.fromInt(profileRatingRun.getScopeEntityType().intValue()).getValue())) {
            final Map<String, Object> villageIdMap = new LinkedHashMap<>();
            villageIdMap.put("villageId", entityId);
            villageIdMapList.add(villageIdMap);
        }
        return villageIdMapList;
    }

    private void saveProfileRatingScore(final ProfileRatingRun profileRatingRun, final RuleResult ruleResult,
            final Integer overriddenScore, final Long entityId) {
        final Integer entityType = profileRatingRun.getEntityType();
        ProfileRatingScore profileRatingScore = this.profileRatingScoreRepository.findOneByEntityTypeAndEntityId(entityType, entityId);
        Integer computedScore = null;
        if (ruleResult.getOutput() != null && ruleResult.getOutput().getValue() != null) {
            computedScore = Integer.parseInt(ruleResult.getOutput().getValue());
        }
        Integer finalScore = null;
        AppUser overriddenBy = null;
        if (overriddenScore != null) {
            finalScore = overriddenScore;
            overriddenBy = this.context.authenticatedUser();
        } else {
            finalScore = computedScore;
        }
        final String criteriaResult = this.gson.toJson(ruleResult);
        final Date updatedTime = new Date();

        if (profileRatingScore != null) {
            final ProfileRatingScoreHistory profileRatingScoreHistory = ProfileRatingScoreHistory.create(profileRatingScore
                    .getProfileRatingRun().getId(), profileRatingScore.getEntityType(), profileRatingScore.getEntityId(),
                    profileRatingScore.getComputedScore(), profileRatingScore.getOverriddenScore(), profileRatingScore.getFinalScore(),
                    profileRatingScore.getCriteriaResult(), profileRatingScore.getUpdatedTime(), profileRatingScore.getOverriddenBy());
            this.profileRatingScoreHistoryRepository.save(profileRatingScoreHistory);
            profileRatingScore.update(profileRatingRun, entityType, entityId, computedScore, overriddenScore, finalScore, criteriaResult,
                    updatedTime, overriddenBy);
        } else {
            profileRatingScore = ProfileRatingScore.create(profileRatingRun, entityType, entityId, computedScore, overriddenScore,
                    finalScore, criteriaResult, updatedTime, overriddenBy);

        }
        this.profileRatingScoreRepository.save(profileRatingScore);
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
