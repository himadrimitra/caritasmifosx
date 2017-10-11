package com.finflux.task.template.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.template.service.TaskConfigTemplateWriteService;

@Service
@CommandType(entity="TASKCONFIGTEMPLATE",action="UPDATE")

public class UpdateTaskConfigTemplateCommandHandler implements NewCommandSourceHandler
{
    private TaskConfigTemplateWriteService taskConfigTemplateWriteService;
    @Autowired
    public UpdateTaskConfigTemplateCommandHandler(final TaskConfigTemplateWriteService taskConfigTemplateWriteService)
    {
        this.taskConfigTemplateWriteService=taskConfigTemplateWriteService;
    }
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        
        return this.taskConfigTemplateWriteService.updateTaskConfigTemplate(command.entityId(),command);
    }
}
