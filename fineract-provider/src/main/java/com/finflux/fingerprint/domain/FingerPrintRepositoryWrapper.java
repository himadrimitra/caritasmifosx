package com.finflux.fingerprint.domain;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.fingerprint.exception.FingerPrintNotFoundException;

@Service
public class FingerPrintRepositoryWrapper {

    private final FingerPrintRepository fingerPrintRepository;
    
    @Autowired
    public FingerPrintRepositoryWrapper(final FingerPrintRepository fingerPrintRepository){
        this.fingerPrintRepository = fingerPrintRepository;
    }
    
    public Collection<FingerPrint> findEntityIdWithNotFoundDetection(final Long clientId){
        final Collection<FingerPrint> entity = this.fingerPrintRepository.findByClientId(clientId); 
        if(entity == null){
            throw new FingerPrintNotFoundException(clientId); 
        }
        return entity;
    }
    
    public void save(final FingerPrint fingerPrint){
        this.fingerPrintRepository.save(fingerPrint);
    }
}
