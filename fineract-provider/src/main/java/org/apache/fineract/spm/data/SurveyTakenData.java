/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.spm.data;

import java.util.Date;
import java.util.List;

public class SurveyTakenData {

    private Long id;
    private Long surveyId;
    private String surveyName;
    private Long entityId;
    private Long surveyedBy;
    private String surveyedByName;
    private Date surveyedOn;
    private Integer totalScore;
    private List<ScorecardValue> scorecardValues;

    public SurveyTakenData() {
        super();
    }

    public SurveyTakenData(final Long entityId, final Long surveyedBy, final Date surveyedOn, final List<ScorecardValue> scorecardValues) {
        super();
        this.entityId = entityId;
        this.surveyedBy = surveyedBy;
        this.surveyedOn = surveyedOn;
        this.scorecardValues = scorecardValues;
    }

    public SurveyTakenData(final Long id, final Long surveyId, final String surveyName, final Long surveyedBy, final String surveyedByName,
            final Date surveyedOn, final Integer totalScore, final List<ScorecardValue> scorecardValues) {
        this.id = id;
        this.surveyId = surveyId;
        this.surveyName = surveyName;
        this.surveyedBy = surveyedBy;
        this.surveyedByName = surveyedByName;
        this.surveyedOn = surveyedOn;
        this.totalScore = totalScore;
        this.scorecardValues = scorecardValues;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getSurveyedBy() {
        return this.surveyedBy;
    }

    public void setSurveyedBy(Long surveyedBy) {
        this.surveyedBy = surveyedBy;
    }

    public Date getSurveyedOn() {
        return this.surveyedOn;
    }

    public void setSurveyedOn(Date surveyedOn) {
        this.surveyedOn = surveyedOn;
    }

    public List<ScorecardValue> getScorecardValues() {
        return this.scorecardValues;
    }

    public void setScorecardValues(List<ScorecardValue> scorecardValues) {
        this.scorecardValues = scorecardValues;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return this.surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyedByName() {
        return this.surveyedByName;
    }

    public void setSurveyedByName(String surveyedByName) {
        this.surveyedByName = surveyedByName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
}
