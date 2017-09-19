/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.fileprocess.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.fileprocess.data.FileProcessData;
import com.finflux.fileprocess.data.FileProcessType;
import com.finflux.fileprocess.data.FileStatus;
import com.finflux.fileprocess.exception.FileProcessNotFoundException;

@Service
public class FileProcessReadPlatformServiceImpl implements FileProcessReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    private final PaginationHelper<FileProcessData> paginationHelper = new PaginationHelper<>();

    @Autowired
    public FileProcessReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public FileProcessData retrieveTemplate() {
        this.context.authenticatedUser();
        final Collection<EnumOptionData> fileProcessTypeOptions = FileProcessType.fileProcessTypeOptions();
        return FileProcessData.template(fileProcessTypeOptions);
    }

    @Override
    public Page<FileProcessData> retrieveAll(final SearchParameters searchParameters, final String fileName, final Integer fileProcessType,
            final Integer status, final Long createdBy) {
        this.context.authenticatedUser();
        final FileProcessDataMapper mapper = new FileProcessDataMapper();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("SELECT SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(mapper.schema());
        String whereCondition = " where";
        final List<Object> params = new ArrayList<>();
        if (StringUtils.isNotBlank(fileName)) {
            sqlBuilder.append(whereCondition);
            sqlBuilder.append(" fp.file_name = ?");
            params.add(fileName);
            whereCondition = " and";
        }
        if (null != fileProcessType) {
            sqlBuilder.append(whereCondition);
            sqlBuilder.append(" fp.file_process_type = ?");
            params.add(fileProcessType);
            whereCondition = " and";
        }
        if (null != status) {
            sqlBuilder.append(whereCondition);
            sqlBuilder.append(" fp.status = ?");
            params.add(status);
            whereCondition = " and";
        }
        if (null != createdBy) {
            sqlBuilder.append(whereCondition);
            sqlBuilder.append(" fp.createdby_id = ?");
            params.add(createdBy);
            whereCondition = " and";
        }
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());

            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
            }
        }

        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final String sqlCountRows = "SELECT FOUND_ROWS()";

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), params.toArray(), mapper);
    }

    @Override
    public FileProcessData retrieveOne(final Long fileProcessId) {
        try {
            this.context.authenticatedUser();
            final FileProcessDataMapper mapper = new FileProcessDataMapper();
            final String sql = "SELECT " + mapper.schema() + " WHERE fp.id = ?";
            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { fileProcessId });
        } catch (final EmptyResultDataAccessException e) {
            throw new FileProcessNotFoundException(fileProcessId);
        }
    }

    private static final class FileProcessDataMapper implements RowMapper<FileProcessData> {

        private final String schema;

        public FileProcessDataMapper() {
            final StringBuilder builder = new StringBuilder();
            builder.append("fp.id AS id, fp.file_name AS fileName, fp.content_type AS contentType, fp.file_path AS filePath, ");
            builder.append("fp.file_process_type AS fileProcessType, fp.total_records AS totalRecords, fp.total_pending_records AS totalPendingRecords, ");
            builder.append("fp.total_success_records AS totalSuccessRecords, fp.total_failure_records totalFailureRecords, ");
            builder.append("fp.status AS STATUS, fp.last_processed_date AS lastProcessedDate, fp.createdby_id AS createdById, ");
            builder.append("au.username AS createdByUser, fp.created_date AS createdDate ");
            builder.append("FROM f_file_process fp ");
            builder.append("JOIN m_appuser au ON fp.createdby_id = au.id ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public FileProcessData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String fileName = rs.getString("fileName");
            final String contentType = rs.getString("contentType");
            final String filePath = rs.getString("filePath");
            final Integer fileProcessTypeId = JdbcSupport.getInteger(rs, "fileProcessType");
            final EnumOptionData fileProcessType = FileProcessType.fromInt(fileProcessTypeId).getEnumOptionData();
            final Long totalRecords = JdbcSupport.getLong(rs, "totalRecords");
            final Long totalPendingRecords = JdbcSupport.getLong(rs, "totalPendingRecords");
            final Long totalSuccessRecords = JdbcSupport.getLong(rs, "totalSuccessRecords");
            final Long totalFailureRecords = JdbcSupport.getLong(rs, "totalFailureRecords");
            final Integer statusId = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData status = FileStatus.fromInt(statusId).getEnumOptionData();
            final DateTime lastProcessedDate = JdbcSupport.getDateTime(rs, "lastProcessedDate");
            final Long createdById = JdbcSupport.getLong(rs, "createdById");
            final String createdByUser = rs.getString("createdByUser");
            final AppUserData createdBy = AppUserData.dropdown(createdById, createdByUser);
            final DateTime createdDate = JdbcSupport.getDateTime(rs, "createdDate");
            return FileProcessData.instance(id, fileName, contentType, filePath, fileProcessType, totalRecords, totalPendingRecords,
                    totalSuccessRecords, totalFailureRecords, status, lastProcessedDate, createdBy, createdDate);
        }

    }
}
