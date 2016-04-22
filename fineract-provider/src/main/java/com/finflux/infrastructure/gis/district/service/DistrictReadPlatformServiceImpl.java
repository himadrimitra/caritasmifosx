
package com.finflux.infrastructure.gis.district.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.district.data.DistrictData;

@Service
public class DistrictReadPlatformServiceImpl implements DistrictReadPlatformService {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final DistrictDataMapper dataMapper;

    @Autowired
    public DistrictReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataMapper = new DistrictDataMapper();
    }

    @Override
    public DistrictData retrieveOne(final Long districtId) {
        try {
            if (districtId != null && districtId > 0) {
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE d.id = ? ";
                return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { districtId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<DistrictData> retrieveAllDistrictDataByDistrictIds(final List<Long> districtIds) {
        try {
            if (districtIds != null && !districtIds.isEmpty()) {
                final String districtIdsStr = StringUtils.join(districtIds, ',');
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE d.id IN (" + districtIdsStr + ") ";
                return this.jdbcTemplate.query(sql, this.dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<DistrictData> retrieveAllDistrictDataByStateId(final Long stateId) {
        try {
            if (stateId != null && stateId > 0) {
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE d.state_id = ? ";
                return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { stateId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<DistrictData> retrieveAllDistrictDataByStateIds(final List<Long> stateIds) {
        try {
            if (stateIds != null && !stateIds.isEmpty()) {
                final String stateIdsStr = StringUtils.join(stateIds, ',');
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE d.state_id IN(" + stateIdsStr + ") ";
                return this.jdbcTemplate.query(sql, this.dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class DistrictDataMapper implements RowMapper<DistrictData> {

        private final String schema;

        public DistrictDataMapper() {
            final StringBuilder builder = new StringBuilder(200);
            builder.append("d.id As districtId, d.state_id AS stateId, d.iso_district_code AS isoDistrictCode, ");
            builder.append("d.district_name As districtName ");
            builder.append("FROM f_district d ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public DistrictData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long districtId = rs.getLong("districtId");
            final Long stateId = rs.getLong("stateId");
            final String isoDistrictCode = rs.getString("isoDistrictCode");
            final String districtName = rs.getString("districtName");
            return DistrictData.instance(districtId, stateId, isoDistrictCode, districtName);
        }
    }
}
