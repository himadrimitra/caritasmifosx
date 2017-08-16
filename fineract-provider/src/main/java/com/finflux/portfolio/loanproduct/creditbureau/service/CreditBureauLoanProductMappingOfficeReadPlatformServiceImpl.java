/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.portfolio.loanproduct.creditbureau.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CreditBureauLoanProductMappingOfficeReadPlatformServiceImpl
        implements CreditBureauLoanProductOfficeMappingReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CreditBureauLoanProductMappingOfficeReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // check for default - prodctid, null(office)
    @Override
    public Integer retrieveLoanProductDefaultMappingCount(Long loanProductId) {
        final String sql = "select count(*) from f_creditbureau_loanproduct_office_mapping clo where clo.loan_product_id = ? and clo.office_id is NULL";
        return this.jdbcTemplate.queryForObject(sql, Integer.class, loanProductId);
    }

    // check for bureau id, product mapping

    @Override
    public Integer retrieveCreditBureauAndLoanProductMappingCount(Long creditBureauProductId, Long loanProductId) {
        final String sql = "select count(*) from f_creditbureau_loanproduct_office_mapping clo inner join f_creditbureau_loanproduct_mapping cl on clo.credit_bureau_loan_product_mapping_id = cl.id and clo.loan_product_id = ? and cl.creditbureau_product_id = ? ";
        return this.jdbcTemplate.queryForObject(sql, Integer.class, loanProductId, creditBureauProductId);
    }

    @Override
    public Integer retrieveDefaultCreditBureauAndLoanProductMappingCount(Long creditBureauProductMappingId, Long loanProductId) {
        final String sql = "select count(*) from f_creditbureau_loanproduct_office_mapping clo inner join f_creditbureau_loanproduct_mapping cl on clo.credit_bureau_loan_product_mapping_id = cl.id and clo.loan_product_id = ? and clo.office_id IS NULL and clo.credit_bureau_loan_product_mapping_id != ?";
        return this.jdbcTemplate.queryForObject(sql, Integer.class, loanProductId, creditBureauProductMappingId);
    }

    @Override
    public Integer retrieveCurrentCreditBureauAndLoanProductMappingCount(Long creditBureauProductId, Long loanProductId,
            Long creditBureauLoanProductMappingId) {
        final String sql = "select count(*) from f_creditbureau_loanproduct_office_mapping clo inner join f_creditbureau_loanproduct_mapping cl on clo.credit_bureau_loan_product_mapping_id = cl.id and clo.loan_product_id = ? and cl.creditbureau_product_id = ?  and clo.credit_bureau_loan_product_mapping_id != ?";
        return this.jdbcTemplate.queryForObject(sql, Integer.class, loanProductId, creditBureauProductId, creditBureauLoanProductMappingId);
    }

}
