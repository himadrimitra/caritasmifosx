package com.finflux.risk.creditbureau.provider.api;

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
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.loanapplicationreference.api.LoanApplicationReferenceApiConstants;
import com.finflux.risk.creditbureau.provider.data.CreditBureauFileContentData;
import com.finflux.risk.creditbureau.provider.data.OtherInstituteLoansSummaryData;
import com.finflux.risk.creditbureau.provider.exception.SearchParameterNotFoundException;
import com.finflux.risk.creditbureau.provider.service.CreditBureauCheckService;

@Path("/enquiry/creditbureau/")
@Component
@Scope("singleton")
public class CreditBureauEnquiryApiResource {

    private final PlatformSecurityContext context;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CreditBureauCheckService creditBureauCheckService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public CreditBureauEnquiryApiResource(final PlatformSecurityContext context, final DefaultToApiJsonSerializer toApiJsonSerializer,
            ApiRequestParameterHelper apiRequestParameterHelper, CreditBureauCheckService creditBureauCheckService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.creditBureauCheckService = creditBureauCheckService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{entityType}/{entityId}/initiate")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String creditBureauReport(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(
                LoanApplicationReferenceApiConstants.INITIATECREDITBUREAUENQUIRY_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final OtherInstituteLoansSummaryData otherInstituteLoansSummaryData = this.creditBureauCheckService.getCreditBureauEnquiryData(
                entityType, entityId);
        return this.toApiJsonSerializer.serialize(settings, otherInstituteLoansSummaryData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{entityType}/{entityId}/summary")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String creditBureauReportFileContant(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, @QueryParam("trancheDisbursalId") Long trancheDisbursalId,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(LoanApplicationReferenceApiConstants.CREDITBUREAUCHECK_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final OtherInstituteLoansSummaryData otherInstituteLoansSummaryData = this.creditBureauCheckService.getOtherInstituteLoansSummary(
                entityType, entityId, trancheDisbursalId);
        return this.toApiJsonSerializer.serialize(settings, otherInstituteLoansSummaryData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{entityType}/{entityId}/creditbureaureport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditBureauReportFileContent(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(LoanApplicationReferenceApiConstants.CREDITBUREAUREPORT_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final CreditBureauFileContentData creditBureauFileContentData = this.creditBureauCheckService.getCreditBureauReportFileContent(
                entityType, entityId);
        return this.toApiJsonSerializer.serialize(settings, creditBureauFileContentData);
    }
    
	@SuppressWarnings("unchecked")
	@GET
	@Path("/creditbureaureport/")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getLatestCreditBureauReportFileContent(@DefaultValue("true")@QueryParam("isLatestdata") final boolean isLatestdata , 
			@QueryParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
		this.context.authenticatedUser()
				.validateHasReadPermission(LoanApplicationReferenceApiConstants.CREDITBUREAUREPORT_RESOURCE_NAME);
		if(clientId == null){
			String errorMessage = "At least one search parameter should be there";
			throw new SearchParameterNotFoundException(errorMessage, errorMessage);
		}
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		final SearchParameters searchParameters = SearchParameters.forCreditBureauSearchParameters(clientId);
		final CreditBureauFileContentData creditBureauFileContentData = this.creditBureauCheckService
				.getCreditBureauReportFileContent(isLatestdata, searchParameters);

		return this.toApiJsonSerializer.serialize(settings, creditBureauFileContentData);
	}

}