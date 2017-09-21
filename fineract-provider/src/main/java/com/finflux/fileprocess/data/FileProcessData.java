/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.fileprocess.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.DateTime;

@SuppressWarnings("unused")
public class FileProcessData {

    private final Long id;
    private final String fileName;
    private final String contentType;
    private final String filePath;
    private final EnumOptionData fileProcessType;
    private final Long totalRecords;
    private final Long totalPendingRecords;
    private final Long totalSuccessRecords;
    private final Long totalFailureRecords;
    private final EnumOptionData status;
    private final DateTime lastProcessedDate;
    private final AppUserData createdBy;
    private final DateTime createdDate;
    private final Collection<EnumOptionData> fileProcessTypeOptions;

    private FileProcessData(final Long id, final String fileName, final String contentType, final String filePath,
            final EnumOptionData fileProcessType, final Long totalRecords, final Long totalPendingRecords, final Long totalSuccessRecords,
            final Long totalFailureRecords, final EnumOptionData status, final DateTime lastProcessedDate, final AppUserData createdBy,
            final DateTime createdDate, final Collection<EnumOptionData> fileProcessTypeOptions) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.filePath = filePath;
        this.fileProcessType = fileProcessType;
        this.totalRecords = totalRecords;
        this.totalPendingRecords = totalPendingRecords;
        this.totalSuccessRecords = totalSuccessRecords;
        this.totalFailureRecords = totalFailureRecords;
        this.status = status;
        this.lastProcessedDate = lastProcessedDate;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.fileProcessTypeOptions = fileProcessTypeOptions;
    }

    public static FileProcessData instance(final Long id, final String fileName, final String contentType, final String filePath,
            final EnumOptionData fileProcessType, final Long totalRecords, final Long totalPendingRecords, final Long totalSuccessRecords,
            final Long totalFailureRecords, final EnumOptionData status, final DateTime lastProcessedDate, final AppUserData createdBy,
            final DateTime createdDate) {
        final Collection<EnumOptionData> fileProcessTypeOptions = null;
        return new FileProcessData(id, fileName, contentType, filePath, fileProcessType, totalRecords, totalPendingRecords,
                totalSuccessRecords, totalFailureRecords, status, lastProcessedDate, createdBy, createdDate, fileProcessTypeOptions);
    }

    public static FileProcessData template(final Collection<EnumOptionData> fileProcessTypeOptions) {
        final Long id = null;
        final String fileName = null;
        final String contentType = null;
        final String filePath = null;
        final EnumOptionData fileProcessType = null;
        final Long totalRecords = null;
        final Long totalPendingRecords = null;
        final Long totalSuccessRecords = null;
        final Long totalFailureRecords = null;
        final EnumOptionData status = null;
        final DateTime lastProcessedDate = null;
        final AppUserData createdBy = null;
        final DateTime createdDate = null;
        return new FileProcessData(id, fileName, contentType, filePath, fileProcessType, totalRecords, totalPendingRecords,
                totalSuccessRecords, totalFailureRecords, status, lastProcessedDate, createdBy, createdDate, fileProcessTypeOptions);
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getFileName() {
        return this.fileName;
    }
}
