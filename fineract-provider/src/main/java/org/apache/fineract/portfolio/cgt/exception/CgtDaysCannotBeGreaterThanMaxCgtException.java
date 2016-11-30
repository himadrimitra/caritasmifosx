package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class CgtDaysCannotBeGreaterThanMaxCgtException extends AbstractPlatformDomainRuleException {

    public CgtDaysCannotBeGreaterThanMaxCgtException(final Long maxCgtDayCount, String defaultUserMessage) {
        super("error.msg.cgt.days.cannot.be.created.more.than."+maxCgtDayCount+"", "CGT Days cannot be created more than "+maxCgtDayCount+"", defaultUserMessage);
    }

}
