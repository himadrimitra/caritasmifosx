package com.finflux.task.template.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskType;
import com.finflux.task.domain.TaskActivity;
import com.finflux.task.domain.TaskActivityRepository;
import com.finflux.task.domain.TaskConfig;
import com.finflux.task.domain.TaskConfigRepository;
import com.finflux.task.template.data.TaskConfigTemplateDataValidator;
import com.finflux.task.template.domain.TaskConfigTemplate;
import com.finflux.task.template.domain.TaskConfigTemplateRepository;
import com.finflux.task.template.form.TaskConfigTemplateForm;

@Service
public class TaskConfigTemplateWriteServiceImpl implements TaskConfigTemplateWriteService {

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final TaskConfigTemplateDataValidator taskConfigTemplateDataValidator;
    private final TaskActivityRepository taskActivityRepository;
    private final TaskConfigTemplateRepository taskConfigTemplateRepository;
    private final TaskConfigRepository taskConfigRepository;
    
    @Autowired
    public TaskConfigTemplateWriteServiceImpl(final TaskConfigRepository taskConfigRepository,final TaskConfigTemplateRepository taskConfigTemplateRepository,final TaskActivityRepository taskActivityRepository,final TaskConfigTemplateDataValidator taskConfigTemplateDataValidator,final PlatformSecurityContext context,final FromJsonHelper fromApiJsonHelper) 
    {
       this.context=context;
       this.fromApiJsonHelper=fromApiJsonHelper;
       this.taskConfigTemplateDataValidator=taskConfigTemplateDataValidator;
       this.taskActivityRepository=taskActivityRepository;
       this.taskConfigTemplateRepository=taskConfigTemplateRepository;
       this.taskConfigRepository=taskConfigRepository;
    }
    @Transactional
    @Override
    public CommandProcessingResult addTemplateAsTask(JsonCommand command) 
    {
        this.context.authenticatedUser();
        this.taskConfigTemplateDataValidator.validateForCreateTaskTemplate(command.json());
        TaskConfigTemplateForm form=fromApiJsonHelper.fromJson(command.json(), TaskConfigTemplateForm.class);
        TaskActivity taskActivity=this.taskActivityRepository.findOne(form.getActivity_id());
        TaskConfig taskConfig=new TaskConfig(taskActivity,form.getTaskName(),form.getShortName(),TaskType.SINGLE.getValue());
        final Map<TaskConfigKey, String> map = new HashMap<>();
        map.put(TaskConfigKey.TASKTEMPLATEENTITY_TYPE, form.getEntity_id().toString());
        String configValueMapStr = fromApiJsonHelper.toJson(map);
        taskConfig.setConfigValues(configValueMapStr);
        this.taskConfigRepository.save(taskConfig);
        TaskConfigTemplate taskConfigTemplate=new TaskConfigTemplate();
        taskConfigTemplate.setName(form.getTaskName());
        taskConfigTemplate.setShortName(form.getShortName());
        taskConfigTemplate.setEntity(Integer.parseInt(form.getEntity_id().toString()));
        taskConfigTemplate.setTaskConfig(taskConfig);
        this.taskConfigTemplateRepository.save(taskConfigTemplate);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).build();
    }
    @Override
    public CommandProcessingResult updateTaskConfigTemplate(Long templateId,JsonCommand command) {
        this.context.authenticatedUser();
        final TaskConfigTemplate taskConfigTemplate=this.taskConfigTemplateRepository.findOne(templateId);
//        this.taskConfigTemplateDataValidator.validateForUpdateTaskTemplate(command.json());
        TaskConfigTemplateForm form=fromApiJsonHelper.fromJson(command.json(), TaskConfigTemplateForm.class);
        TaskActivity taskActivity=this.taskActivityRepository.findOne(form.getActivity_id());
        TaskConfig taskConfig=new TaskConfig(taskActivity,form.getTaskName(),form.getShortName(),1);
        this.taskConfigRepository.save(taskConfig);
        taskConfigTemplate.setName(form.getTaskName());
        taskConfigTemplate.setShortName(form.getShortName());
        taskConfigTemplate.setEntity(Integer.parseInt(form.getEntity_id().toString()));
        taskConfigTemplate.setTaskConfig(taskConfig);
        this.taskConfigTemplateRepository.save(taskConfigTemplate);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).build();
    }
  

}
