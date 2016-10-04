package com.finflux.organisation.transaction.authentication.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationService;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationServiceRepositoryWrapper;
import com.finflux.infrastructure.external.authentication.exception.ExternalAuthenticationServiceNotFoundException;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticaionTransactionTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthentication;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthenticationRepositoryWrapper;
import com.finflux.organisation.transaction.authentication.exception.InAcitiveExternalServiceexception;
import com.finflux.organisation.transaction.authentication.exception.TransactionAuthenticationRuleAlreadyExistException;
import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationReadPlatformService;
import com.google.gson.JsonElement;

@Service
public class TransactionAuthenticationDataAssembler {

	private final FromJsonHelper fromJsonHelper;
	private final PlatformSecurityContext context;
	// Second phase
	// use to get the role of the user to provide overriding to disburse loan using RoleReadPlatformService 
	private final SecondaryAuthenticationServiceRepositoryWrapper secondaryAuthenticationServiceRepository;
	private final PaymentTypeRepositoryWrapper paymentTypeRepository;
	private final TransactionAuthenticationReadPlatformService readPlatformService;

	@Autowired
	public TransactionAuthenticationDataAssembler(final FromJsonHelper fromJsonHelper,
			final PlatformSecurityContext context,
			final SecondaryAuthenticationServiceRepositoryWrapper secondaryAuthenticationServiceRepository,
			final PaymentTypeRepositoryWrapper paymentTypeRepository,
			final TransactionAuthenticationReadPlatformService readPlatformService,
			final TransactionAuthenticationRepositoryWrapper transactionAuthenticationRepositoryWrapper) {
		this.fromJsonHelper = fromJsonHelper;
		this.context = context;
		this.secondaryAuthenticationServiceRepository = secondaryAuthenticationServiceRepository;
		this.paymentTypeRepository = paymentTypeRepository;
		this.readPlatformService = readPlatformService;
	}

	public TransactionAuthentication transactionAuthenticationDataAssembler(final JsonCommand command) {
		AppUser currentUser = this.context.authenticatedUser();
		final JsonElement element = this.fromJsonHelper.parse(command.json());
		final Locale locale = this.fromJsonHelper.extractLocaleParameter(element.getAsJsonObject());
		final Integer portfolioTypeId = this.fromJsonHelper
				.extractIntegerNamed(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID, element, locale);
		final Integer transactionTypeId = this.fromJsonHelper
				.extractIntegerNamed(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID, element, locale);
		final Long paymentTypeId = this.fromJsonHelper
				.extractLongNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID, element);
		final BigDecimal amountGreaterThan = this.fromJsonHelper
				.extractBigDecimalNamed(TransactionAuthenticationApiConstants.AMOUNT, element, locale);
		final Long secondAppUserRoleId = this.fromJsonHelper
				.extractLongNamed(TransactionAuthenticationApiConstants.SECOUND_APP_USER_ROLE_ID, element);

		// SECOND PHASE
		// final RoleData roleData =
		// this.roleReadPlatformService.retrieveOne(secondAppUserRoleId);

		// second phase

		final Role role = null;
		boolean isSecondAppUserEnabled = false;
		if (this.fromJsonHelper.parameterExists(TransactionAuthenticationApiConstants.ENABLE_SECOND_APP_USER,
				element)) {
			isSecondAppUserEnabled = this.fromJsonHelper
					.extractBooleanNamed(TransactionAuthenticationApiConstants.ENABLE_SECOND_APP_USER, element);
		}
		final PaymentType paymentType = this.paymentTypeRepository.findOneWithNotFoundDetection(paymentTypeId);
		
		final Long authenticationTypeId = this.fromJsonHelper
				.extractLongNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID, element);
		final SecondaryAuthenticationService secondaryAuthenticationType = this.secondaryAuthenticationServiceRepository.findOneWithNotFoundDetection(authenticationTypeId);
		return TransactionAuthentication.newTransactionAuthentication(portfolioTypeId, transactionTypeId, paymentType,
				amountGreaterThan, role, isSecondAppUserEnabled, secondaryAuthenticationType, currentUser);

	}
	
	public void isAuthenticationServiceActive(final JsonCommand command) {
		final JsonElement element = this.fromJsonHelper.parse(command.json());
		final Long authenticationTypeId = this.fromJsonHelper.extractLongNamed("authenticationTypeId", element);
		final SecondaryAuthenticationService secondaryAuthenticationService = findSecondaryAuthenticationById(
				authenticationTypeId);
		if (secondaryAuthenticationService == null) {
			throw new ExternalAuthenticationServiceNotFoundException(authenticationTypeId);
		}
		if (!secondaryAuthenticationService.isActive()) {
			throw new InAcitiveExternalServiceexception(secondaryAuthenticationService.getName(), authenticationTypeId);
		}
	}
	
	public void isValidPaymentType(final JsonCommand command) {
		final JsonElement element = this.fromJsonHelper.parse(command.json());
		final Long paymentTypeId = this.fromJsonHelper
				.extractLongNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID, element);
		checkForValidPaymentType(paymentTypeId);
	}
	
	public SecondaryAuthenticationService findSecondaryAuthenticationById(final Long id) {
		return this.secondaryAuthenticationServiceRepository.findOneWithNotFoundDetection(id);
	}
	
	public void checkForValidPaymentType(final Long id) {
			this.paymentTypeRepository.findOneWithNotFoundDetection(id);
	}
	
	// check if the combination of( product type, transaction type, payment type
		// and amount) is unique
		public void isUniqueRule(final JsonCommand command) {
			final String json = command.json();
			final JsonElement element = this.fromJsonHelper.parse(json);
			final Locale locale = this.fromJsonHelper.extractLocaleParameter(element.getAsJsonObject());
			final Integer productTypeId = this.fromJsonHelper
					.extractIntegerNamed(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID, element, locale);
			final Integer transactionTypeId = this.fromJsonHelper
					.extractIntegerNamed(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID, element, locale);
			final Long paymentTypeId = this.fromJsonHelper
					.extractLongNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID, element);
			final BigDecimal amountGreaterThan = this.fromJsonHelper
					.extractBigDecimalNamed(TransactionAuthenticationApiConstants.AMOUNT, element, locale);
			findByProductTypeIdAndTransactionTypeIdAndPaymentTypeIdAndAmount(productTypeId, transactionTypeId,
					paymentTypeId, amountGreaterThan);
		}
		
		public void findByProductTypeIdAndTransactionTypeIdAndPaymentTypeIdAndAmount(final Integer potfolioType,
				final Integer transactionTypeId, final Long paymentTypeId, final BigDecimal amountGreaterThan) {
			final List<TransactionAuthenticationData> transactionAuthentication = this.readPlatformService
					.findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmount(potfolioType, transactionTypeId,
							paymentTypeId, amountGreaterThan);

			if (transactionAuthentication != null && transactionAuthentication.size() > 0) {

				final SupportedAuthenticationPortfolioTypes appliesTo = SupportedAuthenticationPortfolioTypes
						.fromInt(potfolioType);
				final SupportedAuthenticaionTransactionTypes supportedAuthenticaionTransactionTypes = SupportedAuthenticaionTransactionTypes.fromInt(transactionTypeId);

				throw new TransactionAuthenticationRuleAlreadyExistException(
						"ProductType :" + appliesTo + ", Transaction Type :" + supportedAuthenticaionTransactionTypes + ", payment Type ID : "
								+ paymentTypeId + ", Amount :" + amountGreaterThan);
			}
		}


		public void isUniqueRuleForUpdate(final JsonCommand command,
				final TransactionAuthentication transactionAuthentication) {
			JsonElement element = this.fromJsonHelper.parse(command.json());
			final Locale locale = this.fromJsonHelper.extractLocaleParameter(element.getAsJsonObject());
			Integer transactionTypeId;
			if (command.hasParameter(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID)) {
				transactionTypeId = this.fromJsonHelper.extractIntegerNamed("transactionTypeId", element, locale);
			} else {
				transactionTypeId = transactionAuthentication.getTransactionTypeId();
			}

			Integer productTypeId;
			if (command.hasParameter(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE)) {
				productTypeId = this.fromJsonHelper.extractIntegerNamed("productTypeId", element, locale);
			} else {
				productTypeId = transactionAuthentication.getPortfolioType();
			}

			Long paymentTypeId;
			if (command.hasParameter(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID)) {
				paymentTypeId = this.fromJsonHelper.extractLongNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID,
						element);
			} else {
				paymentTypeId = transactionAuthentication.getPaymentTypeId();
			}

			BigDecimal amountGreaterThan;
			if (command.hasParameter(TransactionAuthenticationApiConstants.AMOUNT)) {
				amountGreaterThan = this.fromJsonHelper.extractBigDecimalNamed(TransactionAuthenticationApiConstants.AMOUNT,
						element, locale);
			} else {
				amountGreaterThan = transactionAuthentication.getAmount();
			}

			Long authenticationTypeId = null;
			if (command.hasParameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)) {
				authenticationTypeId = command
						.longValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID);
			} else {
				authenticationTypeId = transactionAuthentication.getId();
			}

			findByProductTypeIdAndTransactionTypeIdAndPaymentTypeIdAndAmountAndAuthenticationTypeId(productTypeId,
					transactionTypeId, paymentTypeId, amountGreaterThan, authenticationTypeId);

		}
		
		public void findByProductTypeIdAndTransactionTypeIdAndPaymentTypeIdAndAmountAndAuthenticationTypeId(
				final Integer productTypeId, final Integer transactionTypeId, final Long paymentTypeId,
				final BigDecimal amountGreaterThan, final Long authenticationTypeId) {
			final List<TransactionAuthenticationData> transactionAuthentications = this.readPlatformService
					.findByPortfolioTypeAndTransactionTypeIdAndPaymentTypeIdAndAmountAndAuthenticationTypeId(productTypeId, transactionTypeId, paymentTypeId, amountGreaterThan, authenticationTypeId);
			if (transactionAuthentications != null && transactionAuthentications.size() > 0) {
				final SupportedAuthenticationPortfolioTypes appliesTo = SupportedAuthenticationPortfolioTypes
						.fromInt(productTypeId);
				final SupportedAuthenticaionTransactionTypes supportedAuthenticaionTransactionTypes = SupportedAuthenticaionTransactionTypes.fromInt(transactionTypeId);

				throw new TransactionAuthenticationRuleAlreadyExistException("ProductType :" + appliesTo
						+ ", Transaction Type :" + supportedAuthenticaionTransactionTypes + ", payment Type ID : " + paymentTypeId + ", Amount :"
						+ amountGreaterThan + " Authentication Type " + authenticationTypeId);
			}
		}
		
		public void validateAuthenticationType(final JsonCommand command){
			final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
			final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
					.resource(TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_RESOURCE_NAME);
			if (command.hasParameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)) {
				final Long authenticationTypeId = command.longValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID);
				dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)
						.value(authenticationTypeId).notNull().integerGreaterThanZero();
				if (authenticationTypeId > 0) {
					final SecondaryAuthenticationService secondaryAuthenticationService = findSecondaryAuthenticationById(
							authenticationTypeId);
					if (secondaryAuthenticationService == null) {
						dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)
								.failWithCode("secondary.authentication.service." + authenticationTypeId + "doesnot.exist",
										"Secondary Authentication service with id " + authenticationTypeId
												+ " does not exit");
					}
					if (!secondaryAuthenticationService.isActive()) {
						dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)
								.failWithCode("secondary.authentication.service." + authenticationTypeId + ".inactive",
										"Secondary Authentication service with id " + authenticationTypeId
												+ " is not active");
					}
				}
			}
			throwExceptionIfValidationWarningsExist(dataValidationErrors);
		}
		
		private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
			if (!dataValidationErrors.isEmpty()) {
				//
				throw new PlatformApiDataValidationException(dataValidationErrors);
			}
		}

}
