package com.finflux.task.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.configuration.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.configuration.domain.TaskConfigEntityTypeMappingRepository;
import com.finflux.task.execution.data.TaskConfigEntityType;
import com.finflux.task.execution.data.TaskConfigKey;
import com.finflux.task.execution.data.TaskEntityType;
import com.finflux.task.execution.service.TaskExecutionService;
import com.finflux.task.form.AdhocTaskForm;

@Service
public class TaskCreationServiceImpl implements TaskCreationService {

    private final static Logger logger = LoggerFactory.getLogger(TaskCreationServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final TaskExecutionService taskExecutionService;
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final OfficeRepositoryWrapper officeRepository;

    @Autowired
    public TaskCreationServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
            final TaskExecutionService workflowExecutionService,
            final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final OfficeRepositoryWrapper officeRepository) {
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.taskExecutionService = workflowExecutionService;
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.officeRepository = officeRepository;
    }

    @Override
    public CommandProcessingResult createAdhocTask(JsonCommand command) {
        this.context.authenticatedUser();
        AdhocTaskForm form = fromApiJsonHelper.fromJson(command.json(), AdhocTaskForm.class);
        TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.ADHOC.getValue(), null);
        if (taskConfigEntityTypeMapping != null) {
            Map<TaskConfigKey, String> map = new HashMap<>();
            final Long officeId = form.getOfficeId();
            final Office office = this.officeRepository.findOneWithNotFoundDetection(officeId);
            map.put(TaskConfigKey.TITLE, form.getTitle());
            map.put(TaskConfigKey.BODY, form.getBody());
            this.taskExecutionService.createTaskConfigExecution(taskConfigEntityTypeMapping.getTaskConfig().getId(), TaskEntityType.ADHOC,
                    null, null, office, map);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .build();
        }
        return null;
    }
}