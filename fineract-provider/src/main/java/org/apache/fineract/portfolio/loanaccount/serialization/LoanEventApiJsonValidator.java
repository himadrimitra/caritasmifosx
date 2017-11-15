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
package org.apache.fineract.portfolio.loanaccount.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.exception.CalendarDateException;
import org.apache.fineract.portfolio.calendar.exception.NotValidRecurringDateException;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.service.BankTransactionLoanActionsValidationService;
import com.finflux.transaction.execution.service.BankTransactionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class LoanEventApiJsonValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final LoanApplicationCommandFromApiJsonHelper fromApiJsonDeserializer;
    private final BankTransactionLoanActionsValidationService bankTransactionLoanActionsValidationService;

    @Autowired
    public LoanEventApiJsonValidator(final FromJsonHelper fromApiJsonHelper,
            final LoanApplicationCommandFromApiJsonHelper fromApiJsonDeserializer,
            final BankTransactionLoanActionsValidationService bankTransactionLoanActionsValidationService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.bankTransactionLoanActionsValidationService = bankTransactionLoanActionsValidationService;
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateDisbursement(final Long loanId, final String json, final boolean isAccountTransfer) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        Set<String> disbursementParameters = null;

        if (isAccountTransfer) {
            disbursementParameters = new HashSet<>(Arrays.asList("actualDisbursementDate", "externalId", "note", "locale", "dateFormat",
                    LoanApiConstants.principalDisbursedParameterName, LoanApiConstants.emiAmountParameterName,
                    LoanApiConstants.discountOnDisbursalAmountParameterName, LoanApiConstants.repaymentsStartingFromDateParameterName));
        } else {
            disbursementParameters = new HashSet<>(Arrays.asList("actualDisbursementDate", "externalId", "note", "locale", "dateFormat",
                    "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber", "adjustRepaymentDate",
                    LoanApiConstants.principalDisbursedParameterName, LoanApiConstants.emiAmountParameterName, "authenticationRuleId",
                    "authenticationType", "clientAuthData", "location", "locationType", "pincode", "longitude", "latitude",
                    LoanApiConstants.clientMembersParamName, LoanApiConstants.skipAuthenticationRule,
                    LoanApiConstants.discountOnDisbursalAmountParameterName, LoanApiConstants.repaymentsStartingFromDateParameterName));
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.disbursement");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate actualDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed("actualDisbursementDate", element);
        baseDataValidator.reset().parameter("actualDisbursementDate").value(actualDisbursementDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        final BigDecimal principal = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApiConstants.principalDisbursedParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.principalDisbursedParameterName).value(principal).ignoreIfNull()
                .positiveAmount();

        final BigDecimal discountAmount = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApiConstants.discountOnDisbursalAmountParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.discountOnDisbursalAmountParameterName).value(discountAmount).ignoreIfNull()
                .positiveAmount();

        final BigDecimal emiAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(LoanApiConstants.emiAmountParameterName,
                element);
        baseDataValidator.reset().parameter(LoanApiConstants.emiAmountParameterName).value(emiAmount).ignoreIfNull().positiveAmount();

        validatePaymentDetails(baseDataValidator, element);
        validateForActiveBankTransactions(loanId);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateDisbursementDateWithMeetingDate(final LocalDate actualDisbursementDate, final CalendarInstance calendarInstance,
            final Boolean isSkipRepaymentOnFirstMonth, final Integer numberOfDays) {
        if (null != calendarInstance) {
            final Calendar calendar = calendarInstance.getCalendar();
            if (!calendar.isValidRecurringDate(actualDisbursementDate, isSkipRepaymentOnFirstMonth, numberOfDays)) {
                // Disbursement date should fall on a meeting date
                final String errorMessage = "Expected disbursement date '" + actualDisbursementDate.toString()
                        + "' does not fall on a meeting date.";
                throw new NotValidRecurringDateException("loan.actual.disbursement.date", errorMessage, actualDisbursementDate.toString(),
                        calendar.getTitle());
            }
        }
    }

    public void validateTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(
                Arrays.asList("transactionDate", "transactionAmount", "externalId", "note", "locale", "dateFormat", "paymentTypeId",
                        "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber", "clientMembers"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().zeroOrPositiveAmount();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateNewRepaymentTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(
                Arrays.asList("transactionDate", "transactionAmount", "externalId", "note", "locale", "dateFormat", "paymentTypeId",
                        "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber", "clientMembers"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().positiveAmount();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateNewAddSubsidyTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(
                Arrays.asList(LoanApiConstants.subsidyReleaseDate, LoanApiConstants.subsidyAmountReleased, "externalId", "note", "locale",
                        "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber"));
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.subsidyReleaseDate, element);
        baseDataValidator.reset().parameter(LoanApiConstants.subsidyReleaseDate).value(transactionDate).notNull()
                .validateDateBeforeOrEqual(DateUtils.getLocalDateOfTenant());

        final BigDecimal subsidyAmountReleased = this.fromApiJsonHelper
                .extractBigDecimalWithLocaleNamed(LoanApiConstants.subsidyAmountReleased, element);
        baseDataValidator.reset().parameter(LoanApiConstants.subsidyAmountReleased).value(subsidyAmountReleased).notNull().positiveAmount();

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateNewRevokeSubsidyTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("subsidyRevokeDate", "externalId", "note", "locale",
                "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("subsidyRevokeDate", element);
        baseDataValidator.reset().parameter("subsidyRevokeDate").value(transactionDate).notNull()
                .validateDateBeforeOrEqual(DateUtils.getLocalDateOfTenant());

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateRefundTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "externalId", "note", "locale",
                "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateRepaymentDateWithMeetingDate(final LocalDate repaymentDate, final CalendarInstance calendarInstance) {
        if (null != calendarInstance) {
            final Calendar calendar = calendarInstance.getCalendar();
            if (calendar != null && repaymentDate != null) {
                // Disbursement date should fall on a meeting date
                if (!CalendarUtils.isValidRecurringDate(calendar.getRecurrence(), calendar.getStartDateLocalDate(), repaymentDate)) {
                    final String errorMessage = "Transaction date '" + repaymentDate.toString() + "' does not fall on a meeting date.";
                    throw new NotValidRecurringDateException("loan.transaction.date", errorMessage, repaymentDate.toString(),
                            calendar.getTitle());
                }

            }
        }
    }

    private void validatePaymentDetails(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        // Validate all string payment detail fields for max length
        final Integer paymentTypeId = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("paymentTypeId", element);
        baseDataValidator.reset().parameter("paymentTypeId").value(paymentTypeId).ignoreIfNull().integerGreaterThanZero();
        final Set<String> paymentDetailParameters = new HashSet<>(
                Arrays.asList("accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber"));
        for (final String paymentDetailParameterName : paymentDetailParameters) {
            final String paymentDetailParameterValue = this.fromApiJsonHelper.extractStringNamed(paymentDetailParameterName, element);
            baseDataValidator.reset().parameter(paymentDetailParameterName).value(paymentDetailParameterValue).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }
    }

    public void validateTransactionWithNoAmount(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(
                Arrays.asList("transactionDate", "note", "locale", "dateFormat", "writeoffReasonId"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateAddLoanCharge(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList("chargeId", "amount", "dueDate", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanCharge");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", element);
        baseDataValidator.reset().parameter("chargeId").value(chargeId).notNull().integerGreaterThanZero();

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
        baseDataValidator.reset().parameter("amount").value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists("dueDate", element)) {
            this.fromApiJsonHelper.extractLocalDateNamed("dueDate", element);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdateOfLoanCharge(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(Arrays.asList("amount", "dueDate", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanCharge");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
        baseDataValidator.reset().parameter("amount").value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists("dueDate", element)) {
            this.fromApiJsonHelper.extractLocalDateNamed("dueDate", element);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdateOfLoanOfficer(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(
                Arrays.asList("assignmentDate", "fromLoanOfficerId", "toLoanOfficerId", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanOfficer");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long toLoanOfficerId = this.fromApiJsonHelper.extractLongNamed("toLoanOfficerId", element);
        baseDataValidator.reset().parameter("toLoanOfficerId").value(toLoanOfficerId).notNull().integerGreaterThanZero();

        final String assignmentDateStr = this.fromApiJsonHelper.extractStringNamed("assignmentDate", element);
        baseDataValidator.reset().parameter("assignmentDate").value(assignmentDateStr).notBlank();

        if (!StringUtils.isBlank(assignmentDateStr)) {
            final LocalDate assignmentDate = this.fromApiJsonHelper.extractLocalDateNamed("assignmentDate", element);
            baseDataValidator.reset().parameter("assignmentDate").value(assignmentDate).notNull();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForBulkLoanReassignment(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> supportedParameters = new HashSet<>(Arrays.asList("assignmentDate", "fromLoanOfficerId", "toLoanOfficerId",
                "loans", "locale", "dateFormat", "centers", "clients", "groups"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanOfficer");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate assignmentDate = this.fromApiJsonHelper.extractLocalDateNamed("assignmentDate", element);
        baseDataValidator.reset().parameter("assignmentDate").value(assignmentDate).notNull();
        final Long fromLoanOfficerId = this.fromApiJsonHelper.extractLongNamed("fromLoanOfficerId", element);
        baseDataValidator.reset().parameter("fromLoanOfficerId").value(fromLoanOfficerId).notNull().longGreaterThanZero();
        final Long toLoanOfficerId = this.fromApiJsonHelper.extractLongNamed("toLoanOfficerId", element);
        baseDataValidator.reset().parameter("toLoanOfficerId").value(toLoanOfficerId).notNull().longGreaterThanZero();
        final String[] loans = this.fromApiJsonHelper.extractArrayNamed("loans", element);
        baseDataValidator.reset().parameter("loans").value(loans).arrayNotEmpty();

        if (this.fromApiJsonHelper.parameterExists("centers", element)) {
            final String[] centers = this.fromApiJsonHelper.extractArrayNamed("centers", element);
            baseDataValidator.reset().parameter("centers").value(centers).arrayNotEmpty();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateChargePaymentTransaction(final String json, final boolean isChargeIdIncluded) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        Set<String> transactionParameters = null;
        if (isChargeIdIncluded) {
            transactionParameters = new HashSet<>(
                    Arrays.asList("transactionDate", "locale", "dateFormat", "chargeId", "dueDate", "installmentNumber"));
        } else {
            transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "locale", "dateFormat", "dueDate", "installmentNumber"));
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("loan.charge.payment.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        if (isChargeIdIncluded) {
            final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", element);
            baseDataValidator.reset().parameter("chargeId").value(chargeId).notNull().integerGreaterThanZero();
        }
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();
        final Integer installmentNumber = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("installmentNumber", element);
        baseDataValidator.reset().parameter("installmentNumber").value(installmentNumber).ignoreIfNull().integerGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateInstallmentChargeTransaction(final String json) {

        if (StringUtils.isBlank(json)) { return; }
        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("dueDate", "locale", "dateFormat", "installmentNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("loan.charge.waive.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Integer installmentNumber = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("installmentNumber", element);
        baseDataValidator.reset().parameter("installmentNumber").value(installmentNumber).ignoreIfNull().integerGreaterThanZero();
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdateDisbursementDateAndAmount(final String json, final LoanDisbursementDetails loanDisbursementDetails) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> disbursementParameters = new HashSet<>(
                Arrays.asList("locale", "dateFormat", LoanApiConstants.disbursementDataParameterName,
                        LoanApiConstants.approvedLoanAmountParameterName, LoanApiConstants.updatedDisbursementDateParameterName,
                        LoanApiConstants.updatedDisbursementPrincipalParameterName, LoanApiConstants.disbursementDateParameterName));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, disbursementParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.update.disbursement");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate actualDisbursementDate = this.fromApiJsonHelper
                .extractLocalDateNamed(LoanApiConstants.disbursementDateParameterName, element);
        baseDataValidator.reset().parameter(LoanApiConstants.disbursementDateParameterName).value(actualDisbursementDate).notNull();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        final BigDecimal principal = this.fromApiJsonHelper
                .extractBigDecimalNamed(LoanApiConstants.updatedDisbursementPrincipalParameterName, element, locale);
        baseDataValidator.reset().parameter(LoanApiConstants.disbursementPrincipalParameterName).value(principal).notNull();

        final BigDecimal approvedPrincipal = this.fromApiJsonHelper.extractBigDecimalNamed(LoanApiConstants.approvedLoanAmountParameterName,
                element, locale);
        if (loanDisbursementDetails.actualDisbursementDate() != null) {
            baseDataValidator.reset().parameter(LoanApiConstants.disbursementDateParameterName)
                    .failWithCode(LoanApiConstants.ALREADY_DISBURSED);
        }

        this.fromApiJsonDeserializer.validateLoanMultiDisbursementdate(element, baseDataValidator, actualDisbursementDate,
                approvedPrincipal);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateNewRefundTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "transactionAmount", "externalId", "note",
                "locale", "dateFormat", "paymentTypeId", "accountNumber", "checkNumber", "routingCode", "receiptNumber", "bankNumber"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().positiveAmount();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    // valiadting for all submitted approved anctive loans
    public void validateGroupMeetingDateHasActiveLoans(final List<Loan> loans, final Boolean reschedulebasedOnMeetingDates,
            final LocalDate presentMeetingDate) {
        if (!reschedulebasedOnMeetingDates) {
            final List<Long> activeLoanIds = new ArrayList<>();
            for (final Loan loan : loans) {
                if (loan.isDisbursed()) {
                    activeLoanIds.add(loan.getId());
                }
            }
            if (!activeLoanIds.isEmpty()) {
                final String defaultUserMessage = "Meeting calendar date cannot be updated since it has active loans";
                throw new CalendarDateException("meeting.cannot.be.updated.since.it.has.active.loans", defaultUserMessage,
                        presentMeetingDate);
            }
        }
    }

    public void validateLoanForeclosure(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> foreclosureParameters = new HashSet<>(Arrays.asList("transactionDate", "note", "locale", "dateFormat"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, foreclosureParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        baseDataValidator.reset().parameter("transactionDate").value(transactionDate).notNull();

        final String note = this.fromApiJsonHelper.extractStringNamed("note", element);
        baseDataValidator.reset().parameter("note").value(note).notExceedingLengthOf(1000);

        validatePaymentDetails(baseDataValidator, element);
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateGlimForWaiveInterest(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("glimloan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonArray clients = this.fromApiJsonHelper.extractJsonArrayNamed("clientMembers", element);
        baseDataValidator.reset().parameter("clientMembers").value(clients).notNull();

        if (clients != null) {
            for (final JsonElement innerElement : clients) {
                final Long glimId = this.fromApiJsonHelper.extractLongNamed("id", innerElement);
                baseDataValidator.reset().parameter("id").value(glimId).notNull().longGreaterThanZero();

                final String transactionAmount = this.fromApiJsonHelper.extractStringNamed("transactionAmount", innerElement);
                baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().zeroOrPositiveAmount();
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateGlimForWriteOff(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("glimloan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonArray clients = this.fromApiJsonHelper.extractJsonArrayNamed("clientMembers", element);
        baseDataValidator.reset().parameter("clientMembers").value(clients).notNull();

        if (clients != null) {
            for (final JsonElement innerElement : clients) {
                final Long glimId = this.fromApiJsonHelper.extractLongNamed("id", innerElement);
                baseDataValidator.reset().parameter("id").value(glimId).notNull().longGreaterThanZero();

                final String transactionAmount = this.fromApiJsonHelper.extractStringNamed("transactionAmount", innerElement);
                baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().zeroOrPositiveAmount();
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateGLIMWaiveChargeTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Set<String> transactionParameters = new HashSet<>(Arrays.asList("transactionDate", "transactionAmount", "externalId", "note",
                "locale", "dateFormat", "clientMembers", "isClientSelected"));

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, transactionParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed("transactionAmount", element, locale);
        baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull().zeroOrPositiveAmount();

        final JsonArray clients = this.fromApiJsonHelper.extractJsonArrayNamed("clientMembers", element);
        baseDataValidator.reset().parameter("clientMembers").value(clients).notNull();

        if (clients != null) {
            for (final JsonElement innerElement : clients) {
                final Long glimId = this.fromApiJsonHelper.extractLongNamed("id", innerElement);
                baseDataValidator.reset().parameter("id").value(glimId).notNull().longGreaterThanZero();

                final BigDecimal indTransactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed("transactionAmount", innerElement,
                        locale);
                baseDataValidator.reset().parameter("transactionAmount").value(indTransactionAmount).notNull();
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateGLIMWaiveChargeIndividualTransaction(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("glimloan.transaction");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonArray clients = this.fromApiJsonHelper.extractJsonArrayNamed("clientMembers", element);
        baseDataValidator.reset().parameter("clientMembers").value(clients).notNull();

        if (clients != null) {
            for (final JsonElement innerElement : clients) {
                final Long clientId = this.fromApiJsonHelper.extractLongNamed("clientId", innerElement);
                baseDataValidator.reset().parameter("clientId").value(clientId).notNull().longGreaterThanZero();

                final String clientName = this.fromApiJsonHelper.extractStringNamed("clientName", innerElement);
                baseDataValidator.reset().parameter("clientName").value(clientName).notNull().notBlank();

                final String remainigInterestAmount = this.fromApiJsonHelper.extractStringNamed("remainingTransactionAmount", innerElement);
                baseDataValidator.reset().parameter("remainingTransactionAmount").value(remainigInterestAmount).notNull();

                final String transactionAmount = this.fromApiJsonHelper.extractStringNamed("transactionAmount", innerElement);
                baseDataValidator.reset().parameter("transactionAmount").value(transactionAmount).notNull();
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    private void validateForActiveBankTransactions(final Long loanId) {
        final List<Integer> activeStatuses = new ArrayList<>(
                Arrays.asList(TransactionStatus.DRAFTED.getValue(), TransactionStatus.SUBMITTED.getValue(),
                        TransactionStatus.INITIATED.getValue(), TransactionStatus.PENDING.getValue(), TransactionStatus.ERROR.getValue()));
        this.bankTransactionLoanActionsValidationService.validateForInactiveBankTransactions(loanId, activeStatuses,
                BankTransactionType.CREATE);
    }

}