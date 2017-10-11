package com.finflux.task.exception;

import com.finflux.task.data.TaskActionType;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

public class TaskActionPermissionException extends AbstractPlatformServiceUnavailableException {

	public TaskActionPermissionException(TaskActionType actionType) {
		super("error.msg.work.flow.execution.step.action.no.permission", "No permission for action " + actionType, actionType);
	}
}
