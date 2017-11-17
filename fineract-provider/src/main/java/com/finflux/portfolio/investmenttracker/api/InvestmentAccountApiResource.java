package com.finflux.portfolio.investmenttracker.api;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.data.InvestmentAccountChargeData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountSavingsLinkagesData;
import com.finflux.portfolio.investmenttracker.data.InvestmentProductData;
import com.finflux.portfolio.investmenttracker.service.InvestmentAccountReadService;

@Path("/investmentaccounts")
@Component
@Scope("singleton")
public class InvestmentAccountApiResource {
    
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final InvestmentAccountReadService investmentAccountReadService;
    
    @Autowired
    public InvestmentAccountApiResource(PlatformSecurityContext context, DefaultToApiJsonSerializer toApiJsonSerializer,
            ApiRequestParameterHelper apiRequestParameterHelper,
            PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
             InvestmentAccountReadService investmentAccountReadService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.investmentAccountReadService = investmentAccountReadService;
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);
        final InvestmentAccountData investmentAccountData = this.investmentAccountReadService.retrieveInvestmentAccountTemplate(null);
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
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentAccountApiConstants.INVESTMENT_ACCOUNT_RESOURCE_NAME);

        final Collection<InvestmentAccountData> investmentAccounts = this.investmentAccountReadService.retrieveAll();

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

        Collection<InvestmentAccountSavingsLinkagesData> investmentAccSavingsLinkages= this.investmentAccountReadService.retrieveInvestmentAccountSavingLinkages(investmentAccountId);

        investmentAccountData.setInvestmentSavingsLinkagesData(investmentAccSavingsLinkages);
        
        Collection<InvestmentAccountChargeData> charges = this.investmentAccountReadService.retrieveInvestmentAccountCharges(investmentAccountId);

        investmentAccountData.setInvestmentAccountCharges(charges);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (settings.isTemplate()) {
            investmentAccountData = this.investmentAccountReadService.retrieveInvestmentAccountTemplate(investmentAccountData);
        }
        return this.toApiJsonSerializer.serialize(settings, investmentAccountData);
    }
    
    @POST
    @Path("{investmentAccountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String stateTransitions(@PathParam("investmentAccountId") final Long investmentAccountId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "approve")) {
            commandRequest = builder.approveInvestmentAccount(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if(is(commandParam, "active")){
            commandRequest = builder.activateInvestmentAccount(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if(is(commandParam, "reject")){
            commandRequest = builder.activateInvestmentAccount(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if(is(commandParam, "undoapproval")){
            commandRequest = builder.undoInvestmentAccountApproval(investmentAccountId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }
        
        
        return this.toApiJsonSerializer.serialize(result);
    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}
