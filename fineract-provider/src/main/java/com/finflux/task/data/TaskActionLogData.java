package com.finflux.task.data;

import java.util.Date;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 15/10/16.
 */
public class TaskActionLogData {

    private Long id;
    private EnumOptionData action;
    private Date actionOn;
    private String actionBy;

    public TaskActionLogData(Long id, EnumOptionData action, Date actionOn, String actionBy) {
        this.id = id;
        this.action = action;
        this.actionOn = actionOn;
        this.actionBy = actionBy;
    }

    public static TaskActionLogData instance(Long id, EnumOptionData action, Date actionOn, String actionBy) {
        return new TaskActionLogData(id, action, actionOn, actionBy);
    }
}
