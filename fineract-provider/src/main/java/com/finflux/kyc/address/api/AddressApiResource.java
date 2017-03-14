package com.finflux.kyc.address.api;

import java.util.Collection;

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
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESSES_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
       
           final Collection<AddressData> addressDatas = this.addressReadPlatformService.retrieveAddressesByEntityTypeAndEntityId(entityType,
                entityId,settings.isTemplate());
        
        return this.toApiJsonSerializer.serialize(settings, addressDatas, AddressApiConstants.ADDRESS_RESPONSE_DATA_PARAMETERS);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("addressId") final Long addressId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESSES_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        final AddressData addressData = this.addressReadPlatformService.retrieveOne(entityType, entityId, addressId,settings.isTemplate());

        return this.toApiJsonSerializer.serialize(settings, addressData, AddressApiConstants.ADDRESS_RESPONSE_DATA_PARAMETERS);
    }

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
