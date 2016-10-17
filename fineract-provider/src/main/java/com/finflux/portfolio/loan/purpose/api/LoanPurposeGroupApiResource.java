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

import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupTemplateData;
import com.finflux.portfolio.loan.purpose.service.LoanPurposeGroupReadPlatformService;

@Path("/loanpurposegroups")
@Component
@Scope("singleton")
public class LoanPurposeGroupApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanPurposeGroupReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public LoanPurposeGroupApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
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
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(LoanPurposeGroupApiConstants.LOAN_PURPOSE_GROUP_RESOURCE_NAME);

        final LoanPurposeGroupTemplateData templateData = this.readPlatformService.retrieveTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanPurposeGroup(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanPurposeGroup().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllLoanPurposeGroups(@QueryParam("loanPurposeGroupTypeId") final Integer loanPurposeGroupTypeId,
            @QueryParam("isFetchLoanPurposeDatas") final Boolean isFetchLoanPurposeDatas, @QueryParam("isActive") final Boolean isActive,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(LoanPurposeGroupApiConstants.LOAN_PURPOSE_GROUP_RESOURCE_NAME);

        final Collection<LoanPurposeGroupData> loanPurposeGroupDatas = this.readPlatformService.retrieveAllLoanPurposeGroups(
                loanPurposeGroupTypeId, isFetchLoanPurposeDatas, isActive);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanPurposeGroupDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{loanPurposeGroupId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneLoanPurposeGroup(@PathParam("loanPurposeGroupId") final Long loanPurposeGroupId,
            @QueryParam("isFetchLoanPurposeDatas") final Boolean isFetchLoanPurposeDatas, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(LoanPurposeGroupApiConstants.LOAN_PURPOSE_GROUP_RESOURCE_NAME);

        final LoanPurposeGroupData loanPurposeGroupData = this.readPlatformService.retrieveOneLoanPurposeGroup(loanPurposeGroupId,
                isFetchLoanPurposeDatas);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanPurposeGroupData);
    }

    @POST
    @Path("{loanPurposeGroupId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanPurposeGroup(@PathParam("loanPurposeGroupId") final Long loanPurposeGroupId,
            @QueryParam("command") final String commandParam, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateLoanPurposeGroup(loanPurposeGroupId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "inactivate")) {
            commandRequest = builder.inActivateLoanPurposeGroup(loanPurposeGroupId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        return null;
    }

    @PUT
    @Path("{loanPurposeGroupId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoanPurposeGroup(@PathParam("loanPurposeGroupId") final Long loanPurposeGroupId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateLoanPurposeGroup(loanPurposeGroupId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}