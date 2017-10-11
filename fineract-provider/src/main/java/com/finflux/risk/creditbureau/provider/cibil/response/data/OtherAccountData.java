package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class OtherAccountData extends Data {

    private final String ACCOUNT_GROUP = "04";
    private final String DISBURSED_DATE = "08";
    private final String LAST_PAYMENT_DATE = "09";
    private final String CLOSED_DATE = "10";
    private final String CERTIFIED_DATE = "11";
    private final String OVERDUE_AMOUNT = "14";
    private final String PAYMENT_HISTORY1 = "28";
    private final String PAYMENT_HISTORY2 = "29";
    private final String PAYMENT_HISTORY_STARTDATE = "30";
    private final String PAYMENT_HISTORY_ENDDATE = "31";
    private final String SUIT_FILED = "32";
    private final String WRITTENOF_STATUS = "33";
    private final String ERRORCODE_ENTRY_DATE = "80";
    private final String ERROR_CODE = "82";
    private final String CIBILREMARKSCODE_ENTRY_DATE = "83";
    private final String CIBILREMARKS_CODE = "84";
    private final String DISPUTEREMARKS_CODE_ENTRY_DATE = "85";
    private final String DISPUTE_REMARKS_CODE1 = "86";
    private final String DISPUTE_REMARKS_CODE2 = "87";

    private String actionGroup;
    private String disbursedDate;
    private String lastPaymentDate;
    private String closedDate;
    private String certifiedDate;
    private String overdueAmount;
    private String paymentHistory1;
    private String paymentHistory2;
    private String paymentHistoryStartDate;
    private String paymenHistoryEndDate;
    private String suitFiled;
    private String writtenOfStatus;
    private String errorCodeEntryDate;
    private String errorCode;
    private String cibilRemarksEntryDate;
    private String cibilRemarksCode;
    private String disputeRemarksEntryDate;
    private String disputeRemarksCode1;
    private String disputeRemakrsCode2;

    public String getActionGroup() {
        return this.actionGroup;
    }

    public String getDisbursedDate() {
        return this.disbursedDate;
    }

    public String getLastPaymentDate() {
        return this.lastPaymentDate;
    }

    public String getClosedDate() {
        return this.closedDate;
    }

    public String getCertifiedDate() {
        return this.certifiedDate;
    }

    public String getOverdueAmount() {
        return this.overdueAmount;
    }

    public String getPaymentHistory1() {
        return this.paymentHistory1;
    }

    public String getPaymentHistory2() {
        return this.paymentHistory2;
    }

    public String getPaymentHistoryStartDate() {
        return this.paymentHistoryStartDate;
    }

    public String getPaymenHistoryEndDate() {
        return this.paymenHistoryEndDate;
    }

    public String getSuitFiled() {
        return this.suitFiled;
    }

    public String getWrittenOfStatus() {
        return this.writtenOfStatus;
    }

    public String getErrorCodeEntryDate() {
        return this.errorCodeEntryDate;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getCibilRemarksEntryDate() {
        return this.cibilRemarksEntryDate;
    }

    public String getCibilRemarksCode() {
        return this.cibilRemarksCode;
    }

    public String getDisputeRemarksEntryDate() {
        return this.disputeRemarksEntryDate;
    }

    public String getDisputeRemarksCode1() {
        return this.disputeRemarksCode1;
    }

    public String getDisputeRemakrsCode2() {
        return this.disputeRemakrsCode2;
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case ACCOUNT_GROUP:
                this.actionGroup = value;
            break;
            case DISBURSED_DATE:
                this.disbursedDate = value;
            break;
            case LAST_PAYMENT_DATE:
                this.lastPaymentDate = value;
            break;
            case CLOSED_DATE:
                this.closedDate = value;
            break;
            case CERTIFIED_DATE:
                this.certifiedDate = value;
            break;
            case OVERDUE_AMOUNT:
                this.overdueAmount = value;
            break;
            case PAYMENT_HISTORY1:
                this.paymentHistory1 = value;
            break;
            case PAYMENT_HISTORY2:
                this.paymentHistory2 = value;
            break;
            case PAYMENT_HISTORY_STARTDATE:
                this.paymentHistoryStartDate = value;
            break;
            case PAYMENT_HISTORY_ENDDATE:
                this.paymenHistoryEndDate = value;
            break;
            case SUIT_FILED:
                this.suitFiled = value;
            break;
            case WRITTENOF_STATUS:
                this.writtenOfStatus = value;
            break;
            case ERRORCODE_ENTRY_DATE:
                this.errorCodeEntryDate = value;
            break;
            case ERROR_CODE:
                this.errorCode = value;
            break;
            case CIBILREMARKSCODE_ENTRY_DATE:
                this.cibilRemarksEntryDate = value;
            break;
            case CIBILREMARKS_CODE:
                this.cibilRemarksCode = value;
            break;
            case DISPUTEREMARKS_CODE_ENTRY_DATE:
                this.disputeRemarksEntryDate = value;
            break;
            case DISPUTE_REMARKS_CODE1:
                this.disputeRemarksCode1 = value;
            break;
            case DISPUTE_REMARKS_CODE2:
                this.disputeRemakrsCode2 = value;
            break;
        }
    }

}
