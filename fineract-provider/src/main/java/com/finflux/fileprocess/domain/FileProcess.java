package com.finflux.fileprocess.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_file_process")
public class FileProcess extends AbstractPersistable<Long> {

    @Column(name = "file_name", length = 100, nullable = false)
    private String fileName;

    @Column(name = "file_type", length = 50, nullable = false)
    private String fileType;

    @Column(name = "file_path", length = 250, nullable = false)
    private String filePath;

    @Column(name = "file_process_type", length = 3, nullable = false)
    private Integer fileProcessType;

    @Column(name = "total_records", length = 10, nullable = false)
    private Integer totalRecords = 0;

    @Column(name = "total_pending_records", length = 10, nullable = false)
    private Integer totalPendingRecords = 0;

    @Column(name = "total_success_records", length = 10, nullable = false)
    private Integer totalSuccessRecords = 0;

    @Column(name = "total_failure_records", length = 10, nullable = false)
    private Integer totalFailureRecords = 0;

    @Column(name = "status", length = 3, nullable = false)
    private Integer status;

    @Column(name = "last_processed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastProcessedDate;

    @Column(name = "createdby_id", length = 20, nullable = false)
    private Long createdbyId;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileProcess", orphanRemoval = true)
    private List<FileRecords> fileRecords = new ArrayList<>();

    protected FileProcess() {}

    private FileProcess(final String fileName, final String fileType, final String filePath, final Integer fileProcessType,
            final Integer totalRecords, final Integer totalPendingRecords, final Integer totalSuccessRecords,
            final Integer totalFailureRecords, final Integer status, final Date lastProcessedDate, final Long createdbyId,
            final Date createdDate) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileProcessType = fileProcessType;
        this.totalRecords = totalRecords;
        this.totalPendingRecords = totalPendingRecords;
        this.totalSuccessRecords = totalSuccessRecords;
        this.totalFailureRecords = totalFailureRecords;
        this.status = status;
        this.lastProcessedDate = lastProcessedDate;
        this.createdbyId = createdbyId;
        this.createdDate = createdDate;
    }

    public static FileProcess create(final String fileName, final String fileType, final String filePath, final Integer fileProcessType,
            final Integer totalRecords, final Integer totalPendingRecords, final Integer totalSuccessRecords,
            final Integer totalFailureRecords, final Integer status, final Date lastProcessedDate, final Long createdbyId,
            final Date createdDate) {
        return new FileProcess(fileName, fileType, filePath, fileProcessType, totalRecords, totalPendingRecords, totalSuccessRecords,
                totalFailureRecords, status, lastProcessedDate, createdbyId, createdDate);
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getFileProcessType() {
        return this.fileProcessType;
    }

    public void setFileProcessType(Integer fileProcessType) {
        this.fileProcessType = fileProcessType;
    }

    public Integer getTotalRecords() {
        return this.totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getTotalPendingRecords() {
        return this.totalPendingRecords;
    }

    public void setTotalPendingRecords(Integer totalPendingRecords) {
        this.totalPendingRecords = totalPendingRecords;
    }

    public Integer getTotalSuccessRecords() {
        return this.totalSuccessRecords;
    }

    public void setTotalSuccessRecords(Integer totalSuccessRecords) {
        this.totalSuccessRecords = totalSuccessRecords;
    }

    public Integer getTotalFailureRecords() {
        return this.totalFailureRecords;
    }

    public void setTotalFailureRecords(Integer totalFailureRecords) {
        this.totalFailureRecords = totalFailureRecords;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastProcessedDate() {
        return this.lastProcessedDate;
    }

    public void setLastProcessedDate(Date lastProcessedDate) {
        this.lastProcessedDate = lastProcessedDate;
    }

    public Long getCreatedbyId() {
        return this.createdbyId;
    }

    public void setCreatedbyId(Long createdbyId) {
        this.createdbyId = createdbyId;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<FileRecords> getFileRecords() {
        return this.fileRecords;
    }

    public void setFileRecords(final List<FileRecords> fileRecords) {
        this.fileRecords = fileRecords;
    }

}
