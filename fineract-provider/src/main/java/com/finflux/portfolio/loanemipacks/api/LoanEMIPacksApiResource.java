package com.finflux.portfolio.loanemipacks.api;

import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;
import com.finflux.portfolio.loanemipacks.service.LoanEMIPacksReadPlatformService;
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
import java.util.Collection;

@Path("/loanemipacks")
@Component
@Scope("singleton")
public class LoanEMIPacksApiResource {

        private final PlatformSecurityContext context;
        private final ApiRequestParameterHelper apiRequestParameterHelper;
        private final DefaultToApiJsonSerializer toApiJsonSerializer;
        private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
        private final LoanEMIPacksReadPlatformService readPlatformService;

        @Autowired
        public LoanEMIPacksApiResource(final PlatformSecurityContext context,
                final ApiRequestParameterHelper apiRequestParameterHelper,
                final DefaultToApiJsonSerializer toApiJsonSerializer,
                final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                final LoanEMIPacksReadPlatformService readPlatformService) {

                this.context = context;
                this.apiRequestParameterHelper = apiRequestParameterHelper;
                this.toApiJsonSerializer = toApiJsonSerializer;
                this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
                this.readPlatformService = readPlatformService;
        }

        @GET
        @Path("template")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveTemplateActiveLoanProductsWithoutEMIPacks(@Context final UriInfo uriInfo) {
                this.context.authenticatedUser().validateHasReadPermission(LoanEMIPacksApiConstants.RESOURCE_NAME);
                final Collection<LoanEMIPackData> templateData = this.readPlatformService.retrieveActiveLoanProductsWithoutEMIPacks();
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, templateData);
        }

        @GET
        @Path("{loanProductId}/template")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveEMIPackTemplate(@PathParam("loanProductId") final Long loanProductId, @Context final UriInfo uriInfo) {
                this.context.authenticatedUser().validateHasReadPermission(LoanEMIPacksApiConstants.RESOURCE_NAME);
                final LoanEMIPackData templateData = this.readPlatformService.retrieveEMIPackTemplate(loanProductId);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, templateData);
        }

        @GET
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveActiveLoanProductsWithEMIPacks(@Context final UriInfo uriInfo) {
                this.context.authenticatedUser().validateHasReadPermission(LoanEMIPacksApiConstants.RESOURCE_NAME);
                final Collection<LoanEMIPackData> data = this.readPlatformService.retrieveActiveLoanProductsWithEMIPacks();
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, data);
        }

        @GET
        @Path("{loanProductId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveEMIPackDetails(@PathParam("loanProductId") final Long loanProductId, @Context final UriInfo uriInfo) {
                this.context.authenticatedUser().validateHasReadPermission(LoanEMIPacksApiConstants.RESOURCE_NAME);
                final Collection<LoanEMIPackData> data = this.readPlatformService.retrieveEMIPackDetails(loanProductId);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, data);
        }

        @POST
        @Path("{loanProductId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String create(@PathParam("loanProductId") final Long loanProductId, final String apiRequestBodyAsJson) {
                final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanEMIPack(loanProductId)
                        .withJson(apiRequestBodyAsJson).build();
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

        @GET
        @Path("{loanProductId}/{loanEMIPackId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveEMIPackDetails(@PathParam("loanProductId") final Long loanProductId,
                @PathParam("loanEMIPackId") final Long loanEMIPackId, @Context final UriInfo uriInfo) {
                this.context.authenticatedUser().validateHasReadPermission(LoanEMIPacksApiConstants.RESOURCE_NAME);
                final LoanEMIPackData data = this.readPlatformService.retrieveEMIPackDetails(loanProductId, loanEMIPackId);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, data);
        }

        @PUT
        @Path("{loanProductId}/{loanEMIPackId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String update(@PathParam("loanProductId") final Long loanProductId,
                @PathParam("loanEMIPackId") final Long loanEMIPackId, final String apiRequestBodyAsJson) {
                final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLoanEMIPack(loanProductId, loanEMIPackId)
                        .withJson(apiRequestBodyAsJson).build();
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

        @DELETE
        @Path("{loanProductId}/{loanEMIPackId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String delete(@PathParam("loanProductId") final Long loanProductId,
                @PathParam("loanEMIPackId") final Long loanEMIPackId, final String apiRequestBodyAsJson) {
                final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteLoanEMIPack(loanProductId, loanEMIPackId)
                        .withJson(apiRequestBodyAsJson).build();
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

}
