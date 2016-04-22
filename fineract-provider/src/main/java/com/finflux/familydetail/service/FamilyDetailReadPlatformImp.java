package com.finflux.familydetail.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.familydetail.FamilyDetailsApiConstants;
import com.finflux.familydetail.data.FamilyDetailData;
import com.finflux.familydetail.data.FamilyDetailTemplateData;

@Service
public class FamilyDetailReadPlatformImp implements FamilyDetailsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public FamilyDetailReadPlatformImp(RoutingDataSource dataSource, CodeValueReadPlatformService codeValueReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @Override
    public Collection<FamilyDetailData> retrieveAllFamilyDetails(Long clientId) {

        FamilyDetailsMapper familyDetailsMapper = new FamilyDetailsMapper();

        final String sql = "select " + familyDetailsMapper.schema() + "where f.client_id = ?";

        return this.jdbcTemplate.query(sql, familyDetailsMapper, new Object[] { clientId });

    }

    private static final class FamilyDetailsMapper implements RowMapper<FamilyDetailData> {

        @Override
        public FamilyDetailData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final CodeValueData salutation = CodeValueData.instance(rs.getLong("salutation"), "Salutaion");
            final String firstname = rs.getString("firstname");
            final String middlename = rs.getString("middlename");
            final String lastname = rs.getString("lastname");
            final CodeValueData relationship = CodeValueData.instance(rs.getLong("relationshipCvId"), "Relationship");
            final CodeValueData gender = CodeValueData.instance(rs.getLong("gender"), "gender");
            final Date dateOfBirth = rs.getDate("dateOfBirth");
            final Integer age = rs.getInt("age");
            final CodeValueData occupation = CodeValueData.instance(rs.getLong("occupationDetailsCvId"), "OccupationalDetails");
            final CodeValueData education = CodeValueData.instance(rs.getLong("educationCvId"), "Education");

            return new FamilyDetailData(id, firstname, middlename, lastname, salutation, relationship, gender, dateOfBirth, age, education,
                    occupation);
        }

        public String schema() {
            StringBuilder query = new StringBuilder();
            query.append(
                    "f.id as id, f.client_id as clientId, f.salutation_cv_id as salutation, f.firstname as firstname, f.middlename as middlename, ");
            query.append("f.lastname as lastname, f.relationship_cv_id as relationshipCvId, f.gender_cv_id as gender, ");
            query.append("f.dateOfBirth as dateOfBirth, f.age as age, f.occupation_details_cv_id as occupationDetailsCvId, ");
            query.append("f.education_cv_id as educationCvId ");
            query.append("FROM f_family_details f ");
            query.append("JOIN m_client mc ON mc.id = f.client_id ");
            query.append("LEFT JOIN m_code_value mcv1 ON mcv1.id = f.salutation_cv_id ");
            query.append("LEFT JOIN m_code_value mcv2 ON mcv2.id = f.relationship_cv_id ");
            query.append("LEFT JOIN m_code_value mcv3 ON mcv3.id = f.gender_cv_id ");
            query.append("LEFT JOIN m_code_value mcv4 ON mcv4.id = f.occupation_details_cv_id ");
            query.append("LEFT JOIN m_code_value mcv5 ON mcv5.id = f.education_cv_id ");

            return query.toString();
        }

    }

    @Override
    public FamilyDetailTemplateData retrieveTemplate() {

        final Collection<CodeValueData> genderOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.genderIdParamName);

        final Collection<CodeValueData> occupationOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.occupationDetailsIdParamName);

        final Collection<CodeValueData> salutationOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.salutationIdParamName);

        final Collection<CodeValueData> educationOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.educationIdParamName);

        final Collection<CodeValueData> relationshipOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(FamilyDetailsApiConstants.relationshipIdParamName);

        return FamilyDetailTemplateData.template(salutationOptions, relationshipOptions, genderOptions, educationOptions,
                occupationOptions);
    }

}
