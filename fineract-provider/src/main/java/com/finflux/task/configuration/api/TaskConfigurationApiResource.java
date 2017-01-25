package com.finflux.task.configuration.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import com.finflux.task.configuration.data.LoanProdcutTasksConfigTemplateData;
import com.finflux.task.configuration.exception.TaskConfigEntityTypeNotFoundException;
import com.finflux.task.configuration.service.TaskConfigurationReadService;
import com.finflux.task.data.TaskConfigEntityType;

@Path("/tasksconfigurations/{entityType}/{entityId}")
@Component
@Scope("singleton")
public class TaskConfigurationApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final TaskConfigurationReadService readService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskConfigurationApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final TaskConfigurationReadService readService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readService = readService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @Context final UriInfo uriInfo) {
        final TaskConfigEntityType taskConfigEntityType = getAndValidateTaskConfigEntityType(entityType);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (TaskConfigurationApiConstants.LOANPRODUCT.equals(taskConfigEntityType.toString())) {
            final LoanProdcutTasksConfigTemplateData loanProdcutTasksConfigTemplateData = this.readService
                    .retrieveLoanProdcutTasksConfigTemplateData();
            return this.toApiJsonSerializer.serialize(settings, loanProdcutTasksConfigTemplateData);
        }
        return null;
    }

    private TaskConfigEntityType getAndValidateTaskConfigEntityType(String entityType) {
        final TaskConfigEntityType taskConfigEntityType = TaskConfigEntityType.getEntityType(entityType.toUpperCase());
        if (taskConfigEntityType == null || taskConfigEntityType.toString().equals(TaskConfigurationApiConstants.INVALID)) { throw new TaskConfigEntityTypeNotFoundException(
                entityType); }
        return taskConfigEntityType;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createTaskConfig(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            final String apiRequestBodyAsJson) {
        getAndValidateTaskConfigEntityType(entityType);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTaskConfig(entityType, entityId)
                .withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}
