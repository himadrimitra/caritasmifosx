package org.apache.fineract.portfolio.deduplication.api;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.deduplication.data.DeduplicationData;
import org.apache.fineract.portfolio.deduplication.service.DeDuplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

@Path("/clientdedup/weightages")
@Component
@Scope("singleton")
public class DeduplicationApiResource {

        private final PlatformSecurityContext context;
        private final ApiRequestParameterHelper apiRequestParameterHelper;
        private final DefaultToApiJsonSerializer toApiJsonSerializer;
        private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
        private final DeDuplicationService deDuplicationService;

        @Autowired
        public DeduplicationApiResource(final PlatformSecurityContext context,
                final ApiRequestParameterHelper apiRequestParameterHelper,
                final DefaultToApiJsonSerializer toApiJsonSerializer,
                final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                final DeDuplicationService deDuplicationService){

                this.context = context;
                this.apiRequestParameterHelper = apiRequestParameterHelper;
                this.toApiJsonSerializer = toApiJsonSerializer;
                this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
                this.deDuplicationService = deDuplicationService;
        }

        @GET
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retreiveDedupWeightages(@Context final UriInfo uriInfo) {
                this.context.authenticatedUser().validateHasReadPermission(DeduplicationApiConstants.RESOURCE_NAME);
                final Collection<DeduplicationData> deduplicationDatas = this.deDuplicationService.getDedupWeightages();
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, deduplicationDatas);
        }

        @PUT
        @Path("{legalForm}")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String update(@PathParam("legalForm") final Long legalForm, final String apiRequestBodyAsJson) {
                final CommandWrapper commandRequest = new CommandWrapperBuilder().updateDedupWeightage(legalForm)
                        .withJson(apiRequestBodyAsJson).build();
                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }
}
