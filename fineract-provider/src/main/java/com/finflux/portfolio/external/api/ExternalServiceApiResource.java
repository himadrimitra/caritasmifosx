
package com.finflux.portfolio.external.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.portfolio.external.data.ExternalServicePropertyData;
import com.finflux.portfolio.external.data.ExternalServicesData;
import com.finflux.portfolio.external.service.ExternalServicesReadService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.bank.data.BankAccountDetailData;

import java.util.Collection;
import java.util.List;

@Path("/otherexternalservices")
@Component
@Scope("singleton")
public class ExternalServiceApiResource {

    private final PlatformSecurityContext context;
    private final ExternalServicesReadService externalServicesReadService;
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public ExternalServiceApiResource(final PlatformSecurityContext context, final ExternalServicesReadService externalServicesReadService,
									  final DefaultToApiJsonSerializer<BankAccountDetailData> toApiJsonSerializer,
									  final ApiRequestParameterHelper apiRequestParameterHelper,
									  final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.externalServicesReadService = externalServicesReadService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    /**
     * @param uriInfo
     * @return
     */
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllExternalServices(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ExternalServiceConstants.resourceNameForPermission);
        Collection<ExternalServicesData> externalServicesDatas = this.externalServicesReadService.findAllExternalServices();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, externalServicesDatas);
    }

    @GET
    @Path("{serviceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveServiceInfo(@PathParam("serviceId") final Long serviceId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ExternalServiceConstants.resourceNameForPermission);
        ExternalServicesData externalServicesData = this.externalServicesReadService.findOneWithNotFoundException(serviceId);
        List<ExternalServicePropertyData> externalServicesPropertyDatas = this.externalServicesReadService.
                findMaskedPropertiesForExternalServices(serviceId);
        externalServicesData.setProperties(externalServicesPropertyDatas);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, externalServicesData);
    }

    @PUT
    @Path("{serviceId}/properties")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateServiceProperties(@PathParam("serviceId") final Long serviceId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandWrapper commandRequest = builder.updateOtherExternalPropertiesForAService(serviceId).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

}