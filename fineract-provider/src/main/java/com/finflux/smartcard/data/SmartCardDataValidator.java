package com.finflux.smartcard.data;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.smartcard.api.SmartCardApiConstants;
import com.finflux.smartcard.domain.SmartCard;
import com.finflux.smartcard.domain.SmartCardStatusTypeEnum;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class SmartCardDataValidator {

	private final FromJsonHelper fromApiJsonHelper;

	/**
	 * Validate Create smartcard data Request Parameters
	 * 
	 * @param json
	 */

	@Autowired
	public SmartCardDataValidator(final FromJsonHelper fromJsonHelper) {
		this.fromApiJsonHelper = fromJsonHelper;
	}

	/**
	 * Validating all required parameters exists or not
	 * 
	 * @param entityId
	 * @param json
	 */

	public void validateForCreate(final Long clientId, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				SmartCardApiConstants.CREATE_SMARTCARD_REQUEST_DATA_PARAMETERS);
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource(SmartCardApiConstants.SMARTCARD_RESOURCE_NAME);
		final JsonElement parentElement = this.fromApiJsonHelper.parse(json);

		if (parentElement.isJsonObject()) {
			validateEachJsonObjectForCreate(clientId, parentElement.getAsJsonObject(), baseDataValidator);

		}
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void validateEachJsonObjectForCreate(final Long clientId, final JsonObject element,
			final DataValidatorBuilder baseDataValidator) {

		final JsonObject topLevelJsonElement = element.getAsJsonObject();
		final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

		baseDataValidator.reset().parameter(SmartCardApiConstants.clientIdParamName).value(clientId).notBlank();

		final String cardNumber = this.fromApiJsonHelper.extractStringNamed(SmartCardApiConstants.cardNumberParamName,
				element);
		baseDataValidator.reset().parameter(SmartCardApiConstants.cardNumberParamName).value(cardNumber).notNull();

		final Integer cardStatus = this.fromApiJsonHelper
				.extractIntegerWithLocaleNamed(SmartCardApiConstants.cardStatusParamName, element);
		baseDataValidator.reset().parameter(SmartCardApiConstants.cardStatusParamName).value(cardStatus).ignoreIfNull();

		final String note = this.fromApiJsonHelper.extractStringNamed(SmartCardApiConstants.cardNoteParamName, element);
		baseDataValidator.reset().parameter(SmartCardApiConstants.cardNoteParamName).value(note).ignoreIfNull()
				.notExceedingLengthOf(500);
	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
	}

	public void validateForEntityType(final SmartCard smartCard, final Integer entityType) {

		if (!smartCard.getEntityType().equals(entityType)) {
			final SmartCardStatusTypeEnum cardEntityType = SmartCardStatusTypeEnum.fromInt(entityType);
			throw new PlatformDataIntegrityException("error.msg.entityType incorrect",
					"entityType `" + cardEntityType.name() + "` is not match with cardNumber entityType",
					"cardNumber `" + smartCard.getCardNumber() + "`", "entityType", "cardNumber", cardEntityType.name(),
					smartCard.getCardNumber());
		}

	}

}
