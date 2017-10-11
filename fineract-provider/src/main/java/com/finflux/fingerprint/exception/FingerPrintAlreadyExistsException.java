package com.finflux.fingerprint.exception;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class FingerPrintAlreadyExistsException extends AbstractPlatformDomainRuleException {

    public FingerPrintAlreadyExistsException(final Long clientId, final Integer fingerId) {
        super("error.msg." + fingerId + ".finger.with.fingerId.already.exists",
                "FingerPrint with fingerId`" + fingerId + "` already exists for client `" + clientId + "`", fingerId, clientId);
    }

}
