package com.finflux.fileprocess.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_file_records")
public class FileRecords extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "file_process_id", nullable = false)
    private FileProcess fileProcess;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "status", length = 3, nullable = false)
    private Integer status;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    protected FileRecords() {}

    private FileRecords(final FileProcess fileProcess, final String content, final Integer status, final Date createdDate) {
        this.fileProcess = fileProcess;
        this.content = content;
        this.status = status;
        this.createdDate = createdDate;
    }

    public static FileRecords create(final FileProcess fileProcess, final String content, final Integer status, final Date createdDate) {
        return new FileRecords(fileProcess, content, status, createdDate);
    }

    public FileProcess getFileProcess() {
        return this.fileProcess;
    }

    public void setFileProcess(final FileProcess fileProcess) {
        this.fileProcess = fileProcess;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }
}
