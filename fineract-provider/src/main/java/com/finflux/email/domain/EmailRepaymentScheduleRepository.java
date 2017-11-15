/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * EmailRepaymentScheduleRepository is used to find pending mails during boot up
 *
 */
@Repository
public interface EmailRepaymentScheduleRepository extends JpaRepository<EmailOutboundMessage, Long>, JpaSpecificationExecutor<EmailOutboundMessage> {
    
    Page<EmailOutboundMessage> findByStatus(String status, Pageable pageable);

}