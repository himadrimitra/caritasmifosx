package com.finflux.kyc.address.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.kyc.address.data.AddressTemplateData;
import com.finflux.kyc.address.service.AddressReadPlatformService;

@Path("/{entityType}/addresses")
@Component
@Scope("singleton")
public class AddressTemplateApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final AddressReadPlatformService addressReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public AddressTemplateApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final AddressReadPlatformService addressReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.addressReadPlatformService = addressReadPlatformService;

    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESSES_RESOURCE_NAME);

        final AddressTemplateData addressTemplateData = this.addressReadPlatformService.retrieveTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, addressTemplateData,
                AddressApiConstants.ADDRESS_TEMPLATE_RESPONSE_DATA_PARAMETERS);
    }

}
