package com.finflux.task.service.impl;

import java.util.*;

import com.finflux.task.api.TaskApiConstants;
import com.google.gson.JsonElement;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.RoleReadPlatformService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.data.EligibilityStatus;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.lib.FieldUndefinedException;
import com.finflux.ruleengine.lib.InvalidExpressionException;
import com.finflux.ruleengine.lib.data.ExpressionNode;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import com.finflux.ruleengine.lib.service.impl.MyExpressionExecutor;
import com.finflux.task.data.*;
import com.finflux.task.domain.*;
import com.finflux.task.exception.TaskActionPermissionException;
import com.finflux.task.service.TaskExecutionService;
import com.finflux.task.service.TaskPlatformReadService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final static Logger logger = LoggerFactory.getLogger(TaskExecutionServiceImpl.class);
    private final TaskPlatformReadService taskReadService;
    private final TaskRepositoryWrapper taskRepository;
    private final RoleReadPlatformService roleReadPlatformService;
    private final RuleExecutionService ruleExecutionService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final ExpressionExecutor expressionExecutor;
    private final TaskActionRepository taskActionRepository;
    private final TaskActionRoleRepository actionRoleRepository;
    private final TaskActionLogRepository actionLogRepository;
    private final TaskNoteRepository noteRepository;
    private final FromJsonHelper fromJsonHelper;
    private final PlatformSecurityContext context;
    private final Gson gson = new Gson();
    private Map<DataLayerKey, Long> dataLayerKeyLongMap;

    @Autowired
    public TaskExecutionServiceImpl(final TaskPlatformReadService taskReadService,
            final TaskRepositoryWrapper taskRepository, final RoleReadPlatformService roleReadPlatformService,
            final RuleExecutionService ruleExecutionService, final DataLayerReadPlatformService dataLayerReadPlatformService,
            final MyExpressionExecutor expressionExecutor, final TaskActionRepository taskActionRepository,
            final TaskActionRoleRepository actionRoleRepository, final TaskActionLogRepository actionLogRepository,
            final TaskNoteRepository noteRepository,final FromJsonHelper fromJsonHelper,
            final PlatformSecurityContext context) {
        this.taskReadService = taskReadService;
        this.taskRepository = taskRepository;
        this.roleReadPlatformService = roleReadPlatformService;
        this.ruleExecutionService = ruleExecutionService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.expressionExecutor = expressionExecutor;
        this.taskActionRepository = taskActionRepository;
        this.actionRoleRepository = actionRoleRepository;
        this.actionLogRepository = actionLogRepository;
        this.noteRepository = noteRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.context = context;
    }
    /*
     * @Override public TaskExecutionData getWorkflowExecutionData(final Long
     * workflowExecutionId) { return
     * this.taskReadService.getTaskData(workflowExecutionId); }
     */

    @Override
    @Transactional
    public CommandProcessingResult doActionOnTask(AppUser performingUser, Long taskId, TaskActionType actionType) {
        return doActionOnTask(performingUser, taskId, actionType, null);

    }

    private CommandProcessingResult doActionOnTask(AppUser performingUser, Long taskId, TaskActionType actionType,
            AppUser assignedTo) {
        Task task = taskRepository.findOneWithNotFoundDetection(taskId);
        TaskStatusType status = TaskStatusType.fromInt(task.getStatus());
        if (actionType != null && status != null) {
            if (status.getPossibleActionsEnumOption().contains(actionType)) {
                // not supported action
            }
            if (actionType.isCheckPermission()) {
                checkUserhasActionPrivilege(performingUser, task, actionType);
            }
            if (actionType.getToStatus() != null) {
                if(status.isCompleted()) {
                    if (!TaskActionType.STARTOVER.equals(actionType)) {
                        return new CommandProcessingResultBuilder().withEntityId(task.getId()).build();
                    }
                }
                TaskActionLog actionLog = TaskActionLog.create(task, actionType.getValue(), performingUser);
                if (assignedTo == null) {
                    assignedTo = performingUser;
                }
                if (TaskActionType.CRITERIACHECK.equals(actionType)) {
                    runCriteriaCheckAndPopulate(task);
                    task.setStatus(TaskActionType.CRITERIACHECK.getToStatus().getValue());
                    updateAssignedTo(assignedTo, task, TaskActionType.fromInt(task.getCurrentAction()));
                }else{
                    TaskStatusType newStatus = getNextEquivalentStatus(task, actionType.getToStatus());

                    // StepStatus newStatus = stepAction.getToStatus();
                    if (status.equals(newStatus)) {
                        // do-nothing
                        return new CommandProcessingResultBuilder().withEntityId(task.getId()).build();
                    }
                    task.setStatus(newStatus.getValue());
                    updateActionAndAssignedTo(assignedTo, task, newStatus);
                }
                // update assigned-to if current user has next action
                task = taskRepository.save(task);
                actionLogRepository.save(actionLog);
                if (task.getParent() != null) {
                    notifyParentTask(performingUser, task.getParent(), task, status, TaskStatusType.fromInt(task.getStatus()));
                }
            }
            if (task.getCompleteOnAction() != null && task.getCompleteOnAction().equals(actionType.getValue())) {
                doActionOnTask(performingUser, taskId, TaskActionType.ACTIVITYCOMPLETE);
            }
        }
        return new CommandProcessingResultBuilder().withEntityId(task.getId()).build();
        // do Action Log
    }
    private CommandProcessingResult taskDataAsCommandProcessingResult(Long taskId) {
        TaskExecutionData taskExecutionData = getTaskData(taskId);
        Map<String,Object> changes  = new HashMap<>();
        changes.put("taskData",taskExecutionData);
        return CommandProcessingResult.withChanges(taskId,changes);
    }

    @Override
    public void updateActionAndAssignedTo(AppUser appUser, final Task task, final TaskStatusType newStatus) {
        TaskActionType nextPossibleActionType = newStatus.getNextPositiveAction();
        if (nextPossibleActionType != null) {
            task.setCurrentAction(nextPossibleActionType.getValue());
            if (nextPossibleActionType != null && canUserDothisAction(appUser,
                    task, nextPossibleActionType)) {
                task.setAssignedTo(appUser);
            } else {
                task.setAssignedTo(null);
            }
        } else {
            task.setCurrentAction(null);
            task.setAssignedTo(null);
        }
    }

    private void updateAssignedTo(final AppUser appUser, final Task task, final TaskActionType nextPossibleActionType) {
        if (nextPossibleActionType != null) {
            task.setCurrentAction(nextPossibleActionType.getValue());
            if (nextPossibleActionType != null && canUserDothisAction(appUser, task, nextPossibleActionType)) {
                task.setAssignedTo(appUser);
            } else {
                task.setAssignedTo(null);
            }
        }
    }

    private void checkUserhasActionPrivilege(AppUser appUser, Task task, TaskActionType actionType) {
        if (task.getActionGroupId() == null) { return; }
        TaskAction workflowStepAction = taskActionRepository
                .findOneByActionGroupIdAndAction(task.getActionGroupId(), actionType.getValue());
        if (workflowStepAction != null) {
            if (canUserDothisAction(appUser, workflowStepAction)) { return; }
            throw new TaskActionPermissionException(actionType);
        }
    }

    @Override
    public boolean canUserDothisAction(AppUser appUser, Task task, TaskActionType taskActionType) {
        if (task.getActionGroupId() == null) { return true; }
        TaskAction workflowStepAction = taskActionRepository.findOneByActionGroupIdAndAction(task.getActionGroupId(),
                taskActionType.getValue());
        return canUserDothisAction(appUser, workflowStepAction);
    }

    private boolean canUserDothisAction(AppUser appUser, TaskAction taskAction) {
        if (taskAction == null) { return true; }
        final TaskActionType taskActionType = TaskActionType.fromInt(taskAction.getAction());
        if (!taskActionType.isCheckPermission()) { return true; }

        List<TaskActionRole> actionRoles = actionRoleRepository.findByTaskActionId(taskAction.getId());
        List<Long> roles = new ArrayList<>();

        if (actionRoles == null || actionRoles.isEmpty()) { return true; }
        for (TaskActionRole actionRole : actionRoles) {
            roles.add(actionRole.getRoleId());
        }
        Collection<RoleData> roleDatas = roleReadPlatformService.retrieveAppUserRoles(appUser.getId());
        for (RoleData roleData : roleDatas) {
            if (roles.contains(roleData.getId())) { return true; }
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void notifyParentTask(AppUser appUser, Task parentTask, Task task, TaskStatusType status, TaskStatusType newStatus) {

        if (TaskType.WORKFLOW.equals(TaskType.fromInt(parentTask.getTaskType()))) {
            if (TaskStatusType.COMPLETED.equals(newStatus) || TaskStatusType.SKIPPED.equals(newStatus)) {
                Task currentTask = task;
                Boolean foundNextInactiveTask = false;
                while (!foundNextInactiveTask) {
                    List<Long> nextExecutionTaskIds = getNextExecutionTasks(parentTask, currentTask);
                    if (nextExecutionTaskIds != null && !nextExecutionTaskIds.isEmpty()) {
                        Long nextExecutionTaskId = nextExecutionTaskIds.get(0);
                        Task nextExecutionTask = taskRepository.findOneWithNotFoundDetection(nextExecutionTaskId);
                        if (TaskStatusType.INACTIVE.getValue().equals(nextExecutionTask.getStatus())) {
                            nextExecutionTask.setStatus(TaskStatusType.INITIATED.getValue());
                            updateActionAndAssignedTo(appUser,nextExecutionTask, TaskStatusType.INITIATED);
                            nextExecutionTask.setCreatedDate(new DateTime());
                            taskRepository.save(nextExecutionTask);
                            foundNextInactiveTask = true;
                        }
                        currentTask = nextExecutionTask;
                    } else {
                        break;
                    }
                }
                if (!foundNextInactiveTask) {
                    // set workflow status as all step done
                    parentTask.setStatus(TaskStatusType.COMPLETED.getValue());
                    taskRepository.save(parentTask);
                }


            } else if (TaskStatusType.CANCELLED.equals(newStatus)) {
                parentTask.setStatus(TaskStatusType.CANCELLED.getValue());
                taskRepository.save(parentTask);
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

    private List<TaskActionData> getPossibleActionsOnTask(AppUser appUser, Long taskId, boolean onlyClickable) {
        Task task = taskRepository.findOneWithNotFoundDetection(taskId);
        TaskStatusType status = TaskStatusType.fromInt(task.getStatus());
        List<TaskActionData> actionDatas = new ArrayList<>();
        List<TaskActionType> actionEnums = new ArrayList<>();
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
                actionEnums.add(stepAction);
                if(TaskActionType.REVIEW.equals(stepAction)){
                    //auto approval/rejection
                    actionEnums.add(TaskActionType.REJECT);
                }
            }
        }else{
            List<TaskActionType> actions = status.getPossibleActionEnums();
            for (TaskActionType action : actions) {

                if (onlyClickable) {
                    if (action.isClickable()) {
                        actionEnums.add(action);
                    }
                } else {
                    actionEnums.add(action);
                }

            }
        }
        for(TaskActionType action: actionEnums){
            TaskAction taskAction= null;
            if (task.getActionGroupId() != null) {
                taskAction = taskActionRepository.findOneByActionGroupIdAndAction(task.getActionGroupId(), action.getValue());
            }

            if (taskAction == null && !action.isEnableByDefault()) {
                continue;
            }
            if (canUserDothisAction(appUser,taskAction)) {
                actionDatas.add(new TaskActionData(action,true,null));
            }else{
                actionDatas.add(new TaskActionData(action,false,null));
            }
        }
        return actionDatas;
    }

    private TaskStatusType getNextEquivalentStatus(Task task, TaskStatusType status) {
        while (status.getNextPositiveAction() != null && !TaskStatusType.INITIATED.equals(status)) {
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
        TaskExecutionData taskExecutionData =  taskReadService.getTaskDetails(taskId);
        if(taskExecutionData.getActionGroupId()!=null && taskExecutionData.getCurrentAction()!=null){
            taskExecutionData.setAssignedRoles(taskReadService.getRolesForAnAction(taskExecutionData.getActionGroupId(),
                    taskExecutionData.getCurrentAction().getId()));
        }
        return taskExecutionData;

    }

    @Override
    public TaskExecutionData getTaskIdByEntity(TaskEntityType taskEntityType, Long entityId) {
        // TODO Auto-generated method stub
        return taskReadService.getTaskDetailsByEntityTypeAndEntityId(taskEntityType, entityId);
    }

    @Override
    public List<TaskActionData> getClickableActionsOnTask(AppUser appUser, Long taskId) {
        return getPossibleActionsOnTask(appUser, taskId, true);
    }

    @Override
    public List<TaskExecutionData> getChildrenOfTask(Long taskId) {
        return taskReadService.getTaskChildren(taskId);
    }

    @Override
    public List<TaskNoteData> getTaskNotes(Long taskId) {
        return taskReadService.getTaskNotes(taskId);
    }

    @Override
    public CommandProcessingResult addNoteToTask(AppUser appUser, Long taskId, String noteFormStr) {
        Task task = taskRepository.findOneWithNotFoundDetection(taskId);
        TaskNoteForm noteForm = fromJsonHelper.fromJson(noteFormStr, TaskNoteForm.class);
        TaskNote taskNote = TaskNote.create(task,noteForm.getNote());
        taskNote.setCreatedBy(appUser);
        taskNote = noteRepository.save(taskNote);
        return new CommandProcessingResultBuilder().withEntityId(taskId).withResourceIdAsString(""+taskNote.getId()).build();
    }

    @Override
    public List<TaskActionLogData> getActionLogs(Long taskId) {
        return taskReadService.getActionLogs(taskId);
    }

    @Override
    public CommandProcessingResult addNoteToTask(Long taskId, String json) {
        return addNoteToTask(context.authenticatedUser(),taskId,json);
    }

    @Override
    public CommandProcessingResult doActionOnTask(Long taskId, TaskActionType activitycomplete) {
        return doActionOnTask(context.authenticatedUser(),taskId,activitycomplete);
    }

    @Override
    public CommandProcessingResult doStartOver(Long taskId, JsonCommand command) {
        Long startOverTaskId = command.longValueOfParameterNamed(TaskApiConstants.STARTOVER_TASK_ID_PARAM_NAME);
        Task currentTask = taskRepository.findOneWithNotFoundDetection(taskId);
        if (startOverTaskId != null && startOverTaskId > 0) {
            Task startOverTask = taskRepository.findOneWithNotFoundDetection(startOverTaskId);
            if (currentTask.getParent() != null
                    && currentTask.getParent().getId().equals(startOverTask.getParent().getId())) {
                if (currentTask.getTaskOrder() >= startOverTask.getTaskOrder()) {
                    List<TaskActionLog> actionLogs = actionLogRepository.findByTaskIdAndActionOrderByIdDesc(
                            startOverTaskId, TaskActionType.ACTIVITYCOMPLETE.getValue());
                    if (actionLogs != null && !actionLogs.isEmpty()) {
                        doActionOnTask(context.authenticatedUser(), startOverTaskId, TaskActionType.STARTOVER,
                                actionLogs.get(0).getActionBy());
                        doActionOnTask(context.authenticatedUser(), taskId, TaskActionType.DISABLE);
                    }
                }
            }
        }
        return new CommandProcessingResultBuilder().withEntityId(taskId).build();
    }

    @SuppressWarnings({})
    private void runCriteriaCheckAndPopulate(final Task task) {
        /**
         * We will reuse this code in future
         */
        Map<String, Object> dataLayerKeyLongMap = new HashMap<>();
        Map<String,String> configValueMap = null;

        if(task.getConfigValues()!=null){
            configValueMap = new Gson().fromJson(task.getConfigValues(),
                    new TypeToken<HashMap<String, String>>() {}.getType());
        }

        if(configValueMap!=null){
            for(String key:configValueMap.keySet()){
            	if(configValueMap.get(key)!=null){
            		dataLayerKeyLongMap.put(key,configValueMap.get(key));
            	}
            }
        }

        TaskDataLayer dataLayer = new TaskDataLayer(dataLayerReadPlatformService);
        dataLayer.build(dataLayerKeyLongMap);

        RuleResult ruleResult = ruleExecutionService.executeARule(task.getCriteria().getId(), dataLayer);
        EligibilityResult eligibilityResult = new EligibilityResult();
        eligibilityResult.setStatus(EligibilityStatus.TO_BE_REVIEWED);
        eligibilityResult.setCriteriaOutput(ruleResult);
        ExpressionNode approvalLogic = gson.fromJson(task.getApprovalLogic(),
                new TypeToken<ExpressionNode>() {}.getType());
        ExpressionNode rejectionLogic = gson.fromJson(task.getRejectionLogic(),
                new TypeToken<ExpressionNode>() {}.getType());
        if (ruleResult != null && ruleResult.getOutput().getValue() != null) {
            Map<String, Object> map = new HashMap();
            map.put("criteria", ruleResult.getOutput().getValue());
            boolean rejectionResult = false;
            boolean approvalResult = false;
            try {
                rejectionResult = expressionExecutor.executeExpression(rejectionLogic, map);
            } catch (FieldUndefinedException e) {
                logger.warn("Field is undefined in Rejection Logic for task:"+task.getId(),e);
            } catch (InvalidExpressionException e) {
                logger.warn("Rejection Logic Expression is invalid for task:"+task.getId(),e);
            }
            if (rejectionResult) {
                eligibilityResult.setStatus(EligibilityStatus.REJECTED);
            }else {
                try {
                    approvalResult = expressionExecutor.executeExpression(approvalLogic, map);
                } catch (FieldUndefinedException e) {
                    logger.warn("Field is undefined in Approval Logic for task:"+task.getId(),e);
                } catch (InvalidExpressionException e) {
                    logger.warn("Approval Logic Expression is invalid for task:"+task.getId(),e);
                }
            }
            if (approvalResult) {
                eligibilityResult.setStatus(EligibilityStatus.APPROVED);
            }
        }
        TaskActionType nextEligibleAction = TaskActionType.REVIEW;
        if (EligibilityStatus.APPROVED.equals(eligibilityResult.getStatus())) {
            nextEligibleAction = TaskActionType.APPROVE;
        } else if (EligibilityStatus.REJECTED.equals(eligibilityResult.getStatus())) {
            nextEligibleAction = TaskActionType.REJECT;
        }
        task.setCriteriaAction(nextEligibleAction.getValue());
        task.setCurrentAction(nextEligibleAction.getValue());
        task.setCriteriaResult(gson.toJson(eligibilityResult));
    }
}
