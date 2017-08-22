package ScorecardMapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.spm.data.ScorecardValue;
import org.apache.fineract.spm.data.SurveyTakenData;
import org.apache.fineract.spm.domain.Scorecard;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.spm.domain.SurveyTaken;

public class SurveyTakenMapper {

    private SurveyTakenMapper() {
        super();
    }

    public static SurveyTaken map(final SurveyTakenData scorecardData, final Survey survey, final Staff surveyedBy, final Staff coSurveyedBy) {
        final SurveyTaken surveyTaken = new SurveyTaken();
        surveyTaken.setEntityType(survey.getEntityType());
        surveyTaken.setEntityId(scorecardData.getEntityId());
        surveyTaken.setSurvey(survey);
        surveyTaken.setSurveyedBy(surveyedBy);
        surveyTaken.setCoSurveyedBy(coSurveyedBy);
        surveyTaken.setSurveyedOn(scorecardData.getSurveyedOn());
        return surveyTaken;
    }

    public static SurveyTaken updateMap(final SurveyTaken surveyTaken, final SurveyTakenData scorecardData, final Survey survey,
            final Staff surveyedBy, final Staff coSurveyedBy) {
        surveyTaken.setEntityType(survey.getEntityType());
        surveyTaken.setEntityId(scorecardData.getEntityId());
        surveyTaken.setSurvey(survey);
        surveyTaken.setSurveyedBy(surveyedBy);
        surveyTaken.setCoSurveyedBy(coSurveyedBy);
        surveyTaken.setSurveyedOn(scorecardData.getSurveyedOn());
        return surveyTaken;
    }
    
    @SuppressWarnings("unused")
    public static SurveyTakenData map(final SurveyTaken surveyTaken) {
        List<ScorecardValue> scorecardValues = null;
        if (surveyTaken.getScorecards() != null && !surveyTaken.getScorecards().isEmpty()) {
            scorecardValues = new ArrayList<ScorecardValue>();
            for (final Scorecard scorecard : surveyTaken.getScorecards()) {
                scorecardValues.add(new ScorecardValue(scorecard.getId(), scorecard.getQuestion().getId(), scorecard.getQuestion()
                        .getText(), scorecard.getResponse().getId(), scorecard.getResponse().getText(), scorecard.getValue()));
            }
        }
        Long coSurveyedBy = null;
        String coSurveyedByName = null;
        if (surveyTaken.getCoSurveyedBy() != null) {
            coSurveyedBy = surveyTaken.getCoSurveyedBy().getId();
            coSurveyedByName = surveyTaken.getCoSurveyedBy().getDisplayName();
        }
        return new SurveyTakenData(surveyTaken.getId(), surveyTaken.getSurvey().getId(), surveyTaken.getSurvey().getName(), surveyTaken
                .getSurveyedBy().getId(), surveyTaken.getSurveyedBy().getDisplayName(), coSurveyedBy, coSurveyedByName,
                surveyTaken.getSurveyedOn(), surveyTaken.getTotalScore(), scorecardValues);
    }
}
