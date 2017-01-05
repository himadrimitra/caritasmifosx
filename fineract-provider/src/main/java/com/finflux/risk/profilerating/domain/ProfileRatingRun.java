package com.finflux.risk.profilerating.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.ruleengine.configuration.domain.RuleModel;

@Entity
@Table(name = "f_profile_rating_run")
public class ProfileRatingRun extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "scope_entity_type", length = 3, nullable = false)
    private Integer scopeEntityType;

    @Column(name = "scope_entity_id", length = 20, nullable = false)
    private Long scopeEntityId;

    @Column(name = "entity_type", length = 3, nullable = false)
    private Integer entityType;

    @ManyToOne
    @JoinColumn(name = "criteria_id")
    private RuleModel criteria;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "status", length = 3, nullable = false)
    private Integer status;

    protected ProfileRatingRun() {}

    private ProfileRatingRun(final Integer scopeEntityType, final Long scopeEntityId, final Integer entityType, final RuleModel criteria,
            final Date startTime, final Date endTime, final Integer status) {
        this.scopeEntityType = scopeEntityType;
        this.scopeEntityId = scopeEntityId;
        this.entityType = entityType;
        this.criteria = criteria;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public static ProfileRatingRun create(final Integer scopeEntityType, final Long scopeEntityId, final Integer entityType,
            final RuleModel criteria, final Date startTime, final Date endTime, final Integer status) {
        return new ProfileRatingRun(scopeEntityType, scopeEntityId, entityType, criteria, startTime, endTime, status);
    }

    public Integer getScopeEntityType() {
        return this.scopeEntityType;
    }

    public void setScopeEntityType(Integer scopeEntityType) {
        this.scopeEntityType = scopeEntityType;
    }

    public Long getScopeEntityId() {
        return this.scopeEntityId;
    }

    public void setScopeEntityId(Long scopeEntityId) {
        this.scopeEntityId = scopeEntityId;
    }

    public Integer getEntityType() {
        return this.entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public RuleModel getCriteria() {
        return this.criteria;
    }

    public void setCriteria(RuleModel criteria) {
        this.criteria = criteria;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}