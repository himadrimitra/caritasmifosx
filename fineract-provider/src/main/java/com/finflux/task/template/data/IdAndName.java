package com.finflux.task.template.data;



public class IdAndName 
{
    
    
    private Long id=null;
    private String name=null;
    public IdAndName(Long id,String name)
    {
        this.id=id;
        this.name=name;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
}
