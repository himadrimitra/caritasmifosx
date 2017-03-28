package com.finflux.portfolio.loan.mandate.data;

import com.finflux.portfolio.loan.mandate.api.MandateApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MandateDataValidator {

        private final FromJsonHelper fromApiJsonHelper;

        @Autowired
        public MandateDataValidator(final FromJsonHelper fromApiJsonHelper){
                this.fromApiJsonHelper = fromApiJsonHelper;
        }

        public void validate(final String json, final boolean isUpdateCancel) {
                if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

                final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
                if(isUpdateCancel){
                        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, MandateApiConstants.ALLOWED_REQUEST_PARAMS_UPDATE_CANCEL);
                } else {
                        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, MandateApiConstants.ALLOWED_REQUEST_PARAMS_CREATE_EDIT);
                }

                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(MandateApiConstants.RESOURCE_NAME);

                final JsonElement element = this.fromApiJsonHelper.parse(json);

                final LocalDate requestDate = this.fromApiJsonHelper.extractLocalDateNamed(MandateApiConstants.requestDate, element);
                baseDataValidator.reset().parameter(MandateApiConstants.requestDate).value(requestDate).notNull();

                if(isUpdateCancel){
                        final String umrn = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.umrn, element);
                        baseDataValidator.reset().parameter(MandateApiConstants.umrn).value(umrn).notNull().notBlank().notExceedingLengthOf(20);
                }

                final String bankAccountHolderName = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.bankAccountHolderName, element);
                baseDataValidator.reset().parameter(MandateApiConstants.bankAccountHolderName).value(bankAccountHolderName).notNull().notBlank().notExceedingLengthOf(100);

                final String bankName = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.bankName, element);
                baseDataValidator.reset().parameter(MandateApiConstants.bankName).value(bankName).notNull().notBlank().notExceedingLengthOf(20);

                final String branchName = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.branchName, element);
                baseDataValidator.reset().parameter(MandateApiConstants.branchName).value(branchName).notNull().notBlank().notExceedingLengthOf(50);

                final String bankAccountNumber = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.bankAccountNumber, element);
                baseDataValidator.reset().parameter(MandateApiConstants.bankAccountNumber).value(bankAccountNumber).notNull().notBlank().notExceedingLengthOf(20);

                final String micr = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.micr, element);
                baseDataValidator.reset().parameter(MandateApiConstants.micr).value(micr).notNull().notBlank().notExceedingLengthOf(10);

                final String ifsc = this.fromApiJsonHelper.extractStringNamed(MandateApiConstants.ifsc, element);
                baseDataValidator.reset().parameter(MandateApiConstants.ifsc).value(ifsc).notNull().notBlank().notExceedingLengthOf(10);

                if(null == micr && ifsc == null){
                        baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("either.micr.or.ifsc.mandatory");
                }
                final Integer accountType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(MandateApiConstants.accountType, element);
                baseDataValidator.reset().parameter(MandateApiConstants.accountType).value(accountType).notNull().inMinMaxRange(1,6);

                final LocalDate periodFromDate = this.fromApiJsonHelper.extractLocalDateNamed(MandateApiConstants.periodFromDate, element);
                baseDataValidator.reset().parameter(MandateApiConstants.periodFromDate).value(periodFromDate).notNull();

                final LocalDate periodToDate = this.fromApiJsonHelper.extractLocalDateNamed(MandateApiConstants.periodToDate, element);
                final Boolean periodUntilCancelled = this.fromApiJsonHelper.extractBooleanNamed(MandateApiConstants.periodUntilCancelled, element);

                baseDataValidator.reset().parameter(MandateApiConstants.periodToDate).value(periodToDate).ignoreIfNull().notNull();

                baseDataValidator.reset().parameter(MandateApiConstants.periodUntilCancelled).value(periodUntilCancelled)
                        .ignoreIfNull().notNull().validateForBooleanValue();

                if(null == periodToDate && (null == periodUntilCancelled || !periodUntilCancelled)){
                        baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("either.periodToDate.or.periodUntilCancelled.mandatory");
                }
                if(null != periodToDate && null != periodUntilCancelled && periodUntilCancelled){
                        baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("both.periodToDate.and.periodUntilCancelled.cannot.be.used");
                }
                final Integer debitType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(MandateApiConstants.debitType, element);
                baseDataValidator.reset().parameter(MandateApiConstants.debitType).value(debitType).notNull().inMinMaxRange(1,2);

                final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(MandateApiConstants.amount, element);
                baseDataValidator.reset().parameter(MandateApiConstants.amount).value(amount).notNull().positiveAmount();

                final Integer debitFrequency = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(MandateApiConstants.debitFrequency, element);
                baseDataValidator.reset().parameter(MandateApiConstants.debitFrequency).value(debitFrequency).notNull().inMinMaxRange(1,5);

                final Long scannedDocumentId = this.fromApiJsonHelper.extractLongNamed(MandateApiConstants.scannedDocumentId, element);
                baseDataValidator.reset().parameter(MandateApiConstants.scannedDocumentId).value(scannedDocumentId).notNull().longGreaterThanZero();

                throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }

        private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                        "Validation errors exist.", dataValidationErrors); }
        }
}
