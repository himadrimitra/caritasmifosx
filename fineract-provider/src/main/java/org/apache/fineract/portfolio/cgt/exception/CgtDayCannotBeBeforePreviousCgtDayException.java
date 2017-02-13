package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.joda.time.LocalDate;


public class CgtDayCannotBeBeforePreviousCgtDayException extends AbstractPlatformDomainRuleException {

    public CgtDayCannotBeBeforePreviousCgtDayException(final LocalDate cgtDate, String cgtDay, String defaultUserMessage) {
        super("error.msg.cgt.day.cannot.be.before.or.on.orevious.cgt.day", defaultUserMessage, cgtDay, cgtDate);
    }

}
