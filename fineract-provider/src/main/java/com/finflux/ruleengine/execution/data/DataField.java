package com.finflux.ruleengine.execution.data;


public class DataField {
    
    private final String name;
    private final String query;
    private final String params;
    
    public DataField(final String name,final String query,final String params)
    {
        this.name=name;
        this.query=query;
        this.params=params;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getQuery() {
        return this.query;
    }
    
    public String getParams() {
        return this.params;
    }
    
    

}
