package com.finflux.workflow.execution.api;

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

import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.workflow.execution.service.WorkFlowStepReadService;

@Path("/workflowsteps")
@Component
@Scope("singleton")
public class WorkFlowStepApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final WorkFlowStepReadService workFlowTaskReadService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public WorkFlowStepApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final WorkFlowStepReadService workFlowTaskReadService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.workFlowTaskReadService = workFlowTaskReadService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("summary")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanProductWorkFlowSummary(@QueryParam("loanProductId") final Long loanProductId,
            @QueryParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESSES_RESOURCE_NAME);

        final List<LoanProductData> loanProductWorkFlowSummaries = this.workFlowTaskReadService.retrieveLoanProductWorkFlowSummary(
                loanProductId, officeId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanProductWorkFlowSummaries);
    }
}