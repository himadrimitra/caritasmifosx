/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.infrastructure.sms.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.sms.SmsApiConstants;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class RecurrenceService {

    public LocalDateTime getNextRecurringDateAndTime(final String recurrence, LocalDateTime nextTriggerDate, LocalDateTime adjustTime) {
        LocalDateTime newTriggerDateWithTime;
        Map<String, Object> recurringRuleMap = createRecurringRuleMap(recurrence);
        String frequency = (String) recurringRuleMap.get("FREQ");
        if (frequency.equalsIgnoreCase(SmsApiConstants.minutely) || frequency.equalsIgnoreCase(SmsApiConstants.secondly)
                || frequency.equalsIgnoreCase(SmsApiConstants.hourly)) {
            LocalDateTime nextTriggerDateTime = getNextTriggerDate(createRecurringRuleMap(recurrence), nextTriggerDate);
            if (nextTriggerDateTime.isBefore(DateUtils.getLocalDateTimeOfTenant())) {
                nextTriggerDateTime = getNextTriggerDate(createRecurringRuleMap(recurrence), DateUtils.getLocalDateTimeOfTenant());
            }
            final String dateString = nextTriggerDateTime.toLocalDate().toString() + " " + nextTriggerDateTime.getHourOfDay() + ":"
                    + nextTriggerDateTime.getMinuteOfHour() + ":" + nextTriggerDateTime.getSecondOfMinute();
            final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            newTriggerDateWithTime = LocalDateTime.parse(dateString, simpleDateFormat);
        } else {
            LocalDate nextRuntime = CalendarUtils.getNextRecurringDate(recurrence, nextTriggerDate.toLocalDate(),
                    nextTriggerDate.toLocalDate());
            // means  next run time is  in  the past calculate  a new future  date
            if (nextRuntime.isBefore(DateUtils.getLocalDateOfTenant())) { 
                nextRuntime = CalendarUtils.getNextRecurringDate(recurrence, nextTriggerDate.toLocalDate(),
                        DateUtils.getLocalDateOfTenant());
            }
            if (recurringRuleMap.get("BYHOUR") != null) {
                int hour = Integer.parseInt((String) recurringRuleMap.get("BYHOUR"));
                adjustTime = adjustTime.withHourOfDay(hour);
            }
            if (recurringRuleMap.get("BYMINUTE") != null) {
                int minute = Integer.parseInt((String) recurringRuleMap.get("BYMINUTE"));
                adjustTime = adjustTime.withMinuteOfHour(minute);
            }
            if (recurringRuleMap.get("BYSECOND") != null) {
                int second = Integer.parseInt((String) recurringRuleMap.get("BYSECOND"));
                adjustTime = adjustTime.withSecondOfMinute(second);
            }
            final String dateString = nextRuntime.toString() + " " + adjustTime.getHourOfDay() + ":" + adjustTime.getMinuteOfHour() + ":"
                    + adjustTime.getSecondOfMinute();
            final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            newTriggerDateWithTime = LocalDateTime.parse(dateString, simpleDateFormat);
        }
        return newTriggerDateWithTime;
    }

    /**
     * if recurrence start date is in the future calculate next trigger date if
     * not use recurrence start date us next trigger date when activating
     */
    public LocalDateTime getNextRecurringDateAndTimeWhenStartDateInFuture(String recurrence, LocalDate startDate,
            LocalDateTime startDateTime, LocalDateTime tenantDateTime) {
        Map<String, Object> recurringRuleMap = createRecurringRuleMap(recurrence);
        String frequency = (String) recurringRuleMap.get("FREQ");
        LocalDateTime nextTriggerDateWithTime = null;
        if (frequency.equalsIgnoreCase(SmsApiConstants.minutely) || frequency.equalsIgnoreCase(SmsApiConstants.secondly)
                || frequency.equalsIgnoreCase(SmsApiConstants.hourly)) {
            LocalDateTime nextTriggerDate = null;
            if (startDateTime.isBefore(tenantDateTime)) {
                nextTriggerDate = getNextTriggerDate(createRecurringRuleMap(recurrence), tenantDateTime);
            } else {
                nextTriggerDate = startDateTime;
            }
            final String dateString = nextTriggerDate.toLocalDate().toString() + " " + nextTriggerDate.getHourOfDay() + ":"
                    + nextTriggerDate.getMinuteOfHour() + ":" + nextTriggerDate.getSecondOfMinute();
            final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            nextTriggerDateWithTime = LocalDateTime.parse(dateString, simpleDateFormat);
        } else {
            LocalDate nextTriggerDate = null;
            if (startDateTime.isBefore(tenantDateTime)) {
                nextTriggerDate = CalendarUtils.getNextRecurringDate(recurrence, startDate, DateUtils.getLocalDateOfTenant());
            } else {
                nextTriggerDate = startDate;
            }
            LocalDateTime adjustTime = startDateTime;
            if (recurringRuleMap.get("BYHOUR") != null) {
                int hour = Integer.parseInt((String) recurringRuleMap.get("BYHOUR"));
                adjustTime = adjustTime.withHourOfDay(hour);
            }
            if (recurringRuleMap.get("BYMINUTE") != null) {
                int minute = Integer.parseInt((String) recurringRuleMap.get("BYMINUTE"));
                adjustTime = adjustTime.withMinuteOfHour(minute);
            }
            if (recurringRuleMap.get("BYSECOND") != null) {
                int second = Integer.parseInt((String) recurringRuleMap.get("BYSECOND"));
                adjustTime = adjustTime.withSecondOfMinute(second);
            }
            final String dateString = nextTriggerDate.toString() + " " + adjustTime.getHourOfDay() + ":" + adjustTime.getMinuteOfHour()
                    + ":" + adjustTime.getSecondOfMinute();
            final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            nextTriggerDateWithTime = LocalDateTime.parse(dateString, simpleDateFormat);
        }
        return nextTriggerDateWithTime;
    }

    public Map<String, Object> createRecurringRuleMap(String ruleList) {
        Map<String, Object> recurringRuleMap = new HashMap<>();
        String[] rules = ruleList.split(";");
        for (int i = 0; i < rules.length; i++) {
            String[] rule = rules[i].split("=");
            recurringRuleMap.put(rule[0], rule[1]);
        }
        return recurringRuleMap;
    }

    public LocalDateTime getNextTriggerDate(Map<String, Object> recurringRuleMap, LocalDateTime previousDateTime) {
        int interval = 1;
        LocalDateTime nextRecurrenceDateTime = previousDateTime;

        String intervalAsString = (String) recurringRuleMap.get("INTERVAL");
        if (intervalAsString != null) {
            interval = Integer.parseInt(intervalAsString);
        }
        String frequency = (String) recurringRuleMap.get("FREQ");
        if (frequency.equalsIgnoreCase(SmsApiConstants.secondly)) {
            nextRecurrenceDateTime = previousDateTime.plusSeconds(interval);
        } else if (frequency.equalsIgnoreCase(SmsApiConstants.minutely)) {
            nextRecurrenceDateTime = previousDateTime.plusMinutes(interval);
        } else if (frequency.equalsIgnoreCase(SmsApiConstants.hourly)) {
            nextRecurrenceDateTime = previousDateTime.plusHours(interval);
        }

        if (recurringRuleMap.get("BYHOUR") != null) {
            int hour = Integer.parseInt((String) recurringRuleMap.get("BYHOUR"));
            nextRecurrenceDateTime = nextRecurrenceDateTime.withHourOfDay(hour);
        }

        if (recurringRuleMap.get("BYMINUTE") != null) {
            int minute = Integer.parseInt((String) recurringRuleMap.get("BYMINUTE"));
            nextRecurrenceDateTime = nextRecurrenceDateTime.withMinuteOfHour(minute);
        }

        if (recurringRuleMap.get("BYSECOND") != null) {
            int second = Integer.parseInt((String) recurringRuleMap.get("BYSECOND"));
            nextRecurrenceDateTime = nextRecurrenceDateTime.withSecondOfMinute(second);
        }

        return nextRecurrenceDateTime;
    }
}
