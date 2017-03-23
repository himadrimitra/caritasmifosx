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
package org.apache.fineract.infrastructure.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.sms.data.SmsData;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageEnumerations;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageStatusType;
import org.apache.fineract.infrastructure.sms.exception.SmsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SmsReadPlatformServiceImpl implements SmsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final SmsMapper smsRowMapper = new SmsMapper();
    private final PaginationHelper<SmsData> paginationHelper = new PaginationHelper<>();


    @Autowired
    public SmsReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class SmsMapper implements RowMapper<SmsData> {

        final String schema;

        public SmsMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append(" smo.id as id, ");
            sql.append("smo.external_id as externalId, ");
            sql.append("smo.group_id as groupId, ");
            sql.append("smo.client_id as clientId, ");
            sql.append("smo.staff_id as staffId, ");
            sql.append("smo.campaign_name as campaignName, ");
            sql.append("smo.status_enum as statusId, ");
            sql.append("smo.source_address as sourceAddress, ");
            sql.append("smo.mobile_no as mobileNo, ");
            sql.append("smo.submittedon_date as sentDate, ");
            sql.append("smo.message as message ");
            sql.append("from " + tableName() + " smo");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }
        
        public String tableName() {
        	return "sms_messages_outbound";
        }

        @Override
        public SmsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long externalId = JdbcSupport.getLong(rs, "externalId");
            final Long groupId = JdbcSupport.getLong(rs, "groupId");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");

            final String sourceAddress = rs.getString("sourceAddress");
            final String mobileNo = rs.getString("mobileNo");
            final String message = rs.getString("message");
            final String campaignName = rs.getString("campaignName");

            final Integer statusId = JdbcSupport.getInteger(rs, "statusId");
            final LocalDate sentDate = JdbcSupport.getLocalDate(rs, "sentDate");

            final EnumOptionData status = SmsMessageEnumerations.status(statusId);

            return SmsData.instance(id, externalId, groupId, clientId, staffId, status, sourceAddress, mobileNo, message,campaignName,sentDate);
        }
    }

    @Override
	public Page<SmsData> retrieveAll(final SearchParameters searchParameters) {
    	
    	final StringBuilder sqlBuilder = new StringBuilder(200);

        final String sql = this.smsRowMapper.schema();
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(sql);
        final Integer status = searchParameters.getStatus();
        if (status != null){
        	sqlBuilder.append(" where smo.status_enum = " + status);
        }
        
        final Date dateFrom = searchParameters.getStartDate();
        final Date dateTo = searchParameters.getEndDate();
        java.sql.Date fromDate = null;
        java.sql.Date toDate  = null;
        if(dateFrom !=null && dateTo !=null){
            fromDate = new java.sql.Date(dateFrom.getTime());
            toDate = new java.sql.Date(dateTo.getTime());
			if (status != null) {
				sqlBuilder.append(" and smo.submittedon_date >= ? and smo.submittedon_date <= ? ");
			} else {
				sqlBuilder.append(" where smo.submittedon_date >= ? and smo.submittedon_date <= ?  ");
			}
        }
        
        if (searchParameters.isLimited()) {
        	sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        final String sqlCountRows = " SELECT FOUND_ROWS() ";
        if(dateFrom !=null && dateTo !=null){
        	return this.paginationHelper.fetchPage(this.jdbcTemplate,sqlCountRows,sqlBuilder.toString(),new Object[]{fromDate,toDate},this.smsRowMapper);
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate,sqlCountRows,sqlBuilder.toString(),null,this.smsRowMapper);
    }

    @Override
    public SmsData retrieveOne(final Long resourceId) {
        try {
            final String sql = "select " + this.smsRowMapper.schema() + " where smo.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.smsRowMapper, new Object[] { resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new SmsNotFoundException(resourceId);
        }
    }
    
    @Override
	public Collection<SmsData> retrieveAllPending(final Integer limit) {
    	final String sqlPlusLimit = (limit > 0) ? " limit 0, " + limit : "";
    	final String sql = "select " + this.smsRowMapper.schema() + " where smo.status_enum = " 
    			+ SmsMessageStatusType.PENDING.getValue() + sqlPlusLimit;

        return this.jdbcTemplate.query(sql, this.smsRowMapper, new Object[] {});
    }

	@Override
	public List<Long> retrieveExternalIdsOfAllSent(final Integer limit) {
		final String sqlPlusLimit = (limit > 0) ? " limit 0, " + limit : "";
		final String sql = "select external_id from " + this.smsRowMapper.tableName() + " where status_enum = " 
    			+ SmsMessageStatusType.SENT.getValue() + sqlPlusLimit;
		
		return this.jdbcTemplate.queryForList(sql, Long.class);
	}
}