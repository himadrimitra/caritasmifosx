package org.apache.fineract.portfolio.group.exception;

import java.util.Date;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.joda.time.LocalDate;

public class CollectionSheetHasAlreadyBeenSubmittedException extends AbstractPlatformDomainRuleException{
    public CollectionSheetHasAlreadyBeenSubmittedException(LocalDate date) {
        super("error.msg.Collection.has.already.been.added", "This collection sheet has already been submitted for the date",date);
    }   
}
