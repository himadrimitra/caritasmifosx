package com.finflux.risk.creditbureau.configuration.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.exception.CodeNotFoundException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;

@Service
public class CreditBureauProductReadPlatformServiceImpl implements CreditBureauProductReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CreditBureauProductReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<CreditBureauData> retrieveCreditBureaus() {
        this.context.authenticatedUser();

        final CBMapper rm = new CBMapper();
        final String sql = "select " + rm.schema() + " order by id";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    private static final class CBMapper implements RowMapper<CreditBureauData> {

        public String schema() {
            return "cb.id as creditBureauID,cb.name as creditBureauName,cb.product as creditBureauProduct,"
                    + "cb.country as country,concat(cb.product,' - ',cb.name,' - ',cb.country) as cbSummary,"
                    + "cb.implementation_key as implementationKey, cb.is_active as is_active from `f_creditbureau_product` cb";
        }

        @Override
        public CreditBureauData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long id = rs.getLong("creditBureauID");
            final String name = rs.getString("creditBureauName");
            final String product = rs.getString("creditBureauProduct");
            final String country = rs.getString("country");
            final String cbSummary = rs.getString("cbSummary");
            // final long implementationKey = rs.getLong("implementationKey");
            final boolean is_active = rs.getBoolean("is_active");

            return CreditBureauData.instance(id, name, country, product, cbSummary, is_active);

        }
    }

    @Override
    public CreditBureauData retrieveCreditBureau(Long creditBureauId) {
        try {
            this.context.authenticatedUser();
            final CBMapper rm = new CBMapper();
            final String sql = "select " + rm.schema() + " where cb.id = ? order by id";
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { creditBureauId });
        } catch (final EmptyResultDataAccessException e) {
            throw new CodeNotFoundException(creditBureauId);
        }

    }
}
