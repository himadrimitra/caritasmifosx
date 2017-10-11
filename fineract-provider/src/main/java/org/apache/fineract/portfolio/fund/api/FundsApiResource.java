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
package org.apache.fineract.portfolio.fund.api;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
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
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.data.FundMappingSearchData;
import org.apache.fineract.portfolio.fund.data.FundSearchQueryBuilder;
import org.apache.fineract.portfolio.fund.service.FundMappingQueryBuilderService;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.multipart.FormDataMultiPart;

@Path("/funds")
@Component
@Scope("singleton")
public class FundsApiResource {

    /**
     * The set of parameters that are supported in response for {@link CodeData}
     */
    private final PlatformSecurityContext context;
    private final FundReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<Object> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FundMappingQueryBuilderService fundMappingQueryBuilderService;

    @Autowired
    public FundsApiResource(final PlatformSecurityContext context, final FundReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<Object> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final FundMappingQueryBuilderService fundMappingQueryBuilderService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.fundMappingQueryBuilderService = fundMappingQueryBuilderService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveFunds(@Context final UriInfo uriInfo, @QueryParam("command") final String command) {

        this.context.authenticatedUser().validateHasReadPermission(FundApiConstants.FUND_RESOURCE_NAME);

        final Collection<FundData> funds = this.readPlatformService.retrieveAllFunds(command);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, funds, FundApiConstants.FUND_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createFund(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createFund().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{fundId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveFund(@PathParam("fundId") final Long fundId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(FundApiConstants.FUND_RESOURCE_NAME);

        final FundData fund = this.readPlatformService.retrieveFund(fundId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, fund, FundApiConstants.FUND_RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{fundId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateFund(@PathParam("fundId") final Long fundId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateFund(fundId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @Path("/template")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveFundTemplates(@Context final UriInfo uriInfo, @QueryParam("command") final String command) {

        this.context.authenticatedUser();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, this.readPlatformService.retrieveTemplate(command));
    }

    @POST
    @Path("{fundId}/assign")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String assignFund(@PathParam("fundId") final Long fundId, final FormDataMultiPart formParams)
            throws InvalidFormatException, IOException {

        this.context.authenticatedUser();
        final CommandWrapper commandRequest = new CommandWrapperBuilder().assignFund(fundId, "fromcsv").withFormDataMultiPart(formParams)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{fundId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String assignFund(@PathParam("fundId") final Long fundId, final String apiRequestBodyAsJson,
            @QueryParam("command") final String command) {

        this.context.authenticatedUser();
        CommandWrapper commandRequest = null;
        if (command != null && command.equalsIgnoreCase("activate")) {
            commandRequest = new CommandWrapperBuilder().activateFund(fundId).withNoJsonBody().build();
        } else if (command != null && command.equalsIgnoreCase("deactivate")) {
            commandRequest = new CommandWrapperBuilder().deactivateFund(fundId).withNoJsonBody().build();
        } else if (command != null && command.equalsIgnoreCase("assign")) {
            commandRequest = new CommandWrapperBuilder().assignFund(fundId, "fromquery").withJson(apiRequestBodyAsJson).build();
        } else {
            throw new UnrecognizedQueryParamException("command", command, new Object[] { "activate", "deactivate", "assign" });
        }

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    // this is to get matching loans based on search criteria, actually its GET
    // method
    @POST
    @Path("/mapping/loans")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String fundAdvancedSearch(@Context final UriInfo uriInfo, final String json, @QueryParam("isSummary") final Boolean isSummary) {
        FundSearchQueryBuilder fundSearchQueryBuilder = null;
        if (isSummary) {
            fundSearchQueryBuilder = this.fundMappingQueryBuilderService.getSummaryQuery(json);
        } else {
            final boolean isDetailedQuery = true;
            fundSearchQueryBuilder = this.fundMappingQueryBuilderService.getQuery(json, isDetailedQuery);
        }
        final List<FundMappingSearchData> fundMappingSearchData = this.fundMappingQueryBuilderService
                .getSearchedData(fundSearchQueryBuilder);
        final Map<String, Object> responseData = new HashMap<>();
        responseData.put("fundMappingSearchData", fundMappingSearchData);
        responseData.put("fundSearchQueryBuilder", fundSearchQueryBuilder);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, responseData);

    }
}