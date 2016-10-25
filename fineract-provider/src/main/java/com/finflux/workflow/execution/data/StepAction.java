package com.finflux.workflow.execution.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */


public enum StepAction {
    TASKCOMPLETE(1, "stepActionType.taskcomplete", 3, false),
    CRITERIACHECK(2, "stepActionType.criteriacheck", 4, true),
    REVIEW(3, "stepActionType.review",5,true),
    APPROVE(4, "stepActionType.approve",6,true),
    NEXT(5, "stepActionType.next",7,true),
    REJECT(6, "stepActionType.reject",8,true),
    SKIP(7, "stepActionType.skip",2,true),
    STARTOVER(8, "stepActionType.startover",2,false),
    TASKEDIT(9, "stepActionType.taskedit", 2, false),
    TASKVIEW(10, "stepActionType.taskview",null,false);

    private final Integer value;
    private final String code;
    private final Integer toStatus;
    private final boolean clickable;//whether to show as button or not


    StepAction(final Integer value, final String code, Integer toStatus, boolean clickable) {
        this.value = value;
        this.code = code;
        this.toStatus = toStatus;
        this.clickable = clickable;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, StepAction> intToEnumMap = new HashMap<>();
    static {
        for (final StepAction type : StepAction.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static StepAction fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }
    
    public  EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }

    public StepStatus getToStatus() {
        if(toStatus==null){
            return null;
        }
        return StepStatus.fromInt(toStatus);
    }

    public boolean isClickable() {
        return clickable;
    }
}
