package com.finflux.infrastructure.external.authentication.aadhar.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

public enum AadhaarRequestPurposeTypeEnum {

    BANKACCOUNT(0, "aadhaarRequestPurposeType.bankAccount"),
    EKYC(1, "aadhaarRequestPurposeType.ekyc");

    /**
     * aadhaar request status Types Enumerations
     */
    public static final String enumTypeBankAccount = "INITIATE";
    public static final String enumTypeEkyc = "SUCCESS";

    private final Integer value;
    private final String code;

    private AadhaarRequestPurposeTypeEnum(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean hasStateOf(final AadhaarRequestPurposeTypeEnum state) {
        return this.value.equals(state.getValue());
    }

    public static AadhaarRequestPurposeTypeEnum fromInt(final Integer frequency) {
        AadhaarRequestPurposeTypeEnum aadhaarRequestTypeEnums = AadhaarRequestPurposeTypeEnum.BANKACCOUNT;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    aadhaarRequestTypeEnums = AadhaarRequestPurposeTypeEnum.EKYC;
                break;
            }
        }
        return aadhaarRequestTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final AadhaarRequestPurposeTypeEnum enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final AadhaarRequestPurposeTypeEnum enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> requestStatusTypeOptions = new ArrayList<>();
        for (final AadhaarRequestPurposeTypeEnum enumType : values()) {
            final EnumOptionData enumOptionData = aadhaarRequestEntity(enumType.getValue());
            if (enumOptionData != null) {
                requestStatusTypeOptions.add(enumOptionData);
            }
        }
        return requestStatusTypeOptions;
    }

    public static EnumOptionData aadhaarRequestEntity(final int id) {
        return aadhaarRequestEntity(AadhaarRequestPurposeTypeEnum.fromInt(id));
    }

    public static EnumOptionData aadhaarRequestEntity(final AadhaarRequestPurposeTypeEnum type) {
        EnumOptionData optionData = null;
        switch (type) {
            case BANKACCOUNT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        AadhaarRequestPurposeTypeEnum.enumTypeBankAccount);
            break;
            case EKYC:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AadhaarRequestPurposeTypeEnum.enumTypeEkyc);
            break;
            default:
            break;

        }
        return optionData;
    }

    public boolean isBankAccount() {
        return this.value.equals(AadhaarRequestPurposeTypeEnum.BANKACCOUNT.getValue());
    }

    public boolean isEkycRequest() {
        return this.value.equals(AadhaarRequestPurposeTypeEnum.EKYC.getValue());
    }

    private static final Map<String, AadhaarRequestPurposeTypeEnum> entityTypeNameToEnumMap = new HashMap<>();

    static {
        for (final AadhaarRequestPurposeTypeEnum entityType : AadhaarRequestPurposeTypeEnum.values()) {
            entityTypeNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static AadhaarRequestPurposeTypeEnum getEntityType(String entityType) {
        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
    }
}
