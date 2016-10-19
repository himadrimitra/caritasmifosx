/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.domain;

public enum GlimChargeCalculationType {

    INVALID(1, "glimChargeCalculationType.invalid"), ROUND(1, "glimChargeCalculationType.round"), ROUND_WITH_MAX_CHARGE(2,
            "glimChargeCalculationType.roundWithMaxCharge"), ROUND_WITHOUT_MAX_CHARGE(3, "glimChargeCalculationType.roundWithoutMaxCharge");

    private final Integer value;
    private final String code;

    private GlimChargeCalculationType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static GlimChargeCalculationType fromInt(final Integer chargeCalculationType) {
        GlimChargeCalculationType glimChargeCalculationType = GlimChargeCalculationType.INVALID;
        if (chargeCalculationType != null) {
            switch (chargeCalculationType) {
                case 0:
                    glimChargeCalculationType = INVALID;
                break;
                case 1:
                    glimChargeCalculationType = ROUND;
                break;
                case 2:
                    glimChargeCalculationType = ROUND_WITH_MAX_CHARGE;
                break;
                default:
                    glimChargeCalculationType = ROUND_WITHOUT_MAX_CHARGE;
                break;
            }
        }
        return glimChargeCalculationType;
    }

    public static Object[] validValues() {
        return new Object[] { GlimChargeCalculationType.ROUND.getValue(), GlimChargeCalculationType.ROUND_WITH_MAX_CHARGE.getValue(),
                GlimChargeCalculationType.ROUND_WITHOUT_MAX_CHARGE.getValue() };
    }
}
