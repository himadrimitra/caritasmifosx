/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;

public enum EmailSupportedEvents {
    LOAN_DISBURSAL(100, "loan_disbursal");

    private final Integer value;
    private final String code;

    private EmailSupportedEvents(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public static EmailSupportedEvents fromInt(final Integer statusValue) {

        EmailSupportedEvents enumeration = EmailSupportedEvents.LOAN_DISBURSAL;
        switch (statusValue) {
            case 100:
                enumeration = EmailSupportedEvents.LOAN_DISBURSAL;
            break;

        }

        return enumeration;

    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

}
