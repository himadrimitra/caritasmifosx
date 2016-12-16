package com.finflux.task.service.impl;

import java.util.*;

import com.finflux.task.data.*;
import com.finflux.task.domain.*;
import com.finflux.task.exception.TaskActionPermissionException;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.data.RoleData;
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
import com.finflux.task.service.TaskPlatformReadService;
import com.finflux.task.domain.Task;
import com.finflux.task.domain.TaskActionLog;
import com.finflux.task.domain.TaskActionLogRepository;
import com.finflux.task.domain.TaskRepositoryWrapper;
import com.finflux.task.service.TaskExecutionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final TaskPlatformReadService taskReadService;
    private final TaskConfigRepositoryWrapper taskConfigRepository;
    private final TaskRepositoryWrapper taskRepository;
    private final RoleReadPlatformService roleReadPlatformService;
    private final PlatformSecurityContext context;
    private final RuleExecutionService ruleExecutionService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final RuleCacheService ruleCacheService;
    private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
    private final ExpressionExecutor expressionExecutor;
    private final TaskActionRepository taskActionRepository;
    private final TaskActionRoleRepository actionRoleRepository;
    private final TaskActionLogRepository actionLogRepository;

    @Autowired
    public TaskExecutionServiceImpl(final TaskPlatformReadService taskReadService, final TaskConfigRepositoryWrapper taskConfigRepository,
            final TaskRepositoryWrapper taskRepository, final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository,
            final RoleReadPlatformService roleReadPlatformService, final PlatformSecurityContext context,
            final RuleExecutionService ruleExecutionService, final DataLayerReadPlatformService dataLayerReadPlatformService,
            final RuleCacheService ruleCacheService,
            final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
            final MyExpressionExecutor expressionExecutor, final TaskActionRepository taskActionRepository,
            final TaskActionRoleRepository actionRoleRepository, final TaskActionLogRepository actionLogRepository) {
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
        this.taskActionRepository = taskActionRepository;
        this.actionRoleRepository = actionRoleRepository;
        this.actionLogRepository = actionLogRepository;
    }
    /*
     * @Override public TaskExecutionData getWorkflowExecutionData(final Long
     * workflowExecutionId) { return
     * this.taskReadService.getTaskData(workflowExecutionId); }
     */

    @Override
    @Transactional
    public void doActionOnTask(Long taskId, TaskActionType actionType) {
        Task task = taskRepository.findOneWithNotFoundDetection(taskId);
        TaskStatusType status = TaskStatusType.fromInt(task.getStatus());
        if (actionType != null && status != null) {
            if (status.getPossibleActionsEnumOption().contains(actionType)) {
                // not supported action
            }

            if (actionType.isCheckPermission()) {
                checkUserhasActionPrivilege(task, actionType);
            }
            if (TaskActionType.CRITERIACHECK.equals(actionType)) {
                runCriteriaCheckAndPopulate(task);
            }
            if (actionType.getToStatus() != null) {
                TaskActionLog actionLog = TaskActionLog.create(task, actionType.getValue(), context.authenticatedUser());
                TaskStatusType newStatus = getNextEquivalentStatus(task, actionType.getToStatus());
                // StepStatus newStatus = stepAction.getToStatus();
                if (status.equals(newStatus)) {
                    // do-nothing
                    return;
                }
                task.setStatus(newStatus.getValue());
                // update assigned-to if current user has next action
                updateActionAndAssignedTo(task, newStatus);

                task = taskRepository.save(task);
                actionLogRepository.save(actionLog);
                if (task.getParent() != null) {
                    notifyParentTask(task.getParent(), task, status, newStatus);
                }

            }
        }
        // do Action Log
    }

    @Override
    public void updateActionAndAssignedTo(final Task task, final TaskStatusType newStatus) {
        TaskActionType nextPossibleActionType = newStatus.getNextPositiveAction();
        if (nextPossibleActionType != null) {
            task.setCurrentAction(nextPossibleActionType.getValue());
            if (nextPossibleActionType != null && canUserDothisAction(task, nextPossibleActionType)) {
                task.setAssignedTo(context.authenticatedUser());
            } else {
                task.setAssignedTo(null);
            }
        } else {
            task.setCurrentAction(null);
        }
    }

    private void checkUserhasActionPrivilege(Task task, TaskActionType actionType) {
        if (task.getActionGroupId() == null) { return; }
        TaskAction workflowStepAction = taskActionRepository
                .findOneByActionGroupIdAndAction(task.getActionGroupId(), actionType.getValue());
        if (workflowStepAction != null) {
            if (canUserDothisAction(workflowStepAction)) { return; }
            throw new TaskActionPermissionException(actionType);
        }
    }

    private boolean canUserDothisAction(Task task, TaskActionType taskActionType) {
        if (task.getActionGroupId() == null) { return true; }
        TaskAction workflowStepAction = taskActionRepository.findOneByActionGroupIdAndAction(task.getActionGroupId(),
                taskActionType.getValue());
        return canUserDothisAction(workflowStepAction);
    }

    private boolean canUserDothisAction(TaskAction taskAction) {
        if (taskAction == null) { return true; }
        final TaskActionType taskActionType = TaskActionType.fromInt(taskAction.getAction());
        if (!taskActionType.isCheckPermission()) { return true; }

        List<TaskActionRole> actionRoles = actionRoleRepository.findByTaskActionId(taskAction.getId());
        List<Long> roles = new ArrayList<>();

        if (actionRoles == null || actionRoles.isEmpty()) { return true; }
        for (TaskActionRole actionRole : actionRoles) {
            roles.add(actionRole.getRoleId());
        }
        Collection<RoleData> roleDatas = roleReadPlatformService.retrieveAppUserRoles(context.authenticatedUser().getId());
        for (RoleData roleData : roleDatas) {
            if (roles.contains(roleData.getId())) { return true; }
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void notifyParentTask(Task parentTask, Task task, TaskStatusType status, TaskStatusType newStatus) {

        if (TaskType.WORKFLOW.equals(TaskType.fromInt(parentTask.getTaskType()))) {
            if (TaskStatusType.COMPLETED.equals(newStatus) || TaskStatusType.SKIPPED.equals(newStatus)) {
                List<Long> nextExecutionTaskIds = getNextExecutionTasks(parentTask, task);
                if (nextExecutionTaskIds != null && !nextExecutionTaskIds.isEmpty()) {
                    for (Long nextExecutionTaskId : nextExecutionTaskIds) {
                        Task nextExecutionTask = taskRepository.findOneWithNotFoundDetection(nextExecutionTaskId);
                        if (TaskStatusType.INACTIVE.getValue().equals(nextExecutionTask.getStatus())) {
                            nextExecutionTask.setStatus(TaskStatusType.INITIATED.getValue());
                            updateActionAndAssignedTo(nextExecutionTask, TaskStatusType.INITIATED);
                            taskRepository.save(nextExecutionTask);
                        }
                    }
                }
                // set workflow status as all step done

            } else if (TaskStatusType.CANCELLED.equals(newStatus)) {
                // do some logging or transition steps
            }
        }

    }

    private List<Long> getNextExecutionTasks(Task parentTask, Task task) {
        return taskReadService.getChildTasksByOrder(parentTask.getId(), task.getTaskOrder() + 1);
    }

    /*
     * @Override public Long getWorkflowExecution(TaskEntityType entityType,
     * Long entityId) { final WorkflowExecution workflowExecution =
     * this.workflowExecutionRepository
     * .findByEntityTypeAndEntityId(entityType.getValue(), entityId); if
     * (workflowExecution != null) { return workflowExecution.getId(); } return
     * null; }
     */

    private List<EnumOptionData> getPossibleActionsOnTask(Long taskId, boolean onlyClickable) {
        Task task = taskRepository.findOneWithNotFoundDetection(taskId);
        TaskStatusType status = TaskStatusType.fromInt(task.getStatus());
        List<EnumOptionData> actionEnums = new ArrayList<>();
        // skip over statuses if no actions been configured
        // workflow_Step_action.
        // Example if Approval is not assigned to a step. After Review it will
        // move the step from Underreview to Completed
        // instead of Under Approval
        // StepStatus nextEquivalentStatus =
        // getNextEquivalentStatus(workflowExecutionStep.getWorkflowStepId(),
        // status);
        if (task.getCriteria() != null && TaskStatusType.UNDERREVIEW.equals(status)) {
            TaskActionType stepAction = TaskActionType.fromInt(task.getCriteriaAction());
            if (stepAction != null) {
                actionEnums.add(stepAction.getEnumOptionData());
                return actionEnums;
            }
        }
        List<TaskActionType> actions = status.getPossibleActionEnums();
        for (TaskActionType action : actions) {
            TaskAction taskAction= null;

            if (task.getActionGroupId() != null) {
                taskAction = taskActionRepository.findOneByActionGroupIdAndAction(task.getActionGroupId(), action.getValue());
            }

            if (taskAction == null && !action.isEnableByDefault()) {
                continue;
            }

            if (onlyClickable) {
                if (action.isClickable() && canUserDothisAction(taskAction)) {
                    actionEnums.add(action.getEnumOptionData());
                }
            } else {
                if (canUserDothisAction(taskAction)) {
                    actionEnums.add(action.getEnumOptionData());
                }
            }

        }
        return actionEnums;
    }

    private TaskStatusType getNextEquivalentStatus(Task task, TaskStatusType status) {
        while (status.getNextPositiveAction() != null) {
            TaskActionType nextAction = status.getNextPositiveAction();
            TaskAction taskAction = null;
            if (task.getActionGroupId() != null) {
                taskAction = taskActionRepository.findOneByActionGroupIdAndAction(task.getActionGroupId(), nextAction.getValue());
            }
            if (taskAction == null) {
                status = nextAction.getToStatus();
            } else {
                return status;
            }
        }
        return status;
    }

    @Override
    public TaskExecutionData getTaskData(Long taskId) {
        return taskReadService.getTaskDetails(taskId);
    }

    @Override
    public TaskExecutionData getTaskIdByEntity(TaskEntityType taskEntityType, Long entityId) {
        // TODO Auto-generated method stub
        return taskReadService.getTaskDetailsByEntityTypeAndEntityId(taskEntityType, entityId);
    }

    @Override
    public List<EnumOptionData> getClickableActionsOnTask(Long taskId) {
        return getPossibleActionsOnTask(taskId, true);
    }

    @Override
    public List<TaskExecutionData> getChildrenOfTask(Long taskId) {
        return taskReadService.getTaskChildren(taskId);
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
