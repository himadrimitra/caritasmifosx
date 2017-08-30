package com.finflux.ruleengine.lib;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

import com.finflux.task.domain.Task;

public class RuleUndefinedException extends AbstractPlatformResourceNotFoundException {

    public RuleUndefinedException(Task task) {
            super("error.msg.rule.not.found", "Rule for task " + task.getName() + "is not configured",task.getId());
    }
}
