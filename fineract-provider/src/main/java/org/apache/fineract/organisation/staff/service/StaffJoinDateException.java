package org.apache.fineract.organisation.staff.service;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class StaffJoinDateException extends AbstractPlatformDomainRuleException {
	
	 public StaffJoinDateException(final Object... defaultUserMessageArgs) {
	        super("error.msg.staff.join.on.date.cannot.be.before.office.start.date",
	                "Staff Join date cannot be before the Office start date", defaultUserMessageArgs);
	    }


}
