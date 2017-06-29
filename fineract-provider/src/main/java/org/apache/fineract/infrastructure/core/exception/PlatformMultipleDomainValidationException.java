package org.apache.fineract.infrastructure.core.exception;

import java.util.List;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;

public class PlatformMultipleDomainValidationException extends RuntimeException {

    private final String globalisationMessageCode;
    private final String defaultUserMessage;
    private final List<ApiParameterError> errors;

    public PlatformMultipleDomainValidationException(final List<ApiParameterError> errors) {
        this.globalisationMessageCode = "domain.validation.msg.domain.validation.errors.exist";
        this.defaultUserMessage = "Domain validation errors exist.";
        this.errors = errors;
    }

    public PlatformMultipleDomainValidationException(final String globalisationMessageCode, final String defaultUserMessage,
            final List<ApiParameterError> errors) {
        this.globalisationMessageCode = globalisationMessageCode;
        this.defaultUserMessage = defaultUserMessage;
        this.errors = errors;
    }

    public String getGlobalisationMessageCode() {
        return this.globalisationMessageCode;
    }

    public String getDefaultUserMessage() {
        return this.defaultUserMessage;
    }

    public List<ApiParameterError> getErrors() {
        return this.errors;
    }
}