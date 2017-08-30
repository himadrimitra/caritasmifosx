package org.apache.fineract.organisation.office.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum OfficeStatus {

    INVALID(0, "officeStatus.invalid", "Invalid"), //
    PENDING(100, "officeStatus.pending", "Pending"), //
    ACTIVE(300, "officeStatus.active", "Active"), //
    REJECTED(400, "officeStatus.rejected", "Rejected");

    private final Integer value;
    private final String code;
    private final String name;

    private OfficeStatus(final Integer value, final String code, final String name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    private static final Map<Integer, OfficeStatus> intToEnumMap = new HashMap<>();
    private static final Map<String, OfficeStatus> entityNameToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final OfficeStatus entityType : OfficeStatus.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            entityNameToEnumMap.put(entityType.getName().toLowerCase(), entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;

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

    public static OfficeStatus fromInt(final int i) {
        final OfficeStatus entityType = intToEnumMap.get(Integer.valueOf(i));
        return entityType;
    }

    public static OfficeStatus fromString(final String str) {
        final OfficeStatus entityType = entityNameToEnumMap.get(str.toLowerCase());
        return entityType;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(getValue().longValue(), getCode(), name());
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> officeStatusOptions = new ArrayList<>();
        for (final OfficeStatus enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                officeStatusOptions.add(enumOptionData);
            }
        }
        return officeStatusOptions;
    }
    
    public boolean isPending() {
        return this.value.equals(OfficeStatus.PENDING.getValue());
    }
}
