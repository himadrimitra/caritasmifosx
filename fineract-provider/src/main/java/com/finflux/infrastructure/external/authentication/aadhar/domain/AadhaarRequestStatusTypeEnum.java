package com.finflux.infrastructure.external.authentication.aadhar.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
public enum AadhaarRequestStatusTypeEnum {

    INITIATE(0, "aadhaarRequestStatusType.initiate"),
    SUCCESS(1, "aadhaarRequestStatusType.success"),
    FAILURE(2,"aadhaarRequestStatusType.failure");

    /**
     * aadhaar request status Types Enumerations
     */
    public static final String enumTypeInitiate = "INITIATE";
    public static final String enumTypeSuccess = "SUCCESS";
    public static final String enumTypeFailure = "FAILURE";

    private final Integer value;
    private final String code;

    private AadhaarRequestStatusTypeEnum(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean hasStateOf(final AadhaarRequestStatusTypeEnum state) {
        return this.value.equals(state.getValue());
    }

    public static AadhaarRequestStatusTypeEnum fromInt(final Integer frequency) {
        AadhaarRequestStatusTypeEnum aadhaarRequestTypeEnums = AadhaarRequestStatusTypeEnum.INITIATE;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    aadhaarRequestTypeEnums = AadhaarRequestStatusTypeEnum.SUCCESS;
                break;
                case 2:
                    aadhaarRequestTypeEnums = AadhaarRequestStatusTypeEnum.FAILURE;
                break;
            }
        }
        return aadhaarRequestTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final AadhaarRequestStatusTypeEnum enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final AadhaarRequestStatusTypeEnum enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> requestStatusTypeOptions = new ArrayList<>();
        for (final AadhaarRequestStatusTypeEnum enumType : values()) {
            final EnumOptionData enumOptionData = aadhaarRequestEntity(enumType.getValue());
            if (enumOptionData != null) {
                requestStatusTypeOptions.add(enumOptionData);
            }
        }
        return requestStatusTypeOptions;
    }

    public static EnumOptionData aadhaarRequestEntity(final int id) {
        return aadhaarRequestEntity(AadhaarRequestStatusTypeEnum.fromInt(id));
    }

    public static EnumOptionData aadhaarRequestEntity(final AadhaarRequestStatusTypeEnum type) {
        EnumOptionData optionData = null;
        switch (type) {
            case INITIATE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AadhaarRequestStatusTypeEnum.enumTypeInitiate);
            break;
            case SUCCESS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AadhaarRequestStatusTypeEnum.enumTypeSuccess);
            break;
            case FAILURE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AadhaarRequestStatusTypeEnum.enumTypeFailure);
            break;
            default:
            break;

        }
        return optionData;
    }

    public boolean isInitiate() {
        return this.value.equals(AadhaarRequestStatusTypeEnum.INITIATE.getValue());
    }

    public boolean isSuccess() {
        return this.value.equals(AadhaarRequestStatusTypeEnum.SUCCESS.getValue());
    }

    public boolean isFailure() {
        return this.value.equals(AadhaarRequestStatusTypeEnum.FAILURE.getValue());
    }

    private static final Map<String, AadhaarRequestStatusTypeEnum> entityTypeNameToEnumMap = new HashMap<>();

    static {
        for (final AadhaarRequestStatusTypeEnum entityType : AadhaarRequestStatusTypeEnum.values()) {
            entityTypeNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static AadhaarRequestStatusTypeEnum getEntityType(String entityType) {
        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
    }
}
