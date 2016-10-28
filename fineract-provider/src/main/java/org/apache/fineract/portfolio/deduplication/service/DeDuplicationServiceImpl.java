/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.deduplication.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.deduplication.exception.DuplicateEntityEntryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeDuplicationServiceImpl implements DeDuplicationService {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public DeDuplicationServiceImpl(final RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void duplicationCheck(Map<String, Object> data, String table, boolean creation) {
		String sqlForDeDuplicationColumns = "select ddc.de_duplication_columns from de_duplication_criteria ddc "
				+ "join de_duplication_table ddt on ddt.id = ddc.criteria where ddc.active = 1 and ddt.table_name = ? ";
		String sqlForDeDuplicationNullColumns = "select ddc.de_duplication_for_null_columns from de_duplication_criteria ddc "
				+ "join de_duplication_table ddt on ddt.id = ddc.criteria where ddc.active = 1 and ddt.table_name = ? ";
		String sqlForErrorMessage = "select ddt.error_message from de_duplication_table ddt where ddt.table_name = ?";
		String sqlForJoinTableDetails = "select ddt.join_tables from de_duplication_table ddt where ddt.table_name = ?";
		List<String> columnDetails = this.jdbcTemplate.queryForList(sqlForDeDuplicationColumns, String.class,
				new Object[] { table });
		List<String> nullColumnDetails = this.jdbcTemplate.queryForList(sqlForDeDuplicationNullColumns, String.class,
				new Object[] { table });
		String errorMessage = this.jdbcTemplate.queryForObject(sqlForErrorMessage, String.class,
				new Object[] { table });
		String joinTableDetails = this.jdbcTemplate.queryForObject(sqlForJoinTableDetails, String.class,
				new Object[] { table });
		for (String columnDetail : columnDetails) {
			StringBuilder builder = new StringBuilder("");
			if (!builder.toString().equals("")) {
				builder.append(" and ");
			}
			String columnArray[] = columnDetail.split(",");
			for (int i = 0; i < columnArray.length; i++) {
				Object dataValue = data.get(columnArray[i]);
				builder.append(" alias." + columnArray[i]);
				if(dataValue == null && nullColumnDetails.contains(columnArray[i])){
					builder.append(" is null ");
				}
				else{
					builder.append(" = " + "'" + data.get(columnArray[i]) + "' ");
				}
				if (i < columnArray.length - 1) {
					builder.append(" and ");
				}
			}
			String joinTableDetailsArray[] = joinTableDetails.split(":");
			StringBuilder joinBuilder = new StringBuilder("");
			StringBuilder joinAliasBuilder = new StringBuilder("join_alias");
			StringBuilder joinSelectBuilder = new StringBuilder("");
			for (int i = 0; i <= joinTableDetailsArray.length - 1; i++) {
				if (!joinBuilder.toString().equals("")) {
					joinAliasBuilder.append(i);
				}
				String joinTables[] = joinTableDetailsArray[i].split(",");
				joinBuilder.append(" join " + joinTables[0] + " " + joinAliasBuilder.toString() + " on "
						+ joinAliasBuilder.toString() + "." + joinTables[2] + " = alias." + joinTables[1]);
				String culmnsForSelect[] = joinTables[3].split(";");
				for(int x=0; x < culmnsForSelect.length; x++){
				joinSelectBuilder.append("," + joinAliasBuilder.toString() + "." + culmnsForSelect[x]+" ");
				}
			}
			String sql = "select alias.* " + joinSelectBuilder.toString() + " from " + table + " alias "
					+ joinBuilder.toString() + " where " + builder.toString();
			List<Map<String, Object>> existingData = this.jdbcTemplate.queryForList(sql);
			if (!existingData.isEmpty()) {
				throwDuplicateEntryException(existingData, creation, data, errorMessage);
			}

		}
	}
	
	private void throwDuplicateEntryException( List<Map<String, Object>> existingData, boolean creation, Map<String, Object> data,
			String errorMessage	){
		if((!creation && existingData.size() > 1) || (creation && existingData.size() > 0) )
		{
			Map<String, Object> entity = existingData.get(0);
			StringBuilder builder = new StringBuilder("");
			Set<String>dataKeys =  data.keySet();
			for (String coulnName: dataKeys){
				if(!builder.toString().equals("")){
					builder.append(",");
				}
				builder.append(entity.get(coulnName));
			}
			Object coulmnArray[] = builder.toString().split(",");
			
		throw new DuplicateEntityEntryException(errorMessage, null, coulmnArray);
		
		}
	}
	
}
