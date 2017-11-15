/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.accounting.financialactivityaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.financialactivityaccount.data.FinancialActivityAccountData;
import org.apache.fineract.accounting.financialactivityaccount.data.FinancialActivityAccountPaymentTypeMappingData;
import org.apache.fineract.accounting.financialactivityaccount.data.FinancialActivityData;
import org.apache.fineract.accounting.financialactivityaccount.exception.FinancialActivityAccountNotFoundException;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FinancialActivityAccountReadPlatformServiceImpl implements FinancialActivityAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final FinancialActivityAccountMapper financialActivityAccountMapper;
    private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
    private final FinancialActivityAccountPaymentTypeMappingReadPlatformService financialActivityAccountPaymentTypeMappingReadPlatformService;

    @Autowired
    public FinancialActivityAccountReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService,
            final FinancialActivityAccountPaymentTypeMappingReadPlatformService financialActivityAccountPaymentTypeMappingReadPlatformService) {
        this.financialActivityAccountMapper = new FinancialActivityAccountMapper();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountingDropdownReadPlatformService = accountingDropdownReadPlatformService;
        this.financialActivityAccountPaymentTypeMappingReadPlatformService = financialActivityAccountPaymentTypeMappingReadPlatformService;
    }

    @Override
    public List<FinancialActivityAccountData> retrieveAll() {
        final String sql = "select " + this.financialActivityAccountMapper.schema();
        return this.jdbcTemplate.query(sql, this.financialActivityAccountMapper, new Object[] {});
    }

    @Override
    public FinancialActivityAccountData retrieve(final Long financialActivityAccountId) {
        try {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("select ");
            sqlBuilder.append(this.financialActivityAccountMapper.schema());
            sqlBuilder.append(" where faa.id=?");

            final FinancialActivityAccountData financialActivityAccountData = this.jdbcTemplate.queryForObject(sqlBuilder.toString(),
                    this.financialActivityAccountMapper, new Object[] { financialActivityAccountId });
            final List<FinancialActivityAccountPaymentTypeMappingData> financialActivityAccountPaymentTypeMappingData = this.financialActivityAccountPaymentTypeMappingReadPlatformService
                    .retrieve(financialActivityAccountId);
            if (financialActivityAccountPaymentTypeMappingData.size() > 0) { return new FinancialActivityAccountData(
                    financialActivityAccountData, financialActivityAccountPaymentTypeMappingData); }
            return financialActivityAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new FinancialActivityAccountNotFoundException(financialActivityAccountId);
        }
    }

    @Override
    public FinancialActivityAccountData addTemplateDetails(final FinancialActivityAccountData financialActivityAccountData) {
        final Map<String, List<GLAccountData>> accountOptions = this.accountingDropdownReadPlatformService.retrieveAccountMappingOptions();
        financialActivityAccountData.setAccountingMappingOptions(accountOptions);
        financialActivityAccountData.setFinancialActivityOptions(FINANCIAL_ACTIVITY.getAllFinancialActivities());
        return financialActivityAccountData;
    }

    @Override
    public FinancialActivityAccountData getFinancialActivityAccountTemplate() {
        final FinancialActivityAccountData financialActivityAccountData = new FinancialActivityAccountData();
        return addTemplateDetails(financialActivityAccountData);
    }

    private static final class FinancialActivityAccountMapper implements RowMapper<FinancialActivityAccountData> {

        private final String sql;

        public FinancialActivityAccountMapper() {
            final StringBuilder sb = new StringBuilder(300);
            sb.append(
                    " faa.id as id, faa.financial_activity_type as financialActivityId, glaccount.id as glAccountId,glaccount.name as glAccountName,glaccount.gl_code as glCode  ");
            sb.append(" from acc_gl_financial_activity_account faa ");
            sb.append(" join acc_gl_account glaccount on glaccount.id = faa.gl_account_id");
            this.sql = sb.toString();
        }

        public String schema() {
            return this.sql;
        }

        @Override
        public FinancialActivityAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long glAccountId = JdbcSupport.getLong(rs, "glAccountId");
            final Integer financialActivityId = JdbcSupport.getInteger(rs, "financialActivityId");
            final String glAccountName = rs.getString("glAccountName");
            final String glCode = rs.getString("glCode");

            final GLAccountData glAccountData = new GLAccountData(glAccountId, glAccountName, glCode);
            final FinancialActivityData financialActivityData = FINANCIAL_ACTIVITY.toFinancialActivityData(financialActivityId);
            final FinancialActivityAccountData financialActivityAccountData = new FinancialActivityAccountData(id, financialActivityData,
                    glAccountData, null);
            return financialActivityAccountData;
        }
    }

}