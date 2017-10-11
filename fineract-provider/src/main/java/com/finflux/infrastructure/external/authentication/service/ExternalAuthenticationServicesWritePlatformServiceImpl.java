package com.finflux.infrastructure.external.authentication.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationService;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationServiceRepositoryWrapper;
import com.finflux.infrastructure.external.authentication.serialization.ExternalAuthenticationServicesDataValidator;

@Service
public class ExternalAuthenticationServicesWritePlatformServiceImpl
		implements ExternalAuthenticationServicesWritePlatformService {

	private final PlatformSecurityContext context;
	private final SecondaryAuthenticationServiceRepositoryWrapper repositoryWrapper;
	private final ExternalAuthenticationServicesDataValidator authenticationServiceValidatory;

	@Autowired
	private ExternalAuthenticationServicesWritePlatformServiceImpl(final PlatformSecurityContext context,
			final SecondaryAuthenticationServiceRepositoryWrapper repositoryWrapper,
			final ExternalAuthenticationServicesDataValidator authenticationServiceValidatory) {
		this.context = context;
		this.repositoryWrapper = repositoryWrapper;
		this.authenticationServiceValidatory = authenticationServiceValidatory;
	}

	@Transactional
	@Override
	public CommandProcessingResult updateTransactionAuthenticationService(final Long serviceId,
			final JsonCommand command) {
		this.authenticationServiceValidatory.validateForUpdate(command);
		return updateAuthenticationService(serviceId, command);
	}

	private CommandProcessingResult updateAuthenticationService(final Long serviceId, final JsonCommand command) {
		try {
			AppUser currentUser = this.context.authenticatedUser();
			final SecondaryAuthenticationService secondaryAuthenticationService = this.repositoryWrapper
					.findOneWithNotFoundDetection(serviceId);
			final Map<String, Object> actualChanges = secondaryAuthenticationService.update(command, currentUser);
			return new CommandProcessingResultBuilder() //
					.withEntityId(secondaryAuthenticationService.getId()).with(actualChanges) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			handleExternalAuthenticationServiceIntegrityIssue(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handleExternalAuthenticationServiceIntegrityIssue(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		throw new PlatformDataIntegrityException("error.msg.external.auth.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource External Authentication: " + realCause.getMessage());
	}

}
