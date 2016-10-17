package com.finflux.portfolio.loan.purpose.api;

import java.util.Collection;

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

import org.apache.commons.lang.StringUtils;
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

import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeTemplateData;
import com.finflux.portfolio.loan.purpose.service.LoanPurposeGroupReadPlatformService;

@Path("/loanpurposes")
@Component
@Scope("singleton")
public class LoanPurposeApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanPurposeGroupReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public LoanPurposeApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final LoanPurposeGroupReadPlatformService readPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readPlatformService = readPlatformService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@QueryParam("isActive") final Boolean isActive, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(LoanPurposeGroupApiConstants.LOAN_PURPOSE_RESOURCE_NAME);

        final LoanPurposeTemplateData templateData = this.readPlatformService.retrieveLoanPurposeTemplate(isActive);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanPurpose(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanPurpose().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllLoanPurposes(@QueryParam("loanPurposeGroupTypeId") final Integer loanPurposeGroupTypeId,
            @QueryParam("isFetchLoanPurposeGroupDatas") final Boolean isFetchLoanPurposeGroupDatas,
            @QueryParam("isActive") final Boolean isActive, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(LoanPurposeGroupApiConstants.LOAN_PURPOSE_RESOURCE_NAME);
        final Collection<LoanPurposeData> loanPurposeDatas = this.readPlatformService.retrieveAllLoanPurposes(loanPurposeGroupTypeId,
                isFetchLoanPurposeGroupDatas, isActive);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanPurposeDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{loanPurposeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneLoanPurpose(@PathParam("loanPurposeId") final Long loanPurposeId,
            @QueryParam("isFetchLoanPurposeGroupDatas") final Boolean isFetchLoanPurposeGroupDatas, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(LoanPurposeGroupApiConstants.LOAN_PURPOSE_GROUP_RESOURCE_NAME);
        final LoanPurposeData loanPurposeData = this.readPlatformService
                .retrieveOneLoanPurpose(loanPurposeId, isFetchLoanPurposeGroupDatas);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanPurposeData);
    }

    @POST
    @Path("{loanPurposeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanPurpose(@PathParam("loanPurposeId") final Long loanPurposeId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateLoanPurpose(loanPurposeId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "inactivate")) {
            commandRequest = builder.inActivateLoanPurpose(loanPurposeId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        return null;
    }

    @PUT
    @Path("{loanPurposeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanPurpose(@PathParam("loanPurposeId") final Long loanPurposeId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateLoanPurpose(loanPurposeId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}