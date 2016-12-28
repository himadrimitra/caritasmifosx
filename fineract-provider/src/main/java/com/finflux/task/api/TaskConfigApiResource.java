package com.finflux.task.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigData;
import com.finflux.task.data.TaskConfigTemplateData;
import com.finflux.task.service.TaskConfigReadService;

@Path("/tasks/config")
@Component
@Scope("singleton")
public class TaskConfigApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final TaskConfigReadService taskConfigReadService;

    @Autowired
    public TaskConfigApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final TaskConfigReadService taskConfigReadService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.taskConfigReadService = taskConfigReadService;
    }

    @GET
    @Path("{configId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveConfig(@Context final UriInfo uriInfo, @PathParam("configId") final Long configId) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        TaskConfigData taskConfigData = this.taskConfigReadService.retrieveOne(configId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, taskConfigData);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo,
            @DefaultValue("false") @QueryParam("includeChildren") final Boolean includeChildren,
            @QueryParam("parentConfigId") final Long parentConfigId) {
        TaskConfigTemplateData taskTemplateData = null;
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_CONFIG_RESOURCE_NAME);
        if (includeChildren) {
            taskTemplateData = this.taskConfigReadService.retrieveTemplate();
        } else {
            taskTemplateData = this.taskConfigReadService.retrieveForLookUp(parentConfigId);
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, taskTemplateData);
    }

}
