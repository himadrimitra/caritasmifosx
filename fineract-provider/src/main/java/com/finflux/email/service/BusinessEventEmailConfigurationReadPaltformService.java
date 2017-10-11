/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package com.finflux.email.service;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

public interface BusinessEventEmailConfigurationReadPaltformService {

    Map<String, Object> retrieveOneWithBuisnessEvent(final BUSINESS_EVENTS businessEvent);
}
