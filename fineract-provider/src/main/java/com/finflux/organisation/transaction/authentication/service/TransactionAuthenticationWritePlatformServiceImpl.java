package com.finflux.organisation.transaction.authentication.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationService;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationServiceRepositoryWrapper;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationDataAssembler;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationDataValidator;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthentication;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthenticationRepositoryWrapper;

@Service
public class TransactionAuthenticationWritePlatformServiceImpl
		implements TransactionAuthenticationWritePlatformService {

	private final TransactionAuthenticationDataValidator dataValidator;
	private final TransactionAuthenticationDataAssembler dataAssembler;
	private final TransactionAuthenticationRepositoryWrapper repository;
	private final PlatformSecurityContext context;
	private final SecondaryAuthenticationServiceRepositoryWrapper secondaryAuthenticationServiceRepository;
	private final PaymentTypeRepositoryWrapper PaymentTypeRepository;

	@Autowired
	public TransactionAuthenticationWritePlatformServiceImpl(final TransactionAuthenticationDataValidator dataValidator,
			final TransactionAuthenticationDataAssembler dataAssembler,
			final TransactionAuthenticationRepositoryWrapper repository, final PlatformSecurityContext context,
			final SecondaryAuthenticationServiceRepositoryWrapper secondaryAuthenticationServiceRepository,
			final PaymentTypeRepositoryWrapper PaymentTypeRepository) {
		this.dataValidator = dataValidator;
		this.dataAssembler = dataAssembler;
		this.repository = repository;
		this.context = context;
		this.secondaryAuthenticationServiceRepository = secondaryAuthenticationServiceRepository;
		this.PaymentTypeRepository = PaymentTypeRepository;
	}

	@Override
	public CommandProcessingResult createTransactionAuthentication(JsonCommand command) {
		this.dataValidator.validateForCreate(command.json());
		this.dataAssembler.isAuthenticationServiceActive(command);
		this.dataAssembler.isValidPaymentType(command);
		this.dataAssembler.isUniqueRule(command);
		final TransactionAuthentication transactionAuthentication = this.dataAssembler
				.transactionAuthenticationDataAssembler(command);
		try {
			this.repository.save(transactionAuthentication);
			return new CommandProcessingResultBuilder().withEntityId(transactionAuthentication.getId()).build();
		} catch (final DataIntegrityViolationException dve) {
			handleTransactionAuthenticationServiceIntegrityIssue(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handleTransactionAuthenticationServiceIntegrityIssue(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		throw new PlatformDataIntegrityException("error.msg.transaction.auth.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource Transaction Authentication: " + realCause.getMessage());
	}

	@Override
	public CommandProcessingResult updateTransactionAuthentication(Long transactionAuthenticationId,
			JsonCommand command) {

		try {
			AppUser currentUser = this.context.authenticatedUser();
			TransactionAuthentication transactionAuthentication = this.repository
					.findOneWithNotFoundDetection(transactionAuthenticationId);
			this.dataValidator.validateTransactionTypeForUpdate(command, transactionAuthentication);
			this.dataAssembler.isValidPaymentType(command);
			this.dataAssembler.isUniqueRuleForUpdate(command, transactionAuthentication);
			this.dataAssembler.validateAuthenticationType(command);
			final SecondaryAuthenticationService secondaryAuthenticationType = this.secondaryAuthenticationServiceRepository
					.findOneWithNotFoundDetection(command
							.longValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID));
			final PaymentType paymentType = this.PaymentTypeRepository.findOneWithNotFoundDetection(
					command.longValueOfParameterNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID));
			final Map<String, Object> actualChanges = transactionAuthentication.update(command, currentUser,
					secondaryAuthenticationType, paymentType);
			return new CommandProcessingResultBuilder() //
					.withEntityId(transactionAuthentication.getId()).with(actualChanges) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			handleExternalAuthenticationServiceIntegrityIssue(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handleExternalAuthenticationServiceIntegrityIssue(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		throw new PlatformDataIntegrityException("error.msg.transaction.auth.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource transaction Authentication: " + realCause.getMessage());
	}

	@Override
	public CommandProcessingResult deleteTransactionAuthentication(final Long transactionAuthenticationId) {

		return this.repository.deletTransactionAuthentication(transactionAuthenticationId);
	}

}
