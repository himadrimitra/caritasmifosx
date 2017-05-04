package com.finflux.ruleengine.execution.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.service.AddressReadPlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientIdentifierData;
import org.apache.fineract.portfolio.client.service.ClientIdentifierReadPlatformService;
import org.apache.fineract.spm.domain.SurveyEntityType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    private final LoanApplicationDataLayerMapper loanApplicationDataLayerMapper = new LoanApplicationDataLayerMapper();
    private final ClientIdentifierReadPlatformService clientIdentifierReadPlatformService;
    private final AddressReadPlatformService addressReadPlatformService;
	private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    public DataLayerReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService, final FromJsonHelper fromJsonHelper,
            final ClientIdentifierReadPlatformService clientIdentifierReadPlatformService,
            final AddressReadPlatformService addressReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.fromJsonHelper = fromJsonHelper;
        this.clientIdentifierReadPlatformService = clientIdentifierReadPlatformService;
        this.addressReadPlatformService = addressReadPlatformService;
    }

    @Override
    public Map<String, Object> getAllClientMatrix(Long clientId) {
        Map<String, Object> allClientData = new HashMap<>();
        final String sql = "select " + clientDataLayerMapper.schema() + " where c.id = ?";
        try {
            Map<String, Object> clientDataMap = this.jdbcTemplate.queryForObject(sql, clientDataLayerMapper, clientId);
            if (clientDataMap != null) {
                allClientData.putAll(clientDataMap);
            }
        } catch (final EmptyResultDataAccessException e) {}

        //identifierData

        Collection<ClientIdentifierData> identifierDatas = this.clientIdentifierReadPlatformService.retrieveClientIdentifiers(clientId);
        if(identifierDatas!=null){
            for(ClientIdentifierData identifierData: identifierDatas){
                final Map<String, Object> keyValue = new HashMap<>();
                keyValue.put(FieldType.CLIENTIDENTIFIER.name()+"_"+identifierData.getDocumentType().getId(), identifierData.getDocumentKey());
                allClientData.putAll(keyValue);
            }
        }

        //addressData
        Long addressCount = addressReadPlatformService.countOfAddressByEntityTypeAndEntityId(AddressEntityTypeEnums.CLIENTS,clientId);
        allClientData.put("clientAddressCount",addressCount);

        //Survey QUestions
        final String questionSql = "SELECT " + surveyQuestionDataLayerMapper.schema()
                + " WHERE st.entity_type = ? AND st.entity_id = ? order by st.id asc";
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
                + " WHERE st.entity_type = ? AND st.entity_id = ? group by ss.survey_id, st.id order by st.id asc";
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
         * Client % Income from high stability
         */
        try {
            final Map<String, Object> clientIncomeFromHighStabilityPercentage = getClientIncomeFromHighStabilityPercentage(clientId);
            if (clientIncomeFromHighStabilityPercentage != null) {
                allClientData.putAll(clientIncomeFromHighStabilityPercentage);
            }
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client meeting attendance history %
         */
        try {
            final Map<String, Object> clientMeetingAttendanceHistoryPercentage = getClientMeetingAttendanceHistoryPercentage(clientId);
            if (clientMeetingAttendanceHistoryPercentage != null) {
                allClientData.putAll(clientMeetingAttendanceHistoryPercentage);
            }
        } catch (final EmptyResultDataAccessException e) {}

        return allClientData;
    }

    @Override
    public Map<String, Object> getAllLoanApplicationMatrix(Long loanApplicationId) {
        Map<String, Object> allLoanApplicationData = new HashMap<>();
        final String sql = "select " + loanApplicationDataLayerMapper.schema() + " where l.id = ?";
        try {
            Map<String, Object> loanAppDataMap = this.jdbcTemplate.queryForObject(sql, loanApplicationDataLayerMapper, loanApplicationId);
            if (loanAppDataMap != null) {
                allLoanApplicationData.putAll(loanAppDataMap);
            }
        } catch (final EmptyResultDataAccessException e) {}

        final String questionSql = "SELECT " + surveyQuestionDataLayerMapper.schema()
                + " WHERE st.entity_type = ? AND st.entity_id = ? order by st.id asc";
        try {
            final SurveyEntityType surveyEntityType = SurveyEntityType.LOANAPPLICATIONS;
            List<Map<String, Object>> questionDataList = this.jdbcTemplate.query(questionSql, surveyQuestionDataLayerMapper, new Object[] {
                    surveyEntityType.getValue(), loanApplicationId });
            if (questionDataList != null) {
                for (Map<String, Object> questionData : questionDataList) {
                    allLoanApplicationData.putAll(questionData);
                }
            }
        } catch (final EmptyResultDataAccessException e) {}
        //

        final String surveySql = "SELECT " + surveyScorecardDataLayerMapper.schema()
                + " WHERE st.entity_type = ? AND st.entity_id = ? group by ss.survey_id, st.id order by st.id asc";
        try {
            final SurveyEntityType surveyEntityType = SurveyEntityType.LOANAPPLICATIONS;
            List<Map<String, Object>> surveyScoreCards = this.jdbcTemplate.query(surveySql, surveyScorecardDataLayerMapper, new Object[] {
                    surveyEntityType.getValue(), loanApplicationId });
            if (surveyScoreCards != null) {
                for (Map<String, Object> surveyScoreCard : surveyScoreCards) {
                    allLoanApplicationData.putAll(surveyScoreCard);
                }
            }
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client Details From Other Lenders Loans
         */
        final List<Map<String, Object>> clientLoanDetailsFromOtherLendersList = getLoanDetailsFromOtherLendersForLoanApplication(loanApplicationId);
        final BigDecimal clienttotaloutstandingamount = BigDecimal.ZERO;
        Integer clientActiveMFILoansCount = 0;
        final BigDecimal clienttotalwrittenoffamount = BigDecimal.ZERO;
        Integer clientnumberofwrittenoffloans = 0;
        final BigDecimal clienttotalmonthlydueamount = BigDecimal.ZERO;
        if (clientLoanDetailsFromOtherLendersList != null && !clientLoanDetailsFromOtherLendersList.isEmpty()) {
            for (final Map<String, Object> clientLoanDetailsFromOtherLenders : clientLoanDetailsFromOtherLendersList) {
                if (clientLoanDetailsFromOtherLenders.get("clientwrittenoffamount") != null) {
                    final BigDecimal clientwrittenoffamount = (BigDecimal) clientLoanDetailsFromOtherLenders.get("clientwrittenoffamount");
                    clientLoanDetailsFromOtherLenders.remove("clientwrittenoffamount");
                    if (clientwrittenoffamount.doubleValue() > BigDecimal.ZERO.doubleValue()) {
                        clientnumberofwrittenoffloans++;
                        clienttotalwrittenoffamount.add(clientwrittenoffamount);
                    }
                }
                if (clientLoanDetailsFromOtherLenders.get("loanstatus") != null
                        && Integer.valueOf(clientLoanDetailsFromOtherLenders.get("loanstatus").toString()) == 300) {
                    clientLoanDetailsFromOtherLenders.remove("loanstatus");
                    allLoanApplicationData.putAll(clientLoanDetailsFromOtherLenders);
                    clienttotaloutstandingamount.add((BigDecimal) clientLoanDetailsFromOtherLenders.get("clientoutstandingamount"));
                    clientActiveMFILoansCount++;

                    if (clientLoanDetailsFromOtherLenders.get("loantenureperiodtype") != null) {
                        if (Integer.valueOf(clientLoanDetailsFromOtherLenders.get("loantenureperiodtype").toString()) == 1) {

                        } else if (Integer.valueOf(clientLoanDetailsFromOtherLenders.get("loantenureperiodtype").toString()) == 2) {
                            clienttotalmonthlydueamount.add((BigDecimal) clientLoanDetailsFromOtherLenders.get("clientinstallmentamount"));
                        }
                        clientLoanDetailsFromOtherLenders.remove("loantenureperiodtype");
                    }
                }
            }
        }

        /**
         * Client total amount monthly due
         */
        try {
            final Map<String, Object> clienttotalmonthlydueamountMap = new HashMap<String, Object>();
            clienttotalmonthlydueamountMap.put("clienttotalmonthlydueamount", clienttotalmonthlydueamount);
            allLoanApplicationData.putAll(clienttotalmonthlydueamountMap);
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client total written off amount from other lenders
         */
        try {
            final Map<String, Object> clienttotalwrittenoffamountMap = new HashMap<String, Object>();
            clienttotalwrittenoffamountMap.put("clienttotalwrittenoffamount", clienttotalwrittenoffamount);
            allLoanApplicationData.putAll(clienttotalwrittenoffamountMap);
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client number of written off loans
         */
        try {
            final Map<String, Object> clientnumberofwrittenoffloansMap = new HashMap<String, Object>();
            clientnumberofwrittenoffloansMap.put("clientnumberofwrittenoffloans", clientnumberofwrittenoffloans);
            allLoanApplicationData.putAll(clientnumberofwrittenoffloansMap);
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client total outstanding amount from other lenders
         */
        try {
            final Map<String, Object> clientTotalOutstandingAmount = new HashMap<String, Object>();
            clientTotalOutstandingAmount.put("clienttotaloutstandingamount", clienttotaloutstandingamount);
            allLoanApplicationData.putAll(clientTotalOutstandingAmount);
        } catch (final EmptyResultDataAccessException e) {}

        /**
         * Client number of active MFI loans
         */
        try {
            final Map<String, Object> clientActiveMFILoansCountMap = new HashMap<String, Object>();
            clientActiveMFILoansCountMap.put("clientactiveloanscount", clientActiveMFILoansCount);
            allLoanApplicationData.putAll(clientActiveMFILoansCountMap);
        } catch (final EmptyResultDataAccessException e) {}

        return allLoanApplicationData;
    }

    private Map<String, Object> getClientMeetingAttendanceHistoryPercentage(Long clientId) {
        final StringBuilder sql = new StringBuilder(150);
        String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
        sql.append("SELECT ");
        sql.append("ifNull(SUM(IF(ca.attendance_type_enum = 1,1,0))/COUNT(ca.id)*100,0) AS clientattendancepresentage ");
        sql.append("FROM m_client_attendance ca ");
        sql.append("INNER JOIN m_meeting meeting on meeting.id = ca.meeting_id AND meeting.meeting_date >= DATE_SUB(?, INTERVAL 180 DAY) ");
        sql.append("WHERE ca.client_id = ? ");
        sql.append("GROUP BY ca.client_id ");
        return this.jdbcTemplate.queryForMap(sql.toString(), new Object[] { clientId,currentdate });
    }

    private List<Map<String, Object>> getClientLoanDetailsFromOtherLenders(final Long clientId) {
        final StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT el.loan_status_id AS loanstatus ");
        sql.append(",IFNULL(el.current_outstanding,0) AS clientoutstandingamount ");
        sql.append(",IFNULL(el.installment_amount, 0) clientinstallmentamount ");
        sql.append(",IFNULL(el.written_off_amount, 0) clientwrittenoffamount ");
        sql.append(",IFNULL(el.loan_tenure_period_type, 0) loantenureperiodtype ");
        sql.append("FROM f_existing_loan el ");
        sql.append("WHERE el.client_id = ? ");
        return this.jdbcTemplate.queryForList(sql.toString(), new Object[] { clientId });
    }

    private List<Map<String, Object>> getLoanDetailsFromOtherLendersForLoanApplication(final Long loanApplicationId) {
        final StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT el.loan_status_id AS loanstatus ");
        sql.append(",IFNULL(el.current_outstanding,0) AS clientoutstandingamount ");
        sql.append(",IFNULL(el.installment_amount, 0) clientinstallmentamount ");
        sql.append(",IFNULL(el.written_off_amount, 0) clientwrittenoffamount ");
        sql.append(",IFNULL(el.loan_tenure_period_type, 0) loantenureperiodtype ");
        sql.append("FROM f_existing_loan el ");
        sql.append("WHERE el.loan_application_id = ? ");
        return this.jdbcTemplate.queryForList(sql.toString(), new Object[] { loanApplicationId });
    }

    private Map<String, Object> getClientIncomeFromHighStabilityPercentage(final Long clientId) {
        final StringBuilder sql = new StringBuilder(250);
        sql.append("SELECT ");
        sql.append("((100*SUM(IF(ie.stability_enum_id = 3, IFNULL(cie.total_income,0),0)))/SUM(IFNULL(cie.total_income,0))) AS clienthighstabilitypercentage ");
        sql.append(",((100*SUM(IF(ie.stability_enum_id = 2, IFNULL(cie.total_income,0),0)))/SUM(IFNULL(cie.total_income,0))) AS clientmediumstabilitypercentage ");
        sql.append(",((100*SUM(IF(ie.stability_enum_id = 1, IFNULL(cie.total_income,0),0)))/SUM(IFNULL(cie.total_income,0))) AS clientlowstabilitypercentage ");
        sql.append("FROM f_client_income_expense cie ");
        sql.append("JOIN f_income_expense ie ON ie.id = cie.income_expense_id ");
        sql.append("WHERE cie.client_id = ? ");
        return this.jdbcTemplate.queryForMap(sql.toString(), new Object[] { clientId });
    }

    private static final class ClientDataLayerMapper implements RowMapper<Map<String, Object>> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public ClientDataLayerMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(100);
            sqlBuilder.append(" ");
            sqlBuilder.append("DATE_FORMAT(FROM_DAYS(DATEDIFF(?,c.date_of_birth)), '%Y')+0 as age, ");
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
            sqlBuilder.append(" ").append(" ss.survey_id as surveyId, ").append(" sum(ss.a_value)  as scorecard ")
                    .append(" FROM m_survey_scorecards as ss JOIN f_survey_taken st ON st.id = ss.survey_taken_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
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

    private static final class LoanApplicationDataLayerMapper implements RowMapper<Map<String, Object>> {

        private static FromJsonHelper jsonHelper = new FromJsonHelper();

        private final String schemaSql;

        public LoanApplicationDataLayerMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(100);
            sqlBuilder.append(" l.loan_amount_requested as loanAmount");

            sqlBuilder.append(" from f_loan_application_reference as l ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Map<String, Object> mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            Map<String, Object> mapData = new HashMap();
            final BigDecimal amount = rs.getBigDecimal("loanAmount");
            mapData.put("loanAmount", amount);
            return mapData;
        }
    }
}