package com.finflux.task.execution.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

import com.finflux.task.execution.data.TaskActionType;

public class TaskActionPermissionException extends AbstractPlatformServiceUnavailableException {

	public TaskActionPermissionException(TaskActionType actionType) {
		super("error.msg.work.flow.execution.step.action.no.permission", "No permission for action " + actionType, actionType);
	}
}
