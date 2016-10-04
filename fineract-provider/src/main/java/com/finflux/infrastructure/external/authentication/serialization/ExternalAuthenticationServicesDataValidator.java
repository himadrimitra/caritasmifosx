package com.finflux.infrastructure.external.authentication.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServicesDataConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.InvalidJsonException;

@Component
public class ExternalAuthenticationServicesDataValidator {
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public ExternalAuthenticationServicesDataValidator(final FromJsonHelper fromJsonHelper) {
		this.fromApiJsonHelper = fromJsonHelper;
	}

	public void validateForUpdate(final JsonCommand jsonCommand) {
		final String json = jsonCommand.json();

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				ExternalAuthenticationServicesDataConstants.TRANSACTION_AUTHENTICATION_SERVICES_RESPONSE);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).reset()
				.resource(ExternalAuthenticationServicesDataConstants.TRANSACTION_AUTHENTICATION_SERVICE);

		final JsonElement element = jsonCommand.parsedJson();

		final boolean isActive = this.fromApiJsonHelper
				.parameterExists(ExternalAuthenticationServicesDataConstants.ISACTIVE, element);
		baseDataValidator.reset().parameter(ExternalAuthenticationServicesDataConstants.ISACTIVE).value(isActive)
				.validateForBooleanValue();
	}
}
