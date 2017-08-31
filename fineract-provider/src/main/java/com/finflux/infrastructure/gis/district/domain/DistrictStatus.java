/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.infrastructure.gis.district.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum DistrictStatus {

    INVALID(0, "districtStatus.invalid", "Invalid"), //
    PENDING(100, "districtStatus.pending", "Pending"), //
    ACTIVE(300, "districtStatus.active", "Active"), //
    REJECTED(400, "districtStatus.rejected", "Rejected");

    private final Integer value;
    private final String code;
    private final String name;

    private DistrictStatus(final Integer value, final String code, final String name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    private static final Map<Integer, DistrictStatus> intToEnumMap = new HashMap<>();
    private static final Map<String, DistrictStatus> entityNameToEnumMap = new HashMap<>();
    static {
        for (final DistrictStatus districtStatus : DistrictStatus.values()) {
            intToEnumMap.put(districtStatus.value, districtStatus);
            entityNameToEnumMap.put(districtStatus.getName().toLowerCase(), districtStatus);
        }
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public static DistrictStatus fromInt(final int i) {
        final DistrictStatus districtStatus = intToEnumMap.get(Integer.valueOf(i));
        return districtStatus;
    }

    public static DistrictStatus fromString(final String str) {
        final DistrictStatus districtStatus = entityNameToEnumMap.get(str.toLowerCase());
        return districtStatus;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(getValue().longValue(), getCode(), name());
    }

    public static Collection<EnumOptionData> districtStatusOptions() {
        final Collection<EnumOptionData> districtStatusOptions = new ArrayList<>();
        for (final DistrictStatus enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                districtStatusOptions.add(enumOptionData);
            }
        }
        return districtStatusOptions;
    }

    public boolean isPending() {
        return this.value.equals(DistrictStatus.PENDING.getValue());
    }
}
