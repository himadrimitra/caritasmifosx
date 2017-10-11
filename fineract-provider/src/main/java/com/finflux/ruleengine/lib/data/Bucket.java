package com.finflux.ruleengine.lib.data;

/**
 * Created by dhirendra on 06/09/16.
 */
public class Bucket {
    private String name;
    private String output;
    private ExpressionNode filter;

    public Bucket(String name, String output, ExpressionNode filter) {
        this.name = name;
        this.output = output;
        this.filter = filter;
    }
    public Bucket(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public ExpressionNode getFilter() {
        return filter;
    }

    public void setFilter(ExpressionNode filter) {
        this.filter = filter;
    }
}
