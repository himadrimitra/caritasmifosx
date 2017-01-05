package com.finflux.task.service;

import java.util.List;

import com.finflux.task.data.*;
import com.finflux.task.domain.Task;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.useradministration.domain.AppUser;

public interface TaskExecutionService {

    void updateActionAndAssignedTo(AppUser appUser, Task task, TaskStatusType newStatus);

    boolean canUserDothisAction(AppUser appUser, Task task, TaskActionType taskActionType);

    TaskExecutionData getTaskData(Long taskId);

    CommandProcessingResult doActionOnTask(AppUser appUser, Long workflowExecutionStepId, TaskActionType stepAction);

    // public void addNoteToTask(Long taskId);

    TaskExecutionData getTaskIdByEntity(TaskEntityType taskEntityType, Long entityId);

    List<TaskActionData> getClickableActionsOnTask(AppUser appUser, Long workflowExecutionStepId);

    List<TaskExecutionData> getChildrenOfTask(Long taskId);

	List<TaskNoteData> getTaskNotes(Long taskId);

    CommandProcessingResult addNoteToTask(AppUser appUser, Long taskId, String noteForm);

    List<TaskActionLogData> getActionLogs(Long taskId);

    CommandProcessingResult addNoteToTask(Long aLong, String json);

    CommandProcessingResult doActionOnTask(Long aLong, TaskActionType activitycomplete);
}
