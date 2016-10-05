package com.finflux.infrastructure.external.authentication.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;
import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServicesDataConstants;
import com.finflux.infrastructure.external.authentication.service.ExternalAuthenticationServicesReadPlatformService;

@Path("/external/authentications")
@Component
@Scope("singleton")
public class ExternalAuthenticationApiResource {
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<Object> toApiJsonSerializer;
	private final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	@Autowired
	public ExternalAuthenticationApiResource(final PlatformSecurityContext context,
			final ToApiJsonSerializer<Object> toApiJsonSerializer,
			final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService,
			final FromJsonHelper fromJsonHelper, final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.externalAuthenticationServicesReadPlatformService = externalAuthenticationServicesReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}

	@GET
	@Path("services")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllTransactionServices(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasPermissionTo(ExternalAuthenticationServicesDataConstants.READ_AUTHENTICATIONSERVICE);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		Page<ExternalAuthenticationServiceData> services = this.externalAuthenticationServicesReadPlatformService
				.retrieveAllExternalAuthenticationServices();
		return this.toApiJsonSerializer.serialize(settings, services,
				ExternalAuthenticationServicesDataConstants.TRANSACTION_AUTHENTICATION_SERVICES_RESPONSE);
	}

	@GET
	@Path("services/{serviceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveOneAuthenticationService(@Context final UriInfo uriInfo,
			@PathParam("serviceId") final Long serviceId) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(ExternalAuthenticationServicesDataConstants.READ_AUTHENTICATIONSERVICE);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		ExternalAuthenticationServiceData serviceData = this.externalAuthenticationServicesReadPlatformService
				.retrieveOneExternalAuthenticationService(serviceId);
		return this.toApiJsonSerializer.serialize(settings, serviceData,
				ExternalAuthenticationServicesDataConstants.TRANSACTION_AUTHENTICATION_SERVICES_RESPONSE);
	}

	@PUT
	@Path("services/{serviceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateAuthenticationService(@Context final UriInfo uriInfo,
			@PathParam("serviceId") final Long serviceId, final String apiRequestBodyAsJson) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(ExternalAuthenticationServicesDataConstants.UPDATE_AUTHENTICATIONSERVICE);
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
				.updateSecondaryAuthenticationService(serviceId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
}
