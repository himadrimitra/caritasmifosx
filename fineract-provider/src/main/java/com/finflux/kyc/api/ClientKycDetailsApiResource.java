/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package com.finflux.kyc.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.service.ClientKycReadPlatformService;

@Path("clientkyc")
@Component
@Scope("singleton")
public class ClientKycDetailsApiResource {

    private final PlatformSecurityContext context;
    private final ClientKycReadPlatformService clientKycReadPlatformService;

    @Autowired
    public ClientKycDetailsApiResource(final PlatformSecurityContext context,
            final ClientKycReadPlatformService clientKycReadPlatformService) {
        this.context = context;
        this.clientKycReadPlatformService = clientKycReadPlatformService;
    }

    @GET
    public String retrieveOne(@QueryParam("clientId") final Long clientId) {

        this.context.authenticatedUser().validateHasReadPermission(AddressApiConstants.ADDRESSES_RESOURCE_NAME);
        String kycResponseObject = clientKycReadPlatformService.retrieveResponseObjectWithClientId(clientId);
        return kycResponseObject;
    }

}
