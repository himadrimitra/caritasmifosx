package com.finflux.common.domain;

public enum ActionType {

    INVALID(0, "actionType.invalid", "invalid", "Invalid"), //
    LOCK(1, "actionType.lock", "lock", "Lock"), //
    UNLOCK(2, "actionType.unlock", "unlock", "Unlock");

    private final Integer value;
    private final String code;
    private final String systemName;
    private final String displayName;

    private ActionType(final Integer value, final String code, final String systemName, final String displayName) {
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

    public static ActionType fromInt(final Integer value) {
        ActionType actionType = ActionType.INVALID;
        if (value != null) {
            switch (value) {
                case 1:
                    actionType = ActionType.LOCK;
                break;
                case 2:
                    actionType = ActionType.UNLOCK;
                break;
            }
        }
        return actionType;
    }

    public boolean isInvalid() {
        return this.value.equals(ActionType.INVALID.getValue());
    }

    public boolean isLock() {
        return this.value.equals(ActionType.LOCK.getValue());
    }

    public boolean isUnlock() {
        return this.value.equals(ActionType.UNLOCK.getValue());
    }
}
