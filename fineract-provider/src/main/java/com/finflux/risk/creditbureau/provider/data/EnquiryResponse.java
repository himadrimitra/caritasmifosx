package com.finflux.risk.creditbureau.provider.data;

import java.util.Date;

import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;

public class EnquiryResponse {

    final String acknowledgementNumber;
    final String request;
    final String response;
    final Date reportGeneratedTime;
    final String fileName;
    final String reportId;
    final CreditBureauEnquiryStatus status;

    public EnquiryResponse(final String acknowledgementNumber, final String request, final String response, final Date reportGeneratedTime,
            final String fileName, CreditBureauEnquiryStatus status, final String reportId) {
        this.acknowledgementNumber = acknowledgementNumber;
        this.request = request;
        this.response = response;
        this.reportGeneratedTime = reportGeneratedTime;
        this.fileName = fileName;
        this.status = status;
        this.reportId = reportId;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public String getRequest() {
        return replacePasswordInRequest();
    }

    private String replacePasswordInRequest() {
        if (request != null && request.indexOf("<PWD>") > 0) { return request.replace(request.substring(request.indexOf("<PWD>") + 5, request.indexOf("</PWD>")), "XXXXX"); }
        if (request != null && request.indexOf("<Password>") > 0) { return request.replace(request.substring(request.indexOf("<Password>") + 10, request.indexOf("</Password>")), "XXXXX"); }
        return null;
    }

    public String getResponse() {
        return response;
    }

    public Date getReportGeneratedTime() {
        return reportGeneratedTime;
    }

    public String getFileName() {
        return fileName;
    }

    public CreditBureauEnquiryStatus getStatus() {
        return status;
    }

    public String getReportId() {
        return reportId;
    }
}
