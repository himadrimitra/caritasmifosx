package com.finflux.portfolio.cashflow.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.data.CashFlowCategoryData;
import com.finflux.portfolio.cashflow.data.IncomeExpenseData;
import com.finflux.portfolio.cashflow.data.IncomeExpenseTemplateData;
import com.finflux.portfolio.cashflow.data.StabilityTypeEnums;
import com.finflux.portfolio.cashflow.exception.IncomeExpenseNotFoundException;

@Service
public class IncomeExpenseReadPlatformServiceImpl implements IncomeExpenseReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final IncomeExpenseDataExtractor incomeExpenseDataExtractor = new IncomeExpenseDataExtractor();
    private final CashFlowCategoryReadPlatformService cashFlowCategoryReadPlatformService;

    @Autowired
    public IncomeExpenseReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CashFlowCategoryReadPlatformService cashFlowCategoryReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.cashFlowCategoryReadPlatformService = cashFlowCategoryReadPlatformService;
    }

    @Override
    public IncomeExpenseTemplateData retrieveIncomeExpenseTemplate(final Boolean isActive) {
        final Collection<EnumOptionData> stabilityEnumOptions = StabilityTypeEnums.stabilityEnumOptions();
        final Integer categoryEnumId = null;
        final Integer typeEnumId = null;
        final Boolean isFetchIncomeExpenseDatas = null;
        final Collection<CashFlowCategoryData> cashFlowCategoryOptions = this.cashFlowCategoryReadPlatformService.retrieveAll(
                categoryEnumId, typeEnumId, isActive, isFetchIncomeExpenseDatas);
        return IncomeExpenseTemplateData.template(stabilityEnumOptions, cashFlowCategoryOptions);
    }

    @Override
    public Collection<IncomeExpenseData> retrieveAll(final Long cashFlowCategoryId, final Boolean isActive,
            final Boolean isFetchCashflowCategoryData) {
        this.context.authenticatedUser();
        int active = 0;
        if (isActive != null && isActive) {
            active = 1;
        }
        String sql = "SELECT " + this.incomeExpenseDataExtractor.schema(isFetchCashflowCategoryData) + " ";
        Boolean isWhereClauseUsed = false;
        if (cashFlowCategoryId != null) {
            sql += "WHERE ie.cashflow_category_id = " + cashFlowCategoryId + " ";
            isWhereClauseUsed = true;
        }
        if (isActive != null) {
            if (isWhereClauseUsed) {
                sql += "AND ie.is_active = " + active + " ";
            } else {
                sql += "WHERE ie.is_active = " + active + " ";
            }
        }
        sql +="ORDER BY ie.name ";
        return this.jdbcTemplate.query(sql, this.incomeExpenseDataExtractor, new Object[] {});
    }

    @Override
    public IncomeExpenseData retrieveOne(final Long incomeExpenseId, final Boolean isFetchCashflowCategoryData) {
        this.context.authenticatedUser();
        final String sql = "SELECT " + this.incomeExpenseDataExtractor.schema(isFetchCashflowCategoryData) + " WHERE ie.id = ? ";
        final Collection<IncomeExpenseData> incomeExpenseDatas = this.jdbcTemplate.query(sql, this.incomeExpenseDataExtractor,
                new Object[] { incomeExpenseId });
        if (incomeExpenseDatas == null || incomeExpenseDatas.isEmpty()) { throw new IncomeExpenseNotFoundException(incomeExpenseId); }
        return incomeExpenseDatas.iterator().next();
    }

    static final class IncomeExpenseDataMapper implements RowMapper<IncomeExpenseData> {

        public String schema(final Boolean isFetchCashflowCategoryData) {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder
                    .append("ie.id AS ieId, ie.cashflow_category_id AS ieCashflowCategoryId,ie.name AS ieName,ie.description AS ieDescription,ie.is_quantifier_needed AS ieIsQuantifierNeeded ")
                    .append(",ie.quantifier_label AS quantifierLabel, ie.is_capture_month_wise_income AS ieIsCaptureMonthWiseIncome ")
                    .append(",ie.stability_enum_id AS ieStabilityEnumId,ie.default_income AS ieDefaultIncome,ie.default_expense AS ieDefaultExpense, ie.is_active AS ieIsActive ");
            if (isFetchCashflowCategoryData) {
                sqlBuilder.append(
                        ",cfc.id AS cfcId, cfc.name AS cfcName, cfc.short_name AS cfcShortName,cfc.description AS cfcDescription ").append(
                        ",cfc.category_enum_id AS cfcCategoryEnumId,cfc.type_enum_id AS cfcTypeEnumId, cfc.is_active AS cfcIsActive ");
            }
            sqlBuilder.append("FROM f_income_expense ie ");
            if (isFetchCashflowCategoryData) {
                sqlBuilder.append("LEFT JOIN f_cashflow_category cfc ON cfc.id = ie.cashflow_category_id ");
            }
            return sqlBuilder.toString();
        }

        @SuppressWarnings({ "null", "unused" })
        @Override
        public IncomeExpenseData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "ieId");
            if (id == null) { return null; }
            final Long cashflowCategoryId = JdbcSupport.getLongDefaultToNullIfZero(rs, "ieCashflowCategoryId");
            final String name = rs.getString("ieName");
            final String description = rs.getString("ieDescription");
            final Boolean isQuantifierNeeded = rs.getBoolean("ieIsQuantifierNeeded");
            final String quantifierLabel = rs.getString("quantifierLabel");
            final Boolean isCaptureMonthWiseIncome = rs.getBoolean("ieIsCaptureMonthWiseIncome");
            Integer stabilityEnumId = JdbcSupport.getInteger(rs, "ieStabilityEnumId");
            EnumOptionData stabilityEnum = null;
            if(stabilityEnumId != null){
                stabilityEnum = StabilityTypeEnums.stabilityType(stabilityEnumId);
            }
            final BigDecimal defaultIncome = rs.getBigDecimal("ieDefaultIncome");
            final BigDecimal defaultExpense = rs.getBigDecimal("ieDefaultExpense");
            final Boolean isActive = rs.getBoolean("ieIsActive");
            return IncomeExpenseData.instance(id, cashflowCategoryId, name, description, isQuantifierNeeded, quantifierLabel,
                    isCaptureMonthWiseIncome, stabilityEnum, defaultIncome, defaultExpense, isActive);
        }
    }

    private static final class IncomeExpenseDataExtractor implements ResultSetExtractor<Collection<IncomeExpenseData>> {

        final IncomeExpenseDataMapper incomeExpenseDataMapper = new IncomeExpenseDataMapper();
        final CashFlowCategoryReadPlatformServiceImpl.CashFlowCategoryDataMapper cashFlowCategoryDataMapper = new CashFlowCategoryReadPlatformServiceImpl.CashFlowCategoryDataMapper();
        private Boolean isFetchCashflowCategoryData = false;

        public String schema(final Boolean isFetchCashflowCategoryData) {
            this.isFetchCashflowCategoryData = isFetchCashflowCategoryData;
            if (this.isFetchCashflowCategoryData == null) {
                this.isFetchCashflowCategoryData = false;
            }
            return this.incomeExpenseDataMapper.schema(this.isFetchCashflowCategoryData);
        }

        @Override
        public Collection<IncomeExpenseData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            final List<IncomeExpenseData> incomeExpenseDataList = new ArrayList<>();
            IncomeExpenseData incomeExpenseData = null;
            Long incomeExpenseId = null;
            int ieIndex = 0;// Income Expense index
            int cfcIndex = 0;// Cash Flow Category index
            while (rs.next()) {
                final Long tempIeId = rs.getLong("ieId");
                if (incomeExpenseData == null || (incomeExpenseId != null && !incomeExpenseId.equals(tempIeId))) {
                    incomeExpenseId = tempIeId;
                    incomeExpenseData = this.incomeExpenseDataMapper.mapRow(rs, ieIndex++);
                    incomeExpenseDataList.add(incomeExpenseData);
                }
                if (this.isFetchCashflowCategoryData && incomeExpenseData != null) {
                    final CashFlowCategoryData cashFlowCategoryData = this.cashFlowCategoryDataMapper.mapRow(rs, cfcIndex++);
                    if (cashFlowCategoryData != null) {
                        incomeExpenseData.addCashFlowCategoryData(cashFlowCategoryData);
                    }
                }
            }
            return incomeExpenseDataList;
        }
    }
}
