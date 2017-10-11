package com.finflux.risk.existingloans.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.risk.existingloans.data.ExistingLoanData;
import com.finflux.risk.existingloans.data.ExistingLoanTemplateData;
import com.finflux.risk.existingloans.service.ExistingLoanReadPlatformService;

@Path("/clients/{clientId}/existingloans")
@Component
@Scope("singleton")
public class ExistingLoanApiResource {

    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ToApiJsonSerializer<ExistingLoanTemplateData> toApiJsonSerializerTemplate;
    private final ToApiJsonSerializer<ExistingLoanData> toApiJsonSerializer;
    private final ExistingLoanReadPlatformService existingLoanReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public ExistingLoanApiResource(final PlatformSecurityContext context,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ToApiJsonSerializer<ExistingLoanTemplateData> toApiJsonSerializerTemplate,
            final ToApiJsonSerializer<ExistingLoanData> toApiJsonSerializer,
            final ExistingLoanReadPlatformService existingLoanReadPlatformService, final ApiRequestParameterHelper apiRequestParameterHelper) {
        this.context = context;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiJsonSerializerTemplate = toApiJsonSerializerTemplate;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.existingLoanReadPlatformService = existingLoanReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveTemplate(@Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ExistingLoanApiConstants.EXISTINGLOAN_RESOURCE_NAME);
        final ExistingLoanTemplateData existingLoanTemplateData = this.existingLoanReadPlatformService.retriveTemplate();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerTemplate.serialize(settings, existingLoanTemplateData);
    }

    @GET
    @Path("{existingloanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveExistingLoan(@PathParam("clientId") final Long clientId,@PathParam("existingloanId") final Long existingloanId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ExistingLoanApiConstants.EXISTINGLOAN_RESOURCE_NAME);
        ExistingLoanData existingLoanData = this.existingLoanReadPlatformService.retrieveOne(clientId,existingloanId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, existingLoanData,
                ExistingLoanApiConstants.EXISTING_LOAN_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retriveAllExistingLoan(@PathParam("clientId") final Long clientId,
            @QueryParam("loanApplicationId") final Long loanApplicationId, @QueryParam("loanId") final Long loanId,
            @QueryParam("trancheDisbursalId") final Long trancheDisbursalId, @Context final UriInfo uriInfo) {
        /* parameter to be added for filteriing the record */
        this.context.authenticatedUser().validateHasReadPermission(ExistingLoanApiConstants.EXISTINGLOAN_RESOURCE_NAME);
        List<ExistingLoanData> existingLoanData = this.existingLoanReadPlatformService.retriveAll(clientId, loanApplicationId, loanId,
                trancheDisbursalId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, existingLoanData,
                ExistingLoanApiConstants.EXISTING_LOAN_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String storeExistingLoan(final String apiRequestBodyAsJson,@PathParam("clientId") final Long clientId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .storeExistingLoan(clientId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{existingloanId}")    
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateExistingLoan(@PathParam("clientId") final Long clientId,@PathParam("existingloanId") final Long existingloanId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateExistingLoan(clientId,existingloanId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{existingloanId}")    
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("clientId") final Long clientId,@PathParam("existingloanId") final Long existingloanId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteExistingLoan(clientId,existingloanId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
