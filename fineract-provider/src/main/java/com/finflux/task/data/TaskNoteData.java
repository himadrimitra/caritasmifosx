package com.finflux.task.data;

import org.joda.time.LocalDate;

/**
 * Created by dhirendra on 15/10/16.
 */
public class TaskNoteData {

    private Long id;
    private Long taskId;
    private String note;
    private String createdBy;
    private LocalDate createdOn;

    public TaskNoteData(Long id, Long taskId, String note, String createdBy, LocalDate createdOn) {
        this.id = id;
        this.taskId = taskId;
        this.note = note;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public static TaskNoteData instance(Long id, Long taskId, String note, String createdBy, LocalDate createdOn) {
        return new TaskNoteData(id, taskId, note, createdBy, createdOn);
    }
}
