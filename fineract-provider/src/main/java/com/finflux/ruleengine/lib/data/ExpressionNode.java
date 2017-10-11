package com.finflux.ruleengine.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by dhirendra on 06/09/16.
 */

public class ExpressionNode {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConnectorType connector; // Connector

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ExpressionNode> nodes;//Exptession

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Expression expression;

    public ExpressionNode(ConnectorType connector, List<ExpressionNode> nodes) {
        this.connector = connector;
        this.nodes = nodes;
    }

    public ExpressionNode(Expression expression) {
        this.expression = expression;
    }

    public ConnectorType getConnector() {
        return connector;
    }

    public void setConnector(ConnectorType connector) {
        this.connector = connector;
    }

    public List<ExpressionNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ExpressionNode> nodes) {
        this.nodes = nodes;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @JsonIgnore
    public boolean isLeafNode() {
        return expression != null;
    }
}
