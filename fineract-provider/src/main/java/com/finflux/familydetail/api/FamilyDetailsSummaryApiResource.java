package com.finflux.familydetail.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.familydetail.FamilyDetailsSummaryApiConstants;
import com.finflux.familydetail.data.FamilyDetailsSummaryData;
import com.finflux.familydetail.service.FamilyDetailsSummaryReadPlatformService;

@Path("/clients/{clientId}/familydetailssummary")
@Component
@Scope("singleton")
public class FamilyDetailsSummaryApiResource {

    private final PlatformSecurityContext platformSecurityContext;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
    private final FamilyDetailsSummaryReadPlatformService familyDetailsSummaryReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public FamilyDetailsSummaryApiResource(final PlatformSecurityContext platformSecurityContext,
            final ApiRequestParameterHelper apiRequestParameterHelper, final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
            final FamilyDetailsSummaryReadPlatformService familyDetailsSummaryReadPlatformService) {
        this.platformSecurityContext = platformSecurityContext;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
        this.familyDetailsSummaryReadPlatformService = familyDetailsSummaryReadPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createFamilyDetailsSummary(clientId) //
                .withJson(apiRequestBodyAsJson) //
                .build();
        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{familyDetailsSummaryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("clientId") final Long clientId, @PathParam("familyDetailsSummaryId") final Long familyDetailsSummaryId,
            final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateFamilyDetailsSummary(clientId, familyDetailsSummaryId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{familyDetailsSummaryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("clientId") final Long clientId, @PathParam("familyDetailsSummaryId") final Long familyDetailsSummaryId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteFamilyDetailsSummary(familyDetailsSummaryId, clientId) //
                .build(); //

        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieve(@Context final UriInfo uriInfo, @PathParam("clientId") final Long clientId) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(
                FamilyDetailsSummaryApiConstants.FAMILY_DETAILS_SUMMARY_RESOURCE_NAME);

        final FamilyDetailsSummaryData familyDetailsData = this.familyDetailsSummaryReadPlatformService.retrieve(clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, familyDetailsData);
    }
}