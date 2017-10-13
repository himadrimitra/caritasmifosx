package com.finflux.task.configuration.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.configuration.service.TaskConfigurationWriteService;

@Service
@CommandType(entity = "TASK_CONFIG_ENTITYMAPPING", action = "INACTIVATE")
public class InActiveTaskConfigEntityMappingCommandHandler implements NewCommandSourceHandler {

    private final TaskConfigurationWriteService taskConfigWriteService;

    @Autowired
    public InActiveTaskConfigEntityMappingCommandHandler(final TaskConfigurationWriteService taskConfigWriteService) {
        this.taskConfigWriteService = taskConfigWriteService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        // TODO Auto-generated method stub
        return this.taskConfigWriteService.inActivateTaskConfigEntityMapping(command);
    }
}
