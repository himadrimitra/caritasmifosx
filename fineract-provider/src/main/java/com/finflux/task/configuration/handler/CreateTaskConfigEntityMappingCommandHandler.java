package com.finflux.task.configuration.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.configuration.service.TaskConfigurationWriteService;

@Service
@CommandType(entity = "TASK_CONFIG_ENTITYMAPPING", action = "CREATE")
public class CreateTaskConfigEntityMappingCommandHandler implements NewCommandSourceHandler {

    private final TaskConfigurationWriteService taskConfigWriteService;

    @Autowired
    public CreateTaskConfigEntityMappingCommandHandler(final TaskConfigurationWriteService taskConfigWriteService) {
        this.taskConfigWriteService = taskConfigWriteService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        // TODO Auto-generated method stub
        return this.taskConfigWriteService.createTaskConfigEntityMapping(command.entityId(), command);
    }

}
