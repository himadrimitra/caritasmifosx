package com.finflux.risk.creditbureau.configuration.api;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.portfolio.loanproduct.creditbureau.data.CreditBureauLoanProductMappingData;
import com.finflux.portfolio.loanproduct.creditbureau.service.CreditBureauLoanProductMappingReadPlatformService;
import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;
import com.finflux.risk.creditbureau.configuration.data.OrganisationCreditbureauData;
import com.finflux.risk.creditbureau.configuration.service.CreditBureauProductReadPlatformService;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Path("/creditbureau")
@Component
@Scope("singleton")
public class CreditBureauConfigurationAPI {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("cb_id", "alias", "country", "cb_product_id",
            "start_date", "end_date", "is_active"));
    private final String resourceNameForPermissions = "CreditBureau";
    private final PlatformSecurityContext context;
    private final CreditBureauProductReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<CreditBureauData> toApiJsonSerializer;
    private final CreditBureauLoanProductMappingReadPlatformService readPlatformServiceCbLp;
    private final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> toApiJsonSerializerCbLp;
    private final DefaultToApiJsonSerializer<OrganisationCreditbureauData> toApiJsonSerializerOrgCb;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public CreditBureauConfigurationAPI(final PlatformSecurityContext context,
            final CreditBureauProductReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<CreditBureauData> toApiJsonSerializer,
            final CreditBureauLoanProductMappingReadPlatformService readPlatformServiceCbLp,
            final DefaultToApiJsonSerializer<CreditBureauLoanProductMappingData> toApiJsonSerializerCbLp,
            final DefaultToApiJsonSerializer<OrganisationCreditbureauData> toApiJsonSerializerOrgCb,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.readPlatformServiceCbLp = readPlatformServiceCbLp;
        this.toApiJsonSerializerCbLp = toApiJsonSerializerCbLp;
        this.toApiJsonSerializerOrgCb = toApiJsonSerializerOrgCb;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    /*
     * Get all credit bureau
     */
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditBureaus(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Collection<CreditBureauData> creditBureau = this.readPlatformService.retrieveCreditBureaus();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, creditBureau, this.RESPONSE_DATA_PARAMETERS);
    }

    /*
     * Get credit bureau by id
     */
    @GET
    @Path("{creditBureauId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditBureau(@PathParam("creditBureauId") final Long creditBureauId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final CreditBureauData creditBureau = this.readPlatformService.retrieveCreditBureau(creditBureauId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, creditBureau, this.RESPONSE_DATA_PARAMETERS);
    }

    /*
     * Activate and deactivate credit bureau
     */
    @POST
    @Path("{creditBureauId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doAction(@PathParam("creditBureauId") final Long creditBureauId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateCreditBureau(creditBureauId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "deactivate")) {
            commandRequest = builder.deactivateCreditBureau(creditBureauId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "activate", "deactivate" }); }
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}
