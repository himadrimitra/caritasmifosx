package com.finflux.infrastructure.gis.state.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class StateNotFoundException extends AbstractPlatformResourceNotFoundException {

    public StateNotFoundException(final Long id) {
        super("error.msg.state.with.id.does.not.exist", "State with identifier " + id + " does not exist", id);
    }
}
