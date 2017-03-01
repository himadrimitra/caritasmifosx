package com.finflux.task.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.commands.domain.CommandSource;
import org.apache.fineract.commands.domain.CommandSourceRepository;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.task.data.TaskActionType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.TaskPriority;
import com.finflux.task.data.TaskStatusType;
import com.finflux.task.data.TaskType;
import com.finflux.task.domain.Task;
import com.finflux.task.domain.TaskActivity;
import com.finflux.task.domain.TaskConfig;
import com.finflux.task.domain.TaskConfigRepositoryWrapper;
import com.finflux.task.domain.TaskRepositoryWrapper;
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
    private final CommandSourceRepository commandSourceRepository;
    private final FromJsonHelper fromJsonHelper;
    private final PlatformSecurityContext context;


    @Autowired
    public TaskPlatformWriteServiceImpl(final TaskPlatformReadService taskPlatformReadService,
            final TaskConfigRepositoryWrapper taskConfigRepository, final TaskRepositoryWrapper taskRepository,
            final TaskExecutionService taskExecutionService, final CommandSourceRepository commandSourceRepository,
            final FromJsonHelper fromJsonHelper,final PlatformSecurityContext context) {
        this.taskPlatformReadService = taskPlatformReadService;
        this.taskConfigRepository = taskConfigRepository;
        this.taskRepository = taskRepository;
        this.taskExecutionService = taskExecutionService;
        this.commandSourceRepository = commandSourceRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.context = context;
    }

    @SuppressWarnings("unused")
    @Transactional
    @Override
    public Long createTaskFromConfig(final Long taskConfigId, final TaskEntityType entityType, final Long entityId, final Client client,AppUser assignedTo,final Date dueDate,
            final Office office, final Map<TaskConfigKey, String> configValues, String description) {

        if(assignedTo==null)
        {
            assignedTo=this.context.authenticatedUser();
        }
        Map<String, String> customConfigMap = new HashMap<>();
        if(configValues!=null)
        {
            for (Map.Entry<TaskConfigKey, String> config : configValues.entrySet()) 
            {
            customConfigMap.put(config.getKey().getValue(), config.getValue());
            }
        }
        Task parentTask = createTaskFromConfig(entityType, entityId, client,assignedTo,dueDate, office, customConfigMap, taskConfigId, description);

        parentTask.setStatus(TaskStatusType.INITIATED.getValue());
        
        if(TaskType.SINGLE.getValue().equals(parentTask.getTaskType()))
        {
            taskExecutionService.updateActionAndAssignedTo(assignedTo,parentTask,TaskStatusType.fromInt(parentTask.getStatus()));
        }
        parentTask = this.taskRepository.save(parentTask);

        final List<Long> childTaskConfigIds = this.taskPlatformReadService.getChildTaskConfigIds(taskConfigId);

        if (!childTaskConfigIds.isEmpty()) {
            int index = 0;
            final List<Task> tasks = new ArrayList<Task>();
            for (final Long cTaskConfigId : childTaskConfigIds) {
                Task task = createTaskFromConfig(entityType, entityId, client,null,null ,office, customConfigMap, cTaskConfigId, description);
                task.setParent(parentTask);
                if (index == 0) {
                    TaskStatusType status = TaskStatusType.INITIATED;
                    task.setStatus(status.getValue());
                    taskExecutionService.updateActionAndAssignedTo(assignedTo,task, status);
                }
                tasks.add(task);
                index++;
            }
            if (!tasks.isEmpty()) {
                this.taskRepository.save(tasks);
            }
        }
        return parentTask.getId();
    }

    private Task createTaskFromConfig(TaskEntityType entityType, Long entityId, Client client,AppUser user,Date dueDate, Office office,
            Map<String, String> customConfigMap, Long cTaskConfigId, String description) {
        final TaskConfig taskConfig = this.taskConfigRepository.findOneWithNotFoundDetection(cTaskConfigId);
        TaskStatusType status = TaskStatusType.INACTIVE;
        Map<String, String> configValueMap = new HashMap<>();
        final String configValues = taskConfig.getConfigValues();
        if (configValues != null && configValues.trim().length() > 0 && configValues.startsWith("{") && configValues.endsWith("}")) {
            configValueMap = new Gson().fromJson(taskConfig.getConfigValues(), new TypeToken<HashMap<String, String>>() {}.getType());
        }
        configValueMap.putAll(customConfigMap);
        final String configValueStr = new Gson().toJson(configValueMap);
        final Task parent = null;
        final Integer currentAction = null;
        final String criteriaResult = null;
        final Integer criteriaAction = null;

        return Task.create(parent, taskConfig.getName(), taskConfig.getShortName(), entityType.getValue(), entityId,
                taskConfig.getTaskType(), taskConfig, status.getValue(), TaskPriority.MEDIUM.getValue(), dueDate, currentAction,
                user, taskConfig.getTaskConfigOrder(), taskConfig.getCriteria(), taskConfig.getApprovalLogic(),
                taskConfig.getRejectionLogic(), configValueStr, client, office, taskConfig.getActionGroupId(), criteriaResult,
                criteriaAction, taskConfig.getTaskActivity(), description);
    }

    /*
     * @Override public TaskExecutionData getWorkflowExecutionData(final Long
     * workflowExecutionId) { return
     * this.taskReadService.getTaskData(workflowExecutionId); }
     */

    @Override
    public Long createSingleTask(TaskActivity taskActivity, String title, Office office, Map<TaskConfigKey, String> map, Long actionGroupId) {
        Map<String, String> customConfigMap = new HashMap<>();
        if (map != null) {
            for (Map.Entry<TaskConfigKey, String> config : map.entrySet()) {
                customConfigMap.put(config.getKey().getValue(), config.getValue());
            }
        }

        final String configValueStr = new Gson().toJson(map);
        Task task = Task.create(null, title, taskActivity.getIdentifier().substring(0, 3), null, null, TaskType.SINGLE.getValue(), null,
                null, TaskPriority.MEDIUM.getValue(), null, null, null, null, null, null, null, configValueStr, null, office,
                actionGroupId, null, null, taskActivity, null);
        TaskStatusType status = TaskStatusType.INITIATED;
        task.setStatus(status.getValue());
        taskExecutionService.updateActionAndAssignedTo(context.authenticatedUser(),task, status);
        return task.getId();
    }

    @Override
    public CommandProcessingResult assignTaskToMe(Long commandId, String json) {
        AppUser newAssignee = getMakerUser(commandId);
        if(newAssignee!=null){
            Type type = new TypeToken<List<Long>>() {}.getType();
            List<Long> taskIds = fromJsonHelper.getGsonConverter().fromJson(json,type);
            if(taskIds!=null){
                for(Long taskId: taskIds){
                    Task task = taskRepository.findOneWithNotFoundDetection(taskId);
                    if(task.getAssignedTo()==null && task.getCurrentAction()!=null){
                        if(taskExecutionService.canUserDothisAction(newAssignee,task,
                                TaskActionType.fromInt(task.getCurrentAction()))){
                            task.setAssignedTo(newAssignee);
                            taskRepository.save(task);
                        }
                    }
                }
            }
        }
        return new CommandProcessingResult(null);
    }

    @Override
    public CommandProcessingResult unassignTaskFromMe(Long commandId, String json) {
        AppUser requestedAssignee = getMakerUser(commandId);
        if(requestedAssignee!=null){
            Type type = new TypeToken<List<Long>>() {}.getType();
            List<Long> taskIds = fromJsonHelper.getGsonConverter().fromJson(json,type);
            if(taskIds!=null){
                for(Long taskId: taskIds){
                    Task task = taskRepository.findOneWithNotFoundDetection(taskId);
                    if(task.getAssignedTo()!=null){
                        if(requestedAssignee.getId().equals(task.getAssignedTo().getId())){
                            task.setAssignedTo(null);
                            taskRepository.save(task);
                        }
                    }
                }
            }
        }
        return new CommandProcessingResult(null);
    }

    private AppUser getMakerUser(Long commandId) {
        AppUser makerUser = null;
        if(commandId!=null) {
            CommandSource commandSource = commandSourceRepository.findOne(commandId);
            if (commandSource!=null) {
                makerUser = commandSource.getMaker();
            }
        }else{
            makerUser = context.authenticatedUser();
        }
        return makerUser;
    }


}
