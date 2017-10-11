/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.accounting.financialactivityaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.accounting.financialactivityaccount.data.FinancialActivityAccountPaymentTypeMappingData;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FinancialActivityAccountPaymentTypeMappingReadPlatformServiceImpl implements FinancialActivityAccountPaymentTypeMappingReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final FinancialActivityAccountPaymentTypeMapper financialActivityAccountMapper;

    @Autowired
    public FinancialActivityAccountPaymentTypeMappingReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        financialActivityAccountMapper = new FinancialActivityAccountPaymentTypeMapper();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    

    @Override
    public List<FinancialActivityAccountPaymentTypeMappingData> retrieve(Long financialActivityAccountId) {
            StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("select ");
            sqlBuilder.append(this.financialActivityAccountMapper.schema());
            sqlBuilder.append(" where faam.financial_activity_account_id =?");
            return this.jdbcTemplate.query(sqlBuilder.toString(), this.financialActivityAccountMapper, new Object[] { financialActivityAccountId});    }


    private static final class FinancialActivityAccountPaymentTypeMapper implements RowMapper<FinancialActivityAccountPaymentTypeMappingData> {

        private final String sql;

        public FinancialActivityAccountPaymentTypeMapper() {
            StringBuilder sb = new StringBuilder(300);
            sb.append(" faam.id as id, gla.id as glAccountId, gla.gl_code as glCode,gla.name as glAccountName, pt.id as paymentTypeId, pt.value as paymentTypeName ");
            sb.append(" from f_financial_activity_account_payment_type_mapping faam  ");
            sb.append(" join m_payment_type pt on pt.id = faam.payment_type_id ");
            sb.append(" join acc_gl_account gla on gla.id = faam.gl_account_id ");
            sql = sb.toString();
        }

        public String schema() {
            return sql;
        }

        @Override
        public FinancialActivityAccountPaymentTypeMappingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long glAccountId = JdbcSupport.getLong(rs, "glAccountId");
            final String glAccountName = rs.getString("glAccountName");
            final String glCode = rs.getString("glCode");
            final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentTypeId");
            final String paymentTypeName = rs.getString("paymentTypeName");
            
            final GLAccountData glAccountData = new GLAccountData(glAccountId, glAccountName, glCode);
            final PaymentTypeData paymentTypeData = PaymentTypeData.instance(paymentTypeId, paymentTypeName);
            
            final FinancialActivityAccountPaymentTypeMappingData financialActivityAccountPaymentTypeMappingData = new FinancialActivityAccountPaymentTypeMappingData(
                    id, glAccountData, paymentTypeData);
            
            return financialActivityAccountPaymentTypeMappingData;
        }
    }

}
