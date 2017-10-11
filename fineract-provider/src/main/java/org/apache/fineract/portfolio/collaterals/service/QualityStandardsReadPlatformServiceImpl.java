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
package org.apache.fineract.portfolio.collaterals.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.data.QualityStandardsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class QualityStandardsReadPlatformServiceImpl implements QualityStandardsReadPlatformService {
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QualityStandardsReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    private static final class QualityStandardsMapper implements RowMapper<QualityStandardsData> {

        private final String sqlQuery = "id, collateral_id, name, description, percentage_price, absolute_price, created_by,"
                + " created_date, updated_by, updated_date from m_collateral_quality_standards ";

        public String schema() {
            return this.sqlQuery;
        }

        @Override
        public QualityStandardsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long collateralId = rs.getLong("collateral_id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final BigDecimal percentagePrice = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "percentage_price");
            final BigDecimal absolutePrice = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "absolute_price");
            final Long createdBy = rs.getLong("created_by");
            final Date createdDate = rs.getDate("created_date");
            final Long updatedBy = JdbcSupport.getLongDefaultToNullIfZero(rs, "updated_by");
            final Date updatedDate = rs.getDate("updated_date");

            return QualityStandardsData.instance(id, collateralId, name, description, percentagePrice, absolutePrice, createdBy,
                    createdDate, updatedBy, updatedDate);
        }

    }

    @Override
    public List<QualityStandardsData> retrieveAllCollateralQualityStandards(Long collateralId) {
        this.context.authenticatedUser();

        final QualityStandardsMapper rm = new QualityStandardsMapper();

        final String sql = "SELECT " + rm.schema() + "WHERE collateral_id = ?";
        ;

        return this.jdbcTemplate.query(sql, rm, collateralId);
    }

    @Override
    public QualityStandardsData retrieveOneCollateralQualityStandards(Long collateralId, Long collateralQualityStandardId) {

        this.context.authenticatedUser();

        final QualityStandardsMapper rm = new QualityStandardsMapper();

        final String sql = "SELECT " + rm.schema() + "WHERE collateral_id = ? and id = ? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { collateralId, collateralQualityStandardId });

    }

    @Override
    public Collection<QualityStandardsData> retrieveAllQualityStandardsForDropdown() {
        
        QualityStandardsDataMapper rm = new QualityStandardsDataMapper();
        String sql = "select " + rm.schema();
        
        return this.jdbcTemplate.query(sql, rm);
    }

    private static final class QualityStandardsDataMapper implements RowMapper<QualityStandardsData> {

        public String schema() {
            return "cqs.id as id, cqs.name as name, cqs.collateral_id as collateralId, cqs.percentage_price as percentagePrice, cqs.absolute_price as absolutePrice" +
            		" from m_collateral_quality_standards cqs ";
        }

        @Override
        public QualityStandardsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final Long collateralId = rs.getLong("collateralId");
            final BigDecimal percentagePrice = rs.getBigDecimal("percentagePrice");
            final BigDecimal absolutePrice = rs.getBigDecimal("absolutePrice");

            return QualityStandardsData.lookUp(id, name, collateralId, percentagePrice, absolutePrice);
        }

    }
    
    @Override
    public Collection<QualityStandardsData> retrieveAllQualityStandardsByCollateralIdForDropdown(final Long collateralId) {
        
        QualityStandardsDataMapper rm = new QualityStandardsDataMapper();
        String sql = "select " + rm.schema() + " join m_collateral_type ct on ct.id = ?";
        
        return this.jdbcTemplate.query(sql, rm, new Object[] {collateralId});
    }

}
