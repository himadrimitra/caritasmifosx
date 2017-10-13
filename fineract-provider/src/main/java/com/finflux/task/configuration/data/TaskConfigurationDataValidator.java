package com.finflux.task.configuration.data;

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

import com.finflux.task.configuration.api.TaskConfigurationApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class TaskConfigurationDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public TaskConfigurationDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateCreateLoanProductWorkflowTasks(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                TaskConfigurationApiConstants.CREATE_LOAN_PRODUCT_WORKFLOW_TASK_CONFIG_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(TaskConfigurationApiConstants.TASK_CONFIG_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String entityType = this.fromApiJsonHelper.extractStringNamed(TaskConfigurationApiConstants.entityTypeParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.entityTypeParamName).value(entityType).notBlank();

        final Long entityId = this.fromApiJsonHelper.extractLongNamed(TaskConfigurationApiConstants.entityIdParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.entityIdParamName).value(entityId).notBlank();

        final JsonArray tasks = this.fromApiJsonHelper.extractJsonArrayNamed(TaskConfigurationApiConstants.tasksParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.tasksParamName).value(tasks).jsonArrayNotEmpty();
        if (tasks != null && tasks.size() > 0) {
            for (int i = 0; i < tasks.size(); i++) {
                final JsonObject e = tasks.get(i).getAsJsonObject();
                validateEachLoanProductTask(e, baseDataValidator);
                throwExceptionIfValidationWarningsExist(dataValidationErrors);
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachLoanProductTask(final JsonObject element, final DataValidatorBuilder baseDataValidator) {

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, element.toString(),
                TaskConfigurationApiConstants.CREATE_LOAN_PRODUCT_WORKFLOW_EACH_TASK_CONFIG_REQUEST_DATA_PARAMETERS);

        final Long taskActivityId = this.fromApiJsonHelper.extractLongNamed(TaskConfigurationApiConstants.taskActivityIdParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.taskActivityIdParamName).value(taskActivityId).notBlank();

        final String name = this.fromApiJsonHelper.extractStringNamed(TaskConfigurationApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.nameParamName).value(name).notBlank().notExceedingLengthOf(200);

        final String shortName = this.fromApiJsonHelper.extractStringNamed(TaskConfigurationApiConstants.shortNameParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.shortNameParamName).value(shortName).notBlank()
                .notExceedingLengthOf(20);

    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
    
    public void validateCreateTaskConfigEntityMapping(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                TaskConfigurationApiConstants.CREATE_TASK_CONFIG_ENTITYMAPPING_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(TaskConfigurationApiConstants.TASK_CONFIG_ENTITYMAPPING_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long taskConfigId = this.fromApiJsonHelper.extractLongNamed(TaskConfigurationApiConstants.taskConfigIdParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.taskConfigIdParamName).value(taskConfigId).notBlank();

        final Integer entityType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(TaskConfigurationApiConstants.entityTypeParamName,
                element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.entityTypeParamName).value(entityType).notBlank();

        final Boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(TaskConfigurationApiConstants.isActiveParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.isActiveParamName).value(isActive).notBlank()
                .trueOrFalseRequired(true);

        final JsonArray entityIds = this.fromApiJsonHelper.extractJsonArrayNamed(TaskConfigurationApiConstants.entityIdsParamName, element);
        baseDataValidator.reset().parameter(TaskConfigurationApiConstants.entityIdsParamName).value(entityIds).jsonArrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}
