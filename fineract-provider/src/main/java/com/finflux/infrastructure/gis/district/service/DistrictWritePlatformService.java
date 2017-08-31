/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.infrastructure.gis.district.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface DistrictWritePlatformService {


    CommandProcessingResult createDistrict(JsonCommand command);

    CommandProcessingResult updateDistrict(Long districtId, JsonCommand command);

    CommandProcessingResult activateDistrict(Long districtId, JsonCommand command);

    CommandProcessingResult rejectDistrict(Long districtId, JsonCommand command);

}
