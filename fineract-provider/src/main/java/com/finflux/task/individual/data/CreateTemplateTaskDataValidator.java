package com.finflux.task.individual.data;

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

import com.finflux.task.individual.api.CreateTemplateTaskApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class CreateTemplateTaskDataValidator 
{
 private final FromJsonHelper fromApiJsonHelper;
    
    @Autowired
    public CreateTemplateTaskDataValidator(final FromJsonHelper fromApiJsonHelper) 
    {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }
    public void validateForAssignTask(final String json)
    {
        if(StringUtils.isEmpty(json))
        {
            throw new InvalidJsonException();
        }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
        		CreateTemplateTaskApiConstants.ASSIGN_TASK_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CreateTemplateTaskApiConstants.TASK_ASSIGN);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final String templateId = this.fromApiJsonHelper.extractStringNamed(CreateTemplateTaskApiConstants.TemplateIdParamName, element);
        baseDataValidator.reset().parameter(CreateTemplateTaskApiConstants.TemplateIdParamName).value(templateId).notNull();

        final String userId = this.fromApiJsonHelper.extractStringNamed(CreateTemplateTaskApiConstants.UserIdParamName, element);
        baseDataValidator.reset().parameter(CreateTemplateTaskApiConstants.UserIdParamName).value(userId).notNull()
                .notExceedingLengthOf(10);

        final String entityId = this.fromApiJsonHelper.extractStringNamed(CreateTemplateTaskApiConstants.EntityIdParamName, element);
        baseDataValidator.reset().parameter(CreateTemplateTaskApiConstants.EntityIdParamName).value(entityId).notNull()
                .notExceedingLengthOf(30);

        final String dueDate = this.fromApiJsonHelper.extractStringNamed(CreateTemplateTaskApiConstants.DueDateParamName, element);
        baseDataValidator.reset().parameter(CreateTemplateTaskApiConstants.DueDateParamName).value(dueDate).notNull();
        
        final String dateFormat= this.fromApiJsonHelper.extractStringNamed(CreateTemplateTaskApiConstants.DateFormat, element);
        baseDataValidator.reset().parameter(CreateTemplateTaskApiConstants.DateFormat).value(dateFormat).notNull();
        
        final String locale= this.fromApiJsonHelper.extractStringNamed(CreateTemplateTaskApiConstants.Locale, element);
        baseDataValidator.reset().parameter(CreateTemplateTaskApiConstants.Locale).value(locale).notNull();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
