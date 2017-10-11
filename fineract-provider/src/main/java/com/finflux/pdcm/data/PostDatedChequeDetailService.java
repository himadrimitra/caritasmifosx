package com.finflux.pdcm.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformMultipleDomainValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.JsonParserHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.finflux.pdcm.constants.ChequeStatus;
import com.finflux.pdcm.constants.ChequeType;
import com.finflux.pdcm.constants.PostDatedChequeDetailApiConstants;
import com.finflux.pdcm.domain.PostDatedChequeDetail;
import com.finflux.pdcm.domain.PostDatedChequeDetailMapping;
import com.finflux.pdcm.domain.PostDatedChequeDetailMappingRepository;
import com.finflux.pdcm.domain.PostDatedChequeDetailsRepositoryWrapper;
import com.finflux.pdcm.exception.PostDatedChequeDetailDeleteException;
import com.finflux.pdcm.exception.PostDatedChequeDetailStatusException;
import com.finflux.pdcm.service.PDCSearchParameters;
import com.finflux.portfolio.loan.mandate.domain.MandateStatusEnum;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.InvalidJsonException;

@Component
public class PostDatedChequeDetailService {

    private final FromJsonHelper fromApiJsonHelper;
    private final PostDatedChequeDetailsRepositoryWrapper postDatedChequeDetailsRepository;
    private final PostDatedChequeDetailMappingRepository mappingRepository;
    private final LoanWritePlatformService loanWritePlatformService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostDatedChequeDetailService(final FromJsonHelper fromApiJsonHelper,
            final PostDatedChequeDetailsRepositoryWrapper postDatedChequeDetailsRepository,
            final PostDatedChequeDetailMappingRepository mappingRepository, final LoanWritePlatformService loanWritePlatformService,
            final RoutingDataSource dataSource) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.postDatedChequeDetailsRepository = postDatedChequeDetailsRepository;
        this.mappingRepository = mappingRepository;
        this.loanWritePlatformService = loanWritePlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Validating requested data or json
     * 
     * @param entityType
     * @param entityId
     * @param json
     */
    public List<PostDatedChequeDetail> validateAndCreate(final Integer entityType, final Long entityId, final String json) {

        validatePDCActionsAllowedOrNot(entityType, entityId);

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PostDatedChequeDetailApiConstants.CREATE_REQUEST_DATA_PARAMETERS);

        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(parentElement.getAsJsonObject());

        final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(parentElement.getAsJsonObject());

        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.entityIdParamName).value(entityId).notBlank()
                .longGreaterThanZero();

        if (parentElement.isJsonObject()
                && !this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.pdcDetailsParamName, parentElement)) {
            validateAndAssembleEachJsonObjectForCreatePDC(postDatedChequeDetails, entityType, entityId, locale, dateFormat,
                    parentElement.getAsJsonObject(), baseDataValidator, dataValidationErrors);
        } else if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.pdcDetailsParamName, parentElement)) {
            final JsonArray array = parentElement.getAsJsonObject().get(PostDatedChequeDetailApiConstants.pdcDetailsParamName)
                    .getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    validateAndAssembleEachJsonObjectForCreatePDC(postDatedChequeDetails, entityType, entityId, locale, dateFormat,
                            element, baseDataValidator, dataValidationErrors);
                }
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        validateCreateForDuplicateChequeNumber(postDatedChequeDetails);
        return postDatedChequeDetails;
    }

    private void validatePDCActionsAllowedOrNot(final Integer entityType, final Long entityId) {
        final EntityType entity = EntityType.fromInt(entityType);
        if (entity.isLoan()) {
            final String sql = "select COUNT(lm.id) from f_loan_mandates lm where lm.loan_id = ? and lm.mandate_status_enum = ?";
            final List<Object> params = new ArrayList<>();
            params.add(entityId);
            params.add(MandateStatusEnum.ACTIVE.getValue());
            final Long count = this.jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
            if (count != null && count > 0) {
                final String globalisationMessageCode = "error.msg.pdc.actions.not.allowed.for.nach.linked.with.loan.account";
                final String defaultUserMessage = "PDC actions are not allowed for NACH linked with loan account";
                throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, entityId);
            }
        }
    }

    private void validateCreateForDuplicateChequeNumber(final List<PostDatedChequeDetail> postDatedChequeDetails) {
        final List<String> allChequeNumbers = new ArrayList<>();
        final List<String> duplicatedChequeNumbers = new ArrayList<>();
        for (int i = 0; i < postDatedChequeDetails.size(); i++) {
            final PostDatedChequeDetail pdc = postDatedChequeDetails.get(i);
            if (i > 0) {
                final PostDatedChequeDetail postDatedChequeDetail = postDatedChequeDetails.get(i - 1);
                if (isChequeNumberDuplicated(allChequeNumbers, pdc, postDatedChequeDetail)) {
                    duplicatedChequeNumbers.add(pdc.getChequeNumber());
                }
            }
            allChequeNumbers.add(pdc.getChequeNumber());
            validateAgainstBasedOnExistingChequeNumbers(pdc, allChequeNumbers, duplicatedChequeNumbers);
        }
    }

    /**
     * This method only used for validating each request json object data
     * 
     * @param postDatedChequeDetails
     * @param entityType
     * @param entityId
     * @param locale
     * @param dateFormat
     * @param element
     * @param baseDataValidator
     * @param dataValidationErrors
     */
    @SuppressWarnings({ "unused", "null" })
    private void validateAndAssembleEachJsonObjectForCreatePDC(final List<PostDatedChequeDetail> postDatedChequeDetails,
            final Integer entityType, final Long entityId, final Locale locale, final String dateFormat, final JsonObject element,
            final DataValidatorBuilder baseDataValidator, final List<ApiParameterError> dataValidationErrors) {

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, element.toString(),
                PostDatedChequeDetailApiConstants.CREATE_EACH_OBJECT_REQUEST_DATA_PARAMETERS);

        final String bankName = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.bankNameParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.bankNameParamName).value(bankName).notBlank()
                .notExceedingLengthOf(100);

        final String branchName = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.branchNameParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.branchNameParamName).value(branchName).notBlank()
                .notExceedingLengthOf(100);

        final String accountNumber = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.accountNumberParamName,
                element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.accountNumberParamName).value(accountNumber).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String ifscCode = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.ifscCodeParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.ifscCodeParamName).value(ifscCode).notBlank()
                .notExceedingLengthOf(50);

        final Integer chequeType = this.fromApiJsonHelper.extractIntegerNamed(PostDatedChequeDetailApiConstants.chequeTypeParamName,
                element, locale);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeTypeParamName).value(chequeType).notBlank()
                .integerGreaterThanZero().isOneOfTheseValues(ChequeType.integerValues());

        final Integer numberOfPDC = this.fromApiJsonHelper.extractIntegerNamed(PostDatedChequeDetailApiConstants.numberOfPDCParamName,
                element, locale);

        final String[] chequeNumbers = this.fromApiJsonHelper.extractArrayNamed(PostDatedChequeDetailApiConstants.chequeNumbersParamName,
                element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeNumbersParamName).value(chequeNumbers).notBlank()
                .arrayNotEmpty();

        if (chequeNumbers != null && chequeNumbers.length > 0) {
            for (final String chequeNumber : chequeNumbers) {
                baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeNumbersParamName).value(chequeNumber)
                        .notBlank().notExceedingLengthOf(30).matches(PostDatedChequeDetailApiConstants.chequeNumberPattern);
            }
        }

        final String[] chequeDates = this.fromApiJsonHelper.extractArrayNamed(PostDatedChequeDetailApiConstants.chequeDatesParamName,
                element);

        if (chequeDates != null && chequeDates.length > 0) {
            for (final String chequeDate : chequeDates) {
                final LocalDate date = JsonParserHelper.convertFrom(chequeDate, PostDatedChequeDetailApiConstants.chequeDatesParamName,
                        dateFormat, locale);
                if (date == null) {
                    baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeDatesParamName).value(chequeDate)
                            .failWithCodeNoParameterAddedToErrorCode(PostDatedChequeDetailApiConstants.ERROR_CODE_INVALID_CHEQUE_DATE);
                }
            }
        }

        final Integer paymentType = this.fromApiJsonHelper.extractIntegerNamed(PostDatedChequeDetailApiConstants.paymentTypeParamName,
                element, locale);

        final Boolean isIncrementChequeNumber = this.fromApiJsonHelper.extractBooleanNamed(
                PostDatedChequeDetailApiConstants.isIncrementChequeNumberParamName, element);

        if (isIncrementChequeNumber != null && isIncrementChequeNumber) {
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.numberOfPDCParamName).value(numberOfPDC).notBlank()
                    .integerGreaterThanZero();
            if (numberOfPDC != null && chequeDates != null && numberOfPDC != chequeDates.length) {
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                        PostDatedChequeDetailApiConstants.ERROR_CODE_CHEQUE_NUMBERS_AND_DATES_ARRAY_SIZE_NOT_EQUAL);
            }
        } else {
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.numberOfPDCParamName).value(numberOfPDC).ignoreIfNull()
                    .integerGreaterThanZero();
            if (chequeNumbers != null && chequeDates != null && chequeNumbers.length != chequeDates.length) {
                baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                        PostDatedChequeDetailApiConstants.ERROR_CODE_CHEQUE_NUMBERS_AND_DATES_ARRAY_SIZE_NOT_EQUAL);
            }
        }

        final String[] amounts = this.fromApiJsonHelper.extractArrayNamed(PostDatedChequeDetailApiConstants.amountsParamName, element);

        if (amounts != null && chequeDates != null && amounts.length != chequeDates.length) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode(
                    PostDatedChequeDetailApiConstants.ERROR_CODE_CHEQUE_DATES_AND_AMOUNTS_ARRAY_SIZE_NOT_EQUAL);
        }

        if (amounts != null && amounts.length > 0) {
            for (final String amt : amounts) {
                final BigDecimal amount = new BigDecimal(amt);
                if (amount == null) {
                    baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.amountsParamName).value(amount)
                            .failWithCodeNoParameterAddedToErrorCode(PostDatedChequeDetailApiConstants.ERROR_CODE_INVALID_AMOUNT);
                }
            }
        }

        if (ChequeType.fromInt(chequeType).isRepaymentPDC()) {

            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeDatesParamName).value(chequeDates).notBlank()
                    .arrayNotEmpty();

            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.paymentTypeParamName).value(paymentType).notBlank()
                    .integerGreaterThanZero();

            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.amountsParamName).value(amounts).notBlank()
                    .arrayNotEmpty();
        } else {

            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeDatesParamName).value(chequeDates).ignoreIfNull()
                    .arrayNotEmpty();

            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.paymentTypeParamName).value(paymentType).ignoreIfNull()
                    .integerGreaterThanZero();

            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.amountsParamName).value(amounts).ignoreIfNull()
                    .arrayNotEmpty();
        }

        if (dataValidationErrors.isEmpty()) {
            assembleEachJsonObjectForCreatePDC(postDatedChequeDetails, bankName, branchName, accountNumber, ifscCode, amounts, chequeType,
                    chequeNumbers, chequeDates, numberOfPDC, isIncrementChequeNumber, dateFormat, locale, paymentType, entityType, entityId);
        }
    }

    /**
     * This method only assemble the {{PostDatedChequeDetail}}
     * 
     * @param postDatedChequeDetails
     * @param bankName
     * @param branchName
     * @param accountNumber
     * @param ifscCode
     * @param amount
     * @param chequeType
     * @param chequeNumbers
     * @param chequeDates
     * @param numberOfPDC
     * @param isIncrementChequeNumber
     * @param dateFormat
     * @param locale
     * @param paymentType
     * @param entityType
     * @param entityId
     */
    private void assembleEachJsonObjectForCreatePDC(final List<PostDatedChequeDetail> postDatedChequeDetails, final String bankName,
            final String branchName, final String accountNumber, final String ifscCode, final String[] amounts, final Integer chequeType,
            final String[] chequeNumbers, final String[] chequeDates, final Integer numberOfPDC, final Boolean isIncrementChequeNumber,
            final String dateFormat, final Locale locale, final Integer paymentType, final Integer entityType, final Long entityId) {
        final List<String> chequeNumbersList = new ArrayList<>(Arrays.asList(chequeNumbers));
        if (isIncrementChequeNumber != null && isIncrementChequeNumber) {
            String chequeNumber = chequeNumbersList.get(0);
            int chequeNumberLength = chequeNumber.length();
            for (int i = 2; i <= numberOfPDC; i++) {
                /**
                 * tempChequeNumber for to generate next sequence cheque number
                 */
                final Long tempChequeNumber = Long.parseLong(chequeNumber) + 1;
                chequeNumber = StringUtils.leftPad(tempChequeNumber.toString(), chequeNumberLength, "0");
                chequeNumbersList.add(chequeNumber);
            }
        }

        int i = 0;
        for (final String chequeNumber : chequeNumbersList) {
            Date chequeDate = null;
            BigDecimal amount = null;
            if (chequeDates != null && chequeDates[i] != null) {
                final LocalDate date = JsonParserHelper.convertFrom(chequeDates[i], PostDatedChequeDetailApiConstants.chequeDatesParamName,
                        dateFormat, locale);
                chequeDate = date.toDate();
            }
            if (amounts != null && amounts[i] != null) {
                amount = new BigDecimal(amounts[i]);
            }
            final PostDatedChequeDetail postDatedChequeDetail = PostDatedChequeDetail.create(bankName, branchName, accountNumber, ifscCode,
                    amount, chequeType, chequeNumber, chequeDate);
            final PostDatedChequeDetailMapping postDatedChequeDetailMapping = PostDatedChequeDetailMapping.create(postDatedChequeDetail,
                    paymentType, entityType, entityId, amount, chequeDate);
            postDatedChequeDetail.setPostDatedChequeDetailMapping(postDatedChequeDetailMapping);
            postDatedChequeDetails.add(postDatedChequeDetail);
            i++;
        }
    }

    public void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public Map<String, Object> validateAndUpdate(final PostDatedChequeDetail postDatedChequeDetail, final JsonCommand command) {

        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());

        if (StringUtils.isBlank(command.json())) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, command.json(),
                PostDatedChequeDetailApiConstants.UPDATE_EACH_OBJECT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(command.json());

        if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.chequeNumberParamName, element)) {
            final String chequeNumber = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.chequeNumberParamName,
                    element);
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeNumberParamName).value(chequeNumber).notBlank()
                    .notExceedingLengthOf(50);
        }

        if (ChequeType.fromInt(postDatedChequeDetail.getChequeType()).isRepaymentPDC()) {
            if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.chequeDateParamName, element)) {
                final LocalDate chequeDate = this.fromApiJsonHelper.extractLocalDateNamed(
                        PostDatedChequeDetailApiConstants.chequeDateParamName, element);
                baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeDateParamName).value(chequeDate).notBlank();
            }
            if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.chequeAmountParamName, element)) {
                final BigDecimal chequeAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        PostDatedChequeDetailApiConstants.chequeAmountParamName, element);
                baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeAmountParamName).value(chequeAmount).notBlank()
                        .positiveAmount();
            }
        } else {
            LocalDate chequeDate = null;
            if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.chequeDateParamName, element)) {
                chequeDate = this.fromApiJsonHelper.extractLocalDateNamed(PostDatedChequeDetailApiConstants.chequeDateParamName, element);
            }
            BigDecimal chequeAmount = null;
            if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.chequeAmountParamName, element)) {
                chequeAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        PostDatedChequeDetailApiConstants.chequeAmountParamName, element);
            }
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeDateParamName).value(chequeDate).ignoreIfNull()
                    .notBlank();
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeAmountParamName).value(chequeAmount)
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.bankNameParamName, element)) {
            final String bankName = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.bankNameParamName, element);
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.bankNameParamName).value(bankName).notBlank()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.branchNameParamName, element)) {
            final String branchName = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.branchNameParamName,
                    element);
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.branchNameParamName).value(branchName).notBlank()
                    .notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(PostDatedChequeDetailApiConstants.ifscCodeParamName, element)) {
            final String ifscCode = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.ifscCodeParamName, element);
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.ifscCodeParamName).value(ifscCode).notBlank()
                    .notExceedingLengthOf(50);
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        final Map<String, Object> changes = postDatedChequeDetail.update(command);
        validateUpdateForDuplicateChequeNumber(postDatedChequeDetail);
        return changes;
    }

    private void validateUpdateForDuplicateChequeNumber(final PostDatedChequeDetail postDatedChequeDetail) {
        final List<String> allChequeNumbers = new ArrayList<>();
        final List<String> duplicatedChequeNumbers = new ArrayList<>();
        validateAgainstBasedOnExistingChequeNumbers(postDatedChequeDetail, allChequeNumbers, duplicatedChequeNumbers);
    }

    private void validateAgainstBasedOnExistingChequeNumbers(final PostDatedChequeDetail postDatedChequeDetail,
            final List<String> allChequeNumbers, final List<String> duplicatedChequeNumbers) {
        final Integer entityType = postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType();
        final Long entityId = postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityId();
        final List<PostDatedChequeDetailMapping> postDatedChequeDetailMappings = this.mappingRepository.findByEntityTypeAndEntityId(
                entityType, entityId);
        for (final PostDatedChequeDetailMapping postDatedChequeDetailMapping : postDatedChequeDetailMappings) {
            if (!postDatedChequeDetailMapping.isDeleted()) {
                final PostDatedChequeDetail pdc = postDatedChequeDetailMapping.getPostDatedChequeDetail();
                if (isChequeNumberDuplicated(allChequeNumbers, pdc, postDatedChequeDetail)) {
                    duplicatedChequeNumbers.add(pdc.getChequeNumber());
                }
                allChequeNumbers.add(pdc.getChequeNumber());
            }
        }
        if (!duplicatedChequeNumbers.isEmpty()) {
            final String globalisationMessageCode = "error.msg.pdc.cheque.numbers.duplicated";
            final String defaultUserMessage = "PDC cheque numbers are duplicated " + duplicatedChequeNumbers + toString();
            throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, duplicatedChequeNumbers);
        }
    }

    private boolean isChequeNumberDuplicated(final List<String> allChequeNumbers, final PostDatedChequeDetail pdc,
            final PostDatedChequeDetail postDatedChequeDetail) {
        if (allChequeNumbers.contains(pdc.getChequeNumber()) && pdc.getChequeNumber().equals(postDatedChequeDetail.getChequeNumber())
                && pdc.getBankName().equals(postDatedChequeDetail.getBankName())
                && pdc.getBranchName().equals(postDatedChequeDetail.getBranchName())
                && pdc.getIfscCode().equals(postDatedChequeDetail.getIfscCode())) { return true; }
        return false;
    }

    public PDCSearchParameters validateAndBuildPDCSearchParameters(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PostDatedChequeDetailApiConstants.SEARCH_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());

        final Long officeId = this.fromApiJsonHelper.extractLongNamed(PostDatedChequeDetailApiConstants.officeIdNameParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.officeIdNameParamName).value(officeId).ignoreIfNull()
                .longGreaterThanZero();

        final Integer chequeType = this.fromApiJsonHelper.extractIntegerNamed(PostDatedChequeDetailApiConstants.chequeTypeParamName,
                element, locale);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeTypeParamName).value(chequeType).ignoreIfNull()
                .integerGreaterThanZero().isOneOfTheseValues(ChequeType.integerValues());

        final Integer chequeStatus = this.fromApiJsonHelper.extractIntegerNamed(PostDatedChequeDetailApiConstants.chequeStatusParamName,
                element, locale);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.chequeStatusParamName).value(chequeStatus).ignoreIfNull()
                .integerGreaterThanZero().isOneOfTheseValues(ChequeStatus.integerValues());

        final LocalDate fromDate = this.fromApiJsonHelper.extractLocalDateNamed(PostDatedChequeDetailApiConstants.fromDateParamName,
                element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.fromDateParamName).value(fromDate).ignoreIfNull();

        final LocalDate toDate = this.fromApiJsonHelper.extractLocalDateNamed(PostDatedChequeDetailApiConstants.toDateParamName, element);
        if (fromDate != null) {
            baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.toDateParamName).value(toDate).ignoreIfNull()
                    .validateDateAfter(fromDate);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

        return PDCSearchParameters.from(officeId, chequeType, chequeStatus, fromDate, toDate);
    }

    public void validateAndDelete(final PostDatedChequeDetail postDatedChequeDetail) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        if (postDatedChequeDetail.isDelete()) { throw new PostDatedChequeDetailDeleteException(postDatedChequeDetail.getId()); }
        final String globalisationMessageCode = "error.msg.pdc.can.not.be.deleted.status.not.in.pending.state";
        final String defaultUserMessage = "PDC details with identifier " + postDatedChequeDetail.getId()
                + " can not be deleted. Status not in pending state.";
        if (!ChequeStatus.fromInt(postDatedChequeDetail.getPresentStatus()).isPending()) { throw new PostDatedChequeDetailStatusException(
                globalisationMessageCode, defaultUserMessage, postDatedChequeDetail.getId()); }
        postDatedChequeDetail.delete();
    }

    public void validateBulkOperationsOnPDC(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PostDatedChequeDetailApiConstants.PRESENTED_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate date = this.fromApiJsonHelper.extractLocalDateNamed(PostDatedChequeDetailApiConstants.dateParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.dateParamName).value(date).notNull();

        final String description = this.fromApiJsonHelper.extractStringNamed(PostDatedChequeDetailApiConstants.descriptionParamName,
                element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.descriptionParamName).value(description).ignoreIfNull()
                .notExceedingLengthOf(500);

        final String[] pdcChequeDetails = this.fromApiJsonHelper.extractArrayNamed(
                PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName).value(pdcChequeDetails).notNull()
                .arrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    @Transactional
    public PostDatedChequeDetail processForPresentPDC(final JsonCommand command, final PostDatedChequeDetail postDatedChequeDetail,
            final LocalDate presentedDate, final String presentedDescription, final String transactionDate) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        postDatedChequeDetail.setPresentedDate(presentedDate.toDate());
        postDatedChequeDetail.setPresentedDescription(presentedDescription);
        postDatedChequeDetail.setPresentStatus(ChequeStatus.PRESENTED.getValue());
        makeLoanRepayment(postDatedChequeDetail, command, transactionDate);
        this.postDatedChequeDetailsRepository.save(postDatedChequeDetail);
        return postDatedChequeDetail;
    }

    private void makeLoanRepayment(final PostDatedChequeDetail postDatedChequeDetail, final JsonCommand command,
            final String transactionDate) {
        final ChequeType chequeType = ChequeType.fromInt(postDatedChequeDetail.getChequeType());
        final Long loanId = postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityId();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("transactionDate", transactionDate);
        if (chequeType.isRepaymentPDC()) {
            jsonObject.addProperty("transactionAmount", postDatedChequeDetail.getPostDatedChequeDetailMapping().getDueAmount());
        } else {
            jsonObject.addProperty("transactionAmount", postDatedChequeDetail.getChequeAmount());
        }
        jsonObject.addProperty("paymentTypeId", postDatedChequeDetail.getPostDatedChequeDetailMapping().getPaymentType());
        jsonObject.addProperty("locale", command.locale().toString());
        jsonObject.addProperty("dateFormat", command.dateFormat().toString());
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject.toString());
        final JsonCommand newCommand = JsonCommand.fromExistingCommand(command, parsedCommand);
        final boolean isRecoveryRepayment = false;
        final CommandProcessingResult result = this.loanWritePlatformService.makeLoanRepayment(loanId, newCommand, isRecoveryRepayment);
        postDatedChequeDetail.getPostDatedChequeDetailMapping().setTransactionId(Long.valueOf(result.getTransactionId()));
    }

    @Transactional
    public PostDatedChequeDetail processForBouncedPDC(final JsonCommand command, final PostDatedChequeDetail postDatedChequeDetail,
            final LocalDate bouncedDate, final String bouncedDescription, final String transactionDate) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        postDatedChequeDetail.setBouncedDate(bouncedDate.toDate());
        postDatedChequeDetail.setBouncedDescription(bouncedDescription);
        postDatedChequeDetail.setPresentStatus(ChequeStatus.BOUNCED.getValue());
        adjustLoanTransaction(postDatedChequeDetail, command, transactionDate);
        this.postDatedChequeDetailsRepository.save(postDatedChequeDetail);
        return postDatedChequeDetail;
    }

    private void adjustLoanTransaction(final PostDatedChequeDetail postDatedChequeDetail, final JsonCommand command,
            final String transactionDate) {
        final Long loanId = postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityId();
        Long transactionId = postDatedChequeDetail.getPostDatedChequeDetailMapping().getTransactionId();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("transactionDate", transactionDate);
        jsonObject.addProperty("transactionAmount", BigDecimal.ZERO);
        jsonObject.addProperty("locale", command.locale().toString());
        jsonObject.addProperty("dateFormat", command.dateFormat().toString());
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject.toString());
        final JsonCommand newCommand = JsonCommand.fromExistingCommand(command, parsedCommand);
        postDatedChequeDetail.getPostDatedChequeDetailMapping().setTransactionId(null);
        this.postDatedChequeDetailsRepository.save(postDatedChequeDetail);
        this.loanWritePlatformService.adjustLoanTransaction(loanId, transactionId, newCommand);
    }

    @Transactional
    public PostDatedChequeDetail processForClearPDC(final PostDatedChequeDetail postDatedChequeDetail, final LocalDate clearedDate,
            final String clearedDescription) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        postDatedChequeDetail.setClearedDate(clearedDate.toDate());
        postDatedChequeDetail.setClearedDescription(clearedDescription);
        postDatedChequeDetail.setPresentStatus(ChequeStatus.CLEARED.getValue());
        postDatedChequeDetail.getPostDatedChequeDetailMapping().setPaidStatus(true);
        this.postDatedChequeDetailsRepository.save(postDatedChequeDetail);
        return postDatedChequeDetail;
    }

    @Transactional
    public PostDatedChequeDetail processForCancelPDC(final PostDatedChequeDetail postDatedChequeDetail, final LocalDate cancelledDate,
            final String cancelledDescription) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        postDatedChequeDetail.setCancelledDate(cancelledDate.toDate());
        postDatedChequeDetail.setCancelledDescription(cancelledDescription);
        postDatedChequeDetail.setPresentStatus(ChequeStatus.CANCELLED.getValue());
        this.postDatedChequeDetailsRepository.save(postDatedChequeDetail);
        return postDatedChequeDetail;
    }

    @Transactional
    public PostDatedChequeDetail processForReturnPDC(final PostDatedChequeDetail postDatedChequeDetail, final LocalDate returnedDate,
            final String returnedDescription) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        postDatedChequeDetail.setReturnedDate(returnedDate.toDate());
        postDatedChequeDetail.setReturnedDescription(returnedDescription);
        postDatedChequeDetail.setPresentStatus(ChequeStatus.RETURNED.getValue());
        this.postDatedChequeDetailsRepository.save(postDatedChequeDetail);
        return postDatedChequeDetail;
    }

    public void validateUndoPDC(final JsonCommand command) {
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PostDatedChequeDetailApiConstants.PRESENTED_REQUEST_DATA_PARAMETERS);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PostDatedChequeDetailApiConstants.RESOURCE_NAME);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final String[] pdcChequeDetails = this.fromApiJsonHelper.extractArrayNamed(
                PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName, element);
        baseDataValidator.reset().parameter(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName).value(pdcChequeDetails).notNull()
                .arrayNotEmpty();
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    @Transactional
    public PostDatedChequeDetail processForUndoPDC(JsonCommand command, final PostDatedChequeDetail postDatedChequeDetail) {
        validatePDCActionsAllowedOrNot(postDatedChequeDetail.getPostDatedChequeDetailMapping().getEntityType(), postDatedChequeDetail
                .getPostDatedChequeDetailMapping().getEntityId());
        if (postDatedChequeDetail.getPreviousStatus() != null) {
            final ChequeStatus presentStatus = ChequeStatus.fromInt(postDatedChequeDetail.getPresentStatus());
            if (presentStatus.isPresented()) {
                postDatedChequeDetail.setPresentStatus(ChequeStatus.PENDING.getValue());
                final String transactionDate = postDatedChequeDetail.getPresentedLocalDate().toString(command.dateFormat().toString());
                adjustLoanTransaction(postDatedChequeDetail, command, transactionDate);
            } else if (presentStatus.isBounced()) {
                postDatedChequeDetail.setPresentStatus(ChequeStatus.PRESENTED.getValue());
                final String transactionDate = postDatedChequeDetail.getPresentedLocalDate().toString(command.dateFormat().toString());
                makeLoanRepayment(postDatedChequeDetail, command, transactionDate);
            } else if (presentStatus.isCleared()) {
                postDatedChequeDetail.setPresentStatus(ChequeStatus.PRESENTED.getValue());
                postDatedChequeDetail.getPostDatedChequeDetailMapping().setPaidStatus(false);
            } else if (presentStatus.isCancelled()) {
                postDatedChequeDetail.setPresentStatus(ChequeStatus.PENDING.getValue());
            } else if (presentStatus.isReturned()) {
                postDatedChequeDetail.setPresentStatus(postDatedChequeDetail.getPreviousStatus());
            }
        }
        return postDatedChequeDetail;
    }

    public void throwExceptionIfDomainValidationWarningsExist(final List<ApiParameterError> validationErrors) {
        if (!validationErrors.isEmpty()) { throw new PlatformMultipleDomainValidationException(validationErrors); }
    }
}