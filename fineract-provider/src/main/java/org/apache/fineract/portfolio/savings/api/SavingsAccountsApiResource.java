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
package org.apache.fineract.portfolio.savings.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.apache.fineract.portfolio.loanaccount.guarantor.service.GuarantorReadPlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.SavingsAccountChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDpDetailsData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.finflux.common.util.FinfluxStringUtils;

@Path("/savingsaccounts")
@Component
@Scope("singleton")
public class SavingsAccountsApiResource {

    private final GuarantorReadPlatformService guarantorReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;

    @Autowired
    public SavingsAccountsApiResource(final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final PlatformSecurityContext context, final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService, final GuarantorReadPlatformService guarantorReadPlatformService) {
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
        this.guarantorReadPlatformService = guarantorReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@QueryParam("clientId") final Long clientId, @QueryParam("groupId") final Long groupId,
            @QueryParam("productId") final Long productId,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);
        final SavingsAccountDpDetailsData savingsAccountDpDetailsData = null;
        final SavingsAccountData savingsAccount = this.savingsAccountReadPlatformService.retrieveTemplate(clientId, groupId, productId,
                staffInSelectedOfficeOnly, savingsAccountDpDetailsData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, savingsAccount, SavingsApiConstants.SAVINGS_ACCOUNT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo,
            @QueryParam("searchConditions") final String searchConditions,
            @QueryParam("externalId") final String externalId, @QueryParam("officeId") final Long officeId,
            // @QueryParam("underHierarchy") final String hierarchy,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder) {
        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);
        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);
        final SearchParameters searchParameters = SearchParameters.forSavings(searchConditionsMap, externalId, offset, limit, orderBy,
                sortOrder, officeId);
        final Page<SavingsAccountData> products = this.savingsAccountReadPlatformService.retrieveAll(searchParameters);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, products, SavingsApiConstants.SAVINGS_ACCOUNT_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String submitApplication(@QueryParam("command") final String commandParam, final String apiRequestBodyAsJson) {

        CommandWrapper commandRequest = null;
        if (is(commandParam, "defaultValues")) {
            commandRequest = new CommandWrapperBuilder().createOrActivateSavings().withJson(apiRequestBodyAsJson).build();

        } else {
            commandRequest = new CommandWrapperBuilder().createSavingsAccount().withJson(apiRequestBodyAsJson).build();
        }
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("accountId") final Long accountId,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @DefaultValue("all") @QueryParam("chargeStatus") final String chargeStatus, @Context final UriInfo uriInfo,
            @QueryParam("transactionsCount") final Integer transactionsCount, @QueryParam("fromDate") final Date fromDate,
            @QueryParam("toDate") final Date toDate, @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
            @QueryParam("searchConditions") final String searchConditions) {
        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);
        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        if (!(is(chargeStatus, "all") || is(chargeStatus, "active") || is(chargeStatus, "inactive"))) { throw new UnrecognizedQueryParamException(
                "status", chargeStatus, new Object[] { "all", "active", "inactive" }); }

        final SavingsAccountData savingsAccount = this.savingsAccountReadPlatformService.retrieveOne(accountId);
        SavingsAccountDpDetailsData savingsAccountDpDetailsData = null;
        final SearchParameters searchParameters = SearchParameters.forTransactions(searchConditionsMap, transactionsCount, fromDate,
                toDate, offset, limit, orderBy, sortOrder);
        if (savingsAccount.isAllowOverdraft()) {
            savingsAccountDpDetailsData = this.savingsAccountReadPlatformService.retrieveSavingsDpDetailsBySavingsId(accountId);
        }

        final Set<String> mandatoryResponseParameters = new HashSet<>();
        final SavingsAccountData savingsAccountTemplate = populateTemplateAndAssociations(accountId, savingsAccount,
                staffInSelectedOfficeOnly, chargeStatus, uriInfo, mandatoryResponseParameters, savingsAccountDpDetailsData,
                searchParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters(),
                mandatoryResponseParameters);
        return this.toApiJsonSerializer.serialize(settings, savingsAccountTemplate,
                SavingsApiConstants.SAVINGS_ACCOUNT_RESPONSE_DATA_PARAMETERS);
    }

    private SavingsAccountData populateTemplateAndAssociations(final Long accountId, final SavingsAccountData savingsAccount,
            final boolean staffInSelectedOfficeOnly, final String chargeStatus, final UriInfo uriInfo,
            final Set<String> mandatoryResponseParameters, SavingsAccountDpDetailsData savingsAccountDpDetailsData, SearchParameters searchParameters) {

        Collection<SavingsAccountTransactionData> transactions = null;
        Collection<SavingsAccountChargeData> charges = null;
        Collection<GuarantorData> guarantors = null;

        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        if (!associationParameters.isEmpty()) {

            if (associationParameters.contains("all")) {
                associationParameters.addAll(Arrays.asList(SavingsApiConstants.transactions, SavingsApiConstants.charges));
            }

            if (associationParameters.contains("guarantors")) {
                mandatoryResponseParameters.add("guarantors");
                guarantors = this.guarantorReadPlatformService.retrieveGuarantorsForSavings(accountId);
                if (CollectionUtils.isEmpty(guarantors)) {
                    guarantors = null;
                }
            }
            
            if (associationParameters.contains(SavingsApiConstants.transactions)) {
                mandatoryResponseParameters.add(SavingsApiConstants.transactions);
                final Collection<SavingsAccountTransactionData> currentTransactions = this.savingsAccountReadPlatformService
                        .retrieveAllTransactions(accountId, DepositAccountType.SAVINGS_DEPOSIT, searchParameters);
                if (!CollectionUtils.isEmpty(currentTransactions)) {
                    transactions = currentTransactions;
                }
            }

            if (associationParameters.contains(SavingsApiConstants.charges)) {
                mandatoryResponseParameters.add(SavingsApiConstants.charges);
                final Collection<SavingsAccountChargeData> currentCharges = this.savingsAccountChargeReadPlatformService
                        .retrieveSavingsAccountCharges(accountId, chargeStatus);
                if (!CollectionUtils.isEmpty(currentCharges)) {
                    charges = currentCharges;
                }
            }
        }

        SavingsAccountData templateData = null;
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (settings.isTemplate()) {
            templateData = this.savingsAccountReadPlatformService.retrieveTemplate(savingsAccount.clientId(), savingsAccount.groupId(),
                    savingsAccount.productId(), staffInSelectedOfficeOnly, savingsAccountDpDetailsData);
        }

        return SavingsAccountData.withTemplateOptions(savingsAccount, templateData, transactions, charges, savingsAccountDpDetailsData, guarantors);
    }

    @PUT
    @Path("{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("accountId") final Long accountId, final String apiRequestBodyAsJson,
            @QueryParam("command") final String commandParam) {

        if (is(commandParam, "updateWithHoldTax")) {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson).updateWithHoldTax(accountId)
                    .build();
            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSavingsAccount(accountId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String handleCommands(@PathParam("accountId") final Long accountId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);

        CommandProcessingResult result = null;
        if (is(commandParam, "reject")) {
            final CommandWrapper commandRequest = builder.rejectSavingsAccountApplication(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "withdrawnByApplicant")) {
            final CommandWrapper commandRequest = builder.withdrawSavingsAccountApplication(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "approve")) {
            final CommandWrapper commandRequest = builder.approveSavingsAccountApplication(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undoapproval")) {
            final CommandWrapper commandRequest = builder.undoSavingsAccountApplication(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "activate")) {
            final CommandWrapper commandRequest = builder.savingsAccountActivation(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "calculateInterest")) {
            final CommandWrapper commandRequest = builder.withNoJsonBody().savingsAccountInterestCalculation(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "postInterest")) {
            final CommandWrapper commandRequest = builder.savingsAccountInterestPosting(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "applyAnnualFees")) {
            final CommandWrapper commandRequest = builder.savingsAccountApplyAnnualFees(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "close")) {
            final CommandWrapper commandRequest = builder.closeSavingsAccountApplication(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "assignSavingsOfficer")) {
            final CommandWrapper commandRequest = builder.assignSavingsOfficer(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "unassignSavingsOfficer")) {
            final CommandWrapper commandRequest = builder.unassignSavingsOfficer(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, SavingsApiConstants.COMMAND_BLOCK_DEBIT)) {
            final CommandWrapper commandRequest = builder.blockDebitsFromSavingsAccount(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, SavingsApiConstants.COMMAND_UNBLOCK_DEBIT)) {
            final CommandWrapper commandRequest = builder.unblockDebitsFromSavingsAccount(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, SavingsApiConstants.COMMAND_BLOCK_CREDIT)) {
            final CommandWrapper commandRequest = builder.blockCreditsToSavingsAccount(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, SavingsApiConstants.COMMAND_UNBLOCK_CREDIT)) {
            final CommandWrapper commandRequest = builder.unblockCreditsToSavingsAccount(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, SavingsApiConstants.COMMAND_BLOCK_ACCOUNT)) {
            final CommandWrapper commandRequest = builder.withNoJsonBody().blockSavingsAccount(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, SavingsApiConstants.COMMAND_UNBLOCK_ACCOUNT)) {
            final CommandWrapper commandRequest = builder.withNoJsonBody().unblockSavingsAccount(accountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) {
            //
            throw new UnrecognizedQueryParamException("command", commandParam,
                    new Object[] { "reject", "withdrawnByApplicant", "approve", "undoapproval", "activate", "calculateInterest",
                            "postInterest", "close", "assignSavingsOfficer", "unassignSavingsOfficer",
                            SavingsApiConstants.COMMAND_BLOCK_DEBIT, SavingsApiConstants.COMMAND_UNBLOCK_DEBIT,
                            SavingsApiConstants.COMMAND_BLOCK_CREDIT, SavingsApiConstants.COMMAND_UNBLOCK_CREDIT,
                            SavingsApiConstants.COMMAND_BLOCK_ACCOUNT, SavingsApiConstants.COMMAND_UNBLOCK_ACCOUNT });
        }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    @DELETE
    @Path("{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("accountId") final Long accountId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSavingsAccount(accountId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}