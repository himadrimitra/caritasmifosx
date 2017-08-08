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

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.charge.data.ChargeSlabData;
import org.apache.fineract.portfolio.charge.domain.SlabChargeType;
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
            String sql = "cs.id as csid, cs.from_loan_amount as min, cs.to_loan_amount as max, cs.amount as chargeAmount, cs.type as type "
                    + " from f_charge_slab cs "
                    +" LEFT JOIN m_charge c on cs.charge_id = c.id ";
            return sql;
        }


        @Override
        public ChargeSlabData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("csid");
            final BigDecimal minValue = rs.getBigDecimal("min");
            final BigDecimal maxValue = rs.getBigDecimal("max");
            final BigDecimal amount = rs.getBigDecimal("chargeAmount");
            final EnumOptionData type = SlabChargeType.fromInt(rs.getInt("type"));
            return ChargeSlabData.createChargeSlabData(id, minValue, maxValue, amount, type);
        }
    }

    @Override
    public Collection<ChargeSlabData> retrieveAllChargeSubSlabsBySlabChargeId(Long slabChargeId) {
        final ChargeSlabMapper csm = new ChargeSlabMapper();
        String sql = "select " + csm.chargeSlabSchema()
                + " where cs.parent_id = ? ";
        return this.jdbcTemplate.query(sql, csm, new Object[] {slabChargeId});
    }

}
