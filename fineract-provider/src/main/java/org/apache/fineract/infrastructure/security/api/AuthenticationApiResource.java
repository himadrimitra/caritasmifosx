/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.security.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.data.AuthenticatedUserData;
import org.apache.fineract.infrastructure.security.service.SpringSecurityPlatformSecurityContext;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.Role;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;
import com.finflux.infrastructure.cryptography.data.CryptographyData;
import com.finflux.infrastructure.cryptography.service.CryptographyReadPlatformService;
import com.finflux.infrastructure.cryptography.service.CryptographyWritePlatformService;
import com.google.gson.JsonElement;
import com.sun.jersey.core.util.Base64;

@Path("/authentication")
@Component
@Profile("basicauth")
@Scope("singleton")
public class AuthenticationApiResource {

    private final DaoAuthenticationProvider customAuthenticationProvider;
    private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService;
    private final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext;
    private final AppUserWritePlatformService appUserWritePlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final DefaultToApiJsonSerializer<CryptographyData> toApiJsonSerializer;
    private final CryptographyReadPlatformService cryptographyReadPlatformService;
    private final CryptographyWritePlatformService cryptographyWritePlatformService;

    @Autowired
    public AuthenticationApiResource(
            @Qualifier("customAuthenticationProvider") final DaoAuthenticationProvider customAuthenticationProvider,
            final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService,
            final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext,
            final AppUserWritePlatformService appUserWritePlatformService, final FromJsonHelper fromApiJsonHelper,
            final DefaultToApiJsonSerializer<CryptographyData> toApiJsonSerializer,
            final CryptographyReadPlatformService cryptographyReadPlatformService,
            final CryptographyWritePlatformService cryptographyWritePlatformService) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.springSecurityPlatformSecurityContext = springSecurityPlatformSecurityContext;
        this.appUserWritePlatformService = appUserWritePlatformService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.cryptographyReadPlatformService = cryptographyReadPlatformService;
        this.cryptographyWritePlatformService = cryptographyWritePlatformService;
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public String authenticate(String apiRequestBodyAsJson,
            @DefaultValue("false") @QueryParam("isPasswordEncrypted") final boolean isPasswordEncrypted) {
        if (isPasswordEncrypted && (apiRequestBodyAsJson == null || apiRequestBodyAsJson.equalsIgnoreCase("{}"))) {
            this.cryptographyWritePlatformService.generateKeyPairAndStoreIntoDataBase(CryptographyApiConstants.keyTypeLogin);
            final CryptographyData publicKeyData = this.cryptographyReadPlatformService.getPublicKey(CryptographyApiConstants.keyTypeLogin);
            return this.toApiJsonSerializer.serialize(publicKeyData);
        }
        final JsonElement element = this.fromApiJsonHelper.parse(apiRequestBodyAsJson);
        final String username = this.fromApiJsonHelper.extractStringNamed("username", element);
        final String password;
        if (isPasswordEncrypted) {
            final boolean isBase64Encoded = true;
            password = this.cryptographyWritePlatformService.decryptEncryptedTextUsingRSAPrivateKey(
                    this.fromApiJsonHelper.extractStringNamed("password", element), CryptographyApiConstants.keyTypeLogin, isBase64Encoded);
        } else {
            password = this.fromApiJsonHelper.extractStringNamed("password", element);
        }
        return authenticate(username, password);
    }
    
    public String authenticate(String username, String password) {
        AuthenticatedUserData authenticatedUserData = null; 
        try {
            final Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            final Authentication authenticationCheck = this.customAuthenticationProvider.authenticate(authentication);

            final Collection<String> permissions = new ArrayList<>();
            authenticatedUserData = new AuthenticatedUserData(username, permissions);

            if (authenticationCheck.isAuthenticated()) {
                final Collection<GrantedAuthority> authorities = new ArrayList<>(authenticationCheck.getAuthorities());
                for (final GrantedAuthority grantedAuthority : authorities) {
                    permissions.add(grantedAuthority.getAuthority());
                }

                final byte[] base64EncodedAuthenticationKey = Base64.encode(username + ":" + password);

                final AppUser principal = (AppUser) authenticationCheck.getPrincipal();
                this.appUserWritePlatformService.updateSuccessLoginStatus(principal);
                final Collection<RoleData> roles = new ArrayList<>();
                final Set<Role> userRoles = principal.getRoles();
                for (final Role role : userRoles) {
                    roles.add(role.toData());
                }

                final Long officeId = principal.getOffice().getId();
                final String officeName = principal.getOffice().getName();

                final Long staffId = principal.getStaffId();
                final String staffDisplayName = principal.getStaffDisplayName();

                final EnumOptionData organisationalRole = principal.organisationalRoleData();

                if (this.springSecurityPlatformSecurityContext.doesPasswordHasToBeRenewed(principal)) {
                    authenticatedUserData = new AuthenticatedUserData(username, principal.getId(), new String(
                            base64EncodedAuthenticationKey));
                } else {
                    authenticatedUserData = new AuthenticatedUserData(username, officeId, officeName, staffId, staffDisplayName,
                            organisationalRole, roles, permissions, principal.getId(), new String(base64EncodedAuthenticationKey));
                    this.appUserWritePlatformService.updatePasswordWithNewSalt(principal, password);
                }

            }
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                this.appUserWritePlatformService.updateFailedLoginStatus(username);
            }
            throw e;
        }
        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}