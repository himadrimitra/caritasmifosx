/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;

public enum EmailStatus {

    PENDING(100, "Pending"), SUCCESS(200, "Success");

    private final Integer value;
    private final String code;

    private EmailStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public static EmailStatus fromInt(final Integer statusValue) {

        EmailStatus enumeration = EmailStatus.PENDING;
        switch (statusValue) {
            case 100:
                enumeration = EmailStatus.PENDING;
            break;
            case 300:
                enumeration = EmailStatus.SUCCESS;
            break;

        }

        return enumeration;

    }

    public boolean hasStateOf(final EmailStatus state) {
        return this.value.equals(state.getValue());
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isPending() {
        return this.value.equals(EmailStatus.PENDING.getValue());
    }

    public boolean isSuccess() {
        return this.value.equals(EmailStatus.SUCCESS.getValue());
    }

}
