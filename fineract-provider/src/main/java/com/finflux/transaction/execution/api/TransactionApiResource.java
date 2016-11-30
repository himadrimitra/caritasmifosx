package com.finflux.transaction.execution.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;
import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.LoanProductEligibilityExecutionService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.transaction.execution.service.AccountTransferService;

/**
 * Created by dhirendra on 15/09/16.
 */
@Path("/transaction/test/")
@Component
@Scope("singleton")
public class TransactionApiResource {

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
    private final AccountTransferService accountTransferService;

    @Autowired
    public TransactionApiResource(final PlatformSecurityContext context,
                                    final DefaultToApiJsonSerializer<RuleResult> toApiJsonSerializer,
                                    final DefaultToApiJsonSerializer<EligibilityResult> toApiEligibilityJsonSerializer,
                                    final RiskConfigReadPlatformService readPlatformService,
                                    final ApiRequestParameterHelper apiRequestParameterHelper,
                                    final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                    final RuleExecutionService ruleExecutionService,
                                    final DataLayerReadPlatformService dataLayerReadPlatformService,
                                    final RuleCacheService ruleCacheService,
                                    final LoanProductEligibilityExecutionService loanProductEligibilityExecutionService,
                                    final AccountTransferService accountTransferService) {
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
        this.accountTransferService = accountTransferService;
     }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String testRBLTransaction(@PathParam("transactionId") final Long transactionId,
                                         @Context final UriInfo uriInfo) {
//        String clientId ="fd27b97f-7f62-4dd9-b27c-c5a02a6d7d00";
//        String clientSecret = "jK0aC3pQ3tG7mG1lY7eF1tV0nI5sO8vE1rN6xB2tT8vN7uG2lH";
//        String user ="CHAITANYA";
//        String password="pass@123";
//        String keystorePath="/Users/dhirendra/workspace/conflux-git/clients/rbl/client1.p12";
//        String keyStorePassword="finflux";
//        String rblEndPoint="https://apideveloper.rblbank.com";
//        String doSingleTxnResource="/test/sb/rbl/v1/payments/corp/payment";
//        String doSingleTxnStatusResource="/test/sb/rbl/v1/payments/corp/payment/query";
//        String validationResource="";
//        String rptCode="HSBA";
//        String corporateId="CHAITANYA";

//        Long txnId = accountTransferService.initiateTransaction(1L,1L,TransferType.FT);

  //      accountTransferService.doTransaction(txnId,4D,"Testing By finflux");

        return "Transaction Details";
    }

}
