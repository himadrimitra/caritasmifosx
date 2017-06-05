package org.apache.fineract.portfolio.common.domain;

public enum EntityType {

    INVALID(0, "entityType.invalid"), //
    CLIENTS(1, "entityType.clients"), //
    GROUPS(2, "entityType.groups"), //
    CENTERS(3, "entityType.centers"), //
    OFFICES(4, "entityType.offices");// ;

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

    public static EntityType fromInt(final Integer type) {
        EntityType entityType = EntityType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    entityType = EntityType.CLIENTS;
                break;
                case 2:
                    entityType = EntityType.GROUPS;
                break;
                case 3:
                    entityType = EntityType.CENTERS;
                break;
                case 4:
                    entityType = EntityType.OFFICES;
                break;
            }
        }
        return entityType;
    }

    public boolean isClients() {
        return this.value.equals(EntityType.CLIENTS.getValue());
    }

    public boolean isGroups() {
        return this.value.equals(EntityType.GROUPS.getValue());
    }

    public boolean isCenters() {
        return this.value.equals(EntityType.CENTERS.getValue());
    }

    public boolean isOffices() {
        return this.value.equals(EntityType.OFFICES.getValue());
    }
}
