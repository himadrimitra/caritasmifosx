package com.finflux.infrastructure.gis.taluka.domain;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.taluka.exception.TalukaNotFoundException;

@Service
public class TalukaRepositoryWrapper {
    
    private final TalukaRepository repository;
    
    @Autowired
    public TalukaRepositoryWrapper(final TalukaRepository repository) {
        this.repository = repository;
    }

    public Taluka findOneWithNotFoundDetection(final Long talukaId) {
        final Taluka taluka = this.repository.findOne(talukaId);
        if (taluka == null) { throw new TalukaNotFoundException(talukaId); }
        return taluka;
    }

    public void save(final Taluka talukas) {
        this.repository.save(talukas);
    }

    public void saveAndFlush(final Taluka taluka) {
        this.repository.saveAndFlush(taluka);
    }

    public void delete(final Taluka taluka) {
        this.repository.delete(taluka);
    }

}
