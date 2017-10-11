/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.integrationtests.savings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsProductHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsStatusChecker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class SavingsAccountOverDraftInterestPostingIntegrationTest {

    private final static String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
    private final static String SAVINGS_SUBMIT_DATE = "02 January 2017";

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private SavingsAccountHelper savingsAccountHelper;

    // These entities should be created only once
    private Integer officeId;
    private Integer clientId;
    private Integer savingsProductId;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        initializeRequiredEntities();
    }

    private void initializeRequiredEntities() {
        // Create a new Office
        final String officeCreationDate = "01 July 2007";
        OfficeHelper officeHelper = new OfficeHelper(requestSpec, responseSpec);
        officeId = officeHelper.createOffice(officeCreationDate);
        // Create a new client under newly created office
        final String clientActivationDate = "04 March 2011";
        clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec, clientActivationDate, officeId.toString());
        // Create new savings product with overdraft enabled
        final String zeroOpeningBalance = "0.0";
        final String minRequiredBalance = null;
        final String enforceMinRequiredBalance = "false";
        final boolean allowOverdraft = true;
        final String minBalanceForInterestCalculation = null;
        savingsProductId = createSavingsProduct(this.requestSpec, this.responseSpec, zeroOpeningBalance, minBalanceForInterestCalculation,
                minRequiredBalance, enforceMinRequiredBalance, allowOverdraft);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testPostInterestAsOnSavingsAccountWithDepositOnInterstPostDateWithOverdraft() {
        final int totalTransactions = 6;
        final String[] withdrawaltransactonDates = { "06 January 2017", "06 January 2017", "16 February 2017", "16 February 2017",
                "14 March 2017", "17 March 2017" };
        final String[] withdrawaltransactonAmounts = { "10000", "10000", "20000", "5000", "7000", "23456" };
        String interestPostingDate = "28 March 2017";
        String depositAmount = "76443.81";

        // Create savings account
        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId, savingsProductId,
                ACCOUNT_TYPE_INDIVIDUAL, SAVINGS_SUBMIT_DATE);
        Assert.assertNotNull(savingsId);
        // Approve the Savings Account
        HashMap savingsStatusHashMap = this.savingsAccountHelper.approveSavingsOnDate(savingsId, SAVINGS_SUBMIT_DATE);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);
        // Activate Savings Account
        savingsStatusHashMap = this.savingsAccountHelper.activateSavingsOnDate(savingsId, SAVINGS_SUBMIT_DATE);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        for (int i = 0; i < totalTransactions; i++) {
            Integer withdrawTransactionId = (Integer) this.savingsAccountHelper.withdrawalFromSavingsAccount(savingsId,
                    withdrawaltransactonAmounts[i], withdrawaltransactonDates[i], CommonConstants.RESPONSE_RESOURCE_ID);
            Assert.assertNotNull(withdrawTransactionId);
        }

        // Get the transactions and verify
        HashMap savingsAccount = this.savingsAccountHelper.getSavingsDetails(savingsId);
        List transactions = (List) savingsAccount.get("transactions");
        // Expected data
        String[] transactionDates = { "6-1-2017", "6-1-2017", "16-2-2017", "16-2-2017", "14-3-2017", "17-3-2017" };
        String[] amounts = { "10000.0", "10000.0", "20000.0", "5000.0", "7000.0", "23456.0" };
        String[] runningBalances = { "-10000.0", "-20000.0", "-40000.0", "-45000.0", "-52000.0", "-75456.0" };
        List<Transaction> expected = retrieveTransactions(transactionDates, amounts, runningBalances);
        List<Transaction> derived = parseTransactions(transactions);
        boolean checkAllPostingPeriods = true;
        assertTransactions(expected, derived, checkAllPostingPeriods);

        // Posting the interest on specific date
        this.savingsAccountHelper.postInterestAsOnSavings(savingsId, interestPostingDate);

        // Get the transactions and verify again
        savingsAccount = this.savingsAccountHelper.getSavingsDetails(savingsId);
        transactions = (List) savingsAccount.get("transactions");
        derived = parseTransactions(transactions);
        // Expected data
        String[] transactionDatesAfterInterestPosting = { "6-1-2017", "6-1-2017", "1-2-2017", "16-2-2017", "16-2-2017", "1-3-2017",
                "14-3-2017", "17-3-2017", "28-3-2017", "1-4-2017" };
        String[] amountsAfterInterestPosting = { "10000.0", "10000.0", "171.66", "20000.0", "5000.0", "293.58", "7000.0", "23456.0",
                "522.57", "100.58" };
        String[] runningBalancesAfterInterestPosting = { "-10000.0", "-20000.0", "-20171.66", "-40171.66", "-45171.66", "-45465.24",
                "-52465.24", "-75921.24", "-76443.81", "-76544.39" };
        expected = retrieveTransactions(transactionDatesAfterInterestPosting, amountsAfterInterestPosting,
                runningBalancesAfterInterestPosting);
        checkAllPostingPeriods = false;
        assertTransactions(expected, derived, checkAllPostingPeriods);

        // Deposit the amount so that running balance will be zero
        Integer depositTransactionId = (Integer) this.savingsAccountHelper.depositToSavingsAccount(savingsId, depositAmount,
                interestPostingDate, CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(depositTransactionId);

        // Get the transactions and verify again
        savingsAccount = this.savingsAccountHelper.getSavingsDetails(savingsId);
        transactions = (List) savingsAccount.get("transactions");
        derived = parseTransactions(transactions);
        // Expected data
        String[] transactionDatesAfterDeposit = { "6-1-2017", "6-1-2017", "1-2-2017", "16-2-2017", "16-2-2017", "1-3-2017", "14-3-2017",
                "17-3-2017", "28-3-2017", "28-3-2017" };
        String[] amountsAfterDeposit = { "10000.0", "10000.0", "171.66", "20000.0", "5000.0", "293.58", "7000.0", "23456.0", "522.57",
                "76443.81" };
        String[] runningBalancesAfterDeposit = { "-10000.0", "-20000.0", "-20171.66", "-40171.66", "-45171.66", "-45465.24", "-52465.24",
                "-75921.24", "-76443.81", "0.0" };
        expected = retrieveTransactions(transactionDatesAfterDeposit, amountsAfterDeposit, runningBalancesAfterDeposit);
        checkAllPostingPeriods = true;
        assertTransactions(expected, derived, checkAllPostingPeriods);
    }

    private void assertTransactions(final List<Transaction> expected, final List<Transaction> derived, final boolean checkAllPostingPeriods) {
        if(checkAllPostingPeriods){
            Assert.assertEquals("Transaction Size is mismatch", expected.size(), derived.size());
        }
        for (Transaction transaction : expected) {
            if (!derived.contains(transaction)) {
                String message = "Expected Transaction with date " + transaction.transactionDate + " with amount "
                        + transaction.transactonAmount + " with running balance " + transaction.runningBalance + "not found";
                Assert.fail(message);
            }
        }
    }

    private Integer createSavingsProduct(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String minOpenningBalance, String minBalanceForInterestCalculation, String minRequiredBalance,
            String enforceMinRequiredBalance, final boolean allowOverdraft) {
        final String taxGroupId = null;
        return createSavingsProduct(requestSpec, responseSpec, minOpenningBalance, minBalanceForInterestCalculation, minRequiredBalance,
                enforceMinRequiredBalance, allowOverdraft, taxGroupId, false);
    }

    private Integer createSavingsProduct(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String minOpenningBalance, String minBalanceForInterestCalculation, String minRequiredBalance,
            String enforceMinRequiredBalance, final boolean allowOverdraft, final String taxGroupId, boolean withDormancy) {
        System.out.println("------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");
        SavingsProductHelper savingsProductHelper = new SavingsProductHelper();
        if (allowOverdraft) {
            final String overDraftLimit = "100000.0";
            savingsProductHelper = savingsProductHelper.withOverDraft(overDraftLimit);
            savingsProductHelper = savingsProductHelper.withnominalAnnualInterestRateOverdraft("12.0");
        }
        if (withDormancy) {
            savingsProductHelper = savingsProductHelper.withDormancy();
        }

        final String savingsProductJSON = savingsProductHelper
                //
                .withNominalInterest("12.0")
                //
                .withDigitsAfterDecimal("2")
                //
                .withInterestCompoundingPeriodTypeAsDaily()
                //
                .withInterestPostingPeriodTypeAsMonthly()
                //
                .withInterestCalculationPeriodTypeAsDailyBalance()
                //
                .withMinBalanceForInterestCalculation(minBalanceForInterestCalculation)
                //
                .withMinRequiredBalance(minRequiredBalance).withEnforceMinRequiredBalance(enforceMinRequiredBalance)
                .withMinimumOpenningBalance(minOpenningBalance).withWithHoldTax(taxGroupId).build();
        return SavingsProductHelper.createSavingsProduct(savingsProductJSON, requestSpec, responseSpec);
    }

    private List<Transaction> parseTransactions(List<HashMap<String, Object>> mapTransactions) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (HashMap<String, Object> map : mapTransactions) {
            String amount = map.get("amount").toString();
            BigDecimal value = new BigDecimal(amount);
            Boolean isRevered = (Boolean) map.get("reversed");
            if (!value.toString().equals("0.0") && !isRevered) {
                List<Integer> date = (List<Integer>) map.get("date");
                String runningBalance = map.get("runningBalance").toString();
                transactions.add(new Transaction(parseDate(date), amount, runningBalance));
            }

        }
        return transactions;
    }

    private List<Transaction> retrieveTransactions(final String[] transactionDates, final String[] amounts,
            final String[] runningBalances) {
        int length = transactionDates.length;
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            transactions.add(new Transaction(transactionDates[i], amounts[i], runningBalances[i]));
        }
        return transactions;
    }

    private String parseDate(final List<Integer> dateValues) {
        String date = "";
        if (dateValues.size() == 3) {
            date = date + dateValues.get(2) + "-" + dateValues.get(1).toString() + "-" + dateValues.get(0).toString();
        }
        return date;
    }

    class Transaction {

        final String transactionDate;
        final String transactonAmount;
        final String runningBalance;

        public Transaction(String transactionDate, String transactonAmount, String runningBalance) {
            super();
            this.transactionDate = transactionDate;
            this.transactonAmount = transactonAmount;
            this.runningBalance = runningBalance;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(this.getClass())) return false;
            Transaction comparable = (Transaction) obj;
            return comparable.transactionDate.equals(transactionDate) && comparable.transactonAmount.equals(transactonAmount)
                    && comparable.runningBalance.equals(runningBalance);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
