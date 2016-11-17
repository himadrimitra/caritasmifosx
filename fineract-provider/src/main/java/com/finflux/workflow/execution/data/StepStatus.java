package com.finflux.workflow.execution.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */


public enum  StepStatus {
    INACTIVE(1, null,"stepStatus.inactive"),
    INITIATED(2, StepAction.TASKCOMPLETE,"stepStatus.initiated", StepAction.SKIP, StepAction.TASKCOMPLETE),
    UNDERCRITERIACHECK(3, StepAction.CRITERIACHECK,"stepStatus.undercriteriacheck", StepAction.CRITERIACHECK),
    UNDERREVIEW(4, StepAction.REVIEW,"stepStatus.underreview", StepAction.REVIEW, StepAction.REJECT, StepAction.TASKEDIT),
    UNDERAPPROVAL(5, StepAction.APPROVE,"stepStatus.underapproval", StepAction.APPROVE, StepAction.REJECT),
    COMPLETED(7, null,"stepStatus.completed", StepAction.STARTOVER, StepAction.NEXT),
    CANCELLED(8, null,"stepStatus.cancelled", StepAction.STARTOVER, StepAction.NEXT),
    SKIPPED(9, null,"stepStatus.skipped", StepAction.STARTOVER, StepAction.NEXT);


    private final Integer value;
    private final String code;
    private final StepAction nextPositiveAction;
    private final List<StepAction> possibleActionEnums;
    private final List<EnumOptionData> possibleActionsEnumOption;


    StepStatus(final Integer value, StepAction nextPositiveAction , final String code, StepAction... nextActions) {
        this.value = value;
        this.code = code;
        this.nextPositiveAction = nextPositiveAction;
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

    private static final Map<Integer, StepStatus> intToEnumMap = new HashMap<>();
    static {
        for (final StepStatus type : StepStatus.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static StepStatus fromInt(final int i) {
        final StepStatus type = intToEnumMap.get(Integer.valueOf(i));
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

    public StepAction getNextPositiveAction() {
        return nextPositiveAction;
    }
}
