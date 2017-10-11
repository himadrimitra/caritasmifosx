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
package org.apache.fineract.scheduledjobs.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PageDataForArrearsAgeingDetailsWithOriginalSchedule {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PageDataForArrearsAgeingDetailsWithOriginalSchedule(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    private final PaginationHelper<Long> paginationHelper = new PaginationHelper<>();
    
    private static final class LoanArrearsAgingMapper implements RowMapper<Long> {
        @Override
        public Long mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long loanId = rs.getLong("loanId");
            return loanId;
        }
    }
    
    @Transactional
    public Page<Long> getPageData(int limit,int offset){
        final LoanArrearsAgingMapper loanArrearsAgingMapper = new LoanArrearsAgingMapper();
        final StringBuilder loanIdentifier = new StringBuilder();
        Date currentDate = DateUtils.getDateOfTenant();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
        formattedDate = "'" + formattedDate + "'";
        loanIdentifier.append("select SQL_CALC_FOUND_ROWS ");
        loanIdentifier.append(" ml.id as loanId FROM m_loan ml  ");
        loanIdentifier.append("INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        loanIdentifier.append(
                "inner join m_product_loan_recalculation_details prd on prd.product_id = ml.product_id and prd.arrears_based_on_original_schedule = 1  ");
        loanIdentifier.append(
                "WHERE ml.loan_status_id = 300  and mr.completed_derived is false  and mr.duedate < SUBDATE(formattedDate,INTERVAL  ifnull(ml.grace_on_arrears_ageing,0) day) group by ml.id");
        loanIdentifier.append(" limit " + limit);
        loanIdentifier.append(" offset " + offset);
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, loanIdentifier.toString(), null,
                loanArrearsAgingMapper);
    }

}
