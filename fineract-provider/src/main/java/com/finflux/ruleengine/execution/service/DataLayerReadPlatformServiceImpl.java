package com.finflux.ruleengine.execution.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.spm.domain.SurveyEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.configuration.data.FieldType;

@Service
public class DataLayerReadPlatformServiceImpl implements DataLayerReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final FromJsonHelper fromJsonHelper;
    private final ClientDataLayerMapper clientDataLayerMapper = new ClientDataLayerMapper();
    private final SurveyQuestionDataLayerMapper surveyQuestionDataLayerMapper = new SurveyQuestionDataLayerMapper();
    private final SurveyScorecardDataLayerMapper surveyScorecardDataLayerMapper = new SurveyScorecardDataLayerMapper();

    @Autowired
    public DataLayerReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService, final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.fromJsonHelper = fromJsonHelper;
    }

    @Override
    public Map<String, Object> getAllMatrix(Long clientId) {
        Map<String, Object> allClientData = new HashMap<>();
        final String sql = "select " + clientDataLayerMapper.schema() + " where c.id = ?";
        try {
            Map<String, Object> clientDataMap = this.jdbcTemplate.queryForObject(sql, clientDataLayerMapper, clientId);
            if (clientDataMap != null) {
                allClientData.putAll(clientDataMap);
            }
        } catch (final EmptyResultDataAccessException e) {}

        final String questionSql = "SELECT " + surveyQuestionDataLayerMapper.schema() + " WHERE st.entity_type = ? AND st.entity_id = ? ";
        try {
            final SurveyEntityType surveyEntityType = SurveyEntityType.CLIENTS;
            List<Map<String, Object>> questionDataList = this.jdbcTemplate.query(questionSql, surveyQuestionDataLayerMapper, new Object[] {
                    surveyEntityType.getValue(), clientId });
            if (questionDataList != null) {
                for (Map<String, Object> questionData : questionDataList) {
                    allClientData.putAll(questionData);
                }
            }
        } catch (final EmptyResultDataAccessException e) {}
        //

        final String surveySql = "SELECT " + surveyScorecardDataLayerMapper.schema()
                + " WHERE st.entity_type = ? AND st.entity_id = ? group by ss.survey_id";
        try {
            final SurveyEntityType surveyEntityType = SurveyEntityType.CLIENTS;
            List<Map<String, Object>> surveyScoreCards = this.jdbcTemplate.query(surveySql, surveyScorecardDataLayerMapper, new Object[] {
                    surveyEntityType.getValue(), clientId });
            if (surveyScoreCards != null) {
                for (Map<String, Object> surveyScoreCard : surveyScoreCards) {
                    allClientData.putAll(surveyScoreCard);
                }
            }
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client number of active MFI loans
         */
        final Map<String, Object> clientActiveMFILoansCountMap = getClientActiveMFILoansCountMap(clientId);
        if (clientActiveMFILoansCountMap != null) {
            allClientData.putAll(clientActiveMFILoansCountMap);
        }

        return allClientData;
    }

    private Map<String, Object> getClientActiveMFILoansCountMap(final Long clientId) {
        final StringBuilder sql = new StringBuilder(250);
        sql.append("SELECT COUNT(*) AS clientactiveloanscount FROM f_existing_loan el WHERE el.client_id = ? AND el.loan_status_id = 300");
        return this.jdbcTemplate.queryForMap(sql.toString(), new Object[] { clientId });
    }

    private static final class ClientDataLayerMapper implements RowMapper<Map<String, Object>> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public ClientDataLayerMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(100);
            sqlBuilder.append(" ");
            sqlBuilder.append("DATE_FORMAT(FROM_DAYS(DATEDIFF(now(),c.date_of_birth)), '%Y')+0 as age, ");
            sqlBuilder.append("c.gender_cv_id  as gender ");
            sqlBuilder.append(",c.client_type_cv_id as clienttype ");
            sqlBuilder.append(",c.client_classification_cv_id as clientclassification ");
            sqlBuilder.append(",c.sub_status as clientsubstatus ");

            sqlBuilder.append("from m_client as c ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Map<String, Object> mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            Map<String, Object> mapData = new HashMap();
            final Long age = rs.getLong("age");
            final String gender = rs.getString("gender");
            mapData.put("age", age);
            mapData.put("gender", gender);
            mapData.put("clienttype", rs.getString("clienttype"));
            mapData.put("clientclassification", rs.getString("clientclassification"));
            mapData.put("clientsubstatus", rs.getString("clientsubstatus"));
            return mapData;
        }
    }

    private static final class SurveyQuestionDataLayerMapper implements RowMapper<Map<String, Object>> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public SurveyQuestionDataLayerMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" ").append("ss.question_id as questionId, ").append("ss.response_id  as responseId ")
                    .append("FROM m_survey_scorecards as ss JOIN f_survey_taken st ON st.id = ss.survey_taken_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Map<String, Object> mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            Map<String, Object> mapData = new HashMap();
            final Long questionId = rs.getLong("questionId");
            final Long responseId = rs.getLong("responseId");
            if (responseId != null) {
                mapData.put(FieldType.QUESTION + "_" + questionId, "" + responseId);
            }
            return mapData;
        }
    }

    private static final class SurveyScorecardDataLayerMapper implements RowMapper<Map<String, Object>> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public SurveyScorecardDataLayerMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" ").append("ss.survey_id as surveyId, ").append("sum(ss.a_value)  as scorecard ")
                    .append("FROM m_survey_scorecards as ss JOIN f_survey_taken st ON st.id = ss.survey_taken_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public Map<String, Object> mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            Map<String, Object> mapData = new HashMap();
            final Long surveyId = rs.getLong("surveyId");
            final Long scroecard = rs.getLong("scorecard");
            if (scroecard != null) {
                mapData.put(FieldType.SURVEY + "_" + surveyId, "" + scroecard);
            }
            return mapData;
        }
    }
}