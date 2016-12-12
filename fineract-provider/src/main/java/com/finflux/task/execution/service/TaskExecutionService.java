package com.finflux.task.execution.service;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;

import com.finflux.task.execution.data.TaskActionType;
import com.finflux.task.execution.data.TaskConfigKey;
import com.finflux.task.execution.data.TaskExecutionData;
import com.finflux.task.execution.data.TaskEntityType;

public interface TaskExecutionService {

    void createTaskFromConfig(final Long taskConfigId, final TaskEntityType entityType, final Long entityId, final Client client,
            final Office office, final Map<TaskConfigKey, String> configValues);

    TaskExecutionData getTaskData(Long taskId);

    void doActionOnTask(Long workflowExecutionStepId, TaskActionType stepAction);

    // public void addNoteToTask(Long taskId);

    TaskExecutionData getTaskIdByEntity(TaskEntityType taskEntityType, Long entityId);

    List<EnumOptionData> getClickableActionsOnTask(Long workflowExecutionStepId);

    List<TaskExecutionData> getChildrenOfTask(Long taskId);
}
