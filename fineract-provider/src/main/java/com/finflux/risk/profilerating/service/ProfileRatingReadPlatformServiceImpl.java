package com.finflux.risk.profilerating.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.data.ProfileRatingScoreData;

@Service
public class ProfileRatingReadPlatformServiceImpl implements ProfileRatingReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProfileRatingReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ProfileRatingScoreData retrieveProfileRatingScoreByEntityTypeAndEntityId(final Integer entityType, final Long entityId) {
        try {
            final ProfileRatingScoreDataMapper dataMapper = new ProfileRatingScoreDataMapper();
            final String sql = "SELECT " + dataMapper.schema() + " WHERE prs.entity_type = ? AND prs.entity_id = ? ";
            return this.jdbcTemplate.queryForObject(sql, dataMapper, new Object[] { entityType, entityId });
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class ProfileRatingScoreDataMapper implements RowMapper<ProfileRatingScoreData> {

        private final String schema;

        public ProfileRatingScoreDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("prs.id AS id, prs.computed_score AS computedScore, prs.overridden_score AS overriddenScore ");
            sqlBuilder.append(",prs.final_score AS finalScore, prs.criteria_result AS criteriaResult ");
            sqlBuilder.append(",prs.updated_time AS updatedTime ");
            sqlBuilder.append("FROM f_profile_rating_score prs ");
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @SuppressWarnings({ "unused" })
        @Override
        public ProfileRatingScoreData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Integer computedScore = rs.getInt("computedScore");
            final Integer overriddenScore = rs.getInt("overriddenScore");
            final Integer finalScore = rs.getInt("finalScore");
            final String criteriaResult = rs.getString("criteriaResult");
            final Date updatedTime = rs.getTimestamp("updatedTime");
            return ProfileRatingScoreData.instance(id, computedScore, overriddenScore, finalScore, criteriaResult, updatedTime);
        }
    }

}