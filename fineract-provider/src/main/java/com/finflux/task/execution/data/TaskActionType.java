package com.finflux.task.execution.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */

public enum TaskActionType {

    ACTIVITYCOMPLETE(1, "taskActionType.taskcomplete", 3, false, true), //
    CRITERIACHECK(2, "taskActionType.criteriacheck", 4, true, true), //
    REVIEW(3, "taskActionType.review", 5, true, true), //
    APPROVE(4, "taskActionType.approve", 7, true, true), //
    // NEXT(5, "taskActionType.next",null,true, false),
    REJECT(6, "taskActionType.reject", 8, true, true), //
    SKIP(7, "taskActionType.skip", 9, true, true), //
    STARTOVER(8, "taskActionType.startover", 2, false, true), //
    TASKEDIT(9, "taskActionType.taskedit", 2, false, true), //
    TASKVIEW(10, "taskActionType.taskview", null, false, true);

    private final Integer value;
    private final String code;
    private final Integer toStatus;
    private final boolean clickable;// whether to show as button or not
    private final boolean checkPermission;

    TaskActionType(final Integer value, final String code, Integer toStatus, boolean clickable, boolean checkPermission) {
        this.value = value;
        this.code = code;
        this.toStatus = toStatus;
        this.clickable = clickable;
        this.checkPermission = checkPermission;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, TaskActionType> intToEnumMap = new HashMap<>();
    static {
        for (final TaskActionType type : TaskActionType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static TaskActionType fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }

    public TaskStatus getToStatus() {
        if (toStatus == null) { return null; }
        return TaskStatus.fromInt(toStatus);
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isCheckPermission() {
        return checkPermission;
    }
}
