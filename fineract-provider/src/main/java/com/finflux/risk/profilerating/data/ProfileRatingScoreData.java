package com.finflux.risk.profilerating.data;

import java.util.Date;

public class ProfileRatingScoreData {

    private Long id;
    private Integer computedScore;
    private Integer overriddenScore;
    private Integer finalScore;
    private String criteriaResult;
    private Date updatedTime;

    private ProfileRatingScoreData(final Long id, final Integer computedScore, final Integer overriddenScore, final Integer finalScore,
            final String criteriaResult, final Date updatedTime) {
        this.id = id;
        this.computedScore = computedScore;
        this.overriddenScore = overriddenScore;
        this.finalScore = finalScore;
        this.criteriaResult = criteriaResult;
        this.updatedTime = updatedTime;
    }

    public static ProfileRatingScoreData instance(final Long id, final Integer computedScore, final Integer overriddenScore,
            final Integer finalScore, final String criteriaResult, final Date updatedTime) {
        return new ProfileRatingScoreData(id, computedScore, overriddenScore, finalScore, criteriaResult, updatedTime);
    }

    public Long getId() {
        return this.id;
    }

    public Integer getComputedScore() {
        return this.computedScore;
    }

    public Integer getOverriddenScore() {
        return this.overriddenScore;
    }

    public Integer getFinalScore() {
        return this.finalScore;
    }

    public String getCriteriaResult() {
        return this.criteriaResult;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }
}
