/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.common.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public enum ConditionalOperator {
    BETWEEN("between", "BETWEEN"), //
    LESS_THAN_OR_EQUAL("<=", "LESS THAN OR EQUAL"), //
    GREATER_THAN_OR_EQUAL(">=", "GREATER THAN OR EQUAL"), //
    LESS_THAN("<", "LESS THAN"), //
    GREATER_THAN(">", "GREATER THAN"), //
    EQUAL("=", "EQUAL");
    
    private final String value;
    private final String code;
    
    private ConditionalOperator(final String value, final String code) {
        this.value = value;
        this.code = code;
    }

    public static EnumOptionData from(ConditionalOperator operator){
        return new EnumOptionData(null, operator.getValue(),operator.getCode());
    }
    public String getValue() {
        return this.value;
    }

    
    public String getCode() {
        return this.code;
    }
    
    
}
