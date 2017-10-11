
package com.finflux.infrastructure.gis.state.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.finflux.infrastructure.gis.district.service.DistrictReadPlatformService;
import com.finflux.infrastructure.gis.state.data.StateData;

@Service
public class StateReadPlatformServiceImpl implements StateReadPlatformService {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final DistrictReadPlatformService districtReadPlatformService;

    @Autowired
    public StateReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final DistrictReadPlatformService districtReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.districtReadPlatformService = districtReadPlatformService;
    }

    @Override
    public StateData retrieveOne(final Long stateId,final boolean isTemplateRequired) {
        try {
            if (stateId != null && stateId > 0) {
                Collection<DistrictData> districtDatas = null;
                if(isTemplateRequired){
                districtDatas = this.districtReadPlatformService.retrieveAllDistrictDataByStateId(stateId);
                }
                final StateDataMapper dataMapper = new StateDataMapper(districtDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE s.id = ? ";
                return this.jdbcTemplate.queryForObject(sql, dataMapper, new Object[] { stateId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<StateData> retrieveAllStateDataByStateIds(final List<Long> stateIds,final boolean isTemplateRequired) {
        try {
            if (stateIds != null && !stateIds.isEmpty()) {
                final String stateIdsStr = StringUtils.join(stateIds, ',');
                Collection<DistrictData> districtDatas = null;
                if(isTemplateRequired){
                districtDatas = this.districtReadPlatformService.retrieveAllDistrictDataByStateIds(stateIds);
                }
                final StateDataMapper dataMapper = new StateDataMapper(districtDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE s.id IN(" + stateIdsStr + ") ";
                return this.jdbcTemplate.query(sql, dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<StateData> retrieveAllStateDataByCountryId(final Long countryId) {
        try {
            if (countryId != null && countryId > 0) {
                final String stateIdsSql = "SELECT s.id FROM f_state s WHERE s.country_id = " + countryId + "";
                final List<Long> stateIds = this.jdbcTemplate.queryForList(stateIdsSql, Long.class);
                final Collection<DistrictData> districtDatas = this.districtReadPlatformService.retrieveAllDistrictDataByStateIds(stateIds);
                final StateDataMapper dataMapper = new StateDataMapper(districtDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE s.country_id = ? ";
                return this.jdbcTemplate.query(sql, dataMapper, new Object[] { countryId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<StateData> retrieveAllStateDataByCountryIds(final List<Long> countryIds) {
        try {
            if (countryIds != null && !countryIds.isEmpty()) {
                final String countryIdsStr = StringUtils.join(countryIds, ',');
                final String stateIdsSql = "SELECT s.id FROM f_state s WHERE s.country_id IN( " + countryIdsStr + " ) ";
                final List<Long> stateIds = this.jdbcTemplate.queryForList(stateIdsSql, Long.class);
                final Collection<DistrictData> districtDatas = this.districtReadPlatformService.retrieveAllDistrictDataByStateIds(stateIds);
                final StateDataMapper dataMapper = new StateDataMapper(districtDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE s.country_id IN( " + countryIdsStr + " ) ";
                return this.jdbcTemplate.query(sql, dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class StateDataMapper implements RowMapper<StateData> {

        private final Collection<DistrictData> districtDatas;
        private final String schema;

        public StateDataMapper(final Collection<DistrictData> districtDatas) {
            this.districtDatas = districtDatas;

            final StringBuilder builder = new StringBuilder(200);
            builder.append("s.id As stateId, s.country_id AS countryId, s.iso_state_code AS isoStateCode, ");
            builder.append("s.state_name As stateName ");
            builder.append("FROM f_state s ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @SuppressWarnings("unused")
        @Override
        public StateData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long stateId = rs.getLong("stateId");
            final Long countryId = rs.getLong("countryId");
            final String isoStateCode = rs.getString("isoStateCode");
            final String stateName = rs.getString("stateName");
            Collection<DistrictData> districtDatas = new ArrayList<DistrictData>();
            if (this.districtDatas != null && this.districtDatas.size() > 0) {
                for (final DistrictData districtData : this.districtDatas) {
                    if (districtData.getStateId() .equals(stateId)) {
                        districtDatas.add(districtData);
                    }
                }
            }
            return StateData.instance(stateId, countryId, isoStateCode, stateName, districtDatas);
        }
    }
}
