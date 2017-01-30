package com.finflux.task.configuration.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TaskConfigEntityTypeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public TaskConfigEntityTypeNotFoundException(final String resource) {
        super("error.msg.task.configuration.entitytype.not.found", "Task configuration entity type is not found for the resource "
                + resource, resource);
    }
}