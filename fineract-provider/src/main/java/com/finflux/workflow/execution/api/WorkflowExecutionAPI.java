package com.finflux.workflow.execution.api;

import com.finflux.workflow.execution.data.StepAction;
import com.finflux.workflow.execution.data.WorkflowExecutionData;
import com.finflux.workflow.execution.data.WorkflowExecutionStepData;
import com.finflux.workflow.execution.service.WorkflowExecutionService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/workflowexecution")
@Component
@Scope("singleton")
public class WorkflowExecutionAPI {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("cb_id", "alias", "country", "cb_product_id",
            "start_date", "end_date", "is_active"));
    private final PlatformSecurityContext context;
    private final WorkflowExecutionService workflowExecutionService;
    private final ToApiJsonSerializer toApiJsonSerializer;

    @Autowired
    public WorkflowExecutionAPI(final PlatformSecurityContext context, final WorkflowExecutionService workflowExecutionService,
            final DefaultToApiJsonSerializer toApiJsonSerializer) {
        this.context = context;
        this.workflowExecutionService = workflowExecutionService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    /*
     * Get Workflow Execution details
     */
    @GET
    @Path("{workflowExecutionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getWorkflowExecution(@PathParam("workflowExecutionId") final Long workflowExecutionId, @Context final UriInfo uriInfo) {
        WorkflowExecutionData executionData = workflowExecutionService.getWorkflowExecutionData(workflowExecutionId);
        return this.toApiJsonSerializer.serialize(executionData);
    }

    @POST
    @Path("step/{workflowExecutionStepId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doActionOnWorkflowExecutionStep(@PathParam("workflowExecutionStepId") final Long workflowExecutionStepId,
            @QueryParam("action") final Long actionId, @Context final UriInfo uriInfo) {
        context.authenticatedUser();
        workflowExecutionService.doActionOnWorkflowExecutionStep(workflowExecutionStepId, StepAction.fromInt(actionId.intValue()));
        WorkflowExecutionStepData executionStepData = workflowExecutionService.getWorkflowExecutionStepData(workflowExecutionStepId);
        return this.toApiJsonSerializer.serialize(executionStepData);
    }

    @GET
    @Path("step/{workflowExecutionStepId}/actions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getNextActions(@PathParam("workflowExecutionStepId") final Long workflowExecutionStepId, @Context final UriInfo uriInfo) {
        context.authenticatedUser();
        List<EnumOptionData> possibleActions = workflowExecutionService.getClickableActionsForUser(workflowExecutionStepId, context
                .authenticatedUser().getId());
        return this.toApiJsonSerializer.serialize(possibleActions);
    }
}
