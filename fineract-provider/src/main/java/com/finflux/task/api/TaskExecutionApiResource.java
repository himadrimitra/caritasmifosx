package com.finflux.task.api;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.task.exception.TaskInvalidActionException;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.data.*;
import com.finflux.task.service.TaskExecutionService;

@Path("/tasks/{taskId}/execute")
@Component
@Scope("singleton")
public class TaskExecutionApiResource {

    private final PlatformSecurityContext context;
    private final TaskExecutionService taskExecutionService;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final FromJsonHelper fromJsonHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskExecutionApiResource(final PlatformSecurityContext context, final TaskExecutionService taskExecutionService,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final FromJsonHelper fromJsonHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.taskExecutionService = taskExecutionService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.fromJsonHelper = fromJsonHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    /**
     * 
     * @param taskId
     * @param uriInfo
     * @return
     */
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskData(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASKEXECUTION_RESOURCE_NAME);
        final TaskExecutionData taskExecutionData = this.taskExecutionService.getTaskData(taskId);
        return this.toApiJsonSerializer.serialize(taskExecutionData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doActionOnTask(@PathParam("taskId") final Long taskId, @QueryParam("action") final String actionName,
                                 final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        TaskActionType taskActionType = TaskActionType.fromString(actionName);
        if(taskActionType==null){
            throw new TaskInvalidActionException(actionName);
        }
        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);
        CommandWrapper commandRequest = builder.doActionOnTask(taskId,taskActionType).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("actions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getNextActionsOnTask(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASKEXECUTION_RESOURCE_NAME);
        List<TaskActionData> possibleActions = taskExecutionService.getClickableActionsOnTask(
                context.authenticatedUser(),taskId);
        return this.toApiJsonSerializer.serialize(possibleActions);
    }

    @GET
    @Path("children")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getChildrenOfTask(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASKEXECUTION_RESOURCE_NAME);
        List<TaskExecutionData> possibleActions = taskExecutionService.getChildrenOfTask(taskId);
        return this.toApiJsonSerializer.serialize(possibleActions);
    }

    @GET
    @Path("notes")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskNotes(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASKEXECUTION_NOTE_RESOURCE_NAME);
        List<TaskNoteData> taskNotes = taskExecutionService.getTaskNotes(taskId);
        return this.toApiJsonSerializer.serialize(taskNotes);
    }

    @GET
    @Path("actionlog")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskActionLogs(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(TaskApiConstants.TASKEXECUTION_ACTIONLOG_RESOURCE_NAME);
        List<TaskActionLogData> taskActionLogs = taskExecutionService.getActionLogs(taskId);
        return this.toApiJsonSerializer.serialize(taskActionLogs);
    }


    @POST
    @Path("notes")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createTaskNote(@PathParam("taskId") final Long taskId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandWrapper commandRequest = builder.addNoteToTask(taskId).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

}
