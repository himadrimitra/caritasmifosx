package com.finflux.infrastructure.gis.district.api;

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

import com.finflux.infrastructure.gis.district.data.DistrictData;
import com.finflux.infrastructure.gis.district.service.DistrictReadPlatformService;

@Path("/states/{stateId}/districts")
@Component
@Scope("singleton")
public class DistrictApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    @SuppressWarnings("unused")
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DistrictReadPlatformService districtReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public DistrictApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final DistrictReadPlatformService districtReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.districtReadPlatformService = districtReadPlatformService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllDistrictDataByStateId(@PathParam("stateId") final Long stateId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(DistrictApiConstants.DISTRICT_RESOURCE_NAME);

        final Collection<DistrictData> districtData = this.districtReadPlatformService.retrieveAllDistrictDataByStateId(stateId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, districtData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{districtId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("districtId") final Long districtId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(DistrictApiConstants.DISTRICT_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        final DistrictData districtData = this.districtReadPlatformService.retrieveOne(districtId, settings.isTemplate());

        return this.toApiJsonSerializer.serialize(settings, districtData);
    }
}
