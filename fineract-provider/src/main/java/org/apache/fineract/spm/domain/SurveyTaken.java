package org.apache.fineract.spm.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_survey_taken")
public class SurveyTaken extends AbstractAuditableCustom<AppUser, Long> {

    @OneToMany(mappedBy = "surveyTaken", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Scorecard> scorecards;

    @Column(name = "entity_type")
    private Integer entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyed_by")
    private Staff surveyedBy;

    @Column(name = "surveyed_on")
    @Temporal(TemporalType.DATE)
    private Date surveyedOn;

    public SurveyTaken() {}

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

    public Survey getSurvey() {
        return this.survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Staff getSurveyedBy() {
        return this.surveyedBy;
    }

    public void setSurveyedBy(Staff surveyedBy) {
        this.surveyedBy = surveyedBy;
    }

    public Date getSurveyedOn() {
        return this.surveyedOn;
    }

    public void setSurveyedOn(Date surveyedOn) {
        this.surveyedOn = surveyedOn;
    }

    public List<Scorecard> getScorecards() {
        return this.scorecards;
    }

    public void setScorecards(List<Scorecard> scorecards) {
        this.scorecards = scorecards;
    }

    public Integer getTotalScore() {
        Integer totalScore = 0;
        if (this.scorecards != null && !this.scorecards.isEmpty()) {
            for (final Scorecard scorecard : this.scorecards) {
                if (scorecard.getValue() != null) {
                    totalScore += scorecard.getValue();
                }
            }
        }

        return totalScore;
    }
}