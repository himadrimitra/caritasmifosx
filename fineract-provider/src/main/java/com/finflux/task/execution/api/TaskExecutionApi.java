package com.finflux.task.execution.api;

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

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.task.execution.data.TaskActionType;
import com.finflux.task.execution.data.TaskData;
import com.finflux.task.execution.service.TaskExecutionService;

@Path("/taskexecutions")
@Component
@Scope("singleton")
public class TaskExecutionApi {

    private final PlatformSecurityContext context;
    private final TaskExecutionService taskExecutionService;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskExecutionApi(final PlatformSecurityContext context, final TaskExecutionService taskExecutionService,
            final DefaultToApiJsonSerializer toApiJsonSerializer) {
        this.context = context;
        this.taskExecutionService = taskExecutionService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    /**
     * 
     * @param taskExecutionId
     * @param uriInfo
     * @return
     */
    @GET
    @Path("{taskExecutionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTaskExecutionData(@PathParam("taskExecutionId") final Long taskExecutionId, @Context final UriInfo uriInfo) {
        final TaskData taskExecutionData = this.taskExecutionService.getTaskExecutionData(taskExecutionId);
        return this.toApiJsonSerializer.serialize(taskExecutionData);
    }

    @POST
    @Path("step/{workflowExecutionStepId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doActionOnWorkflowExecutionStep(@PathParam("workflowExecutionStepId") final Long workflowExecutionStepId,
            @QueryParam("action") final Long actionId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser();
        taskExecutionService.doActionOnWorkflowExecutionStep(workflowExecutionStepId, TaskActionType.fromInt(actionId.intValue()));
        //WorkflowExecutionStepData executionStepData = taskExecutionService.getWorkflowExecutionStepData(workflowExecutionStepId);
        return this.toApiJsonSerializer.serialize(null);
    }

    @GET
    @Path("step/{workflowExecutionStepId}/actions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getNextActions(@PathParam("workflowExecutionStepId") final Long workflowExecutionStepId, @Context final UriInfo uriInfo) {
        context.authenticatedUser();
        List<EnumOptionData> possibleActions = taskExecutionService.getClickableActionsForUser(workflowExecutionStepId, context
                .authenticatedUser().getId());
        return this.toApiJsonSerializer.serialize(possibleActions);
    }
}
