package com.finflux.ruleengine.configuration.api;

import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;
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
@Path("/risk/criteria")
@Component
@Scope("singleton")
public class CriteriaApiResource {

    private final Set<String> RESPONSE_FACTOR_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name",
            "uname","description","isActive"));

    private final String resourceNameForPermissions = "Risk";
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<RuleData> toApiJsonSerializer;
    private final RiskConfigReadPlatformService readPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public CriteriaApiResource(final PlatformSecurityContext context,
                               final DefaultToApiJsonSerializer<RuleData> toApiJsonSerializer,
                               final RiskConfigReadPlatformService readPlatformService,
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
    public String createRiskCriteria(final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createRiskCriteria().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCriterias(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final List<RuleData> criterias = this.readPlatformService.getAllCriterias();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, criterias, this.RESPONSE_FACTOR_DATA_PARAMETERS);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{criteriaId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneRuleCriteria(@PathParam("criteriaId") final Long criteriaId,
                                         @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(RiskConfigurationApiConstants.FACTOR_CONFIGURATION_RESOURCE_NAME);
        final RuleData ruleData = this.readPlatformService.retrieveOneCriteria(criteriaId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, ruleData);
    }

    @PUT
    @Path("{criteriaId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateRuleCriteria(@PathParam("criteriaId") final Long criteriaId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateRiskCriteria(criteriaId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}
