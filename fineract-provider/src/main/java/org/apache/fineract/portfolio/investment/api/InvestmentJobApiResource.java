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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.investment.data.LoanInvestmentData;
import org.apache.fineract.scheduledjobs.service.ScheduledJobRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/investmentBatchJob/")
@Component
@Scope("singleton")
public class InvestmentJobApiResource  {
	
	    private final PlatformSecurityContext context;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	    private final DefaultToApiJsonSerializer<LoanInvestmentData> apiJsonSerializerService;

	
	    @Autowired
	    public InvestmentJobApiResource(
				PlatformSecurityContext context,
				ApiRequestParameterHelper apiRequestParameterHelper,
				PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
				DefaultToApiJsonSerializer<LoanInvestmentData> apiJsonSerializerService) {
			super();
			this.context = context;
			this.apiRequestParameterHelper = apiRequestParameterHelper;
			this.commandSourceWritePlatformService = commandSourceWritePlatformService;
			this.apiJsonSerializerService = apiJsonSerializerService;
		
		}
		
	    
	    @POST
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String runInvestmentJob(final String apiStringBodyAsJson){
	   
	    	this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().investmentBatchJob().withJson(apiStringBodyAsJson).build();
	    	final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
	    	
	    	
	    	return this.apiJsonSerializerService.serialize(result);
	    	
	    }
  
	    
	    

}
