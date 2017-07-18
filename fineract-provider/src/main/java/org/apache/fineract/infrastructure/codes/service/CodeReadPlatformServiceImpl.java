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
package org.apache.fineract.infrastructure.codes.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.codes.domain.CodeRepository;
import org.apache.fineract.infrastructure.codes.exception.CodeNotFoundException;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CodeReadPlatformServiceImpl implements CodeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CodeValueReadPlatformService readPlatformService;
    private final CodeRepository codeRepository;
    private final PaginationHelper<CodeData> paginationHelper = new PaginationHelper<>();

    @Autowired
    public CodeReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
                final CodeValueReadPlatformService readPlatformService, final CodeRepository codeRepository) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.readPlatformService = readPlatformService;
        this.codeRepository = codeRepository;
        
    }

    private static final class CodeMapper implements RowMapper<CodeData> {

        public String schema() {
            return " c.id as id, c.code_name as code_name, c.is_system_defined as systemDefined, c.parent_id as parentId from m_code c ";
        }

        @Override
        public CodeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String code_name = rs.getString("code_name");
            final boolean systemDefined = rs.getBoolean("systemDefined");
            final long parentId = rs.getLong("parentId");

            return CodeData.instance(id, code_name, systemDefined, parentId);
        }
    }

    @Override
    public Page<CodeData> retrieveAllCodes(final SearchParameters searchParameters) {
        this.context.authenticatedUser();
        List<Object> params = new ArrayList<>();
        final CodeMapper rm = new CodeMapper();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(rm.schema());
        sqlBuilder.append(" order by c.code_name");
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        final String sqlCountRows = "SELECT FOUND_ROWS()";

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), params.toArray(), rm);
    }

    @Override
    public CodeData retrieveCode(final Long codeId) {
        try {
            this.context.authenticatedUser();

            final CodeMapper rm = new CodeMapper();
            final String sql = "select " + rm.schema() + " where c.id = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeId });
        } catch (final EmptyResultDataAccessException e) {
            throw new CodeNotFoundException(codeId);
        }
    }

    @Override
    public CodeData retriveCode(final String codeName) {
        try {
            this.context.authenticatedUser();

            final CodeMapper rm = new CodeMapper();
            final String sql = "select " + rm.schema() + " where c.code_name = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeName });
        } catch (final EmptyResultDataAccessException e) {
            throw new CodeNotFoundException(codeName);
        }
    }

    @Override
    public Collection<CodeValueData> retrieveAllCodeValuesForCode(String codeName) {
        try {
            this.context.authenticatedUser();

            final Code code = codeRepository.findOneByName(codeName);
            return this.readPlatformService.retrieveAllCodeValues(code.getId());

        } catch (final NullPointerException e) {
            throw new CodeNotFoundException(codeName);
        }
    }
        
}