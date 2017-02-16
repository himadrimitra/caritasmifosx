package com.finflux.task.individual.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import com.finflux.task.individual.data.CreateTemplateTaskTemplateData;
import com.finflux.task.individual.service.CreateTemplateTaskReadService;


@Path("/taskassign")
@Component
@Scope("singleton")

public class CreateTemplateTaskApiResource 
{
    private final PlatformSecurityContext context;
    private final CreateTemplateTaskReadService createTemplateTaskReadService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer toApiJsonSerializerTemplate;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    
    @Autowired
    public CreateTemplateTaskApiResource(
            final PlatformSecurityContext context,
            final CreateTemplateTaskReadService createTemplateTaskReadService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializerTemplate,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService)
    {
        this.context=context;
        this.createTemplateTaskReadService=createTemplateTaskReadService;
        this.apiRequestParameterHelper=apiRequestParameterHelper;
        this.toApiJsonSerializerTemplate = toApiJsonSerializerTemplate;
        this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
    }

    @GET
    @Path("/template")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public String getTemplate(@Context final UriInfo uriInfo)
    {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final CreateTemplateTaskTemplateData createTemplateTaskTemplateData=this.createTemplateTaskReadService.retrieveForm();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerTemplate.serialize(settings,createTemplateTaskTemplateData);
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String saveTemplate(final String apiRequestBodyAsJson)
    {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().assignTask().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializerTemplate.serialize(result);
    }
}

