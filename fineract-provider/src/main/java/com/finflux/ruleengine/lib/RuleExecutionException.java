package com.finflux.ruleengine.lib;

/**
 * Created by dhirendra on 06/09/16.
 */
public class RuleExecutionException extends Exception{

    public RuleExecutionException(String message, Throwable e){
        super(message, e);
    }

    public RuleExecutionException(String message){
        super(message);
    }
}
