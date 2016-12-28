package org.apache.fineract.commands.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the command type for the annotated class.<br/>
 * <br/>
 * The entity name (e.g. CLIENT, SAVINGSACCOUNT, LOANPRODUCT) must be given.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface CommandValidationType {

    /**
     * Returns the name of the entity for this {@link CommandValidationType}.
     */
    String entity();
}
