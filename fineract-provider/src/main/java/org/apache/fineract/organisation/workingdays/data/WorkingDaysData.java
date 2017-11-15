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
package org.apache.fineract.organisation.workingdays.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class WorkingDaysData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String recurrence;

    @SuppressWarnings("unused")
    private final EnumOptionData repaymentRescheduleType;

    @SuppressWarnings("unused")
    private final Boolean extendTermForDailyRepayments;

    // template date
    @SuppressWarnings("unused")
    private final Collection<EnumOptionData> repaymentRescheduleOptions;

    @SuppressWarnings("unused")
    private final Collection<EnumOptionData> repaymentRescheduleOptionsForAdvancedReschedule;

    @SuppressWarnings("unused")
    private Collection<NonWorkingDayRescheduleData> advancedRescheduleDetail;

    public WorkingDaysData(final Long id, final String recurrence, final EnumOptionData repaymentRescheduleType,
            final Boolean extendTermForDailyRepayments) {
        this.id = id;
        this.recurrence = recurrence;
        this.repaymentRescheduleType = repaymentRescheduleType;
        this.repaymentRescheduleOptions = null;
        this.extendTermForDailyRepayments = extendTermForDailyRepayments;
        this.repaymentRescheduleOptionsForAdvancedReschedule = null;
    }

    public WorkingDaysData(final Long id, final String recurrence, final EnumOptionData repaymentRescheduleType,
            final Collection<EnumOptionData> repaymentRescheduleOptions, final Boolean extendTermForDailyRepayments,
            final Collection<EnumOptionData> repaymentRescheduleOptionsForAdvancedReschedule) {
        this.id = id;
        this.recurrence = recurrence;
        this.repaymentRescheduleType = repaymentRescheduleType;
        this.repaymentRescheduleOptions = repaymentRescheduleOptions;
        this.extendTermForDailyRepayments = extendTermForDailyRepayments;
        this.repaymentRescheduleOptionsForAdvancedReschedule = repaymentRescheduleOptionsForAdvancedReschedule;
    }

    public void setAdvancedRescheduleDetail(final Collection<NonWorkingDayRescheduleData> advancedRescheduleDetail) {
        this.advancedRescheduleDetail = advancedRescheduleDetail;
    }
}