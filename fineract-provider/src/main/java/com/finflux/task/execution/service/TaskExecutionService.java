package com.finflux.task.execution.service;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;

import com.finflux.task.execution.data.TaskActionType;
import com.finflux.task.execution.data.TaskConfigKey;
import com.finflux.task.execution.data.TaskData;
import com.finflux.task.execution.data.TaskEntityType;

public interface TaskExecutionService {

    public void createTaskConfigExecution(final Long taskConfigId, final TaskEntityType entityType, final Long entityId,
            final Client client, final Office office, final Map<TaskConfigKey, String> configValues);

    public TaskData getTaskExecutionData(Long workflowExecutionId);

    public void doActionOnWorkflowExecutionStep(Long workflowExecutionStepId, TaskActionType stepAction);

    public void addNoteToWorkflowExecution(Long workflowExecutionId);

    public void addNoteToWorkflowExecutionStep(Long workflowExecutionId);

    List<EnumOptionData> getClickableActionsForUser(Long workflowExecutionStepId, Long id);

    Long getWorkflowExecution(TaskEntityType entityType, Long entityId);

    Long getTaskExecutionData(TaskEntityType loanApplication, Long entityId);
}
