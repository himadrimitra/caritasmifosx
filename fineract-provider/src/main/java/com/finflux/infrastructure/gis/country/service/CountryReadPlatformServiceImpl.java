
package com.finflux.infrastructure.gis.country.service;

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

import com.finflux.infrastructure.gis.country.data.CountryData;
import com.finflux.infrastructure.gis.state.data.StateData;
import com.finflux.infrastructure.gis.state.service.StateReadPlatformService;

@Service
public class CountryReadPlatformServiceImpl implements CountryReadPlatformService {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final StateReadPlatformService stateReadPlatformService;

    @Autowired
    public CountryReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final StateReadPlatformService stateReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.stateReadPlatformService = stateReadPlatformService;
    }

    @Override
    public CountryData retrieveOne(final Long countryId,final boolean isTemplateRequired) {
        try {
            Collection<StateData> stateDatas = null;
            if (countryId != null && countryId > 0) {
                if(isTemplateRequired){
                stateDatas = this.stateReadPlatformService.retrieveAllStateDataByCountryId(countryId);
                }
                final CountryDataMapper dataMapper = new CountryDataMapper(stateDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE c.id = ? ";
                return this.jdbcTemplate.queryForObject(sql, dataMapper, new Object[] { countryId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<CountryData> retrieveAllCountryDataByCountryIds(final List<Long> countryIds,final boolean isTemplateRequired) {
        try {
            if (countryIds != null && !countryIds.isEmpty()) {
                final String countryIdsStr = StringUtils.join(countryIds, ',');
                Collection<StateData> stateDatas = null;
                if(isTemplateRequired){
                stateDatas = this.stateReadPlatformService.retrieveAllStateDataByCountryIds(countryIds);
                }
                final CountryDataMapper dataMapper = new CountryDataMapper(stateDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE c.id IN (" + countryIdsStr + ") ";
                return this.jdbcTemplate.query(sql, dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<CountryData> retrieveAll() {
        try {
            final String countryIdsSql = "SELECT c.id FROM f_country c";
            final List<Long> countryIds = this.jdbcTemplate.queryForList(countryIdsSql, Long.class);
            if (countryIds != null && !countryIds.isEmpty()) {
                final Collection<StateData> stateDatas = this.stateReadPlatformService.retrieveAllStateDataByCountryIds(countryIds);
                final String countryIdsStr = StringUtils.join(countryIds, ',');
                final CountryDataMapper dataMapper = new CountryDataMapper(stateDatas);
                final String sql = "SELECT " + dataMapper.schema() + " WHERE c.id IN(" + countryIdsStr + ") ";
                return this.jdbcTemplate.query(sql, dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class CountryDataMapper implements RowMapper<CountryData> {

        private final String schema;
        private final Collection<StateData> stateDatas;

        public CountryDataMapper(final Collection<StateData> stateDatas) {
            this.stateDatas = stateDatas;

            final StringBuilder builder = new StringBuilder(200);
            builder.append("c.id As countryId, c.iso_country_code AS isoCountryCode, ");
            builder.append("c.country_name As countryName ");
            builder.append("FROM f_country c ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @SuppressWarnings("unused")
        @Override
        public CountryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long countryId = rs.getLong("countryId");
            final String isoCountryCode = rs.getString("isoCountryCode");
            final String countryName = rs.getString("countryName");
            Collection<StateData> statesDatas = null;
            if (this.stateDatas != null && stateDatas.size() > 0) {
                statesDatas = new ArrayList<StateData>();
                for (final StateData stateData : this.stateDatas) {
                    if (stateData.getCountryId() == countryId) {
                        statesDatas.add(stateData);
                    }
                }
            }
            return CountryData.instance(countryId, isoCountryCode, countryName, statesDatas);
        }
    }

}
