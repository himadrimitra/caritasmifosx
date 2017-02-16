package com.finflux.task.individual.service;

import java.util.List;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.data.AppUserData;
import org.apache.fineract.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.individual.data.CreateTemplateTaskTemplateData;
import com.finflux.task.template.data.TaskConfigTemplateObject;
import com.finflux.task.template.service.TaskConfigTemplateReadService;

@Service
public class CreateTemplateTaskReadServiceImpl implements CreateTemplateTaskReadService
{
    private final PlatformSecurityContext context;
    private final TaskConfigTemplateReadService taskConfigTemplateReadService;
    private final AppUserReadPlatformService readPlatformService;
    
    @Autowired
    public CreateTemplateTaskReadServiceImpl(
            final PlatformSecurityContext context,
            final TaskConfigTemplateReadService taskConfigTemplateReadService,
            final AppUserReadPlatformService readPlatformService) 
    {
        this.context=context;
        this.taskConfigTemplateReadService=taskConfigTemplateReadService;
        this.readPlatformService=readPlatformService;
    }

    @Override
    public CreateTemplateTaskTemplateData retrieveForm() 
    {
        this.context.authenticatedUser();
        final List<TaskConfigTemplateObject> templateList=this.taskConfigTemplateReadService.retrieveTemplateData();
        final List<AppUserData> users = (List<AppUserData>) this.readPlatformService.retrieveAllUsers();
        final CreateTemplateTaskTemplateData taskAssignTemplateData=new CreateTemplateTaskTemplateData(templateList, users);
        return taskAssignTemplateData;
    }

}
