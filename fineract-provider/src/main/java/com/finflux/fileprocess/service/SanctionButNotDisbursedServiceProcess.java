package com.finflux.fileprocess.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.finflux.common.util.FinfluxDocumentConverterUtils;
import com.finflux.common.util.FinfluxParseDataUtils;
import com.finflux.fileprocess.data.FileRecordsStatus;
import com.finflux.fileprocess.domain.FileProcess;
import com.finflux.fileprocess.domain.FileProcessRepositoryWrapper;
import com.finflux.fileprocess.domain.FileRecords;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceStatus;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepositoryWrapper;
import com.finflux.loanapplicationreference.service.LoanApplicationReferenceWritePlatformService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class SanctionButNotDisbursedServiceProcess {

    private final FromJsonHelper fromJsonHelper;
    private final JdbcTemplate jdbcTemplate;
    private final LoanApplicationReferenceWritePlatformService loanApplicationReferenceWritePlatformService;
    private final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final FileProcessRepositoryWrapper fileProcessRepository;

    @SuppressWarnings("rawtypes")
    @Autowired
    public SanctionButNotDisbursedServiceProcess(final RoutingDataSource dataSource, final FromJsonHelper fromJsonHelper,
            final LoanApplicationReferenceWritePlatformService loanApplicationReferenceWritePlatformService,
            final LoanApplicationReferenceRepositoryWrapper loanApplicationReferenceRepository,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final FromJsonHelper fromApiJsonHelper,
            final FileProcessRepositoryWrapper fileProcessRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fromJsonHelper = fromJsonHelper;
        this.loanApplicationReferenceWritePlatformService = loanApplicationReferenceWritePlatformService;
        this.loanApplicationReferenceRepository = loanApplicationReferenceRepository;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fileProcessRepository = fileProcessRepository;
    }

    @Transactional
    public void fileRecordsProcess(final FileProcess fileProcess, final FileRecords fileRecords) {
        JsonObject jsonObject = null;
        String sheetName = null;
        try {
            final String content = fileRecords.getContent();
            jsonObject = this.fromJsonHelper.parse(content).getAsJsonObject();
            sheetName = jsonObject.get("sheetName").getAsString();
            jsonObject.addProperty("locale", "en");
            jsonObject.addProperty("dateFormat", "dd MMMM yyyy");
            if (sheetName != null) {
                if (sheetName.equalsIgnoreCase("Sanctioned Not Disbursed")) {
                    validateSanctionRecord(jsonObject.toString());
                    sanction(jsonObject, fileRecords);
                } else if (sheetName.equalsIgnoreCase("Rejected")) {
                    reject(jsonObject);
                }
            }
            fileProcess.incrementTotalSuccessRecords();
            fileRecords.setStatus(FileRecordsStatus.SUCCESS.getValue());
        } catch (final Exception e) {
            if (jsonObject != null) {
                final int rowIndex = jsonObject.get("rowIndex").getAsInt();
                final String errorMsg = FinfluxParseDataUtils.getErrorMessage(e);
                final String filePath = fileProcess.getFilePath();
                final File file = new File(filePath);
                final Map<String, String> map = new HashMap<>();
                map.put("Error Message", errorMsg);
                FinfluxDocumentConverterUtils.writeContentToExcelWorksheet(rowIndex, file, map, sheetName);
            }
            fileProcess.incrementTotalFailureRecords();
            fileRecords.setStatus(FileRecordsStatus.FAILED.getValue());
        }
        this.fileProcessRepository.saveAndFlush(fileProcess);
    }

    private void validateSanctionRecord(final String json) {
        final JsonElement element = this.fromJsonHelper.parse(json);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("snd");
        final String externalId = this.fromJsonHelper.extractStringNamed("LAF Barcode No.", element);
        baseDataValidator.reset().parameter("externalId").value(externalId).notBlank().notExceedingLengthOf(100);
        final String documentKey = this.fromJsonHelper.extractStringNamed("External Cust No", element);
        baseDataValidator.reset().parameter("documentKey").value(documentKey).notBlank().notExceedingLengthOf(50);
        final String customerName = this.fromJsonHelper.extractStringNamed("Customer Name", element);
        baseDataValidator.reset().parameter("customerName").value(customerName).notBlank().notExceedingLengthOf(100);
        final BigDecimal sanctionedAmount = this.fromJsonHelper.extractBigDecimalWithLocaleNamed("Sanctioned Amount", element);
        baseDataValidator.reset().parameter("sanctionedAmount").value(sanctionedAmount).notBlank().positiveAmount();
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void sanction(final JsonObject jsonObject, final FileRecords fileRecords) {
        final Long loanApplicationReferenceId = getLoanApplicationReferenceId(jsonObject);
        JsonCommand command = null;
        this.loanApplicationReferenceWritePlatformService.requestForApproval(loanApplicationReferenceId, command);
        final BigDecimal sanctionedAmount = jsonObject.get("Sanctioned Amount").getAsBigDecimal();
        final LoanApplicationReference reference = this.loanApplicationReferenceRepository
                .findOneWithNotFoundDetection(loanApplicationReferenceId);
        final LoanApprovalData approvalData = new LoanApprovalData(sanctionedAmount, fileRecords.getCreatedDate(), reference);
        final String json = this.toApiJsonSerializer.serialize(approvalData);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);
        command = JsonCommand.from(json, parsedCommand, this.fromApiJsonHelper, null, null, null, null, null, null, null, null, null, null,
                null, null);
        this.loanApplicationReferenceWritePlatformService.approve(loanApplicationReferenceId, command);
    }

    private void reject(final JsonObject jsonObject) {
        final Long loanApplicationReferenceId = getLoanApplicationReferenceId(jsonObject);
        JsonCommand command = null;
        this.loanApplicationReferenceWritePlatformService.requestForApproval(loanApplicationReferenceId, command);
    }

    private Long getLoanApplicationReferenceId(final JsonObject jsonObject) {
        try {
            final String externalId = jsonObject.get("LAF Barcode No.").getAsString();
            final StringBuilder sb = new StringBuilder(100);
            sb.append("select lar.id as loanAppId from f_loan_application_reference lar join m_client c on c.id = lar.client_id ");
            sb.append("where lar.status_enum = ").append(LoanApplicationReferenceStatus.APPLICATION_CREATED.getValue());
            sb.append(" and c.external_id = '").append(externalId).append("' ");
            return this.jdbcTemplate.queryForObject(sb.toString(), Long.class);
        } catch (EmptyResultDataAccessException e) {
            throw new GeneralPlatformDomainRuleException("error.msg.record.not.found", "Record not found");
        }
    }

    class LoanApprovalData {

        private DateFormat dateFormatter2 = new SimpleDateFormat("dd MMMM yyyy");
        Map<String, Object> formValidationData = new HashMap<>();
        Map<String, Object> formRequestData = new HashMap<>();

        LoanApprovalData(final BigDecimal approvedAmount, Date sanctionedDate, final LoanApplicationReference reference) {

            String formattedSanctionDate = dateFormatter2.format(sanctionedDate);

            formValidationData.put("submittedOnDate", dateFormatter2.format(reference.getSubmittedOnDate()));// Format
            formValidationData.put("clientId", reference.getClient().getId());
            formValidationData.put("productId", reference.getLoanProduct().getId());
            formValidationData.put("principal", reference.getLoanAmountRequested());
            formValidationData.put("loanTermFrequency", reference.getTermFrequency());
            formValidationData.put("loanTermFrequencyType", reference.getTermPeriodFrequencyEnum());
            formValidationData.put("numberOfRepayments", reference.getNumberOfRepayments());
            formValidationData.put("repaymentEvery", reference.getRepayEvery());
            formValidationData.put("repaymentFrequencyType", reference.getRepaymentPeriodFrequencyEnum());
            // formValidationData.put("interestRatePerPeriod", reference.geloan)
            // ;//
            formValidationData.put("expectedDisbursementDate", formattedSanctionDate);
            formValidationData.put("repaymentsStartingFromDate", formattedSanctionDate);

            formRequestData.put("loanAmountApproved", approvedAmount);
            formRequestData.put("expectedDisbursementDate", formattedSanctionDate);
            formRequestData.put("approvedOnDate", formattedSanctionDate);
            formRequestData.put("repaymentsStartingFromDate", formattedSanctionDate);
            formRequestData.put("numberOfRepayments", reference.getNumberOfRepayments());
            formRequestData.put("repayEvery", reference.getRepayEvery());
            formRequestData.put("termFrequency", reference.getTermFrequency());
            formRequestData.put("repaymentPeriodFrequencyEnum", reference.getRepaymentPeriodFrequencyEnum());
            formRequestData.put("termPeriodFrequencyEnum", reference.getTermPeriodFrequencyEnum());
        }
    }

}
