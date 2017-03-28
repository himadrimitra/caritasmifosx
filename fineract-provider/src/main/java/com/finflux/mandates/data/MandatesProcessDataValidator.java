package com.finflux.mandates.data;

import com.finflux.mandates.api.MandatesProcessingApiConstants;
import com.google.gson.JsonArray;
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
public class MandatesProcessDataValidator {

        private final FromJsonHelper fromApiJsonHelper;

        @Autowired
        public MandatesProcessDataValidator(final FromJsonHelper fromApiJsonHelper){
                this.fromApiJsonHelper = fromApiJsonHelper;
        }

        public void validateMandatesDownload(final String json) {
                if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

                final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
                this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, MandatesProcessingApiConstants.ALLOWED_REQUEST_PARAMS_MANDATES_DOWNLOAD);

                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(MandatesProcessingApiConstants.RESOURCE_NAME);

                final JsonElement element = this.fromApiJsonHelper.parse(json);

                final Long officeId = this.fromApiJsonHelper.extractLongNamed(MandatesProcessingApiConstants.officeId, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.officeId).value(officeId).notNull().longGreaterThanZero();

                final Boolean includeChildOffices = this.fromApiJsonHelper.extractBooleanNamed(MandatesProcessingApiConstants.includeChildOffices, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.includeChildOffices).value(includeChildOffices).notNull().validateForBooleanValue();

                final Boolean includeMandateScans = this.fromApiJsonHelper.extractBooleanNamed(MandatesProcessingApiConstants.includeMandateScans, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.includeMandateScans).value(includeMandateScans).notNull().validateForBooleanValue();

                throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }

        public void validateTransactionsDownload(String json) {
                if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

                final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
                this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, MandatesProcessingApiConstants.ALLOWED_REQUEST_PARAMS_TRANSACTIONS_DOWNLOAD);

                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(MandatesProcessingApiConstants.RESOURCE_NAME);

                final JsonElement element = this.fromApiJsonHelper.parse(json);

                final Long officeId = this.fromApiJsonHelper.extractLongNamed(MandatesProcessingApiConstants.officeId, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.officeId).value(officeId).notNull().longGreaterThanZero();

                final Boolean includeChildOffices = this.fromApiJsonHelper.extractBooleanNamed(MandatesProcessingApiConstants.includeChildOffices, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.includeChildOffices).value(includeChildOffices).notNull().validateForBooleanValue();

                final LocalDate paymentDueEndDate = this.fromApiJsonHelper.extractLocalDateNamed(MandatesProcessingApiConstants.paymentDueEndDate, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.paymentDueEndDate).value(paymentDueEndDate)
                        .notNull();

                final LocalDate paymentDueStartDate = this.fromApiJsonHelper.extractLocalDateNamed(MandatesProcessingApiConstants.paymentDueStartDate, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.paymentDueStartDate).value(paymentDueStartDate)
                        .notNull().validateDateAfter(LocalDate.now().minusDays(1)).validateDateBeforeOrEqual(paymentDueEndDate);

                final JsonArray includeFailedTransactions = this.fromApiJsonHelper.extractJsonArrayNamed(MandatesProcessingApiConstants.includeFailedTransactions, element);
                baseDataValidator.reset().parameter(MandatesProcessingApiConstants.includeFailedTransactions).value(includeFailedTransactions)
                        .ignoreIfNull().notNull().jsonArrayNotEmpty();

                throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }

        private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                        "Validation errors exist.", dataValidationErrors); }
        }

}
