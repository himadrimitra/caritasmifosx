package com.finflux.workflow.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_workflow")
public class Workflow extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    protected Workflow() {}
}