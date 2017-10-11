/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BankGLAccountReadPlatformServiceImpl implements BankGLAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BankGLAccountReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GLAccountDataMapper implements RowMapper<GLAccountDataForLookup> {

        public String schema() {
            return " gl.id as id, gl.name as name, gl.gl_code as glCode from acc_gl_account gl ";
        }

        @Override
        public GLAccountDataForLookup mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String glCode = rs.getString("glCode");
            return new GLAccountDataForLookup(id, name, glCode);
        }

    }

    @Override
    public GLAccountDataForLookup retrieveGLAccountByGLCode(String glCode) {
        final GLAccountDataMapper mapper = new GLAccountDataMapper();
        final String sql = "Select " + mapper.schema() + " where gl.gl_code=? ";
        GLAccountDataForLookup glAccountDataForLookup = null;
        try {
            glAccountDataForLookup = this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { glCode });
        } catch (EmptyResultDataAccessException ex) {
            glAccountDataForLookup = null;
        }
        return glAccountDataForLookup;
    }

}
