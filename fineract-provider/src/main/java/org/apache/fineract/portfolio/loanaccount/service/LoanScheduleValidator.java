/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.util.List;
import java.util.Set;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.service.HolidayUtil;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.calendar.data.CalendarHistoryDataWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.calendar.exception.MeetingFrequencyMismatchException;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.apache.fineract.portfolio.loanaccount.exception.MinDaysBetweenDisbursalAndFirstRepaymentViolationException;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanScheduleValidator {

    private final ConfigurationDomainService configurationDomainService;
    private LoanUtilService loanUtilService;

    @Autowired
    public LoanScheduleValidator(final ConfigurationDomainService configurationDomainService) {
        this.configurationDomainService = configurationDomainService;
    }

    @Autowired
    public void setLoanUtilService(LoanUtilService loanUtilService) {
        this.loanUtilService = loanUtilService;
    }

    public void validateMinimumDaysBetweenDisbursalAndFirstRepayment(LocalDate repaymentsStartingFromDate, Loan loan,
            LocalDate disbursementDate) {

        final PeriodFrequencyType repaymentFrequencyType = loan.getLoanProductRelatedDetail().getRepaymentPeriodFrequencyType();
        final Integer repaymentEvery = loan.getLoanProductRelatedDetail().getRepayEvery();
        final Integer nthDay = null;
        final Integer dayOfWeek = null;
        final DayOfWeekType weekDayType = DayOfWeekType.fromInt(dayOfWeek);
        LoanProduct loanProduct = loan.loanProduct();
        validateMinimumDaysBetweenDisbursalAndFirstRepayment(disbursementDate, repaymentsStartingFromDate, loanProduct,
                repaymentFrequencyType, repaymentEvery, nthDay, weekDayType);

    }

    public void validateMinimumDaysBetweenDisbursalAndFirstRepayment(final LocalDate disbursalDate, final LocalDate firstRepaymentDate,
            final LoanProduct loanProduct, final PeriodFrequencyType loanTermPeriodFrequencyType, final Integer repaymentEvery,
            Integer nthDay, DayOfWeekType dayOfWeek) {

        final Integer calculatedminimumDaysBetweenDisbursalAndFirstRepayment = this.loanUtilService
                .calculateMinimumDaysBetweenDisbursalAndFirstRepayment(disbursalDate, loanProduct, loanTermPeriodFrequencyType,
                        repaymentEvery, nthDay, dayOfWeek);
        validateMinimumDaysBetweenDisbursalAndFirstRepayment(disbursalDate, firstRepaymentDate, loanProduct,
                calculatedminimumDaysBetweenDisbursalAndFirstRepayment);
    }

    public void validateMinimumDaysBetweenDisbursalAndFirstRepayment(final LocalDate disbursalDate, final LocalDate firstRepaymentDate,
            final LoanProduct loanProduct, final Integer calculatedminimumDaysBetweenDisbursalAndFirstRepayment) {
        final Integer minimumDaysBetweenDisbursalAndFirstRepayment = loanProduct.getMinimumDaysBetweenDisbursalAndFirstRepayment();
        Integer minumumPeriodsBetweenDisbursalAndFirstrepaymentdate = loanProduct.getMinimumPeriodsBetweenDisbursalAndFirstRepayment();
        if (minimumDaysBetweenDisbursalAndFirstRepayment > 0 || minumumPeriodsBetweenDisbursalAndFirstrepaymentdate > 0) {
            validateMinimumDaysBetweenDisbursalAndFirstRepayment(disbursalDate, firstRepaymentDate,
                    calculatedminimumDaysBetweenDisbursalAndFirstRepayment);
        }
    }

    public void validateMinimumDaysBetweenDisbursalAndFirstRepayment(final LocalDate disbursalDate, final LocalDate firstRepaymentDate,
            final Integer minimumDaysBetweenDisbursalAndFirstRepayment) {

        final LocalDate minimumFirstRepaymentDate = disbursalDate.plusDays(minimumDaysBetweenDisbursalAndFirstRepayment);
        if (firstRepaymentDate.isBefore(minimumFirstRepaymentDate)) {
            final String errorMessage = "error.msg.loan.days.between.first.repayment.and.disbursal.are.less.than.minimum.days.or.periods.allowed";
            final String developerErrorMessage = "Number of days between loan disbursal  (" + disbursalDate + ") and first repayment ("
                    + firstRepaymentDate + ") can't be less than (" + minimumFirstRepaymentDate + ").";
            throw new MinDaysBetweenDisbursalAndFirstRepaymentViolationException(errorMessage, developerErrorMessage, disbursalDate,
                    firstRepaymentDate, minimumDaysBetweenDisbursalAndFirstRepayment);
        }
    }

    /*
     * validate repayment start date with meeting date validate Disbursement
     * start date with meeting date validate Minimum Days Between Disbursal And
     * FirstRepayment
     */
    public void validateRepaymentAndDisbursementDateWithMeetingDateAndMinimumDaysBetweenDisbursalAndFirstRepayment(
            LocalDate calculatedRepaymentsStartingFromDate, final LocalDate expectedDisbursementDate, final Calendar calendar,
            Integer minimumDaysBetweenDisbursalAndFirstRepayment, final AccountType loanType, final Boolean synchDisbursement) {
        if (calendar != null) {
            boolean isSkipMeetingOnFirstDay = this.configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
            int numberOfDays = this.configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
            if ((loanType.isJLGAccount() || loanType.isGroupAccount())) {
                validateRepaymentsStartDateWithMeetingDates(calculatedRepaymentsStartingFromDate, calendar, isSkipMeetingOnFirstDay,
                        numberOfDays);
                if (synchDisbursement != null && synchDisbursement.booleanValue()) {
                    validateDisbursementDateWithMeetingDates(expectedDisbursementDate, calendar, isSkipMeetingOnFirstDay, numberOfDays);
                }
            }
        }

        if (calculatedRepaymentsStartingFromDate != null) {
            validateMinimumDaysBetweenDisbursalAndFirstRepayment(expectedDisbursementDate, calculatedRepaymentsStartingFromDate,
                    minimumDaysBetweenDisbursalAndFirstRepayment);
        }

    }

    public void validateRepaymentsStartDateWithMeetingDates(final LocalDate repaymentsStartingFromDate, final Calendar calendar,
            boolean isSkipRepaymentOnFirstDayOfMonth, final Integer numberOfDays) {
        if (repaymentsStartingFromDate != null && calendar != null) {
            Set<CalendarHistory> calendarHistory = calendar.getActiveCalendarHistory();
            final CalendarHistoryDataWrapper calendarHistoryDataWrapper = new CalendarHistoryDataWrapper(calendarHistory);
            CalendarHistory history = calendarHistoryDataWrapper.getCalendarHistory(repaymentsStartingFromDate);
            String recurringRule = calendar.getRecurrence();
            LocalDate seedDate = calendar.getStartDateLocalDate();
            if (history != null) {
                recurringRule = history.getRecurrence();
                seedDate = history.getStartDateLocalDate();
            }
            if (!CalendarUtils.isValidRedurringDate(recurringRule, seedDate, repaymentsStartingFromDate, isSkipRepaymentOnFirstDayOfMonth,
                    numberOfDays)) {
                final String errorMessage = "First repayment date '" + repaymentsStartingFromDate + "' do not fall on a meeting date";
                throw new LoanApplicationDateException("first.repayment.date.do.not.match.meeting.date", errorMessage,
                        repaymentsStartingFromDate);
            }
        }
    }

    public void validateDisbursementDateWithMeetingDates(final LocalDate expectedDisbursementDate, final Calendar calendar,
            Boolean isSkipRepaymentOnFirstMonth, Integer numberOfDays) {
        // disbursement date should fall on a meeting date
        if (!calendar.isValidRecurringDate(expectedDisbursementDate, isSkipRepaymentOnFirstMonth, numberOfDays)) {
            final String errorMessage = "Expected disbursement date '" + expectedDisbursementDate + "' do not fall on a meeting date";
            throw new LoanApplicationDateException("disbursement.date.do.not.match.meeting.date", errorMessage, expectedDisbursementDate);
        }

    }

    /*
     * validate Disbursement date on holiday validate Disbursement date on Non
     * Working Day
     */
    public void validateDisbursementDateIsOnHolidayOrNonWorkingDay(final LocalDate disbursementDate, final WorkingDays workingDays,
            final boolean isHolidayEnabled, final List<Holiday> holidays) {
        if (isHolidayEnabled) {
            validateDisbursementDateIsOnHoliday(disbursementDate, isHolidayEnabled, holidays);
        }
        validateDisbursementDateIsOnNonWorkingDay(disbursementDate, workingDays);
    }

    public void validateDisbursementDateIsOnNonWorkingDay(final LocalDate disbursementDate, final WorkingDays workingDays) {
        if (!WorkingDaysUtil.isWorkingDay(workingDays, disbursementDate)) {
            final String errorMessage = "The expected disbursement date cannot be on a non working day";
            throw new LoanApplicationDateException("disbursement.date.on.non.working.day", errorMessage, disbursementDate);
        }
    }

    public void validateDisbursementDateIsOnHoliday(final LocalDate disbursementDate, final boolean isHolidayEnabled,
            final List<Holiday> holidays) {
        if (isHolidayEnabled) {
            if (HolidayUtil.isHoliday(disbursementDate, holidays)) {
                final String errorMessage = "The expected disbursement date cannot be on a holiday";
                throw new LoanApplicationDateException("disbursement.date.on.holiday", errorMessage, disbursementDate);
            }
        }
    }

    /*
     * validate RepaymentFrequency Is Same As Meeting Frequency validate Minimum
     * Days Between Disbursal And FirstRepayment
     */
    public void validateRepaymentFrequencyAsMeetingFrequencyAndMinimumDaysBetweenDisbursalAndFirstRepayment(Loan loan, Calendar calendar) {
        final AccountType loanType = AccountType.fromInt(loan.getLoanType());
        if ((loanType.isJLGAccount() || loanType.isGroupAccount()) && calendar != null) {
            final PeriodFrequencyType meetingPeriodFrequency = CalendarUtils.getMeetingPeriodFrequencyType(calendar.getRecurrence());
            final Integer repaymentFrequencyType = loan.getLoanProductRelatedDetail().getRepaymentPeriodFrequencyType().getValue();
            final Integer repaymentEvery = loan.getLoanProductRelatedDetail().getRepayEvery();
            validateRepaymentFrequencyIsSameAsMeetingFrequency(meetingPeriodFrequency.getValue(), repaymentFrequencyType,
                    CalendarUtils.getInterval(calendar.getRecurrence()), repaymentEvery);
        }

        LocalDate repaymentsStartingFromDate = loan.getExpectedFirstRepaymentOnDate();
        if (repaymentsStartingFromDate != null) {
            LocalDate expectedDisbursementDate = loan.getExpectedDisbursedOnLocalDate();
            validateMinimumDaysBetweenDisbursalAndFirstRepayment(repaymentsStartingFromDate, loan, expectedDisbursementDate);
        }
    }

    public void validateRepaymentFrequencyIsSameAsMeetingFrequency(final Integer meetingFrequency, final Integer repaymentFrequency,
            final Integer meetingInterval, final Integer repaymentInterval) {
        // meeting with daily frequency should allow loan products with any
        // frequency.
        if (!PeriodFrequencyType.DAYS.getValue().equals(meetingFrequency)) {
            // repayment frequency must match with meeting frequency
            if (!meetingFrequency.equals(repaymentFrequency)) {
                throw new MeetingFrequencyMismatchException("loanapplication.repayment.frequency",
                        "Loan repayment frequency period must match that of meeting frequency period", repaymentFrequency);
            } else if (meetingFrequency.equals(repaymentFrequency)) {
                // repayment frequency is same as meeting frequency repayment
                // interval should be same or multiple of meeting interval
                String userMessageGlobalisationCode = "loanapplication.repayment.interval";
                String developerMessage = null;
                if (configurationDomainService.isForceLoanRepaymentFrequencyMatchWithMeetingFrequencyEnabled()
                        && (repaymentInterval != meetingInterval)) {
                    developerMessage = "Loan repayment frequency period must match that of meeting frequency period and "
                            + "repayment repaid every # must equal  meeting interval " + meetingInterval;
                } else if (repaymentInterval % meetingInterval != 0) {
                    // throw exception: Loan product frequency/interval
                    developerMessage = "Loan repayment repaid every # must equal or multiple of meeting interval " + meetingInterval;
                }
                if (developerMessage != null) { throw new MeetingFrequencyMismatchException(userMessageGlobalisationCode, developerMessage,
                        meetingInterval, repaymentInterval); }
            }
        }
    }

    public void validateSubmittedOnDate(final LocalDate submittedOnDate, final LocalDate expectedFirstRepaymentOnDate,
            final LoanProduct loanProduct, final Client client) {

        final LocalDate startDate = loanProduct.getStartDate();
        final LocalDate closeDate = loanProduct.getCloseDate();
        String defaultUserMessage = "";

        if (client != null) {
            final LocalDate activationDate = client.getActivationLocalDate();

            if (activationDate == null) {
                defaultUserMessage = "Client is not activated.";
                throw new LoanApplicationDateException("client.is.not.activated", defaultUserMessage);
            }

            if (submittedOnDate.isBefore(activationDate)) {
                defaultUserMessage = "Submitted on date cannot be before the client activation date.";
                throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.client.activation.date", defaultUserMessage,
                        submittedOnDate.toString(), activationDate.toString());
            }
        }

        if (startDate != null && submittedOnDate.isBefore(startDate)) {
            defaultUserMessage = "submittedOnDate cannot be before the loan product startDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.loan.product.start.date", defaultUserMessage,
                    submittedOnDate.toString(), startDate.toString());
        }

        if (closeDate != null && submittedOnDate.isAfter(closeDate)) {
            defaultUserMessage = "submittedOnDate cannot be after the loan product closeDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.after.the.loan.product.close.date", defaultUserMessage,
                    submittedOnDate.toString(), closeDate.toString());
        }

        if (expectedFirstRepaymentOnDate != null && submittedOnDate.isAfter(expectedFirstRepaymentOnDate)) {
            defaultUserMessage = "submittedOnDate cannot be after the loans  expectedFirstRepaymentOnDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.after.the.loan.expected.first.repayment.date",
                    defaultUserMessage, submittedOnDate.toString(), expectedFirstRepaymentOnDate.toString());
        }
    }

}
