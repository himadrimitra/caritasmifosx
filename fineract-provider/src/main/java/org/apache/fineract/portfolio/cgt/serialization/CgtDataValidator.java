package org.apache.fineract.portfolio.cgt.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.cgt.api.CgtApiConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class CgtDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public CgtDataValidator(final FromJsonHelper fromApiJsonHelper,
    		final ConfigurationDomainService configurationDomainService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.configurationDomainService = configurationDomainService;
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForCreateCgt(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CgtApiConstants.CGT_CREATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CgtApiConstants.CGT_RESOURCE_NAME);

		final Long entityId = this.fromApiJsonHelper.extractLongNamed(CgtApiConstants.entityIdParamName, element);
		baseDataValidator.reset().parameter(CgtApiConstants.entityIdParamName).value(entityId).notBlank().notNull()
				.integerGreaterThanZero();

		final Long entityType = this.fromApiJsonHelper.extractLongNamed(CgtApiConstants.entityTypeParamName, element);
		baseDataValidator.reset().parameter(CgtApiConstants.entityTypeParamName).value(entityType).notBlank().notNull()
				.integerGreaterThanZero();
		
		
		if (this.fromApiJsonHelper.parameterExists(CgtApiConstants.loanOfficerIdParamName, element)) {
			final Long loanOfficerId = this.fromApiJsonHelper.extractLongNamed(CgtApiConstants.loanOfficerIdParamName,
					element);
			baseDataValidator.reset().parameter(CgtApiConstants.loanOfficerIdParamName).value(loanOfficerId).notNull();
		}
		
		LocalDate expectedStartDate = null;
		if (this.fromApiJsonHelper.parameterExists(CgtApiConstants.expectedStartDateParamName, element)) {
			expectedStartDate = this.fromApiJsonHelper
					.extractLocalDateNamed(CgtApiConstants.expectedStartDateParamName, element);
			baseDataValidator.reset().parameter(CgtApiConstants.expectedStartDateParamName).value(expectedStartDate)
					.notNull();
		}
		
		LocalDate expectedEndDate = null;
		if (this.fromApiJsonHelper.parameterExists(CgtApiConstants.expectedEndDateParamName, element)) {
			 expectedEndDate = this.fromApiJsonHelper
					.extractLocalDateNamed(CgtApiConstants.expectedEndDateParamName, element);
			baseDataValidator.reset().parameter(CgtApiConstants.expectedEndDateParamName).value(expectedEndDate)
					.notNull();
		}
		
		baseDataValidator.reset().parameter(CgtApiConstants.expectedStartDateParamName).
		value(expectedStartDate).validateDateBeforeOrEqual(expectedEndDate);
		
		if(dataValidationErrors.isEmpty()){
		validateForMinumumAndMaximumCgtDays(expectedStartDate, expectedEndDate, baseDataValidator);
		}
		
        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForUpdateCgt(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CgtApiConstants.CGT_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CgtApiConstants.CGT_RESOURCE_NAME);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForRejectCgt(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CgtApiConstants.CGT_REJECT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CgtApiConstants.CGT_RESOURCE_NAME);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForCompleteCgt(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CgtApiConstants.CGT_COMPLETE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CgtApiConstants.CGT_RESOURCE_NAME);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }
    
	private void validateForMinumumAndMaximumCgtDays(final LocalDate expectedStartDate, final LocalDate expectedEndDate,
			DataValidatorBuilder baseDataValidator) {
		if (configurationDomainService.isMinCgtDaysEnabled()) {
			Long minumumCgtDyas = configurationDomainService.getMinCgtDays();
			if(minumumCgtDyas != null){
			baseDataValidator.reset().parameter(CgtApiConstants.expectedStartDateParamName)
					.validateMinimumDaysBetweenTwoDates(expectedStartDate, expectedEndDate, minumumCgtDyas);
			}
		}
		if (configurationDomainService.isMaxCgtDaysEnabled()) {
			Long maximumCgtDays = configurationDomainService.getMaxCgtDays();
			if(maximumCgtDays != null){
			baseDataValidator.reset().parameter(CgtApiConstants.expectedEndDateParamName)
					.validateMaximumDaysBetweenTwoDates(expectedStartDate, expectedEndDate, maximumCgtDays);
			}
		}
	}

}
