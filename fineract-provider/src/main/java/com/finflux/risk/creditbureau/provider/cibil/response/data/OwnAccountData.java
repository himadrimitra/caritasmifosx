package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class OwnAccountData extends Data {

    private final String MEMBER_SHORTNAME = "02";
    private final String ACCOUNT_NUMBER = "03";
    private final String ACCOUNT_TYPE = "04";
    private final String OWNERSHIP_INDICATOR = "05";
    private final String OPENED_DATE = "08";
    private final String LASTPAYMENT_DATE = "09";
    private final String CLOSED_DATE = "10";
    private final String REPORTED_CERTIFIED_DATE = "11";
    private final String SACTIONED_AMOUNT = "12";
    private final String CURRENT_BALANCE = "13";
    private final String OVERDUE_AMOUNT = "14";
    private final String PAYMENT_HISTORY1 = "28";
    private final String PAYMENT_HISTORY2 = "29";
    private final String PAYMENT_HISTORY_STARTDATE = "30";
    private final String PAYMENT_HISTORY_ENDDATE = "31";
    private final String SUIT_FILED = "32";
    private final String WRITTENOFF_STATUS = "33";
    private final String COLLATERAL_VALUE = "34";
    private final String COLLATERAL_TYPE = "35";
    private final String CREDIT_LIMIT = "36";
    private final String CASH_LIMIT = "37";
    private final String INTEREST_RATE = "38";
    private final String PAYMENT_TENURE = "39";
    private final String EMI_AMOUNT = "40";
    private final String TOTAL_WRITTENOFF_AMOUNT = "41";
    private final String PRINCIPAL_WRITTENOFF_AMOUNT = "42";
    private final String SETTLED_AMOUNT = "43";
    private final String PAYMENT_FREQUENCY = "44";
    private final String ACTUAL_PAYMENT_AMOUNT = "45";
    private final String ERRORCODE_ENTRY_DATE = "80";
    private final String ERROR_CODE = "82";
    private final String CIBILREMARKSCODE_ENTRY_DATE = "83";
    private final String CIBILREMARKS_CODE = "84";
    private final String DISPUTEREMARKS_CODE_ENTRY_DATE = "85";
    private final String DISPUTE_REMARKS_CODE1 = "86";
    private final String DISPUTE_REMARKS_CODE2 = "87";

    private String memberShortName;
    private String accountNumber;
    private String accountType;
    private String ownershipIndicator;
    private String dateOpened;
    private String lastPaymentDate;
    private String closedDate;
    private String reportedAndCertifiedDate;
    private String sanctionedAmount;
    private String currentBalance;
    private String overdueAmount;
    private String paymentHistory1;
    private String paymentHistory2;
    private String paymentHistoryStartDate;
    private String paymentHistoryEndDate;
    private String suitFiledCode;
    private String writtenOfStatus;
    private String collateralValue;
    private String collateralType;
    private String creditLimit;
    private String cashLimit;
    private String interestRate;
    private String repaymentTenure;
    private String emiAmount;
    private String totalWrittenOfAmount;
    private String principalWrittenOfAmount;
    private String settledAmount;
    private String paymentFrequency;
    private String actualPaymentAmount;
    private String errorCodeEntryDate;
    private String errorCode;
    private String cibilRemarksEntryDate;
    private String cibilRemarksCode;
    private String disputeRemarksEntryDate;
    private String disputeRemarksCode1;
    private String disputeRemakrsCode2;

    public String getMemberShortName() {
        return this.memberShortName;
    }

    public void setMemberShortName(String memberShortName) {
        this.memberShortName = memberShortName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getOwnershipIndicator() {
        return this.ownershipIndicator;
    }

    public void setOwnershipIndicator(String ownershipIndicator) {
        this.ownershipIndicator = ownershipIndicator;
    }

    public String getDateOpened() {
        return this.dateOpened;
    }

    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getLastPaymentDate() {
        return this.lastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getClosedDate() {
        return this.closedDate;
    }

    public void setClosedDate(String closedDate) {
        this.closedDate = closedDate;
    }

    public String getReportedAndCertifiedDate() {
        return this.reportedAndCertifiedDate;
    }

    public void setReportedAndCertifiedDate(String reportedAndCertifiedDate) {
        this.reportedAndCertifiedDate = reportedAndCertifiedDate;
    }

    public String getSanctionedAmount() {
        return this.sanctionedAmount;
    }

    public void setSanctionedAmount(String sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }

    public String getCurrentBalance() {
        return this.currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getOverdueAmount() {
        return this.overdueAmount;
    }

    public void setOverdueAmount(String overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public String getPaymentHistory1() {
        return this.paymentHistory1;
    }

    public void setPaymentHistory1(String paymentHistory1) {
        this.paymentHistory1 = paymentHistory1;
    }

    public String getPaymentHistory2() {
        return this.paymentHistory2;
    }

    public void setPaymentHistory2(String paymentHistory2) {
        this.paymentHistory2 = paymentHistory2;
    }

    public String getPaymentHistoryStartDate() {
        return this.paymentHistoryStartDate;
    }

    public void setPaymentHistoryStartDate(String paymentHistoryStartDate) {
        this.paymentHistoryStartDate = paymentHistoryStartDate;
    }

    public String getPaymentHistoryEndDate() {
        return this.paymentHistoryEndDate;
    }

    public void setPaymentHistoryEndDate(String paymentHistoryEndDate) {
        this.paymentHistoryEndDate = paymentHistoryEndDate;
    }

    public String getSuitFiledCode() {
        return this.suitFiledCode;
    }

    public void setSuitFiledCode(String suitFiledCode) {
        this.suitFiledCode = suitFiledCode;
    }

    public String getWrittenOfStatus() {
        return this.writtenOfStatus;
    }

    public void setWrittenOfStatus(String writtenOfStatus) {
        this.writtenOfStatus = writtenOfStatus;
    }

    public String getCollateralValue() {
        return this.collateralValue;
    }

    public void setCollateralValue(String collateralValue) {
        this.collateralValue = collateralValue;
    }

    public String getCollateralType() {
        return this.collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public String getCreditLimit() {
        return this.creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCashLimit() {
        return this.cashLimit;
    }

    public void setCashLimit(String cashLimit) {
        this.cashLimit = cashLimit;
    }

    public String getInterestRate() {
        return this.interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getRepaymentTenure() {
        return this.repaymentTenure;
    }

    public void setRepaymentTenure(String repaymentTenure) {
        this.repaymentTenure = repaymentTenure;
    }

    public String getEmiAmount() {
        return this.emiAmount;
    }

    public void setEmiAmount(String emiAmount) {
        this.emiAmount = emiAmount;
    }

    public String getTotalWrittenOfAmount() {
        return this.totalWrittenOfAmount;
    }

    public void setTotalWrittenOfAmount(String totalWrittenOfAmount) {
        this.totalWrittenOfAmount = totalWrittenOfAmount;
    }

    public String getPrincipalWrittenOfAmount() {
        return this.principalWrittenOfAmount;
    }

    public void setPrincipalWrittenOfAmount(String principalWrittenOfAmount) {
        this.principalWrittenOfAmount = principalWrittenOfAmount;
    }

    public String getSettledAmount() {
        return this.settledAmount;
    }

    public void setSettledAmount(String settledAmount) {
        this.settledAmount = settledAmount;
    }

    public String getPaymentFrequency() {
        return this.paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public String getActualPaymentAmount() {
        return this.actualPaymentAmount;
    }

    public void setActualPaymentAmount(String actualPaymentAmount) {
        this.actualPaymentAmount = actualPaymentAmount;
    }

    public String getErrorCodeEntryDate() {
        return this.errorCodeEntryDate;
    }

    public void setErrorCodeEntryDate(String errorCodeEntryDate) {
        this.errorCodeEntryDate = errorCodeEntryDate;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getCibilRemarksEntryDate() {
        return this.cibilRemarksEntryDate;
    }

    public void setCibilRemarksEntryDate(String cibilRemarksEntryDate) {
        this.cibilRemarksEntryDate = cibilRemarksEntryDate;
    }

    public String getCibilRemarksCode() {
        return this.cibilRemarksCode;
    }

    public void setCibilRemarksCode(String cibilRemarksCode) {
        this.cibilRemarksCode = cibilRemarksCode;
    }

    public String getDisputeRemarksEntryDate() {
        return this.disputeRemarksEntryDate;
    }

    public void setDisputeRemarksEntryDate(String disputeRemarksEntryDate) {
        this.disputeRemarksEntryDate = disputeRemarksEntryDate;
    }

    public String getDisputeRemarksCode1() {
        return this.disputeRemarksCode1;
    }

    public void setDisputeRemarksCode1(String disputeRemarksCode1) {
        this.disputeRemarksCode1 = disputeRemarksCode1;
    }

    public String getDisputeRemakrsCode2() {
        return this.disputeRemakrsCode2;
    }

    public void setDisputeRemakrsCode2(String disputeRemakrsCode2) {
        this.disputeRemakrsCode2 = disputeRemakrsCode2;
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case MEMBER_SHORTNAME:
                setMemberShortName(value);
            break;
            case ACCOUNT_NUMBER:
                setAccountNumber(value);
            break;
            case ACCOUNT_TYPE:
                setAccountType(value);
            break;
            case OWNERSHIP_INDICATOR:
                setOwnershipIndicator(value);
            break;
            case OPENED_DATE:
                setDateOpened(value);
            break;
            case LASTPAYMENT_DATE:
                setLastPaymentDate(value);
            break;
            case CLOSED_DATE:
                setClosedDate(value);
            break;
            case REPORTED_CERTIFIED_DATE:
                setReportedAndCertifiedDate(value);
            break;
            case SACTIONED_AMOUNT:
                setSanctionedAmount(value);
            break;
            case CURRENT_BALANCE:
                setCurrentBalance(value);
            break;
            case OVERDUE_AMOUNT:
                setOverdueAmount(value);
            break;
            case PAYMENT_HISTORY1:
                setPaymentHistory1(value);
            break;
            case PAYMENT_HISTORY2:
                setPaymentHistory2(value);
            break;
            case PAYMENT_HISTORY_STARTDATE:
                setPaymentHistoryStartDate(value);
            break;
            case PAYMENT_HISTORY_ENDDATE:
                setPaymentHistoryEndDate(value);
            break;
            case SUIT_FILED:
                setSuitFiledCode(value);
            break;
            case WRITTENOFF_STATUS:
                setWrittenOfStatus(value);
            break;
            case COLLATERAL_VALUE:
                setCollateralValue(value);
            break;
            case COLLATERAL_TYPE:
                setCollateralType(value);
            break;
            case CREDIT_LIMIT:
                setCreditLimit(value);
            break;
            case CASH_LIMIT:
                setCashLimit(value);
            break;
            case INTEREST_RATE:
                setInterestRate(value);
            break;
            case PAYMENT_TENURE:
                setRepaymentTenure(value);
            break;
            case EMI_AMOUNT:
                setEmiAmount(value);
            break;
            case TOTAL_WRITTENOFF_AMOUNT:
                setTotalWrittenOfAmount(value);
            break;
            case PRINCIPAL_WRITTENOFF_AMOUNT:
                setPrincipalWrittenOfAmount(value);
            break;
            case SETTLED_AMOUNT:
                setSettledAmount(value);
            break;
            case PAYMENT_FREQUENCY:
                setPaymentFrequency(value);
            break;
            case ACTUAL_PAYMENT_AMOUNT:
                setActualPaymentAmount(value);
            break;
            case ERRORCODE_ENTRY_DATE:
                setErrorCodeEntryDate(value);
            break;
            case ERROR_CODE:
                setErrorCode(value);
            break;
            case CIBILREMARKSCODE_ENTRY_DATE:
                setCibilRemarksEntryDate(value);
            break;
            case CIBILREMARKS_CODE:
                setCibilRemarksCode(value);
            break;
            case DISPUTEREMARKS_CODE_ENTRY_DATE:
                setDisputeRemarksEntryDate(value);
            break;
            case DISPUTE_REMARKS_CODE1:
                setDisputeRemarksCode1(value);
            break;
            case DISPUTE_REMARKS_CODE2:
                setDisputeRemakrsCode2(value);
            break;
        }
    }

}
