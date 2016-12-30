package org.apache.fineract.portfolio.validations.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.validations.data.EntityFieldRegexValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class FieldRegexValidator {

	private final EntityFieldRegexValidationReadPlatfromService entityFieldTypeReadflatformService;
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public FieldRegexValidator(EntityFieldRegexValidationReadPlatfromService entityFieldTypeReadflatformService,
			final FromJsonHelper fromApiJsonHelper) {
		this.entityFieldTypeReadflatformService = entityFieldTypeReadflatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	@SuppressWarnings("unused")
	public void validateForFieldName(Integer entityType, final JsonCommand command) {
		
		final String json = command.json();
		final JsonElement element = this.fromApiJsonHelper.parse(json);

		final Collection<EntityFieldRegexValidation> retrieveEntityFieldType = entityFieldTypeReadflatformService
				.retrieveEntityFieldType(entityType);

		Iterator<EntityFieldRegexValidation> it = retrieveEntityFieldType.iterator();
		while (it.hasNext()) {
			EntityFieldRegexValidation en = (EntityFieldRegexValidation) it.next();
			String regex = en.getRegex();
			String errorMsg = en.getErrorMsg();
			String fieldName = null;
			if(this.fromApiJsonHelper.parameterExists(en.getFieldName(), element)){
			fieldName = command.stringValueOfParameterNamed(en.getFieldName());
			}
			if (en.getPreriquisitesType() != null) {
				String preriquisitesType = en.getPreriquisitesType();
				String[] parts = preriquisitesType.split(":");
				final String documentTypeId = command.stringValueOfParameterNamed(parts[0]);
				String preriquisitesTypeId = parts[1];
				if (documentTypeId != null && preriquisitesTypeId != null && fieldName != null
						&& !fieldName.equals("")  && documentTypeId.equals(preriquisitesTypeId)) {
					validateForFieldName(regex, fieldName, errorMsg);
				}

			} else if (fieldName != null && !fieldName.equals("")) {
				validateForFieldName(regex, fieldName, errorMsg);
			}
		}
	}

	private void validateForFieldName(String regex, final String fieldName, String errorMsg) {
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors);
		if (!Pattern.compile(regex).matcher(fieldName).matches()) {
			baseDataValidator.failWithCodeValidator(errorMsg);
		}
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

	}
}
