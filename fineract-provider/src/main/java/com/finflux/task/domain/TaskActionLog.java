package com.finflux.task.domain;

import java.util.Date;

import javax.persistence.*;

import org.apache.fineract.useradministration.domain.AppUser;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_task_action_log")
public class TaskActionLog extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "action", length = 3)
    private Integer action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_by")
    private AppUser actionBy;

    @Column(name = "action_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionOn;


    protected TaskActionLog() {}

    private TaskActionLog(final Task task, final Integer action, final AppUser actionBy, final Date actionOn) {
        this.task = task;
        this.action = action;
        this.actionBy = actionBy;
        this.actionOn = actionOn;
    }

    public static TaskActionLog create(final Task task, final Integer action, final AppUser actionBy) {
        return new TaskActionLog(task,action,actionBy, new Date());
    }
}
