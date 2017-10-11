package com.finflux.familydetail.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.data.FamilyDetailsSummaryData;

@Service
public class FamilyDetailsSummaryReadPlatformServiceImpl implements FamilyDetailsSummaryReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FamilyDetailsSummaryReadPlatformServiceImpl(RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public FamilyDetailsSummaryData retrieve(final Long clientId) {

        final FamilyDetailsSummaryDataMapper familyDetailsSummaryDataMapper = new FamilyDetailsSummaryDataMapper();

        final String sql = "SELECT " + familyDetailsSummaryDataMapper.schema() + "WHERE f.client_id = ? ";

        return this.jdbcTemplate.queryForObject(sql, familyDetailsSummaryDataMapper, new Object[] { clientId });
    }

    private static final class FamilyDetailsSummaryDataMapper implements RowMapper<FamilyDetailsSummaryData> {

        public String schema() {
            StringBuilder query = new StringBuilder();
            query.append("f.id as id, f.client_id as clientId ")
                    .append(",f.no_of_family_members as noOfFamilyMembers, f.no_of_dependent_minors as noOfDependentMinors ")
                    .append(",f.no_of_dependent_adults as noOfDependentAdults,f.no_of_dependent_seniors as noOfDependentSeniors ")
                    .append(",f.no_of_dependents_with_serious_illness as noOfDependentsWithSeriousIllness ")
                    .append("FROM f_family_details_summary f ");
            return query.toString();
        }

        @Override
        public FamilyDetailsSummaryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final Integer noOfFamilyMembers = rs.getInt("noOfFamilyMembers");
            final Integer noOfDependentMinors = rs.getInt("noOfDependentMinors");
            final Integer noOfDependentAdults = rs.getInt("noOfDependentAdults");
            final Integer noOfDependentSeniors = rs.getInt("noOfDependentSeniors");
            final Integer noOfDependentsWithSeriousIllness = rs.getInt("noOfDependentsWithSeriousIllness");
            return new FamilyDetailsSummaryData(id, clientId, noOfFamilyMembers, noOfDependentMinors, noOfDependentAdults,
                    noOfDependentSeniors, noOfDependentsWithSeriousIllness);
        }
    }
}