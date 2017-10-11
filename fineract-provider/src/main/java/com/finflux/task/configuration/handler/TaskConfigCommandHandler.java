package com.finflux.task.configuration.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.configuration.service.TaskConfigurationWriteService;

@Service
@CommandType(entity = "TASK_CONFIG", action = "CREATE")
public class TaskConfigCommandHandler implements NewCommandSourceHandler {

    private final TaskConfigurationWriteService writeService;

    @Autowired
    public TaskConfigCommandHandler(final TaskConfigurationWriteService writeService) {
        this.writeService = writeService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writeService.createTaskConfig(command.entityId(), command);
    }
}