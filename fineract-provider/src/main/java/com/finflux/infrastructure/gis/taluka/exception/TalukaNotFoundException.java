package com.finflux.infrastructure.gis.taluka.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TalukaNotFoundException extends AbstractPlatformResourceNotFoundException {
    
    public TalukaNotFoundException(final Long id) {
        super("error.msg.taluka.with.id.does.not.exist", "Taluka with identifier " + id + " does not exist", id);
    }

}
