package com.finflux.risk.creditbureau.provider.cibil.request;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

public class HeaderSegment extends RequestSegment {

    public final static String LEGACY_AUTHORIZATION = "L";
    public final static String ADVANCED_AUTHORIZATION = "A";

    private final static Integer REFERENCE_NUMBER_LENGTH = 25;
    private final static Integer USERNAME_LENGTH = 30;
    private final static Integer PASSWORD_LENGTH = 30;
    private final static Integer LOAN_AMOUNT_LENGTH = 9;

    private final String segmentTag = "TUEF";
    private final Integer version = new Integer(12);
    private String memberReferenceNumber = ""; // 25 bytes
    private final String futureUse1 = "  "; // 2 spaces
    private String userName = "";
    private String password = "";
    private String enquiryPurpose = "01";
    private Long loanAmount = 0L;
    private final String futureUse2 = "   "; // 3 spaces
    private String creditScoreType = "01";
    private String outputFormat = "01";
    private final Integer responseSize = 1; // Supported values is only 1
    private String reportPurpose = "CC"; // CC - CPU to CPU ; TT -
                                         // Tape to Tape
    private final String autherizationMethod = LEGACY_AUTHORIZATION;

    @SuppressWarnings("unused")
    private final CibilRequest request;

    public HeaderSegment(final CibilRequest request) {
        this.request = request;
    }

    @Override
    public String prepareTuefPacket() {
        StringBuilder builder = new StringBuilder();
        builder.append(segmentTag);
        builder.append(String.valueOf(version));
        if (this.memberReferenceNumber != null) {
            builder.append(StringUtils.rightPad(memberReferenceNumber, REFERENCE_NUMBER_LENGTH, " "));
        }

        builder.append(futureUse1);

        if (this.userName != null) {
            builder.append(StringUtils.rightPad(userName, USERNAME_LENGTH, " "));
        }

        if (this.password != null) {
            builder.append(StringUtils.rightPad(password, PASSWORD_LENGTH, " "));
        }

        builder.append(getFormattedLength(enquiryPurpose));
        String amountString = String.valueOf(this.loanAmount);
        builder.append(StringUtils.leftPad(amountString, LOAN_AMOUNT_LENGTH, "0"));
        builder.append(futureUse2);
        builder.append(creditScoreType);
        builder.append(outputFormat);
        builder.append(responseSize);
        builder.append(reportPurpose);
        builder.append(autherizationMethod);
        return builder.toString();
    }

    public void setMemberReferenceNumber(String memberReferenceNumber) {
        if (!StringUtils.isEmpty(memberReferenceNumber)) {
            this.memberReferenceNumber = memberReferenceNumber;
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnquiryPurpose(String enquiryPurpose) {
        this.enquiryPurpose = enquiryPurpose;
    }

    public void setLoanAmount(final BigDecimal loanAmount) {
        this.loanAmount = loanAmount.longValue();
        String value = String.valueOf(this.loanAmount);
        if (value.length() > LOAN_AMOUNT_LENGTH) { throw new RuntimeException(); }
    }

    public void setCreditScoreType(String creditScoreType) {
        this.creditScoreType = creditScoreType;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public void setReportPurpose(String reportPurpose) {
        this.reportPurpose = reportPurpose;
    }

    public String getSegmentTag() {
        return this.segmentTag;
    }

    public Integer getVersion() {
        return this.version;
    }

    public String getMemberReferenceNumber() {
        return this.memberReferenceNumber;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEnquiryPurpose() {
        return this.enquiryPurpose;
    }

    public Long getLoanAmount() {
        return this.loanAmount;
    }

    public String getCreditScoreType() {
        return this.creditScoreType;
    }

    public String getOutputFormat() {
        return this.outputFormat;
    }

    public Integer getResponseSize() {
        return this.responseSize;
    }

    public String getReportPurpose() {
        return this.reportPurpose;
    }

    public String getAutherizationMethod() {
        return this.autherizationMethod;
    }
}
