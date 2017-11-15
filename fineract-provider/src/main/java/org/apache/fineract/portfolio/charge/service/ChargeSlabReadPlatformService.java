/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.service;

import java.util.Collection;

import org.apache.fineract.portfolio.charge.data.ChargeSlabData;


public interface ChargeSlabReadPlatformService {
    
    Collection<ChargeSlabData> retrieveAllChargeSubSlabsBySlabChargeId(final Long chargeId);

}