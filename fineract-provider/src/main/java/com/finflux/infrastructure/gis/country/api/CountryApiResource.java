package com.finflux.infrastructure.gis.country.api;

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

import com.finflux.infrastructure.gis.country.data.CountryData;
import com.finflux.infrastructure.gis.country.service.CountryReadPlatformService;

@Path("/countries")
@Component
@Scope("singleton")
public class CountryApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    @SuppressWarnings("unused")
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CountryReadPlatformService countryReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public CountryApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CountryReadPlatformService countryReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.countryReadPlatformService = countryReadPlatformService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(CountryApiConstants.COUNTRY_RESOURCE_NAME);

        final Collection<CountryData> countryData = this.countryReadPlatformService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, countryData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{countryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("countryId") final Long countryId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(CountryApiConstants.COUNTRY_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        final CountryData countryData = this.countryReadPlatformService.retrieveOne(countryId,settings.isTemplate());

        return this.toApiJsonSerializer.serialize(settings, countryData);
    }
}
