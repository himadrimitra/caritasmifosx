package com.finflux.ruleengine.configuration.data;

import com.finflux.ruleengine.lib.data.KeyValue;
import com.finflux.ruleengine.lib.data.ValueType;

import java.util.List;

/**
 * Created by dhirendra on 16/09/16.
 */
public class SurveyFieldData {

    private Long surveyId;

    private String surveyDisplay;

    private Long questionId;

    private String  questionDisplay;

    private Long responseId;

    private String  responseDisplay;


    public SurveyFieldData(Long surveyId, String surveyDisplay, Long questionId, String questionDisplay, Long responseId,
                           String responseDisplay) {
        this.surveyId = surveyId;
        this.surveyDisplay = surveyDisplay;
        this.questionId = questionId;
        this.questionDisplay = questionDisplay;
        this.responseId = responseId;
        this.responseDisplay = responseDisplay;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyDisplay() {
        return surveyDisplay;
    }

    public void setSurveyDisplay(String surveyDisplay) {
        this.surveyDisplay = surveyDisplay;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionDisplay() {
        return questionDisplay;
    }

    public void setQuestionDisplay(String questionDisplay) {
        this.questionDisplay = questionDisplay;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public String getResponseDisplay() {
        return responseDisplay;
    }

    public void setResponseDisplay(String responseDisplay) {
        this.responseDisplay = responseDisplay;
    }
}
