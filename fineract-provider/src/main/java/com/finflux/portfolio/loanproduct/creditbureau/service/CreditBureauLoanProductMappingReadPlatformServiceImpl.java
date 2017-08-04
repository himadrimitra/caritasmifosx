package com.finflux.portfolio.loanproduct.creditbureau.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
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
    private final OfficeReadPlatformService officeReadPlatformService;

    @Autowired
    public CreditBureauLoanProductMappingReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final OfficeReadPlatformService readPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeReadPlatformService = readPlatformService;
    }

    @Override
    public Collection<CreditBureauLoanProductMappingData> retrieveAllCreditbureauLoanproductMappingData() {
        this.context.authenticatedUser();
        final CreditbureauLoanproductDataMapper rm = new CreditbureauLoanproductDataMapper();
        final String sql = "select " + rm.schema() + " group by cblp.id order by cblp.id";
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
            sb.append("JOIN `f_creditbureau_loanproduct_office_mapping` cblpom ON cblpom.credit_bureau_loan_product_mapping_id = cblp.id ");
            sb.append("JOIN `m_product_loan` pl ON pl.id = cblpom.loan_product_id ");
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
            final CreditbureauLoanproductOfficeDataMapper rm = new CreditbureauLoanproductOfficeDataMapper();
            final String sql = "select " + rm.schema() + " WHERE cblp.id = ? order by cblp.id, office.id";
            CreditBureauLoanProductMappingData creditBureauLoanProductMappingData = this.jdbcTemplate.queryForObject(sql, rm,
                    new Object[] { productId });

            String orderBy = "id";
            String sortOrder = "ASC";
            boolean onlyManualEntries = false;

            final SearchParameters searchParameters = SearchParameters.forOffices(orderBy, sortOrder);

            final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOffices(onlyManualEntries, searchParameters);
            final Collection<OfficeData> availableOffices = new ArrayList<>();

            Collection<OfficeData> selectedOffices = creditBureauLoanProductMappingData.getSelectedOfficeList();

            for (OfficeData emp : offices) {
                boolean found = false;
                for (OfficeData tgtOffice : selectedOffices) {
                    if ((emp.getId().equals(tgtOffice.getId()))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    availableOffices.add(emp);
                }
            }

            creditBureauLoanProductMappingData.updateAvailableOfficeList(availableOffices);
            return creditBureauLoanProductMappingData;
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class CreditbureauLoanproductOfficeDataMapper implements RowMapper<CreditBureauLoanProductMappingData> {

        public String schema() {
            final StringBuilder sb = new StringBuilder(200);
            sb.append("cblp.id as cblpId ");
            sb.append(",cb.id as creditBureauID,cb.name as creditBureauName,cb.product as creditBureauProduct ");
            sb.append(",cb.country as country,concat(cb.product,' - ',cb.name,' - ',cb.country) as cbSummary ");
            sb.append(",cb.implementation_key as implementationKey, cb.is_active as cbIsActive ");
            sb.append(",pl.id as loanProductId ,pl.name as loanProductName ");
            sb.append(
                    ",cblp.is_creditcheck_mandatory as isCreditcheckMandatory,cblp.skip_creditcheck_in_failure as skipCreditcheckInFailure ");
            sb.append(",cblp.stale_period as stalePeriod,cblp.is_active as isActive, office.id as officeId, office.name as officeName  ");
            sb.append("FROM `f_creditbureau_loanproduct_mapping` cblp ");
            sb.append("JOIN `f_creditbureau_product` cb ON cb.id = cblp.creditbureau_product_id ");
            sb.append("JOIN `f_creditbureau_loanproduct_office_mapping` cblpom ON cblpom.credit_bureau_loan_product_mapping_id = cblp.id ");
            sb.append("LEFT JOIN  `m_office` office on cblpom.office_id = office.id ");
            sb.append("JOIN `m_product_loan` pl ON pl.id = cblpom.loan_product_id ");
            return sb.toString();
        }

        @Override
        public CreditBureauLoanProductMappingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            Long id = null;
            long creditBureauID;
            String name;
            String product;
            String country;
            String cbSummary;
            Boolean cbIsActive;
            CreditBureauData creditBureauData = null;
            Long loanProductId = null;
            String loanProductName = null;
            Boolean isCreditcheckMandatory = null;
            Boolean skipCreditcheckInFailure = null;
            Integer stalePeriod = null;
            Boolean isActive = false;
            Long officeId;
            String officeName;
            Collection<OfficeData> selectedOfficeList = new ArrayList<>();
            Collection<OfficeData> availableOffices = null;
            rs.beforeFirst();

            while (rs.next()) {
                id = rs.getLong("cblpId");
                creditBureauID = rs.getLong("creditBureauID");
                name = rs.getString("creditBureauName");
                product = rs.getString("creditBureauProduct");
                country = rs.getString("country");
                cbSummary = rs.getString("cbSummary");
                cbIsActive = rs.getBoolean("cbIsActive");
                creditBureauData = CreditBureauData.instance(creditBureauID, name, country, product, cbSummary, cbIsActive);
                loanProductId = rs.getLong("loanProductId");
                loanProductName = rs.getString("loanProductName");
                isCreditcheckMandatory = rs.getBoolean("isCreditcheckMandatory");
                skipCreditcheckInFailure = rs.getBoolean("skipCreditcheckInFailure");
                stalePeriod = JdbcSupport.getIntegeActualValue(rs, "stalePeriod");
                isActive = rs.getBoolean("isActive");
                officeId = rs.getLong("officeId");
                officeName = rs.getString("officeName");
                OfficeData office = OfficeData.lookup(officeId, officeName);
                selectedOfficeList.add(office);

            }

            return CreditBureauLoanProductMappingData.create(id, creditBureauData, loanProductId, loanProductName, isCreditcheckMandatory,
                    skipCreditcheckInFailure, stalePeriod, isActive, availableOffices, selectedOfficeList);
        }
    }

    @Override
    public Long retrieveCreditBureauLoanProductMappingId(Long creditBureauProductId, Long loanProductId) {
        final String sql = "select id from f_creditbureau_loanproduct_mapping where creditbureau_product_id = ?  and loan_product_id = ?";
        Long mappingId = this.jdbcTemplate.queryForObject(sql, Long.class, creditBureauProductId, loanProductId);
        return mappingId;
    }

}
