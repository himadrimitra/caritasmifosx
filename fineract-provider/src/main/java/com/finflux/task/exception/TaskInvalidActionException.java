package com.finflux.task.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

import com.finflux.task.data.TaskActionType;

public class TaskInvalidActionException extends AbstractPlatformServiceUnavailableException {

	public TaskInvalidActionException(String action) {
		super("error.msg.work.flow.execution.step.action.invalid", "Invalid action " + action, action);
	}
}
