package org.apache.fineract.portfolio.common.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class EntityTypeNotSupportedException extends AbstractPlatformResourceNotFoundException {

    public EntityTypeNotSupportedException(final String resource) {
        super("entity.type.not.supported", "Entity type is not supported for the resource " + resource, resource);
    }
}