package com.finflux.ruleengine.execution.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when loan resources are not found.
 */
public class SqlNotFoundException extends AbstractPlatformResourceNotFoundException {

    public SqlNotFoundException(final Long id) {
        super("error.msg.rule.sql.not.found", "Sql not found for rule identifier " + id, id);
    }
}