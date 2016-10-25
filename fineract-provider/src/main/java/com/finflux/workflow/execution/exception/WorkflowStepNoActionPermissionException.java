package com.finflux.workflow.execution.exception;

import com.finflux.workflow.execution.data.StepAction;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

public class WorkflowStepNoActionPermissionException extends AbstractPlatformServiceUnavailableException {

    public WorkflowStepNoActionPermissionException(StepAction actionType) {
        super("error.msg.work.flow.execution.step.action.no.permission", "No permission for action " + actionType, actionType);
    }
}
