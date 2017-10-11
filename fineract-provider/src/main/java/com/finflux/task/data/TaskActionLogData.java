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
    private Long actionByUserId;

    public TaskActionLogData(Long id, EnumOptionData action, Date actionOn, Long actionByUserId, String actionBy) {
        this.id = id;
        this.action = action;
        this.actionOn = actionOn;
        this.actionByUserId = actionByUserId;
        this.actionBy = actionBy;
    }

    public static TaskActionLogData instance(Long id, EnumOptionData action, Date actionOn, Long actionByUserId, String actionBy) {
        return new TaskActionLogData(id, action, actionOn, actionByUserId,actionBy);
    }

    public Long getId() {
        return id;
    }

    public EnumOptionData getAction() {
        return action;
    }

    public Date getActionOn() {
        return actionOn;
    }

    public String getActionBy() {
        return actionBy;
    }

    public Long getActionByUserId() {
        return actionByUserId;
    }
}
