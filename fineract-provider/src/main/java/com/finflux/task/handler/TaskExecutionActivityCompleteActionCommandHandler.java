package com.finflux.task.handler;

import com.finflux.task.data.TaskActionType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.service.TaskExecutionService;
import com.finflux.task.service.impl.TaskExecutionServiceImpl;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.service.TaskCreationService;

@Service
@CommandType(entity = "TASK_EXECUTION", action = "ACTION_ACTIVITYCOMPLETE")
public class TaskExecutionActivityCompleteActionCommandHandler implements NewCommandSourceHandler {

    private final TaskExecutionService taskExecutionService;

    @Autowired
    public TaskExecutionActivityCompleteActionCommandHandler(final TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.taskExecutionService.doActionOnTask(command.entityId(), TaskActionType.ACTIVITYCOMPLETE);
    }
}