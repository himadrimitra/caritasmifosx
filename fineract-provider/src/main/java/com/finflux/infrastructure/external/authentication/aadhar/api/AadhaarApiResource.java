package com.finflux.infrastructure.external.authentication.aadhar.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aadhaarconnect.bridge.gateway.model.OtpResponse;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarDataValidator;
import com.finflux.infrastructure.external.authentication.aadhar.service.AadhaarBridgeProvidedService;

@Path("/aadhaar")
@Component
@Scope("singleton")
public class AadhaarApiResource {
	private final PlatformSecurityContext context;
	private final AadhaarBridgeProvidedService readPlatformService;
	private final AadhaarDataValidator validator;
	private final ToApiJsonSerializer<Object> toApiJsonSerializer;;

	@Autowired
	public AadhaarApiResource(final PlatformSecurityContext context,
			final AadhaarBridgeProvidedService readPlatformService, final AadhaarDataValidator validator,
			final ToApiJsonSerializer<Object> toApiJsonSerializer) {
		this.context = context;
		this.readPlatformService = readPlatformService;
		this.validator = validator;
		this.toApiJsonSerializer = toApiJsonSerializer;
	}

	@POST
	@Path("/otp")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String generateOtp(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {
		context.authenticatedUser().validateHasPermissionTo("GENERATE_OTP");
		this.validator.validateJsonForGenerateOtp(apiRequestBodyAsJson);
		final OtpResponse otpResponse = this.readPlatformService.processOtpRequest(apiRequestBodyAsJson);
		return this.toApiJsonSerializer.serialize(otpResponse);
	}

	@POST
	@Path("/kyc")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getEKyc(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {
		context.authenticatedUser().validateHasPermissionTo("READ_KYC_DETAILS");
		return this.readPlatformService.processKycRequest(apiRequestBodyAsJson);
	}
}
