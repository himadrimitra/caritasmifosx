/** Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class NonWorkingDayRescheduleData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String fromWeekDay;
    @SuppressWarnings("unused")
    private final EnumOptionData repaymentReschedulingType;
    @SuppressWarnings("unused")
    private final String toWeekDay;

    public NonWorkingDayRescheduleData(final Long id,final String fromWeekDay, final EnumOptionData repaymentReschedulingType, final String toWeekDay) {
        this.id = id;
        this.fromWeekDay = fromWeekDay;
        this.repaymentReschedulingType = repaymentReschedulingType;
        this.toWeekDay = toWeekDay;
    }

}
