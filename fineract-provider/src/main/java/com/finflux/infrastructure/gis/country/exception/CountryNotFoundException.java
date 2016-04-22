package com.finflux.infrastructure.gis.country.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CountryNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CountryNotFoundException(final Long id) {
        super("error.msg.country.with.id.does.not.exist", "Country with identifier " + id + " does not exist", id);
    }
}
