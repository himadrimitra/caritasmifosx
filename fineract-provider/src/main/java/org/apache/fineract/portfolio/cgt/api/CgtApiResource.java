package org.apache.fineract.portfolio.cgt.api;

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
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.cgt.data.CgtData;
import org.apache.fineract.portfolio.cgt.service.CgtReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/cgt")
@Component
@Scope("singleton")
public class CgtApiResource {
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<CgtData> cgtApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final CgtReadPlatformService cgtReadPlatformService;
	
	@Autowired
	public CgtApiResource(final PlatformSecurityContext context, final ToApiJsonSerializer cgtApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final CgtReadPlatformService cgtReadPlatformService) {
		this.context = context;
		this.cgtApiJsonSerializer = cgtApiJsonSerializer;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.cgtReadPlatformService = cgtReadPlatformService;
	}
	
	@GET
	@Path("{cgtId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveCgt(@PathParam("cgtId") final Long cgtId, @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(CgtApiConstants.CGT_RESOURCE_NAME);
		final CgtData cgtData = this.cgtReadPlatformService.retrievetCgtDataById(cgtId);
        return cgtApiJsonSerializer.serialize(cgtData);
        
    }
	
	@GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllCgt(@Context final UriInfo uriInfo, @QueryParam("entityId") final Integer entityId) {
		this.context.authenticatedUser().validateHasReadPermission(CgtApiConstants.CGT_RESOURCE_NAME);
		final Collection<CgtData> cgtDatas = this.cgtReadPlatformService.retrievetAllCgtDataByEntityId(entityId);
        return cgtApiJsonSerializer.serialize(cgtDatas);
        
    }

	@GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@QueryParam("entityId") final Long entityId, @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(CgtApiConstants.CGT_RESOURCE_NAME);
		final CgtData cgtData = this.cgtReadPlatformService.retrievetTemplateDataOfEntity(entityId);
		return cgtApiJsonSerializer.serialize(cgtData);

    }
	
	@POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createCgt(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
	        .createCgt() //
			.withJson(apiRequestBodyAsJson) //
	        .build(); //
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		
		return cgtApiJsonSerializer.serialize(result);
		
    }

	@PUT
    @Path("{cgtId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateCgt(@PathParam("cgtId") final Long cgtId, @QueryParam("action") final String action, final String apiRequestBodyAsJson) {
		
		CommandWrapper commandRequest = null;
		if (is(action, CgtApiConstants.cgtStatusRejectParamName)) {
			commandRequest = new CommandWrapperBuilder().rejectCgt(cgtId).withJson(apiRequestBodyAsJson).build();
		}else if(is(action, CgtApiConstants.cgtStatusCompleteParamName)){
			commandRequest = new CommandWrapperBuilder().completeCgt(cgtId).withJson(apiRequestBodyAsJson).build();
		}else{
			commandRequest = new CommandWrapperBuilder().updateCgt(cgtId).withJson(apiRequestBodyAsJson).build();
		}
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		
		return cgtApiJsonSerializer.serialize(result);

    }
	
	private boolean is(final String action, final String commandValue) {
        return StringUtils.isNotBlank(action) && action.trim().equalsIgnoreCase(commandValue);
    }
	
}
