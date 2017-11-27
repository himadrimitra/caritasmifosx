package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentProductNotFoundException;
import com.finflux.portfolio.investmenttracker.data.InvestmentProductData;

@Service
public class InvestmentProductReadServiceImpl implements InvestmentProductReadService {

    private final JdbcTemplate jdbcTemplate;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final InvestmentTrackerDropDownReadService dropdownReadPlatformService;
    private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;

    @Autowired
    public InvestmentProductReadServiceImpl(final RoutingDataSource dataSource,
            final CurrencyReadPlatformService currencyReadPlatformService,
            final InvestmentTrackerDropDownReadService dropdownReadPlatformService,
            final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService, final ChargeReadPlatformService chargeReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.accountingDropdownReadPlatformService = accountingDropdownReadPlatformService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
    }

    @Override
    public InvestmentProductData retrieveInvestmentProductTemplate(InvestmentProductData investmentProductData) {

        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
        final List<EnumOptionData> interestRateFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();
        final List<EnumOptionData> investmentCompoundingPeriodTypeOptions = this.dropdownReadPlatformService
                .retrieveCompoundingInterestPeriodTypeOptions();
        final List<EnumOptionData> investmentTermFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveInvestmentTermFrequencyTypeOptions();
        final Collection<ChargeData> chargeOptions = this.chargeReadPlatformService.retrieveInvestmentProductApplicableCharges();
        final Map<String, List<GLAccountData>> accountOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountMappingOptionsForLoanProducts();
        final List<EnumOptionData> accountingRuleTypeOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountingRuleTypeOptions();
        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();

        if (investmentProductData != null) {
            investmentProductData = InvestmentProductData.withTemplateDetails(investmentProductData, currencyOptions,
                    interestRateFrequencyTypeOptions, investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions,
                    chargeOptions, accountingRuleTypeOptions, accountOptions, paymentTypeOptions);
        } else {
            investmentProductData = InvestmentProductData.onlyTemplateDetails(currencyOptions, interestRateFrequencyTypeOptions,
                    investmentCompoundingPeriodTypeOptions, investmentTermFrequencyTypeOptions, chargeOptions, accountingRuleTypeOptions,
                    accountOptions, paymentTypeOptions);
        }
        return investmentProductData;
    }

    @Override
    public Collection<InvestmentProductData> retrieveAll() {
        InvestmentProductMapper investmentProductMapper = new InvestmentProductMapper();
        String sql = "SELECT " + investmentProductMapper.schema();
        Collection<InvestmentProductData> investmentProductDetails = this.jdbcTemplate.query(sql, investmentProductMapper);
        return investmentProductDetails;
    }

    @Override
    public InvestmentProductData retrieveOne(Long investmentProductId) {
        try {
            InvestmentProductMapper investmentProductMapper = new InvestmentProductMapper();
            String sql = "SELECT " + investmentProductMapper.schema() + " where fip.id = ?";
            InvestmentProductData investmentProduct = this.jdbcTemplate.queryForObject(sql, investmentProductMapper, investmentProductId);
            return investmentProduct;
        } catch (final EmptyResultDataAccessException e) {
            throw new InvestmentProductNotFoundException(investmentProductId);
        }

    }
    
    @Override
    public Collection<InvestmentProductData> retrieveAllLookUpData() {
        InvestmentProductLookUpMapper investmentProductMapper = new InvestmentProductLookUpMapper();
        String sql = "SELECT " + investmentProductMapper.schema() + " order by fip.id";
        Collection<InvestmentProductData> investmentProductDetails = this.jdbcTemplate.query(sql, investmentProductMapper);
        return investmentProductDetails;
    }

    private static final class InvestmentProductMapper implements RowMapper<InvestmentProductData> {

        private final String schemaSql;

        public InvestmentProductMapper() {
            StringBuilder sqlBuilder = new StringBuilder(300);
            sqlBuilder.append(" fip.id as id, fip.name as name, fip.short_name as shortName, fip.description as description,");
            sqlBuilder
                    .append(" fip.currency_code as currencyCode, fip.currency_digits as currencyDigits, fip.currency_multiplesof as inMultiplesOf,");
            sqlBuilder.append(" curr.name as currencyName, curr.internationalized_name_code as currencyNameCode,");
            sqlBuilder.append(" curr.display_symbol as currencyDisplaySymbol,");
            sqlBuilder.append(" fip.min_nominal_interest_rate, fip.default_nominal_interest_rate,fip.max_nominal_interest_rate,");
            sqlBuilder.append(" fip.nominal_interest_rate_type, fip.interest_compounding_period_type, fip.min_investment_term_period,");
            sqlBuilder.append(" fip.default_investment_term_period, fip.max_investment_term_period, fip.investment_term_type,");
            sqlBuilder.append(" fip.override_terms_in_investment_accounts, fip.nominal_interset_rate,");
            sqlBuilder.append(" fip.interest_compounding_period, fip.investment_term,");
            sqlBuilder.append(" fip.accounting_type");
            sqlBuilder.append(" from f_investment_product fip");
            sqlBuilder.append(" join m_currency curr on curr.code = fip.currency_code");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public InvestmentProductData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String shortName = rs.getString("shortName");
            final String description = rs.getString("description");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final BigDecimal minNominalInterestRate = rs.getBigDecimal("min_nominal_interest_rate");
            final BigDecimal defaultNominalInterestRate = rs.getBigDecimal("default_nominal_interest_rate");
            final BigDecimal maxNominalInterestRate = rs.getBigDecimal("max_nominal_interest_rate");
            final Integer interestRateType = JdbcSupport.getInteger(rs, "nominal_interest_rate_type");
            final EnumOptionData interestRateTypeEnum = InvestmentTrackerEnumerations.interestRateFrequencyType(interestRateType);

            final Integer compoundingPeriodType = JdbcSupport.getInteger(rs, "interest_compounding_period_type");
            final EnumOptionData compoundingPeriodTypeEnum = InvestmentTrackerEnumerations
                    .compoundingInterestPeriodType(compoundingPeriodType);

            final Integer minInvesmentTermPeriod = JdbcSupport.getInteger(rs, "min_investment_term_period");
            final Integer defaultInvesmentTermPeriod = JdbcSupport.getInteger(rs, "default_investment_term_period");
            final Integer maxInvesmentTermPeriod = JdbcSupport.getInteger(rs, "max_investment_term_period");
            final Integer invesmentTermPeriodType = JdbcSupport.getInteger(rs, "investment_term_type");
            final EnumOptionData invesmentTermPeriodEnum = InvestmentTrackerEnumerations
                    .investmentTermFrequencyType(invesmentTermPeriodType);

            final boolean overrideTermsInvesmentAccounts = rs.getBoolean("override_terms_in_investment_accounts");
            final boolean nominalIntersetRate = rs.getBoolean("nominal_interset_rate");
            final boolean interestCompoundingPeriod = rs.getBoolean("interest_compounding_period");
            final boolean invesmentTerm = rs.getBoolean("investment_term");

            final Integer accountingType = JdbcSupport.getInteger(rs, "accounting_type");
            final EnumOptionData accountingRuleType = AccountingEnumerations.accountingRuleType(accountingType);

            return InvestmentProductData.investmentProductDetails(id, name, shortName, description, currency, minNominalInterestRate,
                    defaultNominalInterestRate, maxNominalInterestRate, interestRateTypeEnum, compoundingPeriodTypeEnum,
                    minInvesmentTermPeriod, defaultInvesmentTermPeriod, maxInvesmentTermPeriod, invesmentTermPeriodEnum,
                    overrideTermsInvesmentAccounts, nominalIntersetRate, interestCompoundingPeriod, invesmentTerm, accountingRuleType);
        }
    }
    
    private static final class InvestmentProductLookUpMapper implements RowMapper<InvestmentProductData> {

        private final String schemaSql;

        public InvestmentProductLookUpMapper() {
            StringBuilder sqlBuilder = new StringBuilder(300);
            sqlBuilder.append(" fip.id as id, fip.name as name");
            sqlBuilder.append(" from f_investment_product fip");
            sqlBuilder.append(" join m_currency curr on curr.code = fip.currency_code");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @SuppressWarnings("unused")
        @Override
        public InvestmentProductData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            return InvestmentProductData.lookup(id, name);
        }
    }

}
