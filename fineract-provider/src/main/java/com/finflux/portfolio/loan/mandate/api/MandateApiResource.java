package com.finflux.portfolio.loan.mandate.api;

import com.finflux.portfolio.loan.mandate.data.MandateData;
import com.finflux.portfolio.loan.mandate.exception.CommandQueryParamExpectedException;
import com.finflux.portfolio.loan.mandate.exception.InvalidCommandQueryParamException;
import com.finflux.portfolio.loan.mandate.service.MandateReadPlatformService;
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

@Path("/loans/{loanId}/mandates")
@Component
@Scope("singleton")
public class MandateApiResource {

        private final PlatformSecurityContext context;
        private final ApiRequestParameterHelper apiRequestParameterHelper;
        private final DefaultToApiJsonSerializer toApiJsonSerializer;
        private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
        private final MandateReadPlatformService readPlatformService;

        @Autowired
        public MandateApiResource(final PlatformSecurityContext context,
                final ApiRequestParameterHelper apiRequestParameterHelper,
                final DefaultToApiJsonSerializer toApiJsonSerializer,
                final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                final MandateReadPlatformService readPlatformService) {
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
        public String retrieveTemplate(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
                @Context final UriInfo uriInfo) {

                this.context.authenticatedUser().validateHasReadPermission(MandateApiConstants.RESOURCE_NAME);

                MandateData template = this.readPlatformService.retrieveTemplate(loanId, commandParam);

                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, template, MandateApiConstants.ALLOWED_RESPONSE_PARAMS);
        }

        @GET
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveMandates(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {

                this.context.authenticatedUser().validateHasReadPermission(MandateApiConstants.RESOURCE_NAME);

                Collection<MandateData> mandates = this.readPlatformService.retrieveMandates(loanId);

                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, mandates, MandateApiConstants.ALLOWED_RESPONSE_PARAMS);
        }

        @POST
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String processMandate(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
                final String apiRequestBodyAsJson) {
                CommandWrapper commandRequest = null;
                if(null == commandParam){
                        throw new CommandQueryParamExpectedException();
                } else {
                        switch (commandParam.trim().toUpperCase()){
                                case "CREATE":
                                        commandRequest = new CommandWrapperBuilder().createMandate(loanId)
                                                .withJson(apiRequestBodyAsJson).build();
                                        break;
                                case "UPDATE":
                                        commandRequest = new CommandWrapperBuilder().updateMandate(loanId)
                                                .withJson(apiRequestBodyAsJson).build();
                                        break;
                                case "CANCEL":
                                        commandRequest = new CommandWrapperBuilder().cancelMandate(loanId)
                                                .withJson(apiRequestBodyAsJson).build();
                                        break;
                                default:
                                        throw new InvalidCommandQueryParamException(commandParam);
                        }
                }
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

        @GET
        @Path("{mandateId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveMandates(@PathParam("loanId") final Long loanId, @PathParam("mandateId") final Long mandateId,
                @Context final UriInfo uriInfo) {

                this.context.authenticatedUser().validateHasReadPermission(MandateApiConstants.RESOURCE_NAME);

                MandateData mandate = this.readPlatformService.retrieveMandate(loanId, mandateId);

                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, mandate, MandateApiConstants.ALLOWED_RESPONSE_PARAMS);
        }

        @PUT
        @Path("{mandateId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String processMandate(@PathParam("loanId") final Long loanId, @PathParam("mandateId") final Long mandateId,
                final String apiRequestBodyAsJson) {
                CommandWrapper commandRequest = new CommandWrapperBuilder().editMandate(loanId, mandateId)
                                                .withJson(apiRequestBodyAsJson).build();
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

        @DELETE
        @Path("{mandateId}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String processMandate(@PathParam("loanId") final Long loanId, @PathParam("mandateId") final Long mandateId) {
                CommandWrapper commandRequest = new CommandWrapperBuilder().deleteMandate(loanId, mandateId).build();
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }
}
