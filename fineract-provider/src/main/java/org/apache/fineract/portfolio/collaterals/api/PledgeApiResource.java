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

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.data.CollateralDetailsData;
import org.apache.fineract.portfolio.collaterals.data.PledgeData;
import org.apache.fineract.portfolio.collaterals.service.PledgeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Component;

@Path("/pledges")
@Component
@Scope("singleton")
public class PledgeApiResource {
    
    private final DefaultToApiJsonSerializer<PledgeData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<PledgeData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<CollateralDetailsData> apiJsonSerializerServiceForCollateralDetail;
    private final ToApiJsonSerializer<CollateralDetailsData> toApiJsonSerializers;
    private final PledgeReadPlatformService pledgeReadPlatformService;
    
    @Autowired
    public PledgeApiResource(final DefaultToApiJsonSerializer<PledgeData> apiJsonSerializerService, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final PlatformSecurityContext context,
            final PledgeReadPlatformService pledgeReadPlatformService, final ToApiJsonSerializer<PledgeData> toApiJsonSerializer,
            final ToApiJsonSerializer<CollateralDetailsData> toApiJsonSerializers, final DefaultToApiJsonSerializer<CollateralDetailsData> apiJsonSerializerServiceForCollateralDetail) {
        this.context = context;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.pledgeReadPlatformService = pledgeReadPlatformService;
        this.toApiJsonSerializers = toApiJsonSerializers;
        this.apiJsonSerializerServiceForCollateralDetail = apiJsonSerializerServiceForCollateralDetail;
    }
    
    
    @GET
    @Path("/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveTemplate(@Context final UriInfo uriInfo, @QueryParam("collateralId") final Long collateralId){
        
        this.context.authenticatedUser().validateHasReadPermission(PledgeApiConstants.COLLATERAL_PLEDGE_RESOURCE_NAME);
        
        PledgeData pledgeData = this.pledgeReadPlatformService.retrieveTemplate(collateralId);
      
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, pledgeData, PledgeApiConstants.PLEDGE_RESPONSE_DATA_PARAMETERS);
        
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createPledge(final String apiRequestBodyAsJson){
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createPledge().withJson(apiRequestBodyAsJson).build();
        
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
        
    }
    
    @PUT
    @Path("{pledgeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updatePledge(@PathParam("pledgeId") final Long pledgeId, final String apiRequestBodyAsJson){
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePledges(pledgeId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.apiJsonSerializerService.serialize(result);
        
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("offset") final Integer offset, 
            @QueryParam("limit") final Integer limit, @QueryParam("OrderBy") final String OrderBy){
        
        this.context.authenticatedUser().validateHasReadPermission(PledgeApiConstants.COLLATERAL_PLEDGE_RESOURCE_NAME);
        
        final SearchParameters searchParameters = SearchParameters.forPledges(offset, limit, OrderBy);
        
        Page<PledgeData> pledgeData = this.pledgeReadPlatformService.retrieveAllPledges(searchParameters);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters()); 
          
        return this.toApiJsonSerializer.serialize(settings, pledgeData, PledgeApiConstants.PLEDGE_RESPONSE_DATA_PARAMETERS);

    }
    
    @GET
    @Path("{pledgeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@Context final UriInfo uriInfo, @PathParam("pledgeId") final Long pledgeId, 
            @QueryParam("association") final String association) {
        
        this.context.authenticatedUser().validateHasReadPermission(PledgeApiConstants.COLLATERAL_PLEDGE_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());  
        
        final PledgeData pledgeData = this.pledgeReadPlatformService.retriveOne(pledgeId);
        if(association != null && association.contains("collateralDetails")){
            Collection<CollateralDetailsData>  collateralDetailsData = this.pledgeReadPlatformService.retrieveCollateralDetailsByPledgeId(pledgeId);
            pledgeData.updateCollateralDetails(collateralDetailsData);
        }
        
        return this.toApiJsonSerializer.serialize(settings, pledgeData, PledgeApiConstants.PLEDGE_RESPONSE_DATA_PARAMETERS);
        
    }
    
    @POST
    @Path("{pledgeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String closePledge(@PathParam("pledgeId") final Long pledgeId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        
        CommandProcessingResult result = null;
        if (is(commandParam, "close")) {
            final CommandWrapper commandRequest = builder.closePledge(pledgeId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else{
            throw new UnrecognizedQueryParamException("command", commandParam, new Object[] {  "close" });
        }
        return this.toApiJsonSerializer.serialize(result);
        
    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
    
    @DELETE
    @Path("{pledgeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("pledgeId") final Long pledgeId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deletePledge(pledgeId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
        
    }   
    
    
    @PUT
    @Path("{pledgeId}/collateraldetails/{collateraldetailId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateCollateralDetails(@PathParam("pledgeId") final Long pledgeId, @PathParam("pledgeId") final Long collateraldetailId,
            final String apiRequestBodyAsJson){
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateCollateralDetails(pledgeId, collateraldetailId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.apiJsonSerializerServiceForCollateralDetail.serialize(result);
        
    }
    
    
    
    @DELETE
    @Path("{pledgeId}/collateraldetails/{collateraldetailId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteCollateralDetails(@PathParam("pledgeId") final Long pledgeId, @PathParam("collateraldetailId") final Long collateraldetailId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteCollateralDetails(pledgeId, collateraldetailId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializers.serialize(result);
        
    }

}
