/** Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.organisation.workingdays.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.workingdays.api.WorkingDaysApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_non_working_day_reschedule_detail")
public class NonWorkingDayRescheduleDetail extends AbstractPersistable<Long> {

    @Column(name = "from_week_day", nullable = false)
    private String fromWeekDay;

    @Column(name = "repayment_rescheduling_enum", nullable = false)
    private Integer repaymentReschedulingType;

    @Column(name = "to_week_day", nullable = false)
    private String toWeekDay;

    protected  NonWorkingDayRescheduleDetail() {
        
    }
    
    public NonWorkingDayRescheduleDetail(final String fromWeekDay, final Integer repaymentReschedulingType, final String toWeekDay) {
        this.fromWeekDay = fromWeekDay;
        this.repaymentReschedulingType = repaymentReschedulingType;
        this.toWeekDay = toWeekDay;
    }
    
    public void update(final JsonCommand command, final Locale locale, final List<Map<String, Object>> nonWorkingDayRescheduleDetailChangesList) {

        final Map<String, Object> changes = new HashMap<>(3);
        if (command.isChangeInIntegerParameterNamed(WorkingDaysApiConstants.repayment_rescheduling_enum, this.repaymentReschedulingType,
                locale)) {
            final Integer newValue = command.integerValueOfParameterNamed(WorkingDaysApiConstants.repayment_rescheduling_enum, locale);
            changes.put(WorkingDaysApiConstants.repayment_rescheduling_enum, WorkingDaysEnumerations.workingDaysStatusType(newValue));
            this.repaymentReschedulingType = RepaymentRescheduleType.fromInt(newValue).getValue();
        }

        if (command.isChangeInStringParameterNamed(WorkingDaysApiConstants.fromWeekDay, this.fromWeekDay)) {
            final String newValue = command.stringValueOfParameterNamed(WorkingDaysApiConstants.fromWeekDay);
            this.fromWeekDay = newValue;
            changes.put(WorkingDaysApiConstants.fromWeekDay, newValue);
        }

        if (command.isChangeInStringParameterNamed(WorkingDaysApiConstants.toWeekDay, this.toWeekDay)) {
            final String newValue = command.stringValueOfParameterNamed(WorkingDaysApiConstants.toWeekDay);
            this.toWeekDay = newValue;
            changes.put(WorkingDaysApiConstants.toWeekDay, newValue);
        }

        if (!changes.isEmpty()) {
            changes.put(WorkingDaysApiConstants.idParamName, getId());
            nonWorkingDayRescheduleDetailChangesList.add(changes);
        }

    }

    public String getFromWeekDay() {
        return this.fromWeekDay;
    }

    public Integer getRepaymentReschedulingType() {
        return this.repaymentReschedulingType;
    }

    public String getToWeekDay() {
        return this.toWeekDay;
    }
    

}
