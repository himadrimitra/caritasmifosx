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
package org.apache.fineract.portfolio.loanproduct.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.CodeValueToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingReadPlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.common.service.DropdownReadPlatformService;
import org.apache.fineract.portfolio.floatingrates.data.FloatingRateData;
import org.apache.fineract.portfolio.floatingrates.service.FloatingRatesReadPlatformService;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.apache.fineract.portfolio.loanproduct.productmix.data.ProductMixData;
import org.apache.fineract.portfolio.loanproduct.productmix.service.ProductMixReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loanproduct.creditbureau.data.CreditBureauLoanProductMappingData;
import com.finflux.portfolio.loanproduct.creditbureau.service.CreditBureauLoanProductMappingReadPlatformService;
import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityData;
import com.finflux.ruleengine.eligibility.service.LoanProductEligibilityReadPlatformService;

@Path("/loanproducts")
@Component
@Scope("singleton")
public class LoanProductsApiResource {

    private final Set<String> LOAN_PRODUCT_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "shortName", "description",
            "fundId", "fundName", "includeInBorrowerCycle", "currency", "principal", "minPrincipal", "maxPrincipal", "numberOfRepayments",
            "minNumberOfRepayments", "maxNumberOfRepayments", "repaymentEvery", "repaymentFrequencyType", "graceOnPrincipalPayment", "recurringMoratoriumOnPrincipalPeriods",
            "graceOnInterestPayment", "graceOnInterestCharged", "interestRatePerPeriod", "minInterestRatePerPeriod",
            "maxInterestRatePerPeriod", "interestRateFrequencyType", "annualInterestRate", "amortizationType", "interestType",
            "interestCalculationPeriodType", LoanProductConstants.allowPartialPeriodInterestCalcualtionParamName, "inArrearsTolerance",
            "transactionProcessingStrategyId", "transactionProcessingStrategyName", "charges", "accountingRule", "externalId",
            "accountingMappings", "paymentChannelToFundSourceMappings", "fundOptions", "paymentTypeOptions", "currencyOptions",
            "repaymentFrequencyTypeOptions", "interestRateFrequencyTypeOptions", "amortizationTypeOptions", "interestTypeOptions",
            "interestCalculationPeriodTypeOptions", "transactionProcessingStrategyOptions", "chargeOptions", "accountingOptions",
            "accountingRuleOptions", "accountingMappingOptions", "floatingRateOptions", "isLinkedToFloatingInterestRates",
            "floatingRatesId", "interestRateDifferential", "minDifferentialLendingRate", "defaultDifferentialLendingRate",
            "maxDifferentialLendingRate", "isFloatingInterestRateCalculationAllowed", LoanProductConstants.minLoanTerm, 
            LoanProductConstants.maxLoanTerm, LoanProductConstants.loanTenureFrequencyType,
            LoanProductConstants.canUseForTopup ,"writeOffToExpenseAccountMapping", LoanProductConstants.weeksInYearType));

    private final Set<String> PRODUCT_MIX_DATA_PARAMETERS = new HashSet<>(Arrays.asList("productId", "productName", "restrictedProducts",
            "allowedProducts", "productOptions"));

    private final Set<String> ELIGIBILITY_DATA_PARAMETERS = new HashSet<>(Arrays.asList("loanProductId", "loanProductName","isActive"));

    private final String resourceNameForPermissions = "LOANPRODUCT";

    private final PlatformSecurityContext context;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final DefaultToApiJsonSerializer<LoanProductData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService;
    private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
    private final DefaultToApiJsonSerializer<ProductMixData> productMixDataApiJsonSerializer;
    private final ToApiJsonSerializer<LoanProductEligibilityData> productEligibilityDataToApiJsonSerializer;
    private final ProductMixReadPlatformService productMixReadPlatformService;
    private final DropdownReadPlatformService commonDropdownReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final FloatingRatesReadPlatformService floatingRateReadPlatformService;
    private final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> creditbureauLoanproductDataApiJsonSerializer;
    private final CreditBureauLoanProductMappingReadPlatformService creditBureauLoanProductMappingReadPlatformService;
    private final LoanProductEligibilityReadPlatformService productEligibilityReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public LoanProductsApiResource(final PlatformSecurityContext context, final LoanProductReadPlatformService readPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService, final CurrencyReadPlatformService currencyReadPlatformService,
            final FundReadPlatformService fundReadPlatformService, final LoanDropdownReadPlatformService dropdownReadPlatformService,
            final DefaultToApiJsonSerializer<LoanProductData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService,
            final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService,
            final DefaultToApiJsonSerializer<ProductMixData> productMixDataApiJsonSerializer,
            final DefaultToApiJsonSerializer<LoanProductEligibilityData> productEligibilityDataToApiJsonSerializer,
            final ProductMixReadPlatformService productMixReadPlatformService,
            final DropdownReadPlatformService commonDropdownReadPlatformService,
            PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final FloatingRatesReadPlatformService floatingRateReadPlatformService,
            final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> creditbureauLoanproductDataApiJsonSerializer,
            final CreditBureauLoanProductMappingReadPlatformService creditBureauLoanProductMappingReadPlatformService,final LoanProductEligibilityReadPlatformService productEligibilityReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.loanProductReadPlatformService = readPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.fundReadPlatformService = fundReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.accountMappingReadPlatformService = accountMappingReadPlatformService;
        this.accountingDropdownReadPlatformService = accountingDropdownReadPlatformService;
        this.productMixDataApiJsonSerializer = productMixDataApiJsonSerializer;
        this.productMixReadPlatformService = productMixReadPlatformService;
        this.commonDropdownReadPlatformService = commonDropdownReadPlatformService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.floatingRateReadPlatformService = floatingRateReadPlatformService;
        this.creditbureauLoanproductDataApiJsonSerializer = creditbureauLoanproductDataApiJsonSerializer;
        this.creditBureauLoanProductMappingReadPlatformService = creditBureauLoanProductMappingReadPlatformService;
        this.productEligibilityDataToApiJsonSerializer = productEligibilityDataToApiJsonSerializer;
        this.productEligibilityReadPlatformService = productEligibilityReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanProduct(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanProduct().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllLoanProducts(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (!associationParameters.isEmpty()) {
            if (associationParameters.contains("productMixes")) {
                this.context.authenticatedUser().validateHasReadPermission("PRODUCTMIX");
                final Collection<ProductMixData> productMixes = this.productMixReadPlatformService.retrieveAllProductMixes();
                return this.productMixDataApiJsonSerializer.serialize(settings, productMixes, this.PRODUCT_MIX_DATA_PARAMETERS);
            }else if (associationParameters.contains("creditBureaus")) {
                final Collection<CreditBureauLoanProductMappingData> creditBureauLoanProductMappingData = this.creditBureauLoanProductMappingReadPlatformService.retrieveAllCreditbureauLoanproductMappingData();
                return this.creditbureauLoanproductDataApiJsonSerializer.serialize(settings, creditBureauLoanProductMappingData);
            }
            if (associationParameters.contains("eligibility")) {
                final Collection<LoanProductEligibilityData> eligibilityLoanProducts = this.productEligibilityReadPlatformService.getAllLoanProductEligibility();
                return this.productEligibilityDataToApiJsonSerializer.serialize(settings, eligibilityLoanProducts, this.ELIGIBILITY_DATA_PARAMETERS);
            }
        }

        final Collection<LoanProductData> products = this.loanProductReadPlatformService.retrieveAllLoanProducts();

        return this.toApiJsonSerializer.serialize(settings, products, this.LOAN_PRODUCT_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("isProductMixTemplate") final boolean isProductMixTemplate) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (isProductMixTemplate) {
            this.context.authenticatedUser().validateHasReadPermission("PRODUCTMIX");

            final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAvailableLoanProductsForMix();
            final ProductMixData productMixData = ProductMixData.template(productOptions);
            return this.productMixDataApiJsonSerializer.serialize(settings, productMixData, this.PRODUCT_MIX_DATA_PARAMETERS);
        }

        LoanProductData loanProduct = this.loanProductReadPlatformService.retrieveNewLoanProductDetails();
        loanProduct = handleTemplate(loanProduct);

        return this.toApiJsonSerializer.serialize(settings, loanProduct, this.LOAN_PRODUCT_DATA_PARAMETERS);
    }

    @GET
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanProductDetails(@PathParam("productId") final Long productId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (associationParameters.contains("creditBureaus")) {
            final CreditBureauLoanProductMappingData creditBureauLoanProductMappingData = this.creditBureauLoanProductMappingReadPlatformService.retrieveCreditbureauLoanproductMappingData(productId);
            return this.creditbureauLoanproductDataApiJsonSerializer.serialize(settings, creditBureauLoanProductMappingData);
        }
        LoanProductData loanProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);

        Map<String, Object> accountingMappings = null;
        Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;
        Collection<ChargeToGLAccountMapper> feeToGLAccountMappings = null;
        Collection<CodeValueToGLAccountMapper> codeValueToGLAccountMappings = null;
        Collection<ChargeToGLAccountMapper> penaltyToGLAccountMappings = null;
        if (loanProduct.hasAccountingEnabled()) {
            accountingMappings = this.accountMappingReadPlatformService.fetchAccountMappingDetailsForLoanProduct(productId, loanProduct
                    .accountingRuleType().getId().intValue());
            paymentChannelToFundSourceMappings = this.accountMappingReadPlatformService
                    .fetchPaymentTypeToFundSourceMappingsForLoanProduct(productId);
            feeToGLAccountMappings = this.accountMappingReadPlatformService
                    .fetchFeeToIncomeOrLiabilityAccountMappingsForLoanProduct(productId);            
            penaltyToGLAccountMappings = this.accountMappingReadPlatformService
                    .fetchPenaltyToIncomeAccountMappingsForLoanProduct(productId);
            codeValueToGLAccountMappings = this.accountMappingReadPlatformService.fetchCodeValueToExpenseAccountMappingsForLoanProduct(productId);
            loanProduct = LoanProductData.withAccountingDetails(loanProduct, accountingMappings, paymentChannelToFundSourceMappings,
                    feeToGLAccountMappings, penaltyToGLAccountMappings, codeValueToGLAccountMappings);
        }

        if (settings.isTemplate()) {
            loanProduct = handleTemplate(loanProduct);
        }
        return this.toApiJsonSerializer.serialize(settings, loanProduct, this.LOAN_PRODUCT_DATA_PARAMETERS);
    }

    @PUT
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanProduct(@PathParam("productId") final Long productId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLoanProduct(productId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    private LoanProductData handleTemplate(final LoanProductData productData) {

        Collection<ChargeData> chargeOptions = this.chargeReadPlatformService.retrieveLoanApplicableFees();
        if (chargeOptions.isEmpty()) {
            chargeOptions = null;
        }

        Collection<ChargeData> penaltyOptions = this.chargeReadPlatformService.retrieveLoanApplicablePenalties();
        if (penaltyOptions.isEmpty()) {
            penaltyOptions = null;
        }

        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
        final List<EnumOptionData> amortizationTypeOptions = this.dropdownReadPlatformService.retrieveLoanAmortizationTypeOptions();
        final List<EnumOptionData> interestTypeOptions = this.dropdownReadPlatformService.retrieveLoanInterestTypeOptions();
        final List<EnumOptionData> interestCalculationPeriodTypeOptions = this.dropdownReadPlatformService
                .retrieveLoanInterestRateCalculatedInPeriodOptions();
        final List<EnumOptionData> repaymentFrequencyTypeOptions = this.dropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions();
        final List<EnumOptionData> interestRateFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();
        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();

        Collection<FundData> fundOptions = this.fundReadPlatformService.retrieveAllFunds(FundApiConstants.activeParamName);
        if (fundOptions.isEmpty()) {
            fundOptions = null;
        }
        final Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions = this.dropdownReadPlatformService
                .retreiveTransactionProcessingStrategies();

        final Map<String, List<GLAccountData>> accountOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountMappingOptionsForLoanProducts();

        final List<EnumOptionData> accountingRuleTypeOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountingRuleTypeOptions();

        final List<EnumOptionData> loanCycleValueConditionTypeOptions = this.dropdownReadPlatformService
                .retrieveLoanCycleValueConditionTypeOptions();

        final List<EnumOptionData> daysInMonthTypeOptions = commonDropdownReadPlatformService.retrieveDaysInMonthTypeOptions();
        final List<EnumOptionData> daysInYearTypeOptions = commonDropdownReadPlatformService.retrieveDaysInYearTypeOptions();
        final List<EnumOptionData> interestRecalculationCompoundingTypeOptions = dropdownReadPlatformService
                .retrieveInterestRecalculationCompoundingTypeOptions();
        final List<EnumOptionData> rescheduleStrategyTypeOptions = dropdownReadPlatformService.retrieveRescheduleStrategyTypeOptions();
        final List<EnumOptionData> interestRecalculationFrequencyTypeOptions = dropdownReadPlatformService
                .retrieveInterestRecalculationFrequencyTypeOptions();
        final List<EnumOptionData> interestRecalculationNthDayTypeOptions = dropdownReadPlatformService
                .retrieveInterestRecalculationNthDayTypeOptions();
        final List<EnumOptionData> interestRecalculationDayOfWeekTypeOptions = dropdownReadPlatformService
                .retrieveInterestRecalculationDayOfWeekTypeOptions();
        final List<EnumOptionData> preCloseInterestCalculationStrategyOptions = dropdownReadPlatformService
                .retrivePreCloseInterestCalculationStrategyOptions();
        final List<FloatingRateData> floatingRateOptions = this.floatingRateReadPlatformService.retrieveLookupActive();
        final boolean closeLoanOnOverpayment = productData.closeLoanOnOverpayment();

        final List<CodeValueData> codeValueOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(LoanApiConstants.WRITEOFFREASONS));
        
        final List<EnumOptionData> weeksInYearTypeOptions = this.dropdownReadPlatformService
                .retrieveWeeksInYearTypeOptions();
        
        final List<EnumOptionData> brokenPeriodMethodTypeOptions = this.dropdownReadPlatformService.retrieveBrokenPeriodMethodTypeOptions();
        
        return new LoanProductData(productData, chargeOptions, penaltyOptions, paymentTypeOptions, currencyOptions,
                amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions, repaymentFrequencyTypeOptions,
                interestRateFrequencyTypeOptions, fundOptions, transactionProcessingStrategyOptions, accountOptions,
                accountingRuleTypeOptions, loanCycleValueConditionTypeOptions, daysInMonthTypeOptions, daysInYearTypeOptions,
                interestRecalculationCompoundingTypeOptions, rescheduleStrategyTypeOptions, interestRecalculationFrequencyTypeOptions,
                preCloseInterestCalculationStrategyOptions, floatingRateOptions, interestRecalculationNthDayTypeOptions,
                interestRecalculationDayOfWeekTypeOptions, closeLoanOnOverpayment, codeValueOptions, weeksInYearTypeOptions, brokenPeriodMethodTypeOptions);
    }

}