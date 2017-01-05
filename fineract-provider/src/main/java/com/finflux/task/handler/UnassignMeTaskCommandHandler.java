package com.finflux.task.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.service.TaskPlatformWriteService;

@Service
@CommandType(entity = "TASK", action = "UNASSIGN")
public class UnassignMeTaskCommandHandler implements NewCommandSourceHandler {

    private final TaskPlatformWriteService taskPlatformWriteService;

    @Autowired
    public UnassignMeTaskCommandHandler(final TaskPlatformWriteService taskPlatformWriteService) {
        this.taskPlatformWriteService = taskPlatformWriteService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.taskPlatformWriteService.unassignTaskFromMe(command.commandId(),command.json());
    }
}