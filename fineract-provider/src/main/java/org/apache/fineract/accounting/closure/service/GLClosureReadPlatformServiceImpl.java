/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.accounting.closure.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.fineract.accounting.closure.data.GLClosureData;
import org.apache.fineract.accounting.closure.exception.GLClosureNotFoundException;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GLClosureReadPlatformServiceImpl implements GLClosureReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PaginationHelper<GLClosureData> paginationHelper = new PaginationHelper<>();
    final GLClosureMapper rm = new GLClosureMapper();

    @Autowired
    public GLClosureReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GLClosureMapper implements RowMapper<GLClosureData> {

        public String schema() {
            return " glClosure.id as id, ounder.id as officeId,ounder.name as officeName ,glClosure.closing_date as closingDate,"
                    + " glClosure.is_deleted as isDeleted, creatingUser.id as creatingUserId,creatingUser.username as creatingUserName,"
                    + " updatingUser.id as updatingUserId,updatingUser.username as updatingUserName, glClosure.created_date as createdDate,"
                    + " glClosure.lastmodified_date as updatedDate, glClosure.comments as comments " + " from m_office office "
                    + " join m_office ounder on ounder.hierarchy like "
                    + " concat(office.hierarchy, '%') and ounder.hierarchy like concat('.', '%')"
                    + " join acc_gl_closure glClosure on glClosure.office_id=ounder.id"
                    + " join m_appuser creatingUser on glClosure.createdby_id=creatingUser.id"
                    + " join m_appuser updatingUser on glClosure.lastmodifiedby_id=updatingUser.id" + " where ";
        }

        public String latestGLClosureOfOfficeSchema() {
            return " glClosure.id as id, ounder.id as officeId,ounder.name as officeName , glClosure.closing_date as closingDate,"
                    + " glClosure.is_deleted as isDeleted, creatingUser.id as creatingUserId,creatingUser.username as creatingUserName,"
                    + " updatingUser.id as updatingUserId,updatingUser.username as updatingUserName, glClosure.created_date as createdDate,"
                    + " glClosure.lastmodified_date as updatedDate, glClosure.comments as comments " + " from m_office office "
                    + " join m_office ounder on ounder.hierarchy like "
                    + " concat(office.hierarchy, '%') and ounder.hierarchy like concat('.', '%')"
                    + " join (select max(glc.closing_date) as closingDate, glc.office_id "
                    + " from acc_gl_closure glc where glc.is_deleted = 0 group by glc.office_id) x ON x.office_id = ounder.id"
                    + " join acc_gl_closure glClosure on glClosure.office_id=ounder.id and x.closingDate = glClosure.closing_date and glClosure.is_deleted = 0"
                    + " join m_appuser creatingUser on glClosure.createdby_id=creatingUser.id"
                    + " join m_appuser updatingUser on glClosure.lastmodifiedby_id=updatingUser.id" + " where ";
        }

        public String glClosureByIdSchema() {
            return " glClosure.id as id, glClosure.office_id as officeId,office.name as officeName ,glClosure.closing_date as closingDate,"
                    + " glClosure.is_deleted as isDeleted, creatingUser.id as creatingUserId,creatingUser.username as creatingUserName,"
                    + " updatingUser.id as updatingUserId,updatingUser.username as updatingUserName, glClosure.created_date as createdDate,"
                    + " glClosure.lastmodified_date as updatedDate, glClosure.comments as comments "
                    + " from acc_gl_closure as glClosure, m_appuser as creatingUser, m_appuser as updatingUser,m_office as office"
                    + " where glClosure.createdby_id=creatingUser.id and "
                    + " glClosure.lastmodifiedby_id=updatingUser.id and glClosure.office_id=office.id";
        }

        public String glClosureByOfficeIdSchema() {
            return "glClosure.id as id, glClosure.office_id as officeId,office.name as officeName ,glClosure.closing_date as closingDate,"
                    + " glClosure.is_deleted as isDeleted, creatingUser.id as creatingUserId,creatingUser.username as creatingUserName,"
                    + " updatingUser.id as updatingUserId,updatingUser.username as updatingUserName, glClosure.created_date as createdDate,"
                    + " glClosure.lastmodified_date as updatedDate, glClosure.comments as comments "
                    + " from m_office office Join (select  max(glc.closing_date) as closingDate , glc.office_id from acc_gl_closure glc "
                    + "where glc.is_deleted = 0 group by glc.office_id) x on x.office_id = office.id "
                    + "JOIN acc_gl_closure glClosure ON glClosure.office_id=office.id and x.closingDate = glClosure.closing_date "
                    + "JOIN m_appuser creatingUser ON glClosure.createdby_id=creatingUser.id "
                    + "JOIN m_appuser updatingUser ON glClosure.lastmodifiedby_id=updatingUser.id" + "	where";
        }

        @Override
        public GLClosureData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final LocalDate closingDate = JdbcSupport.getLocalDate(rs, "closingDate");
            final Boolean deleted = rs.getBoolean("isDeleted");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final LocalDate lastUpdatedDate = JdbcSupport.getLocalDate(rs, "updatedDate");
            final Long creatingByUserId = rs.getLong("creatingUserId");
            final String createdByUserName = rs.getString("creatingUserName");
            final Long lastUpdatedByUserId = rs.getLong("updatingUserId");
            final String lastUpdatedByUserName = rs.getString("updatingUserName");
            final String comments = rs.getString("comments");

            return new GLClosureData(id, officeId, officeName, closingDate, deleted, createdDate, lastUpdatedDate, creatingByUserId,
                    createdByUserName, lastUpdatedByUserId, lastUpdatedByUserName, comments);
        }

    }

    @Override
    public Page<GLClosureData> retrieveAllGLClosures(final Long officeId, final boolean limitToOne, final PaginationParameters parameters,
            final SearchParameters searchParameters) {
        String sql = "select SQL_CALC_FOUND_ROWS";
        if (limitToOne) {
            sql += this.rm.latestGLClosureOfOfficeSchema() + "glClosure.is_deleted = 0";
        } else {
            sql += this.rm.schema() + "glClosure.is_deleted = 0";
        }
        final Object[] objectArray = new Object[1];
        int arrayPos = 0;
        String sqlForSingleClosure = "";
        if (officeId != null && officeId != 0) {
            sql += " and office.id = ?";
            objectArray[arrayPos] = officeId;
            arrayPos = arrayPos + 1;
            if (limitToOne) {
                sqlForSingleClosure = " group by ounder.id ";
            }
        }

        sql = sql + sqlForSingleClosure + " order by glClosure.closing_date desc ";
        final StringBuilder sqlBuilder = new StringBuilder(200);

        if (parameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        sql = sql + sqlBuilder.toString();
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        final Page<GLClosureData> retrieveAllGLClosures = this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql,
                new Object[] { officeId }, this.rm);
        return retrieveAllGLClosures;
    }

    @Override
    public List<GLClosureData> retrieveAllGLClosures(final Long officeId, final boolean limitToOne) {

        String sql = "select " + this.rm.schema() + " glClosure.is_deleted = 0";
        final Object[] objectArray = new Object[1];
        int arrayPos = 0;
        String sqlForSingleClosure = "";
        if (officeId != null && officeId != 0) {
            sql += " and office.id = ?";
            objectArray[arrayPos] = officeId;
            arrayPos = arrayPos + 1;
            if (limitToOne) {
                sqlForSingleClosure = "limit 1";
            }
        }

        sql = sql + " order by glClosure.closing_date desc " + sqlForSingleClosure;
        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        return this.jdbcTemplate.query(sql, this.rm, finalObjectArray);

    }

    @Override
    public GLClosureData retrieveGLClosureById(final long glClosureId) {
        try {

            final String sql = "select " + this.rm.glClosureByIdSchema() + " and glClosure.id = ?";

            final GLClosureData glAccountData = this.jdbcTemplate.queryForObject(sql, this.rm, new Object[] { glClosureId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new GLClosureNotFoundException(glClosureId);
        }
    }

    @Override
    public GLClosureData retrieveGLClosureByOfficeId(final Long officeId) {
        try {

            final String sql = "select " + this.rm.glClosureByOfficeIdSchema() + " office.id = ?";

            final GLClosureData glAccountData = this.jdbcTemplate.queryForObject(sql, this.rm, new Object[] { officeId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

}
