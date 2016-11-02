/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bank.data.BankData;

@Service
public class BankReadPlatformServiceImpl implements BankReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public BankReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public static final class BankMapper implements RowMapper<BankData> {

        public String schema() {

            return " b.id as id, b.name as name, b.gl_account as glAccount, gl.gl_code as glCode, b.support_simplified_statement as supportSimplifiedStatement from f_bank b left join acc_gl_account gl on gl.id = b.gl_account ";

        }

        @Override
        public BankData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final Long glAccount = JdbcSupport.getLong(rs, "glAccount");
            final String glCode = rs.getString("glCode");
            Boolean supportSimplifiedStatement = rs.getBoolean("supportSimplifiedStatement");
            return new BankData(id, name, glAccount, glCode, supportSimplifiedStatement);
        }
    }

    @Override
    public List<BankData> retrieveAllBanks() {
        this.context.authenticatedUser();

        final BankMapper rm = new BankMapper();

        final String sql = "SELECT " + rm.schema() + " ORDER BY b.id DESC ";

        return this.jdbcTemplate.query(sql, rm);
    }

    @Override
    public BankData getBank(Long bankId) {

        this.context.authenticatedUser();

        final BankMapper rm = new BankMapper();

        final String sql = "SELECT " + rm.schema() + " WHERE b.id = ? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { bankId });
    }

    @Override
    public List<BankData> getBankByGLAccountId(Long gLAccountId) {

        this.context.authenticatedUser();

        final BankMapper rm = new BankMapper();

        final String sql = "SELECT " + rm.schema() + " WHERE b.gl_account = ? ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { gLAccountId });
    }

}
