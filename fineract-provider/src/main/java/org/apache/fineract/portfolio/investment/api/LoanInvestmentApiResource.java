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
package org.apache.fineract.portfolio.investment.api;

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
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.investment.data.LoanInvestmentData;
import org.apache.fineract.portfolio.investment.data.SavingInvestmentData;
import org.apache.fineract.portfolio.investment.service.InvestmentReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loans/{loanId}/loanInvestment")
@Component
@Scope("singleton")

public class LoanInvestmentApiResource {

    
    private final InvestmentReadPlatformService loanInvestmentReadPlatformService;
    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<LoanInvestmentData> apiJsonSerializerService;
    
    @Autowired
    public LoanInvestmentApiResource(InvestmentReadPlatformService loanInvestmentReadPlatformService,
            PlatformSecurityContext context, ApiRequestParameterHelper apiRequestParameterHelper,
            PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,  DefaultToApiJsonSerializer<LoanInvestmentData> apiJsonSerializerService) {
        super();
        this.loanInvestmentReadPlatformService = loanInvestmentReadPlatformService;
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
        this.apiJsonSerializerService = apiJsonSerializerService;
    }
    
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retriveSavingAccounts(@PathParam("loanId") Long loanId, @Context final UriInfo uriInfo ){
     
        this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);

    final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    
    List<LoanInvestmentData>  data = this.loanInvestmentReadPlatformService.retriveSavingAccountsByLoanId(loanId);
        
    return this.apiJsonSerializerService.serialize(settings, data);
   
    }
    

    @POST
    @Path("/delete")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteLoanInvestment(@PathParam("loanId") Long loanId, @QueryParam("savingId") Long savingId,  final String apiRequestBodyAsJson){
        
        this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteLoanInvestment(loanId)
        		.withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.apiJsonSerializerService.serialize(result);
    }
    
    
    @POST
    @Path("/close")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String closeLoanInvestment(@PathParam("loanId") Long loanId, final String apiRequestBodyAsJson){
		
    	this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);
    	final CommandWrapper commandRequest = new CommandWrapperBuilder().closeLoanInvestment(loanId).withJson(apiRequestBodyAsJson)
    			.build();
    	final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
    	
    	return this.apiJsonSerializerService.serialize(result);
    	
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addLoanInvestment(@PathParam("loanId") Long loanId, String apiJsonBody){
        
        this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanInvestment(loanId).
                withJson(apiJsonBody).
                build();
        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        return this.apiJsonSerializerService.serialize(result);
        
    }
    
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanInvestment(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLoanInvestment(loanId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);

    }
    
}
