package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.joda.time.LocalDate;


public class CgtCopletedDayCannotBeScheduledDateException extends AbstractPlatformDomainRuleException {

    public CgtCopletedDayCannotBeScheduledDateException(final LocalDate sheduleDate, String cgtDay, String defaultUserMessage) {
        super("error.msg.cgt.day.cannot.be.bfore.schedule.date", defaultUserMessage, cgtDay, sheduleDate);
    }

}
