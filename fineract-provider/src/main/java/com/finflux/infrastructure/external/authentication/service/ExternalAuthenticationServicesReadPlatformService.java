package com.finflux.infrastructure.external.authentication.service;

import java.util.Collection;
import org.apache.fineract.infrastructure.core.service.Page;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;

public interface ExternalAuthenticationServicesReadPlatformService {

	public Collection<ExternalAuthenticationServiceData> getOnlyActiveExternalAuthenticationServices();

	public Page<ExternalAuthenticationServiceData> retrieveAllExternalAuthenticationServices();

	public ExternalAuthenticationServiceData retrieveOneExternalAuthenticationService(
			Long externalAuthenticationServiceId);

	public ExternalAuthenticationServiceData retrieveOneActiveExternalAuthenticationService(
			final Long externalAuthenticationServiceId);

}
