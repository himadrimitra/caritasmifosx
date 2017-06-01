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
package org.apache.fineract.portfolio.common.service;

import static org.apache.fineract.portfolio.common.service.CommonEnumerations.conditionType;
import static org.apache.fineract.portfolio.common.service.CommonEnumerations.daysInMonthType;
import static org.apache.fineract.portfolio.common.service.CommonEnumerations.daysInYearType;
import static org.apache.fineract.portfolio.common.service.CommonEnumerations.termFrequencyType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.common.domain.ConditionType;
import org.apache.fineract.portfolio.common.domain.ConditionalOperator;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.common.domain.DaysInMonthType;
import org.apache.fineract.portfolio.common.domain.DaysInYearType;
import org.apache.fineract.portfolio.common.domain.NthDayType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.springframework.stereotype.Service;

@Service
public class DropdownReadPlatformServiceImpl implements DropdownReadPlatformService {

    @Override
    public List<EnumOptionData> retrievePeriodFrequencyTypeOptions() {
        final List<EnumOptionData> loanTermFrequencyOptions = Arrays.asList(termFrequencyType(PeriodFrequencyType.DAYS, "frequency"),
                termFrequencyType(PeriodFrequencyType.WEEKS, "frequency"), termFrequencyType(PeriodFrequencyType.MONTHS, "frequency"),
                termFrequencyType(PeriodFrequencyType.YEARS, "frequency"));
        return loanTermFrequencyOptions;
    }

    @Override
    public List<EnumOptionData> retrieveConditionTypeOptions() {
        final List<EnumOptionData> loanTermFrequencyOptions = Arrays.asList(conditionType(ConditionType.EQUAL, "condition"),
                conditionType(ConditionType.NOT_EQUAL, "condition"), conditionType(ConditionType.GRETERTHAN, "condition"),
                conditionType(ConditionType.LESSTHAN, "condition"));
        return loanTermFrequencyOptions;
    }

    @Override
    public List<EnumOptionData> retrieveDaysInMonthTypeOptions() {

        final List<EnumOptionData> daysInMonthTypeOptions = Arrays.asList(daysInMonthType(DaysInMonthType.ACTUAL),
                daysInMonthType(DaysInMonthType.DAYS_30));
        return daysInMonthTypeOptions;
    }

    @Override
    public List<EnumOptionData> retrieveDaysInYearTypeOptions() {

        final List<EnumOptionData> daysInYearTypeOptions = Arrays.asList(daysInYearType(DaysInYearType.ACTUAL),
                daysInYearType(DaysInYearType.DAYS_360), daysInYearType(DaysInYearType.DAYS_364), daysInYearType(DaysInYearType.DAYS_365), daysInYearType(DaysInYearType.DAYS_240));
        return daysInYearTypeOptions;
    }

    @Override
    public List<EnumOptionData> retrieveConditionalOperatorOptions() {

        return Arrays.asList(ConditionalOperator.from(ConditionalOperator.BETWEEN),ConditionalOperator.from(ConditionalOperator.LESS_THAN),ConditionalOperator.from(ConditionalOperator.LESS_THAN_OR_EQUAL),
                ConditionalOperator.from(ConditionalOperator.GREATER_THAN),ConditionalOperator.from(ConditionalOperator.GREATER_THAN_OR_EQUAL),ConditionalOperator.from(ConditionalOperator.EQUAL));
    }
    
    @Override
    public Collection<EnumOptionData> retrieveNthDayTypeOptions(final String codePrefix) {
        return Arrays.asList(CommonEnumerations.nthDayType(NthDayType.ONE, codePrefix),
                CommonEnumerations.nthDayType(NthDayType.TWO, codePrefix), CommonEnumerations.nthDayType(NthDayType.THREE, codePrefix),
                CommonEnumerations.nthDayType(NthDayType.FOUR, codePrefix), CommonEnumerations.nthDayType(NthDayType.LAST, codePrefix),
                CommonEnumerations.nthDayType(NthDayType.ONDAY, codePrefix));
    }

    @Override
    public Collection<EnumOptionData> retrieveDayOfWeekTypeOptions(final String codePrefix) {
        return Arrays.asList(CommonEnumerations.dayOfWeekType(DayOfWeekType.SUNDAY, codePrefix),
                CommonEnumerations.dayOfWeekType(DayOfWeekType.MONDAY, codePrefix),
                CommonEnumerations.dayOfWeekType(DayOfWeekType.TUESDAY, codePrefix),
                CommonEnumerations.dayOfWeekType(DayOfWeekType.WEDNESDAY, codePrefix),
                CommonEnumerations.dayOfWeekType(DayOfWeekType.THURSDAY, codePrefix),
                CommonEnumerations.dayOfWeekType(DayOfWeekType.FRIDAY, codePrefix),
                CommonEnumerations.dayOfWeekType(DayOfWeekType.SATURDAY, codePrefix));
    }

}
