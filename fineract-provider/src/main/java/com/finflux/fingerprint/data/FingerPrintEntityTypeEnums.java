package com.finflux.fingerprint.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.fingerprint.api.FingerPrintApiConstants;

public enum FingerPrintEntityTypeEnums {
    
    INVALID(0, "fingerIdEntityType.invalid"), 
    RIGHT_THUMB(1,"fingerIdEntityType.right-thumb"),
    RIGHT_INDEX(2,"fingerIdEntityType.right-index"),
    RIGHT_MIDDLE(3,"fingerIdEntityType.right-middle"),
    RIGHT_RING(4,"fingerIdEntityType.right-ring"),
    RIGHT_PINKY(5,"fingerIdEntityType.right-pinky"),
    LEFT_THUMB(6,"fingerIdEntityType.left-thumb"),
    LEFT_INDEX(7,"fingerIdEntityType.left-index"),
    LEFT_MIDDLE(8,"fingerIdEntityType.left-middle"),
    LEFT_RING(9,"fingerIdEntityType.left-ring"),
    LEFT_PINKY(10,"fingerIdEntityType.left-pinky");
    
    private final Integer value;
    private final String code;

    private FingerPrintEntityTypeEnums(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }
    
    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public static FingerPrintEntityTypeEnums fromInt(final Integer frequency) {
        FingerPrintEntityTypeEnums fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_THUMB;
                break;
                case 2:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_INDEX;
                break;
                case 3:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_MIDDLE;
                break;
                case 4:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_RING;
                break;
                case 5:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_PINKY;
                break;
                case 6:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_THUMB;
                break;
                case 7:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_INDEX;
                break;
                case 8:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_MIDDLE;
                break;
                case 9:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_RING;
                break;
                case 10:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_PINKY;
                break;
            }
        }
        return fingerPrintEntityTypeEnums;
    }
    
    public static FingerPrintEntityTypeEnums fromString(final String frequency) {
        FingerPrintEntityTypeEnums fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case FingerPrintApiConstants.enumTypeRightThumb:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_THUMB;
                break;
                case FingerPrintApiConstants.enumTypeRightIndex:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_INDEX;
                break;
                case FingerPrintApiConstants.enumTypeRightMiddle:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_MIDDLE;
                break;
                case FingerPrintApiConstants.enumTypeRightRing:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_RING;
                break;
                case FingerPrintApiConstants.enumTypeRightPinky:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.RIGHT_PINKY;
                break;
                case FingerPrintApiConstants.enumTypeLeftThumb:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_THUMB;
                break;
                case FingerPrintApiConstants.enumTypeLeftIndex:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_INDEX;
                break;
                case FingerPrintApiConstants.enumTypeLeftMiddle:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_MIDDLE;
                break;
                case FingerPrintApiConstants.enumTypeLeftRing:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_RING;
                break;
                case FingerPrintApiConstants.enumTypeLeftPinky:
                    fingerPrintEntityTypeEnums = FingerPrintEntityTypeEnums.LEFT_PINKY;
                break;

            }
        }
        return fingerPrintEntityTypeEnums;
    }
    
    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final FingerPrintEntityTypeEnums enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final FingerPrintEntityTypeEnums enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }
    
    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> fingerPrintEntityOptions = new ArrayList<>();
        for (final FingerPrintEntityTypeEnums enumType : values()) {
            final EnumOptionData enumOptionData = fingerPrintEntity(enumType.getValue());
            if (enumOptionData != null) {
                fingerPrintEntityOptions.add(enumOptionData);
            }
        }
        return fingerPrintEntityOptions;
    }

    public static EnumOptionData fingerPrintEntity(final int id) {
        return fingerPrintEntity(FingerPrintEntityTypeEnums.fromInt(id));
    }
    
    public static EnumOptionData fingerPrintEntity(final FingerPrintEntityTypeEnums type) {
        EnumOptionData optionData = null;
        switch (type) {
            case RIGHT_THUMB:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeRightThumb);
            break;
            case RIGHT_INDEX:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeRightIndex);
            break;
            case RIGHT_MIDDLE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeRightMiddle);
            break;
            case RIGHT_RING:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeRightRing);
            break;
            case RIGHT_PINKY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeRightPinky);
            break;
            case LEFT_THUMB:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeLeftThumb);
            break;
            case LEFT_INDEX:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeLeftIndex);
            break;
            case LEFT_MIDDLE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeLeftMiddle);
            break;
            case LEFT_RING:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeLeftRing);
            break;
            case LEFT_PINKY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), FingerPrintApiConstants.enumTypeLeftPinky);
            break;
            
            default:
            break;

        }
        return optionData;
    }

    public boolean isRightThumb() {
        return this.value.equals(FingerPrintEntityTypeEnums.RIGHT_THUMB.getValue());
    }

    public boolean isRightIndex() {
        return this.value.equals(FingerPrintEntityTypeEnums.RIGHT_INDEX.getValue());
    }

    public boolean isRightMiddle() {
        return this.value.equals(FingerPrintEntityTypeEnums.RIGHT_MIDDLE.getValue());
    }

    public boolean isRightRing() {
        return this.value.equals(FingerPrintEntityTypeEnums.RIGHT_RING.getValue());
    }
    
    public boolean isRightPinky() {
        return this.value.equals(FingerPrintEntityTypeEnums.RIGHT_PINKY.getValue());
    }
   
    public boolean isLeftThumb() {
        return this.value.equals(FingerPrintEntityTypeEnums.LEFT_THUMB.getValue());
    }

    public boolean isLeftIndex() {
        return this.value.equals(FingerPrintEntityTypeEnums.LEFT_INDEX.getValue());
    }

    public boolean isLeftMiddle() {
        return this.value.equals(FingerPrintEntityTypeEnums.LEFT_MIDDLE.getValue());
    }

    public boolean isLeftRing() {
        return this.value.equals(FingerPrintEntityTypeEnums.LEFT_RING.getValue());
    }
    
    public boolean isLeftPinky() {
        return this.value.equals(FingerPrintEntityTypeEnums.LEFT_PINKY.getValue());
    }
    
    private static final Map<Integer, FingerPrintEntityTypeEnums> entityTypeNameToEnumMap = new HashMap<>();

    static {
        for (final FingerPrintEntityTypeEnums entityType : FingerPrintEntityTypeEnums.values()) {
            entityTypeNameToEnumMap.put(entityType.value, entityType);
        }
    }
    
    public static FingerPrintEntityTypeEnums getEntityType(Integer entityType) {
        return entityTypeNameToEnumMap.get(entityType);
    }
    
}
