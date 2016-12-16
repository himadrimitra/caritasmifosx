package com.finflux.task.data;
/**
 * 
 * @author CT
 *
 */
public class TaskSummaryData {

    private String taskStatus;
    private Long noOfCount;

    public TaskSummaryData() {}

    public TaskSummaryData(final String taskStatus, final Long noOfCount) {
        this.taskStatus = taskStatus;
        this.noOfCount = noOfCount;
    }

    public String getTaskStatus() {
        return this.taskStatus;
    }

    public void setTaskStatus(final String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getNoOfCount() {
        return this.noOfCount;
    }

    public void setNoOfCount(final Long noOfCount) {
        this.noOfCount = noOfCount;
    }

}
