package com.finflux.task.individual.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.domain.TaskConfig;
import com.finflux.task.domain.TaskConfigRepository;
import com.finflux.task.individual.api.CreateTemplateTaskApiConstants;
import com.finflux.task.individual.data.CreateTemplateTaskDataValidator;
import com.finflux.task.individual.form.CreateTemplateTaskForm;
import com.finflux.task.service.TaskPlatformWriteService;
import com.finflux.task.template.data.TaskConfigTemplateEntityType;
import com.finflux.task.template.domain.TaskConfigTemplate;
import com.finflux.task.template.domain.TaskConfigTemplateRepository;

@Service
public class CreateTemplateTaskWriteServiceImpl implements CreateTemplateTaskWriteService 
{
    
    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final CreateTemplateTaskDataValidator taskAssignDataValidator;
    private final TaskConfigTemplateRepository taskConfigTemplateRepository;
    @SuppressWarnings("unused")
    private final TaskConfigRepository taskConfigRepository;
    private final TaskPlatformWriteService taskPlatformWriteService;
    private final AppUserRepository appUserRepository;
    
    
    @Autowired
    public CreateTemplateTaskWriteServiceImpl(final PlatformSecurityContext context,
            final FromJsonHelper fromApiJsonHelper,
            final CreateTemplateTaskDataValidator taskAssignDataValidator,
            final TaskConfigTemplateRepository taskConfigTemplateRepository,
            final TaskConfigRepository taskConfigRepository,
            final TaskPlatformWriteService taskPlatformWriteService,
            final AppUserRepository appUserRepository)
    {
        this.context=context;
        this.fromApiJsonHelper=fromApiJsonHelper;
        this.taskAssignDataValidator=taskAssignDataValidator;
        this.taskConfigTemplateRepository=taskConfigTemplateRepository;
        this.taskConfigRepository=taskConfigRepository;
        this.taskPlatformWriteService=taskPlatformWriteService;
        this.appUserRepository=appUserRepository;
    }
    @Override
    public CommandProcessingResult assignTask(JsonCommand command) 
    {
        this.context.authenticatedUser();
        this.taskAssignDataValidator.validateForAssignTask(command.json());
        final Date dueDate = command.DateValueOfParameterNamed(CreateTemplateTaskApiConstants.DueDateParamName);
        Date dueTime = null;
        final LocalDateTime time = command.localTimeValueOfParameterNamed(CreateTemplateTaskApiConstants.DueTimeParamName);
        if (time != null) {
            dueTime = time.toDate();
        }
        CreateTemplateTaskForm form=fromApiJsonHelper.fromJson(command.json(), CreateTemplateTaskForm.class);
        TaskConfigTemplate taskConfigTemplate=this.taskConfigTemplateRepository.findOne(form.getTemplateId());
        TaskConfig taskConfig= taskConfigTemplate.getTaskConfig();
        TaskConfigTemplateEntityType taskConfigTemplateEntityType = TaskConfigTemplateEntityType.fromInt(taskConfigTemplate.getEntity());
        TaskEntityType taskEntity=taskConfigTemplateEntityType.getCorrespondingTaskEntity();
        String description=form.getDescription();
        Map<TaskConfigKey, String> configValues = new HashMap<>();
        Office office=this.context.authenticatedUser().getOffice();
        configValues.put(taskConfigTemplateEntityType.getCorrespondingTaskConfigKey(), String.valueOf(form.getEntity_id()));
        configValues.put(TaskConfigKey.TASKTEMPLATEENTITY_ID,String.valueOf(form.getEntity_id()));
        Client client=null;
        Long taskId=this.taskPlatformWriteService.createTaskFromConfig(taskConfig.getId(),taskEntity,form.getEntity_id(),client,this.appUserRepository.findOne(form.getUserId()),dueDate,office,configValues,description, dueTime);
        return new CommandProcessingResultBuilder().withResourceIdAsString(taskId.toString()).build();
    }

}
