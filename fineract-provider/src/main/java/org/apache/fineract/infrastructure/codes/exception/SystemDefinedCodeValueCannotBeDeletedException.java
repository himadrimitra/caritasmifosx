package org.apache.fineract.infrastructure.codes.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SystemDefinedCodeValueCannotBeDeletedException extends AbstractPlatformDomainRuleException {

        public SystemDefinedCodeValueCannotBeDeletedException(final Long codeValueId) {
                super("error.msg.code.systemdefined", "This code value is system defined and cannot be modified or deleted.", codeValueId);
        }
}
