package com.finflux.infrastructure.gis.district.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.gis.district.data.DistrictData;
import com.finflux.infrastructure.gis.district.service.DistrictReadPlatformService;

@Path("/districts")
@Component
@Scope("singleton")
public class DistrictApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<Object> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DistrictReadPlatformService districtReadPlatformService;

    @Autowired
    public DistrictApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer<Object> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final DistrictReadPlatformService districtReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.districtReadPlatformService = districtReadPlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllDistrictDataByStateId(@QueryParam("stateId") final Long stateId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(DistrictApiConstants.DISTRICT_RESOURCE_NAME);

        final Collection<DistrictData> districtData = this.districtReadPlatformService.retrieveAllDistrictDataByStateId(stateId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, districtData);
    }

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

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDistrict(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createDistrict().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{districtId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDistrict(@PathParam("districtId") final Long districtId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateDistrict(districtId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{districtId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String stateTransitions(@PathParam("districtId") final Long districtId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        if (is(commandParam, DistrictApiConstants.ACTIVATE_COMMAND_NAME)) {
            final CommandWrapper commandRequest = builder.activateDistrict(districtId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, DistrictApiConstants.REJECT_COMMAND_NAME)) {
            final CommandWrapper commandRequest = builder.rejectDistrict(districtId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, DistrictApiConstants.INITIATE_WORKFLOW_COMMAND)) {
            final CommandWrapper commandRequest = builder.intiateDistirctWorkflow(districtId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { DistrictApiConstants.ACTIVATE_COMMAND_NAME,
                    DistrictApiConstants.REJECT_COMMAND_NAME, DistrictApiConstants.INITIATE_WORKFLOW_COMMAND });
        }
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    @GET
    @Path("{districtId}/villages")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveVillages(@PathParam("districtId") final Long districtId, @Context final UriInfo uriInfo,
            @DefaultValue("100") @QueryParam("status") final Integer status) {

        this.context.authenticatedUser().validateHasReadPermission(DistrictApiConstants.DISTRICT_RESOURCE_NAME);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        final Collection<VillageData> villages = this.districtReadPlatformService.retrieveVillages(districtId, status);

        return this.toApiJsonSerializer.serialize(settings, villages);
    }
}
