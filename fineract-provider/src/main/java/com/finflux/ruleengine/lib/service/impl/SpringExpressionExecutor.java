package com.finflux.ruleengine.lib.service.impl;

import com.finflux.ruleengine.lib.RuleUtils;
import com.finflux.ruleengine.lib.data.ExpressionNode;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */
@Service
@Scope("singleton")
public class SpringExpressionExecutor implements ExpressionExecutor {

    private final RuleUtils ruleUtils;

    @Autowired
    public SpringExpressionExecutor(RuleUtils ruleUtils){
        this.ruleUtils = ruleUtils;
    }

    @Override
    public boolean executeExpression(ExpressionNode expressionNode, Map<String, Object> keyValueMap) {
        EvaluationContext context = new StandardEvaluationContext();
        for(Map.Entry<String, Object> entry: keyValueMap.entrySet()){
            context.setVariable(entry.getKey(),entry.getValue());
        }

        ExpressionParser parser = new SpelExpressionParser();
        try {
            Expression exp = parser.parseExpression(ruleUtils.buildExpression(expressionNode));
            return (boolean) exp.getValue(context, Boolean.class);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
