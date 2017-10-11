package com.finflux.infrastructure.external.authentication.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.exception.ExternalAuthenticationServiceNotFoundException;

@Service
public class SecondaryAuthenticationServiceRepositoryWrapper {
	private final SecondaryAuthenticationServiceRepository repository;

	@Autowired
	private SecondaryAuthenticationServiceRepositoryWrapper(SecondaryAuthenticationServiceRepository repository) {
		this.repository = repository;
	}

	public SecondaryAuthenticationService findOneWithNotFoundDetection(final Long id) {
		final SecondaryAuthenticationService authenticationService = this.repository.findOne(id);
		if (authenticationService == null) {
			throw new ExternalAuthenticationServiceNotFoundException(id);
		}
		return authenticationService;
	}

	public void save(SecondaryAuthenticationService authenticationService) {
		this.repository.save(authenticationService);
	}
}
