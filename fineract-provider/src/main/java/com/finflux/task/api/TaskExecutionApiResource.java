package com.finflux.task.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.finflux.task.data.TaskEntityType;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskActionType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.service.TaskExecutionService;

@Path("/taskexecution")
@Component
@Scope("singleton")
public class TaskExecutionApiResource {

    private final PlatformSecurityContext context;
    private final TaskExecutionService taskExecutionService;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskExecutionApiResource(final PlatformSecurityContext context, final TaskExecutionService taskExecutionService,
									final DefaultToApiJsonSerializer toApiJsonSerializer) {
        this.context = context;
        this.taskExecutionService = taskExecutionService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    /**
     * 
     * @param taskId
     * @param uriInfo
     * @return
     */
    @GET
    @Path("{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskData(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        final TaskExecutionData taskExecutionData = this.taskExecutionService.getTaskData(taskId);
        return this.toApiJsonSerializer.serialize(taskExecutionData);
    }

    @POST
    @Path("{taskId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doActionOnTask(@PathParam("taskId") final Long taskId,
                                 @QueryParam("action") final Long actionId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser();
        taskExecutionService.doActionOnTask(taskId, TaskActionType.fromInt(actionId.intValue()));
        TaskExecutionData taskData = taskExecutionService.getTaskData(taskId);
        return this.toApiJsonSerializer.serialize(taskData);
    }

    @GET
    @Path("{taskId}/actions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getNextActionsOnTask(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        context.authenticatedUser();
        List<EnumOptionData> possibleActions = taskExecutionService.getClickableActionsOnTask(taskId);
        return this.toApiJsonSerializer.serialize(possibleActions);
    }


    @GET
    @Path("{taskId}/children")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getChildrenOfTask(@PathParam("taskId") final Long taskId, @Context final UriInfo uriInfo) {
        context.authenticatedUser();
        List<TaskExecutionData> possibleActions = taskExecutionService.getChildrenOfTask(taskId);
        return this.toApiJsonSerializer.serialize(possibleActions);
    }


    @GET
    @Path("{entityType}/{entityId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskData(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
                              @Context final UriInfo uriInfo) {
        TaskEntityType taskEntityType = TaskEntityType.fromString(entityType);
        final TaskExecutionData taskExecutionData = this.taskExecutionService.getTaskIdByEntity(taskEntityType,entityId);
        return this.toApiJsonSerializer.serialize(taskExecutionData);
    }
}
