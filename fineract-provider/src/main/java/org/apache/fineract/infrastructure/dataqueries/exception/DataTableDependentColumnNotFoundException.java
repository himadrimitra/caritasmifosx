package org.apache.fineract.infrastructure.dataqueries.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class DataTableDependentColumnNotFoundException extends AbstractPlatformResourceNotFoundException{

    public DataTableDependentColumnNotFoundException() {
        super("error.msg.dependent.column.name.not.found", "Dependent Column not found.");
    }
}
