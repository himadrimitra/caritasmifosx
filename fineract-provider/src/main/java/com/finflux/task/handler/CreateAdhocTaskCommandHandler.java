package com.finflux.task.handler;

import com.finflux.task.service.TaskCreationService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.configuration.service.RiskConfigWritePlatformService;
import com.finflux.ruleengine.lib.data.EntityRuleType;

@Service
@CommandType(entity = "ADHOCTASK", action = "CREATE")
public class CreateAdhocTaskCommandHandler implements NewCommandSourceHandler {

    private final TaskCreationService taskCreationService;

    @Autowired
    public CreateAdhocTaskCommandHandler(final TaskCreationService taskCreationService) {
        this.taskCreationService = taskCreationService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.taskCreationService.createAdhocTask(command);
    }
}