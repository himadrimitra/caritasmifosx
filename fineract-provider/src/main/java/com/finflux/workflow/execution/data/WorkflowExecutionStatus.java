package com.finflux.workflow.execution.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */


public enum WorkflowExecutionStatus {
    INITIATED(1, "stepStatus.survey", StepAction.SKIP),
    UNDERREVIEW(2, "stepStatus.survey", StepAction.REVIEW, StepAction.REJECT),
    UNDERAPPROVAL(3, "stepStatus.survey", StepAction.APPROVE, StepAction.REJECT),
    UNDERCOMPLETE(4, "stepStatus.question"),
    COMPLETED(5, "stepStatus.question", StepAction.STARTOVER),
    CANCELLED(6, "stepStatus.question", StepAction.STARTOVER),
    SKIPPED(7, "stepStatus.question", StepAction.STARTOVER),
    INACTIVE(8, "stepStatus.question");

    private final Integer value;
    private final String code;
    private final List<StepAction> possibleActionEnums;
    private final List<EnumOptionData> possibleActionsEnumOption;


    WorkflowExecutionStatus(final Integer value, final String code, StepAction... nextActions) {
        this.value = value;
        this.code = code;
        this.possibleActionEnums = new ArrayList<>();
        this.possibleActionsEnumOption = new ArrayList<>();
        for(final StepAction type: nextActions){
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

    private static final Map<Integer, WorkflowExecutionStatus> intToEnumMap = new HashMap<>();
    static {
        for (final WorkflowExecutionStatus type : WorkflowExecutionStatus.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static WorkflowExecutionStatus fromInt(final int i) {
        final WorkflowExecutionStatus type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }

    public List<EnumOptionData> getPossibleActionsEnumOption() {
        return this.possibleActionsEnumOption;
    }

    public List<StepAction> getPossibleActionEnums() {
        return possibleActionEnums;
    }

}
