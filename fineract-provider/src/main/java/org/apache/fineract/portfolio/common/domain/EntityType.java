package org.apache.fineract.portfolio.common.domain;

public enum EntityType {

    INVALID(0, "entityType.invalid","invalid"), //
    CLIENTS(1, "entityType.clients","clients"), //
    GROUPS(2, "entityType.groups","groups"), //
    CENTERS(3, "entityType.centers","centers"), //
    OFFICES(4, "entityType.offices","offices"),
    TASKS(5, "entityType.tasks","tasks"),
    STAFF(6,"entityType.STAFF","staff");// ;

    private final Integer value;
    private final String code;
    private final String displayName;

    private EntityType(final Integer value, final String code,final String displayName) {
        this.value = value;
        this.code = code;
        this.displayName=displayName;
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
                case 5:
                    entityType= EntityType.TASKS;
                break;
                case 6:
                    entityType= EntityType.STAFF;
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

    
    public String getDisplayName() {
        return this.displayName;
    }

    public static EntityType getEntityTypeByString(String entityType) {
        for(EntityType entity:EntityType.values())
        {
            if(entity.getDisplayName().equalsIgnoreCase(entityType)){
                return entity;
            }
        }
        return null;
    }
}
