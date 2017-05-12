package com.finflux.task.template.data;


public enum TaskConfigActionGroupType {
    
    AdhocTaskGroup("adhoctaskgroup");
    
    private String identifier;
    
    private TaskConfigActionGroupType(String identifier){
        this.identifier=identifier;
    }

    
    public String getIdentifier() {
        return this.identifier;
    }

}
