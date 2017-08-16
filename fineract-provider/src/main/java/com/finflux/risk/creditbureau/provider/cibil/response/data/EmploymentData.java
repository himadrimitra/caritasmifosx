package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class EmploymentData extends Data {

    private final String ACCOUNT_TYPE = "01";
    private final String REPORTED_CERTIFIED_DATE = "02";
    private final String OCCUPATION_CODE = "03";
    private final String INCOME = "04";
    private final String NET_GROSS_INCOME_INDIATOR = "05";
    private final String MONTH_ANNUAL_INCOMEE_INDICATOR = "06";
    private final String ERRORCODE_ENTRY_DATE = "80";
    private final String ERROR_CODE = "82";
    private final String CIBILREMARKSCODE_ENTRY_DATE = "83";
    private final String CIBILREMARKS_CODE = "84";
    private final String DISPUTEREMARKS_CODE_ENTRY_DATE = "85";
    private final String DISPUTE_REMARKS_CODE1 = "86";
    private final String DISPUTE_REMARKS_CODE2 = "87";

    private String accountType;
    private String reportedAndCertifiedDate;
    private String occupationCode;
    private String income;
    private String grossOrNetincomeIndicator; // Gross or Net
    private String monthlyorYearlyIndicator;
    private String errorCodeEntryDate;
    private String errorCode;
    private String cibilRemrksEntryDate;
    private String cibilRemarksCode;
    private String disputeRemarksCodeEntryDate;
    private String remarksCode1;
    private String remarksCode2;

    public EmploymentData() {

    }

    public String getAccountType() {
        return this.accountType;
    }

    public String getReportedAndCertifiedDate() {
        return this.reportedAndCertifiedDate;
    }

    public String getOccupationCode() {
        return this.occupationCode;
    }

    public String getIncome() {
        return this.income;
    }

    public String getGrossOrNetincomeIndicator() {
        return this.grossOrNetincomeIndicator;
    }

    public String getMonthlyorYearlyIndicator() {
        return this.monthlyorYearlyIndicator;
    }

    public String getErrorCodeEntryDate() {
        return this.errorCodeEntryDate;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getCibilRemrksEntryDate() {
        return this.cibilRemrksEntryDate;
    }

    public String getCibilRemarksCode() {
        return this.cibilRemarksCode;
    }

    public String getDisputeRemarksCodeEntryDate() {
        return this.disputeRemarksCodeEntryDate;
    }

    public String getRemarksCode1() {
        return this.remarksCode1;
    }

    public String getRemarksCode2() {
        return this.remarksCode2;
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case ACCOUNT_TYPE:
                this.accountType = value;
            break;
            case REPORTED_CERTIFIED_DATE:
                this.reportedAndCertifiedDate = value;
            break;
            case OCCUPATION_CODE:
                this.occupationCode = value;
            break;
            case INCOME:
                this.income = value;
            break;
            case NET_GROSS_INCOME_INDIATOR:
                this.grossOrNetincomeIndicator = value;
            break;
            case MONTH_ANNUAL_INCOMEE_INDICATOR:
                this.monthlyorYearlyIndicator = value;
            break;
            case ERRORCODE_ENTRY_DATE:
                this.errorCodeEntryDate = value;
            break;
            case ERROR_CODE:
                this.errorCode = value;
            break;
            case CIBILREMARKSCODE_ENTRY_DATE:
                this.cibilRemrksEntryDate = value;
            break;
            case CIBILREMARKS_CODE:
                this.cibilRemarksCode = value;
            break;
            case DISPUTEREMARKS_CODE_ENTRY_DATE:
                this.disputeRemarksCodeEntryDate = value;
            break;
            case DISPUTE_REMARKS_CODE1:
                this.remarksCode1 = value;
            break;
            case DISPUTE_REMARKS_CODE2:
                this.remarksCode2 = value;
            break;
        }
    }

}
