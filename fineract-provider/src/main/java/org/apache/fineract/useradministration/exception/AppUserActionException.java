package org.apache.fineract.useradministration.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class AppUserActionException extends AbstractPlatformResourceNotFoundException {

	public AppUserActionException(String userName, String action) {
		super("error.msg.user.invalid.action", "Unable to " + action + " appuser " + userName, userName, action);

	}

}
