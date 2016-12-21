package com.finflux.task.domain;

import javax.persistence.*;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_task_note")
public class TaskNote extends AbstractAuditableCustom<AppUser,Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "note", length = 1024)
    private String note;


    protected TaskNote() {}

    private TaskNote(final Task task, final String note) {
        this.task = task;
        this.note = note;
    }

    public static TaskNote create(final Task task, final String note) {
        return new TaskNote(task,note);
    }
}
