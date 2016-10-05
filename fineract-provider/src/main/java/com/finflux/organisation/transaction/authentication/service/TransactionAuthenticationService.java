package com.finflux.organisation.transaction.authentication.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.capture.model.common.LocationType;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationService;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationServiceRepositoryWrapper;
import com.finflux.infrastructure.external.authentication.service.GenerateOtpFactory;
import com.finflux.infrastructure.external.authentication.service.GenerateOtpService;
import com.finflux.infrastructure.external.authentication.service.SecondLevelAuthenticationService;
import com.finflux.infrastructure.external.authentication.service.SecondaryAuthenticationFactory;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.organisation.transaction.authentication.data.ClientDataForAuthentication;
import com.finflux.organisation.transaction.authentication.data.ClientDataForAuthenticationAssembler;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationDataValidator;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticaionTransactionTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthentication;
import com.finflux.organisation.transaction.authentication.domain.TransactionAuthenticationRepositoryWrapper;
import com.finflux.organisation.transaction.authentication.exception.InAcitiveExternalServiceexception;
import com.finflux.organisation.transaction.authentication.exception.OtpTypeNotSupported;
import com.finflux.organisation.transaction.authentication.exception.SecondaryAuthenticationFailedException;
import com.finflux.organisation.transaction.authentication.exception.TransactionAuthenticationMismatchException;
import com.google.gson.JsonElement;

@Service
public class TransactionAuthenticationService {
	private final BusinessEventNotifierService businessEventNotifierService;
	private final SecondaryAuthenticationFactory secondaryAuthenticationFactory;
	private final FromJsonHelper fromJsonHelper;
	private final SecondaryAuthenticationServiceRepositoryWrapper repository;
	private final TransactionAuthenticationRepositoryWrapper transactionAuthenticationRepository;
	private final ClientDataForAuthenticationAssembler clientData;
	private final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService;
	private final TransactionAuthenticationDataValidator transactionAuthenticationDataValidator;
	private final GenerateOtpFactory generateOtpFactory;

	@Autowired
	public TransactionAuthenticationService(final BusinessEventNotifierService businessEventNotifierService,
			final SecondaryAuthenticationFactory secondaryAuthenticationFactory, final FromJsonHelper fromJsonHelper,
			final SecondaryAuthenticationServiceRepositoryWrapper repository,
			final TransactionAuthenticationRepositoryWrapper transactionAuthenticationRepository,
			final ClientDataForAuthenticationAssembler clientData,
			final TransactionAuthenticationDataValidator transactionAuthenticationDataValidator,
			final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService,
			final GenerateOtpFactory generateOtpFactory) {
		this.businessEventNotifierService = businessEventNotifierService;
		this.secondaryAuthenticationFactory = secondaryAuthenticationFactory;
		this.fromJsonHelper = fromJsonHelper;
		this.repository = repository;
		this.transactionAuthenticationRepository = transactionAuthenticationRepository;
		this.clientData = clientData;
		this.transactionAuthenticationDataValidator = transactionAuthenticationDataValidator;
		this.transactionAuthenticationReadPlatformService = transactionAuthenticationReadPlatformService;
		this.generateOtpFactory = generateOtpFactory;
	}

	@PostConstruct
	public void registerForNotification() {
		this.businessEventNotifierService.addBusinessEventPreListners(BUSINESS_EVENTS.LOAN_DISBURSAL,
				new DisbursementAuthenticationEventListner());
	}

	private class DisbursementAuthenticationEventListner implements BusinessEventListner {

		@SuppressWarnings("unused")
		@Override
		public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
			if (loanEntity instanceof JsonCommand) {
				final JsonCommand jsonString = (JsonCommand) loanEntity;
				executeTransactionAuthenticationService(jsonString);
			}
		}

		@Override
		public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {

		}

	}

	public Object sendOtpForTheCLient(final String json) {
		final JsonElement element = this.fromJsonHelper.parse(json);

		final Long loanId = this.fromJsonHelper.extractLongNamed("loanId", element);
		final long authenticationRuleId = this.fromJsonHelper
				.extractLongNamed(TransactionAuthenticationApiConstants.AUTHENTICAION_RULE_ID, element);
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource(TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_RESOURCE_NAME);

		baseDataValidator.reset().parameter("loanId").value(loanId).longGreaterThanZero();
		baseDataValidator.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICAION_RULE_ID)
				.value(authenticationRuleId).longGreaterThanZero();
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}

		TransactionAuthentication transactionAuthentication = this.transactionAuthenticationRepository
				.findOneWithNotFoundDetection(authenticationRuleId);

		SecondaryAuthenticationService secondaryAuthenticationService = this.repository
				.findOneWithNotFoundDetection(transactionAuthentication.getAuthenticationTypeId());
		if (secondaryAuthenticationService.isActive()) {
			if (secondaryAuthenticationService.getName().equals(TransactionAuthenticationApiConstants.AADHAAR_OTP)) {
				String aadhaarNumber = this.clientData.getAadhaarNumberOfClient(loanId);
				final GenerateOtpService generateOtpService = this.generateOtpFactory
						.getOtpService(secondaryAuthenticationService.getAuthServiceClassName());
				return generateOtpService.generateOtp(aadhaarNumber);

			} else {
				throw new OtpTypeNotSupported();
			}
		} else {
			throw new InAcitiveExternalServiceexception(secondaryAuthenticationService.getName(),
					secondaryAuthenticationService.getId());
		}

	}

	public void executeTransactionAuthenticationService(final JsonCommand command) {
		final Integer count = this.transactionAuthenticationRepository.getPortfolioTypeAndTransactionType(
				SupportedAuthenticationPortfolioTypes.LOANS.getValue(), SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue());
		if (count > 0) {
			this.transactionAuthenticationDataValidator.validateForDisbursement(command);
			final Integer portfolioTypeId = SupportedAuthenticationPortfolioTypes.LOANS.getValue();
			final Integer transactionTypeId = SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue();
			final Long paymentTypeId = command
					.longValueOfParameterNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID);
			final BigDecimal amountGreaterThan = command
					.bigDecimalValueOfParameterNamed(TransactionAuthenticationApiConstants.TRANSACTION_AMOUNT);
			final TransactionAuthenticationData transactionAuthentication = this.transactionAuthenticationReadPlatformService
					.retriveTransactionAuthenticationDetails(portfolioTypeId, transactionTypeId, paymentTypeId,
							amountGreaterThan);

			if (transactionAuthentication != null) {
				final Long authenticationRuleId = command
						.longValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICAION_RULE_ID);

				if (authenticationRuleId != transactionAuthentication.getId()) {
					throw new TransactionAuthenticationMismatchException(authenticationRuleId);
				} else {

					final SecondaryAuthenticationService secondaryAuthenticationService = this.repository
							.findOneWithNotFoundDetection(transactionAuthentication.getAuthenticationTypeId());

					if (secondaryAuthenticationService.isActive()) {
						final ClientDataForAuthentication clientDataForAuthentication = clientData
								.validateAndAssembleClientDataForAuthentication(command,
										SupportedAuthenticationPortfolioTypes.LOANS.getValue(),
										SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue());

						AuthResponse authResponse = null;
						final String aadhaarNumber = clientDataForAuthentication.getClientAadhaarNumber();
						final SecondLevelAuthenticationService secondLevelAuthenticationService = this.secondaryAuthenticationFactory
								.getSecondLevelAuthenticationService(
										secondaryAuthenticationService.getAuthServiceClassName());
						Location location = getLocationDetails(clientDataForAuthentication);
						final String otp = clientDataForAuthentication.getClientAuthdata();
						Object response = secondLevelAuthenticationService.authenticateUser(aadhaarNumber, otp,
								location);
						if (response instanceof AuthResponse) {
							authResponse = (AuthResponse) response;
							if (authResponse != null) {
								if (!authResponse.isSuccess()) {
									throw new SecondaryAuthenticationFailedException(authResponse);
								}
							}
						}
					} else {
						throw new InAcitiveExternalServiceexception(secondaryAuthenticationService.getName(),
								secondaryAuthenticationService.getId());
					}
				}
			}
		}
	}

	public Location getLocationDetails(final ClientDataForAuthentication data) {
		String locationType = data.getLocationType();
		Location location = new Location();
		if (locationType.equals("gps")) {
			location.setType(LocationType.gps);
			location.setLongitude(data.getLongitude());
			location.setLatitude(data.getLatitude());
		} else if (locationType.equals("pincode")) {
			location.setType(LocationType.pincode);
			location.setPincode(data.getPincode());
		}
		return location;
	}

}
