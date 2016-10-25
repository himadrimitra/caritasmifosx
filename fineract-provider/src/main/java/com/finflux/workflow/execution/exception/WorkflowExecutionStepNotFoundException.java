package com.finflux.workflow.execution.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class WorkflowExecutionStepNotFoundException extends AbstractPlatformResourceNotFoundException {

    public WorkflowExecutionStepNotFoundException(final Long id) {
        super("error.msg.work.flow.execution.step.id.invalid", "Work flow execution step id " + id + " does not exist", id);
    }
}
