package com.finflux.task.service.impl;

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

import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.domain.TaskActivity;
import com.finflux.task.domain.TaskActivityRepository;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;
import com.finflux.task.form.AdhocTaskForm;
import com.finflux.task.service.TaskCreationService;
import com.finflux.task.service.TaskPlatformWriteService;

@Service
public class TaskCreationServiceImpl implements TaskCreationService {

    private final static Logger logger = LoggerFactory.getLogger(TaskCreationServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final TaskPlatformWriteService taskPlatformWriteService;
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final OfficeRepositoryWrapper officeRepository;
    private final TaskActivityRepository taskActivityRepository;

    @Autowired
    public TaskCreationServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
                                   final TaskPlatformWriteService taskPlatformWriteService,
                                   final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
                                   final OfficeRepositoryWrapper officeRepository,
                                   final TaskActivityRepository taskActivityRepository) {
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.taskPlatformWriteService = taskPlatformWriteService;
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.officeRepository = officeRepository;
        this.taskActivityRepository = taskActivityRepository;
    }

    @Override
    public CommandProcessingResult createAdhocTask(JsonCommand command) {
        this.context.authenticatedUser();
        AdhocTaskForm form = fromApiJsonHelper.fromJson(command.json(), AdhocTaskForm.class);
//        TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
//                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.ADHOC.getValue(), null);
//        if (taskConfigEntityTypeMapping != null) {
            Map<TaskConfigKey, String> map = new HashMap<>();
            final Long officeId = form.getOfficeId();
            final Office office = this.officeRepository.findOneWithNotFoundDetection(officeId);
            map.put(TaskConfigKey.TITLE, form.getTitle());
            map.put(TaskConfigKey.BODY, form.getBody());
            TaskActivity taskActivity = taskActivityRepository.findOneByIdentifier("adhoc");

            Long taskId = this.taskPlatformWriteService.createSingleTask(taskActivity, form.getTitle(), office, map,null);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(taskId)
                    .build();
//        }
//        return null;
    }
}