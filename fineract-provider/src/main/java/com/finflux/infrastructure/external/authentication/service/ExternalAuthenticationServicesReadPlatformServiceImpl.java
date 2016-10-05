package com.finflux.infrastructure.external.authentication.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;
import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServicesDataConstants;
import com.finflux.infrastructure.external.authentication.exception.ExternalAuthenticationServiceNotFoundException;

@Service
public class ExternalAuthenticationServicesReadPlatformServiceImpl
		implements ExternalAuthenticationServicesReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	// data mapper
	private final ExternalAutenticationServicesMapper externalAutenticationServicesMapper = new ExternalAutenticationServicesMapper();
	private final PaginationHelper<ExternalAuthenticationServiceData> paginationHelper = new PaginationHelper<>();

	@Autowired
	private ExternalAuthenticationServicesReadPlatformServiceImpl(final RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Collection<ExternalAuthenticationServiceData> getOnlyActiveExternalAuthenticationServices() {
		String sql = "select " + this.externalAutenticationServicesMapper.schema()
				+ " from f_authentication a where a.is_active = true;";
		return this.jdbcTemplate.query(sql, this.externalAutenticationServicesMapper, new Object[] {});
	}

	@Override
	public Page<ExternalAuthenticationServiceData> retrieveAllExternalAuthenticationServices() {
		String sql = "select " + this.externalAutenticationServicesMapper.schema() + " from f_authentication a;";
		final String sqlCountRows = "SELECT FOUND_ROWS()";
		return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql, new Object[] {},
				this.externalAutenticationServicesMapper);
	}

	@Override
	public ExternalAuthenticationServiceData retrieveOneExternalAuthenticationService(
			final Long externalAuthenticationServiceId) {
		try {
			String sql = "select " + this.externalAutenticationServicesMapper.schema()
					+ " from f_authentication a where a.id=? ;";
			ExternalAuthenticationServiceData externalAuthenticationServiceData = this.jdbcTemplate.queryForObject(sql,
					this.externalAutenticationServicesMapper, new Object[] { externalAuthenticationServiceId });
			return externalAuthenticationServiceData;
		} catch (final EmptyResultDataAccessException e) {
			throw new ExternalAuthenticationServiceNotFoundException(externalAuthenticationServiceId);
		}
	}

	private static final class ExternalAutenticationServicesMapper
			implements RowMapper<ExternalAuthenticationServiceData> {
		private final String schema;

		public String schema() {
			return schema;
		}

		public ExternalAutenticationServicesMapper() {
			final StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append("a.id, a.name, a.description, a.auth_service_class_name, a.is_active ");
			this.schema = sqlBuilder.toString();
		}

		@Override
		public ExternalAuthenticationServiceData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = JdbcSupport.getLong(rs, ExternalAuthenticationServicesDataConstants.ID);
			final String name = rs.getString(ExternalAuthenticationServicesDataConstants.NAME);
			final String description = rs.getString(ExternalAuthenticationServicesDataConstants.DESCRIPTION);
			final String authServiceClassName = rs
					.getString(ExternalAuthenticationServicesDataConstants.AUTH_SERVICE_CLASS_NAME);
			final boolean isActive = rs.getBoolean(ExternalAuthenticationServicesDataConstants.IS_ACTIVE);
			return ExternalAuthenticationServiceData.instance(id, name, description, authServiceClassName, isActive);
		}

	}

	public ExternalAuthenticationServiceData retrieveOneActiveExternalAuthenticationService(
			final Long externalAuthenticationServiceId) {
		try {
			String sql = "select " + this.externalAutenticationServicesMapper.schema()
					+ " from f_authentication a where a.id=? and is_active=true;";
			ExternalAuthenticationServiceData externalAuthenticationServiceData = this.jdbcTemplate.queryForObject(sql,
					this.externalAutenticationServicesMapper, new Object[] { externalAuthenticationServiceId });
			return externalAuthenticationServiceData;
		} catch (final EmptyResultDataAccessException e) {
			throw new ExternalAuthenticationServiceNotFoundException(externalAuthenticationServiceId);
		}
	}
}
