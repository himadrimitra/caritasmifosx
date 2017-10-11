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
package org.apache.fineract.infrastructure.jobs.service;

public enum JobName {

    UPDATE_LOAN_SUMMARY("Update loan Summary"), //
    UPDATE_LOAN_ARREARS_AGEING("Update Loan Arrears Ageing"), //
    UPDATE_LOAN_PAID_IN_ADVANCE("Update Loan Paid In Advance"), //
    APPLY_ANNUAL_FEE_FOR_SAVINGS("Apply Annual Fee For Savings"), //
    APPLY_HOLIDAYS("Apply Holidays"), //
    POST_INTEREST_FOR_SAVINGS("Post Interest For Savings"), //
    TRANSFER_FEE_CHARGE_FOR_LOANS("Transfer Fee For Loans From Savings"), //
    ACCOUNTING_RUNNING_BALANCE_UPDATE("Update Accounting Running Balances"), //
    PAY_DUE_SAVINGS_CHARGES("Pay Due Savings Charges"), //
    APPLY_CHARGE_TO_OVERDUE_LOAN_INSTALLMENT("Apply penalty to overdue loans"),
    ADD_ACCRUAL_ENTRIES("Add Accrual Transactions"),
    RECALCULATE_INTEREST_FOR_LOAN("Recalculate Interest For Loans"),
    APPY_SAVING_DEPOSITE_LATE_FEE("Apply Saving Deposite Late Fee"),
    UPDATE_CLIENT_SUB_STATUS("Update Client Sub-Status"),
    LOAN_REPAYMENT_SMS_REMINDER_TO_CLIENT("Loan Repayment Sms Reminder"),
    LOAN_FIRST_OVERDUE_REPAYMENT_REMINDER_SMS("Loan First Overdue Repayment Reminder"),
    LOAN_SECOND_OVERDUE_REPAYMENT_REMINDER_SMS("Loan Second Overdue Repayment Reminder"), 
    LOAN_THIRD_OVERDUE_REPAYMENT_REMINDER_SMS("Loan Third Overdue Repayment Reminder"),
    LOAN_FOURTH_OVERDUE_REPAYMENT_REMINDER_SMS("Loan Fourth Overdue Repayment Reminder"),
    DEFAULT_WARNING_SMS_TO_CLIENT("Default Wring Sms To Client"),
    DEFAULT_WARNING_SMS_TO_GURANTOR("Default Warning Sms To gurantor"),
    DORMANCY_WARNING_SMS_TO_CLIENT("Dormancy Warning Sms To Clients"),
    DO_INVESTMENT_DISTRIBUTION("Distribute Investment Earning"),

    OVERDUE_CALCULATIONS_FOR_LOANS("Overdue Calculations For Loans"), //
    APPLY_PENALTY_CHARGE_FOR_BROKEN_PERIODS("Apply Penalty For Broken Periods"), //
    EXECUTE_STANDING_INSTRUCTIONS("Execute Standing Instruction"), //
    ADD_DUE_DATE_ACCRUAL_ENTRIES("Add Due Date Accrual Transactions"), //
    UPDATE_NPA("Update Non Performing Assets"), //
    UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS("Update Deposit Accounts Maturity details"), //
    TRANSFER_INTEREST_TO_SAVINGS("Transfer Interest To Savings"), //
    ADD_PERIODIC_ACCRUAL_ENTRIES("Add Periodic Accrual Transactions"), //
    GENERATE_RD_SCEHDULE("Generate Mandatory Savings Schedule"), //
    GENERATE_LOANLOSS_PROVISIONING("Generate Loan Loss Provisioning"), //
    POST_DIVIDENTS_FOR_SHARES("Post Dividends For Shares"), //
    UPDATE_SAVINGS_DORMANT_ACCOUNTS("Update Savings Dormant Accounts"), //
    ADD_PERIODIC_ACCRUAL_ENTRIES_FOR_LOANS_WITH_INCOME_POSTED_AS_TRANSACTIONS("Add Accrual Transactions For Loans With Income Posted As Transactions"),
    SEND_MESSAGES_TO_SMS_GATEWAY("Send messages to SMS gateway"), 
    GET_DELIVERY_REPORTS_FROM_SMS_GATEWAY("Get delivery reports from SMS gateway"),
    UPDATE_SMS_OUTBOUND_WITH_CAMPAIGN_MESSAGE("Update Sms Outbound with campaign message"),
    EXECUTE_REPORT_MAILING_JOBS("Execute Report Mailing Jobs"),
    APPLY_RECURRING_CHARGE_ON_CLIENT("Apply Recurring Charge On Client"),
    INITIATE_BANK_TRANSACTION("Initiate Bank Transactions"),
    UPDATE_BANK_TRANSACTION_STATUS("Update Bank Transaction Status"),
    HIGHMARK_ENQUIRY("Highmark Enquiry"),
    REDUCE_DP_LIMIT_FOR_SAVINGS("Reduce Dp Limit For Savings"),
    FUND_STATUS_UPDATE("Fund Status update"),
    UPDATE_NEXT_RECURRING_DATE("Update Next Recurring Date");

    private final String name;

    private JobName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}