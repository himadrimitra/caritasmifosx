package com.finflux.risk.profilerating.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_profile_rating_score")
public class ProfileRatingScore extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "profile_rating_run_id", nullable = true)
    private ProfileRatingRun profileRatingRun;

    @Column(name = "entity_type", nullable = false)
    private Integer entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "computed_score", nullable = true)
    private Integer computedScore;

    @Column(name = "overridden_score", nullable = true)
    private Integer overriddenScore;

    @Column(name = "final_score", nullable = true)
    private Integer finalScore;

    @Column(name = "criteria_result", nullable = true)
    private String criteriaResult;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    @ManyToOne
    @JoinColumn(name = "overridden_by_id", nullable = true)
    private AppUser overriddenBy;

    protected ProfileRatingScore() {}

    private ProfileRatingScore(final ProfileRatingRun profileRatingRun, final Integer entityType, final Long entityId,
            final Integer computedScore, final Integer overriddenScore, final Integer finalScore, final String criteriaResult,
            final Date updatedTime, final AppUser overriddenBy) {
        this.profileRatingRun = profileRatingRun;
        this.entityType = entityType;
        this.entityId = entityId;
        this.computedScore = computedScore;
        this.overriddenScore = overriddenScore;
        this.finalScore = finalScore;
        this.criteriaResult = criteriaResult;
        this.updatedTime = updatedTime;
        this.overriddenBy = overriddenBy;
    }

    public static ProfileRatingScore create(final ProfileRatingRun profileRatingRun, final Integer entityType, final Long entityId,
            final Integer computedScore, final Integer overriddenScore, final Integer finalScore, final String criteriaResult,
            final Date updatedTime, final AppUser overriddenBy) {
        return new ProfileRatingScore(profileRatingRun, entityType, entityId, computedScore, overriddenScore, finalScore, criteriaResult,
                updatedTime, overriddenBy);
    }

    public void update(final ProfileRatingRun profileRatingRun, final Integer entityType, final Long entityId, final Integer computedScore,
            final Integer overriddenScore, final Integer finalScore, final String criteriaResult, final Date updatedTime,
            final AppUser overriddenBy) {
        this.profileRatingRun = profileRatingRun;
        this.entityType = entityType;
        this.entityId = entityId;
        this.computedScore = computedScore;
        this.overriddenScore = overriddenScore;
        this.finalScore = finalScore;
        this.criteriaResult = criteriaResult;
        this.updatedTime = updatedTime;
        this.overriddenBy = overriddenBy;
    }

    public ProfileRatingRun getProfileRatingRun() {
        return this.profileRatingRun;
    }

    public void setProfileRatingRun(ProfileRatingRun profileRatingRun) {
        this.profileRatingRun = profileRatingRun;
    }

    public Integer getEntityType() {
        return this.entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Integer getComputedScore() {
        return this.computedScore;
    }

    public void setComputedScore(Integer computedScore) {
        this.computedScore = computedScore;
    }

    public Integer getOverriddenScore() {
        return this.overriddenScore;
    }

    public void setOverriddenScore(Integer overriddenScore) {
        this.overriddenScore = overriddenScore;
    }

    public Integer getFinalScore() {
        return this.finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public String getCriteriaResult() {
        return this.criteriaResult;
    }

    public void setCriteriaResult(String criteriaResult) {
        this.criteriaResult = criteriaResult;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public AppUser getOverriddenBy() {
        return this.overriddenBy;
    }

    public void setOverriddenBy(AppUser overriddenBy) {
        this.overriddenBy = overriddenBy;
    }
}
