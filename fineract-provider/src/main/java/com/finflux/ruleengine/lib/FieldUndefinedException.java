package com.finflux.ruleengine.lib;

/**
 * Created by dhirendra on 06/09/16.
 */
public class FieldUndefinedException extends RuleExecutionException{

    public FieldUndefinedException(String message){
        super(message);
    }
}
