package com.finflux.infrastructure.external.authentication.aadhar.domain;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

public interface AadhaarOutBoundRequestDetailsRepository
        extends JpaRepository<AadhaarOutBoundRequestDetails, Long>, JpaSpecificationExecutor<AadhaarOutBoundRequestDetails> {

    AadhaarOutBoundRequestDetails findByRequestId(@Param("requestId") final String requestId);

    Collection<AadhaarOutBoundRequestDetails> findByAadhaarNumber(@Param("aadhaarNumber") final String aadhaarNumber);
}
