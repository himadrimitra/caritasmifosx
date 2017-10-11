package com.finflux.infrastructure.gis.taluka.exception;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TalukaNotFoundException extends AbstractPlatformResourceNotFoundException {
    
    public TalukaNotFoundException(final Long id) {
        super("error.msg.taluka.with.id.does.not.exist", "Taluka with identifier " + id + " does not exist", id);
    }

}
