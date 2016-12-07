package com.finflux.task.execution.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */

public enum TaskStatus {
    INACTIVE(1, null, "taskStatus.inactive"), //
    INITIATED(2, TaskActionType.ACTIVITYCOMPLETE, "taskStatus.initiated", TaskActionType.SKIP, TaskActionType.ACTIVITYCOMPLETE), //
    UNDERCRITERIACHECK(3, TaskActionType.CRITERIACHECK, "taskStatus.undercriteriacheck", TaskActionType.CRITERIACHECK), //
    UNDERREVIEW(4, TaskActionType.REVIEW, "taskStatus.underreview", TaskActionType.REVIEW, TaskActionType.REJECT, TaskActionType.TASKEDIT), //
    UNDERAPPROVAL(5, TaskActionType.APPROVE, "taskStatus.underapproval", TaskActionType.APPROVE, TaskActionType.REJECT), //
    COMPLETED(7, null, "taskStatus.completed"), //
    CANCELLED(8, null, "taskStatus.cancelled"), //
    SKIPPED(9, null, "taskStatus.skipped", TaskActionType.ACTIVITYCOMPLETE);

    private final Integer value;
    private final String code;
    private final TaskActionType nextPositiveAction;
    private final List<TaskActionType> possibleActionEnums;
    private final List<EnumOptionData> possibleActionsEnumOption;

    TaskStatus(final Integer value, TaskActionType nextPositiveAction, final String code, TaskActionType... nextActions) {
        this.value = value;
        this.code = code;
        this.nextPositiveAction = nextPositiveAction;
        this.possibleActionEnums = new ArrayList<>();
        this.possibleActionsEnumOption = new ArrayList<>();
        for (final TaskActionType type : nextActions) {
            possibleActionEnums.add(type);
            this.possibleActionsEnumOption.add(type.getEnumOptionData());
        }
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, TaskStatus> intToEnumMap = new HashMap<>();
    static {
        for (final TaskStatus type : TaskStatus.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static TaskStatus fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }

    public List<EnumOptionData> getPossibleActionsEnumOption() {
        return this.possibleActionsEnumOption;
    }

    public List<TaskActionType> getPossibleActionEnums() {
        return possibleActionEnums;
    }

    public TaskActionType getNextPositiveAction() {
        return nextPositiveAction;
    }
}