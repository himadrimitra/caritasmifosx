package com.finflux.portfolio.client.cashflow.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.data.IncomeExpenseData;
import com.finflux.portfolio.cashflow.service.IncomeExpenseReadPlatformService;
import com.finflux.portfolio.client.cashflow.data.ClientIncomeExpenseData;
import com.finflux.portfolio.client.cashflow.data.ClientMonthWiseIncomeExpenseData;
import com.finflux.portfolio.client.cashflow.exception.ClientOrFamilyMemberIncomeExpenseNotFoundException;

@Service
public class ClientIncomeExpenseReadPlatformServiceImpl implements ClientIncomeExpenseReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService;

    @Autowired
    public ClientIncomeExpenseReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.incomeExpenseReadPlatformService = incomeExpenseReadPlatformService;
    }

    @Override
    public Collection<ClientIncomeExpenseData> retrieveAll(final Long clientId, final Boolean isFetchFamilyDeatilsIncomeAndExpense,
            final Boolean isActive) {
        final ClientIncomeExpenseDataExtractor clientIncomeExpenseDataExtractor = new ClientIncomeExpenseDataExtractor(
                this.incomeExpenseReadPlatformService);
        this.context.authenticatedUser();
        int active = 0;
        if (isActive != null && isActive) {
            active = 1;
        }
        String sql = "SELECT " + clientIncomeExpenseDataExtractor.schema() + "WHERE cie.client_id = ?  ";
        if (isFetchFamilyDeatilsIncomeAndExpense == null || isFetchFamilyDeatilsIncomeAndExpense == false) {
            sql += "AND cie.family_details_id IS NULL ";
        }
        if (isActive != null) {
            sql += "AND cie.is_active = " + active + " ";
        }
        return this.jdbcTemplate.query(sql, clientIncomeExpenseDataExtractor, new Object[] { clientId });
    }

    @Override
    public ClientIncomeExpenseData retrieveOne(final Long clientIncomeExpenseId) {
        this.context.authenticatedUser();
        final ClientIncomeExpenseDataExtractor clientIncomeExpenseDataExtractor = new ClientIncomeExpenseDataExtractor(
                this.incomeExpenseReadPlatformService);
        final String sql = "SELECT " + clientIncomeExpenseDataExtractor.schema() + "WHERE cie.id = ?  ";
        final Collection<ClientIncomeExpenseData> clientOrFamilyMemberIncomeExpenseData = this.jdbcTemplate.query(sql,
                clientIncomeExpenseDataExtractor, new Object[] { clientIncomeExpenseId });
        if (clientOrFamilyMemberIncomeExpenseData == null || clientOrFamilyMemberIncomeExpenseData.isEmpty()) { throw new ClientOrFamilyMemberIncomeExpenseNotFoundException(
                clientIncomeExpenseId); }
        return clientOrFamilyMemberIncomeExpenseData.iterator().next();
    }

    private static final class ClientIncomeExpenseDataMapper implements RowMapper<ClientIncomeExpenseData> {

        final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService;

        public ClientIncomeExpenseDataMapper(final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService) {
            this.incomeExpenseReadPlatformService = incomeExpenseReadPlatformService;
        }

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("cie.id AS cieId, cie.client_id AS clientId ");
            sqlBuilder.append(",cie.family_details_id AS familyDetailId,cie.income_expense_id AS incomeExpenseId ");
            sqlBuilder.append(",cie.quintity AS quintity ");
            sqlBuilder.append(",cie.default_income AS defaultIncome,cie.default_expense AS defaultExpense ");
            sqlBuilder.append(",cie.total_income AS totalIncome,cie.total_expense AS totalExpense ");
            sqlBuilder
                    .append(",cie.is_month_wise_income AS isMonthWiseIncome,cie.is_primary_income AS isPrimaryIncome,cie.is_active AS cieIsActive,cie.is_remmitance_income AS isRemmitanceIncome ");
            sqlBuilder.append(",cmie.id AS cmieId,cmie.month AS month,cmie.year AS year ");
            sqlBuilder.append(",cmie.income_amount AS incomeAmount, cmie.expense_amount AS expenseAmount, cmie.is_active AS cmieIsActive ");
            sqlBuilder.append("FROM f_client_income_expense cie ");
            sqlBuilder.append("LEFT JOIN f_client_month_wise_income_expense cmie ON cmie.client_income_expense_id = cie.id ");
            return sqlBuilder.toString();
        }

        @Override
        public ClientIncomeExpenseData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "cieId");
            if (id == null) { return null; }
            final Long clientId = JdbcSupport.getLongDefaultToNullIfZero(rs, "clientId");
            final Long familyDetailId = JdbcSupport.getLongDefaultToNullIfZero(rs, "familyDetailId");
            final Long incomeExpenseId = JdbcSupport.getLongDefaultToNullIfZero(rs, "incomeExpenseId");
            IncomeExpenseData incomeExpenseData = null;
            if (incomeExpenseId != null && incomeExpenseId > 0) {
                incomeExpenseData = this.incomeExpenseReadPlatformService.retrieveOne(incomeExpenseId, true);
            }
            final BigDecimal quintity = rs.getBigDecimal("quintity");
            final BigDecimal defaultIncome = rs.getBigDecimal("defaultIncome");
            final BigDecimal defaultExpense = rs.getBigDecimal("defaultExpense");
            final BigDecimal totalIncome = rs.getBigDecimal("totalIncome");
            final BigDecimal totalExpense = rs.getBigDecimal("totalExpense");
            final Boolean isMonthWiseIncome = rs.getBoolean("isMonthWiseIncome");
            final Boolean isPrimaryIncome = rs.getBoolean("isPrimaryIncome");
            final Boolean isActive = rs.getBoolean("cieIsActive");
            final Boolean isRemmitanceIncome=rs.getBoolean("isRemmitanceIncome");
            return ClientIncomeExpenseData.instance(id, clientId, familyDetailId, incomeExpenseData, quintity, defaultIncome,
                    defaultExpense, totalIncome, totalExpense, isMonthWiseIncome, isPrimaryIncome, isActive,isRemmitanceIncome);
        }
    }

    private static final class ClientIncomeExpenseDataExtractor implements ResultSetExtractor<Collection<ClientIncomeExpenseData>> {

        final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService;
        final ClientIncomeExpenseDataMapper clientIncomeExpenseDataMapper;
        final ClientMonthWiseIncomeExpenseDataMapper clientMonthWiseIncomeExpenseDataMapper;

        public ClientIncomeExpenseDataExtractor(final IncomeExpenseReadPlatformService incomeExpenseReadPlatformService) {
            this.incomeExpenseReadPlatformService = incomeExpenseReadPlatformService;
            this.clientIncomeExpenseDataMapper = new ClientIncomeExpenseDataMapper(this.incomeExpenseReadPlatformService);
            this.clientMonthWiseIncomeExpenseDataMapper = new ClientMonthWiseIncomeExpenseDataMapper();
        }

        public String schema() {
            return this.clientIncomeExpenseDataMapper.schema();
        }

        @Override
        public Collection<ClientIncomeExpenseData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<ClientIncomeExpenseData> clientIncomeExpenseDataList = new ArrayList<>();
            ClientIncomeExpenseData clientIncomeExpenseData = null;
            Long clientIncomeExpenseDataId = null;
            int cieIndex = 0;// Client Income Expense Data
            int cmieIndex = 0;// Client Month Wise Income Expense Data
            while (rs.next()) {
                final Long tempCieId = rs.getLong("cieId");
                if (clientIncomeExpenseData == null || (clientIncomeExpenseDataId != null && !clientIncomeExpenseDataId.equals(tempCieId))) {
                    clientIncomeExpenseDataId = tempCieId;
                    clientIncomeExpenseData = this.clientIncomeExpenseDataMapper.mapRow(rs, cieIndex++);
                    clientIncomeExpenseDataList.add(clientIncomeExpenseData);
                }
                if (clientIncomeExpenseData != null) {
                    final ClientMonthWiseIncomeExpenseData clientMonthWiseIncomeExpenseData = this.clientMonthWiseIncomeExpenseDataMapper
                            .mapRow(rs, cmieIndex++);
                    if (clientMonthWiseIncomeExpenseData != null) {
                        clientIncomeExpenseData.addClientMonthWiseIncomeExpenseData(clientMonthWiseIncomeExpenseData);
                    }
                }
            }
            return clientIncomeExpenseDataList;
        }
    }

    private static final class ClientMonthWiseIncomeExpenseDataMapper implements RowMapper<ClientMonthWiseIncomeExpenseData> {

        @Override
        public ClientMonthWiseIncomeExpenseData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "cmieId");
            if (id == null) { return null; }
            final Integer month = rs.getInt("month");
            final Integer year = rs.getInt("year");
            final BigDecimal incomeAmount = rs.getBigDecimal("incomeAmount");
            final BigDecimal expenseAmount = rs.getBigDecimal("expenseAmount");
            final Boolean isActive = rs.getBoolean("cmieIsActive");
            return ClientMonthWiseIncomeExpenseData.instance(id, month, year, incomeAmount, expenseAmount, isActive);
        }
    }
}