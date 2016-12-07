package com.finflux.task.execution.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
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
import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import com.finflux.ruleengine.lib.service.impl.MyExpressionExecutor;
import com.finflux.task.configuration.domain.TaskConfig;
import com.finflux.task.configuration.domain.TaskConfigRepositoryWrapper;
import com.finflux.task.execution.data.TaskActionType;
import com.finflux.task.execution.data.TaskConfigKey;
import com.finflux.task.execution.data.TaskData;
import com.finflux.task.execution.data.TaskEntityType;
import com.finflux.task.execution.data.TaskPriority;
import com.finflux.task.execution.data.TaskStatus;
import com.finflux.task.execution.domain.Task;
import com.finflux.task.execution.domain.TaskRepositoryWrapper;
import com.finflux.task.execution.service.TaskExecutionService;
import com.finflux.task.execution.service.TaskReadService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final TaskReadService taskReadService;
    private final TaskConfigRepositoryWrapper taskConfigRepository;
    private final TaskRepositoryWrapper taskRepository;
    private final RoleReadPlatformService roleReadPlatformService;
    private final PlatformSecurityContext context;
    private final RuleExecutionService ruleExecutionService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final RuleCacheService ruleCacheService;
    private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
    private final ExpressionExecutor expressionExecutor;

    @Autowired
    public TaskExecutionServiceImpl(final TaskReadService taskReadService, final TaskConfigRepositoryWrapper taskConfigRepository,
            final TaskRepositoryWrapper taskRepository, final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository,
            final RoleReadPlatformService roleReadPlatformService, final PlatformSecurityContext context,
            final RuleExecutionService ruleExecutionService, final DataLayerReadPlatformService dataLayerReadPlatformService,
            final RuleCacheService ruleCacheService,
            final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
            final MyExpressionExecutor expressionExecutor) {
        this.taskReadService = taskReadService;
        this.taskConfigRepository = taskConfigRepository;
        this.taskRepository = taskRepository;
        this.roleReadPlatformService = roleReadPlatformService;
        this.context = context;
        this.ruleCacheService = ruleCacheService;
        this.ruleExecutionService = ruleExecutionService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.loanApplicationReferenceReadPlatformService = loanApplicationReferenceReadPlatformService;
        this.expressionExecutor = expressionExecutor;
    }

    @SuppressWarnings("unused")
    @Transactional
    @Override
    public void createTaskConfigExecution(final Long taskConfigId, final TaskEntityType entityType, final Long entityId,
            final Client client, final Office office, final Map<TaskConfigKey, String> configValues) {
        final List<Long> childTaskConfigIds = this.taskReadService.getChildTaskConfigIds(taskConfigId);
        Map<String, String> customConfigMap = new HashMap<>();
        for (Map.Entry<TaskConfigKey, String> config : configValues.entrySet()) {
            customConfigMap.put(config.getKey().getValue(), config.getValue());
        }
        if (!childTaskConfigIds.isEmpty()) {
            int index = 0;
            final List<Task> tasks = new ArrayList<Task>();
            for (final Long cTaskConfigId : childTaskConfigIds) {
                final TaskConfig taskConfig = this.taskConfigRepository.findOneWithNotFoundDetection(cTaskConfigId);
                TaskStatus status = TaskStatus.INACTIVE;
                Map<String, String> configValueMap = new HashMap<>();
                if (taskConfig.getConfigValues() != null) {
                    configValueMap = new Gson().fromJson(taskConfig.getConfigValues(),
                            new TypeToken<HashMap<String, String>>() {}.getType());
                }
                configValueMap.putAll(customConfigMap);
                final String configValueStr = new Gson().toJson(configValueMap);
                final Task parent = null;
                final Integer taskType = 1;
                final Date dueDate = null;
                final Integer currentAction = null;
                AppUser assignedTo = null;
                final String criteriaResult = null;
                final Integer criteriaAction = null;
                if (index == 0) {
                    status = TaskStatus.INITIATED;
                    assignedTo = this.context.authenticatedUser();
                }
                final Task task = Task.create(parent, taskConfig.getName(), taskConfig.getShortName(), entityType.getValue(), entityId,
                        taskType, taskConfig, status.getValue(), TaskPriority.MEDIUM.getValue(), dueDate, currentAction, assignedTo,
                        taskConfig.getTaskConfigOrder(), taskConfig.getCriteria(), taskConfig.getApprovalLogic(),
                        taskConfig.getRejectionLogic(), configValueStr, client, office, taskConfig.getActionGroupId(), criteriaResult,
                        criteriaAction);
                tasks.add(task);
                index++;
            }
            if (!tasks.isEmpty()) {
                this.taskRepository.save(tasks);
            }
        }
    }

   /* @Override
    public TaskExecutionData getWorkflowExecutionData(final Long workflowExecutionId) {
        return this.taskReadService.getTaskExecutionData(workflowExecutionId);
    }*/

    
    /*@Override
    @Transactional
    public void doActionOnWorkflowExecutionStep(Long workflowExecutionStepId, TaskActionType stepAction) {
        WorkflowExecutionStep workflowExecutionStep = workflowExecutionStepRepository.findOne(workflowExecutionStepId);
        TaskStatus status = TaskStatus.fromInt(workflowExecutionStep.getStatus());
        if (stepAction != null && status != null) {
            if (status.getPossibleActionsEnumOption().contains(stepAction)) {
                // not supported action
            }

            if (stepAction.isCheckPermission()) {
                checkUserhasActionPrivilege(workflowExecutionStep, stepAction);
            }
            if (TaskActionType.CRITERIACHECK.equals(stepAction)) {
                runCriteriaCheckAndPopulate(workflowExecutionStep);
            }
            if (stepAction.getToStatus() != null) {
                TaskStatus newStatus = getNextEquivalentStatus(workflowExecutionStep.getWorkflowStepId(), stepAction.getToStatus());
                // StepStatus newStatus = stepAction.getToStatus();
                if (status.equals(newStatus)) {
                    // do-nothing
                    return;
                }
                workflowExecutionStep.setStatus(newStatus.getValue());
                // update assigned-to if current user has next action
                updateAssignedTo(workflowExecutionStep, newStatus);

                workflowExecutionStepRepository.save(workflowExecutionStep);
                notifyWorkflow(workflowExecutionStep.getWorkflowExecutionId(), workflowExecutionStepId, status, newStatus);
            }
        }
        // do Action Log
    }*/

    /*private void updateAssignedTo(final WorkflowExecutionStep workflowExecutionStep, final TaskStatus newStatus) {
        TaskActionType nextPossibleAction = newStatus.getNextPositiveAction();
        if (nextPossibleAction != null) {
            workflowExecutionStep.setCurrentAction(nextPossibleAction.getValue());
        }
        if (nextPossibleAction != null && canUserDothisAction(workflowExecutionStep, nextPossibleAction)) {
            workflowExecutionStep.setAssignedTo(context.authenticatedUser().getId());
        } else {
            workflowExecutionStep.setAssignedTo(null);
        }
    }*/

    

    /*private void checkUserhasActionPrivilege(WorkflowExecutionStep workflowExecutionStep, TaskActionType stepAction) {
        if (workflowExecutionStep.getActionGroupId() == null) { return; }
        WorkflowStepAction workflowStepAction = workflowStepActionRepository.findOneByActionGroupIdAndAction(
                workflowExecutionStep.getActionGroupId(), stepAction.getValue());
        if (workflowStepAction != null) {
            if (canUserDothisAction(workflowStepAction)) { return; }
            throw new WorkflowStepNoActionPermissionException(stepAction);
        }
    }*/

    /*private boolean canUserDothisAction(WorkflowExecutionStep workflowExecutionStep, TaskActionType stepAction) {
        if (workflowExecutionStep.getActionGroupId() == null) { return true; }
        WorkflowStepAction workflowStepAction = workflowStepActionRepository.findOneByActionGroupIdAndAction(
                workflowExecutionStep.getActionGroupId(), stepAction.getValue());
        return canUserDothisAction(workflowStepAction);
    }*/

    /*private boolean canUserDothisAction(WorkflowStepAction workflowStepAction) {
        if (workflowStepAction == null) { return true; }
        final TaskActionType stepAction = TaskActionType.fromInt(workflowStepAction.getAction());
        if (!stepAction.isCheckPermission()) { return true; }

        List<WorkflowStepActionRole> actionRoles = workflowStepActionRoleRepository.findByWorkflowStepActionId(workflowStepAction.getId());
        List<Long> roles = new ArrayList<>();

        if (actionRoles == null || actionRoles.isEmpty()) { return true; }
        for (WorkflowStepActionRole actionRole : actionRoles) {
            roles.add(actionRole.getRoleId());
        }
        Collection<RoleData> roleDatas = roleReadPlatformService.retrieveAppUserRoles(context.authenticatedUser().getId());
        for (RoleData roleData : roleDatas) {
            if (roles.contains(roleData.getId())) { return true; }
        }
        return false;
    }*/

    @SuppressWarnings("unused")
    /*private void notifyWorkflow(Long workflowExecutionId, Long workflowExecutionStepId, TaskStatus status, TaskStatus newStatus) {

        if (TaskStatus.COMPLETED.equals(newStatus) || TaskStatus.SKIPPED.equals(newStatus)) {

            List<Long> nextExecutionStepIds = getNextExecutionStepsByOrder(workflowExecutionId, workflowExecutionStepId);
            if (nextExecutionStepIds != null && !nextExecutionStepIds.isEmpty()) {
                for (Long nextExecutionStepId : nextExecutionStepIds) {
                    WorkflowExecutionStep nextExecutionStep = workflowExecutionStepRepository.findOne(nextExecutionStepId);
                    if (TaskStatus.INACTIVE.getValue().equals(nextExecutionStep.getStatus())) {
                        nextExecutionStep.setStatus(TaskStatus.INITIATED.getValue());
                        updateAssignedTo(nextExecutionStep, TaskStatus.INITIATED);
                        workflowExecutionStepRepository.save(nextExecutionStep);
                    }
                }
            }
            // set workflow status as all step done

        } else if (TaskStatus.CANCELLED.equals(newStatus)) {
            // do some logging or transition steps
        }
    }*/

    /*private List<Long> getNextExecutionStepsByOrder(Long workflowExecutionId, Long workflowExecutionStepId) {
        WorkflowExecutionStep workflowExecutionStep = workflowExecutionStepRepository.findOne(workflowExecutionStepId);
        return taskReadService.getExecutionStepsByOrder(workflowExecutionId, workflowExecutionStep.getStepOrder() + 1);
    }*/

    @Override
    public void addNoteToWorkflowExecution(Long workflowExecutionId) {

    }

    @Override
    public void addNoteToWorkflowExecutionStep(Long workflowExecutionStepId) {

    }

    /*@Override
    public List<EnumOptionData> getClickableActionsForUser(Long workflowExecutionStepId, Long userId) {
        return getPossibleActions(workflowExecutionStepId, userId, true);
    }*/

    /*@Override
    public Long getWorkflowExecution(TaskEntityType entityType, Long entityId) {
        final WorkflowExecution workflowExecution = this.workflowExecutionRepository.findByEntityTypeAndEntityId(entityType.getValue(),
                entityId);
        if (workflowExecution != null) { return workflowExecution.getId(); }
        return null;
    }*/

    /*private List<EnumOptionData> getPossibleActions(Long workflowExecutionStepId, Long userId, boolean onlyClickable) {
        WorkflowExecutionStep workflowExecutionStep = workflowExecutionStepRepository.findOne(workflowExecutionStepId);
        TaskStatus status = TaskStatus.fromInt(workflowExecutionStep.getStatus());
        List<EnumOptionData> actionEnums = new ArrayList<>();
        // skip over statuses if no actions been configured
        // workflow_Step_action.
        // Example if Approval is not assigned to a step. After Review it will
        // move the step from Underreview to Completed
        // instead of Under Approval
        // StepStatus nextEquivalentStatus =
        // getNextEquivalentStatus(workflowExecutionStep.getWorkflowStepId(),
        // status);
        if (workflowExecutionStep.getCriteriaId() != null && TaskStatus.UNDERREVIEW.equals(status)) {
            TaskActionType stepAction = TaskActionType.fromInt(workflowExecutionStep.getCriteriaAction());
            if (stepAction != null) {
                actionEnums.add(stepAction.getEnumOptionData());
                return actionEnums;
            }
        }
        List<TaskActionType> actions = status.getPossibleActionEnums();
        for (TaskActionType action : actions) {
            WorkflowStepAction workflowStepAction = workflowStepActionRepository.findOneByActionGroupIdAndAction(
                    workflowExecutionStep.getActionGroupId(), action.getValue());

            if (workflowStepAction == null && action.isCheckPermission()) {
                continue;
            }

            if (onlyClickable) {
                if (action.isClickable() && canUserDothisAction(workflowStepAction)) {
                    actionEnums.add(action.getEnumOptionData());
                }
            } else {
                if (canUserDothisAction(workflowStepAction)) {
                    actionEnums.add(action.getEnumOptionData());
                }
            }

        }
        return actionEnums;
    }*/

   /* private TaskStatus getNextEquivalentStatus(Long workflowStepId, TaskStatus status) {
        while (status.getNextPositiveAction() != null) {
            TaskActionType nextAction = status.getNextPositiveAction();
            WorkflowStepAction workflowStepAction = workflowStepActionRepository.findOneByActionGroupIdAndAction(workflowStepId,
                    nextAction.getValue());
            if (workflowStepAction == null) {
                status = nextAction.getToStatus();
            } else {
                return status;
            }
        }
        return status;
    }*/

    @Override
    public TaskData getTaskExecutionData(Long workflowExecutionId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getTaskExecutionData(TaskEntityType loanApplication, Long entityId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doActionOnWorkflowExecutionStep(Long workflowExecutionStepId, TaskActionType stepAction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<EnumOptionData> getClickableActionsForUser(Long workflowExecutionStepId, Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getWorkflowExecution(TaskEntityType entityType, Long entityId) {
        // TODO Auto-generated method stub
        return null;
    }
}
