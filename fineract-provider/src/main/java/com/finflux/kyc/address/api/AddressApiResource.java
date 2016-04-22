package com.finflux.kyc.address.api;

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

import com.finflux.kyc.address.data.AddressData;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.data.AddressTemplateData;
import com.finflux.kyc.address.exception.AddressEntityTypeNotSupportedException;
import com.finflux.kyc.address.service.AddressReadPlatformService;

@Path("/{entityType}/{entityId}/addresses")
@Component
@Scope("singleton")
public class AddressApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final AddressReadPlatformService addressReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public AddressApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final AddressReadPlatformService addressReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.addressReadPlatformService = addressReadPlatformService;

    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESS_RESOURCE_NAME);

        final AddressTemplateData addressTemplateData = this.addressReadPlatformService.retrieveTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, addressTemplateData,
                AddressApiConstants.ADDRESS_TEMPLATE_RESPONSE_DATA_PARAMETERS);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createAddress(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            final String apiRequestBodyAsJson) {

        final AddressEntityTypeEnums addressEntityType = AddressEntityTypeEnums.getEntityType(entityType);
        if (addressEntityType == null) { throw new AddressEntityTypeNotSupportedException(entityType); }

        final Long entityTypeId = addressEntityType.getValue().longValue();

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createAddress(entityTypeId, entityType, entityId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("addressId") final Long addressId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESS_RESOURCE_NAME);

        final AddressData addressData = this.addressReadPlatformService.retrieveOne(entityType, entityId, addressId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, addressData, AddressApiConstants.ADDRESS_RESPONSE_DATA_PARAMETERS);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PUT
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateAddress(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("addressId") final Long addressId, final String apiRequestBodyAsJson) {

        final AddressEntityTypeEnums addressEntityType = AddressEntityTypeEnums.getEntityType(entityType);
        if (addressEntityType == null) { throw new AddressEntityTypeNotSupportedException(entityType); }

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateAddress(entityType, entityId, addressId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @DELETE
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String daleteAddress(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("addressId") final Long addressId, final String apiRequestBodyAsJson) {

        final AddressEntityTypeEnums addressEntityType = AddressEntityTypeEnums.getEntityType(entityType);
        if (addressEntityType == null) { throw new AddressEntityTypeNotSupportedException(entityType); }

        final CommandWrapper commandRequest = new CommandWrapperBuilder().daleteAddress(entityType, entityId, addressId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}
