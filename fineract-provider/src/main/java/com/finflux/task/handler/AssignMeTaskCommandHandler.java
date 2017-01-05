package com.finflux.task.handler;

import com.finflux.task.service.TaskPlatformWriteService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.service.TaskCreationService;

@Service
@CommandType(entity = "TASK", action = "ASSIGN")
public class AssignMeTaskCommandHandler implements NewCommandSourceHandler {

    private final TaskPlatformWriteService taskPlatformWriteService;

    @Autowired
    public AssignMeTaskCommandHandler(final TaskPlatformWriteService taskPlatformWriteService) {
        this.taskPlatformWriteService = taskPlatformWriteService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.taskPlatformWriteService.assignTaskToMe(command.commandId(),command.json());
    }
}