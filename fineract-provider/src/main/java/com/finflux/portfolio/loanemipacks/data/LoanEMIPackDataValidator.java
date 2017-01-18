package com.finflux.portfolio.loanemipacks.data;

import com.finflux.portfolio.loanemipacks.api.LoanEMIPacksApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LoanEMIPackDataValidator {

        private final FromJsonHelper fromApiJsonHelper;

        @Autowired
        public LoanEMIPackDataValidator(final FromJsonHelper fromApiJsonHelper){
                this.fromApiJsonHelper = fromApiJsonHelper;
        }

        public void validateForCreate(final String json){
                if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

                final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
                this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                        LoanEMIPacksApiConstants.EMI_PACK_REQUEST_DATA_PARAMETERS);

                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(LoanEMIPacksApiConstants.RESOURCE_NAME);

                final JsonElement element = this.fromApiJsonHelper.parse(json);

                final Integer repaymentEvery = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.repaymentEvery, element);
                baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.repaymentEvery).value(repaymentEvery)
                        .notNull().integerGreaterThanZero();

                final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.repaymentFrequencyType, element);
                baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.repaymentFrequencyType).value(repaymentFrequencyType)
                        .notNull().inMinMaxRange(0, 3);

                final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.numberOfRepayments, element);
                baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.numberOfRepayments).value(numberOfRepayments)
                        .notNull().integerGreaterThanZero();

                final BigDecimal sanctionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.sanctionAmount, element);
                baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.sanctionAmount).value(sanctionAmount)
                        .notNull().positiveAmount();

                final BigDecimal fixedEmi = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.fixedEmi, element);
                baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.fixedEmi).value(fixedEmi)
                        .notNull().positiveAmount().notGreaterThanMax(sanctionAmount);

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount1, element)) {
                        final BigDecimal disbursalAmount1 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount1, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount1).value(disbursalAmount1)
                                .notNull().positiveAmount().notGreaterThanMax(sanctionAmount);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2, element)) {
                        final BigDecimal disbursalAmount2 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount2, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount2).value(disbursalAmount2)
                                .notNull().positiveAmount().notGreaterThanMax(sanctionAmount);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3, element)) {
                        final BigDecimal disbursalAmount3 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount3, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount3).value(disbursalAmount3)
                                .notNull().positiveAmount().notGreaterThanMax(sanctionAmount);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)) {
                        final BigDecimal disbursalAmount4 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount4, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount4).value(disbursalAmount4)
                                .notNull().positiveAmount().notGreaterThanMax(sanctionAmount);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2, element)) {
                        final Integer disbursalEmi2 = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.disbursalEmi2, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalEmi2).value(disbursalEmi2)
                                .notNull().integerGreaterThanZero().notGreaterThanMax(numberOfRepayments);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3, element)) {
                        final Integer disbursalEmi3 = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.disbursalEmi3, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalEmi3).value(disbursalEmi3)
                                .notNull().integerGreaterThanZero().notGreaterThanMax(numberOfRepayments);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)) {
                        final Integer disbursalEmi4 = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.disbursalEmi4, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalEmi4).value(disbursalEmi4)
                                .notNull().integerGreaterThanZero().notGreaterThanMax(numberOfRepayments);
                }

                if ((this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2, element)
                        && !this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2, element))
                        || (!this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2, element)
                        && this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2, element))) {
                        baseDataValidator
                                .reset()
                                .failWithCode("disbursalAmount2.and.disbursalEmi2.both.should.be.used",
                                        "disbursalEmi2 must be present when disbursalAmount2 is used and viceversa");
                }

                if ((this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3, element)
                        && !this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3, element))
                        || (!this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3, element)
                        && this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3, element))) {
                        baseDataValidator
                                .reset()
                                .failWithCode("disbursalAmount3.and.disbursalEmi3.both.should.be.used",
                                        "disbursalEmi3 must be present when disbursalAmount3 is used and viceversa");
                }


                if ((this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)
                        && !this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi4, element))
                        || (!this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)
                        && this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi4, element))) {
                        baseDataValidator
                                .reset()
                                .failWithCode("disbursalAmount4.and.disbursalEmi4.both.should.be.used",
                                        "disbursalEmi4 must be present when disbursalAmount4 is used and viceversa");
                }


                throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }

        public void validateForUpdate(final String json){
                if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

                final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
                this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                        LoanEMIPacksApiConstants.EMI_PACK_REQUEST_DATA_PARAMETERS);

                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(LoanEMIPacksApiConstants.RESOURCE_NAME);

                final JsonElement element = this.fromApiJsonHelper.parse(json);

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.repaymentEvery, element)) {
                        final Integer repaymentEvery = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.repaymentEvery, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.repaymentEvery).value(repaymentEvery)
                                .notNull().integerGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.repaymentFrequencyType, element)) {
                        final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.repaymentFrequencyType, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.repaymentFrequencyType).value(repaymentFrequencyType)
                                .notNull().inMinMaxRange(0, 3);
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.numberOfRepayments, element)) {
                        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.numberOfRepayments, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.numberOfRepayments).value(numberOfRepayments)
                                .notNull().integerGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.sanctionAmount, element)) {
                        final BigDecimal sanctionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.sanctionAmount, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.sanctionAmount).value(sanctionAmount)
                                .notNull().positiveAmount();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.fixedEmi, element)) {
                        final BigDecimal fixedEmi = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.fixedEmi, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.fixedEmi).value(fixedEmi)
                                .notNull().positiveAmount();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount1, element)) {
                        final BigDecimal disbursalAmount1 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount1, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount1).value(disbursalAmount1)
                                .ignoreIfNull().positiveAmount();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2, element)) {
                        final BigDecimal disbursalAmount2 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount2, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount2).value(disbursalAmount2)
                                .ignoreIfNull().positiveAmount();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3, element)) {
                        final BigDecimal disbursalAmount3 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount3, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount3).value(disbursalAmount3)
                                .ignoreIfNull().positiveAmount();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)) {
                        final BigDecimal disbursalAmount4 = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanEMIPacksApiConstants.disbursalAmount4, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalAmount4).value(disbursalAmount4)
                                .ignoreIfNull().positiveAmount();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2, element)) {
                        final Integer disbursalEmi2 = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.disbursalEmi2, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalEmi2).value(disbursalEmi2)
                                .ignoreIfNull().integerGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3, element)) {
                        final Integer disbursalEmi3 = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.disbursalEmi3, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalEmi3).value(disbursalEmi3)
                                .ignoreIfNull().integerGreaterThanZero();
                }

                if (this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi4, element)) {
                        final Integer disbursalEmi4 = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LoanEMIPacksApiConstants.disbursalEmi4, element);
                        baseDataValidator.reset().parameter(LoanEMIPacksApiConstants.disbursalEmi4).value(disbursalEmi4)
                                .ignoreIfNull().integerGreaterThanZero();
                }

                if ((this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2, element)
                        && !this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2, element))
                        || (!this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2, element)
                        && this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2, element))) {
                        baseDataValidator
                                .reset()
                                .failWithCode("disbursalAmount2.and.disbursalEmi2.both.should.be.used",
                                        "disbursalEmi2 must be present when disbursalAmount2 is used and viceversa");
                }

                if ((this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3, element)
                        && !this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3, element))
                        || (!this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3, element)
                        && this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3, element))) {
                        baseDataValidator
                                .reset()
                                .failWithCode("disbursalAmount3.and.disbursalEmi3.both.should.be.used",
                                        "disbursalEmi3 must be present when disbursalAmount3 is used and viceversa");
                }


                if ((this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)
                        && !this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi4, element))
                        || (!this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4, element)
                        && this.fromApiJsonHelper.parameterExists(LoanEMIPacksApiConstants.disbursalEmi4, element))) {
                        baseDataValidator
                                .reset()
                                .failWithCode("disbursalAmount4.and.disbursalEmi4.both.should.be.used",
                                        "disbursalEmi4 must be present when disbursalAmount4 is used and viceversa");
                }

                throwExceptionIfValidationWarningsExist(dataValidationErrors);
        }

        private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                        "Validation errors exist.", dataValidationErrors); }
        }

}
