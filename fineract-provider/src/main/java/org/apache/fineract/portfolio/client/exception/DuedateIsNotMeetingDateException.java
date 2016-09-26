package org.apache.fineract.portfolio.client.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.joda.time.LocalDate;

public class DuedateIsNotMeetingDateException extends AbstractPlatformResourceNotFoundException {
	public DuedateIsNotMeetingDateException(LocalDate date) {
		super("error.msg.duedate.is.not.meetingDate", "duedate is not meetingDate", date);
	}
}
