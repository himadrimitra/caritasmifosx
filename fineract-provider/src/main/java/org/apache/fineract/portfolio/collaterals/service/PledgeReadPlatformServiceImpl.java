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

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants.PLEDGE_STATUS_PARAMS;
import org.apache.fineract.portfolio.collaterals.data.CollateralDetailsData;
import org.apache.fineract.portfolio.collaterals.data.CollateralsData;
import org.apache.fineract.portfolio.collaterals.data.PledgeData;
import org.apache.fineract.portfolio.collaterals.data.QualityStandardsData;
import org.apache.fineract.portfolio.collaterals.domain.Collaterals;
import org.apache.fineract.portfolio.collaterals.domain.CollateralsRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.exception.PledgeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PledgeReadPlatformServiceImpl implements PledgeReadPlatformService {
    
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final CollateralsReadPlatformService collateralsReadPlatformService;
    private final QualityStandardsReadPlatformService qualityStandardsReadPlatformService;
    private final CollateralsRepositoryWrapper collateralsRepositoryWrapper;
    private final PaginationHelper<PledgeData> paginationHelper = new PaginationHelper<>();

    @Autowired
    public PledgeReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource, final CollateralsReadPlatformService collateralsReadPlatformService,
            final QualityStandardsReadPlatformService qualityStandardsReadPlatformService, final CollateralsRepositoryWrapper collateralsRepositoryWrapper) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.collateralsReadPlatformService = collateralsReadPlatformService;
        this.qualityStandardsReadPlatformService = qualityStandardsReadPlatformService;
        this.collateralsRepositoryWrapper = collateralsRepositoryWrapper;
    }
    
    private static final class PledgesMapper implements RowMapper<PledgeData> {
    	 
        private final String sql = "p.id as id, p.client_id as clientId, c.display_name as clientName, o.name as officeName, p.loan_id as loanId, l.account_no as loanAccountNo, p.seal_number as sealNumber, " +
        		" p.pledge_number as pledgeNumber, p.status as status, p.system_value as systemValue, p.user_value as userValue, p.created_by as createdBy, p.created_date as createdDate, p.updated_by as updatedBy, p.updated_date  as updatedDate from m_pledge p " +
        		" left join m_client c on p.client_id = c.id  left join m_office o on c.office_id = o.id" +
        		" left join m_loan l on l.id = p.loan_id " ;
 
        
        public String schema() {
            return this.sql;
        }

        @Override
        public PledgeData mapRow(ResultSet rs , @SuppressWarnings("unused") int rowNum) throws SQLException {
            
            final Long id = rs.getLong("id");
            final Long clientId = JdbcSupport.getLongDefaultToNullIfZero(rs, "clientId");
            final Long loanId = JdbcSupport.getLongDefaultToNullIfZero(rs, "loanId");
            final String loanAccountNo = rs.getString("loanAccountNo");
            final String clientName = rs.getString("clientName");
            final String officeName = rs.getString("officeName");
            final Long sealNumber = JdbcSupport.getLongDefaultToNullIfZero(rs, "sealNumber");
            final String pledgeNumber = rs.getString("pledgeNumber");
            final Integer statusEnum = rs.getInt("status");
            final EnumOptionData status = PledgeApiConstants.PLEDGE_STATUS_PARAMS.status(statusEnum);
            final BigDecimal systemValue = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "systemValue");
            final BigDecimal userValue = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "userValue");
            final Long createdBy = rs.getLong("createdBy");
            final Date createdDate = rs.getDate("createdDate");
            final Long updatedBy = JdbcSupport.getLongDefaultToNullIfZero(rs, "updatedBy");
            final Date updatedDate = rs.getDate("updatedDate");
            
            return PledgeData.createNew(id, clientId, loanId, loanAccountNo, officeName, clientName, sealNumber, pledgeNumber, status, systemValue, userValue, createdBy,
                    createdDate, updatedBy, updatedDate);
        }
        
    }

    @Override
    public Page<PledgeData> retrieveAllPledges(final SearchParameters searchParameters) {
        
        this.context.authenticatedUser();
        
        PledgesMapper rm = new PledgesMapper();
        
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(rm.schema());
        
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
        }
        
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), new Object[] { }, rm);
    }

    @Override
    public PledgeData retrieveTemplate(final Long collateralId) {
        
        Collection<CollateralsData> collateralDataOptions = null;
        Collection<QualityStandardsData> qualityStandardsDataOptions = null;
        BigDecimal systemCalculatedPrice = new BigDecimal(0);
        if(collateralId != null){
            Collaterals collateral = this.collateralsRepositoryWrapper.findOneWithNotFoundDetection(collateralId);
            final BigDecimal baseUnitPrice = collateral.getBaseUnitPrice();       
            qualityStandardsDataOptions = this.qualityStandardsReadPlatformService.retrieveAllQualityStandardsByCollateralIdForDropdown(collateralId);
            systemCalculatedPrice = getSystemCalculatedPrice(qualityStandardsDataOptions, systemCalculatedPrice, baseUnitPrice);
        }else{
            collateralDataOptions = this.collateralsReadPlatformService.retrieveAllCollateralsForDropdown();
            qualityStandardsDataOptions = this.qualityStandardsReadPlatformService.retrieveAllQualityStandardsForDropdown();
        }
            
        return PledgeData.retrieveTemplateData(systemCalculatedPrice, collateralDataOptions, qualityStandardsDataOptions);
    }

    private BigDecimal getSystemCalculatedPrice(final Collection<QualityStandardsData> qualityStandardsDataOptions, BigDecimal systemCalculatedPrice, 
            final BigDecimal baseUnitPrice) {
        
        BigDecimal percentagePrice;
        BigDecimal divisor = new BigDecimal("100.0");
        BigDecimal noOfUnits = new BigDecimal(qualityStandardsDataOptions.size());
        for(QualityStandardsData qualityStandardsData : qualityStandardsDataOptions){
            if(qualityStandardsData.getPercentagePrice() != null){
                percentagePrice = qualityStandardsData.getPercentagePrice();
                systemCalculatedPrice = systemCalculatedPrice.add((baseUnitPrice.multiply(percentagePrice)).divide(divisor));
            }else{
                systemCalculatedPrice = systemCalculatedPrice.add(baseUnitPrice.multiply(noOfUnits));
            }
            
        }
        return systemCalculatedPrice;
    }

    @Override
    public PledgeData retriveOne(final Long pledgeId) {
        try {
            this.context.authenticatedUser();       
            PledgesMapper rm = new PledgesMapper();      
            String query = "SELECT " + rm.schema() + " WHERE p.id = ?";       
            return this.jdbcTemplate.queryForObject(query, rm, new Object[] { pledgeId });
        }catch(final EmptyResultDataAccessException e){
            throw new PledgeNotFoundException(pledgeId);
        }

    }

    @Override
    public Collection<CollateralDetailsData> retrieveCollateralDetailsByPledgeId(final Long pledgeId) {
        try{
            this.context.authenticatedUser();
            CollateralDetailsMapper rm = new CollateralDetailsMapper();
            String query = "SELECT " + rm.schema() + " WHERE cd.pledge_id = ? ";
            return this.jdbcTemplate.query(query, rm, new Object[] { pledgeId });
        }catch(final EmptyResultDataAccessException e){
            throw new PledgeNotFoundException(pledgeId);
        }
    }
    
    private static final class CollateralDetailsMapper implements RowMapper<CollateralDetailsData> {
        
        private final String sql = "cd.id as id, cd.pledge_id as pledgeId, cd.collateral_id as collateralId, cd.quality_standard_id as qualityStandardId , " +
        		" ct.name as collateralName, qs.name as qualityStandardName, cd.description as description, cd.gross_weight as grossWeight, cd.net_weight as netWeight, " +
        		" cd.system_price as systemPrice, cd.user_price as userPrice from m_collateral_details cd " +
        		" left join m_collateral_quality_standards qs on cd.quality_standard_id = qs.id " +
        		" left join m_collateral_type ct on cd.collateral_id = ct.id";
        
        public String schema() {
            return this.sql;
        }

        @Override
        public CollateralDetailsData mapRow(ResultSet rs , @SuppressWarnings("unused") int rowNum) throws SQLException {
            
            final Long id = rs.getLong("id");
            final Long pledgeId = rs.getLong("pledgeId");
            final Long collateralId = rs.getLong("collateralId");
            final String collateralName = rs.getString("collateralName");
            final Long qualityStandardId = rs.getLong("qualityStandardId");
            final String qualityStandardName = rs.getString("qualityStandardName");
            final String description = rs.getString("description");
            final BigDecimal grossWeight = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "grossWeight");
            final BigDecimal netWeight = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "netWeight");
            final BigDecimal systemPrice = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "systemPrice");
            final BigDecimal userPrice = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "userPrice");
            
            return CollateralDetailsData.instance(id, pledgeId, collateralId, collateralName, qualityStandardId, qualityStandardName, 
                    description, grossWeight, netWeight, systemPrice, userPrice);
        }
        
    }

    @Override
    public List<PledgeData> retrievePledgesByClientId(final Long clientId) {

        this.context.authenticatedUser();
        PledgesMapper rm = new PledgesMapper();
        String query = "SELECT " + rm.schema() + " WHERE p.client_id = ? ";
        
        return this.jdbcTemplate.query(query, rm, new Object[] { clientId });

    }

    @Override
    public Collection<PledgeData> retrievePledgesByClientIdAndProductId(final Long clientId, final Long productId, final Long loanId) {
        
        this.context.authenticatedUser();
        LoanProductPledgesMapper rm = new LoanProductPledgesMapper();
        StringBuilder query = new StringBuilder();
        query.append("SELECT " + rm.schema());
        query.append("WHERE p.client_id = ? and p.loan_id IS NULL  ");
        if(loanId != null){
            query.append(" OR p.loan_id = "+loanId+" " );
        }
        query.append(" and cd.collateral_id in ");
        query.append("(select pcm.collateral_id from m_product_to_collateral_mappings pcm where pcm.product_id = ?) ");
        query.append(" and  p.status != ? ");

        return this.jdbcTemplate.query(query.toString(), rm, new Object[] { clientId,  productId, PLEDGE_STATUS_PARAMS.CLOSE_PLEDGE.getValue() });
    }
    
    private static final class LoanProductPledgesMapper implements RowMapper<PledgeData> {
        
        private final String sql = " DISTINCT p.id as Id, p.pledge_number as pledgeNumber, p.system_value as systemValue, p.user_value as" +
        		" userValue from m_collateral_details cd join m_pledge p on cd.pledge_id = p.id " ;
 
        
        public String schema() {
            return this.sql;
        }

        @Override
        public PledgeData mapRow(ResultSet rs , @SuppressWarnings("unused") int rowNum) throws SQLException {
            
            final Long id = rs.getLong("id");
            final String pledgeNumber = rs.getString("pledgeNumber");
            final BigDecimal systemValue = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "systemValue");
            final BigDecimal userValue = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "userValue");
            
            return PledgeData.retrieveLoanPledgesTemplate(id, pledgeNumber,  systemValue, userValue);
        }
        
    }

    @Override
    public Long retrievePledgesByloanId(final Long loanId) {
        
        this.context.authenticatedUser();
        PledgesMapper rm = new PledgesMapper();
        String query = "SELECT " + rm.schema() + " WHERE p.loan_id = ? ";
        List<PledgeData> pledgeData = this.jdbcTemplate.query(query, rm, new Object[] { loanId });
        if(pledgeData.isEmpty()){
            return null;
        }
        return pledgeData.get(0).getPledgeId();

    }

	@Override
	public Integer retrieveNumberOfCollateralDetailsByQualityStandardId(
			Long qualityStandardId) {
		final String query = "SELECT count(*) from m_collateral_details WHERE quality_standard_id = ?";
		int count = this.jdbcTemplate.queryForObject(query, new Object[] { qualityStandardId }, Integer.class);
		return count;
	}

}
