package com.finflux.mandates.exception;

import org.apache.fineract.infrastructure.core.exception.PlatformInternalServerException;

public class ProcessFailedException extends PlatformInternalServerException{

        public ProcessFailedException(final Exception e) {
                super("error.unknown", "Unknown error", e);
        }
}
