/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.charge.data.ChargeSlabData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChargeSlabReadPlatformServiceImpl implements ChargeSlabReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public ChargeSlabReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public static final class ChargeSlabMapper implements RowMapper<ChargeSlabData> {

        public String chargeSlabSchema() {
            return "cs.id as csid, cs.from_loan_amount as fromLoanAmount, cs.to_loan_amount as toLoanAmount, cs.amount as chargeAmount "
                    + " from f_charge_slab cs "
                    + " LEFT JOIN m_charge c on cs.charge_id = c.id ";
        }


        @Override
        public ChargeSlabData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("csid");
            final BigDecimal fromLoanAmount = rs.getBigDecimal("fromLoanAmount");
            final BigDecimal toLoanAmount = rs.getBigDecimal("toLoanAmount");
            final BigDecimal amount = rs.getBigDecimal("chargeAmount");

            return ChargeSlabData.createChargeSlabData(id, fromLoanAmount, toLoanAmount, amount);
        }
    }
    
    
    @Override
    public Collection<ChargeSlabData> retrieveAllChargeSlabsByChargeId(final Long chargeId) {
        final ChargeSlabMapper csm = new ChargeSlabMapper();
        
        String sql = "select " + csm.chargeSlabSchema()
                + " where c.id = ? ";
        return this.jdbcTemplate.query(sql, csm, new Object[] {chargeId});
        
    }

}
