package org.apache.fineract.portfolio.validations.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientIdentifierData;

import org.apache.fineract.portfolio.validations.data.EntityFieldRegexValidation;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class EntityFieldRegexValidationReadPlatformServiceImp implements EntityFieldRegexValidationReadPlatfromService {
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public EntityFieldRegexValidationReadPlatformServiceImp(final PlatformSecurityContext context,
			final RoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Collection<EntityFieldRegexValidation> retrieveEntityFieldType(Integer entityType) {

		final AppUser currentUser = this.context.authenticatedUser();

		final EntityFieldTypeMapper rm = new EntityFieldTypeMapper();

		String sql = "select " + rm.schema() + " where et.entity_type= ?";

		Collection<EntityFieldRegexValidation> entityFieldType = this.jdbcTemplate.query(sql, rm,
				new Object[] { entityType });
		return entityFieldType;
	}

	private static final class EntityFieldTypeMapper implements RowMapper<EntityFieldRegexValidation> {
		public EntityFieldTypeMapper() {
		}

		public String schema() {
			return "et.entity_type as entityType, et.preriquisities_type as preriquisitesType , et.field_name as fieldName, et.regex as regex,et.error_msg as error"
					+ " from m_entity_field_type et";

		}

		@Override
		public EntityFieldRegexValidation mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Integer entityType = rs.getInt("entityType");
			final String preriquisitesType = rs.getString("preriquisitesType");
			final String fieldName = rs.getString("fieldName");
			final String regex = rs.getString("regex");
			final String errorMsg = rs.getString("error");

			return EntityFieldRegexValidation.intance(entityType, preriquisitesType, fieldName, regex, errorMsg);

		}

	}

}
