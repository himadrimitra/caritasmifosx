package com.finflux.ruleengine.lib;

/**
 * Created by dhirendra on 06/09/16.
 */
public class InvalidExpressionException extends RuleExecutionException{
    public InvalidExpressionException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidExpressionException(String message) {
        super(message);
    }
}
