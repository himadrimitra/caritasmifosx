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
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.data.CollateralsData;
import org.apache.fineract.portfolio.collaterals.data.QualityStandardsData;
import org.apache.fineract.portfolio.collaterals.exception.CollateralNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CollateralsReadPlatformServiceImpl implements CollateralsReadPlatformService {
    
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public CollateralsReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
    }
    
    private static final class CollateralsMapper implements RowMapper<CollateralsData> {
        
        private final String sqlQuery = "id, name, description, base_unit_price, type_classifier from m_collateral_type ";
        
        public String schema() {
            return this.sqlQuery;
        }

        @Override
        public CollateralsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final BigDecimal baseUnitPrice = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "base_unit_price");
            final Integer typeClassifier = rs.getInt("type_classifier");
            final Collection<QualityStandardsData> qualityStandards = null;
            
            return CollateralsData.instance(id, name, description, baseUnitPrice, typeClassifier, qualityStandards);
        }
        
    }

    @Override
    public List<CollateralsData> retrieveAllCollaterals() {
     
        this.context.authenticatedUser();
        
        final CollateralsMapper rm = new CollateralsMapper();
        
        final String sql = "SELECT " + rm.schema();
        
        return this.jdbcTemplate.query(sql, rm);
    }

    @Override
    public CollateralsData retrieveOne(final Long collateralId) {
        try{
            this.context.authenticatedUser();
            
            final CollateralsMapper rm = new CollateralsMapper();
            
            final String sql = "SELECT " + rm.schema() + "WHERE id = ?";
            
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {collateralId});
        }catch(final EmptyResultDataAccessException e){
            throw new CollateralNotFoundException(collateralId);
        }
 
    }

    @Override
    public Collection<CollateralsData> retrieveAllCollateralsForDropdown() {
        
        CollateralDataMapper rm = new CollateralDataMapper();
        String sql = rm.schema();
        
        return this.jdbcTemplate.query(sql, rm);
    }
  
    private static final class CollateralDataMapper implements RowMapper<CollateralsData> {
        
        public String schema() {
            return "select ct.id as id, ct.name as name, ct.base_unit_price as baseUnitPrice from m_collateral_type ct";
        }

        @Override
        public CollateralsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final BigDecimal baseUnitPrice = rs.getBigDecimal("baseUnitPrice");
           
            return CollateralsData.lookUp(id, name, baseUnitPrice);
        }
        
    }

}
