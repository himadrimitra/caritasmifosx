package com.finflux.common.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;

public class FinfluxParseDataUtils {

    public static String getErrorMessage(final Exception exception) {
        if (exception instanceof AbstractPlatformDomainRuleException) {
            return ((AbstractPlatformDomainRuleException) exception).getDefaultUserMessage();
        } else if (exception instanceof PlatformApiDataValidationException) {
            final StringBuilder errorMsg = new StringBuilder();
            final List<ApiParameterError> errors = ((PlatformApiDataValidationException) exception).getErrors();
            for (ApiParameterError error : errors) {
                errorMsg.append(error.getDefaultUserMessage());
                errorMsg.append(".");
            }
            return errorMsg.toString();
        } else if (exception instanceof PlatformDataIntegrityException) {
            return ((PlatformDataIntegrityException) exception).getDefaultUserMessage();
        } else if (exception instanceof AbstractPlatformResourceNotFoundException) {
            return ((AbstractPlatformResourceNotFoundException) exception).getDefaultUserMessage();
        } else {
            if (!StringUtils.isEmpty(exception.getMessage())) {
                exception.printStackTrace();
                return exception.getMessage();
            }
            exception.printStackTrace();
            return "Internal Server Error - " + exception.getClass().getCanonicalName();
        }
    }
}
