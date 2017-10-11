package com.finflux.ruleengine.configuration.service;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.finflux.ruleengine.lib.data.*;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.spm.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.configuration.data.FieldData;
import com.finflux.ruleengine.configuration.data.FieldType;
import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.configuration.data.SurveyFieldData;
import com.google.gson.reflect.TypeToken;

@Service
public class RiskConfigReadPlatformServiceImpl implements RiskConfigReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final FromJsonHelper fromJsonHelper;
    private final FieldDataMapper fieldDataMapper;
    private final SurveyFieldDataMapper surveyFieldDataMapper = new SurveyFieldDataMapper();
    private final RuleDataMapper ruleDataMapper = new RuleDataMapper();
    private final SurveyRepository surveyRepository;

    @Autowired
    public RiskConfigReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService, final FromJsonHelper fromJsonHelper,
            final SurveyRepository surveyRepository) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.fromJsonHelper = fromJsonHelper;
        this.fieldDataMapper = new FieldDataMapper(this.fromJsonHelper, this.codeValueReadPlatformService);
        this.surveyRepository = surveyRepository;
    }

    @Override
    public List<FieldData> getAllFields() {
        List<FieldData> allFields = new ArrayList<>();
        final String sql = "select " + fieldDataMapper.schema() + " where rf.is_active = ? ";
        try {
            allFields.addAll(this.jdbcTemplate.query(sql, fieldDataMapper, true));
        } catch (final EmptyResultDataAccessException e) {
            // return null;
        }

        try {
            List<FieldData> clientIdentifierList = getAllClientIdentifierFields();
            allFields.addAll(clientIdentifierList);
        } catch (final EmptyResultDataAccessException e) {
            // return null;
        }

        final String surveyFieldsSql = "select " + surveyFieldDataMapper.schema() + " order by s.id asc, sq.id asc, sr.id asc";
        try {
            List<SurveyFieldData> surveyFieldDataList = this.jdbcTemplate.query(surveyFieldsSql, surveyFieldDataMapper);
            List<FieldData> surveyFields = getAllSurveyFields(surveyFieldDataList);
            allFields.addAll(surveyFields);
        } catch (final EmptyResultDataAccessException e) {
            // return null;
        }

        return allFields;

    }

    private List<FieldData> getAllClientIdentifierFields() {
        List<FieldData> fields = new ArrayList<>();
        Collection<CodeValueData> codeValues = codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier");
        for(CodeValueData codeValueData: codeValues){
            FieldData clientIdentifierField = new FieldData(codeValueData.getName(), FieldType.CLIENTIDENTIFIER.name() + "_"
                    + codeValueData.getId(), ValueType.STRING, null, FieldType.CLIENTIDENTIFIER);
            fields.add(clientIdentifierField);
        }
        return fields;
    }

    @Override
    public List<RuleData> getAllFactors() {
        return getAllRulesByEntity(EntityRuleType.FACTOR);
    }

    @Override
    public RuleData retrieveOneFactor(Long factorId) {
        return retrieveOneRule(EntityRuleType.FACTOR, factorId);
    }

    @Override
    public List<RuleData> getAllDimensions() {
        return getAllRulesByEntity(EntityRuleType.DIMENSION);
    }

    @Override
    public RuleData retrieveOneDimension(Long dimensionId) {
        return retrieveOneRule(EntityRuleType.DIMENSION, dimensionId);
    }

    @Override
    public List<RuleData> getAllCriterias() {
        return getAllRulesByEntity(EntityRuleType.CRITERIA);
    }

    @Override
    public RuleData retrieveOneCriteria(Long criteriaId) {
        return retrieveOneRule(EntityRuleType.CRITERIA, criteriaId);
    }

    @Override
    public List<RuleData> getAllRules() {
        final String sql = "select " + ruleDataMapper.schema();
        try {
            return this.jdbcTemplate.query(sql, ruleDataMapper);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private RuleData retrieveOneRule(EntityRuleType ruleType, Long id) {
        final String sql = "select " + ruleDataMapper.schema() + " where rr.entity_type = ? and rr.id = ?";
        try {
            List<RuleData> ruleDatas = this.jdbcTemplate.query(sql, ruleDataMapper, ruleType.getValue(), id);
            if (ruleDatas != null && ruleDatas.size() == 1) { return ruleDatas.get(0); }
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
        return null;
    }

    @Override
    public RuleData retrieveRuleByUname(String uname) {
        final String sql = "select " + ruleDataMapper.schema() + " where rr.uname = ? ";
        try {
            return this.jdbcTemplate.queryForObject(sql, ruleDataMapper, uname);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

	@Override
	public Rule getRuleById(Long ruleId) {
		final String sql = "select " + ruleDataMapper.schema() + " where rr.id = ? ";
		try {
			RuleData ruleData =  this.jdbcTemplate.queryForObject(sql, ruleDataMapper, ruleId);
			return ruleData.getRule();
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<RuleData> getAllRulesByEntity(EntityRuleType ruleType) {
        final String sql = "select " + ruleDataMapper.schema() + " where rr.entity_type = ? ";
        try {
            return this.jdbcTemplate.query(sql, ruleDataMapper, ruleType.getValue());
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class RuleDataMapper implements RowMapper<RuleData> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public RuleDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("rr.id as id, ").append("rr.entity_type as entityType, ").append("rr.name as name, ")
                    .append("rr.uname as uname, ").append("rr.description as description, ").append("rr.default_value as defaultValue, ")
                    .append("rr.value_type as valueType, ").append("rr.possible_outputs as possibleOutputs, ")
                    .append("rr.expression as expression, ").append("rr.is_active as isActive ").append("from f_risk_rule as rr ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public RuleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String uname = rs.getString("uname");
            final Integer entityType = rs.getInt("entityType");
            final String description = rs.getString("description");
            final String defaultValue = rs.getString("defaultValue");
            final Integer valueType = rs.getInt("valueType");
            final String possibleOutputStr = rs.getString("possibleOutputs");
            final String expressionStr = rs.getString("expression");
            final Boolean isActive = rs.getBoolean("isActive");
            List<KeyValue> possibleOutputs = null;
            List<Bucket> buckets = null;
            if (possibleOutputStr != null) {
                Type type = new TypeToken<List<KeyValue>>() {}.getType();
                possibleOutputs = jsonHelper.getGsonConverter().fromJson(possibleOutputStr, type);
            }
            if (expressionStr != null) {
                Type type = new TypeToken<List<Bucket>>() {}.getType();
                buckets = jsonHelper.getGsonConverter().fromJson(expressionStr, type);
            }
            return new RuleData(id, EntityRuleType.fromInt(entityType), name, uname, description, defaultValue,
                    ValueType.fromInt(valueType), possibleOutputs, buckets, isActive);
        }
    }

    private static final class FieldDataMapper implements RowMapper<FieldData> {

        private final FromJsonHelper jsonHelper;
        private final Type type;
        private final CodeValueReadPlatformService codeValueReadPlatformService;

        private final String schemaSql;

        public FieldDataMapper(FromJsonHelper fromJsonHelper, CodeValueReadPlatformService codeValueReadPlatformService) {
            this.jsonHelper = fromJsonHelper;
            this.type = new TypeToken<List<KeyValue>>() {}.getType();
            this.codeValueReadPlatformService = codeValueReadPlatformService;
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("rf.id as id,").append("rf.name as name,").append("rf.uname as uname,").append("rf.value_type as valueType,")
                    .append("rf.options as options, ").append("rf.code_name as codeName ").append("from f_risk_field rf");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public FieldData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String uname = rs.getString("uname");
            final Integer valueType = rs.getInt("valueType");
            final String options = rs.getString("options");
            final String codeName = rs.getString("codeName");
            List<KeyValue> optionList = null;
            if (options != null) {
                optionList = jsonHelper.getGsonConverter().fromJson(options, type);
            } else if (codeName != null) {
                Collection<CodeValueData> codeValueList = codeValueReadPlatformService.retrieveCodeValuesByCode(codeName);
                if (codeValueList != null && !codeValueList.isEmpty()) {
                    optionList = new ArrayList<>();
                    for (CodeValueData codeValueData : codeValueList) {
                        KeyValue keyValue = new KeyValue("" + codeValueData.getId(), codeValueData.getName());
                        optionList.add(keyValue);
                    }
                }
            }
            return new FieldData(name, uname, ValueType.fromInt(valueType), optionList);
        }

    }

    private static final class SurveyFieldDataMapper implements RowMapper<SurveyFieldData> {

        private final String schemaSql;

        public SurveyFieldDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("s.id as surveyId,").append(" s.a_key as surveyDisplay,").append(" sq.id as questionId,")
                    .append(" sq.a_key as questionDisplay,").append(" sr.id as responseId,").append(" sr.a_text as responseDisplay ")
                    .append(" from m_survey_responses as sr ").append(" left join m_survey_questions as sq on sr.question_id = sq.id ")
                    .append(" left join m_surveys as s on sq.survey_id = s.id");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SurveyFieldData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long surveyID = rs.getLong("surveyId");
            final String surveyDisplay = rs.getString("surveyDisplay");
            final Long questionID = rs.getLong("questionId");
            final String questionDisplay = rs.getString("questionDisplay");
            final Long responseID = rs.getLong("responseId");
            final String responseDisplay = rs.getString("responseDisplay");
            return new SurveyFieldData(surveyID, surveyDisplay, questionID, questionDisplay, responseID, responseDisplay);
        }
    }

    private List<FieldData> getAllSurveyFields(List<SurveyFieldData> surveyFieldDataList) {
        Long currQues = -1L;
        FieldData currFieldData = null;
        Long currSurvey = -1L;
        List<FieldData> surveyFields = new ArrayList<>();
        for (SurveyFieldData surveyFieldData : surveyFieldDataList) {
            if (!surveyFieldData.getSurveyId().equals(currSurvey)) {
                FieldData surveyField = new FieldData(surveyFieldData.getSurveyDisplay(), FieldType.SURVEY + "_"
                        + surveyFieldData.getSurveyId(), ValueType.NUMBER, null, FieldType.SURVEY);
                surveyFields.add(surveyField);
                currSurvey = surveyFieldData.getSurveyId();
            }
            if (!surveyFieldData.getQuestionId().equals(currQues)) {
                currQues = surveyFieldData.getQuestionId();
                currFieldData = new FieldData(surveyFieldData.getQuestionDisplay(), FieldType.QUESTION + "_"
                        + surveyFieldData.getQuestionId(), ValueType.STRING, new ArrayList<>(), FieldType.QUESTION);
                surveyFields.add(currFieldData);
            }
            currFieldData.getOptions().add(new KeyValue("" + surveyFieldData.getResponseId(), surveyFieldData.getResponseDisplay()));
        }
        return surveyFields;
    }
}