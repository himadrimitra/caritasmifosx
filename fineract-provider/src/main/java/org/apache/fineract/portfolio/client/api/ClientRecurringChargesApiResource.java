package org.apache.fineract.portfolio.client.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.apache.fineract.portfolio.client.service.ClientRecurringChargeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;

@Path("/clients/{clientId}/recurringcharges")
@Component
public class ClientRecurringChargesApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<ClientRecurringChargeData> toApiJsonSerializer;
    private final ClientRecurringChargeReadPlatformService clientRecurringChargeReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public ClientRecurringChargesApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer<ClientRecurringChargeData> toApiJsonSerializer,
            final ClientRecurringChargeReadPlatformService clientRecurringChargeReadPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.clientRecurringChargeReadPlatformService = clientRecurringChargeReadPlatformService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveAllClientRecurringCharges(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final List<ClientRecurringChargeData> clientRecurringCharges = this.clientRecurringChargeReadPlatformService
                .retrieveRecurringClientCharges(clientId);
        return this.toApiJsonSerializer.serialize(settings, clientRecurringCharges,
                ClientApiConstants.CLIENT_RECURRING_CHARGES_RESPONSE_DATA_PARAMETERS);

    }

    @GET
    @Path("{recurringChargeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveRecurringClientCharge(@PathParam("clientId") final Long clientId,
            @PathParam("recurringChargeId") final Long recurringChargeId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_CHARGES_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final ClientRecurringChargeData clientRecurringCharges = this.clientRecurringChargeReadPlatformService
                .retriveRecurringClientCharge(clientId, recurringChargeId);
        return this.toApiJsonSerializer.serialize(settings, clientRecurringCharges,
                ClientApiConstants.CLIENT_RECURRING_CHARGES_RESPONSE_DATA_PARAMETERS);

    }

    @POST
    @Path("{recurringChargeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String inactivateRecurringCharge(@PathParam("clientId") final Long clientId,
            @PathParam("recurringChargeId") final Long recurringChargeId) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().inactivateClientRecurringCharge(clientId, recurringChargeId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}
