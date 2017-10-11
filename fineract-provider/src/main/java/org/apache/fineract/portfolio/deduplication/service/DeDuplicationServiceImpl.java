/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.deduplication.service;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientNonPersonData;
import org.apache.fineract.portfolio.client.data.ClientTimelineData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.deduplication.data.DeduplicationData;
import org.apache.fineract.portfolio.deduplication.exception.DuplicateEntityEntryException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	@Override
	public Collection<ClientData> getDuplicationMatches(final long clientId) {
		try{
			String sql = "CALL clientdedupmatches(?)";
			ClientDataMapper mapper = new ClientDataMapper();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		}catch (final EmptyResultDataAccessException e){
			return null;
		}
	}

	@Override
	public Collection<DeduplicationData> getDedupWeightages() {
		DedupWeightagesMapper mapper = new DedupWeightagesMapper();
		return this.jdbcTemplate.query(DedupWeightagesMapper.sql,mapper);
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

	private static final class ClientDataMapper implements RowMapper<ClientData> {

		@Override
		public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
			final Long id = JdbcSupport.getLong(rs, "id");
			final String firstname = rs.getString("firstname");
			final String middlename = rs.getString("middlename");
			final String lastname = rs.getString("lastname");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("display_name");
			final String mobileNo = rs.getString("mobile_no");
			final LocalDate dateOfBirth = JdbcSupport.getLocalDate(rs, "date_of_birth");

			final Long genderId = JdbcSupport.getLong(rs, "gender_cv_id");
			final String genderValue = rs.getString("gender_value");
			final CodeValueData gender = CodeValueData.instance(genderId, genderValue);

			final Integer legalFormEnum = JdbcSupport.getInteger(rs, "legal_form_enum");
			EnumOptionData legalForm = null;
			if(legalFormEnum != null)
				legalForm = ClientEnumerations.legalForm(legalFormEnum);

			final String incorpNo = rs.getString("incorp_no");

			final ClientNonPersonData clientNonPerson = new ClientNonPersonData(incorpNo);
			return ClientData.instanceDedup(id, firstname, middlename, lastname, fullname, displayName,
				mobileNo, dateOfBirth, gender, legalForm, clientNonPerson);

		}
	}

	private static final class DedupWeightagesMapper implements RowMapper<DeduplicationData> {
		public static final String sql = "select legal_form as legalForm, firstname_exact as firstnameExact, "
			+ "firstname_like as firstnameLike, middlename_exact as middlenameExact, middlename_like as middlenameLike, "
			+ "lastname_exact as lastnameExact, lastname_like as lastnameLike, fullname_exact as fullnameExact, "
			+ "fullname_like as fullnameLike, mobile_no as mobileNo, date_of_birth as dateOfBirth, "
			+ "gender_cv_id as genderCvId, incorp_no as incorpNo, client_identifier as clientIdentifier "
			+ "from f_client_dedup_weightage ";

		@Override
		public DeduplicationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
			final Integer legalForm = JdbcSupport.getInteger(rs,"legalForm");
			final Integer firstnameExact = JdbcSupport.getInteger(rs,"firstnameExact");
			final Integer firstnameLike = JdbcSupport.getInteger(rs,"firstnameLike");
			final Integer middlenameExact = JdbcSupport.getInteger(rs,"middlenameExact");
			final Integer middlenameLike = JdbcSupport.getInteger(rs,"middlenameLike");
			final Integer lastnameExact = JdbcSupport.getInteger(rs,"lastnameExact");
			final Integer lastnameLike = JdbcSupport.getInteger(rs,"lastnameLike");
			final Integer fullnameExact = JdbcSupport.getInteger(rs,"fullnameExact");
			final Integer fullnameLike = JdbcSupport.getInteger(rs,"fullnameLike");
			final Integer mobileNo = JdbcSupport.getInteger(rs,"mobileNo");
			final Integer dateOfBirth = JdbcSupport.getInteger(rs,"dateOfBirth");
			final Integer genderCvId = JdbcSupport.getInteger(rs,"genderCvId");
			final Integer incorpNo = JdbcSupport.getInteger(rs,"incorpNo");
			final Integer clientIdentifier = JdbcSupport.getInteger(rs,"clientIdentifier");

			return new DeduplicationData(legalForm, firstnameExact, firstnameLike, middlenameExact, middlenameLike,
				lastnameExact, lastnameLike, fullnameExact, fullnameLike, mobileNo, dateOfBirth, genderCvId,
				incorpNo, clientIdentifier);

		}
	}
}
