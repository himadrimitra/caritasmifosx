/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.accounting.glaccount.domain;


public enum GLClassificationType {

    Cash(1, "glClassificationType.Cash"), Bank(2, "glClassificationType.Bank"), OtherJV(3, "glClassificationType.OtherJV");

    private final Integer value;
    private final String code;

    private GLClassificationType(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static GLClassificationType fromInt(final Integer frequency) {
        GLClassificationType glClassificationType = GLClassificationType.Cash;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    glClassificationType = GLClassificationType.Cash;
                break;
                case 2:
                    glClassificationType = GLClassificationType.Bank;
                break;
                case 3:
                    glClassificationType = GLClassificationType.OtherJV;
                break;
            }
        }
        return glClassificationType;
    }
    
    public static GLClassificationType fromString(final String name) {
        GLClassificationType glClassificationType = null;
        if (name != null) {
            switch (name) {
                case "Cash":
                    glClassificationType = GLClassificationType.Cash;
                break;
                case "Bank":
                    glClassificationType = GLClassificationType.Bank;
                break;
                case "Other JV":
                    glClassificationType = GLClassificationType.OtherJV;
                break;
            }
        }
        return glClassificationType;
    }
}