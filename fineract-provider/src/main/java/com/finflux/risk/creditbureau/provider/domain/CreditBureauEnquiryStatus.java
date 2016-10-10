/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.finflux.risk.creditbureau.provider.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Enum representation of loan status states.
 */
public enum CreditBureauEnquiryStatus {

    INITIATED(0, "INITIATED"), //
    ACKNOWLEDGED(1, "ACKNOWLEDGED"), //
    PENDING(2, "PENDING"), //
    SUCCESS(3, "SUCCESS"), //
    EXPIRED(4, "EXPIRED"), //
    ERROR(11, "ERROR"), //
    INTERNAL_ERROR(12, "INTERNAL ERROR"), //
    INVALID(10, "INVALID");

    private final Integer value;
    private final String code;

    public static CreditBureauEnquiryStatus fromInt(final Integer statusValue) {

        CreditBureauEnquiryStatus enumeration = CreditBureauEnquiryStatus.INVALID;
        switch (statusValue) {
            case 0:
                enumeration = CreditBureauEnquiryStatus.INITIATED;
            break;
            case 1:
                enumeration = CreditBureauEnquiryStatus.ACKNOWLEDGED;
            break;
            case 2:
                enumeration = CreditBureauEnquiryStatus.PENDING;
            break;
            case 3:
                enumeration = CreditBureauEnquiryStatus.SUCCESS;
            break;
            case 4:
                enumeration = CreditBureauEnquiryStatus.EXPIRED;
            break;
            case 11:
                enumeration = CreditBureauEnquiryStatus.ERROR;
            break;
            case 12:
                enumeration = CreditBureauEnquiryStatus.INTERNAL_ERROR;
            break;
        }
        return enumeration;
    }

    public static EnumOptionData creditBureauEnquiryStatus(final int id) {
        return creditBureauEnquiryStatus(CreditBureauEnquiryStatus.fromInt(id));
    }

    public static EnumOptionData creditBureauEnquiryStatus(final CreditBureauEnquiryStatus type) {
        EnumOptionData optionData = null;
        switch (type) {
            case ACKNOWLEDGED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "ACKNOWLEDGED");
            break;
            case PENDING:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "PENDING");
            break;
            case SUCCESS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "SUCCESS");
            break;
            case EXPIRED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "EXPIRED");
            break;
            case ERROR:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "ERROR");
            break;
            case INTERNAL_ERROR:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "INTERNAL ERROR");
            break;
            default:
            break;
        }
        return optionData;
    }

    private CreditBureauEnquiryStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public boolean hasStateOf(final CreditBureauEnquiryStatus state) {
        return this.value.equals(state.getValue());
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isInValid() {
        if (this.equals(CreditBureauEnquiryStatus.SUCCESS)) { return false; }
        return true;
    }

    public boolean isSuccess() {
        return this.equals(CreditBureauEnquiryStatus.SUCCESS);
    }

    public boolean isPending() {
        return this.equals(CreditBureauEnquiryStatus.PENDING);
    }
}