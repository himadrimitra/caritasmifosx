package com.finflux.task.template.data;

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

import com.finflux.task.template.api.TaskConfigTemplateApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class TaskConfigTemplateDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    
    @Autowired
    public TaskConfigTemplateDataValidator(final FromJsonHelper fromApiJsonHelper) 
    {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }
    
    public void validateForCreateTaskTemplate(final String json)
    {
        if(StringUtils.isEmpty(json))
        {
            throw new InvalidJsonException();
        }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                TaskConfigTemplateApiConstants.CREATE_TASK_TEMPLATE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(TaskConfigTemplateApiConstants.TEMPLATE_CONFIG_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final String taskName = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.taskNameParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.taskNameParamName).value(taskName).notNull();

        final String shortName = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.shortNameParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.shortNameParamName).value(shortName).notNull()
                .notExceedingLengthOf(10);

        final String selectedActivity = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.selectedActivityParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.selectedActivityParamName).value(selectedActivity).notNull()
                .notExceedingLengthOf(30);

        final String selectedEntity = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.selectedEntityParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.selectedEntityParamName).value(selectedEntity).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    public void validateForUpdateTaskTemplate(final String json)
    {
        if(StringUtils.isEmpty(json))
        {
            throw new InvalidJsonException();
        }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                TaskConfigTemplateApiConstants.CREATE_TASK_TEMPLATE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(TaskConfigTemplateApiConstants.TEMPLATE_CONFIG_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final String taskName = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.taskNameParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.taskNameParamName).value(taskName).ignoreIfNull();

        final String shortName = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.shortNameParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.shortNameParamName).value(shortName).ignoreIfNull()
                .notExceedingLengthOf(10);

        final String selectedActivity = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.selectedActivityParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.selectedActivityParamName).value(selectedActivity).ignoreIfNull()
                .notExceedingLengthOf(30);

        final String selectedEntity = this.fromApiJsonHelper.extractStringNamed(TaskConfigTemplateApiConstants.selectedEntityParamName, element);
        baseDataValidator.reset().parameter(TaskConfigTemplateApiConstants.selectedEntityParamName).value(selectedEntity).ignoreIfNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
