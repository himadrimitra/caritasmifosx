/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.accounting.financialactivityaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when product to GL account mapping are not
 * found.
 */
public class DuplicatePaymentTypeFoundInFinanacialAvtivityMappingException extends AbstractPlatformDomainRuleException {

    private final static String errorCode = "error.msg.payment.type.canoot.map.more.than.one.time";

    public DuplicatePaymentTypeFoundInFinanacialAvtivityMappingException(final Long paymentTypeId) {
        super(errorCode, "PaymentType  with " + paymentTypeId +" already maaped", paymentTypeId);
    }

    public static String getErrorcode() {
        return errorCode;
    }

}