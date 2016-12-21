package com.finflux.task.api;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskInfoData;
import com.finflux.task.data.TaskTemplateData;
import com.finflux.task.service.TaskPlatformReadService;
import com.finflux.task.service.TaskPlatformReadServiceImpl;

@Path("/tasks")
@Component
@Scope("singleton")
public class TaskApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final TaskPlatformReadService taskPlatformReadService;
    private final TaskPlatformReadServiceImpl taskConfigurationReadService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final TaskPlatformReadService taskPlatformReadService,
            final TaskPlatformReadServiceImpl taskConfigurationReadService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.taskPlatformReadService = taskPlatformReadService;
        this.taskConfigurationReadService = taskConfigurationReadService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("summary")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanProductTasksSummary(@QueryParam("loanProductId") final Long loanProductId,
            @QueryParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

        final List<LoanProductData> loanProductTaskSummaries = this.taskPlatformReadService.retrieveLoanProductTaskSummary(loanProductId,
                officeId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanProductTaskSummaries);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("actions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveWorkFlowStepActions(@QueryParam("filterby") final String filterBy, @Context final UriInfo uriInfo) {

        final List<TaskInfoData> workFlowStepActions = this.taskPlatformReadService.retrieveTaskInformations(filterBy);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, workFlowStepActions);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly) {

        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_RESOURCE_NAME);

        TaskTemplateData taskTemplateData = null;
        taskTemplateData = this.taskConfigurationReadService.retrieveTemplate(officeId, staffInSelectedOfficeOnly);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, taskTemplateData);
    }
}