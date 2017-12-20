package com.finflux.portfolio.investmenttracker.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
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
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.common.util.FinfluxStringUtils;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountChargeData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountSavingsLinkagesData;
import com.finflux.portfolio.investmenttracker.data.InvestmentSavingsTransactionData;
import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionData;
import com.finflux.portfolio.investmenttracker.service.InvestmentAccountReadService;
import com.finflux.portfolio.investmenttracker.service.InvestmentTransactionReadPlatformService;

@Path("/investmentaccounts")
@Component
@Scope("singleton")
public class InvestmentAccountApiResource {

    private final PlatformSecurityContext context;
    @SuppressWarnings("rawtypes")
	private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final InvestmentAccountReadService investmentAccountReadService;
    private final InvestmentTransactionReadPlatformService investmentTransactionReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;

    @SuppressWarnings("rawtypes")
	@Autowired
    public InvestmentAccountApiResource(PlatformSecurityContext context, DefaultToApiJsonSerializer toApiJsonSerializer,
            ApiRequestParameterHelper apiRequestParameterHelper,
            PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            InvestmentAccountReadService investmentAccountReadService,
            InvestmentTransactionReadPlatformService investmentTransactionReadPlatformService,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final OfficeReadPlatformService officeReadPlatformService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.investmentAccountReadService = investmentAccountReadService;
        this.investmentTransactionReadPlatformService = investmentTransactionReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @QueryParam("officeId") final Long officeId) {
        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final InvestmentAccountData investmentAccountData = this.investmentAccountReadService.retrieveInvestmentAccountTemplate(null,
                staffInSelectedOfficeOnly, officeId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, investmentAccountData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createInvestmentAccount(final String apiRequestBodyAsJson) {

        this.context.authenticatedUser();

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createInvestmentAccountt().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("searchConditions") final String searchConditions,
            @QueryParam("officeId") final Long officeId, @QueryParam("partnerId") final Long partnerId,
            @QueryParam("investmentProductId") final Long investmentProductId,
            @QueryParam("investmentAccountStatus") final Integer investmentAccountStatus,
            @QueryParam("marturityFromDate") final Date marturityFromDate, @QueryParam("marturityToDate") final Date marturityToDate) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);

        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);

        SearchParameters searchParameters = SearchParameters.forInvestmentAccount(searchConditionsMap, officeId, investmentProductId,
                partnerId, investmentAccountStatus, marturityFromDate, marturityToDate);

        final Collection<InvestmentAccountData> investmentAccounts = this.investmentAccountReadService.retrieveAll(searchParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, investmentAccounts);
    }

    @GET
    @Path("{investmentAccountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("investmentAccountId") Long investmentAccountId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);

        InvestmentAccountData investmentAccountData = this.investmentAccountReadService.retrieveInvestmentAccount(investmentAccountId);

        Collection<InvestmentAccountSavingsLinkagesData> investmentAccSavingsLinkages = this.investmentAccountReadService
                .retrieveInvestmentAccountSavingLinkages(investmentAccountId);

        investmentAccountData.setInvestmentSavingsLinkagesData(investmentAccSavingsLinkages);

        Collection<InvestmentAccountChargeData> charges = this.investmentAccountReadService
                .retrieveInvestmentAccountCharges(investmentAccountId);

        investmentAccountData.setInvestmentAccountCharges(charges);

        Collection<InvestmentTransactionData> transactions = this.investmentTransactionReadPlatformService
                .findByAccountId(investmentAccountId);

        investmentAccountData.setInvestmentAccountTransactions(transactions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (settings.isTemplate()) {
            investmentAccountData = this.investmentAccountReadService.retrieveInvestmentAccountTemplate(investmentAccountData, true,
                    investmentAccountData.getOfficeData().getId());
        }
        return this.toApiJsonSerializer.serialize(settings, investmentAccountData);
    }

    @GET
    @Path("{investmentAccountId}/savingslinkage/{savingsLinkageAccountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLinkedSavingAccount(@PathParam("investmentAccountId") Long investmentAccountId,
            @PathParam("savingsLinkageAccountId") Long savingsLinkageAccountId,
            @DefaultValue("false") @QueryParam("isTemplate") final boolean isTemplate, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);

        InvestmentAccountSavingsLinkagesData savingLinkageAccountData = this.investmentAccountReadService
                .retrieveInvestmentSavingsLinkageAccountData(investmentAccountId, savingsLinkageAccountId);

        List<InvestmentSavingsTransactionData> investmentSavingsTransactionData = this.investmentTransactionReadPlatformService
                .findByInvestmentIdAndSavingsId(investmentAccountId, savingLinkageAccountData.getSavingsAccountId());
        savingLinkageAccountData.setInvestmentSavingsTransactionData(investmentSavingsTransactionData);
        if (isTemplate) {
        	InvestmentAccountData investmentAccountData = this.investmentAccountReadService.retrieveInvestmentAccount(investmentAccountId);
        	OfficeData officeData = this.officeReadPlatformService.retrieveOffice(investmentAccountData.getOfficeData().getId());
            final List<SavingsAccountData> savingsAccounts = this.savingsAccountReadPlatformService
                    .retrieveAllActiveSavingsAccountsByOffice(officeData.getHierarchy());
            savingLinkageAccountData.setSavingsAccounts(savingsAccounts);
        }

        return this.toApiJsonSerializer.serialize(savingLinkageAccountData);
    }
    
    @POST
    @Path("{investmentAccountId}/savingslinkage/{savingsLinkageAccountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String releaseLinkedSavingAccount(@PathParam("investmentAccountId") Long investmentAccountId,
            @PathParam("savingsLinkageAccountId") Long savingsLinkageAccountId, @QueryParam("command") final String commandParam, final String apiRequestBodyAsJson , @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        if (is(commandParam, "release")) {
            commandRequest = builder.releaseSavingLinkageAccount(investmentAccountId, savingsLinkageAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "transfer")) {
            commandRequest = builder.transferSavingLinkageAccount(investmentAccountId, savingsLinkageAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam);
        }
        return null;
    }

    @POST
    @Path("{investmentAccountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String stateTransitions(@PathParam("investmentAccountId") final Long investmentAccountId,
            @QueryParam("command") final String commandParam, final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "approve")) {
            commandRequest = builder.approveInvestmentAccount(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "active")) {
             commandRequest = builder.activateInvestmentAccount(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "reject")) {
            commandRequest = builder.rejectInvestmentAccount(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undoapproval")) {
            commandRequest = builder.undoInvestmentAccountApproval(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "reinvest")) {
            commandRequest = builder.investmentAccountReinvest(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "close")) {
            commandRequest = builder.investmentAccountClose(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @PUT
    @Path("{investmentAccountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyInvestmentAccount(@PathParam("investmentAccountId") final Long investmentAccountId,
            final String apiRequestBodyAsJson) {

        this.context.authenticatedUser();

        final CommandWrapper commandRequest = new CommandWrapperBuilder().modifyInvestmentAccountApproval(investmentAccountId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
    
    @GET
    @Path("{investmentAccountId}/reinvesttemplate")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReinvestmentAccountTemplate(@Context final UriInfo uriInfo, @PathParam("investmentAccountId") final Long investmentAccountId) {
        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final InvestmentAccountData investmentAccountData = this.investmentAccountReadService.retrieveReinvestmentAccountTemplateData(investmentAccountId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, investmentAccountData);
    }

}
