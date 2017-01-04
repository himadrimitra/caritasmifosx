package com.finflux.risk.profilerating.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.risk.profilerating.data.ProfileRatingScoreData;
import com.finflux.risk.profilerating.service.ProfileRatingReadPlatformService;

@Path("/profileratings")
@Component
@Scope("singleton")
public class ProfileRatingApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ProfileRatingReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public ProfileRatingApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper, final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ProfileRatingReadPlatformService readPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readPlatformService = readPlatformService;
    }
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("{entityType}/{entityId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveProfileRatingScoreByEntityTypeAndEntityId(@PathParam("entityType") final Integer entityType,
            @PathParam("entityId") final Long entityId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ComputeProfileRatingApiConstants.COMPUTE_PROFILE_RATING_RESOURCE_NAME);
        final ProfileRatingScoreData profileRatingScoreData = this.readPlatformService.retrieveProfileRatingScoreByEntityTypeAndEntityId(
                entityType, entityId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, profileRatingScoreData);
    }
}
