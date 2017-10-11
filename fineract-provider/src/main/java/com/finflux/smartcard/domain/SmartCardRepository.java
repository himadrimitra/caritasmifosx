package com.finflux.smartcard.domain;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmartCardRepository extends JpaRepository<SmartCard, Long>, JpaSpecificationExecutor<SmartCard> {

	Collection<SmartCard> findByClientId(final Long clientId);

	SmartCard findByCardNumber(final String cardNumber);
}
