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
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariations;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.RescheduleLoansApiConstants;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class LoanRescheduleRequestDataValidator {

    private final FromJsonHelper fromJsonHelper;
    private final CalendarReadPlatformService calendarReadPlatformService;

    @Autowired
    public LoanRescheduleRequestDataValidator(FromJsonHelper fromJsonHelper,
            final CalendarReadPlatformService calendarReadPlatformService) {
        this.fromJsonHelper = fromJsonHelper;
        this.calendarReadPlatformService = calendarReadPlatformService;
    }

    /**
     * Validates the request to create a new loan reschedule entry
     * 
     * @param jsonCommand
     *            the JSON command object (instance of the JsonCommand class)
     * @param isBulkCreateAndApprove TODO
     * @return void
     **/
    public void validateForCreateAction(final JsonCommand jsonCommand, final Loan loan, boolean isBulkCreateAndApprove) {

        final String jsonString = jsonCommand.json();

        if (StringUtils.isBlank(jsonString)) { throw new InvalidJsonException(); }

        final Type typeToken = new TypeToken<Map<String, Object>>() {}.getType();
        Set<String> REQUEST_DATA_PARAMETERS = null;
        if (isBulkCreateAndApprove) {
            REQUEST_DATA_PARAMETERS = RescheduleLoansApiConstants.CREATE_AND_APPROVE_REQUEST_DATA_PARAMETERS;
        } else {
            REQUEST_DATA_PARAMETERS = loan.isGLIMLoan() ? RescheduleLoansApiConstants.CREATE_REQUEST_DATA_PARAMETERS
                    : RescheduleLoansApiConstants.CREATE_REQUEST_DATA_PARAMETERS;
        }      
        this.fromJsonHelper
                .checkForUnsupportedParameters(typeToken, jsonString, REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(StringUtils
                .lowerCase(RescheduleLoansApiConstants.ENTITY_NAME));

        final JsonElement jsonElement = jsonCommand.parsedJson();

        if (!isBulkCreateAndApprove && !loan.status().isActive()) {
            dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode("loan.is.not.active", "Loan is not active");
        }
        if (isBulkCreateAndApprove) {
            JsonArray loansArray = this.fromJsonHelper.extractJsonArrayNamed(RescheduleLoansApiConstants.loansParamName, jsonElement);
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.loansParamName).value(loansArray).notNull()
                    .jsonArrayNotEmpty();
            if (loansArray != null) {
                for (JsonElement element : loansArray) {
                    Long loanId = element.getAsLong();
                    dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.loansParamName).value(loanId).notNull()
                            .integerGreaterThanZero();
                }
            }
        } else {
            final Long loanId = this.fromJsonHelper.extractLongNamed(RescheduleLoansApiConstants.loanIdParamName, jsonElement);
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.loanIdParamName).value(loanId).notNull()
                    .integerGreaterThanZero();
        }
        
        final LocalDate submittedOnDate = this.fromJsonHelper.extractLocalDateNamed(RescheduleLoansApiConstants.submittedOnDateParamName,
                jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();

        if (submittedOnDate != null && !isBulkCreateAndApprove && loan.getDisbursementDate().isAfter(submittedOnDate)) {
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.submittedOnDateParamName)
                    .failWithCode("before.loan.disbursement.date", "Submission date cannot be before the loan disbursement date");
        }

        final LocalDate rescheduleFromDate = this.fromJsonHelper.extractLocalDateNamed(
                RescheduleLoansApiConstants.rescheduleFromDateParamName, jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName).value(rescheduleFromDate).notNull();

        final Integer graceOnPrincipal = this.fromJsonHelper.extractIntegerWithLocaleNamed(
                RescheduleLoansApiConstants.graceOnPrincipalParamName, jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.graceOnPrincipalParamName).value(graceOnPrincipal)
                .ignoreIfNull().integerGreaterThanZero();

        final Integer graceOnInterest = this.fromJsonHelper.extractIntegerWithLocaleNamed(
                RescheduleLoansApiConstants.graceOnInterestParamName, jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.graceOnInterestParamName).value(graceOnInterest).ignoreIfNull()
                .integerGreaterThanZero();

        final Integer extraTerms = this.fromJsonHelper.extractIntegerWithLocaleNamed(RescheduleLoansApiConstants.extraTermsParamName,
                jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.extraTermsParamName).value(extraTerms).ignoreIfNull()
                .integerGreaterThanZero();

        final Long rescheduleReasonId = this.fromJsonHelper.extractLongNamed(RescheduleLoansApiConstants.rescheduleReasonIdParamName,
                jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleReasonIdParamName).value(rescheduleReasonId).notNull()
                .integerGreaterThanZero();

        final BigDecimal emiAmount = this.fromJsonHelper.extractBigDecimalWithLocaleNamed(RescheduleLoansApiConstants.newInstallmentAmountParamName,
                jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.newInstallmentAmountParamName).value(emiAmount).ignoreIfNull().positiveAmount();
        if(!isBulkCreateAndApprove && emiAmount != null && (loan.repaymentScheduleDetail().getAmortizationMethod().isEqualPrincipal() || !loan.loanProduct().canDefineInstallmentAmount())){
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.newInstallmentAmountParamName).value(emiAmount).failWithCode("not.supported");
        }
        
        final String rescheduleReasonComment = this.fromJsonHelper.extractStringNamed(
                RescheduleLoansApiConstants.rescheduleReasonCommentParamName, jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleReasonCommentParamName).value(rescheduleReasonComment)
                .ignoreIfNull().notExceedingLengthOf(500);

        final LocalDate adjustedDueDate = this.fromJsonHelper.extractLocalDateNamed(RescheduleLoansApiConstants.adjustedDueDateParamName,
                jsonElement);
        
        if (adjustedDueDate != null) {
            if (isBulkCreateAndApprove) {
                String[] loans = jsonCommand.arrayValueOfParameterNamed(RescheduleLoansApiConstants.loansParamName);
                for (String loanId : loans) {
                    validateForCalendarMeetingDate(dataValidatorBuilder, Long.parseLong(loanId), adjustedDueDate);
                }
            } else {
                validateForCalendarMeetingDate(dataValidatorBuilder, loan.getId(), adjustedDueDate);
            }
        }
        
        LoanRepaymentScheduleInstallment installment = null;
        if (rescheduleFromDate != null && !isBulkCreateAndApprove) {
            installment = loan.getRepaymentScheduleInstallment(rescheduleFromDate);
            if (installment == null) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)
                        .failWithCode("repayment.schedule.installment.does.not.exist", "Repayment schedule installment does not exist");
            }

            if (!isBulkCreateAndApprove && installment != null && installment.isObligationsMet()) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)
                        .failWithCode("repayment.schedule.installment.obligation.met", "Repayment schedule installment obligation met");
            }
        }
        LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        if (adjustedDueDate != null && rescheduleFromDate != null && adjustedDueDate.isBefore(rescheduleFromDate)) {
            if (adjustedDueDate.isBefore(currentDate)) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName).failWithCode(
                        "reschedule.to.previous.date.not.allowed.before.current.date", "Reschedule to previous date not allowed before current date");
            } else if (installment != null && !adjustedDueDate.isAfter(installment.getFromDate())) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName).failWithCode(
                        "adjustedDueDate.cannot.be.before.last.installment.due.date",
                        "Adjusted due date cannot cannot be before last installment due date");
            }
        }

        // at least one of the following must be provided => graceOnPrincipal,
        // graceOnInterest, extraTerms, newInterestRate
        if (!this.fromJsonHelper.parameterExists(RescheduleLoansApiConstants.graceOnPrincipalParamName, jsonElement)
                && !this.fromJsonHelper.parameterExists(RescheduleLoansApiConstants.graceOnInterestParamName, jsonElement)
                && !this.fromJsonHelper.parameterExists(RescheduleLoansApiConstants.extraTermsParamName, jsonElement)
                && !this.fromJsonHelper.parameterExists(RescheduleLoansApiConstants.newInterestRateParamName, jsonElement)
                && !this.fromJsonHelper.parameterExists(RescheduleLoansApiConstants.adjustedDueDateParamName, jsonElement)
                && !this.fromJsonHelper.parameterExists(RescheduleLoansApiConstants.newInstallmentAmountParamName, jsonElement)) {
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.graceOnPrincipalParamName).notNull();
        }
        if(!isBulkCreateAndApprove){
            validateForOverdueCharges(dataValidatorBuilder, loan, installment);
        }
		if (extraTerms != null) {
			List<LoanTermVariations> loanTermVariations = new ArrayList<>();
			loanTermVariations = loan.getLoanTermVariations();
			if (!loanTermVariations.isEmpty()) {
				for (LoanTermVariations loanTermVariation : loanTermVariations) {
					if (loanTermVariation.getTermType().getValue() == LoanTermVariationType.EMI_AMOUNT.getValue()) {
						dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.extraTermsParamName)
								.failWithCode("cannot.be.extend.repayment.period.of.loan.having.fixed.emi",
										"Cannot be extend repayment period of loan having fixed emi");
					}
				}
			}
        }
        
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
    
    @SuppressWarnings("null")
    private void validateForCalendarMeetingDate(DataValidatorBuilder dataValidatorBuilder, final Long loanId,
            final LocalDate adjustedDueDate) {
        CalendarData calendar = this.calendarReadPlatformService.retrieveCollctionCalendarByEntity(loanId,
                CalendarEntityType.LOANS.getValue());
        if (calendar != null) {
            String recurringRule = calendar.getRecurrence();
            LocalDate seedDate = calendar.getStartDate();
            if (seedDate.isAfter(adjustedDueDate)) {
                CalendarData calendarHistoryData = this.calendarReadPlatformService
                        .retrieveCalendarHistoryByCalendarInstanceAndDueDate(adjustedDueDate.toDate(), calendar.getCalendarInstanceId());
                if (calendarHistoryData != null) {
                    recurringRule = calendarHistoryData.getRecurrence();
                    seedDate = calendarHistoryData.getStartDate();
                }
            }
            if (!CalendarUtils.isValidRecurringDate(recurringRule, seedDate, adjustedDueDate)) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.adjustedDueDateParamName).failWithCode(
                        "adjustedDueDate.should.be.calendar.meeting.date", "Adjusted due date should be a calander meeting date");
            }
        }
    }
    
    private void validateForOverdueCharges(DataValidatorBuilder dataValidatorBuilder, final Loan loan,
            final LoanRepaymentScheduleInstallment installment) {
        if (installment != null) {
            LocalDate rescheduleFromDate = installment.getFromDate();
            Collection<LoanCharge> charges = loan.getLoanCharges();
            for (LoanCharge loanCharge : charges) {
                if (loanCharge.isOverdueInstallmentCharge() && loanCharge.getDueLocalDate().isAfter(rescheduleFromDate)) {
                    dataValidatorBuilder.failWithCodeNoParameterAddedToErrorCode("not.allowed.due.to.overdue.charges");
                    break;
                }
            }
        }
    }

    /**
     * Validates a user request to approve a loan reschedule request
     * 
     * @param jsonCommand
     *            the JSON command object (instance of the JsonCommand class)
     * @return void
     **/
    public void validateForApproveAction(final JsonCommand jsonCommand, LoanRescheduleRequest loanRescheduleRequest) {
        final String jsonString = jsonCommand.json();

        if (StringUtils.isBlank(jsonString)) { throw new InvalidJsonException(); }

        final Type typeToken = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromJsonHelper.checkForUnsupportedParameters(typeToken, jsonString,
                RescheduleLoansApiConstants.APPROVE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(StringUtils
                .lowerCase(RescheduleLoansApiConstants.ENTITY_NAME));

        final JsonElement jsonElement = jsonCommand.parsedJson();

        final LocalDate approvedOnDate = this.fromJsonHelper.extractLocalDateNamed(RescheduleLoansApiConstants.approvedOnDateParam,
                jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.approvedOnDateParam).value(approvedOnDate).notNull();

        if (approvedOnDate != null && loanRescheduleRequest.getSubmittedOnDate().isAfter(approvedOnDate)) {
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.approvedOnDateParam)
                    .failWithCode("before.submission.date", "Approval date cannot be before the request submission date.");
        }

        LoanRescheduleRequestStatusEnumData loanRescheduleRequestStatusEnumData = LoanRescheduleRequestEnumerations
                .status(loanRescheduleRequest.getStatusEnum());

        if (!loanRescheduleRequestStatusEnumData.isPendingApproval()) {
            dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode(
                    "request.is.not.in.submitted.and.pending.state",
                    "Loan reschedule request approval is not allowed. "
                            + "Loan reschedule request is not in submitted and pending approval state.");
        }

        LocalDate rescheduleFromDate = loanRescheduleRequest.getRescheduleFromDate();
        final Loan loan = loanRescheduleRequest.getLoan();
        LoanRepaymentScheduleInstallment installment = null;
        if (loan != null) {

            if (!loan.status().isActive()) {
                dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode("loan.is.not.active", "Loan is not active");
            }

            if (rescheduleFromDate != null) {
                 installment = loan.getRepaymentScheduleInstallment(rescheduleFromDate);

                if (installment == null) {
                    dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode(
                            "loan.repayment.schedule.installment.does.not.exist", "Repayment schedule installment does not exist");
                }

                if (installment != null && installment.isObligationsMet()) {
                    dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode(
                            "loan.repayment.schedule.installment." + "obligation.met", "Repayment schedule installment obligation met");
                }
            }
        }
        
        validateForOverdueCharges(dataValidatorBuilder, loan, installment);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    /**
     * Validates a user request to reject a loan reschedule request
     * 
     * @param jsonCommand
     *            the JSON command object (instance of the JsonCommand class)
     * @return void
     **/
    public void validateForRejectAction(final JsonCommand jsonCommand, LoanRescheduleRequest loanRescheduleRequest) {
        final String jsonString = jsonCommand.json();

        if (StringUtils.isBlank(jsonString)) { throw new InvalidJsonException(); }

        final Type typeToken = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromJsonHelper
                .checkForUnsupportedParameters(typeToken, jsonString, RescheduleLoansApiConstants.REJECT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(StringUtils
                .lowerCase(RescheduleLoansApiConstants.ENTITY_NAME));

        final JsonElement jsonElement = jsonCommand.parsedJson();

        final LocalDate rejectedOnDate = this.fromJsonHelper.extractLocalDateNamed(RescheduleLoansApiConstants.rejectedOnDateParam,
                jsonElement);
        dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rejectedOnDateParam).value(rejectedOnDate).notNull();

        if (rejectedOnDate != null && loanRescheduleRequest.getSubmittedOnDate().isAfter(rejectedOnDate)) {
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rejectedOnDateParam)
                    .failWithCode("before.submission.date", "Rejection date cannot be before the request submission date.");
        }

        LoanRescheduleRequestStatusEnumData loanRescheduleRequestStatusEnumData = LoanRescheduleRequestEnumerations
                .status(loanRescheduleRequest.getStatusEnum());

        if (!loanRescheduleRequestStatusEnumData.isPendingApproval()) {
            dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode(
                    "request.is.not.in.submitted.and.pending.state",
                    "Loan reschedule request rejection is not allowed. "
                            + "Loan reschedule request is not in submitted and pending approval state.");
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
    
    public void validateForBulkCreateAndApproveAction(Loan loan, LocalDate rescheduleFromDate, LocalDate submittedOnDate,
            LocalDate adjustedDueDate, BigDecimal installmentAmount) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(StringUtils
                .lowerCase(RescheduleLoansApiConstants.ENTITY_NAME));

        if (!loan.status().isActive()) {
            dataValidatorBuilder.reset().failWithCodeNoParameterAddedToErrorCode("loan.is.not.active", "Loan is not active");
        }

        if (submittedOnDate != null && loan.getDisbursementDate().isAfter(submittedOnDate)) {
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.submittedOnDateParamName)
                    .failWithCode("before.loan.disbursement.date", "Submission date cannot be before the loan disbursement date");
        }

        if (installmentAmount != null
                && (loan.repaymentScheduleDetail().getAmortizationMethod().isEqualPrincipal() || !loan.loanProduct()
                        .canDefineInstallmentAmount())) {
            dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.newInstallmentAmountParamName).value(installmentAmount)
                    .failWithCode("not.supported");
        }

        if (adjustedDueDate != null && rescheduleFromDate != null && adjustedDueDate.isBefore(rescheduleFromDate)) {
            dataValidatorBuilder
                    .reset()
                    .parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)
                    .failWithCode("adjustedDueDate.before.rescheduleFromDate",
                            "Adjusted due date cannot be before the reschedule from date");
        }

        LoanRepaymentScheduleInstallment installment = null;
        if (rescheduleFromDate != null) {
            installment = loan.getRepaymentScheduleInstallment(rescheduleFromDate);

            if (installment == null) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)
                        .failWithCode("repayment.schedule.installment.does.not.exist", "Repayment schedule installment does not exist");
            }

            if (installment != null && installment.isObligationsMet()) {
                dataValidatorBuilder.reset().parameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)
                        .failWithCode("repayment.schedule.installment.obligation.met", "Repayment schedule installment obligation met");
            }

        }

        validateForOverdueCharges(dataValidatorBuilder, loan, installment);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }
}
