package com.finflux.familydetail.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.data.FamilyDetailData;
import com.finflux.familydetail.data.FamilyDetailTemplateData;
import com.finflux.familydetail.service.FamilyDetailsReadPlatformService;

@Path("/clients/{clientId}/familydetails")
@Component
@Scope("singleton")
public class FamilyDetailApiResource {

    private final PlatformSecurityContext platformSecurityContext;
    private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
    private final FamilyDetailsReadPlatformService familyDetailsReadPlatformService;
    private final ToApiJsonSerializer<FamilyDetailData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer defaultToApiJsonSerializer;

    @SuppressWarnings("rawtypes")
    @Autowired
    public FamilyDetailApiResource(final PlatformSecurityContext platformSecurityContext,
            final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
            final FamilyDetailsReadPlatformService familyDetailsReadPlatformService,
            final ToApiJsonSerializer<FamilyDetailData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer defaultToApiJsonSerializer) {
        this.platformSecurityContext = platformSecurityContext;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
        this.familyDetailsReadPlatformService = familyDetailsReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.defaultToApiJsonSerializer = defaultToApiJsonSerializer;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveFamilyDetailsTemplate(@Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(FamilyDetailsApiConstants.FAMILY_DETAIL_RESOURCE_NAME);
        final FamilyDetailTemplateData familyDetailTemplateData = this.familyDetailsReadPlatformService.retrieveTemplate();
        ApiRequestJsonSerializationSettings settings = null;
        if (uriInfo != null) {
            settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.defaultToApiJsonSerializer.serialize(settings, familyDetailTemplateData);
        }
        return this.defaultToApiJsonSerializer.serialize(familyDetailTemplateData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createFamilyDetails(clientId) //
                .withJson(apiRequestBodyAsJson) //
                .build();
        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{familyDetailsId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("familyDetailsId") final Long familyDetailsId, @PathParam("clientId") final Long clientId,
            final String apiRequestBodyAsJson, @QueryParam("command") final String command) {
    	if(command!= null && command.equals(FamilyDetailsApiConstants.removeFamilyMemberClientAssociation)){
    		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
					.removeFamilyMemberAssociation(clientId, familyDetailsId) //
					.withJson(apiRequestBodyAsJson) //
					.build(); //

			final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService
					.logCommandSource(commandRequest);

			return this.toApiJsonSerializer.serialize(result);
    	}else {
			final CommandWrapper commandRequest = new CommandWrapperBuilder() //
					.updateFamilyDetails(clientId, familyDetailsId) //
					.withJson(apiRequestBodyAsJson) //
					.build(); //

			final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService
					.logCommandSource(commandRequest);

			return this.toApiJsonSerializer.serialize(result);
    	}
    }

    @DELETE
    @Path("{familyDetailsId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("familyDetailsId") final Long familyDetailsId, @PathParam("clientId") final Long clientId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteFamilyDetails(familyDetailsId, clientId) //
                .build(); //

        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @PathParam("clientId") final Long clientId) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(FamilyDetailsApiConstants.FAMILY_DETAIL_RESOURCE_NAME);

        final Collection<FamilyDetailData> familyDetailsData = this.familyDetailsReadPlatformService.retrieveAllFamilyDetails(clientId);

        ApiRequestJsonSerializationSettings settings = null;
        if (uriInfo != null) {
            settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.toApiJsonSerializer.serialize(settings, familyDetailsData);
        }
        return this.toApiJsonSerializer.serialize(familyDetailsData);
    }

    @GET
    @Path("{familyDetailsId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("clientId") final Long clientId, @PathParam("familyDetailsId") final Long familyDetailsId,
            @Context final UriInfo uriInfo) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(FamilyDetailsApiConstants.FAMILY_DETAIL_RESOURCE_NAME);

        final FamilyDetailData familyDetailsData = this.familyDetailsReadPlatformService.retrieveOneFamilyDetail(familyDetailsId);

        ApiRequestJsonSerializationSettings settings = null;
        if (uriInfo != null) {
            settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.toApiJsonSerializer.serialize(settings, familyDetailsData);
        }
        return this.toApiJsonSerializer.serialize(familyDetailsData);
    }
}