package com.finflux.task.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import com.finflux.task.execution.data.TaskInfoData;
import com.finflux.task.execution.service.TaskReadService;

@Path("/tasks")
@Component
@Scope("singleton")
public class TaskApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final TaskReadService taskReadService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TaskApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final TaskReadService taskReadService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.taskReadService = taskReadService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("summary")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanProductWorkFlowSummary(@QueryParam("loanProductId") final Long loanProductId,
            @QueryParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

        final List<LoanProductData> loanProductWorkFlowSummaries = this.taskReadService.retrieveLoanProductWorkFlowSummary(loanProductId,
                officeId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanProductWorkFlowSummaries);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("actions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveWorkFlowStepActions(@QueryParam("filterby") final String filterBy, @Context final UriInfo uriInfo) {

        final List<TaskInfoData> workFlowStepActions = this.taskReadService.retrieveTaskInformations(filterBy);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, workFlowStepActions);
    }
}