package com.finflux.risk.profilerating.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.data.ProfileRatingConfigData;
import com.finflux.risk.profilerating.data.ProfileRatingConfigTemplateData;
import com.finflux.risk.profilerating.data.ProfileRatingType;
import com.finflux.risk.profilerating.exception.ProfileRatingConfigNotFoundException;
import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;

@Service
public class ProfileRatingConfigReadPlatformServiceImpl implements ProfileRatingConfigReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final RiskConfigReadPlatformService riskConfigReadPlatformService;

    @Autowired
    public ProfileRatingConfigReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final RiskConfigReadPlatformService riskConfigReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.riskConfigReadPlatformService = riskConfigReadPlatformService;
    }

    @Override
    public ProfileRatingConfigTemplateData retrieveTemplate() {
        final Collection<EnumOptionData> typeOptions = ProfileRatingType.entityTypeOptions();
        final Collection<RuleData> criteriaOptions = this.riskConfigReadPlatformService.getAllCriterias();
        return ProfileRatingConfigTemplateData.template(typeOptions, criteriaOptions);
    }

    @Override
    public Collection<ProfileRatingConfigData> retrieveAll() {
        final ProfileRatingConfigDataMapper dm = new ProfileRatingConfigDataMapper();
        final String sql = "SELECT " + dm.schema();
        return this.jdbcTemplate.query(sql, dm, new Object[] {});
    }

    private static final class ProfileRatingConfigDataMapper implements RowMapper<ProfileRatingConfigData> {

        private final String schema;

        ProfileRatingConfigDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("prc.id AS id, prc.type AS typeId ");
            sqlBuilder.append(",rr.id AS criteriaId, rr.name AS criteriaName, rr.uname AS criteriaUname,prc.is_active AS criteriaIsActive ");
            sqlBuilder.append(",prc.is_active AS isActive ");
            sqlBuilder.append("FROM f_profile_rating_config prc ");
            sqlBuilder.append("JOIN f_risk_rule rr ON rr.id = prc.criteria_id ");
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @SuppressWarnings({ "unused" })
        @Override
        public ProfileRatingConfigData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Integer typeId = JdbcSupport.getIntegeActualValue(rs, "typeId");
            final EnumOptionData type = ProfileRatingType.profileRatingType(typeId);
            final Long criteriaId = rs.getLong("criteriaId");
            final String criteriaName = rs.getString("criteriaName");
            final String criteriaUname = rs.getString("criteriaUname");
            final boolean criteriaIsActive = rs.getBoolean("criteriaIsActive");
            final RuleData criteriaData = new RuleData(criteriaId, criteriaName, criteriaUname, criteriaIsActive);
            final boolean isActive = rs.getBoolean("isActive");
            return ProfileRatingConfigData.instance(id, type, criteriaData, isActive);
        }
    }

    @Override
    public ProfileRatingConfigData retrieveOne(final Long profileRatingConfigId) {
        try {
            final ProfileRatingConfigDataMapper dm = new ProfileRatingConfigDataMapper();
            final String sql = "SELECT " + dm.schema() + " WHERE prc.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, dm, new Object[] { profileRatingConfigId });
        } catch (EmptyResultDataAccessException ex) {
            throw new ProfileRatingConfigNotFoundException(profileRatingConfigId);
        }
    }
}