package com.finflux.task.service;

import java.util.List;

import com.finflux.task.data.TaskActionType;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.data.TaskStatusType;
import com.finflux.task.domain.Task;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public interface TaskExecutionService {

    void updateActionAndAssignedTo(Task task, TaskStatusType newStatus);

    TaskExecutionData getTaskData(Long taskId);

    void doActionOnTask(Long workflowExecutionStepId, TaskActionType stepAction);

    // public void addNoteToTask(Long taskId);

    TaskExecutionData getTaskIdByEntity(TaskEntityType taskEntityType, Long entityId);

    List<EnumOptionData> getClickableActionsOnTask(Long workflowExecutionStepId);

    List<TaskExecutionData> getChildrenOfTask(Long taskId);

}
