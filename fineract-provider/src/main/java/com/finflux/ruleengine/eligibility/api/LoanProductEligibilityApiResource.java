package com.finflux.ruleengine.eligibility.api;

import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityData;
import com.finflux.ruleengine.eligibility.service.LoanProductEligibilityReadPlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dhirendra on 15/09/16.
 */
@Path("/loanproduct/{loanProductId}/eligibility")
@Component
@Scope("singleton")
public class LoanProductEligibilityApiResource {

    private final Set<String> RESPONSE_FACTOR_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name",
            "uname","description","isActive"));

    private final String resourceNameForPermissions = "Risk";
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<LoanProductEligibilityData> toApiJsonSerializer;
    private final LoanProductEligibilityReadPlatformService readPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public LoanProductEligibilityApiResource(final PlatformSecurityContext context,
                                             final DefaultToApiJsonSerializer<LoanProductEligibilityData> toApiJsonSerializer,
                                             final LoanProductEligibilityReadPlatformService readPlatformService,
                                             final ApiRequestParameterHelper apiRequestParameterHelper,
                                             final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.readPlatformService =readPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
     }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanProductEligibility(@PathParam("loanProductId") final Long loanProductId,
                                               final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanProductEligibility(loanProductId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

//    @GET
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String getAllLoanProductEligibility(@Context final UriInfo uriInfo) {
//        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
//        final List<LoanProductEligibilityData> loanProductEligibilitys = this.readPlatformService.getAllLoanProductEligibility();
//        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//        return this.toApiJsonSerializer.serialize(settings, loanProductEligibilitys, this.RESPONSE_FACTOR_DATA_PARAMETERS);
//    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneLoanProductEligibility(@PathParam("loanProductId") final Long loanProductId,
                                         @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(LoanProductEligibilityApiConstants.FACTOR_CONFIGURATION_RESOURCE_NAME);
        final LoanProductEligibilityData ruleData = this.readPlatformService.retrieveOneLoanProductEligibility(loanProductId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, ruleData);
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanProductEligibility(@PathParam("loanProductId") final Long loanProductId,
                                               final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateLoanProductEligibility(loanProductId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}
