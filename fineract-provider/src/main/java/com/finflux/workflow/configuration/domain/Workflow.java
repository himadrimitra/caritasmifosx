package com.finflux.workflow.configuration.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "f_workflow")
public class Workflow extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflow", orphanRemoval = true)
    private Set<WorkflowEntityTypeMapping> eorkflowEntityTypeMappings = new HashSet<>();

    protected Workflow() {}
}