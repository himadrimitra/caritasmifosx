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
package org.apache.fineract.organisation.workingdays.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.workingdays.api.WorkingDaysApiConstants;
import org.apache.fineract.organisation.workingdays.data.WorkingDayValidator;
import org.apache.fineract.organisation.workingdays.domain.NonWorkingDayRescheduleDetail;
import org.apache.fineract.organisation.workingdays.domain.NonWorkingDayRescheduleDetailRepository;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.apache.fineract.organisation.workingdays.exception.NonWorkingDayRescheduleDetailNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.property.RRule;

@Service
public class WorkingDaysWritePlatformServiceJpaRepositoryImpl implements WorkingDaysWritePlatformService {

    private final WorkingDaysRepositoryWrapper daysRepositoryWrapper;
    private final WorkingDayValidator fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final NonWorkingDayRescheduleDetailRepository nonWorkingDayRescheduleDetailRepository;

    @Autowired
    public WorkingDaysWritePlatformServiceJpaRepositoryImpl(final WorkingDaysRepositoryWrapper daysRepositoryWrapper,
            final WorkingDayValidator fromApiJsonDeserializer, final FromJsonHelper fromApiJsonHelper,
            final NonWorkingDayRescheduleDetailRepository nonWorkingDayRescheduleDetailRepository) {
        this.daysRepositoryWrapper = daysRepositoryWrapper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.nonWorkingDayRescheduleDetailRepository = nonWorkingDayRescheduleDetailRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateWorkingDays(JsonCommand command) {
        String recurrence = "";
        RRule rrule = null;
        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json());
            final WorkingDays workingDays = this.daysRepositoryWrapper.findOne(false);

            recurrence = command.stringValueOfParameterNamed(WorkingDaysApiConstants.recurrence);
            rrule = new RRule(recurrence);
            rrule.validate();
            rrule.getRecur().getDayList();

            Map<String, Object> changes = workingDays.update(command);
            this.daysRepositoryWrapper.save(workingDays);
            updateNonWorkingDayRescheduleDetails(command, rrule, changes);
            
            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(workingDays.getId()).with(changes)
                    .build();
        } catch (final ValidationException e) {
            throw new PlatformDataIntegrityException("error.msg.invalid.recurring.rule",
                    "The Recurring Rule value: " + recurrence + " is not valid.", "recurrence", recurrence);
        } catch (final IllegalArgumentException | ParseException e) {
            throw new PlatformDataIntegrityException("error.msg.recurring.rule.parsing.error",
                    "Error in passing the Recurring Rule value: " + recurrence, "recurrence", e.getMessage());
        }
    }
    
    
    private void updateNonWorkingDayRescheduleDetails(JsonCommand command, RRule rrule, Map<String, Object> changes) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(WorkingDaysApiConstants.WORKING_DAYS_RESOURCE_NAME + "." + WorkingDaysApiConstants.advancedRescheduleDetail);
        final JsonElement element = this.fromApiJsonHelper.parse(command.json());
        List<NonWorkingDayRescheduleDetail> nonWorkingDayRescheduleDetails = new ArrayList<>();
        List<NonWorkingDayRescheduleDetail> nonWorkingDayRescheduleDetailsForRemove = new ArrayList<>();
        Map<Long, NonWorkingDayRescheduleDetail> nonWorkingDayRescheduleDetailsAsMap = this.daysRepositoryWrapper
                .fetchAllNonWorkingDayRescheduleDetail();
        nonWorkingDayRescheduleDetails.addAll(nonWorkingDayRescheduleDetailsAsMap.values());
        final List<Map<String, Object>> nonWorkingDayRescheduleDetailChangesList = new ArrayList<>();
        changes.put(WorkingDaysApiConstants.advancedRescheduleDetail, nonWorkingDayRescheduleDetailChangesList);
        boolean hasModifications = false;
        if (this.fromApiJsonHelper.parameterExists(WorkingDaysApiConstants.advancedRescheduleDetail, element)) {
            JsonArray advancedRescheduleDetail = this.fromApiJsonHelper.extractJsonArrayNamed(
                    WorkingDaysApiConstants.advancedRescheduleDetail, element);
            if (advancedRescheduleDetail != null && advancedRescheduleDetail.size() > 0) {

                Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
                for (int i = 0; i < advancedRescheduleDetail.size(); i++) {
                    final JsonObject jsonObject = advancedRescheduleDetail.get(i).getAsJsonObject();
                    Long id = this.fromApiJsonHelper.extractLongNamed(WorkingDaysApiConstants.idParamName, jsonObject);
                    if (id == null) {
                        final Integer repaymentRescheduleType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                                WorkingDaysApiConstants.repayment_rescheduling_enum, jsonObject);
                        final String fromWeekDay = this.fromApiJsonHelper.extractStringNamed(WorkingDaysApiConstants.fromWeekDay, jsonObject);
                        final String toWeekDay = this.fromApiJsonHelper.extractStringNamed(WorkingDaysApiConstants.toWeekDay, jsonObject);
                        NonWorkingDayRescheduleDetail detail = new NonWorkingDayRescheduleDetail(fromWeekDay, repaymentRescheduleType,
                                toWeekDay);
                        nonWorkingDayRescheduleDetails.add(detail);
                        final Map<String, Object> changeDetail = new HashMap<>(1);
                        changeDetail.put(WorkingDaysApiConstants.repayment_rescheduling_enum, repaymentRescheduleType);
                        changeDetail.put(WorkingDaysApiConstants.fromWeekDay, fromWeekDay);
                        if (toWeekDay != null) {
                            changeDetail.put(WorkingDaysApiConstants.toWeekDay, toWeekDay);
                        }
                        nonWorkingDayRescheduleDetailChangesList.add(changeDetail);
                        hasModifications = true;
                    } else {
                        Boolean delete = this.fromApiJsonHelper.extractBooleanNamed(WorkingDaysApiConstants.delete, jsonObject);
                        NonWorkingDayRescheduleDetail nonWorkingDayRescheduleDetail = nonWorkingDayRescheduleDetailsAsMap.get(id);
                        if (nonWorkingDayRescheduleDetail == null) { throw new NonWorkingDayRescheduleDetailNotFoundException(id); }
                        if (delete != null && delete) {
                            nonWorkingDayRescheduleDetails.remove(nonWorkingDayRescheduleDetail);
                            nonWorkingDayRescheduleDetailsForRemove.add(nonWorkingDayRescheduleDetail);
                            final Map<String, Object> changeDetail = new HashMap<>(2);
                            changeDetail.put(WorkingDaysApiConstants.idParamName, id);
                            changeDetail.put("isDeleted", true);
                            nonWorkingDayRescheduleDetailChangesList.add(changeDetail);
                        } else {
                            JsonCommand detailCommand = JsonCommand.from(this.fromApiJsonHelper, jsonObject, id);
                            nonWorkingDayRescheduleDetail.update(detailCommand, locale, nonWorkingDayRescheduleDetailChangesList);
                            hasModifications = true;
                        }

                    }

                }
            }
        }
        if (!nonWorkingDayRescheduleDetails.isEmpty()) {
            List<String> fromWeekDayList = new ArrayList<>();

            WeekDayList workingDayList = rrule.getRecur().getDayList();
            for (NonWorkingDayRescheduleDetail detail : nonWorkingDayRescheduleDetails) {
                WeekDay fromDay = new WeekDay(detail.getFromWeekDay());
                WeekDay toDay = new WeekDay(detail.getToWeekDay());
                if (workingDayList.contains(fromDay)) {
                    baseDataValidator.reset().parameter(WorkingDaysApiConstants.fromWeekDay)
                            .failWithCode("can.not.be.working.day", fromDay.getDay());
                }
                if (!workingDayList.contains(toDay)) {
                    baseDataValidator.reset().parameter(WorkingDaysApiConstants.toWeekDay)
                            .failWithCode("must.be.working.day", toDay.getDay());
                }
                if (fromWeekDayList.contains(detail.getFromWeekDay())) {
                    baseDataValidator.reset().parameter(WorkingDaysApiConstants.fromWeekDay).value(detail.getFromWeekDay())
                            .failWithCode("can.not.be.duplicated");
                }
                fromWeekDayList.add(detail.getFromWeekDay());
            }

            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
            if (hasModifications) {
                this.nonWorkingDayRescheduleDetailRepository.save(nonWorkingDayRescheduleDetails);
            }
        }

        if (!nonWorkingDayRescheduleDetailsForRemove.isEmpty()) {
            this.nonWorkingDayRescheduleDetailRepository.delete(nonWorkingDayRescheduleDetailsForRemove);
        }

    }

}
