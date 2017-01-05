package com.finflux.task.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TaskConfigNotFoundException extends AbstractPlatformResourceNotFoundException {

    public TaskConfigNotFoundException(final Long id) {
        super("error.msg.task.config.id.invalid", "Task config identifier " + id + " does not exist", id);
    }
}