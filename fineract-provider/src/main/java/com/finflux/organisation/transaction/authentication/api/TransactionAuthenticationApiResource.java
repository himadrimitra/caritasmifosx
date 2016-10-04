package com.finflux.organisation.transaction.authentication.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;
import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationReadPlatformService;
import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationService;

@Path("/transactions/authentication")
@Component
@Scope("singleton")
public class TransactionAuthenticationApiResource {
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<Object> toApiJsonSerializer;
	private final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	private final TransactionAuthenticationService transactionAuthenticationService;

	@Autowired
	public TransactionAuthenticationApiResource(final PlatformSecurityContext context,
			final ToApiJsonSerializer<Object> toApiJsonSerializer,
			final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final TransactionAuthenticationService transactionAuthenticationService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.transactionAuthenticationReadPlatformService = transactionAuthenticationReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.transactionAuthenticationService = transactionAuthenticationService;
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveTemplate(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser();
		TransactionAuthenticationData authenticationData = this.transactionAuthenticationReadPlatformService
				.retriveTemplate();
		return this.toApiJsonSerializer.serialize(authenticationData);
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriceAllTransactionAuthentications(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(TransactionAuthenticationApiConstants.READ_TRANSACTIONAUTHENTICATIONSERVICE);
		Collection<TransactionAuthenticationData> authenticationDatas = this.transactionAuthenticationReadPlatformService
				.retriveAllTransactionAuthenticationDeatails();
		return this.toApiJsonSerializer.serialize(authenticationDatas);
	}

	@POST
	@Path("generate/otp")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String generateOTP(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {
		this.context.authenticatedUser().validateHasPermissionTo(TransactionAuthenticationApiConstants.GENERATE_OTP);
		Object otpResponse = this.transactionAuthenticationService.sendOtpForTheCLient(apiRequestBodyAsJson);
		return this.toApiJsonSerializer.serialize(otpResponse);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createTransactionToAuthenticate(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(TransactionAuthenticationApiConstants.CREATE_TRANSACTIONAUTHENTICATIONSERVICE);
		final CommandWrapper commandWrapper = new CommandWrapperBuilder().createTransactionAuthenticationService()
				.withJson(apiRequestBodyAsJson).build();

		final CommandProcessingResult processingResult = this.commandsSourceWritePlatformService
				.logCommandSource(commandWrapper);

		return this.toApiJsonSerializer.serialize(processingResult);
	}

	@GET
	@Path("{transactionAuthenticationId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retreiveOneTransactionAuthenticationDetail(@Context final UriInfo uriInfo,
			@PathParam("transactionAuthenticationId") final Long transactionAuthenticationId) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(TransactionAuthenticationApiConstants.READ_TRANSACTIONAUTHENTICATIONSERVICE);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		final TransactionAuthenticationData transactionAuthentication = this.transactionAuthenticationReadPlatformService
				.retrieveOneById(transactionAuthenticationId);
		return this.toApiJsonSerializer.serialize(settings, transactionAuthentication,
				TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATIOM_SERVICE_RESPONSE);
	}

	@PUT
	@Path("{transactionAuthenticationId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateTransactionAuthentication(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson,
			@PathParam("transactionAuthenticationId") final Long transactionAuthenticationId) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(TransactionAuthenticationApiConstants.UPDATE_TRANSACTIONAUTHENTICATIONSERVICE);
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
				.updateTransactionAuthenticationService(transactionAuthenticationId).withJson(apiRequestBodyAsJson)
				.build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("{transactionAuthenticationId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteTransactionAuthentication(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson,
			@PathParam("transactionAuthenticationId") final Long transactionAuthenticationId) {
		this.context.authenticatedUser()
				.validateHasPermissionTo(TransactionAuthenticationApiConstants.DELETE_TRANSACTIONAUTHENTICATIONSERVICE);
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
				.deleteTransactionAuthenticationService(transactionAuthenticationId).withJson(apiRequestBodyAsJson)
				.build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

}
