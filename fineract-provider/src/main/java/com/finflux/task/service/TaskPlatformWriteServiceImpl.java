package com.finflux.task.service;

import java.util.*;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.RoleReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepositoryWrapper;
import com.finflux.loanapplicationreference.service.LoanApplicationReferenceReadPlatformService;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import com.finflux.ruleengine.lib.service.impl.MyExpressionExecutor;
import com.finflux.task.data.*;
import com.finflux.task.domain.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class TaskPlatformWriteServiceImpl implements TaskPlatformWriteService {

    private final TaskExecutionService taskExecutionService;
    private final TaskPlatformReadService taskPlatformReadService;
    private final TaskConfigRepositoryWrapper taskConfigRepository;
    private final TaskRepositoryWrapper taskRepository;
    private final RoleReadPlatformService roleReadPlatformService;
    private final PlatformSecurityContext context;
    private final RuleExecutionService ruleExecutionService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
    private final ExpressionExecutor expressionExecutor;
    private final TaskActionRepository taskActionRepository;
    private final TaskActionRoleRepository actionRoleRepository;
    private final TaskActionLogRepository actionLogRepository;

    @Autowired
    public TaskPlatformWriteServiceImpl(final TaskPlatformReadService taskPlatformReadService, final TaskConfigRepositoryWrapper taskConfigRepository,
										final TaskRepositoryWrapper taskRepository, final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository,
										final RoleReadPlatformService roleReadPlatformService, final PlatformSecurityContext context,
										final RuleExecutionService ruleExecutionService, final DataLayerReadPlatformService dataLayerReadPlatformService,
										final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
										final MyExpressionExecutor expressionExecutor, final TaskActionRepository taskActionRepository,
										final TaskActionRoleRepository actionRoleRepository, final TaskActionLogRepository actionLogRepository,
                                        final TaskExecutionService taskExecutionService) {
        this.taskPlatformReadService = taskPlatformReadService;
        this.taskConfigRepository = taskConfigRepository;
        this.taskRepository = taskRepository;
        this.roleReadPlatformService = roleReadPlatformService;
        this.context = context;
        this.ruleExecutionService = ruleExecutionService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.loanApplicationReferenceReadPlatformService = loanApplicationReferenceReadPlatformService;
        this.expressionExecutor = expressionExecutor;
        this.taskActionRepository = taskActionRepository;
        this.actionRoleRepository = actionRoleRepository;
        this.actionLogRepository = actionLogRepository;
        this.taskExecutionService = taskExecutionService;
    }

    @SuppressWarnings("unused")
    @Transactional
    @Override
    public void createTaskFromConfig(final Long taskConfigId, final TaskEntityType entityType, final Long entityId, final Client client,
                                     final Office office, final Map<TaskConfigKey, String> configValues, String description) {

        Map<String, String> customConfigMap = new HashMap<>();
        for (Map.Entry<TaskConfigKey, String> config : configValues.entrySet()) {
            customConfigMap.put(config.getKey().getValue(), config.getValue());
        }

        Task parentTask = createTaskFromConfig(entityType, entityId, client, office, customConfigMap, taskConfigId, description);
        parentTask = this.taskRepository.save(parentTask);

        final List<Long> childTaskConfigIds = this.taskPlatformReadService.getChildTaskConfigIds(taskConfigId);

        if (!childTaskConfigIds.isEmpty()) {
            int index = 0;
            final List<Task> tasks = new ArrayList<Task>();
            for (final Long cTaskConfigId : childTaskConfigIds) {
                Task task = createTaskFromConfig(entityType, entityId, client, office, customConfigMap, cTaskConfigId, description);
                task.setParent(parentTask);
                if (index == 0) {
                    TaskStatusType status = TaskStatusType.INITIATED;
                    task.setStatus(status.getValue());
                    taskExecutionService.updateActionAndAssignedTo(task, status);
                }
                tasks.add(task);
                index++;
            }
            if (!tasks.isEmpty()) {
                this.taskRepository.save(tasks);
            }
        }
    }

    private Task createTaskFromConfig(TaskEntityType entityType, Long entityId, Client client, Office office,
            Map<String, String> customConfigMap, Long cTaskConfigId, String description) {
        final TaskConfig taskConfig = this.taskConfigRepository.findOneWithNotFoundDetection(cTaskConfigId);
        TaskStatusType status = TaskStatusType.INACTIVE;
        Map<String, String> configValueMap = new HashMap<>();
        if (taskConfig.getConfigValues() != null) {
            configValueMap = new Gson().fromJson(taskConfig.getConfigValues(), new TypeToken<HashMap<String, String>>() {}.getType());
        }
        configValueMap.putAll(customConfigMap);
        final String configValueStr = new Gson().toJson(configValueMap);
        final Task parent = null;
        final Date dueDate = null;
        final Integer currentAction = null;
        AppUser assignedTo = null;
        final String criteriaResult = null;
        final Integer criteriaAction = null;

        return Task.create(parent, taskConfig.getName(), taskConfig.getShortName(), entityType.getValue(), entityId,
                taskConfig.getTaskType(), taskConfig, status.getValue(), TaskPriority.MEDIUM.getValue(), dueDate, currentAction,
                assignedTo, taskConfig.getTaskConfigOrder(), taskConfig.getCriteria(), taskConfig.getApprovalLogic(),
                taskConfig.getRejectionLogic(), configValueStr, client, office, taskConfig.getActionGroupId(), criteriaResult,
                criteriaAction, taskConfig.getTaskActivity(), description);
    }

    /*
     * @Override public TaskExecutionData getWorkflowExecutionData(final Long
     * workflowExecutionId) { return
     * this.taskReadService.getTaskData(workflowExecutionId); }
     */

    @Override
    public Long createSingleTask(TaskActivity taskActivity, String title, Office office, Map<TaskConfigKey, String> map,
                                 Long actionGroupId) {
        Map<String, String> customConfigMap = new HashMap<>();
        if(map!=null) {
            for (Map.Entry<TaskConfigKey, String> config : map.entrySet()) {
                customConfigMap.put(config.getKey().getValue(), config.getValue());
            }
        }

        final String configValueStr = new Gson().toJson(map);
        Task task = Task.create(null, title, taskActivity.getIdentifier().substring(0,3), null, null,
                TaskType.SINGLE.getValue(), null, null, TaskPriority.MEDIUM.getValue(), null,
                null, null, null, null, null,
                null, configValueStr, null, office, actionGroupId, null,
                null, taskActivity, null);
        TaskStatusType status = TaskStatusType.INITIATED;
        task.setStatus(status.getValue());
        taskExecutionService.updateActionAndAssignedTo(task, status);
        return task.getId();
    }

    @SuppressWarnings({})
    private void runCriteriaCheckAndPopulate(final Task task) {
        /**
         * We will reuse this code in future
         */
        /*
         * final WorkflowStep workflowStep =
         * workflowStepRepository.findOne(workflowExecutionStep
         * .getWorkflowStepId()); Long loanApplicationId =
         * loanApplicationWorkflowExecution.getLoanApplicationId();
         * LoanApplicationReferenceData loanApplicationReference =
         * loanApplicationReferenceReadPlatformService
         * .retrieveOne(loanApplicationId); Long clientId =
         * loanApplicationReference.getClientId(); LoanApplicationDataLayer
         * dataLayer = new LoanApplicationDataLayer(loanApplicationId, clientId,
         * dataLayerReadPlatformService, ruleCacheService); RuleResult
         * ruleResult =
         * ruleExecutionService.executeCriteria(workflowStep.getCriteriaId(),
         * dataLayer); EligibilityResult eligibilityResult = new
         * EligibilityResult();
         * eligibilityResult.setStatus(EligibilityStatus.TO_BE_REVIEWED);
         * eligibilityResult.setCriteriaOutput(ruleResult); ExpressionNode
         * approvalLogic = new Gson().fromJson(workflowStep.getApprovalLogic(),
         * new TypeToken<ExpressionNode>() {}.getType()); ExpressionNode
         * rejectionLogic = new
         * Gson().fromJson(workflowStep.getRejectionLogic(), new
         * TypeToken<ExpressionNode>() {}.getType()); if (ruleResult != null &&
         * ruleResult.getOutput().getValue() != null) { Map<String, Object> map
         * = new HashMap(); map.put("criteria",
         * ruleResult.getOutput().getValue()); boolean rejectionResult = false;
         * boolean approvalResult = false; try { rejectionResult =
         * expressionExecutor.executeExpression(rejectionLogic, map); } catch
         * (FieldUndefinedException e) { e.printStackTrace(); } catch
         * (InvalidExpressionException e) { e.printStackTrace(); } if
         * (rejectionResult) {
         * eligibilityResult.setStatus(EligibilityStatus.REJECTED); } else { try
         * { approvalResult =
         * expressionExecutor.executeExpression(approvalLogic, map); } catch
         * (FieldUndefinedException e) { e.printStackTrace(); } catch
         * (InvalidExpressionException e) { e.printStackTrace(); } } if
         * (approvalResult) {
         * eligibilityResult.setStatus(EligibilityStatus.APPROVED); } }
         * StepAction nextEligibleAction = StepAction.REVIEW; if
         * (EligibilityStatus.APPROVED.equals(eligibilityResult.getStatus())) {
         * nextEligibleAction = StepAction.APPROVE; } else if
         * (EligibilityStatus.REJECTED.equals(eligibilityResult.getStatus())) {
         * nextEligibleAction = StepAction.REJECT; }
         * workflowExecutionStep.setCriteriaAction
         * (nextEligibleAction.getValue());
         * workflowExecutionStep.setCriteriaResult(new
         * Gson().toJson(eligibilityResult));
         */
    }
}
