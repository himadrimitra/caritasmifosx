/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.organisation.workingdays.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum RepaymentRescheduleType {

    INVALID(0, "RepaymentRescheduleType.invalid"), //
    SAME_DAY(1, "RepaymentRescheduleType.same.day"), //
    MOVE_TO_NEXT_WORKING_DAY(2, "RepaymentRescheduleType.move.to.next.working.day"), //
    MOVE_TO_NEXT_REPAYMENT_DAY(3, "RepaymentRescheduleType.move.to.next.repayment.day"), //
    MOVE_TO_PREVIOUS_WORKING_DAY(4, "RepaymentRescheduleType.move.to.previous.working.day"), //
    MOVE_TO_NEXT_MEETING_DAY(5, "RepaymentRescheduleType.move.to.next.meeting.day"),
    MOVE_TO_NEXT_WORKING_WEEK_DAY(6, "RepaymentRescheduleType.move.to.next.working.week.day"),
    MOVE_TO_PREVIOUS_WORKING_WEEK_DAY(7, "RepaymentRescheduleType.move.to.previous.working.week.day");

    private final Integer value;
    private final String code;

    private RepaymentRescheduleType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isMoveToNextRepaymentDay() {
        return this.value.equals(RepaymentRescheduleType.MOVE_TO_NEXT_REPAYMENT_DAY.getValue());
    }
    
    private static final Map<Integer, RepaymentRescheduleType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final RepaymentRescheduleType entityType : RepaymentRescheduleType.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;
        }
    }

    public static RepaymentRescheduleType fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }

    public static int getMinValue() {
        return minValue;
    }

    public static int getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return name().toString();
    }

    public static RepaymentRescheduleType fromInt(final Integer frequency) {
        RepaymentRescheduleType repaymentRescheduleType = RepaymentRescheduleType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    repaymentRescheduleType = RepaymentRescheduleType.SAME_DAY;
                break;
                case 2:
                    repaymentRescheduleType = RepaymentRescheduleType.MOVE_TO_NEXT_WORKING_DAY;
                break;
                case 3:
                    repaymentRescheduleType = RepaymentRescheduleType.MOVE_TO_NEXT_REPAYMENT_DAY;
                break;
                case 4:
                    repaymentRescheduleType = RepaymentRescheduleType.MOVE_TO_PREVIOUS_WORKING_DAY;
                break;
                case 5:
                    repaymentRescheduleType = RepaymentRescheduleType.MOVE_TO_NEXT_MEETING_DAY;
                break;
                case 6:
                    repaymentRescheduleType = RepaymentRescheduleType.MOVE_TO_NEXT_WORKING_WEEK_DAY;
                break;
                case 7:
                    repaymentRescheduleType = RepaymentRescheduleType.MOVE_TO_PREVIOUS_WORKING_WEEK_DAY;
                break;
            }
        }
        return repaymentRescheduleType;
    }

    public static EnumOptionData repaymentRescheduleTypeOptionData(final int id) {
        return repaymentRescheduleType(RepaymentRescheduleType.fromInt(id));
    }

    public static EnumOptionData repaymentRescheduleType(final RepaymentRescheduleType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case SAME_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Same Day");
            break;
            case MOVE_TO_NEXT_WORKING_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Move To Next Working Day");
            break;
            case MOVE_TO_NEXT_REPAYMENT_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Move To Next Repayment Day");
            break;
            case MOVE_TO_PREVIOUS_WORKING_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Move To Previous Working Day");
            break;
            case MOVE_TO_NEXT_MEETING_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Move To Next Meeting Day");
            break;
            case MOVE_TO_NEXT_WORKING_WEEK_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Move To Next Working week Day");
            break;
            case MOVE_TO_PREVIOUS_WORKING_WEEK_DAY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Move To Previous Working week Day");
            break;
            default:
            break;
        }
        return optionData;
    }

    public boolean isSameDay() {
        return this.value.equals(RepaymentRescheduleType.SAME_DAY.getValue());
    }
    
    public boolean isMoveToNextWorkingWeektDay() {
        return this.value.equals(RepaymentRescheduleType.MOVE_TO_NEXT_WORKING_WEEK_DAY.getValue());
    }
    
    public boolean isMoveToPreviousWorkingWeektDay() {
        return this.value.equals(RepaymentRescheduleType.MOVE_TO_PREVIOUS_WORKING_WEEK_DAY.getValue());
    }
    
    public boolean isInvalid() {
        return this.value.equals(RepaymentRescheduleType.INVALID.getValue());
    }
}
