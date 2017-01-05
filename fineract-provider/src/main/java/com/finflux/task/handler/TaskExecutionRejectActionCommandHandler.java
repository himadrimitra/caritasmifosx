package com.finflux.task.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.data.TaskActionType;
import com.finflux.task.service.TaskExecutionService;

@Service
@CommandType(entity = "TASK_EXECUTION", action = "ACTION_REJECT")
public class TaskExecutionRejectActionCommandHandler implements NewCommandSourceHandler {

    private final TaskExecutionService taskExecutionService;

    @Autowired
    public TaskExecutionRejectActionCommandHandler(final TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.taskExecutionService.doActionOnTask(command.entityId(), TaskActionType.REJECT);
    }
}