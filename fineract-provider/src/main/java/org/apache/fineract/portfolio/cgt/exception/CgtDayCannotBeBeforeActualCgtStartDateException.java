package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.joda.time.LocalDate;


public class CgtDayCannotBeBeforeActualCgtStartDateException extends AbstractPlatformDomainRuleException {

    public CgtDayCannotBeBeforeActualCgtStartDateException(final LocalDate actualStartDate, String defaultUserMessage) {
        super("error.msg.cgt.day.cannot.be.before.actual.start.date", defaultUserMessage, actualStartDate);
    }

}
