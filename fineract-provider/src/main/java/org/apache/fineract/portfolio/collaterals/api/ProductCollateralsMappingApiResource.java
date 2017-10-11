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
package org.apache.fineract.portfolio.collaterals.api;

import java.util.List;

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
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.data.ProductCollateralsMappingData;
import org.apache.fineract.portfolio.collaterals.service.ProductCollateralsMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("loanproducts/{loanProductId}/collaterals")
@Component
@Scope("singleton")
public class ProductCollateralsMappingApiResource {
    
    private final DefaultToApiJsonSerializer<ProductCollateralsMappingData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<ProductCollateralsMappingData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlatformSecurityContext context;
    private final ProductCollateralsMappingReadPlatformService productCollateralsMappingReadPlatformService;
    
    @Autowired
    private ProductCollateralsMappingApiResource(final DefaultToApiJsonSerializer<ProductCollateralsMappingData> apiJsonSerializerService,
            final ApiRequestParameterHelper apiRequestParameterHelper, final ToApiJsonSerializer<ProductCollateralsMappingData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final PlatformSecurityContext context,
            final ProductCollateralsMappingReadPlatformService productCollateralsMappingReadPlatformService) {
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.context = context;
        this.productCollateralsMappingReadPlatformService = productCollateralsMappingReadPlatformService;
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @PathParam("loanProductId") final Long loanProductId){
        
        this.context.authenticatedUser().validateHasReadPermission(CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME);
        
        List<ProductCollateralsMappingData> productCollateralsMappingData = this.productCollateralsMappingReadPlatformService.retrieveAll(loanProductId);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());    
        return this.toApiJsonSerializer.serialize(settings, productCollateralsMappingData, CollateralsApiConstants.PRODUCT_COLLATERAL_RESPONSE_DATA_PARAMETERS);
        
    }
    
    @GET
    @Path("{productCollateralMappingId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@Context final UriInfo uriInfo, @PathParam("productCollateralMappingId") final Long productCollateralMappingId){
        
        this.context.authenticatedUser().validateHasReadPermission(CollateralsApiConstants.PRODUCT_COLLATERALS_MAPPING_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        ProductCollateralsMappingData productCollateralsMappingData = this.productCollateralsMappingReadPlatformService.retrieveOne(productCollateralMappingId);
                
        return this.toApiJsonSerializer.serialize(settings, productCollateralsMappingData, CollateralsApiConstants.PRODUCT_COLLATERAL_RESPONSE_DATA_PARAMETERS);
        
    }
   
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createProductCollateralMapping(final String apiRequestBodyAsJson, @PathParam("loanProductId") final Long loanProductId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createProductCollateralMapping(loanProductId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }
    
    @PUT
    @Path("{productCollateralMappingId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateProductCollateralMapping(@PathParam("loanProductId") final Long loanProductId, @PathParam("productCollateralMappingId") final Long productCollateralMappingId, final String apiRequestBodyAsJson) {
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateProductCollateralMapping(loanProductId, productCollateralMappingId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.apiJsonSerializerService.serialize(result);
        
    }
    
    @DELETE
    @Path("{productCollateralMappingId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteProductCollateralMapping(@Context final UriInfo uriInfo, @PathParam("loanProductId") final Long loanProductId, @PathParam("productCollateralMappingId") final Long productCollateralMappingId){
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteProductCollateralMapping(loanProductId, productCollateralMappingId) //
                .build(); //
        
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.apiJsonSerializerService.serialize(result);
        
    }
}
