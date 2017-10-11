package com.finflux.task.individual.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.individual.service.CreateTemplateTaskWriteService;

@Service
@CommandType(entity="TEMPLATETASK",action="CREATE")
public class CreateTemplateTaskCommandHandler implements NewCommandSourceHandler
{
    private final CreateTemplateTaskWriteService createTemplateTaskWriteService;
    
    @Autowired
    public CreateTemplateTaskCommandHandler(final CreateTemplateTaskWriteService createTemplateTaskWriteService)
    {
        this.createTemplateTaskWriteService=createTemplateTaskWriteService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        
        return this.createTemplateTaskWriteService.assignTask(command);
    }
}
