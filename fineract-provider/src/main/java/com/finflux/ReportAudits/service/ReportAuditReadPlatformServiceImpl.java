/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.data.PaginationParametersDataValidator;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.data.AppUserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.ReportAudits.data.ReportAuditData;
import com.finflux.ReportAudits.exception.DateRangeInvalidException;
import com.finflux.ReportAudits.exception.ReportAuditNotFoundException;
import com.finflux.ReportAudits.exception.ResourceCanNotBeNullException;

@Service
public class ReportAuditReadPlatformServiceImpl implements ReportAuditReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationParametersDataValidator paginationParametersDataValidator;
	private final PaginationHelper<ReportAuditData> paginationHelper = new PaginationHelper<>();
	private final static Set<String> supportedOrderByValues = new HashSet<>(Arrays.asList("id"));
	
	@Autowired
	public ReportAuditReadPlatformServiceImpl(final RoutingDataSource dataSource,
			final PlatformSecurityContext context,
			final PaginationParametersDataValidator paginationParametersDataValidator) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.paginationParametersDataValidator = paginationParametersDataValidator;
	}

	private static final class ReportAuditDataMapper implements RowMapper<ReportAuditData> {

	   	 public String schema() {
	       	
	       return	" ra.id as id, ra.param_as_json as reportParameters, "
	       + " ra.execution_start_date as executionStartDate, ra.execution_end_date as executionEndDate, "
	    	+" report.id as reportId, report.report_name as reportName , report.report_type as reportType , "
	    	+" user.id as userId, user.username as userName "
	    	+ " from f_stretchy_report_logs ra"
	    	+ " left join m_appuser user on user.id = ra.user_id "
	    	+ " left join stretchy_report report on report.id = ra.report_id ";
	       }

	       @SuppressWarnings("unused")
	       @Override
	       public ReportAuditData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

	           final Long id = JdbcSupport.getLong(rs, "id");
	           final String reportParameters = rs.getString("reportParameters");
	           final Date executionStartDate = rs.getTimestamp("executionStartDate");
	           final Date executionEndDate = rs.getTimestamp("executionEndDate");
	           
	           final Long userId = rs.getLong("userId");
	           final String userName = rs.getString("userName");
	           
	           final Integer reportId = JdbcSupport.getInteger(rs, "reportId");	           
	           final String reportType = rs.getString("reportType");
	           final String reportName = rs.getString("reportName");
	           
	           AppUserData user = AppUserData.dropdown(userId, userName);
	           
	           String executionTime = ReportAuditReadPlatformServiceImpl.getDuration(executionStartDate, executionEndDate);
	           return ReportAuditData.instance(id, reportId, reportName, reportType, user, executionStartDate, executionEndDate, reportParameters, executionTime);

	       }
	   }	

	@Override
	public ReportAuditData getReportAudits(Long id) {
		this.context.authenticatedUser();
		ReportAuditDataMapper rm = new ReportAuditDataMapper();
		String sql = " select "+rm.schema()+" where ra.id = ?";
		List<ReportAuditData> reportAuditList = this.jdbcTemplate.query(sql, rm, new Object[] {id});
		if(reportAuditList.size()==0){
			throw new ReportAuditNotFoundException(id);
		}
		return reportAuditList.get(0);
	}
	
	public static String getDuration(Date startDate, Date endDate){
		long duration  = (endDate.getTime() - startDate.getTime())/1000;
		long diffInHours = (duration/3600);
		duration = duration%3600;
		long diffInMinutes = (duration)/60;
		duration = duration%60;
		float diffInMilli = duration%1000;
		String hours = (diffInHours<10)?"0"+diffInHours:diffInHours+"";
		String minutes = (diffInMinutes<10)?"0"+diffInMinutes:diffInMinutes+"";
		String seconds = (duration<10)?"0"+duration:duration+"";
		String millis = (duration<10)?"0"+diffInMilli:diffInMilli+"";
		return hours+":"+minutes+":"+seconds+":"+millis;
	}

	@Override
	public Page<ReportAuditData> retrieveAllReportAudits(
			SearchParameters searchParameters, PaginationParameters parameters) {
		
		this.paginationParametersDataValidator.validateParameterValues(parameters, supportedOrderByValues, "audits");
		validateRequest(searchParameters);
		ReportAuditDataMapper rm = new ReportAuditDataMapper();
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(rm.schema());
        sqlBuilder.append(" where ra.id > 0 ");
        final String extraCriteria = getSearchCriteria(searchParameters);
        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(extraCriteria);
        }
        if (parameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder());
        }

        if (parameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
        		new Object[] {}, rm);
	}
	
	private String getSearchCriteria(SearchParameters searchCriteria){
		
		StringBuffer extraCriteria = new StringBuffer(200);
        final Long userId = searchCriteria.getUserId();
        final Integer reportId = searchCriteria.getReportId();
        final Date startDate = searchCriteria.getStartDate();
        final Date endDate = searchCriteria.getEndDate();
        if(reportId > -1){
        	extraCriteria.append(" and ra.report_id = ").append(reportId);
        }
        if(userId > -1){
        	extraCriteria.append(" and ra.user_id = ").append(userId);
        }
        if(startDate != null){
        	extraCriteria.append(" and (UNIX_TIMESTAMP(Date(ra.execution_start_date))*1000) >= ").append(startDate.getTime());
        }
        if(endDate != null){
        	extraCriteria.append(" and (UNIX_TIMESTAMP(Date(ra.execution_start_date))*1000) < ").append((endDate.getTime()+24*3600*1000));
        }
        
        return extraCriteria.toString();
	}
	
	private void validateRequest(SearchParameters searchCriteria){
		final Long userId = searchCriteria.getUserId();
        final Integer reportId = searchCriteria.getReportId();
        final Date startDate = searchCriteria.getStartDate();
        final Date endDate = searchCriteria.getEndDate();
        if(reportId == null){
        	throw new ResourceCanNotBeNullException("report");
        }
        if(userId == null){
        	throw new ResourceCanNotBeNullException("user");
        }
        if(startDate != null && endDate != null && startDate.getTime()>endDate.getTime()){
        	throw new DateRangeInvalidException();
        }
	}

}
