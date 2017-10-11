package com.finflux.infrastructure.external.authentication.aadhar.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

@Entity
@Table(name = "f_aadhaar_outbound_request_details")
public class AadhaarOutBoundRequestDetails extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "aadhaar_number", length = 20, nullable = false)
    private String aadhaarNumber;

    @Column(name = "request_id", length = 20, nullable = false)
    private String requestId;

    @Column(name = "purpose", length = 3, nullable = false)
    private Integer purpose;

    @Column(name = "status", length = 3, nullable = false)
    private Integer status;

    protected AadhaarOutBoundRequestDetails() {

    }

    public AadhaarOutBoundRequestDetails(final String aadhaarNumber, final String requestId, final Integer status, final Integer purpose) {
        this.aadhaarNumber = aadhaarNumber;
        this.requestId = requestId;
        this.status = status;
        this.purpose = purpose;
    }

    public static AadhaarOutBoundRequestDetails create(final String aadhaarNumber, final String requestId, final String status) {
        Integer requestStatus = null;
        Integer requestPurpose = null;
        final AadhaarRequestStatusTypeEnum optionData = AadhaarRequestStatusTypeEnum.getEntityType(status);
        if (optionData != null) {
            requestStatus = optionData.getValue();
        }
        final AadhaarRequestPurposeTypeEnum requestOption = AadhaarRequestPurposeTypeEnum.getEntityType(AadhaarApiConstants.REQUESTPURPOSE);
        if (requestOption != null) {
            requestPurpose = requestOption.getValue();
        }
        return new AadhaarOutBoundRequestDetails(aadhaarNumber, requestId, requestStatus, requestPurpose);
    }

    public AadhaarOutBoundRequestDetails update(final String status) {
        if (status.equals(AadhaarApiConstants.SUCCESS)) {
            final AadhaarRequestStatusTypeEnum statusEnum = AadhaarRequestStatusTypeEnum
                    .fromInt(AadhaarRequestStatusTypeEnum.SUCCESS.getValue());
            if (!statusEnum.hasStateOf(AadhaarRequestStatusTypeEnum.fromInt(this.status))) {
                this.status = statusEnum.getValue();
            }
        } else {
            final AadhaarRequestStatusTypeEnum statusEnum = AadhaarRequestStatusTypeEnum
                    .fromInt(AadhaarRequestStatusTypeEnum.FAILURE.getValue());
            if (!statusEnum.hasStateOf(AadhaarRequestStatusTypeEnum.fromInt(this.status))) {
                this.status = statusEnum.getValue();
            }
        }
        return this;
    }

    public String getAadhaarNumber() {
        return this.aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getPurpose() {
        return this.purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
