package ScorecardMapper;

import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.spm.data.SurveyTakenData;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.spm.domain.SurveyTaken;

public class SurveyTakenMapper {

    private SurveyTakenMapper() {
        super();
    }

    public static SurveyTaken map(final SurveyTakenData scorecardData, final Survey survey, final Staff surveyedBy) {
        final SurveyTaken surveyTaken = new SurveyTaken();
        surveyTaken.setEntityType(survey.getEntityType());
        surveyTaken.setEntityId(scorecardData.getEntityId());
        surveyTaken.setSurvey(survey);
        surveyTaken.setSurveyedBy(surveyedBy);
        surveyTaken.setSurveyedOn(scorecardData.getSurveyedOn());
        return surveyTaken;
    }

    public static SurveyTakenData map(final SurveyTaken surveyTaken) {
        return new SurveyTakenData(surveyTaken.getId(), surveyTaken.getSurvey().getId(), surveyTaken.getSurvey().getName(), surveyTaken
                .getSurveyedBy().getId(), surveyTaken.getSurveyedBy().getDisplayName(), surveyTaken.getSurveyedOn(),
                surveyTaken.getTotalScore());
    }
}
