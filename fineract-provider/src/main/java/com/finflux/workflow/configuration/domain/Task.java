package com.finflux.workflow.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_task")
public class Task extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "identifier", length = 200, nullable = false)
    private String identifier;

    @Column(name = "config", nullable = true)
    private String config;

    @Column(name = "supported_actions", nullable = true)
    private String supportedActions;

    @Column(name = "type", length = 3, nullable = true)
    private Integer type;

    protected Task() {}
}