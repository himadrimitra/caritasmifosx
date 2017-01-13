package com.finflux.ruleengine.lib.service.impl;

import com.finflux.ruleengine.configuration.data.FieldData;
import com.finflux.ruleengine.lib.FieldUndefinedException;
import com.finflux.ruleengine.lib.InvalidExpressionException;
import com.finflux.ruleengine.lib.RuleUtils;
import com.finflux.ruleengine.lib.data.ConnectorType;
import com.finflux.ruleengine.lib.data.Expression;
import com.finflux.ruleengine.lib.data.ExpressionNode;
import com.finflux.ruleengine.lib.data.ValueType;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.expression.EvaluationContext;
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
public class MyExpressionExecutor implements ExpressionExecutor {

    private final RuleUtils ruleUtils;

    @Autowired
    public MyExpressionExecutor(RuleUtils ruleUtils){
        this.ruleUtils = ruleUtils;
    }

    @Override
    public boolean executeExpression(ExpressionNode expressionNode, Map<String, Object> keyValueMap) throws FieldUndefinedException, InvalidExpressionException {
        if(expressionNode == null){
            return true;
        }
        if(expressionNode.isLeafNode()){
            return executeExpressionLeafNode(expressionNode, keyValueMap);
        }else{
            if(expressionNode.getNodes() !=null && !expressionNode.getNodes().isEmpty()){
                for (ExpressionNode exprNode : expressionNode.getNodes()) {
                    boolean result = executeExpression(exprNode,keyValueMap);
                    if(ConnectorType.and.equals(expressionNode.getConnector()) && result == false){
                        return false;
                    }
                    if(ConnectorType.or.equals(expressionNode.getConnector()) && result == true){
                        return true;
                    }
                }
                if(ConnectorType.and.equals(expressionNode.getConnector())){
                    return  true;
                }
                if(ConnectorType.or.equals(expressionNode.getConnector())){
                    return  false;
                }
            }
            return  true;
        }
    }

    private boolean executeExpressionLeafNode(ExpressionNode expressionNode, Map<String,Object> map) throws FieldUndefinedException, InvalidExpressionException {
        Expression expression = expressionNode.getExpression();
        Object value = map.get(expression.getParameter());
        if(value==null && !expression.getComparator().getSupportNullValue()) {
            FieldData errorField = ruleUtils.getFieldFromParameter(expression.getParameter());
            throw new FieldUndefinedException("No data found for Key:["+errorField.getName()+"]");
        }
        try {
            if (value instanceof String) {
                if (ValueType.BOOLEAN.equals(expression.getValueType())) {
                    String tmpValue = (String) value;
                    value = Boolean.parseBoolean(tmpValue);
                } else if (ValueType.NUMBER.equals(expression.getValueType())) {
                    String tmpValue = (String) value;
                    value = NumberUtils.createNumber(tmpValue);
                }
            }

            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable(expression.getParameter(), value);

            ExpressionParser parser = new SpelExpressionParser();

            String expressionStr = ruleUtils.buildExpression(expressionNode);
            org.springframework.expression.Expression exp = parser.parseExpression(expressionStr);
            return exp.getValue(context, Boolean.class);
        }catch(Exception e){
            throw new InvalidExpressionException("Error occurred while evaluating:["
                    +ruleUtils.buildExpression(expressionNode)+"]",e);
        }
    }

}
