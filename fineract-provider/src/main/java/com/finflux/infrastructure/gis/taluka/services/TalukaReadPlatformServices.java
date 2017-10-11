package com.finflux.infrastructure.gis.taluka.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.taluka.data.TalukaData;

public interface TalukaReadPlatformServices {
    
    TalukaData retrieveOne(final Long talukaId);
  
    Collection<TalukaData> retrieveAllTalukaDataByTalukaIds(final List<Long> talukaIds);
    
    Collection<TalukaData> retrieveAllTalukaDataByDistrictId(final Long districtId);

    Collection<TalukaData> retrieveAllTalukaDataByDistrictIds(final List<Long> districtIds);

}
