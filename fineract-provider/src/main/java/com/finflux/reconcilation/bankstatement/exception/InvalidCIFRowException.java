/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.exception;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class InvalidCIFRowException extends AbstractPlatformDomainRuleException {

    public InvalidCIFRowException(Map<Integer, List<String>> errorRows) {
        super(getError(errorRows), "error.msg.excel.rows.invalid", errorRows);
    }

    public static String getError(Map<Integer, List<String>> errorRows) {
        String error = "CPIF File contains invalid rows. ";
        for (Map.Entry<Integer, List<String>> entry : errorRows.entrySet()) {
            String row = " Row no. " + entry.getKey() + " have invalid column/s " + entry.getValue();
            error += row;
        }
        return error;
    }

}
