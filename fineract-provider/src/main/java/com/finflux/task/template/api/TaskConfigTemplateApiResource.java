package com.finflux.task.template.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.api.TaskApiConstants;
import com.finflux.task.template.data.TaskConfigTemplateEntityData;
import com.finflux.task.template.data.TaskConfigTemplateFormData;
import com.finflux.task.template.data.TaskConfigTemplateObject;
import com.finflux.task.template.service.TaskConfigTemplateFormCreateService;
import com.finflux.task.template.service.TaskConfigTemplateFormCreateServiceImpl;
import com.finflux.task.template.service.TaskConfigTemplateReadService;
import com.finflux.task.template.service.TaskConfigTemplateWriteService;

@Path("/taskconfigtemplate")
@Component
@Scope("singleton")
public class TaskConfigTemplateApiResource 
{
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer toApiJsonSerializerTemplate;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final TaskConfigTemplateFormCreateService taskConfigTemplateFormCreateService;
    private final TaskConfigTemplateFormCreateServiceImpl taskConfigTemplateFormCreateServiceImpl;
    private final TaskConfigTemplateWriteService taskConfigTemplateWriteService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final TaskConfigTemplateReadService taskConfigTemplateReadService;
    
    @SuppressWarnings("unused")
    @Autowired
    public TaskConfigTemplateApiResource(final TaskConfigTemplateReadService taskConfigTemplateReadService,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final PlatformSecurityContext context,
            final DefaultToApiJsonSerializer toApiJsonSerializerTemplate,
            final ApiRequestParameterHelper apiRequestParameterHelper
            ,final TaskConfigTemplateFormCreateService taskConfigTemplateFormCreateService,
            final TaskConfigTemplateFormCreateServiceImpl taskConfigTemplateFormCreateServiceImpl,
            final TaskConfigTemplateWriteService taskConfigTemplateWriteService) 
    {
        this.taskConfigTemplateReadService=taskConfigTemplateReadService;
        this.context = context;
        this.toApiJsonSerializerTemplate = toApiJsonSerializerTemplate;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.taskConfigTemplateFormCreateService=taskConfigTemplateFormCreateService;
        this.taskConfigTemplateFormCreateServiceImpl=taskConfigTemplateFormCreateServiceImpl;
        this.taskConfigTemplateWriteService=taskConfigTemplateWriteService;
        this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
    }
    
    
    
    @GET
    @Path("/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo)
    {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final TaskConfigTemplateFormData templateData=this.taskConfigTemplateFormCreateService.retrieveTemplate();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerTemplate.serialize(settings,templateData);
    }
    
    @GET
    @Path("/{templateId}/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateEntities(@PathParam("templateId") final Long templateId,@Context final UriInfo uriInfo)
    {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final TaskConfigTemplateEntityData taskConfigTemplateEntityData=this.taskConfigTemplateReadService.retrieveTemplateEntities(templateId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerTemplate.serialize(settings,taskConfigTemplateEntityData);
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateData(@Context final UriInfo uriInfo)
    {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final List<TaskConfigTemplateObject> templateList=this.taskConfigTemplateReadService.retrieveTemplateData();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerTemplate.serialize(templateList);
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String saveTemplate(final String apiRequestBodyAsJson)
    {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTaskConfigTemplate().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializerTemplate.serialize(result);
    }
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("{templateId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOnetemplate(@PathParam("templateId") final Long templateId,
                                         @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final TaskConfigTemplateObject taskConfigObject = this.taskConfigTemplateReadService.readOneTask(templateId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerTemplate.serialize(settings, taskConfigObject);
    }
    
    @PUT
    @Path("{templateId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateTemplate(@PathParam("templateId") final Long templateId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateTaskConfigTemplate(templateId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializerTemplate.serialize(result);
    }
}
