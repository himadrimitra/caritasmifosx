package com.finflux.loanapplicationreference.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import com.finflux.loanapplicationreference.data.LoanApplicationChargeData;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceData;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceTemplateData;
import com.finflux.loanapplicationreference.data.LoanApplicationSanctionData;
import com.finflux.loanapplicationreference.service.LoanApplicationReferenceReadPlatformService;
import com.finflux.risk.creditbureau.provider.data.CreditBureauFileContentData;
import com.finflux.risk.creditbureau.provider.data.OtherInstituteLoansSummaryData;
import com.finflux.risk.creditbureau.provider.service.CreditBureauCheckService;

@Path("/loanapplicationreferences")
@Component
@Scope("singleton")
public class LoanApplicationReferenceApiResource {

    private final PlatformSecurityContext context;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
    private final CreditBureauCheckService creditBureauCheckService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public LoanApplicationReferenceApiResource(final PlatformSecurityContext context, final DefaultToApiJsonSerializer toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
            final CreditBureauCheckService creditBureauCheckService) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.loanApplicationReferenceReadPlatformService = loanApplicationReferenceReadPlatformService;
        this.creditBureauCheckService = creditBureauCheckService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@DefaultValue("false") @QueryParam("activeOnly") final boolean onlyActive, @Context final UriInfo uriInfo) {
        final LoanApplicationReferenceTemplateData templateData = this.loanApplicationReferenceReadPlatformService.templateData(onlyActive);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanApplicationReference().withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@QueryParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(
                LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final Collection<LoanApplicationReferenceData> loanApplicationReferenceDatas = this.loanApplicationReferenceReadPlatformService
                .retrieveAll(clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanApplicationReferenceDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{loanApplicationReferenceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("loanApplicationReferenceId") final Long loanApplicationReferenceId,
            @QueryParam("command") final String commandParam, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(
                LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (is(commandParam, "loanapplicationcharges")) {
            final Collection<LoanApplicationChargeData> loanApplicationChargeData = this.loanApplicationReferenceReadPlatformService
                    .retrieveChargesByLoanAppRefId(loanApplicationReferenceId);
            return this.toApiJsonSerializer.serialize(settings, loanApplicationChargeData);
        } else if (is(commandParam, "approveddata")) {
            final LoanApplicationSanctionData loanApplicationSanctionData = this.loanApplicationReferenceReadPlatformService
                    .retrieveSanctionDataByLoanAppRefId(loanApplicationReferenceId);
            return this.toApiJsonSerializer.serialize(settings, loanApplicationSanctionData);
        }
        final LoanApplicationReferenceData loanApplicationReferenceData = this.loanApplicationReferenceReadPlatformService
                .retrieveOne(loanApplicationReferenceId);
        return this.toApiJsonSerializer.serialize(settings, loanApplicationReferenceData);
    }

    @PUT
    @Path("{loanApplicationReferenceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("loanApplicationReferenceId") final Long loanApplicationReferenceId, final String apiRequestBodyAsJson,
            @QueryParam("command") final String commandParam) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;

        if (is(commandParam, "requestforapproval")) {
            final CommandWrapper commandRequest = builder.requestForApprovalLoanApplicationReference(loanApplicationReferenceId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "reject")) {
            final CommandWrapper commandRequest = builder.rejectLoanApplicationReference(loanApplicationReferenceId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "approve")) {
            final CommandWrapper commandRequest = builder.approveLoanApplicationReference(loanApplicationReferenceId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undoapprove")) {
            final CommandWrapper commandRequest = builder.undoApproveLoanApplicationReference(loanApplicationReferenceId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "disburse")) {
            final CommandWrapper commandRequest = builder.disburseLoanApplicationReference(loanApplicationReferenceId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else {
            final CommandWrapper commandRequest = builder.updateLoanApplicationReference(loanApplicationReferenceId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }
        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{loanApplicationReferenceId}/creditbureaureport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String creditBureauReport(@PathParam("loanApplicationReferenceId") final Long loanApplicationReferenceId,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(
                LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final OtherInstituteLoansSummaryData otherInstituteLoansSummaryData = this.creditBureauCheckService
                .getCreditBureauDataForLoanApplication(loanApplicationReferenceId);
        return this.toApiJsonSerializer.serialize(settings, otherInstituteLoansSummaryData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{loanApplicationReferenceId}/otherinstituteloanssummary")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String creditBureauReportFileContant(@PathParam("loanApplicationReferenceId") final Long loanApplicationReferenceId,
            @PathParam("sourceId") final Long sourceId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(
                LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final OtherInstituteLoansSummaryData otherInstituteLoansSummaryData = this.creditBureauCheckService.getOtherInstituteLoansSummary(
                loanApplicationReferenceId, sourceId);
        return this.toApiJsonSerializer.serialize(settings, otherInstituteLoansSummaryData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{loanApplicationReferenceId}/creditbureaureportfile")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getCreditBureauReportFileContent(@PathParam("loanApplicationReferenceId") final Long loanApplicationReferenceId,
            @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(
                LoanApplicationReferenceApiConstants.LOANAPPLICATIONREFERENCE_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final CreditBureauFileContentData creditBureauFileContentData = this.creditBureauCheckService
                .getCreditBureauReportFileContent(loanApplicationReferenceId);
        return this.toApiJsonSerializer.serialize(settings, creditBureauFileContentData);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
