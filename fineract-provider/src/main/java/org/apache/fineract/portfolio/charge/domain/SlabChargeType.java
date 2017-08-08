/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum SlabChargeType {
    INVALID(0, "slabChargeType.installment.invalid", "INVALID"), //
    INSTALLMENT_AMOUNT(1, "slabChargeType.installment.amount", "Installment Amount"), // loan
                                                                                      // amount
                                                                                      // based
                                                                                      // slabs
    INSTALLMENT_NUMBER(2, "slabChargeType.installment.number", "Installment Number"); // number
                                                                                      // of
                                                                                      // repayment
                                                                                      // based
                                                                                      // slabs;

    private final Integer value;
    private final String code;
    private final String displayValue;

    private SlabChargeType(Integer value, String code, String displayValue) {
        this.value = value;
        this.code = code;
        this.displayValue = displayValue;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public static EnumOptionData fromInt(final Integer slabType) {
        EnumOptionData slabChargeType = null;
        if (slabType != null) {
            switch (slabType) {
                case 1:
                    slabChargeType = new EnumOptionData(SlabChargeType.INSTALLMENT_AMOUNT.getValue().longValue(), SlabChargeType.INSTALLMENT_AMOUNT.getCode(),
                            SlabChargeType.INSTALLMENT_AMOUNT.getDisplayValue());
                break;
                case 2:
                    slabChargeType = new EnumOptionData(SlabChargeType.INSTALLMENT_NUMBER.getValue().longValue(), SlabChargeType.INSTALLMENT_NUMBER.getCode(),
                            SlabChargeType.INSTALLMENT_NUMBER.getDisplayValue());
                break;
            }
        }
        return slabChargeType;
    }

}
