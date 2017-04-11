package org.apache.fineract.infrastructure.security.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.api.AuthenticationApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class AuthenticationDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public AuthenticationDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForAuthentication(JsonElement element) {
        if (element == null) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final String authenticationJson = this.fromApiJsonHelper.toJson(element.getAsJsonObject());
        if (authenticationJson != null) this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, authenticationJson,
                AuthenticationApiConstants.AUTHENTICATION_PARAMETER_NAMES);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(AuthenticationApiConstants.AUTHENICATION_RESOURCE_NAME);

        final String username = this.fromApiJsonHelper.extractStringNamed(AuthenticationApiConstants.userNameParamName, element);
        baseDataValidator.reset().parameter(AuthenticationApiConstants.userNameParamName).value(username).notNull().notBlank();

        final String password = this.fromApiJsonHelper.extractStringNamed(AuthenticationApiConstants.passwordParamName, element);
        baseDataValidator.reset().parameter(AuthenticationApiConstants.passwordParamName).value(password).notNull().notBlank();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
