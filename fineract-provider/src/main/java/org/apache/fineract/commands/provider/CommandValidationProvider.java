package org.apache.fineract.commands.provider;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.annotation.CommandValidationType;
import org.apache.fineract.commands.exception.UnsupportedCommandException;
import org.apache.fineract.commands.validator.CommandSourceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

/**
 * {@link CommandValidationProvider} provides {@link CommandSourceValidator}s
 * for a given entity and action.<br/>
 * <br/>
 * A {@link CommandSourceValidator} can be registered and the annotation
 * {@link CommandValidationType} is used to determine the entity and the action
 * the handler is capable to process.
 *
 * @see CommandSourceValidator
 * @see CommandValidationType
 */
@Component
@Scope("singleton")
public class CommandValidationProvider implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandValidationProvider.class);

    private ApplicationContext applicationContext;
    private HashMap<String, String> registeredValidators;

    CommandValidationProvider() {
        super();
    }

    /**
     * Returns a handler for the given entity and action.<br/>
     * <br/>
     * Throws an {@link UnsupportedCommandException} if no handler for the given
     * entity, action combination can be found.
     * 
     * @param entity
     *            the entity to lookup the handler, must be given.
     */
    public CommandSourceValidator getHandler(@Nonnull final String entity) {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(entity), "An entity must be given!");
        CommandSourceValidator commandSourceValidator = null;
        if (this.registeredValidators.containsKey(entity)) {
            commandSourceValidator = (CommandSourceValidator) this.applicationContext.getBean(this.registeredValidators.get(entity));
        }
        return commandSourceValidator;
    }

    private void initializeValidatorRegistry() {
        if (this.registeredValidators == null) {
            this.registeredValidators = new HashMap<>();

            final String[] commandHandlerBeans = this.applicationContext.getBeanNamesForAnnotation(CommandValidationType.class);
            if (ArrayUtils.isNotEmpty(commandHandlerBeans)) {
                for (final String validatorName : commandHandlerBeans) {
                    LOGGER.info("Register Validator '" + validatorName + "' ...");
                    final CommandValidationType type = this.applicationContext.findAnnotationOnBean(validatorName,
                            CommandValidationType.class);
                    try {
                        String key = type.entity();
                        this.registeredValidators.put(key, validatorName);
                    } catch (final Throwable th) {
                        LOGGER.error("Unable to register validator '" + validatorName + "'!", th);
                    }
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.initializeValidatorRegistry();
    }
}
