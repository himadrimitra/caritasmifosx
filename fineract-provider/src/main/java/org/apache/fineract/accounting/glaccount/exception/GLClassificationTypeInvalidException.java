package org.apache.fineract.accounting.glaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class GLClassificationTypeInvalidException extends AbstractPlatformDomainRuleException {

    public GLClassificationTypeInvalidException(final Integer glClassificationType) {
        super("error.msg.gl.classification.type.invalid", "GL classification type is invalid: " + glClassificationType);
    }
}