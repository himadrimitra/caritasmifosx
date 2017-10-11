package com.finflux.task.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */

public enum TaskActionType {

    ACTIVITYCOMPLETE(1, "taskActionType.taskcomplete", 3, true, true,false), //
    CRITERIACHECK(2, "taskActionType.criteriacheck", 4, true, true, false), //
    REVIEW(3, "taskActionType.review", 5, true, true, false), //
    APPROVE(4, "taskActionType.approve", 7, true, true, false), //
    // NEXT(5, "taskActionType.next",null,true, false),
    REJECT(6, "taskActionType.reject", 8, true, true,true), //
    SKIP(7, "taskActionType.skip", 9, true, true,false), //
    STARTOVER(8, "taskActionType.startover", 2, true, true, false), //
    TASKEDIT(9, "taskActionType.taskedit", 2, false, true,false), //
    TASKVIEW(10, "taskActionType.taskview", null, false, true, false), DISABLE(11, "taskActionType.disable", 1, false,
            true, false);

    private final Integer value;
    private final String code;
    private final Integer toStatus;
    private final boolean clickable;// whether to show as button or not
    private final boolean checkPermission;
    private final boolean enableByDefault;

    TaskActionType(final Integer value, final String code, Integer toStatus, boolean clickable,
                   boolean checkPermission, boolean enableByDefault) {
        this.value = value;
        this.code = code;
        this.toStatus = toStatus;
        this.clickable = clickable;
        this.checkPermission = checkPermission;
        this.enableByDefault = enableByDefault;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, TaskActionType> intToEnumMap = new HashMap<>();
    private static final Map<String, TaskActionType> stringToEnumMap = new HashMap<>();
    static {
        for (final TaskActionType type : TaskActionType.values()) {
            intToEnumMap.put(type.value, type);
            stringToEnumMap.put(type.name().toLowerCase(), type);
        }
    }

    public static TaskActionType fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }

    public static TaskActionType fromString(final String str) {
        if(StringUtils.isNotEmpty(str)) {
            return stringToEnumMap.get(str.toLowerCase());
        }else{
            return null;
        }
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name().toLowerCase());
    }

    public TaskStatusType getToStatus() {
        if (toStatus == null) { return null; }
        return TaskStatusType.fromInt(toStatus);
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isCheckPermission() {
        return checkPermission;
    }

    public boolean isEnableByDefault() {
        return enableByDefault;
    }
}
