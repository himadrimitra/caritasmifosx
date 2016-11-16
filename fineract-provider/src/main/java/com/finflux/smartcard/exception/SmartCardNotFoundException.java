package com.finflux.smartcard.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

public class SmartCardNotFoundException extends AbstractPlatformResourceNotFoundException {
	
	public SmartCardNotFoundException(final String cardNumber) {
        super("error.msg.id.invalid", "SmartCard Number with identifier " + cardNumber + " does not exist", cardNumber);

    }


}
