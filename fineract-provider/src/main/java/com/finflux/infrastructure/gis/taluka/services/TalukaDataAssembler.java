package com.finflux.infrastructure.gis.taluka.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.district.domain.District;
import com.finflux.infrastructure.gis.district.domain.DistrictRepositoryWrapper;
import com.finflux.infrastructure.gis.taluka.domain.Taluka;

@Service
public class TalukaDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final DistrictRepositoryWrapper districtRepository;

    @Autowired
    public TalukaDataAssembler(final FromJsonHelper fromApiJsonHelper, final CodeValueRepositoryWrapper codeValueRepository,
            final DistrictRepositoryWrapper districtRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.codeValueRepository = codeValueRepository;
        this.districtRepository = districtRepository;
    }

    public Taluka createTaluka(final Long entityId, JsonCommand command) {

        District district = null;
        final Long districtId = entityId;
        if (districtId != null) {
            district = this.districtRepository.findOneWithNotFoundDetection(districtId);
        }
        return Taluka.create(command, district);

    }
}
