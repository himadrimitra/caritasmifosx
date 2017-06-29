package org.apache.fineract.portfolio.common.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum EntityType {

    INVALID(0, "entityType.invalid"), //
    CLIENT(1, "entityType.client"), //
    GROUP(2, "entityType.group"), //
    CENTER(3, "entityType.center"), //
    OFFICE(4, "entityType.office"), //
    LOAN(5, "entityType.loan");

    private final Integer value;
    private final String code;

    private EntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static EnumOptionData type(final int id) {
        return type(EntityType.fromInt(id));
    }

    public static EnumOptionData type(final EntityType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CLIENT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Client");
            break;
            case GROUP:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Group");
            break;
            case CENTER:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Center");
            break;
            case OFFICE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Office");
            break;
            case LOAN:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Loan");
            break;
            default:
            break;
        }
        return optionData;
    }

    public static EntityType fromInt(final Integer type) {
        EntityType entityType = EntityType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    entityType = EntityType.CLIENT;
                break;
                case 2:
                    entityType = EntityType.GROUP;
                break;
                case 3:
                    entityType = EntityType.CENTER;
                break;
                case 4:
                    entityType = EntityType.OFFICE;
                break;
                case 5:
                    entityType = EntityType.LOAN;
                break;
            }
        }
        return entityType;
    }

    public boolean isInvalid() {
        return this.value.equals(EntityType.INVALID.getValue());
    }

    public boolean isClient() {
        return this.value.equals(EntityType.CLIENT.getValue());
    }

    public boolean isGroup() {
        return this.value.equals(EntityType.GROUP.getValue());
    }

    public boolean isCenter() {
        return this.value.equals(EntityType.CENTER.getValue());
    }

    public boolean isOffice() {
        return this.value.equals(EntityType.OFFICE.getValue());
    }

    public boolean isLoan() {
        return this.value.equals(EntityType.LOAN.getValue());
    }

    private static final Map<String, EntityType> entityTypes = new HashMap<>();

    static {
        for (final EntityType entityType : EntityType.values()) {
            entityTypes.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static EntityType getEntityType(final String entityType) {
        return entityTypes.get(entityType.toLowerCase());
    }
}
