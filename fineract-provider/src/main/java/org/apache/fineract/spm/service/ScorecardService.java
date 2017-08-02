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
package org.apache.fineract.spm.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.spm.data.ScorecardValue;
import org.apache.fineract.spm.data.SurveyTakenData;
import org.apache.fineract.spm.domain.Scorecard;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.spm.domain.SurveyTaken;
import org.apache.fineract.spm.repository.QuestionRepository;
import org.apache.fineract.spm.repository.ResponseRepository;
import org.apache.fineract.spm.repository.ScorecardRepository;
import org.apache.fineract.spm.repository.SurveyTakenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ScorecardMapper.SurveyTakenMapper;

@Service
public class ScorecardService {

    private final PlatformSecurityContext securityContext;
    private final SurveyTakenRepository surveyTakenRepository;
    private final ScorecardRepository scorecardRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;

    @Autowired
    public ScorecardService(final PlatformSecurityContext securityContext, final SurveyTakenRepository surveyTakenRepository,
            final ScorecardRepository scorecardRepository, final StaffRepositoryWrapper staffRepository,
            final QuestionRepository questionRepository, final ResponseRepository responseRepository) {
        super();
        this.securityContext = securityContext;
        this.surveyTakenRepository = surveyTakenRepository;
        this.scorecardRepository = scorecardRepository;
        this.staffRepository = staffRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
    }

    public List<Scorecard> createScorecard(final List<Scorecard> scorecards) {
        this.securityContext.authenticatedUser();

        return this.scorecardRepository.save(scorecards);
    }

    public List<Scorecard> findBySurvey(final Survey survey) {
        this.securityContext.authenticatedUser();

        return this.scorecardRepository.findBySurvey(survey);
    }

    public List<Scorecard> findBySurveyAndClient(final Survey survey, final Client client) {
        this.securityContext.authenticatedUser();

        return null;
    }

    @SuppressWarnings("unused")
    public SurveyTaken createSurveyTakenScorecard(final SurveyTakenData surveyTakenData, final Survey survey) {
        this.securityContext.authenticatedUser();
        final Long surveyedById = surveyTakenData.getSurveyedBy();
        final Staff surveyedBy = this.staffRepository.findOneWithNotFoundDetection(surveyedById);
        Staff coSurveyedBy = null;
        if (surveyTakenData.getCoSurveyedBy() != null) {
            coSurveyedBy = this.staffRepository.findOneWithNotFoundDetection(surveyTakenData.getCoSurveyedBy());
        }
        final SurveyTaken surveyTaken = SurveyTakenMapper.map(surveyTakenData, survey, surveyedBy, coSurveyedBy);
        List<Scorecard> scorecards = new ArrayList<Scorecard>();
        if (surveyTakenData.getScorecardValues() != null) {
            for (final ScorecardValue scorecardValue : surveyTakenData.getScorecardValues()) {
                final Scorecard scorecard = new Scorecard();
                scorecard.setSurveyTaken(surveyTaken);
                scorecard.setSurvey(survey);
                scorecard.setQuestion(this.questionRepository.findOne(scorecardValue.getQuestionId()));
                scorecard.setResponse(this.responseRepository.findOne(scorecardValue.getResponseId()));
                scorecard.setValue(scorecardValue.getValue());
                scorecards.add(scorecard);
            }
        }
        if (!scorecards.isEmpty()) {
            surveyTaken.setScorecards(scorecards);
        }
        return this.surveyTakenRepository.save(surveyTaken);
    }

    public List<SurveyTaken> findByEntityTypeAndEntityId(final Integer entityType, final Long entityId) {
        return this.surveyTakenRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @SuppressWarnings("unused")
    public SurveyTaken updateSurveyTakenScorecard(final SurveyTakenData surveyTakenData, final Survey survey) {
        this.securityContext.authenticatedUser();
        final Long surveyedById = surveyTakenData.getSurveyedBy();
        final Staff surveyedBy = this.staffRepository.findOneWithNotFoundDetection(surveyedById);
        Staff coSurveyedBy = null;
        if (surveyTakenData.getCoSurveyedBy() != null) {
            coSurveyedBy = this.staffRepository.findOneWithNotFoundDetection(surveyTakenData.getCoSurveyedBy());
        }
        final SurveyTaken surveyTaken = this.surveyTakenRepository.findOne(surveyTakenData.getId());
        SurveyTakenMapper.updateMap(surveyTaken, surveyTakenData, survey, surveyedBy,coSurveyedBy);
        List<Scorecard> scorecards = new ArrayList<Scorecard>();
        if (surveyTakenData.getScorecardValues() != null) {
            for (final ScorecardValue scorecardValue : surveyTakenData.getScorecardValues()) {
                final Scorecard scorecard = new Scorecard();;
                scorecard.setSurveyTaken(surveyTaken);
                scorecard.setSurvey(survey);
                scorecard.setQuestion(this.questionRepository.findOne(scorecardValue.getQuestionId()));
                scorecard.setResponse(this.responseRepository.findOne(scorecardValue.getResponseId()));
                scorecard.setValue(scorecardValue.getValue());
                scorecards.add(scorecard);
            }
        }
        if (!scorecards.isEmpty()) {
            surveyTaken.setScorecards(scorecards);
        }
        return this.surveyTakenRepository.save(surveyTaken);
    }
}