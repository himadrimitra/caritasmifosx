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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.data.ProductCollateralsMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductCollateralsMappingReadPlatformServiceImpl implements ProductCollateralsMappingReadPlatformService{
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ProductCollateralsMappingReadPlatformServiceImpl(final PlatformSecurityContext context,final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @SuppressWarnings("unused")
    private static final class ProductCollateralsMappingMapper implements RowMapper<ProductCollateralsMappingData> {
        
        private final String sqlQuery = " pc.id as id, pc.product_id as productId, pl.name as productName, pl.short_name as productShortName, pc.collateral_id as collateralId, ct.name  as collateralName from m_product_to_collateral_mappings pc join m_product_loan pl on pc.product_id = pl.id join m_collateral_type ct on ct.id = pc.collateral_id";
        
        public String schema() {
            return this.sqlQuery;
        }

        @Override
        public ProductCollateralsMappingData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            
            final Long id = rs.getLong("id");
            final Long product = rs.getLong("productId");
            final String productName = rs.getString("productName");
            final String productShortName = rs.getString("productShortName");
            final Long collateral = rs.getLong("collateralId");
            final String collateralName = rs.getString("collateralName");
            return ProductCollateralsMappingData.instance(id, product, productName, productShortName, collateral, collateralName);
        }
        
    }

    @Override
    public List<ProductCollateralsMappingData> retrieveAll(Long productId) {
        
        this.context.authenticatedUser();
        
        final ProductCollateralsMappingMapper rm = new ProductCollateralsMappingMapper();
        if(productId==-1){
            final String sql = "SELECT " + rm.schema();
            return this.jdbcTemplate.query(sql, rm);
        }
        final String sql = "SELECT " + rm.schema() + " WHERE pc.product_id = ?";
        return this.jdbcTemplate.query(sql, rm, new Object[] {productId});             
        
    }

    @Override
    public ProductCollateralsMappingData retrieveOne(Long productCollateralsMappingId) {
        this.context.authenticatedUser();
        
        final ProductCollateralsMappingMapper mapper = new ProductCollateralsMappingMapper();
        
        final String sql = "SELECT " + mapper.schema() + " WHERE pc.id = ?";
        
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {productCollateralsMappingId});
    }

    @Override
    public List<ProductCollateralsMappingData> retrieveOneWithProductAndCollateral(Long productId, Long collateralId) {
        this.context.authenticatedUser();
        
        final ProductCollateralsMappingMapper mapper = new ProductCollateralsMappingMapper();
        
        final String sql = "SELECT " + mapper.schema() + " WHERE pc.product_id = ? and pc.collateral_id = ?";
        
        return this.jdbcTemplate.query(sql, mapper, new Object[] {productId,collateralId});
        
    }
    
    
}
