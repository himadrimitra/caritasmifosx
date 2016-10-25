package com.finflux.workflow.execution.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class WorkflowExecutionNotFoundException extends AbstractPlatformResourceNotFoundException {

    public WorkflowExecutionNotFoundException(final Long id) {
        super("error.msg.work.flow.execution.id.invalid", "Work flow execution id " + id + " does not exist", id);
    }
}
