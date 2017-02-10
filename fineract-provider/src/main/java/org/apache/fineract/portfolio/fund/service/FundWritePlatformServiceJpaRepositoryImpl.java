/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.fund.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepository;
import org.apache.fineract.infrastructure.documentmanagement.exception.ContentManagementException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.data.FundDataValidator;
import org.apache.fineract.portfolio.fund.data.FundSearchQueryBuilder;
import org.apache.fineract.portfolio.fund.domain.Fund;
import org.apache.fineract.portfolio.fund.domain.FundLoanPurpose;
import org.apache.fineract.portfolio.fund.domain.FundMappingHistory;
import org.apache.fineract.portfolio.fund.domain.FundMappingHistoryRepository;
import org.apache.fineract.portfolio.fund.domain.FundMappingHistoryRepositoryWrapper;
import org.apache.fineract.portfolio.fund.domain.FundRepositoryWrapper;
import org.apache.fineract.portfolio.fund.exception.CsvDataException;
import org.apache.fineract.portfolio.fund.exception.FundNotFoundException;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.JsonElement;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

@Service
public class FundWritePlatformServiceJpaRepositoryImpl implements FundWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(FundWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final FundRepositoryWrapper fundRepositoryWrapper;
    private final FundDataValidator fundDataValidator;
    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;
    private final FromJsonHelper fromApiJsonHelper;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final DocumentRepository documentRepository;
    private final JdbcTemplate jdbcTemplate;
    private final FundMappingHistoryRepositoryWrapper fundMappingHistoryRepositoryWrapper;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final FundMappingQueryBuilderService fundMappingQueryBuilderService;
    private final FundMappingHistoryRepository fundMappingHistoryRepository;

    @Autowired
    public FundWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final FundRepositoryWrapper fundRepositoryWrapper, final FundDataValidator fundDataValidator,
            final CodeValueRepositoryWrapper codeValueRepositoryWrapper, final FromJsonHelper fromApiJsonHelper,
            final ContentRepositoryFactory contentRepositoryFactory, final DocumentRepository documentRepository,
            final RoutingDataSource dataSource, final FundMappingHistoryRepositoryWrapper fundMappingHistoryRepositoryWrapper,
            final LoanRepositoryWrapper loanRepositoryWrapper, final FundMappingQueryBuilderService fundMappingQueryBuilderService,
            final FundMappingHistoryRepository fundMappingHistoryRepository) {
        this.context = context;
        this.fundRepositoryWrapper = fundRepositoryWrapper;
        this.fundDataValidator = fundDataValidator;
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.documentRepository = documentRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fundMappingHistoryRepositoryWrapper = fundMappingHistoryRepositoryWrapper;
        this.fundMappingHistoryRepository = fundMappingHistoryRepository;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.fundMappingQueryBuilderService = fundMappingQueryBuilderService;
    }

    @Transactional
    @Override
    @CacheEvict(value = "funds", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('fn')")
    public CommandProcessingResult createFund(final JsonCommand command) {

        this.fundDataValidator.validate(command);

        CodeValue fundSource = null;
        final Long fundSourceId = command.longValueOfParameterNamed(FundApiConstants.fundSourceParamName);
        if (fundSourceId != null) {
            fundSource = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                    FundApiConstants.FUND_SOURCE_CODE_VALUE, fundSourceId);
        }
        boolean isNotOwn = true;
        boolean isActive = true;
        boolean isLoanAssigned = false;
        Fund fund = null;
        final String name = command.stringValueOfParameterNamed(FundApiConstants.nameParamName);

        CodeValue facilityType = null;
        final Long facilityTypeId = command.longValueOfParameterNamed(FundApiConstants.facilityTypeParamName);
        if (facilityTypeId != null) {
            facilityType = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                    FundApiConstants.FACILITY_TYPE_CODE_VALUE, facilityTypeId);
            if (facilityType != null && facilityType.label().equalsIgnoreCase(FundApiConstants.ownParam)) {
                isNotOwn = false;
            }
        }
        if (isNotOwn) {

            CodeValue fundCategory = null;
            final Long fundCategoryId = command.longValueOfParameterNamed(FundApiConstants.fundCategoryParamName);
            if (fundCategoryId != null) {
                fundCategory = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        FundApiConstants.CATEGORY_CODE_VALUE, fundCategoryId);
            }

            String externalId = null;
            if (command.parameterExists(FundApiConstants.externalIdParamName)) {
                externalId = command.stringValueOfParameterNamed(FundApiConstants.externalIdParamName);
            }

            final LocalDate assignmentStartDate = command.localDateValueOfParameterNamed(FundApiConstants.assignmentStartDateParamName);

            final LocalDate assignmentEndDate = command.localDateValueOfParameterNamed(FundApiConstants.assignmentEndDateParamName);

            final LocalDate sanctionedDate = command.localDateValueOfParameterNamed(FundApiConstants.sanctionedDateParamName);

            final BigDecimal sanctionedAmount = command.bigDecimalValueOfParameterNamed(FundApiConstants.sanctionedAmountParamName);

            final LocalDate disbursedDate = command.localDateValueOfParameterNamed(FundApiConstants.disbursedDateParamName);

            final BigDecimal disbursedAmount = command.bigDecimalValueOfParameterNamed(FundApiConstants.disbursedAmountParamName);

            final LocalDate maturityDate = command.localDateValueOfParameterNamed(FundApiConstants.maturityDateParamName);

            final BigDecimal interestRate = command.bigDecimalValueOfParameterNamed(FundApiConstants.interestRateParamName);

            CodeValue fundRepaymentFrequency = null;
            final Long fundRepaymentFrequencyId = command.longValueOfParameterNamed(FundApiConstants.fundRepaymentFrequencyParamName);
            if (fundRepaymentFrequencyId != null) {
                fundRepaymentFrequency = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        FundApiConstants.FUND_REPAYMENT_FREQUENCY_CODE_VALUE, fundRepaymentFrequencyId);
            }

            final Integer tenure = command.integerValueOfParameterNamed(FundApiConstants.tenureParamName);
            final Integer tenureFrequency = command.integerValueOfParameterNamed(FundApiConstants.tenureFrequencyParamName);

            Integer morotorium = null;
            Integer morotoriumFrequency = null;
            if (command.hasParameter(FundApiConstants.morotoriumParamName)
                    && command.hasParameter(FundApiConstants.morotoriumFrequencyParamName)) {
                morotorium = command.integerValueOfParameterNamed(FundApiConstants.morotoriumParamName);
                morotoriumFrequency = command.integerValueOfParameterNamed(FundApiConstants.morotoriumFrequencyParamName);
            }
            BigDecimal loanPortfolioFee = null;
            if (command.hasParameter(FundApiConstants.loanPortfolioFeeParamName)) {
                loanPortfolioFee = command.bigDecimalValueOfParameterNamed(FundApiConstants.loanPortfolioFeeParamName);
            }

            BigDecimal bookDebtHypothecation = null;
            if (command.hasParameter(FundApiConstants.bookDebtHypothecationParamName)) {
                bookDebtHypothecation = command.bigDecimalValueOfParameterNamed(FundApiConstants.bookDebtHypothecationParamName);
            }
            BigDecimal cashCollateral = null;
            if (command.hasParameter(FundApiConstants.cashCollateralParamName)) {
                cashCollateral = command.bigDecimalValueOfParameterNamed(FundApiConstants.cashCollateralParamName);
            }
            String personalGurantee = null;
            if (command.hasParameter(FundApiConstants.personalGuranteeParamName)) {
                personalGurantee = command.stringValueOfParameterNamed(FundApiConstants.personalGuranteeParamName);
            }
            List<FundLoanPurpose> fundLoanPurpose = fromJson(command);
            fund = Fund.instance(fundSource, fundCategory, facilityType, name, assignmentStartDate.toDate(), assignmentEndDate.toDate(),
                    sanctionedDate.toDate(), sanctionedAmount, disbursedDate.toDate(), disbursedAmount, maturityDate.toDate(),
                    interestRate, fundRepaymentFrequency, tenure, tenureFrequency, morotorium, morotoriumFrequency, loanPortfolioFee,
                    bookDebtHypothecation, cashCollateral, personalGurantee, isActive, isLoanAssigned, externalId, fundLoanPurpose);
        } else {
            fund = Fund.instance(name, facilityType, isActive, isLoanAssigned);

        }
        this.fundRepositoryWrapper.save(fund);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(fund.getId()) //
                .build();

    }

    public List<FundLoanPurpose> fromJson(final JsonCommand command) {
        List<FundLoanPurpose> fundLoanPurposeList = new ArrayList<>();
        JsonElement fundLoanPurpose = command.parsedJson().getAsJsonObject().get(FundApiConstants.fundLoanPurposeParamName);
        for (JsonElement fundloanPurposeData : fundLoanPurpose.getAsJsonArray()) {
            Integer loanPurposeId = this.fromApiJsonHelper.extractIntegerNamed("loanPurposeId", fundloanPurposeData, Locale.ENGLISH);
            CodeValue loanPurpose = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                    FundApiConstants.LOAN_PURPOSE_CODE_VALUE, loanPurposeId.longValue());
            BigDecimal loanPurposeAmount = this.fromApiJsonHelper.extractBigDecimalNamed("loanPurposeAmount", fundloanPurposeData,
                    Locale.ENGLISH);
            fundLoanPurposeList.add(FundLoanPurpose.instanceWithoutFund(loanPurpose, loanPurposeAmount));
        }
        return fundLoanPurposeList;
    }

    @Transactional
    @Override
    @CacheEvict(value = "funds", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('fn')")
    public CommandProcessingResult updateFund(final Long fundId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();
            Fund fund = this.fundRepositoryWrapper.findOneWithNotFoundDetection(fundId);
            this.fundDataValidator.validateForUpdate(command, fund);
            fund = update(fund, command);
            this.fundRepositoryWrapper.saveAndFlush(fund);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(fund.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleFundDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    public Fund update(Fund fund, JsonCommand command) {
        boolean isOwn = false;
        boolean isChangedFromOwn = false;
        final JsonElement element = command.parsedJson();

        if (command.isChangeInStringParameterNamed(FundApiConstants.nameParamName, fund.getName())) {
            final String name = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.nameParamName, element);
            fund.setName(name);
        }

        if (command.isChangeInBooleanParameterNamed(FundApiConstants.isActiveParamName, fund.isActive())) {
            final boolean isActive = this.fromApiJsonHelper.extractBooleanNamed(FundApiConstants.isActiveParamName, element);
            fund.setActive(isActive);
        }

        if (command.isChangeInLongParameterNamed(FundApiConstants.facilityTypeParamName, fund.getFacilityType().getId())) {
            final Long facilityTypeId = this.fromApiJsonHelper.extractLongNamed(FundApiConstants.facilityTypeParamName, element);
            CodeValue facilityType = this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                    FundApiConstants.FACILITY_TYPE_CODE_VALUE, facilityTypeId);
            if (facilityType != null && facilityType.label().equalsIgnoreCase("Own")) {
                isOwn = true;
            } else {
                if (fund.getFacilityType().label().equalsIgnoreCase("Own")) {
                    isChangedFromOwn = true;
                }
            }
            fund.setFacilityType(facilityType);
        } else {
            if (fund.getFacilityType().label().equalsIgnoreCase("Own")) {
                isOwn = true;
            }
        }
        if (!isOwn) {

            if (fund.getFundSource() == null
                    || command.isChangeInLongParameterNamed(FundApiConstants.fundSourceParamName, fund.getFundSource().getId())
                    || isChangedFromOwn) {
                final Long fundSourceId = this.fromApiJsonHelper.extractLongNamed(FundApiConstants.fundSourceParamName, element);
                fund.setFundSource(this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        FundApiConstants.FUND_SOURCE_CODE_VALUE, fundSourceId));
            }

            if (fund.getFundCategory() == null
                    || command.isChangeInLongParameterNamed(FundApiConstants.fundCategoryParamName, fund.getFundCategory().getId())
                    || isChangedFromOwn) {
                final Long fundCategoryId = this.fromApiJsonHelper.extractLongNamed(FundApiConstants.fundCategoryParamName, element);
                fund.setFundCategory(this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        FundApiConstants.CATEGORY_CODE_VALUE, fundCategoryId));
            }

            if (fund.getFundRepaymentFrequency() == null
                    || command.isChangeInLongParameterNamed(FundApiConstants.fundRepaymentFrequencyParamName, fund
                            .getFundRepaymentFrequency().getId()) || isChangedFromOwn) {
                final Long fundRepaymentFrequencyId = this.fromApiJsonHelper.extractLongNamed(
                        FundApiConstants.fundRepaymentFrequencyParamName, element);
                fund.setFundRepaymentFrequency(this.codeValueRepositoryWrapper.findOneByCodeNameAndIdWithNotFoundDetection(
                        FundApiConstants.FUND_REPAYMENT_FREQUENCY_CODE_VALUE, fundRepaymentFrequencyId));
            }

            if (command.isChangeInDateParameterNamed(FundApiConstants.assignmentStartDateParamName, fund.getAssignmentStartDate())
                    || isChangedFromOwn) {
                final LocalDate assignmentStartDate = this.fromApiJsonHelper.extractLocalDateNamed(
                        FundApiConstants.assignmentStartDateParamName, element);
                fund.setAssignmentStartDate(assignmentStartDate.toDate());
            }

            if (command.isChangeInDateParameterNamed(FundApiConstants.assignmentEndDateParamName, fund.getAssignmentEndDate())
                    || isChangedFromOwn) {
                final LocalDate assignmentEndDate = this.fromApiJsonHelper.extractLocalDateNamed(
                        FundApiConstants.assignmentEndDateParamName, element);
                fund.setAssignmentEndDate(assignmentEndDate.toDate());
            }

            if (command.isChangeInDateParameterNamed(FundApiConstants.sanctionedDateParamName, fund.getSanctionedDate())
                    || isChangedFromOwn) {
                final LocalDate sanctionedDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.sanctionedDateParamName,
                        element);
                fund.setSanctionedDate(sanctionedDate.toDate());
            }

            if (command.isChangeInDateParameterNamed(FundApiConstants.disbursedDateParamName, fund.getDisbursedDate()) || isChangedFromOwn) {
                final LocalDate disbursedDate = this.fromApiJsonHelper.extractLocalDateNamed(FundApiConstants.disbursedDateParamName,
                        element);
                fund.setDisbursedDate(disbursedDate.toDate());
            }

            if (command.isChangeInDateParameterNamed(FundApiConstants.maturityDateParamName, fund.getMaturityDate()) || isChangedFromOwn) {
                final LocalDate maturityDate = this.fromApiJsonHelper
                        .extractLocalDateNamed(FundApiConstants.maturityDateParamName, element);
                fund.setMaturityDate(maturityDate.toDate());
            }

            if (command.isChangeInBigDecimalParameterNamed(FundApiConstants.sanctionedAmountParamName, fund.getSanctionedAmount())
                    || isChangedFromOwn) {
                final BigDecimal sanctionedAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.sanctionedAmountParamName, element);
                fund.setSanctionedAmount(sanctionedAmount);
            }

            if (command.isChangeInBigDecimalParameterNamed(FundApiConstants.disbursedAmountParamName, fund.getDisbursedAmount())
                    || isChangedFromOwn) {
                final BigDecimal disbursedAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.disbursedAmountParamName, element);
                fund.setDisbursedAmount(disbursedAmount);
            }

            if (command.isChangeInBigDecimalParameterNamed(FundApiConstants.interestRateParamName, fund.getInterestRate())
                    || isChangedFromOwn) {
                final BigDecimal interestRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.interestRateParamName, element);
                fund.setInterestRate(interestRate);
                ;
            }

            if (command.isChangeInIntegerParameterNamed(FundApiConstants.tenureParamName, fund.getTenure()) || isChangedFromOwn) {
                final Integer tenure = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.tenureParamName, element);
                fund.setTenure(tenure);
            }
            if (command.isChangeInIntegerParameterNamed(FundApiConstants.tenureFrequencyParamName, fund.getTenureFrequency())
                    || isChangedFromOwn) {
                final Integer tenureFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                        FundApiConstants.tenureFrequencyParamName, element);
                fund.setTenureFrequency(tenureFrequency);
            }

            if (command.isChangeInBigDecimalParameterNamed(FundApiConstants.loanPortfolioFeeParamName, fund.getLoanPortfolioFee())) {
                final BigDecimal loanPortfolioFee = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.loanPortfolioFeeParamName, element);
                fund.setLoanPortfolioFee(loanPortfolioFee);
            }

            if (command
                    .isChangeInBigDecimalParameterNamed(FundApiConstants.bookDebtHypothecationParamName, fund.getBookDebtHypothecation())) {
                final BigDecimal bookDebtHypothecation = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.bookDebtHypothecationParamName, element);
                fund.setBookDebtHypothecation(bookDebtHypothecation);
            }

            if (command.isChangeInBigDecimalParameterNamed(FundApiConstants.cashCollateralParamName, fund.getCashCollateral())) {
                final BigDecimal cashCollateral = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        FundApiConstants.cashCollateralParamName, element);
                fund.setCashCollateral(cashCollateral);
            }
            if (command.isChangeInStringParameterNamed(FundApiConstants.personalGuranteeParamName, fund.getPersonalGurantee())) {
                final String personalGurantee = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.personalGuranteeParamName,
                        element);
                fund.setPersonalGurantee(personalGurantee);
            }

            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.morotoriumFrequencyParamName, element)) {
                if (command.isChangeInIntegerParameterNamed(FundApiConstants.morotoriumFrequencyParamName, fund.getMorotoriumFrequency())) {
                    final Integer morotoriumFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(
                            FundApiConstants.morotoriumFrequencyParamName, element);
                    fund.setMorotoriumFrequency(morotoriumFrequency);
                }
            }
            if (this.fromApiJsonHelper.parameterExists(FundApiConstants.morotoriumParamName, element)) {
                if (command.isChangeInIntegerParameterNamed(FundApiConstants.morotoriumParamName, fund.getMorotorium())) {
                    final Integer morotorium = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(FundApiConstants.morotoriumParamName,
                            element);
                    fund.setMorotorium(morotorium);
                }
            }
            List<FundLoanPurpose> fundLoanPurpose = fromJson(command);
            fund.setFundLoanPurpose(fundLoanPurpose);
        } else {
            Fund.updateFromOwn(fund);
        }
        return fund;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleFundDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("fund_externalid_org")) {
            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.fund.duplicate.externalId", "A fund with external id '" + externalId
                    + "' already exists", "externalId", externalId);
        } else if (realCause.getMessage().contains("fund_name_org")) {
            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.fund.duplicate.name", "A fund with name '" + name + "' already exists",
                    "name", name);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.fund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public CommandProcessingResult assignFundFromCSV(FormDataMultiPart formParams) {
        this.context.authenticatedUser();
        Long fundId = null;
        if (formParams.getFields().containsKey(FundApiConstants.fundParamName)) {
            String id = formParams.getField(FundApiConstants.fundParamName).getValue();
            if (StringUtils.isNotBlank(id) && StringUtils.isNumeric(id)) {
                fundId = Long.valueOf(id);
                this.fundRepositoryWrapper.findOneWithNotFoundDetection(fundId);
            } else {
                throw new FundNotFoundException(fundId);
            }
        }
        Document document = getDocument(formParams);
        this.documentRepository.delete(document);
        Map<String, Object> dataMap = readCSV(document);
        Set<Long> loanIds = (Set<Long>) dataMap.get("loanIds");
        Set<Integer> errorList = (Set<Integer>) dataMap.get("errorList");
        if (errorList.isEmpty()) {
            assignLoan(loanIds, fundId);
        } else {
            throw new CsvDataException(errorList.toString());
        }
        return new CommandProcessingResultBuilder() //
                .withEntityId(fundId) //
                .build();
    }

    private Document getDocument(FormDataMultiPart formParams) {
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final Long fileSize = new Long(formParams.getField(FundApiConstants.csvFileSize).getValue());
        FormDataBodyPart bodyPart = formParams.getField(FundApiConstants.FIRST_FILE);
        InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        String fileName = bodyPart.getFormDataContentDisposition().getFileName();
        String description = fileName;
        String name = fileName;
        final DocumentCommand documentCommand = new DocumentCommand(null, null, FundApiConstants.entityName,
                FundApiConstants.fundMappingFolder, name, fileName, fileSize, bodyPart.getMediaType().toString(), description, null);
        final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);
        final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());

        this.documentRepository.save(document);
        return document;
    }

    public Map<String, Object> readCSV(Document document) {
        final File file = new File(document.getLocation());
        FileReader fileReader;
        char seperator = ',';
        char quotes = '"';
        int biginingIndex = 1;
        Map<String, Object> dataMap = new HashMap<>();
        Set<Long> loanIds = new HashSet<>();
        Set<Integer> errorList = new HashSet<>();
        try {
            fileReader = new FileReader(file);
            CSVReader reader = new CSVReader(fileReader, seperator, quotes, biginingIndex);
            int count = biginingIndex;
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                count++;
                String firstColumn = nextLine[0];
                if (StringUtils.isNotBlank(firstColumn) && StringUtils.isNumeric(firstColumn)) {
                    loanIds.add(Long.valueOf(nextLine[0]));
                } else {
                    errorList.add(count);
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new ContentManagementException(file.getName(),"unknow.issue");
        }
        dataMap.put("loanIds", loanIds);
        dataMap.put("errorList", errorList);
        return dataMap;
    }

    private void assignLoan(Set<Long> loanIds, Long fundId) {
        Fund fund = this.fundRepositoryWrapper.findOneWithNotFoundDetection(fundId);
        fund.setLoanAssigned(true);
        List<FundMappingHistory> fundMappingHistoryList = new ArrayList<>();
        for (Long loanId : loanIds) {
            Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
            if (loan.getFund() == null
                    || !(loan.getFund().getFacilityType().label().equalsIgnoreCase(FundApiConstants.buyoutParam) || loan.getFund()
                            .getFacilityType().label().equalsIgnoreCase(FundApiConstants.securitizationParam))) {
                loan.updateFund(fund);
                List<FundMappingHistory> historyList = this.fundMappingHistoryRepository.findPreviousHistoryByLoan(loanId);
                FundMappingHistory previousFundMappingHistory = null;
                if(historyList != null && !historyList.isEmpty()){
                    previousFundMappingHistory = historyList.get(0);
                }
                if (previousFundMappingHistory != null) {
                    previousFundMappingHistory.setAssignmentEndDate(new Date());
                    fundMappingHistoryList.add(previousFundMappingHistory);
                }
                fundMappingHistoryList.add(FundMappingHistory.instance(loan, fund, this.context.authenticatedUser(), new Date(),
                        fund.getAssignmentEndDate()));
                ;
            }
        }
        this.fundMappingHistoryRepositoryWrapper.save(fundMappingHistoryList);
    }

    @Override
    public CommandProcessingResult assignFund(Long fundId, JsonCommand command) {
        FundSearchQueryBuilder fundSearchQueryBuilder = this.fundMappingQueryBuilderService.getQuery(command.json(), false);
        List<Long> loanIds = this.jdbcTemplate.queryForList(fundSearchQueryBuilder.getQueryBuilder().toString(), Long.class);
        assignLoan(new HashSet<>(loanIds), fundId);
        return new CommandProcessingResultBuilder() //
                .withEntityId(fundId) //
                .build();
    }

    @Override
    public CommandProcessingResult activateFund(Long fundId) {
        Fund fund = this.fundRepositoryWrapper.findOneWithNotFoundDetection(fundId);
        fund.setActive(true);
        this.fundRepositoryWrapper.save(fund);
        return new CommandProcessingResultBuilder() //
                .withEntityId(fundId) //
                .build();
    }

    @Override
    public CommandProcessingResult deactivateFund(Long fundId) {
        Fund fund = this.fundRepositoryWrapper.findOneWithNotFoundDetection(fundId);
        fund.setActive(false);
        this.fundRepositoryWrapper.save(fund);
        return new CommandProcessingResultBuilder() //
                .withEntityId(fundId) //
                .build();
    }

}