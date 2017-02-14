package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.joda.time.LocalDate;


public class CgtDayCannotBeAfterFutureCgtDaysException extends AbstractPlatformDomainRuleException {

    public CgtDayCannotBeAfterFutureCgtDaysException(final LocalDate cgtDate, String cgtDay, String defaultUserMessage) {
        super("error.msg.cgt.day.cannot.be.after.or.on.future.cgt.day", defaultUserMessage, cgtDay, cgtDate);
    }

}
