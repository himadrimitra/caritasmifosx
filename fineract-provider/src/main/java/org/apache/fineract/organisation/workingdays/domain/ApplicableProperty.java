/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum ApplicableProperty {
    INVALID(0, "applicablePropert.invalid"), //
    INSTALLMENTDATE(1, "applicablePropert.installmentDate");

    private final Integer value;
    private final String code;

    private ApplicableProperty(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public boolean isInstallmentDate() {
        return this.value.equals(ApplicableProperty.INSTALLMENTDATE.getValue());
    }

    public static ApplicableProperty fromInt(final Integer frequency) {
        ApplicableProperty applicableProperty = ApplicableProperty.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    applicableProperty = ApplicableProperty.INSTALLMENTDATE;
                break;
            }
        }
        return applicableProperty;
    }

    public static EnumOptionData applicablePropertyOptionData(final int id) {
        return applicableProperty(ApplicableProperty.fromInt(id));
    }

    public static EnumOptionData applicableProperty(final ApplicableProperty type) {
        EnumOptionData optionData = null;
        switch (type) {
            case INSTALLMENTDATE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "INSTALLMENTDATE");
            break;
            default:
            break;

        }
        return optionData;
    }
}
