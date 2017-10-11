package com.finflux.task.api;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.service.TaskExecutionService;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
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
    private final TaskExecutionService taskExecutionService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final TaskPlatformReadService taskPlatformReadService,
            final TaskPlatformReadServiceImpl taskConfigurationReadService,
           final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                           final TaskExecutionService taskExecutionService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.taskPlatformReadService = taskPlatformReadService;
        this.taskConfigurationReadService = taskConfigurationReadService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.taskExecutionService = taskExecutionService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTaskList(@QueryParam("filterby") final String filterBy, @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit, @QueryParam("officeId") final Long officeId,
            @QueryParam("parentConfigId") final Long parentConfigId, @QueryParam("childConfigId") final Long childConfigId,
            @Context final UriInfo uriInfo, @QueryParam("loanType") final Integer loanType, @QueryParam("centerId") final Long centerId) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASK_RESOURCE_NAME);
        SearchParameters searchParameters = SearchParameters.forTask(null, officeId, null, null, null, offset, limit, null, null, null);
        final Page<TaskInfoData> workFlowStepActions = this.taskPlatformReadService.retrieveTaskInformations(filterBy, searchParameters,
                parentConfigId, childConfigId, loanType, centerId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, workFlowStepActions);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateTask( @QueryParam("command") final String commandParam,
                                        final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "assign")) {
            commandRequest = builder.assignTaskToMe().build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if(is(commandParam, "unassign")){
            commandRequest = builder.unAssignTaskFromMe().build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }
        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "assign", "unassign" }); }
        return this.toApiJsonSerializer.serialize(result);
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

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }


    @GET
    @Path("{entityType}/{entityId}/execute/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskData(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
                              @Context final UriInfo uriInfo) {
        TaskEntityType taskEntityType = TaskEntityType.fromString(entityType);
        final TaskExecutionData taskExecutionData = this.taskExecutionService.getTaskIdByEntity(taskEntityType, entityId);
        return this.toApiJsonSerializer.serialize(taskExecutionData);
    }
}