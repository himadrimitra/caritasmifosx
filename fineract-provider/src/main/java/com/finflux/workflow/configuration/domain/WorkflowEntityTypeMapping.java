package com.finflux.workflow.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_workflow_entity_type_mapping", uniqueConstraints = { @UniqueConstraint(columnNames = { "workflow_id", "entity_type",
        "entity_id" }, name = "UQ_f_workflow_entity_type_mapping") })
public class WorkflowEntityTypeMapping extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @Column(name = "entity_type", length = 3, nullable = false)
    private Integer entityType;

    @Column(name = "entity_id", length = 20, nullable = true)
    private Long entityId;

    protected WorkflowEntityTypeMapping() {}

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public Long getWorkflowId() {
        return this.workflow.getId();
    }
}
