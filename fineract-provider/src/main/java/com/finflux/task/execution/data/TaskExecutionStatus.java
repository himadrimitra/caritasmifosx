package com.finflux.task.execution.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */

public enum TaskExecutionStatus {

    INITIATED(1, "taskExecutionStatus.initiated", TaskActionType.SKIP), //
    UNDERREVIEW(2, "taskExecutionStatus.underreview", TaskActionType.REVIEW, TaskActionType.REJECT), //
    UNDERAPPROVAL(3, "taskExecutionStatus.underapproval", TaskActionType.APPROVE, TaskActionType.REJECT), //
    UNDERCOMPLETE(4, "taskExecutionStatus.undercomplete"), //
    COMPLETED(5, "taskExecutionStatus.completed", TaskActionType.STARTOVER), //
    CANCELLED(6, "taskExecutionStatus.cancelled", TaskActionType.STARTOVER), //
    SKIPPED(7, "taskExecutionStatus.skipped", TaskActionType.STARTOVER), //
    INACTIVE(8, "taskExecutionStatus.inactive");

    private final Integer value;
    private final String code;
    private final List<TaskActionType> possibleActionEnums;
    private final List<EnumOptionData> possibleActionsEnumOption;

    TaskExecutionStatus(final Integer value, final String code, TaskActionType... nextActions) {
        this.value = value;
        this.code = code;
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

    private static final Map<Integer, TaskExecutionStatus> intToEnumMap = new HashMap<>();
    static {
        for (final TaskExecutionStatus type : TaskExecutionStatus.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static TaskExecutionStatus fromInt(final int i) {
        final TaskExecutionStatus type = intToEnumMap.get(Integer.valueOf(i));
        return type;
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

}
