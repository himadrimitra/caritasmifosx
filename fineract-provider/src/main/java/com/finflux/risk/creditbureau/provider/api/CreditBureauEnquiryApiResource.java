package com.finflux.risk.creditbureau.provider.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.risk.creditbureau.provider.data.CreditBureauReportData;
import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkApiConstants;
import com.finflux.risk.creditbureau.provider.service.CreditBureauCheckService;

@Path("/enquiry/creditbureau/")
@Component
@Scope("singleton")
public class CreditBureauEnquiryApiResource {

    private final ToApiJsonSerializer<CreditBureauReportData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CreditBureauCheckService creditBureauCheckService;

    @Autowired
    public CreditBureauEnquiryApiResource(ToApiJsonSerializer<CreditBureauReportData> toApiJsonSerializer,
            ApiRequestParameterHelper apiRequestParameterHelper, CreditBureauCheckService creditBureauCheckService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.creditBureauCheckService = creditBureauCheckService;
    }

    @GET
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getHighmarkData(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {
        CreditBureauReportData reportData = creditBureauCheckService.getCreditBureauDataForLoan(loanId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, reportData, HighmarkApiConstants.HIGHMARK_RESPONSE_DATA_PARAMETERS);
    }

}