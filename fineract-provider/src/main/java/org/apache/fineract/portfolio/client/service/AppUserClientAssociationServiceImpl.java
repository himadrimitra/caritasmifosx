package org.apache.fineract.portfolio.client.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppUserClientAssociationServiceImpl implements AppUserClientAssociationService {

    private final JdbcTemplate jdbcTemplate;

    private final String OFFICE_FINDER_SQL = "SELECT CASE WHEN (COUNT(*) > 0) THEN TRUE ELSE FALSE END " + "FROM m_appuser ma "
            + "JOIN m_office ao ON ao.id = ma.office_id " + " JOIN m_client mc ON mc.id = ? "
            + "JOIN m_office mco ON mco.id = mc.office_id " + "WHERE ma.id = ? AND mco.hierarchy LIKE CONCAT('%',ao.hierarchy,'%')";

    @Autowired
    AppUserClientAssociationServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Boolean hasAccessToClient(Long clientId, Long appUserId) {
        return this.jdbcTemplate.queryForObject(OFFICE_FINDER_SQL, Boolean.class, clientId, appUserId);
    }

}
