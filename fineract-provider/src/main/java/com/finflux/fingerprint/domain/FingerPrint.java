package com.finflux.fingerprint.domain;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.fingerprint.api.FingerPrintApiConstants;

@Entity
@Table(name = "f_client_fingerprint")
public class FingerPrint extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "finger_id", length = 3, nullable = false)
    private Integer fingerId;

    @Column(name = "finger_print", length = 5000, nullable = false)
    private String fingerPrint;

    protected FingerPrint() {

    }

    private FingerPrint(final Long clientId, final Integer fingerId, final String fingerPrint) {
        this.clientId = clientId;
        this.fingerId = fingerId;
        this.fingerPrint = fingerPrint;
    }

    public static FingerPrint create(final Long clientId, final JsonCommand command) {
        final Integer fingerId = command.integerValueOfParameterNamed(FingerPrintApiConstants.fingerIdParamName);
        final String fingerPrint = command.stringValueOfParameterNamed(FingerPrintApiConstants.fingerDataParamName);
        return new FingerPrint(clientId, fingerId, fingerPrint);
    }

    public Long getClientId() {
        return this.clientId;
    }

    public Integer getFingerId() {
        return this.fingerId;
    }

    public String getFingerPrint() {
        return this.fingerPrint;
    }

}
