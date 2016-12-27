package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppUserLoanAssociationServiceImple implements AppUserLoanAssociationService {

    private final JdbcTemplate jdbcTemplate;

    private final String LOAN_USER_OFFICE_FINDER_SQL = "SELECT CASE WHEN (COUNT(*) > 0) THEN TRUE ELSE FALSE END " + "FROM m_appuser ma "
            + "JOIN m_office ao ON ao.id = ma.office_id " + "JOIN m_loan ml ON ml.id = ? "
            + "LEFT JOIN m_client mc ON mc.id = ml.client_id " + "LEFT JOIN m_group mg ON mg.id = ml.group_id "
            + "LEFT JOIN m_office mco ON mco.id = mc.office_id " + "LEFT JOIN m_office mgo ON mgo.id = mg.office_id "
            + "WHERE ma.id = ? AND (mco.hierarchy LIKE CONCAT('%',ao.hierarchy,'%') OR mgo.hierarchy LIKE CONCAT('%',ao.hierarchy,'%'))";

    @Autowired
    AppUserLoanAssociationServiceImple(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Boolean hasAccessToLoan(final Long loanId, final Long appUserId) {
        return this.jdbcTemplate.queryForObject(LOAN_USER_OFFICE_FINDER_SQL, Boolean.class, loanId, appUserId);
    }
}
