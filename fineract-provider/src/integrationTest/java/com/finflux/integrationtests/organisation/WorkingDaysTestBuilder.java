package com.finflux.integrationtests.organisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

public class WorkingDaysTestBuilder {

    private String recurrence = "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU";
    private final String LOCALE = "en";
    private String repaymentRescheduleType = "4";
    private final String extendTermForDailyRepayments = "false";
    private final List<HashMap<String, Object>> advancedRescheduleDetail = new ArrayList<>();

    public String build() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("recurrence", this.recurrence);
        map.put("locale", this.LOCALE);
        map.put("repaymentRescheduleType", this.repaymentRescheduleType);
        map.put("extendTermForDailyRepayments", this.extendTermForDailyRepayments);
        if (!advancedRescheduleDetail.isEmpty()) {
            map.put("advancedRescheduleDetail", this.advancedRescheduleDetail);
        }
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public void addAdvancedDetail(final String fromWeekDay, final String repaymentRescheduleType, final String toWeekDay) {
        final HashMap<String, Object> detailMap = new HashMap<>();
        detailMap.put("fromWeekDay", fromWeekDay);
        detailMap.put("repaymentRescheduleType", repaymentRescheduleType);
        if (toWeekDay != null) {
            detailMap.put("toWeekDay", toWeekDay);
        }
        this.advancedRescheduleDetail.add(detailMap);
    }

    public void deleteAdvancedDetail(final String id) {
        final HashMap<String, Object> detailMap = new HashMap<>();
        detailMap.put("id", id);
        detailMap.put("delete", true);
        this.advancedRescheduleDetail.add(detailMap);
    }

    public void clearAdvancedDetail() {
        this.advancedRescheduleDetail.clear();
    }

    public WorkingDaysTestBuilder withRecurrnce(final String recurrence) {
        this.recurrence = recurrence;
        return this;
    }

    public WorkingDaysTestBuilder withRepaymentRescheduleType(final String repaymentRescheduleType) {
        this.repaymentRescheduleType = repaymentRescheduleType;
        return this;
    }
}
