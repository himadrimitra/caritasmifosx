package org.apache.fineract.spm.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.spm.data.SurveyTakenData;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.spm.domain.SurveyTaken;
import org.apache.fineract.spm.exception.SurveyNotFoundException;
import org.apache.fineract.spm.service.ScorecardService;
import org.apache.fineract.spm.service.SpmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ScorecardMapper.SurveyTakenMapper;

@Path("/{entityType}/{entityId}/takesurveys")
@Component
@Scope("singleton")
public class SurveyTakenApiResource {

    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SpmService spmService;
    private final ScorecardService scorecardService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public SurveyTakenApiResource(final DefaultToApiJsonSerializer toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper, final SpmService spmService, final ScorecardService scorecardService) {
        super();
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.spmService = spmService;
        this.scorecardService = scorecardService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Long createScorecard(final SurveyTakenData surveyTakenData) {
        final Survey survey = findSurvey(surveyTakenData.getSurveyId());
        final SurveyTaken surveyTaken = this.scorecardService.createSurveyTakenScorecard(surveyTakenData, survey);
        return surveyTaken.getId();
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Long updateScorecard(final SurveyTakenData surveyTakenData) {
        final Survey survey = findSurvey(surveyTakenData.getSurveyId());
        final SurveyTaken surveyTaken = this.scorecardService.updateSurveyTakenScorecard(surveyTakenData, survey);
        return surveyTaken.getId();
    }

    private Survey findSurvey(final Long surveyId) {
        final Survey survey = this.spmService.findById(surveyId);
        if (survey == null) { throw new SurveyNotFoundException(surveyId); }
        return survey;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public String fetchActiveSurveys(@PathParam("entityType") final Integer entityType, @PathParam("entityId") final Long entityId,
            @Context final UriInfo uriInfo) {
        final List<SurveyTakenData> result = new ArrayList<>();
        final List<SurveyTaken> surveyTakens = this.scorecardService.findByEntityTypeAndEntityId(entityType, entityId);
        if (surveyTakens != null) {
            for (final SurveyTaken surveyTaken : surveyTakens) {
                result.add(SurveyTakenMapper.map(surveyTaken));
            }
        }
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, result);
    }
}
