package com.finflux.portfolio.loanproduct.creditbureau.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loanproduct.creditbureau.data.CreditBureauLoanProductMappingData;
import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;

@Service
public class CreditBureauLoanProductMappingReadPlatformServiceImpl implements CreditBureauLoanProductMappingReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CreditBureauLoanProductMappingReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<CreditBureauLoanProductMappingData> retrieveAllCreditbureauLoanproductMappingData() {
        this.context.authenticatedUser();
        final CreditbureauLoanproductDataMapper rm = new CreditbureauLoanproductDataMapper();
        final String sql = "select " + rm.schema() + " order by cblp.id";
        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    private static final class CreditbureauLoanproductDataMapper implements RowMapper<CreditBureauLoanProductMappingData> {

        public String schema() {
            final StringBuilder sb = new StringBuilder(200);
            sb.append("cblp.id as cblpId ");
            sb.append(",cb.id as creditBureauID,cb.name as creditBureauName,cb.product as creditBureauProduct ");
            sb.append(",cb.country as country,concat(cb.product,' - ',cb.name,' - ',cb.country) as cbSummary ");
            sb.append(",cb.implementation_key as implementationKey, cb.is_active as cbIsActive ");
            sb.append(",pl.id as loanProductId ,pl.name as loanProductName ");
            sb.append(",cblp.is_creditcheck_mandatory as isCreditcheckMandatory,cblp.skip_creditcheck_in_failure as skipCreditcheckInFailure ");
            sb.append(",cblp.stale_period as stalePeriod,cblp.is_active as isActive ");
            sb.append("FROM `f_creditbureau_loanproduct_mapping` cblp ");
            sb.append("JOIN `f_creditbureau_product` cb ON cb.id = cblp.creditbureau_product_id ");
            sb.append("JOIN `m_product_loan` pl ON pl.id = cblp.loan_product_id ");
            return sb.toString();
        }

        @Override
        public CreditBureauLoanProductMappingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final long id = rs.getLong("cblpId");
            final long creditBureauID = rs.getLong("creditBureauID");
            final String name = rs.getString("creditBureauName");
            final String product = rs.getString("creditBureauProduct");
            final String country = rs.getString("country");
            final String cbSummary = rs.getString("cbSummary");
            final Boolean cbIsActive = rs.getBoolean("cbIsActive");
            final CreditBureauData creditBureauData = CreditBureauData.instance(creditBureauID, name, country, product, cbSummary,
                    cbIsActive);
            final long loanProductId = rs.getLong("loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final Boolean isCreditcheckMandatory = rs.getBoolean("isCreditcheckMandatory");
            final Boolean skipCreditcheckInFailure = rs.getBoolean("skipCreditcheckInFailure");
            final Integer stalePeriod = JdbcSupport.getIntegeActualValue(rs, "stalePeriod");
            final Boolean isActive = rs.getBoolean("isActive");
            return CreditBureauLoanProductMappingData.instance(id, creditBureauData, loanProductId, loanProductName,
                    isCreditcheckMandatory, skipCreditcheckInFailure, stalePeriod, isActive);
        }
    }

    @Override
    public CreditBureauLoanProductMappingData retrieveCreditbureauLoanproductMappingData(final Long productId) {
        try {
            this.context.authenticatedUser();
            final CreditbureauLoanproductDataMapper rm = new CreditbureauLoanproductDataMapper();
            final String sql = "select " + rm.schema() + " WHERE pl.id = ? order by cblp.id";
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { productId });
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

}
