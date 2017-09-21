/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.fileprocess.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum FileProcessType {

    INVALID(0, "fileProcessType.invalid", "invalid", "Invalid"), //
    SND(1, "fileProcessType.sanctioned.but.not.disbursed", "sanctionedButNotDisbursed", "Sanctioned but not disbursed");

    private final Integer value;
    private final String code;
    private final String systemName;
    private final String displayName;

    private FileProcessType(final Integer value, final String code, final String systemName, final String displayName) {
        this.value = value;
        this.code = code;
        this.systemName = systemName;
        this.displayName = displayName;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static FileProcessType fromInt(final Integer type) {
        FileProcessType fileProcessType = FileProcessType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    fileProcessType = FileProcessType.SND;
                break;
            }
        }
        return fileProcessType;
    }
    
    public static FileProcessType fromString(final String type) {
        FileProcessType fileProcessType = FileProcessType.INVALID;
        if (type != null) {
            switch (type) {
                case "sanctionedButNotDisbursed":
                    fileProcessType = FileProcessType.SND;
                break;
            }
        }
        return fileProcessType;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(getValue().longValue(), getSystemName(), getDisplayName());
    }

    public static Collection<EnumOptionData> fileProcessTypeOptions() {
        final Collection<EnumOptionData> fileProcessTypeOptions = new ArrayList<>();
        for (final FileProcessType fileProcessType : values()) {
            if (fileProcessType.isNotInvalid()) {
                fileProcessTypeOptions.add(fileProcessType.getEnumOptionData());
            }
        }
        return fileProcessTypeOptions;
    }

    private boolean isNotInvalid() {
        return !this.value.equals(FileProcessType.INVALID.getValue());
    }
}
