package com.finflux.task.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TaskNotFoundException extends AbstractPlatformResourceNotFoundException {

    public TaskNotFoundException(final Long id) {
        super("error.msg.task.id.invalid", "Task id " + id + " does not exist", id);
    }
}