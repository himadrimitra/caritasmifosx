package com.finflux.portfolio.external.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.external.exception.ExternalServicesNotFoundException;

@Service
public class ExternalServicesReadServiceImpl implements ExternalServicesReadService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExternalServicesReadServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ExternalServicesData findOneWithNotFoundException(final Long id) {
        ExternalServicesMapper externalServicesMapper = new ExternalServicesMapper();
        String sql = "select " + externalServicesMapper.schema() + " where es.id = ?";
        try {
            ExternalServicesData data = this.jdbcTemplate.queryForObject(sql, externalServicesMapper, id);
            return data;
        } catch (final EmptyResultDataAccessException e) {
            throw new ExternalServicesNotFoundException(id);
        }

    }

    @Override
    public Collection<ExternalServicesData> findExternalServicesByType(final Integer type) {
        ExternalServicesMapper externalServicesMapper = new ExternalServicesMapper();
        String sql = "select " + externalServicesMapper.schema() + " where es.id = ?";
        return this.jdbcTemplate.query(sql, externalServicesMapper, type);
    }

    private class ExternalServicesMapper implements RowMapper<ExternalServicesData> {

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" es.id as id, es.name as name, es.display_code as displayCode, es.type as type ");
            sb.append(" from f_external_service_details es ");
            return sb.toString();
        }

        @Override
        public ExternalServicesData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String displayCode = rs.getString("displayCode");
            final Integer type = JdbcSupport.getInteger(rs, "type");
            return new ExternalServicesData(id, name, displayCode, type);
        }

    }

}
