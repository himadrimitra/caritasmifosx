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

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collateral.data.CollateralData;
import org.apache.fineract.portfolio.collaterals.data.CollateralsData;
import org.apache.fineract.portfolio.collaterals.data.QualityStandardsData;
import org.apache.fineract.portfolio.collaterals.service.CollateralsReadPlatformService;
import org.apache.fineract.portfolio.collaterals.service.QualityStandardsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/collaterals")
@Component
@Scope("singleton")
public class CollateralsTypeApiResource {

    private final DefaultToApiJsonSerializer<CollateralData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<CollateralsData> toApiJsonSerializer;
    private final ToApiJsonSerializer<QualityStandardsData> toApiJsonSerializerForQualityStandard;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformServiceForQualityStandards;
    private final PlatformSecurityContext context;
    private final CollateralsReadPlatformService collateralsReadPlatformService;
    private final QualityStandardsReadPlatformService qualityStandardsReadPlatformService;
    
    @Autowired
    public CollateralsTypeApiResource(final DefaultToApiJsonSerializer<CollateralData> apiJsonSerializerService, final ApiRequestParameterHelper apiRequestParameterHelper, 
        final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformServiceForQualityStandards, final PlatformSecurityContext context,
        final ToApiJsonSerializer<CollateralsData> toApiJsonSerializer, final ToApiJsonSerializer<QualityStandardsData> toApiJsonSerializerForQualityStandard,
        final CollateralsReadPlatformService collateralsReadPlatformService,
        final QualityStandardsReadPlatformService qualityStandardsReadPlatformService) {
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.toApiJsonSerializerForQualityStandard = toApiJsonSerializerForQualityStandard;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.commandsSourceWritePlatformServiceForQualityStandards = commandsSourceWritePlatformServiceForQualityStandards;
        this.context = context;
        this.collateralsReadPlatformService = collateralsReadPlatformService;
        this.qualityStandardsReadPlatformService = qualityStandardsReadPlatformService;
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createCollaterals(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCollaterals().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }
    
    @PUT
    @Path("{collateralId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateCollaterals(@PathParam("collateralId") final Long collateralId, final String apiRequestBodyAsJson) {
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateCollaterals(collateralId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.apiJsonSerializerService.serialize(result);
        
    }
   
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo){
        
        this.context.authenticatedUser().validateHasReadPermission(CollateralsApiConstants.COLLATERALS_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());  
        
        List<CollateralsData> collateralData = this.collateralsReadPlatformService.retrieveAllCollaterals();
          
        return this.toApiJsonSerializer.serialize(settings, collateralData, CollateralsApiConstants.COLLATERALS_RESPONSE_DATA_PARAMETERS);

        
    }
    
    @GET
    @Path("{collateralId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@Context final UriInfo uriInfo, @PathParam("collateralId") final Long collateralId,
            @QueryParam("associations") final String associations){
        
        this.context.authenticatedUser().validateHasReadPermission(CollateralsApiConstants.COLLATERALS_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        CollateralsData collateralData = this.collateralsReadPlatformService.retrieveOne(collateralId);
        
        if(associations != null && associations.contains("qualityStandards")){
            Collection<QualityStandardsData> qualityStandardsData = this.qualityStandardsReadPlatformService.retrieveAllCollateralQualityStandards(collateralId);
            if(!qualityStandardsData.isEmpty()){
                collateralData.updateQualityStandards(qualityStandardsData);
            }
        }

        return this.toApiJsonSerializer.serialize(settings, collateralData, CollateralsApiConstants.COLLATERALS_RESPONSE_DATA_PARAMETERS);
        
    }
    
    @POST
    @Path("{collateralId}/qualitystandards")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createQualityStandards(@PathParam("collateralId") final Long collateralId,final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createQualityStandards(collateralId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }
    
    @PUT
    @Path("{collateralId}/qualitystandards/{qualityStandardId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateCollateralQualityStandards(@PathParam("collateralId") final Long collateralId,@PathParam("qualityStandardId") final Long qualityStandardId, final String apiRequestBodyAsJson) {
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateQualityStandards(collateralId, qualityStandardId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformServiceForQualityStandards.logCommandSource(commandRequest);
        
        return this.toApiJsonSerializerForQualityStandard.serialize(result);
        
    }
    
    @GET
    @Path("{collateralId}/qualitystandards")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllCollateralQualityStandards(@Context final UriInfo uriInfo, @PathParam("collateralId") final Long collateralId){
        
        this.context.authenticatedUser().validateHasReadPermission(CollateralsApiConstants.COLLATERALS_QUALITY_STANDARDS_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        List<QualityStandardsData> qualityStandardsData = this.qualityStandardsReadPlatformService.retrieveAllCollateralQualityStandards(collateralId);
        
        return this.toApiJsonSerializerForQualityStandard.serialize(settings, qualityStandardsData, CollateralsApiConstants.QUALITY_STANDARDS_RESPONSE_DATA_PARAMETERS);
        
    }
    
    @GET
    @Path("{collateralId}/qualitystandards/{qualityStandardId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneQualityStandards(@Context final UriInfo uriInfo, @PathParam("collateralId") final Long collateralId, @PathParam("qualityStandardId") final Long qualityStandardId){
        
        this.context.authenticatedUser().validateHasReadPermission(CollateralsApiConstants.COLLATERALS_QUALITY_STANDARDS_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        QualityStandardsData qualityStandardsData = this.qualityStandardsReadPlatformService.retrieveOneCollateralQualityStandards(collateralId, qualityStandardId);
        
        return this.toApiJsonSerializerForQualityStandard.serialize(settings, qualityStandardsData, CollateralsApiConstants.QUALITY_STANDARDS_RESPONSE_DATA_PARAMETERS);
        
    }
    
    @DELETE
    @Path("{collateralId}/qualitystandards/{qualityStandardId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteOneQualityStandards(@Context final UriInfo uriInfo, @PathParam("collateralId") final Long collateralId, @PathParam("qualityStandardId") final Long qualityStandardId){
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
	        .deleteQualityStandards(collateralId, qualityStandardId) //
	        .build(); //
        
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.toApiJsonSerializerForQualityStandard.serialize(result);
        
    }
    
}
