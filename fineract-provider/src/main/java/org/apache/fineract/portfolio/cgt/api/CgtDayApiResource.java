package org.apache.fineract.portfolio.cgt.api;

import javax.ws.rs.Consumes;
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
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.cgt.data.CgtDayData;
import org.apache.fineract.portfolio.cgt.service.CgtDayReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/cgt/{cgtId}/cgtDay")
@Component
@Scope("singleton")
public class CgtDayApiResource {

    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<CgtDayData> cgtDayApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CgtDayReadPlatformService cgtDayReadPlatformService;

    @Autowired
    public CgtDayApiResource(final PlatformSecurityContext context, final ToApiJsonSerializer<CgtDayData> cgtDayApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CgtDayReadPlatformService cgtDayReadPlatformService) {
        this.context = context;
        this.cgtDayApiJsonSerializer = cgtDayApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.cgtDayReadPlatformService = cgtDayReadPlatformService;
    }

    @GET
    @Path("{cgtDayId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveCgtDay(@PathParam("cgtId") final Long cgtId, @PathParam("cgtDayId") final Long cgtDayId,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(CgtDayApiConstants.CGT_DAY_RESOURCE_NAME);
        final CgtDayData cgtDayData = this.cgtDayReadPlatformService.retrievetCgtDayDataById(cgtDayId);
        return cgtDayApiJsonSerializer.serialize(cgtDayData);

    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createCgtDay(@PathParam("cgtId") final Long cgtId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createCgtDay(cgtId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return cgtDayApiJsonSerializer.serialize(result);

    }

    @PUT
    @Path("{cgtDayId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateCgtDay(@PathParam("cgtId") final Long cgtId, @PathParam("cgtDayId") final Long cgtDayId,
            @QueryParam("action") final String action, final String apiRequestBodyAsJson) {

        CommandWrapper commandRequest = null;
        if (is(action, CgtApiConstants.cgtStatusCompleteParamName)) {
            commandRequest = new CommandWrapperBuilder().completeCgtDay(cgtId, cgtDayId).withJson(apiRequestBodyAsJson).build();
        } else {
            commandRequest = new CommandWrapperBuilder().updateCgtDay(cgtId, cgtDayId).withJson(apiRequestBodyAsJson).build();
        }
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return cgtDayApiJsonSerializer.serialize(result);

    }

    private boolean is(final String action, final String commandValue) {
        return StringUtils.isNotBlank(action) && action.trim().equalsIgnoreCase(commandValue);
    }

}
