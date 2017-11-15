package com.finflux.organisation.transaction.authentication.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.exception.CodeValueNotFoundException;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.data.ClientIdentifierData;
import org.apache.fineract.portfolio.client.exception.ClientIdentifierNotFoundException;
import org.apache.fineract.portfolio.client.service.ClientIdentifierReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;
import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationService;
import com.finflux.infrastructure.external.authentication.service.ExternalAuthenticationServicesReadPlatformService;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.finflux.organisation.transaction.authentication.exception.TransactionAuthenticationMismatchException;
import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationReadPlatformService;
import com.google.gson.JsonElement;

@Service
public class ClientDataForAuthenticationAssembler {

	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final ClientIdentifierReadPlatformService clientIdentifierReadPlatformService;
	private final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService;
	private final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService;
	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public ClientDataForAuthenticationAssembler(final CodeValueReadPlatformService codeValueReadPlatformService,
			final ClientIdentifierReadPlatformService clientIdentifierReadPlatformService,
			final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService,
			final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService,
			final FromJsonHelper fromJsonHelper) {
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.clientIdentifierReadPlatformService = clientIdentifierReadPlatformService;
		this.transactionAuthenticationReadPlatformService = transactionAuthenticationReadPlatformService;
		this.externalAuthenticationServicesReadPlatformService = externalAuthenticationServicesReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}

	public ClientDataForAuthentication validateAndAssembleClientDataForAuthentication(JsonCommand command,
			final Integer productType, final Integer transactionType,
			final SecondaryAuthenticationService secondaryAuthenticationService, final Long productId,
			final Loan loan) {

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource(TransactionAuthenticationApiConstants.TRANSACTION_AUTHENTICATION_RESOURCE_NAME);

		final Long paymentTypeId = command
				.longValueOfParameterNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID);
		baseDataValidator.reset().parameter(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID).value(paymentTypeId)
				.notNull().notBlank();

		final BigDecimal amount = command
				.bigDecimalValueOfParameterNamed(TransactionAuthenticationApiConstants.TRANSACTION_AMOUNT);
		baseDataValidator.reset().parameter(TransactionAuthenticationApiConstants.TRANSACTION_AMOUNT).value(amount)
				.notNull().zeroOrPositiveAmount();

		final Long transactionAuthenticationId = command
				.longValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICAION_RULE_ID);

		baseDataValidator.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICAION_RULE_ID)
				.value(transactionAuthenticationId).notNull().notBlank().longGreaterThanZero();

		String authenticationType = command
				.stringValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE);

		baseDataValidator.reset().parameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE)
				.value(authenticationType).notNull().notBlank();

		JsonElement element = this.fromJsonHelper.parse(command.json());

		JsonElement clientAuthData = element.getAsJsonObject()
				.get(TransactionAuthenticationApiConstants.CLIENT_AUTH_DATA);

		baseDataValidator.reset().parameter(TransactionAuthenticationApiConstants.CLIENT_AUTH_DATA)
				.value(clientAuthData).notNull().notBlank();

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}

		final TransactionAuthenticationData transactionAuthenticationData = this.transactionAuthenticationReadPlatformService
				.retriveTransactionAuthenticationDetails(productType, transactionType, paymentTypeId, amount,
						productId);

		if (transactionAuthenticationData == null
				|| !transactionAuthenticationData.getId().equals(transactionAuthenticationId)) {
			throw new TransactionAuthenticationMismatchException(transactionAuthenticationId);
		}

		final ExternalAuthenticationServiceData externalAuthenticationServiceData = this.externalAuthenticationServicesReadPlatformService
				.retrieveOneActiveExternalAuthenticationService(
						transactionAuthenticationData.getAuthenticationTypeId());

		String aadhaarNumber = null;
		if (secondaryAuthenticationService.getName().equalsIgnoreCase("Aadhaar OTP")
				|| secondaryAuthenticationService.getName().equalsIgnoreCase("Aadhaar fingerprint")
				|| secondaryAuthenticationService.getName().equalsIgnoreCase("Aadhaar Iris")) {
			aadhaarNumber = getAadhaarNumberOfClient(loan.getClientId(),
					transactionAuthenticationData.getIdentificationType().getId());
		}

		return ClientDataForAuthentication.newInsatance(externalAuthenticationServiceData, aadhaarNumber,
				authenticationType, clientAuthData.toString());

	}

	public String getAadhaarNumberOfClient(final Long clientId, final Long identifierTypeId) {
		Collection<CodeValueData> codeValues = this.codeValueReadPlatformService
				.retrieveCodeValuesByCode("Customer Identifier");
		CodeValueData customerDocumentType = null;
		for (CodeValueData codeValue : codeValues) {
			if (codeValue.getId().equals(identifierTypeId)) {
				customerDocumentType = codeValue;
				break;
			}
		}

		if (customerDocumentType == null) {
			throw new CodeValueNotFoundException("Aadhaar", "Aadhaar");
		}

		Collection<ClientIdentifierData> clientIdentifiers = this.clientIdentifierReadPlatformService
				.retrieveClientIdentifiers(clientId);
		String aadhaarNumber = null;
		for (ClientIdentifierData clientIdentifier : clientIdentifiers) {
			if (clientIdentifier.getDocumentType().getId().equals(customerDocumentType.getId())) {
				aadhaarNumber = clientIdentifier.getDocumentKey();
				break;
			}
		}
		if (aadhaarNumber == null) {
			// ("Aadhaar number for the client not updated");
			throw new ClientIdentifierNotFoundException(customerDocumentType.getId());
		}
		return aadhaarNumber;

	}

}