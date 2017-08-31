package com.finflux.risk.creditbureau.migration.service;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.migration.data.CreditBureauEnquiryData;
import com.finflux.risk.creditbureau.migration.data.LoanCreditBureauEnquiryData;
import com.google.gson.Gson;

@Service
public class CreditBureauMigrationServiceImpl implements CreditBureauMigrationService {

    private final JdbcTemplate jdbcTemplate;
    private final ContentRepositoryFactory contentRepositoryFactory;

    private final PaginationHelper<CreditBureauEnquiryData> creditBureauEnquiryDataPaginationHelper = new PaginationHelper<>();
    private final PaginationHelper<LoanCreditBureauEnquiryData> loanCreditBureauEnquiryDataPaginationHelper = new PaginationHelper<>();

    @Autowired
    public CreditBureauMigrationServiceImpl(final RoutingDataSource routingDataSource,
            final ContentRepositoryFactory contentRepositoryFactory) {
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Override
    public void updateCreditBureauEnquiry() {
        int limit = 1000;
        int offset = 0;
        int totalRecords = 0;
        StringBuilder updateBuilder = new StringBuilder();
        updateBuilder.append("UPDATE f_creditbureau_enquiry enquiry SET enquiry.request_location=? , ");
        updateBuilder.append("enquiry.response_location=?, enquiry.errors_json=? WHERE  enquiry.id=?");
        final String errorsJson = getErrorsJson();
        do {
            Page<CreditBureauEnquiryData> pageitems = getCreditBureauEnquiryData(offset, limit);
            totalRecords = pageitems.getTotalFilteredRecords();
            List<CreditBureauEnquiryData> enquiryDataList = pageitems.getPageItems();
            moveDataIntoFileSystem(enquiryDataList, updateBuilder.toString(), errorsJson);
            offset = offset + enquiryDataList.size();
        } while (offset < totalRecords);
    }

    private Page<CreditBureauEnquiryData> getCreditBureauEnquiryData(final Integer offset, final Integer limit) {
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        final StringBuilder builder = new StringBuilder();
        Object[] params = new Object[] {};
        CreditBureauEnquiryMapper mapper = new CreditBureauEnquiryMapper();
        builder.append("select SQL_CALC_FOUND_ROWS ");
        builder.append(mapper.query());
        builder.append(" where enquiry.request_location is null");
        builder.append(" limit " + limit);
        builder.append(" offset " + offset);
        return this.creditBureauEnquiryDataPaginationHelper.fetchPage(jdbcTemplate, sqlCountRows, builder.toString(), params, mapper);
    }

    private void moveDataIntoFileSystem(List<CreditBureauEnquiryData> list, final String query, final String errors) {
        List<Object[]> args = new ArrayList<>();
        for (CreditBureauEnquiryData enquiryData : list) {
            String requestLocation = null;
            String responseLocation = null;
            String errorsJson = "";
            if (StringUtils.isNotBlank(enquiryData.getRequest()) && StringUtils.isNotBlank(enquiryData.getResponse())) {
                requestLocation = saveContent(enquiryData.getRequest().getBytes(), enquiryData.getId());
                responseLocation = saveContent(enquiryData.getResponse().getBytes(), enquiryData.getId());
                if (enquiryData.getStatus().equals(11)) {
                    errorsJson = errors;
                }
                Object[] arg = new Object[] { requestLocation, responseLocation, errorsJson, enquiryData.getId() };
                args.add(arg);
            }
        }
        this.jdbcTemplate.batchUpdate(query, args);
    }

    private final String getErrorsJson() {
        String errorsJson = null;
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> errorsMap = new HashMap<>();
        errorsMap.put("code", "Migrated");
        errorsMap.put("description", "Migrated Data. Please contact customer support.");
        list.add(errorsMap);
        errorsJson = new Gson().toJson(list);
        return errorsJson;
    }

    @Override
    public void updateLoanCreditBureauEnquiry() {
        int limit = 1000;
        int offset = 0;
        int totalRecords = 0;
        StringBuilder updateBuilder = new StringBuilder();
        updateBuilder.append("UPDATE f_loan_creditbureau_enquiry enquiry SET enquiry.request_location=? , ");
        updateBuilder.append("enquiry.response_location=?, enquiry.report_location=? WHERE  enquiry.id=?");
        do {
            Page<LoanCreditBureauEnquiryData> pageitems = getLoanCreditBureauEnquiryData(offset, limit);
            totalRecords = pageitems.getTotalFilteredRecords();
            List<LoanCreditBureauEnquiryData> enquiryDataList = pageitems.getPageItems();
            moveLoanDataIntoFileSystem(enquiryDataList, updateBuilder.toString());
            offset = offset + enquiryDataList.size();
        } while (offset < totalRecords);
    }

    private void moveLoanDataIntoFileSystem(List<LoanCreditBureauEnquiryData> list, final String query) {
        List<Object[]> args = new ArrayList<>();
        for (LoanCreditBureauEnquiryData enquiryData : list) {
            String requestLocation = null;
            String responseLocation = null;
            String reportLocation = "";
            if (StringUtils.isNotBlank(enquiryData.getRequest()) && StringUtils.isNotBlank(enquiryData.getResponse())) {
                requestLocation = saveContent(enquiryData.getRequest().getBytes(), enquiryData.getId());
                responseLocation = saveContent(enquiryData.getResponse().getBytes(), enquiryData.getId());
                if (enquiryData.getReportData() != null) {
                    reportLocation = saveContent(enquiryData.getReportData(), enquiryData.getId());
                }
                Object[] arg = new Object[] { requestLocation, responseLocation, reportLocation, enquiryData.getId() };
                args.add(arg);
            }
        }
        this.jdbcTemplate.batchUpdate(query, args);
    }

    private Page<LoanCreditBureauEnquiryData> getLoanCreditBureauEnquiryData(final Integer offset, final Integer limit) {
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        final StringBuilder builder = new StringBuilder();
        Object[] params = new Object[] {};
        LoanCreditBureauEnquiryMapper mapper = new LoanCreditBureauEnquiryMapper();
        builder.append("select SQL_CALC_FOUND_ROWS ");
        builder.append(mapper.query());
        builder.append(" where enquiry.request_location is null");
        builder.append(" limit " + limit);
        builder.append(" offset " + offset);
        return this.loanCreditBureauEnquiryDataPaginationHelper.fetchPage(jdbcTemplate, sqlCountRows, builder.toString(), params, mapper);
    }

    private String saveContent(final byte[] data, final Long parentId) {
        final String entityType = "CREDIT_BUREAU";
        final ByteArrayInputStream contentInputStream = new ByteArrayInputStream(data);
        final String fileName = RandomStringUtils.randomAlphanumeric(15);
        final DocumentCommand documentCommand = new DocumentCommand(entityType, parentId, fileName, new Long(data.length));
        final String fileLocation = this.contentRepositoryFactory.getRepository().saveFile(contentInputStream, documentCommand);
        return fileLocation;
    }

    class CreditBureauEnquiryMapper implements RowMapper<CreditBureauEnquiryData> {

        final StringBuilder builder = new StringBuilder();

        CreditBureauEnquiryMapper() {
            builder.append(" enquiry.id, enquiry.request, enquiry.response, enquiry.status from f_creditbureau_enquiry enquiry ");
        }

        @Override
        public CreditBureauEnquiryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String request = rs.getString("request");
            final String response = rs.getString("response");
            final Integer status = rs.getInt("status");
            return new CreditBureauEnquiryData(id, request, response, status);
        }

        public String query() {
            return this.builder.toString();
        }
    }

    class LoanCreditBureauEnquiryMapper implements RowMapper<LoanCreditBureauEnquiryData> {

        final StringBuilder builder = new StringBuilder();

        LoanCreditBureauEnquiryMapper() {
            builder.append(
                    " enquiry.id, enquiry.request, enquiry.response, enquiry.file_content from f_loan_creditbureau_enquiry enquiry ");
        }

        @Override
        public LoanCreditBureauEnquiryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String request = rs.getString("request");
            final String response = rs.getString("response");
            final byte[] reportData = rs.getBytes("file_content");
            return new LoanCreditBureauEnquiryData(id, request, response, reportData);
        }

        public String query() {
            return this.builder.toString();
        }
    }
}
