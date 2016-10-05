package com.finflux.risk.creditbureau.provider.service;

import com.finflux.risk.creditbureau.provider.data.*;
import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;
import com.finflux.risk.creditbureau.provider.domain.LoanCreditBureauEnquiryMappingRepository;

import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
public class CreditBureauEnquiryReadServiceImpl implements CreditBureauEnquiryReadService {

    final EnquiryRequestDataExtractor enquiryRequestDataExtractor = new EnquiryRequestDataExtractor();
    final ClientAddressDataExtractor clientAddressDataExtractor = new ClientAddressDataExtractor();
    final ClientDocumentDataExtractor clientDocumentDataExtractor = new ClientDocumentDataExtractor();
    final ReportRequestDataExtractor reportRequestDataExtractor = new ReportRequestDataExtractor();
    final CreditBureauEnquiryBasicDataExtractor creditBureauEnquiryBasicDataExtractor = new CreditBureauEnquiryBasicDataExtractor();
    final LoanCreditBureauEnquiryDataExtractor loanCreditBureauEnquiryDataExtractor = new LoanCreditBureauEnquiryDataExtractor();
    final JdbcTemplate jdbcTemplate;
    final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;
    final LoanCreditBureauEnquiryMappingRepository loanCreditBureauEnquiryMappingRepository;

    @Autowired
    public CreditBureauEnquiryReadServiceImpl(final RoutingDataSource dataSource,
            final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService,
            final LoanCreditBureauEnquiryMappingRepository loanCreditBureauEnquiryMappingRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
        this.loanCreditBureauEnquiryMappingRepository = loanCreditBureauEnquiryMappingRepository;
    }

    @Override
    public List<LoanEnquiryData> getEnquiryRequestDataForAll() {
        String dataQuery = "SELECT mc.display_name AS clientName, mc.date_of_birth AS clientDOB, mc.office_id AS branchId,"
                + "mci.document_type_id AS clientIdentificationType,mci.document_key AS clientIdentification, "
                + "IFNULL(mc.mobile_no, IFNULL(ad.Mobile,'')) AS clientMobileNo, CONCAT(IFNULL(ad.`House No / Name`,''), "
                + "IFNULL(ad.`Cross/Street/Phase/Socity`,''), IFNULL(ad.`Village Name`,''), IFNULL(ad.Taluk,''), "
                + "IFNULL(ad.District,'')) AS clientAddress, IFNULL(ad.`Village Name`,'') AS clientCity,ad.State AS clientState,"
                + " IFNULL(ad.Pincode,'') AS clientPin,ml.id AS loanId,ml.approved_principal AS loanAmount,mc.id AS clientId "
                + "FROM m_loan ml INNER JOIN m_client mc ON mc.id = ml.client_id "
                + "LEFT JOIN ct_credit_req_client_details ccrd ON ml.id = ccrd.loan_id "
                + "LEFT JOIN ct_credit_request_response ccr ON ccr.id = ccrd.request_response_id "
                + "LEFT JOIN address ad ON mc.id = ad.client_id LEFT JOIN m_client_identifier mci ON mc.id = mci.client_id "
                + "WHERE mc.status_enum = 100 AND "
                + "mc.id NOT IN ( SELECT ccrd.client_id FROM ct_credit_req_client_details ccrd) GROUP BY mc.id";
        return this.jdbcTemplate.query(dataQuery, this.enquiryRequestDataExtractor, new Object[] {});
    }

    @Override
    public LoanEnquiryData getEnquiryRequestDataForLoan(Long loanId) {
        String dataQuery = "SELECT mc.display_name AS clientName, mc.firstname AS firstname, mc.middlename AS middlename, "
                + "mc.lastname AS lastname, mc.date_of_birth AS clientDOB, mc.office_id AS branchId, "
                + "mc.mobile_no AS clientMobileNo,  ml.product_id AS loanProductId,  ml.approved_principal AS loanAmount, "
                + " mc.id AS clientId  FROM m_loan ml  INNER JOIN m_client mc ON mc.id = ml.client_id "
                + " WHERE mc.status_enum = 300  AND ml.id = ?";
        LoanEnquiryData enquiryData = this.jdbcTemplate
                .queryForObject(dataQuery, this.enquiryRequestDataExtractor, new Object[] { loanId });
        return enquiryData;
    }

    @Override
    public List<EnquiryAddressData> getClientAddressData(Long clientId) {
        String dataQuery = "SELECT CONCAT(IFNULL(ad.house_no,''), ' ', IFNULL(ad.street_no,''),  ' ', "
                + "IFNULL(ad.village_town,''),  ' ', IFNULL(taluka.taluka_name,''),  ' ', " + "IFNULL(dist.district_name,'')) AS clientAddress, "
                + "IFNULL(ad.village_town,dist.district_name) AS clientCity,  state.state_name AS clientState, "
                + "state.iso_state_code As stateShortCode,  IFNULL(ad.postal_code,'') AS clientPin,  mc.id AS clientId "
                + "FROM m_client mc  LEFT JOIN f_address_entity ea ON ea.entity_id  = mc.id and ea.entity_type_enum = 1 "
                + "LEFT JOIN f_address ad ON ad.id = ea.address_id  LEFT JOIN f_district dist ON dist.id = ad.district_id "
                + "LEFT JOIN f_taluka taluka ON taluka.id = ad.taluka_id "
                + "LEFT JOIN f_state state on state.id = ad.state_id  WHERE mc.id = ? ";
        List<EnquiryAddressData> enquiryAddressDataList = this.jdbcTemplate.query(dataQuery, this.clientAddressDataExtractor,
                new Object[] { clientId });
        return enquiryAddressDataList;
    }

    @Override
    public List<EnquiryDocumentData> getClientDocumentData(Long clientId) {
        String dataQuery = "SELECT        mci.document_type_id AS clientIdentificationType, "
                + "mci.document_key AS clientIdentification  FROM m_client_identifier mci  WHERE mci.client_id = ?";
        List<EnquiryDocumentData> enquiryDocumentDataList = this.jdbcTemplate.query(dataQuery, this.clientDocumentDataExtractor,
                new Object[] { clientId });
        return enquiryDocumentDataList;
    }

    @Override
    public LoanEnquiryReferenceData getLatestCreditBureauEnquiryForLoan(Long loanId, Long creditBureauProductId) {
        String dataQuery = "SELECT  lce.id AS id,  lce.creditbureau_enquiry_id AS enquiryId,lce.client_id AS clientId, lce.loan_id AS loanId, "
                + " lce.loan_application_id AS loanApplicationId,  lce.reference_num AS refNumber, "
                + " lce.cb_report_id AS cbReportId,  ce.acknowledgement_num AS acknowledgementNumber, "
                + " ce.creditbureau_product_id AS cbProductId,  ce.created_date AS requestedDate,  lce.status AS status "
                + " FROM f_loan_creditbureau_enquiry lce "
                + " LEFT JOIN f_creditbureau_enquiry ce ON lce.creditbureau_enquiry_id  = ce.id  WHERE lce.loan_id = ? "
                + " AND ce.creditbureau_product_id = ? order by id desc limit 1";
        LoanEnquiryReferenceData loanEnquiryReferenceData = this.jdbcTemplate.queryForObject(dataQuery,
                this.loanCreditBureauEnquiryDataExtractor, new Object[] { loanId, creditBureauProductId });
        return loanEnquiryReferenceData;
    }

    @Override
    public LoanEnquiryReferenceData getLatestCreditBureauEnquiryForLoanApplicationReference(final Long loanApplicationId,
            final Long creditBureauProductId) {
        final StringBuilder sb = new StringBuilder(200);
        sb.append("SELECT lce.id AS id, lce.creditbureau_enquiry_id AS enquiryId, lce.client_id AS clientId, lce.loan_id AS loanId ");
        sb.append(",lce.loan_application_id AS loanApplicationId,  lce.reference_num AS refNumber ");
        sb.append(",lce.cb_report_id AS cbReportId,  ce.acknowledgement_num AS acknowledgementNumber ");
        sb.append(",ce.creditbureau_product_id AS cbProductId,  ce.created_date AS requestedDate, lce.status AS status ");
        sb.append("FROM f_loan_creditbureau_enquiry lce ");
        sb.append("LEFT JOIN f_creditbureau_enquiry ce ON lce.creditbureau_enquiry_id  = ce.id ");
        sb.append("WHERE lce.loan_application_id = ? AND ce.creditbureau_product_id = ? order by id desc limit 1 ");
        final List<LoanEnquiryReferenceData> l = this.jdbcTemplate.query(sb.toString(), this.loanCreditBureauEnquiryDataExtractor,
                new Object[] { loanApplicationId, creditBureauProductId });
        if (!l.isEmpty()) { return l.get(0); }
        return null;
    }

    @Override
    public EnquiryReferenceData getCreditBureauEnquiryBasicData(Long enquiryId) {
        String dataQuery = "SELECT  ce.id AS enquiryId,  ce.acknowledgement_num AS acknowledgementNumber, "
                + " ce.status AS status,  ce.type AS type  ce.created_date AS requestedDate "
                + " ce.creditbureau_product_id AS cbProductId  FROM f_creditbureau_enquiry ce  WHERE ce.id = ? ";
        EnquiryReferenceData enquiryReferenceData = this.jdbcTemplate.queryForObject(dataQuery, this.creditBureauEnquiryBasicDataExtractor,
                new Object[] { enquiryId });
        return enquiryReferenceData;
    }

    private static final class CreditBureauEnquiryBasicDataExtractor implements RowMapper<EnquiryReferenceData> {

        @Override
        public EnquiryReferenceData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long enquiryId = rs.getLong("enquiryId");
            final String acknowledgementNumber = rs.getString("acknowledgementNumber");
            final Integer status = rs.getInt("status");
            final String type = rs.getString("type");
            final Date requestedDate = rs.getDate("requestedDate");
            final Long cbProductId = rs.getLong("cbProductId");
            return new EnquiryReferenceData(enquiryId, acknowledgementNumber, CreditBureauEnquiryStatus.fromInt(status), type,
                    requestedDate, cbProductId);
        }
    }

    private static final class LoanCreditBureauEnquiryDataExtractor implements RowMapper<LoanEnquiryReferenceData> {

        @Override
        public LoanEnquiryReferenceData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long enquiryId = rs.getLong("enquiryId");
            final String refNumber = rs.getString("refNumber");
            final String cbReportId = rs.getString("cbReportId");
            final String acknowledgementNumber = rs.getString("acknowledgementNumber");
            final Integer status = rs.getInt("status");
            final Long cbProductId = rs.getLong("cbProductId");
            final Long clientId = rs.getLong("clientId");
            final Long loanId = rs.getLong("loanId");
            final Long loanApplicationId = rs.getLong("loanApplicationId");
            final Date requestedDate = rs.getDate("requestedDate");
            return new LoanEnquiryReferenceData(id, enquiryId, refNumber, clientId, loanId, loanApplicationId, cbReportId,
                    acknowledgementNumber, CreditBureauEnquiryStatus.fromInt(status), cbProductId, requestedDate);
        }
    }

    @Override
    public List<ReportRequestData> getReportRequestData() {
        String sql = "select distinct " + this.reportRequestDataExtractor.schema();
        return this.jdbcTemplate.query(sql, this.reportRequestDataExtractor, new Object[] {});
    }

    private static final class EnquiryRequestDataExtractor implements RowMapper<LoanEnquiryData> {

        @Override
        public LoanEnquiryData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final String clientName = rs.getString(ClientDataRequestConstants.clientName);
            final String clientFirstName = rs.getString(ClientDataRequestConstants.firstname);
            final String clientMiddleName = rs.getString(ClientDataRequestConstants.middlename);
            final String clientLastName = rs.getString(ClientDataRequestConstants.lastname);
            final Date clientDOB = rs.getDate(ClientDataRequestConstants.clientDOB);
            final String clientMobileNo = rs.getString(ClientDataRequestConstants.clientMobileNo);
            final Long loanProductId = rs.getLong(ClientDataRequestConstants.loanProductId);
            final BigDecimal loanAmount = rs.getBigDecimal(ClientDataRequestConstants.loanAmount);
            final Long clientId = rs.getLong(ClientDataRequestConstants.clientId);
            final Long branchId = rs.getLong(ClientDataRequestConstants.branchId);
            return new LoanEnquiryData(clientName, clientDOB, clientMobileNo, loanProductId, loanAmount, clientId, branchId,
                    clientFirstName, clientMiddleName, clientLastName);
        }

    }

    private static final class ClientAddressDataExtractor implements RowMapper<EnquiryAddressData> {

        @Override
        public EnquiryAddressData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            // final String clientAddressType =
            // rs.getString(ClientDataRequestConstants.clientAddressType);
            final String clientAddressType = "D04";
            final String clientAddress = rs.getString(ClientDataRequestConstants.clientAddress);
            final String clientCity = rs.getString(ClientDataRequestConstants.clientCity);
            final String clientStateCode = rs.getString(ClientDataRequestConstants.clientStateShortCode);
            final String clientPin = rs.getString(ClientDataRequestConstants.clientPin);
            return new EnquiryAddressData(clientAddressType, clientAddress, clientCity, clientStateCode, clientPin);
        }
    }

    private static final class ClientDocumentDataExtractor implements RowMapper<EnquiryDocumentData> {

        @Override
        public EnquiryDocumentData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final String clientIdentificationType = rs.getString(ClientDataRequestConstants.clientIdentificationType);
            final String clientIdentification = rs.getString(ClientDataRequestConstants.clientIdentification);
            return new EnquiryDocumentData(clientIdentificationType, clientIdentification);
        }
    }

    private static final class ReportRequestDataExtractor implements RowMapper<ReportRequestData> {

        private final String schema;

        public ReportRequestDataExtractor() {
            StringBuilder builder = new StringBuilder(400);
            builder.append("ccr.batch_id batchid , ccr.inquiry_ref_num inquiryno");
            builder.append(" from ct_credit_request_response ccr");
            builder.append(" where ccr.last_req_type = 'AT01' and ccr.status = 'ACKNOWLEDGEMENT' limit 1");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ReportRequestData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final String batchId = rs.getString("batchid");
            final String inquiryNumber = rs.getString("inquiryno");
            return new ReportRequestData(batchId, inquiryNumber);
        }
    }

    @Override
    public LoanEnquiryData getEnquiryRequestDataForLoanApplication(final Long loanApplicationId) {
        final StringBuilder sb = new StringBuilder(200);
        sb.append("SELECT mc.display_name AS clientName, mc.firstname AS firstname, mc.middlename AS middlename ");
        sb.append(",mc.lastname AS lastname, mc.date_of_birth AS clientDOB, mc.office_id AS branchId ");
        sb.append(",mc.mobile_no AS clientMobileNo, lar.loan_product_id AS loanProductId, lar.loan_amount_requested AS loanAmount,mc.id AS clientId ");
        sb.append("FROM f_loan_application_reference lar ");
        sb.append("INNER JOIN m_client mc ON mc.id = lar.client_id ");
        sb.append("WHERE mc.status_enum = 300 AND lar.id = ? ");
        return this.jdbcTemplate.queryForObject(sb.toString(), this.enquiryRequestDataExtractor, new Object[] { loanApplicationId });
    }

    @Override
    public void inActivePreviousLoanApplicationCreditbureauEnquiries(final Long loanApplicationId) {
        final String sql = "UPDATE f_loan_creditbureau_enquiry l SET l.is_active = '0' WHERE l.loan_application_id = " + loanApplicationId;
        this.jdbcTemplate.execute(sql);
    }
}