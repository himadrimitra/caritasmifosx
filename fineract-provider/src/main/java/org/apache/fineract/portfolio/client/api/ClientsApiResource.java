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
package org.apache.fineract.portfolio.client.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.accountdetails.PaymentDetailCollectionData;
import org.apache.fineract.portfolio.accountdetails.SharesAccountBalanceCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.MpesaTransactionSummaryData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.deduplication.service.DeDuplicationService;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.common.util.FinfluxStringUtils;

@Path("/clients")
@Component
@Scope("singleton")
public class ClientsApiResource {

    private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
    private final ToApiJsonSerializer<AccountSummaryCollectionData> clientAccountSummaryToApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final DeDuplicationService deDuplicationService;

    @Autowired
    public ClientsApiResource(final PlatformSecurityContext context, final ClientReadPlatformService readPlatformService,
            final ToApiJsonSerializer<ClientData> toApiJsonSerializer,
            final ToApiJsonSerializer<AccountSummaryCollectionData> clientAccountSummaryToApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
            final ConfigurationDomainService configurationDomainService, final DeDuplicationService deDuplicationService,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final GroupReadPlatformService groupReadPlatformService) {
        this.context = context;
        this.clientReadPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.clientAccountSummaryToApiJsonSerializer = clientAccountSummaryToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.configurationDomainService = configurationDomainService;
        this.deDuplicationService = deDuplicationService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId,
            @QueryParam("commandParam") final String commandParam,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);

        ClientData clientData = null;
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        if (is(commandParam, "close")) {
            clientData = this.clientReadPlatformService.retrieveAllNarrations(ClientApiConstants.CLIENT_CLOSURE_REASON);
        } else if (is(commandParam, "acceptTransfer")) {
            clientData = this.clientReadPlatformService.retrieveAllNarrations(ClientApiConstants.CLIENT_CLOSURE_REASON);
        } else if (is(commandParam, "reject")) {
            clientData = this.clientReadPlatformService.retrieveAllNarrations(ClientApiConstants.CLIENT_REJECT_REASON);
        } else if (is(commandParam, "withdraw")) {
            clientData = this.clientReadPlatformService.retrieveAllNarrations(ClientApiConstants.CLIENT_WITHDRAW_REASON);
        } else {
            clientData = this.clientReadPlatformService.retrieveTemplate(officeId, staffInSelectedOfficeOnly);
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("searchConditions") final String searchConditions,
            @QueryParam("officeId") final Long officeId, @QueryParam("externalId") final String externalId,
            @QueryParam("displayName") final String displayName, @QueryParam("firstName") final String firstname,
            @QueryParam("lastName") final String lastname, @QueryParam("underHierarchy") final String hierarchy,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
            @QueryParam("orphansOnly") final Boolean orphansOnly, @QueryParam("groupId") final Long groupId) {
        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);
        return this.retrieveAll(uriInfo, searchConditionsMap, officeId, externalId, displayName, firstname, lastname, hierarchy, offset,
                limit, orderBy, sortOrder, orphansOnly, false, groupId);
    }

    public String retrieveAll(final UriInfo uriInfo, final Map<String, String> searchConditions, final Long officeId,
            final String externalId, final String displayName, final String firstname, final String lastname, final String hierarchy,
            final Integer offset, final Integer limit, final String orderBy, final String sortOrder, final Boolean orphansOnly,
            final boolean isSelfUser, final Long groupId) {
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final SearchParameters searchParameters = SearchParameters.forClients(searchConditions, officeId, externalId, displayName,
                firstname, lastname, hierarchy, offset, limit, orderBy, sortOrder, orphansOnly, isSelfUser, groupId);
        final Page<ClientData> clientData = this.clientReadPlatformService.retrieveAll(searchParameters);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("lookup")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllForTaskLookupBySearchParameters(@Context final UriInfo uriInfo,
            @QueryParam("searchConditions") final String searchConditions, @QueryParam("officeId") final Long officeId,
            @QueryParam("staffId") final Long staffId, @QueryParam("groupId") final Long groupId,
            @QueryParam("centerId") final Long centerId) {
        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final Integer offset = null;
        final Integer limit = null;
        final String orderBy = null;
        final String sortOrder = null;
        final Long paymentTypeId = null;
        final SearchParameters searchParameters = SearchParameters.forTask(searchConditionsMap, officeId, staffId, centerId, groupId,
                offset, limit, orderBy, sortOrder, paymentTypeId);
        final Collection<ClientData> clientData = this.clientReadPlatformService
                .retrieveAllForTaskLookupBySearchParameters(searchParameters);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @DefaultValue("false") @QueryParam("isFetchAdressDetails") final boolean isFetchAdressDetails) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        ClientData clientData = null;
        if (isFetchAdressDetails) {
            clientData = this.clientReadPlatformService.retrieveOneWithBasicDetails(clientId);
        } else {
            clientData = this.clientReadPlatformService.retrieveOne(clientId);

            if (!associationParameters.isEmpty()) {
                if (associationParameters.contains("hierarchyLookup")) {
                    final Collection<GroupGeneralData> groups = clientData.getGroups();
                    if (groups != null) {
                        final Collection<GroupGeneralData> updatedgroups = new ArrayList<>();
                        for (final GroupGeneralData groupGeneralData : groups) {
                            final GroupGeneralData data = this.groupReadPlatformService.retrieveCenterDetailsWithGroup(groupGeneralData);
                            updatedgroups.add(data);
                        }
                        clientData = ClientData.setParentGroups(clientData, updatedgroups);
                    }
                }
            }

            if (settings.isTemplate()) {
                final ClientData templateData = this.clientReadPlatformService.retrieveTemplate(clientData.officeId(),
                        staffInSelectedOfficeOnly);
                clientData = ClientData.templateOnTop(clientData, templateData);
                final Collection<SavingsAccountData> savingAccountOptions = this.savingsAccountReadPlatformService
                        .retrieveForLookup(clientId, null);
                if (savingAccountOptions != null && savingAccountOptions.size() > 0) {
                    clientData = ClientData.templateWithSavingAccountOptions(clientData, savingAccountOptions);
                }
            }
        }

        return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{clientId}/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateForClient(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo,
            @QueryParam("command") final String commandParam) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);

        ClientData clientData = null;
        if (is(commandParam, "activate")) {
            clientData = this.clientReadPlatformService.retrieveOne(clientId);
            if (this.configurationDomainService.isCustomerDeDuplicationEnabled()) {
                clientData.setPossibleClientMatches(this.deDuplicationService.getDuplicationMatches(clientId));
            }
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createClient() //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateClient(clientId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("clientId") final Long clientId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteClient(clientId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String activate(@PathParam("clientId") final Long clientId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "forceActivate")) {
            commandRequest = builder.forceActivateClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "assignStaff")) {
            commandRequest = builder.assignClientStaff(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "unassignStaff")) {
            commandRequest = builder.unassignClientStaff(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "close")) {
            commandRequest = builder.closeClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "proposeTransfer")) {
            commandRequest = builder.proposeClientTransfer(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "proposeAndAcceptTransfer")) {
            commandRequest = builder.proposeAndAcceptClientTransfer(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "withdrawTransfer")) {
            commandRequest = builder.withdrawClientTransferRequest(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "acceptTransfer")) {
            commandRequest = builder.acceptClientTransfer(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "rejectTransfer")) {
            commandRequest = builder.rejectClientTransfer(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "updateSavingsAccount")) {
            commandRequest = builder.updateClientSavingsAccount(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "reject")) {
            commandRequest = builder.rejectClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "withdraw")) {
            commandRequest = builder.withdrawClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "reactivate")) {
            commandRequest = builder.reActivateClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undoRejection")) {
            commandRequest = builder.undoRejection(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undoWithdrawal")) {
            commandRequest = builder.undoWithdrawal(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam,
                new Object[] { "activate", "unassignStaff", "assignStaff", "close", "proposeTransfer", "withdrawTransfer", "acceptTransfer",
                        "rejectTransfer", "updateSavingsAccount", "reject", "withdraw", "reactivate" }); }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    @GET
    @Path("{clientId}/accounts")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAssociatedAccounts(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final AccountSummaryCollectionData clientAccount = this.accountDetailsReadPlatformService.retrieveClientAccountDetails(clientId);

        final Set<String> CLIENT_ACCOUNTS_DATA_PARAMETERS = new HashSet<>(
                Arrays.asList("loanAccounts", "savingsAccounts", "paymentTypeOptions", "pledges", "shareAccounts"));

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.clientAccountSummaryToApiJsonSerializer.serialize(settings, clientAccount, CLIENT_ACCOUNTS_DATA_PARAMETERS);
    }

    @GET
    @Path("{clientId}/incomingSmsDetail")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrievePaymentDetail(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final ArrayList<PaymentDetailCollectionData> PaymentDetail = (ArrayList<PaymentDetailCollectionData>) this.accountDetailsReadPlatformService
                .retrivePaymentDetail(clientId);
        // final ApiRequestJsonSerializationSettings settings =
        // this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.clientAccountSummaryToApiJsonSerializer.serialize(PaymentDetail);
    }

    @GET
    @Path("{clientId}/sharesAccount")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveSharesAccountBalance(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final ArrayList<SharesAccountBalanceCollectionData> sharesAccountBalance = (ArrayList<SharesAccountBalanceCollectionData>) this.accountDetailsReadPlatformService
                .retriveSharesBalance(clientId);
        // final ApiRequestJsonSerializationSettings settings =
        // this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.clientAccountSummaryToApiJsonSerializer.serialize(sharesAccountBalance);
    }

    @GET
    @Path("{clientId}/Mpesa")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveMpesaTransactionDetail(@PathParam("clientId") final Long clientId,
            @QueryParam("TransactionDate") final String TransactionDate, @QueryParam("ReceiptNo") final String ReceiptNo,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final ArrayList<MpesaTransactionSummaryData> mpesaTxnDetails = (ArrayList<MpesaTransactionSummaryData>) this.accountDetailsReadPlatformService
                .retriveMpesaTransactionDetail(clientId, TransactionDate, ReceiptNo);
        return this.clientAccountSummaryToApiJsonSerializer.serialize(mpesaTxnDetails);
    }

    @GET
    @Path("{clientId}/clientsPayments")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveAssociateAccountsAndCharges(@PathParam("clientId") final Long clientId,
            @QueryParam("submittedOnDate") final Date submittedOnDate, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final Date currentdate = new Date();
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String chargeonDate = formatter.format(currentdate);
        if (submittedOnDate != null) {
            chargeonDate = formatter.format(submittedOnDate);
        }
        final AccountSummaryCollectionData clientAccount = this.accountDetailsReadPlatformService
                .retriveClientAccountAndChargeDetails(clientId, chargeonDate);
        final Set<String> CLIENT_ACCOUNTS_DATA_PARAMETERS = new HashSet<>(
                Arrays.asList("loanAccounts", "savingsAccounts", "paymentTypeOptions", "loanCharges", "savingsCharges"));
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.clientAccountSummaryToApiJsonSerializer.serialize(settings, clientAccount, CLIENT_ACCOUNTS_DATA_PARAMETERS);

    }

}