package org.apache.fineract.portfolio.group.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppUserGroupAssociationServiceImpl implements AppUserGroupAssociationService {

    private final JdbcTemplate jdbcTemplate;

    private final String OFFICE_FINDER_SQL = "SELECT CASE WHEN (COUNT(*) > 0) THEN TRUE ELSE FALSE END " + "FROM m_appuser ma "
            + "JOIN m_office ao ON ao.id = ma.office_id " + "JOIN m_group mg ON mg.id = ? " + "JOIN m_office mgo ON mgo.id = mg.office_id "
            + "WHERE ma.id = ? AND mgo.hierarchy LIKE CONCAT('%',ao.hierarchy,'%')";

    @Autowired
    AppUserGroupAssociationServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Boolean hasAccessToGroup(Long groupId, Long appUserId) {
        return this.jdbcTemplate.queryForObject(OFFICE_FINDER_SQL, Boolean.class, groupId, appUserId);
    }

}
