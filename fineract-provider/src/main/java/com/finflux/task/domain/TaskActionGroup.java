package com.finflux.task.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="f_task_action_group")
public class TaskActionGroup extends AbstractPersistable<Long> {
    
    @Column
    (name="identifier",nullable=true)
    private String identifier;

    
    public String getIdentifier() {
        return this.identifier;
    }

    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
