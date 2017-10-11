package com.finflux.portfolio.cashflow.service;

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
import com.finflux.portfolio.cashflow.data.CashFlowCategoryTemplateData;
import com.finflux.portfolio.cashflow.data.CashFlowCategoryTypeEnums;
import com.finflux.portfolio.cashflow.data.CashFlowTypeEnums;
import com.finflux.portfolio.cashflow.data.IncomeExpenseData;
import com.finflux.portfolio.cashflow.exception.CashFlowCategoryNotFoundException;

@Service
public class CashFlowCategoryReadPlatformServiceImpl implements CashFlowCategoryReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final CashFlowCategoryDataExtractor cashFlowCategoryDataExtractor = new CashFlowCategoryDataExtractor();

    @Autowired
    public CashFlowCategoryReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CashFlowCategoryTemplateData retrieveCashFlowTemplate() {
        final Collection<EnumOptionData> cashFlowCategoryTypeOptions = CashFlowCategoryTypeEnums.cashFlowCategoryTypeOptions();
        final Collection<EnumOptionData> cashFlowTypeOptions = CashFlowTypeEnums.cashFlowTypeOptions();
        return CashFlowCategoryTemplateData.template(cashFlowCategoryTypeOptions, cashFlowTypeOptions);
    }

    @Override
    public Collection<CashFlowCategoryData> retrieveAll(final Integer categoryEnumId, final Integer typeEnumId, final Boolean isActive,
            final Boolean isFetchIncomeExpenseDatas) {
        this.context.authenticatedUser();
        int active = 0;
        if (isActive != null && isActive) {
            active = 1;
        }
        String sql = "SELECT " + this.cashFlowCategoryDataExtractor.schema(isFetchIncomeExpenseDatas) + " ";
        Boolean isWhereClauseUsed = false;
        if (categoryEnumId != null) {
            sql += "WHERE cfc.category_enum_id = " + categoryEnumId + " ";
            isWhereClauseUsed = true;
        }
        if (typeEnumId != null) {
            if (isWhereClauseUsed) {
                sql += "AND cfc.type_enum_id = " + typeEnumId + " ";
            } else {
                sql += "WHERE cfc.type_enum_id = " + typeEnumId + " ";
            }
            isWhereClauseUsed = true;
        }
        if (isActive != null) {
            if (isWhereClauseUsed) {
                sql += "AND cfc.is_active = " + active + " ";
            } else {
                sql += "WHERE cfc.is_active = " + active + " ";
            }
        }
        sql +="ORDER BY cfc.name ";
        return this.jdbcTemplate.query(sql, this.cashFlowCategoryDataExtractor, new Object[] {});
    }

    @Override
    public CashFlowCategoryData retrieveOne(final Long cashFlowCategoryId, final Boolean isFetchIncomeExpenseDatas) {
        this.context.authenticatedUser();
        final String sql = "SELECT " + this.cashFlowCategoryDataExtractor.schema(isFetchIncomeExpenseDatas) + " WHERE cfc.id = ? ";
        final Collection<CashFlowCategoryData> cashFlowCategoryDatas = this.jdbcTemplate.query(sql, this.cashFlowCategoryDataExtractor,
                new Object[] { cashFlowCategoryId });
        if (cashFlowCategoryDatas == null || cashFlowCategoryDatas.isEmpty()) { throw new CashFlowCategoryNotFoundException(
                cashFlowCategoryId); }
        return cashFlowCategoryDatas.iterator().next();
    }

    static final class CashFlowCategoryDataMapper implements RowMapper<CashFlowCategoryData> {

        public String schema(final Boolean isFetchIncomeExpenseDatas) {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("cfc.id AS cfcId, cfc.name AS cfcName, cfc.short_name AS cfcShortName,cfc.description AS cfcDescription ")
                    .append(",cfc.category_enum_id AS cfcCategoryEnumId,cfc.type_enum_id AS cfcTypeEnumId, cfc.is_active AS cfcIsActive ");
            if (isFetchIncomeExpenseDatas) {
                sqlBuilder
                        .append(",ie.id AS ieId, ie.cashflow_category_id AS ieCashflowCategoryId, ie.name AS ieName,ie.description AS ieDescription,ie.is_quantifier_needed AS ieIsQuantifierNeeded ")
                        .append(",ie.quantifier_label AS quantifierLabel, ie.is_capture_month_wise_income AS ieIsCaptureMonthWiseIncome ")
                        .append(",ie.stability_enum_id AS ieStabilityEnumId,ie.default_income AS ieDefaultIncome,ie.default_expense AS ieDefaultExpense,ie.is_active AS ieIsActive ");
            }
            sqlBuilder.append("FROM f_cashflow_category cfc ");
            if (isFetchIncomeExpenseDatas) {
                sqlBuilder.append("LEFT JOIN f_income_expense ie ON ie.cashflow_category_id = cfc.id ");
            }
            return sqlBuilder.toString();
        }

        @Override
        public CashFlowCategoryData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "cfcId");
            if (id == null) { return null; }
            final String name = rs.getString("cfcName");
            final String shortName = rs.getString("cfcShortName");
            final String description = rs.getString("cfcDescription");
            final Integer categoryEnumId = JdbcSupport.getInteger(rs, "cfcCategoryEnumId");
            final EnumOptionData categoryEnum = CashFlowCategoryTypeEnums.cashFlowCategoryType(categoryEnumId);
            final Integer typeEnumId = JdbcSupport.getInteger(rs, "cfcTypeEnumId");
            final EnumOptionData typeEnum = CashFlowTypeEnums.cashFlowType(typeEnumId);
            final Boolean isActive = rs.getBoolean("cfcIsActive");
            return CashFlowCategoryData.instance(id, name, shortName, description, categoryEnum, typeEnum, isActive);
        }
    }

    private static final class CashFlowCategoryDataExtractor implements ResultSetExtractor<Collection<CashFlowCategoryData>> {

        final CashFlowCategoryDataMapper cashFlowCategoryDataMapper = new CashFlowCategoryDataMapper();
        final IncomeExpenseReadPlatformServiceImpl.IncomeExpenseDataMapper incomeExpenseDataMapper = new IncomeExpenseReadPlatformServiceImpl.IncomeExpenseDataMapper();
        private Boolean isFetchIncomeExpenseDatas = false;

        public String schema(final Boolean isFetchIncomeExpenseDatas) {
            this.isFetchIncomeExpenseDatas = isFetchIncomeExpenseDatas;
            if (this.isFetchIncomeExpenseDatas == null) {
                this.isFetchIncomeExpenseDatas = false;
            }
            return this.cashFlowCategoryDataMapper.schema(this.isFetchIncomeExpenseDatas);
        }

        @Override
        public Collection<CashFlowCategoryData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            final List<CashFlowCategoryData> cashFlowCategoryDataList = new ArrayList<>();
            CashFlowCategoryData cashFlowCategoryData = null;
            Long cashFlowCategoryId = null;
            int cfcIndex = 0;// Cash Flow Category index
            int ieIndex = 0;// Income Expense index
            while (rs.next()) {
                final Long tempCfcId = rs.getLong("cfcId");
                if (cashFlowCategoryData == null || (cashFlowCategoryId != null && !cashFlowCategoryId.equals(tempCfcId))) {
                    cashFlowCategoryId = tempCfcId;
                    cashFlowCategoryData = this.cashFlowCategoryDataMapper.mapRow(rs, cfcIndex++);
                    cashFlowCategoryDataList.add(cashFlowCategoryData);
                }
                if (this.isFetchIncomeExpenseDatas && cashFlowCategoryData != null) {
                    final IncomeExpenseData IncomeExpenseData = this.incomeExpenseDataMapper.mapRow(rs, ieIndex++);
                    if (IncomeExpenseData != null) {
                        cashFlowCategoryData.addIncomeExpenseData(IncomeExpenseData);
                    }
                }
            }
            return cashFlowCategoryDataList;
        }
    }
}