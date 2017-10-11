package org.apache.fineract.portfolio.loanproduct.domain;

import java.util.Arrays;
import java.util.List;

public enum ValueEntityType {

    INVALID(0, null), //
    ENUM_VALUE(1, Arrays.asList(ClientProfileType.LEGAL_FORM)), //
    CODE_VALUE(2, Arrays.asList(ClientProfileType.CLIENT_TYPE, ClientProfileType.CLIENT_CLASSIFICATION));

    private final Integer value;
    private final List<ClientProfileType> ProfileType;

    private ValueEntityType(final Integer value, final List<ClientProfileType> ProfileType) {
        this.value = value;
        this.ProfileType = ProfileType;
    }

    public Integer getValue() {
        return this.value;
    }

    public List<ClientProfileType> getProfileTypes() {
        return this.ProfileType;
    }

    public static boolean isEnumValue(final ClientProfileType clientProfileType) {
        if (ENUM_VALUE.getProfileTypes().contains(clientProfileType)) { return true; }
        return false;
    }

    public static boolean isCodeValue(final ClientProfileType clientProfileType) {
        if (CODE_VALUE.getProfileTypes().contains(clientProfileType)) { return true; }
        return false;
    }

    public boolean isEnumValue() {
        return this.value.equals(ValueEntityType.ENUM_VALUE.getValue());
    }

    public boolean isCodeValue() {
        return this.value.equals(ValueEntityType.CODE_VALUE.getValue());
    }
    
    public static ValueEntityType getValueEntityTypeByClientProfileType(final Integer clientProfileType) {
        ValueEntityType valueEntityType = ValueEntityType.INVALID;
        if (ValueEntityType.isEnumValue(ClientProfileType.fromInt(clientProfileType))) {
            valueEntityType = ValueEntityType.ENUM_VALUE;
        } else if (ValueEntityType.isCodeValue(ClientProfileType.fromInt(clientProfileType))) {
            valueEntityType = ValueEntityType.CODE_VALUE;
        }
        return valueEntityType;
    }

    public static ValueEntityType fromInt(final Integer type) {
        ValueEntityType valueEntityType = ValueEntityType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    valueEntityType = ValueEntityType.ENUM_VALUE;
                break;
                case 2:
                    valueEntityType = ValueEntityType.CODE_VALUE;
                break;
            }
        }
        return valueEntityType;
    }
}