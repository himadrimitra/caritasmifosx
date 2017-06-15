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
import java.util.List;
import java.util.Map;

import org.apache.fineract.organisation.workingdays.exception.WorkingDaysNotFoundException;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link WorkingDaysRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class WorkingDaysRepositoryWrapper {

    private final WorkingDaysRepository repository;
    private final NonWorkingDayRescheduleDetailRepository nonWorkingDayRescheduleDetailRepository; 

    @Autowired
    public WorkingDaysRepositoryWrapper(final WorkingDaysRepository repository,
            final NonWorkingDayRescheduleDetailRepository nonWorkingDayRescheduleDetailRepository) {
        this.repository = repository;
        this.nonWorkingDayRescheduleDetailRepository = nonWorkingDayRescheduleDetailRepository;
    }

    public WorkingDays findOne() {
        final boolean loadNonWorkingDayRescheduleDetail = true;
        return findOne(loadNonWorkingDayRescheduleDetail);
    }
    
    public WorkingDays findOne(final boolean loadNonWorkingDayRescheduleDetail) {
        final List<WorkingDays> workingDaysList = this.repository.findAll();

        if (workingDaysList == null || workingDaysList.isEmpty()) { throw new WorkingDaysNotFoundException(); }
        WorkingDays workingDays =  workingDaysList.get(0);
        if (loadNonWorkingDayRescheduleDetail) {
            List<NonWorkingDayRescheduleDetail> nonWorkingDayRescheduleDetails = this.nonWorkingDayRescheduleDetailRepository.findAll();
            for (NonWorkingDayRescheduleDetail nonWorkingDayRescheduleDetail : nonWorkingDayRescheduleDetails) {
                workingDays.getNonWorkingDayRescheduleDetails().put(
                        CalendarUtils.getWeekDayAsInt(nonWorkingDayRescheduleDetail.getFromWeekDay()), nonWorkingDayRescheduleDetail);
            }
        }
        return workingDays;
    }

    public void save(final WorkingDays workingDays) {
        this.repository.save(workingDays);
    }

    public void saveAndFlush(final WorkingDays workingDays) {
        this.repository.saveAndFlush(workingDays);
    }

    public void delete(final WorkingDays workingDays) {
        this.repository.delete(workingDays);
    }

    public boolean isWorkingDay(LocalDate transactionDate) {
        final WorkingDays workingDays = findOne();
        return WorkingDaysUtil.isWorkingDay(workingDays, transactionDate);
    }
    
    public Map<Long, NonWorkingDayRescheduleDetail> fetchAllNonWorkingDayRescheduleDetail() {
        List<NonWorkingDayRescheduleDetail> nonWorkingDayRescheduleDetails = this.nonWorkingDayRescheduleDetailRepository.findAll();
        Map<Long, NonWorkingDayRescheduleDetail> detailMap = new HashMap<>();
        for (NonWorkingDayRescheduleDetail nonWorkingDayRescheduleDetail : nonWorkingDayRescheduleDetails) {
            detailMap.put(nonWorkingDayRescheduleDetail.getId(), nonWorkingDayRescheduleDetail);
        }
        return detailMap;
    }
    
}