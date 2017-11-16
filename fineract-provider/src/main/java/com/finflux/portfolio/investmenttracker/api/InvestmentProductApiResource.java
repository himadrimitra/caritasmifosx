package com.finflux.portfolio.investmenttracker.api;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingReadPlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.investmenttracker.data.InvestmentProductData;
import com.finflux.portfolio.investmenttracker.service.InvestmentProductReadService;

@Path("/investmentproducts")
@Component
@Scope("singleton")
public class InvestmentProductApiResource {

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final InvestmentProductReadService investmentProductReadService;
    private final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;

    @Autowired
    public InvestmentProductApiResource(PlatformSecurityContext context, DefaultToApiJsonSerializer toApiJsonSerializer,
            ApiRequestParameterHelper apiRequestParameterHelper,
            PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final InvestmentProductReadService investmentProductReadService,
            final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.investmentProductReadService = investmentProductReadService;
        this.accountMappingReadPlatformService = accountMappingReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(InvestmentProductApiconstants.INVESTMENT_PRODUCT_RESOURCE_NAME);
        final InvestmentProductData investmentProductData = this.investmentProductReadService.retrieveInvestmentProductTemplate(null);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, investmentProductData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createInvestmentProduct(final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createInvestmentProduct().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentProductApiconstants.INVESTMENT_PRODUCT_RESOURCE_NAME);

        final Collection<InvestmentProductData> products = this.investmentProductReadService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, products);
    }

    @GET
    @Path("{investmentProductId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("investmentProductId") Long investmentProductId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(InvestmentProductApiconstants.INVESTMENT_PRODUCT_RESOURCE_NAME);

        InvestmentProductData investmentProductData = this.investmentProductReadService.retrieveOne(investmentProductId);

        final Collection<ChargeData> charges = this.chargeReadPlatformService
                .retrieveInvestmentProductApplicableCharges(investmentProductId);

        investmentProductData = InvestmentProductData.withCharges(investmentProductData, charges);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (investmentProductData.hasAccountingEnabled()) {
            final Map<String, Object> accountingMappings = this.accountMappingReadPlatformService
                    .fetchAccountMappingDetailsForInvestmentProduct(investmentProductId, investmentProductData.accountingRuleTypeId());
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = this.accountMappingReadPlatformService
                    .fetchPaymentTypeToFundSourceMappingsForInvestmentProduct(investmentProductId);
            final Collection<ChargeToGLAccountMapper> feeToExpenseAccountMappings = this.accountMappingReadPlatformService
                    .fetchFeeToExpenseAccountMappingsForInvestmentProduct(investmentProductId);
            investmentProductData = InvestmentProductData.withAccountDetails(investmentProductData, accountingMappings,
                    paymentChannelToFundSourceMappings, feeToExpenseAccountMappings);
        }
        if (settings.isTemplate()) {
            investmentProductData = this.investmentProductReadService.retrieveInvestmentProductTemplate(investmentProductData);
        }
        return this.toApiJsonSerializer.serialize(settings, investmentProductData);
    }

    @PUT
    @Path("{investmentProductId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("investmentProductId") final Long investmentProductId, final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateInvestmentProduct(investmentProductId)
                .withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}
