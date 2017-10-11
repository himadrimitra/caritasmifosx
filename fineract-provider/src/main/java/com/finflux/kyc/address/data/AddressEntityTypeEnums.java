package com.finflux.kyc.address.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.kyc.address.api.AddressApiConstants;

public enum AddressEntityTypeEnums {

    INVALID(0, "addressEntityType.invalid"), //
    CLIENTS(1, "addressEntityType.clients"), //
    GROUPS(2, "addressEntityType.groups"), //
    CENTERS(3, "addressEntityType.centers"), //
    OFFICES(4, "addressEntityType.offices"), //
    BUSINESSCORRESPONDENTS(5, "addressEntityType.businesscorrespondents"),
    VILLAGES(6,"addressEntityType.villages");// ;

    private final Integer value;
    private final String code;

    private AddressEntityTypeEnums(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static AddressEntityTypeEnums fromInt(final Integer frequency) {
        AddressEntityTypeEnums addressEntityTypeEnums = AddressEntityTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    addressEntityTypeEnums = AddressEntityTypeEnums.CLIENTS;
                break;
                case 2:
                    addressEntityTypeEnums = AddressEntityTypeEnums.GROUPS;
                break;
                case 3:
                    addressEntityTypeEnums = AddressEntityTypeEnums.CENTERS;
                break;
                case 4:
                    addressEntityTypeEnums = AddressEntityTypeEnums.OFFICES;
                break;
                case 5:
                    addressEntityTypeEnums = AddressEntityTypeEnums.BUSINESSCORRESPONDENTS;
                break;
                case 6:
                    addressEntityTypeEnums = AddressEntityTypeEnums.VILLAGES;
                break;
            }
        }
        return addressEntityTypeEnums;
    }

    public static AddressEntityTypeEnums fromString(final String frequency) {
        AddressEntityTypeEnums addressEntityTypeEnums = AddressEntityTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case AddressApiConstants.enumTypeClients:
                    addressEntityTypeEnums = AddressEntityTypeEnums.CLIENTS;
                break;
                case AddressApiConstants.enumTypeGroups:
                    addressEntityTypeEnums = AddressEntityTypeEnums.GROUPS;
                break;
                case AddressApiConstants.enumTypeCenters:
                    addressEntityTypeEnums = AddressEntityTypeEnums.CENTERS;
                break;
                case AddressApiConstants.enumTypeOffices:
                    addressEntityTypeEnums = AddressEntityTypeEnums.OFFICES;
                break;
                case AddressApiConstants.enumTypeBusinessCorrespondents:
                    addressEntityTypeEnums = AddressEntityTypeEnums.BUSINESSCORRESPONDENTS;
                break;
                case AddressApiConstants.enumTypeVillages:
                    addressEntityTypeEnums = AddressEntityTypeEnums.VILLAGES;
                break;

            }
        }
        return addressEntityTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final AddressEntityTypeEnums enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final AddressEntityTypeEnums enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> addressEntityOptions = new ArrayList<>();
        for (final AddressEntityTypeEnums enumType : values()) {
            final EnumOptionData enumOptionData = addressEntity(enumType.getValue());
            if (enumOptionData != null) {
                addressEntityOptions.add(enumOptionData);
            }
        }
        return addressEntityOptions;
    }

    public static EnumOptionData addressEntity(final int id) {
        return addressEntity(AddressEntityTypeEnums.fromInt(id));
    }

    public static EnumOptionData addressEntity(final AddressEntityTypeEnums type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CLIENTS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AddressApiConstants.enumTypeClients);
            break;
            case GROUPS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AddressApiConstants.enumTypeGroups);
            break;
            case CENTERS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AddressApiConstants.enumTypeCenters);
            break;
            case OFFICES:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AddressApiConstants.enumTypeOffices);
            break;
            case BUSINESSCORRESPONDENTS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AddressApiConstants.enumTypeBusinessCorrespondents);
            break;
            case VILLAGES:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), AddressApiConstants.enumTypeVillages);
            break;
            default:
            break;

        }
        return optionData;
    }

    public boolean isClients() {
        return this.value.equals(AddressEntityTypeEnums.CLIENTS.getValue());
    }

    public boolean isGroups() {
        return this.value.equals(AddressEntityTypeEnums.GROUPS.getValue());
    }

    public boolean isCenters() {
        return this.value.equals(AddressEntityTypeEnums.CENTERS.getValue());
    }

    public boolean isOffice() {
        return this.value.equals(AddressEntityTypeEnums.OFFICES.getValue());
    }
    
    public boolean isBusinessCorrespondents() {
        return this.value.equals(AddressEntityTypeEnums.BUSINESSCORRESPONDENTS.getValue());
    }
    public boolean isVillages() {
        return this.value.equals(AddressEntityTypeEnums.VILLAGES.getValue());
    }
    
    private static final Map<String, AddressEntityTypeEnums> entityTypeNameToEnumMap = new HashMap<>();

    static {
        for (final AddressEntityTypeEnums entityType : AddressEntityTypeEnums.values()) {
            entityTypeNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }
    
    public static AddressEntityTypeEnums getEntityType(String entityType) {
        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
    }
}
