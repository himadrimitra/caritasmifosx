package com.finflux.ruleengine.lib;

import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.lib.data.*;
import com.finflux.ruleengine.lib.service.impl.BasicRuleExecutor;
import com.finflux.ruleengine.lib.service.impl.MyExpressionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dhirendra on 06/09/16.
 */
@Scope("singleton")
@Service
public class RuleUtils {

    private final RuleCacheService ruleCacheService;

    @Autowired
    public RuleUtils(RuleCacheService ruleCacheService){
        this.ruleCacheService = ruleCacheService;
    }

    public String buildExpression(ExpressionNode expressionNode) throws InvalidExpressionException {
        if(expressionNode == null){
            return "";
        }
        if(expressionNode.isLeafNode()){
            Expression expression = expressionNode.getExpression();
            return expressionNode.getExpression().getComparator().buildSpringExpression(expression.getParameter(),
                    expression.getValue(),expression.getValueType());
        }else{
            List<String> expressionList = new ArrayList<>();
            if(expressionNode.getNodes() !=null && !expressionNode.getNodes().isEmpty()) {
                for(ExpressionNode expr: expressionNode.getNodes()){
                    expressionList.add(buildExpression(expr));
                }
            }
            return "("+String.join(" "+expressionNode.getConnector()+" ",expressionList)+")";
        }
    }

    public Set<String> getRuleParameters(Rule rule) {
        Set<String> requiredParameters = new HashSet<>();
        for(Bucket bucket: rule.getBuckets()){
            populateRequiredParameters(bucket.getFilter(),requiredParameters);
        }
        return requiredParameters;
    }

    public Set<String> getRequiredFields(Rule rule) {
        Set<String> requiredFields = new HashSet<>();
        for(Bucket bucket: rule.getBuckets()){
            populateFields(rule,requiredFields);
        }
        return requiredFields;
    }

    public Set<String> getExpressionParameters(ExpressionNode expressionNode) {
        Set<String> requiredParameters = new HashSet<>();
        populateRequiredParameters(expressionNode,requiredParameters);
        return requiredParameters;
    }

    public void populateRequiredParameters(ExpressionNode expressionNode, Set<String> parameters) {
        if(expressionNode == null){
            return;
        }
        if(expressionNode.isLeafNode()){
            Expression expression = expressionNode.getExpression();
            parameters.add(expression.getParameter());
        }else{
            if(expressionNode.getNodes() !=null && !expressionNode.getNodes().isEmpty()) {
                for(ExpressionNode node: expressionNode.getNodes()){
                    populateRequiredParameters(node,parameters);
                }
            }
        }
    }

    public void populateFields(Rule rule, Set<String> fields) {
        Set<String> expressionEntites = getRuleParameters(rule);
        if(!EntityRuleType.FACTOR.equals(rule.getEntity())) {
            for (String childRule : expressionEntites) {
                Rule tmpRule = getRule(childRule);
                populateFields(tmpRule,fields);
            }
        }else{
            fields.addAll(expressionEntites);
        }
    }

    public Rule getRule(String childRule) {
        return ruleCacheService.getRuleByUname(childRule);
    }
}
