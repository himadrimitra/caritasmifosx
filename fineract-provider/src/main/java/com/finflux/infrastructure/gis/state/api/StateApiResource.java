package com.finflux.infrastructure.gis.state.api;

import java.util.Collection;

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

import com.finflux.infrastructure.gis.state.data.StateData;
import com.finflux.infrastructure.gis.state.service.StateReadPlatformService;

@Path("/countries/{countryId}/states")
@Component
@Scope("singleton")
public class StateApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    @SuppressWarnings("unused")
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final StateReadPlatformService stateReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public StateApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final StateReadPlatformService stateReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.stateReadPlatformService = stateReadPlatformService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllStateDataByCountryId(@PathParam("countryId") final Long countryId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(StateApiConstants.STATE_RESOURCE_NAME);

        final Collection<StateData> stateData = this.stateReadPlatformService.retrieveAllStateDataByCountryId(countryId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, stateData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{stateId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("stateId") final Long stateId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(StateApiConstants.STATE_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        final StateData stateData = this.stateReadPlatformService.retrieveOne(stateId, settings.isTemplate());

        return this.toApiJsonSerializer.serialize(settings, stateData);
    }
}
