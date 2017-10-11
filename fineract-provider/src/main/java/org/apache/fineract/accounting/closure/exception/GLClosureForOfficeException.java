
package org.apache.fineract.accounting.closure.exception;

import java.util.Date;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class GLClosureForOfficeException extends AbstractPlatformDomainRuleException {

    public GLClosureForOfficeException(final Long officeId, final Date latestclosureDate) {
        super("error.msg.accounting.closed", "The latest closure for office with Id " + officeId + " is on " + latestclosureDate.toString()
                + ", please delete this closure first", latestclosureDate);
    }
}