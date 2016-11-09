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
package org.apache.fineract.spm.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.spm.data.ComponentData;
import org.apache.fineract.spm.data.QuestionData;
import org.apache.fineract.spm.data.SurveyData;
import org.apache.fineract.spm.domain.Question;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.spm.exception.SurveyNotFoundException;
import org.apache.fineract.spm.service.SpmService;
import org.apache.fineract.spm.util.SurveyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("/surveys")
@Component
@Scope("singleton")
public class SpmApiResource {

    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext securityContext;
    private final SpmService spmService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public SpmApiResource(final DefaultToApiJsonSerializer toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PlatformSecurityContext securityContext, final SpmService spmService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.securityContext = securityContext;
        this.spmService = spmService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public SurveyData retrieveTemplate() {
        return this.spmService.retrieveTemplate();
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public String fetchActiveSurveys(@QueryParam("isActive") final Boolean isActive,
            @QueryParam("entityTypeId") final Integer entityTypeId, @Context final UriInfo uriInfo) {
        this.securityContext.authenticatedUser();
        final List<SurveyData> result = new ArrayList<>();
        List<Survey> surveys = null;
        surveys = this.spmService.fetchAllSurveys(isActive, entityTypeId);
        if (surveys != null) {
            for (final Survey survey : surveys) {
                result.add(SurveyMapper.map(survey));
            }
        }
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, result);
    }

    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public SurveyData findSurvey(@PathParam("id") final Long id) {
        this.securityContext.authenticatedUser();

        final Survey survey = this.spmService.findById(id);

        if (survey == null) { throw new SurveyNotFoundException(id); }

        return SurveyMapper.map(survey);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Long createSurvey(final SurveyData surveyData) {
        this.securityContext.authenticatedUser();
        final Survey survey = SurveyMapper.map(surveyData);
        this.spmService.saveSurvey(survey);
        return survey.getId();
    }

    @PUT
    @Path("{surveyId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void updateSurveyQuestions(@PathParam("surveyId") Long surveyId, final SurveyData surveyData) {
        this.securityContext.authenticatedUser();
        final Survey survey = this.spmService.findById(surveyId);
        SurveyMapper.mapUpdate(survey, surveyData);
        this.spmService.saveSurvey(survey);
    }

    @POST
    @Path("{surveyId}/components")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void createSurveyComponent(@PathParam("surveyId") Long surveyId, final List<ComponentData> componentDatas) {
        this.securityContext.authenticatedUser();
        final Survey survey = this.spmService.findById(surveyId);
        final List<org.apache.fineract.spm.domain.Component> components = SurveyMapper.mapComponentDatas(componentDatas, survey);
        survey.setComponents(components);
        this.spmService.saveSurvey(survey);
    }

    @POST
    @Path("{surveyId}/questions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void createSurveyQuestions(@PathParam("surveyId") Long surveyId, final List<QuestionData> questionDatas) {
        this.securityContext.authenticatedUser();
        final Survey survey = this.spmService.findById(surveyId);
        final List<Question> questions = SurveyMapper.mapQuestionDatas(questionDatas, survey);
        survey.setQuestions(questions);
        this.spmService.saveSurvey(survey);
    }

    @DELETE
    @Path("/{surveyId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void deactivateSurvey(@PathParam("surveyId") final Long surveyId) {
        this.securityContext.authenticatedUser();
        this.spmService.deactivateSurvey(surveyId);
    }
}
