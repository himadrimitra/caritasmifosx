package com.finflux.risk.creditbureau.summary.api;

import javax.ws.rs.Consumes;
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
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.risk.creditbureau.summary.data.CreditBureauSummaryData;
import com.finflux.risk.creditbureau.summary.service.CreditBureauSummaryReadPlatformService;
import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;

@Path("/clients/creditsummary/{clientId}/")
@Component
@Scope("singleton")
public class CreditSummaryApiResource {

    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<CreditBureauSummaryData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CreditBureauSummaryReadPlatformService creditBureauSummaryReadPlatformService ;
    @Autowired
    public CreditSummaryApiResource(final PlatformSecurityContext context,
            final ToApiJsonSerializer<CreditBureauSummaryData> toApiJsonSerializer,
           final ApiRequestParameterHelper apiRequestParameterHelper,
            final CreditBureauSummaryReadPlatformService creditBureauSummaryReadPlatformService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.creditBureauSummaryReadPlatformService = creditBureauSummaryReadPlatformService ;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveAllExistingLoan(@PathParam("clientId") final Long clientId,
            @QueryParam("loanApplicationId") final Long loanApplicationId, @QueryParam("loanId") final Long loanId,
            @QueryParam("trancheDisbursalId") final Long trancheDisbursalId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ExistingLoanApiConstants.EXISTINGLOAN_RESOURCE_NAME);
        CreditBureauSummaryData data = this.creditBureauSummaryReadPlatformService.retrieveCreditSummary(clientId, loanApplicationId, loanId, trancheDisbursalId) ;
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data,
                ExistingLoanApiConstants.EXISTING_LOAN_RESPONSE_DATA_PARAMETERS);
    }
}
