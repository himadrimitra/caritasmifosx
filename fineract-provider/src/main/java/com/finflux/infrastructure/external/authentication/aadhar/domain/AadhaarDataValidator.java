package com.finflux.infrastructure.external.authentication.aadhar.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class AadhaarDataValidator {

	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public AadhaarDataValidator(final FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	public void validateJsonForGenerateOtp(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();

		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AadhaarApiConstants.OTP_REQUEST_DATA);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(AadhaarApiConstants.AADHAAR_SERIVICE_RESOURCE_NAME);

		JsonElement element = this.fromJsonHelper.parse(json);

		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_NUMBER,
				element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AADHAAR_NUMBER).value(aadhaarNumber).notNull()
				.notExceedingLengthOf(12).notLessThanMin(12);

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	public void validateJsonForEKyc(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();

		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AadhaarApiConstants.KYC_REQUEST_DATA);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(AadhaarApiConstants.AADHAAR_SERIVICE_RESOURCE_NAME);

		JsonElement element = this.fromJsonHelper.parse(json);

		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_NUMBER,
				element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AADHAAR_NUMBER).value(aadhaarNumber).notNull();

		final String authType = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AUTH_TYPE, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AUTH_TYPE).value(authType).notNull().notBlank();

		final JsonElement locationElement = element.getAsJsonObject().get(AadhaarApiConstants.LOCATION);

		final String locationType = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.LOCATION_TYPE,
				locationElement);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.LOCATION_TYPE).value(locationType).notNull()
				.notBlank();

		if (locationType.equals(TransactionAuthenticationApiConstants.PINCODE)) {
			final String pincode = this.fromJsonHelper.extractStringNamed(TransactionAuthenticationApiConstants.PINCODE,
					locationElement);
			dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.PINCODE).value(pincode)
					.notNull().notBlank();
		} else if (locationType.equals(TransactionAuthenticationApiConstants.GPS)) {
			final String longitude = this.fromJsonHelper
					.extractStringNamed(TransactionAuthenticationApiConstants.LONGITUDE, locationElement);
			final String latitude = this.fromJsonHelper
					.extractStringNamed(TransactionAuthenticationApiConstants.LATITUDE, locationElement);
			dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.LONGITUDE).value(longitude)
					.notNull().notBlank();
			dataValidatorBuilder.reset().parameter(TransactionAuthenticationApiConstants.LATITUDE).value(latitude)
					.notBlank().notNull();
		} else {
			dataValidatorBuilder.reset().parameter(AadhaarApiConstants.LOCATION_TYPE).value(locationType)
					.isNotOneOfTheseValues(TransactionAuthenticationApiConstants.PINCODE,
							TransactionAuthenticationApiConstants.GPS);
		}
		final String authData = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AUTH_DATA, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AUTH_DATA).value(authData).notNull().notBlank();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			//
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
	}

}
