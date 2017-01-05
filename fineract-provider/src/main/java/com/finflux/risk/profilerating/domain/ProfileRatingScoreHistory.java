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
@Table(name = "f_profile_rating_score_history")
public class ProfileRatingScoreHistory extends AbstractPersistable<Long> {

    @Column(name = "profile_rating_run_id", nullable = true)
    private Long profileRatingRunId;

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

    protected ProfileRatingScoreHistory() {}

    private ProfileRatingScoreHistory(final Long profileRatingRunId, final Integer entityType, final Long entityId,
            final Integer computedScore, final Integer overriddenScore, final Integer finalScore, final String criteriaResult,
            final Date updatedTime, final AppUser overriddenBy) {
        this.profileRatingRunId = profileRatingRunId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.computedScore = computedScore;
        this.overriddenScore = overriddenScore;
        this.finalScore = finalScore;
        this.criteriaResult = criteriaResult;
        this.updatedTime = updatedTime;
        this.overriddenBy = overriddenBy;
    }

    public static ProfileRatingScoreHistory create(final Long profileRatingRunId, final Integer entityType, final Long entityId,
            final Integer computedScore, final Integer overriddenScore, final Integer finalScore, final String criteriaResult,
            final Date updatedTime, final AppUser overriddenBy) {
        return new ProfileRatingScoreHistory(profileRatingRunId, entityType, entityId, computedScore, overriddenScore, finalScore,
                criteriaResult, updatedTime, overriddenBy);
    }
}
