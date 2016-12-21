package com.finflux.ruleengine.execution.api;

import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;
import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibility;
import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.LoanProductEligibilityExecutionService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.execution.service.impl.LoanApplicationDataLayer;
import com.finflux.ruleengine.lib.data.RuleResult;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.*;

/**
 * Created by dhirendra on 15/09/16.
 */
@Path("/risk/execute/")
@Component
@Scope("singleton")
public class RuleExecutionApiResource {

    private final Set<String> RESPONSE_FACTOR_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name",
            "uname","description","isActive","valueType", "possibleOutputs"));

    private final String resourceNameForPermissions = "Risk";
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<RuleResult> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<EligibilityResult> toApiEligibilityJsonSerializer;
    private final RiskConfigReadPlatformService readPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final RuleExecutionService ruleExecutionService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final RuleCacheService ruleCacheService;
    private final LoanProductEligibilityExecutionService loanProductEligibilityExecutionService;

    @Autowired
    public RuleExecutionApiResource(final PlatformSecurityContext context,
                                    final DefaultToApiJsonSerializer<RuleResult> toApiJsonSerializer,
                                    final DefaultToApiJsonSerializer<EligibilityResult> toApiEligibilityJsonSerializer,
                                    final RiskConfigReadPlatformService readPlatformService,
                                    final ApiRequestParameterHelper apiRequestParameterHelper,
                                    final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                    final RuleExecutionService ruleExecutionService,
                                    final DataLayerReadPlatformService dataLayerReadPlatformService,
                                    final RuleCacheService ruleCacheService,
                                    LoanProductEligibilityExecutionService loanProductEligibilityExecutionService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.readPlatformService =readPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.ruleExecutionService = ruleExecutionService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.ruleCacheService = ruleCacheService;
        this.loanProductEligibilityExecutionService = loanProductEligibilityExecutionService;
        this.toApiEligibilityJsonSerializer = toApiEligibilityJsonSerializer;
     }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{ruleId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneRuleFactor(@PathParam("ruleId") final Long ruleId,
                                         @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(RuleExecutionApiConstants.RULE_EXECUTION_RESOURCE_NAME);
        LoanApplicationDataLayer dataLayer = new LoanApplicationDataLayer(dataLayerReadPlatformService);
        Map<DataLayerKey,Long> dataLayerKeyLongMap = new HashMap<>();
        dataLayerKeyLongMap.put(DataLayerKey.CLIENT_ID,1L);
        dataLayerKeyLongMap.put(DataLayerKey.LOANAPPLICATION_ID,1L);
        dataLayer.build(dataLayerKeyLongMap);
        final RuleResult ruleResult = this.ruleExecutionService.executeCriteria(ruleId,dataLayer);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, ruleResult);
    }


    @GET
    @Path("loanapplication/{loanApplicationId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getRiskAnalysisForLoan(@PathParam("loanApplicationId") final Long loanApplicationId,
                                        @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(RuleExecutionApiConstants.LOAN_PRODUCT_ELIGIBILITY_EXECUTION_RESOURCE_NAME);

        final EligibilityResult eligibilityResult = this.loanProductEligibilityExecutionService.getLoanEligibility(loanApplicationId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiEligibilityJsonSerializer.serialize(settings, eligibilityResult);
    }


    @POST
    @Path("loanapplication/{loanApplicationId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doRiskAnalysisForLoan(@PathParam("loanApplicationId") final Long loanApplicationId,
                                      @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(RuleExecutionApiConstants.LOAN_PRODUCT_ELIGIBILITY_EXECUTION_RESOURCE_NAME);

        final EligibilityResult eligibilityResult = this.loanProductEligibilityExecutionService.checkLoanEligibility(loanApplicationId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiEligibilityJsonSerializer.serialize(settings, eligibilityResult);
    }


}
