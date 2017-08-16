package com.finflux.risk.creditbureau.provider.cibil.response.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.finflux.risk.creditbureau.provider.data.CreditScore;

public class ScoreData extends Data {

    protected final DateFormat dateFormat_DDMMYYYY = new SimpleDateFormat("ddMMyyyy");

    private final Integer HEADERTAG_LENGTH = 14;

    private final String SCORE_NAME = "SC";
    private final String SCORECARD_NAME = "01";
    private final String SCORECARD_VERSION = "02";
    private final String SCORE_DATE = "03";
    private final String SCORE = "04";
    private final String EXCLUSION_CODE1 = "05";
    private final String EXCLUSION_CODE2 = "06";
    private final String EXCLUSION_CODE3 = "07";
    private final String EXCLUSION_CODE4 = "08";
    private final String EXCLUSION_CODE5 = "09";
    private final String EXCLUSION_CODE6 = "10";
    private final String EXCLUSION_CODE7 = "11";
    private final String EXCLUSION_CODE8 = "12";
    private final String EXCLUSION_CODE9 = "13";
    private final String EXCLUSION_CODE10 = "14";
    private final String REASON_CODE1 = "25";
    private final String REASON_CODE2 = "26";
    private final String REASON_CODE3 = "27";
    private final String REASON_CODE4 = "28";
    private final String REASON_CODE5 = "29";
    private final String ERROR_CODE = "75";

    private String scoreName;
    private String scoreCardName;
    private String scoreCardVersion;
    private String scoreCardDate;
    private String score;
    private List<String> exclusionCodes = new ArrayList<>();
    private List<String> reasonCodes = new ArrayList<>();
    private String errorCode;

    public String getScoreCardName() {
        return this.scoreCardName;
    }

    public String getScoreCardVersion() {
        return this.scoreCardVersion;
    }

    public String getScoreCardDate() {
        return this.scoreCardDate;
    }

    public String getScore() {
        return this.score;
    }

    public List<String> getExclusionCodes() {
        return this.exclusionCodes;
    }

    public List<String> getReasonCodes() {
        return this.reasonCodes;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getScoreName() {
        return this.scoreName;
    }

    @Override
    public void setHeaderData(String headerData) {
        super.setHeaderData(headerData);
        if (headerData != null && headerData.startsWith(SCORE_NAME)) {
            this.scoreName = headerData.substring(4);
        }
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case SCORECARD_NAME:
                this.scoreCardName = value;
            break;
            case SCORECARD_VERSION:
                this.scoreCardVersion = value;
            break;
            case SCORE_DATE:
                this.scoreCardDate = value;
            break;
            case SCORE:
                this.score = value;
            break;
            case EXCLUSION_CODE1:
            case EXCLUSION_CODE2:
            case EXCLUSION_CODE3:
            case EXCLUSION_CODE4:
            case EXCLUSION_CODE5:
            case EXCLUSION_CODE6:
            case EXCLUSION_CODE7:
            case EXCLUSION_CODE8:
            case EXCLUSION_CODE9:
            case EXCLUSION_CODE10:
                this.exclusionCodes.add(value);
            break;
            case REASON_CODE1:
            case REASON_CODE2:
            case REASON_CODE3:
            case REASON_CODE4:
            case REASON_CODE5:
                this.reasonCodes.add(value);
            break;
            case ERROR_CODE:
                this.errorCode = value;
            break;
        }

    }

    @Override
    public Integer getSegmentHeaderLength() {
        return this.HEADERTAG_LENGTH;
    }

    public CreditScore toCreditScore() {
        Date scoreCardDate = null;
        if (this.scoreCardDate != null) {
            try {
                this.dateFormat_DDMMYYYY.parse(this.scoreCardDate);
            } catch (ParseException e) {}
        }
        return new CreditScore(this.scoreName, scoreCardName, scoreCardVersion, scoreCardDate, this.score);
    }
}
