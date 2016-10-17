package com.finflux.ruleengine.configuration.exception;

import com.finflux.ruleengine.lib.data.EntityRuleType;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RuleNotFoundException extends AbstractPlatformResourceNotFoundException {

    public RuleNotFoundException(final Long id, final EntityRuleType ruleType) {
        super("error.msg.rule.id.invalid", "Rule " + ruleType +" with id " + id + " does not exist", id);
    }

}