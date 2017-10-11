package com.finflux.task.template.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.template.service.TaskConfigTemplateWriteService;

@Service
@CommandType(entity="TASKCONFIGTEMPLATE",action="CREATE")
public class CreateTaskConfigTemplateCommandHandler implements NewCommandSourceHandler {

    private TaskConfigTemplateWriteService taskConfigTemplateWriteService;
    @Autowired
    public CreateTaskConfigTemplateCommandHandler(final TaskConfigTemplateWriteService taskConfigTemplateWriteService)
    {
        this.taskConfigTemplateWriteService=taskConfigTemplateWriteService;
    }
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) 
    {
        return this.taskConfigTemplateWriteService.addTemplateAsTask(command);
    }

}
