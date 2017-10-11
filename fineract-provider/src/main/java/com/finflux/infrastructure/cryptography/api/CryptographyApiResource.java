package com.finflux.infrastructure.cryptography.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.cryptography.data.CryptographyData;
import com.finflux.infrastructure.cryptography.data.CryptographyEntityType;
import com.finflux.infrastructure.cryptography.service.CryptographyReadPlatformService;
import com.finflux.infrastructure.cryptography.service.CryptographyWritePlatformService;

@Path("/cryptography/{entityType}")
@Component
@Scope("singleton")
public class CryptographyApiResource {

    private final DefaultToApiJsonSerializer<CryptographyData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CryptographyReadPlatformService cryptographyReadPlatformService;
    private final CryptographyWritePlatformService cryptographyWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public CryptographyApiResource(final DefaultToApiJsonSerializer<CryptographyData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final CryptographyReadPlatformService cryptographyReadPlatformService,
            final CryptographyWritePlatformService cryptographyWritePlatformService,
            final ConfigurationDomainService configurationDomainService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.cryptographyReadPlatformService = cryptographyReadPlatformService;
        this.cryptographyWritePlatformService = cryptographyWritePlatformService;
        this.configurationDomainService = configurationDomainService;
    }

    @GET
    @Path("publickey")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getPublicKey(@PathParam("entityType") final String entityType, @QueryParam("username") final String username,
            @Context final UriInfo uriInfo) {
        if (this.configurationDomainService.isEnabledEncryptLoginPasswordForAuthentication()) {
            boolean isUpdate = false;
            final CryptographyEntityType cryptographyEntityType = CryptographyEntityType.fromString(entityType);
            if (cryptographyEntityType.isLogin()) {
                isUpdate = this.configurationDomainService.isEnabledEveryUserLoginGenerateNewCryptographicKeyPair();
                if (username == null || username.trim().length() == 0) { throw new InvalidJsonException(); }
            }
            this.cryptographyWritePlatformService.generateKeyPairAndStoreIntoDataBase(cryptographyEntityType, username, isUpdate);
            final CryptographyData encryptData = this.cryptographyReadPlatformService.getPublicKey(entityType, username);
            final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.toApiJsonSerializer.serialize(settings, encryptData);
        }
        return "{}";
    }
}
