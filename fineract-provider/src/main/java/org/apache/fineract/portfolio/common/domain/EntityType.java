package org.apache.fineract.portfolio.common.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum EntityType {

    INVALID(0, "entityType.invalid", "invalid", "invalid"), //
    CLIENT(1, "entityType.client", "client", "client"), //
    GROUP(2, "entityType.group", "group", "group"), //
    CENTER(3, "entityType.center", "center", "center"), //
    OFFICE(4, "entityType.office", "office", "office"), //
    TASK(5, "entityType.tasks", "task", "task"), //
    STAFF(6, "entityType.STAFF", "staff", "staff"), // ;
    LOAN(7, "entityType.loan", "loan", "loan"), //
    SAVINGS(8, "entityType.savings", "savings", "savings"), //

    ADDRESS(51, "entityType.address", "address", "Address"), //
    DOCUMENT(52, "entityType.document", "document", "Document"), //
    FAMILY_DETAILS(53, "entityType.family.details", "familyDetails", "Family details"), //
    BANK_ACCOUNT_DETAILS(54, "entityType.bank.account.details", "bankAccountDetails", "Bank account details"), //

    CLIENT_IDENTIFIER(101, "entityType.client.identifier", "clientIdentifier", "Client identifier"), //
    CLIENT_INCOME_EXPENSE(102, "entityType.client.income.expense", "clientIncomeExpense", "Client income expense"), //
    EXISTING_LOAN(103, "entityType.existing.loan", "existingLoan", "Existing loan");

    private final Integer value;
    private final String code;
    // Can not change the values...
    // It is used for some conditions checking in code level
    private final String systemName;
    private final String displayName;

    private EntityType(final Integer value, final String code, final String systemName, final String displayName) {
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
            case TASK:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Task");
            break;
            case STAFF:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Staff");
            break;
            case SAVINGS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "savings");
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
                    entityType = EntityType.TASK;
                break;
                case 6:
                    entityType = EntityType.STAFF;
                break;
                case 7:
                    entityType = EntityType.LOAN;
                break;
                case 8:
                    entityType = EntityType.SAVINGS;
                break;
                case 51:
                    entityType = EntityType.ADDRESS;
                break;
                case 52:
                    entityType = EntityType.DOCUMENT;
                break;
                case 53:
                    entityType = EntityType.FAMILY_DETAILS;
                break;
                case 54:
                    entityType = EntityType.BANK_ACCOUNT_DETAILS;
                break;
                case 101:
                    entityType = EntityType.CLIENT_IDENTIFIER;
                break;
                case 102:
                    entityType = EntityType.CLIENT_INCOME_EXPENSE;
                break;
                case 103:
                    entityType = EntityType.EXISTING_LOAN;
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

    public boolean isSavings() {
        return this.value.equals(EntityType.SAVINGS.getValue());
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

    public String getDisplayName() {
        return this.displayName;
    }

    public static EntityType getEntityTypeByString(String entityType) {
        for (EntityType entity : EntityType.values()) {
            if (entity.getDisplayName().equalsIgnoreCase(entityType)) { return entity; }
        }
        return null;
    }
}
