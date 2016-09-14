package com.finflux.fingerprint.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

public class FingerPrintData {

    private final Long clientId;
    private final Integer fingerId;
    private final String fingerData;
    private final Collection<EnumOptionData> fingerOptions;
    

    private FingerPrintData(final Long clientId,final Integer fingerId, final String fingerData,final Collection<EnumOptionData> fingerOptions) {
        this.fingerId = fingerId;
        this.fingerData = fingerData;
        this.clientId = clientId;
        this.fingerOptions = fingerOptions;
    }

    public static FingerPrintData instance(final Long clientId,final Integer fingerId, final String fingerData,final Collection<EnumOptionData> fingerOptions) {

        return new FingerPrintData(clientId,fingerId, fingerData,fingerOptions);
    }

    public Long getClientId(){
        return this.clientId;
    }
    public Integer getFingerId() {
        return this.fingerId;
    }

    public String getFingerData() {
        return this.fingerData;
    }
    
    public Collection<EnumOptionData> getFingerOptions() {
        return this.fingerOptions;
    }

}
