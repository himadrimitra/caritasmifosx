package com.finflux.risk.creditbureau.provider.highmark.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.highmark.data.HighmarkDataConstants;

@Service
public class HighmarkDataReadPlatformServiceImpl implements HighmarkDataReadPlatformService {

    final HighmarkDataExtractor highmarkDataExtractor = new HighmarkDataExtractor();
    final JdbcTemplate jdbcTemplate;

    @Autowired
    public HighmarkDataReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<HighmarkData> getHighmarkData(Long clientId) {
        final String sql = "select " + this.highmarkDataExtractor.schema();
        return this.jdbcTemplate.query(sql, this.highmarkDataExtractor, new Object[] { clientId });
    }

    public static final class HighmarkDataExtractor implements RowMapper<HighmarkData> {

        private String schema;

        public HighmarkDataExtractor() {
            final StringBuilder builder = new StringBuilder(400);
            builder.append(" ccrd.active_loan_count activelncnt,");
            builder.append(" ccrd.closed_loan_count closedlncnt,");
            builder.append(" ccrd.delinquent_loan_count dellncnt,");
            builder.append(" ccrd.total_outstanding outstanding,");
            builder.append(" ccrd.total_overdues overdues,");
            builder.append(" ccrd.total_installments installments, ");
            builder.append(" ccrr.last_request_date_time responsedate");
            builder.append(" from ct_credit_res_client_details ccrd");
            builder.append(" inner join ct_credit_request_response ccrr on ccrd.request_response_id = ccrr.id");
            builder.append(" where ccrd.client_id = ?");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public HighmarkData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long activeLoanCount = rs.getLong(HighmarkDataConstants.ACTIVELOANCOUNT);
            final Long closedLoanCount = rs.getLong(HighmarkDataConstants.CLOSEDLOANCOUNT);
            final Long delinquentLoanCount = rs.getLong(HighmarkDataConstants.DELINQUENTLOANCOUNT);
            final BigDecimal totalOutstanding = rs.getBigDecimal(HighmarkDataConstants.TOTALOUTSTANDING);
            final BigDecimal totalOverDues = rs.getBigDecimal(HighmarkDataConstants.TOTALOVERDUE);
            final BigDecimal totalInstallments = rs.getBigDecimal(HighmarkDataConstants.TOTALINSTALMENTS);
            final Date responseDate = rs.getDate(HighmarkDataConstants.RESPONSEDATE);
            return new HighmarkData(activeLoanCount, closedLoanCount, delinquentLoanCount, totalOutstanding, totalOverDues,
                    totalInstallments,responseDate);
        }

    }

}
