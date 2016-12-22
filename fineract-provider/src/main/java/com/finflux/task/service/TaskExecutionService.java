package com.finflux.task.service;

import java.util.List;

import com.finflux.task.data.*;
import com.finflux.task.domain.Task;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public interface TaskExecutionService {

    void updateActionAndAssignedTo(Task task, TaskStatusType newStatus);

    TaskExecutionData getTaskData(Long taskId);

    void doActionOnTask(Long workflowExecutionStepId, TaskActionType stepAction);

    // public void addNoteToTask(Long taskId);

    TaskExecutionData getTaskIdByEntity(TaskEntityType taskEntityType, Long entityId);

    List<TaskActionData> getClickableActionsOnTask(Long workflowExecutionStepId);

    List<TaskExecutionData> getChildrenOfTask(Long taskId);

	List<TaskNoteData> getTaskNotes(Long taskId);

    Long addNoteToTask(Long taskId, TaskNoteForm noteForm);

    List<TaskActionLogData> getActionLogs(Long taskId);
}
