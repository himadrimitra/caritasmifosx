package com.finflux.infrastructure.gis.district.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class DistrictNotFoundException extends AbstractPlatformResourceNotFoundException {

    public DistrictNotFoundException(final Long id) {
        super("error.msg.district.with.id.does.not.exist", "District with identifier " + id + " does not exist", id);
    }
}
