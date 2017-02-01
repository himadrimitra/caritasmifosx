package com.finflux.task.data;

/**
 * Created by dhirendra on 15/10/16.
 */
public class TaskMakerCheckerData {

    private Long taskId;
    private Long makerUserId;
    private String makerUserName;
    private Long checkerUserId;
    private String checkerUserName;
    private Long approverUserId;
    private String approverUserName;

    public TaskMakerCheckerData(Long taskId, Long makerUserId, String makerUserName, Long checkerUserId,
                                String checkerUserName, Long approverUserId, String approverUserName) {
        this.taskId = taskId;
        this.makerUserId = makerUserId;
        this.makerUserName = makerUserName;
        this.checkerUserId = checkerUserId;
        this.checkerUserName = checkerUserName;
        this.approverUserId = approverUserId;
        this.approverUserName = approverUserName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getMakerUserId() {
        return makerUserId;
    }

    public String getMakerUserName() {
        return makerUserName;
    }

    public Long getCheckerUserId() {
        return checkerUserId;
    }

    public String getCheckerUserName() {
        return checkerUserName;
    }

    public Long getApproverUserId() {
        return approverUserId;
    }

    public String getApproverUserName() {
        return approverUserName;
    }
}
