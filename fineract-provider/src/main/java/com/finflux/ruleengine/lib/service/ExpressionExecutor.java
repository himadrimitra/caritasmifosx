package com.finflux.ruleengine.lib.service;

import com.finflux.ruleengine.lib.FieldUndefinedException;
import com.finflux.ruleengine.lib.InvalidExpressionException;
import com.finflux.ruleengine.lib.data.ExpressionNode;

import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */
public interface ExpressionExecutor {
    public boolean executeExpression(ExpressionNode expressionNode, Map<String, Object> keyValueMap) throws FieldUndefinedException, InvalidExpressionException;
}
