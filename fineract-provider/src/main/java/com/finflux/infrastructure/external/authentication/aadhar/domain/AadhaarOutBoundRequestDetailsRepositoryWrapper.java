package com.finflux.infrastructure.external.authentication.aadhar.domain;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.aadhar.exception.AadhaarNumberNotFoundException;
import com.finflux.infrastructure.external.authentication.aadhar.exception.RequestIdNotFoundException;
import com.finflux.smartcard.exception.SmartCardNotFoundException;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

@Service
public class AadhaarOutBoundRequestDetailsRepositoryWrapper {

    private final AadhaarOutBoundRequestDetailsRepository aadhaarServicesRepository;

    @Autowired
    public AadhaarOutBoundRequestDetailsRepositoryWrapper(final AadhaarOutBoundRequestDetailsRepository aadhaarServicesRepository) {
        this.aadhaarServicesRepository = aadhaarServicesRepository;
    }

    public AadhaarOutBoundRequestDetails findRequestIdWithNotFoundDetection(final String requestId) {
        final AadhaarOutBoundRequestDetails entity = this.aadhaarServicesRepository.findByRequestId(requestId);
        if (entity == null) { throw new RequestIdNotFoundException(requestId); }
        return entity;
    }

    public Collection<AadhaarOutBoundRequestDetails> findOneWithNotFoundDetection(final String aadhaarNumber) {
        final Collection<AadhaarOutBoundRequestDetails> aadhaarServices = this.aadhaarServicesRepository.findByAadhaarNumber(aadhaarNumber);
        if (aadhaarServices == null) { throw new AadhaarNumberNotFoundException(aadhaarNumber); }
        return aadhaarServices;
    }

    public void save(final AadhaarOutBoundRequestDetails aadhaarServices) {
        this.aadhaarServicesRepository.save(aadhaarServices);
    }

    public void saveAndFlush(final AadhaarOutBoundRequestDetails aadhaarServices) {
        this.aadhaarServicesRepository.saveAndFlush(aadhaarServices);
    }
}
