/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.integrationtests.savings;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.google.protobuf.TextFormat.ParseException;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class SavingsAccountInterestPostingIntegrationTest {

    private final static String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
    private final static String SAVINGS_SUBMIT_DATE = "02 January 2017";

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private SavingsAccountHelper savingsAccountHelper;

    // These entities should be created only once
    private Integer officeId;
    private Integer clientId;
    private Integer savingsProductId;
    private boolean isInitialized = false ;
    
    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        if(!isInitialized) {
            initializeRequiredEntities();
            this.isInitialized = true ;
        }
    }

    private void initializeRequiredEntities() {
        // Create a new Office
        final String officeCreationDate = "01 July 2007";
        OfficeHelper officeHelper = new OfficeHelper(requestSpec, responseSpec);
        officeId = officeHelper.createOffice(officeCreationDate);
        // Create a new client under newly created office
        final String clientActivationDate = "04 March 2011";
        clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec, clientActivationDate, officeId.toString());
       
    }
    
    @Test
    public void testCreateProductWithInterestRateChart() throws Exception {
        final String zeroOpeningBalance = "0.0";
        final String minRequiredBalance = null;
        final String minBalanceForInterestCalculation = null;
        List<HashMap<String, String>> floatingInterestRateChart = createFloatingInterestRateChart();
        savingsProductId = createSavingsProduct(this.requestSpec, this.responseSpec, zeroOpeningBalance, minBalanceForInterestCalculation,
                minRequiredBalance, floatingInterestRateChart);
        
        List<HashMap<String, Object>> interestRateChart = SavingsProductHelper.getSavingsProduct(this.requestSpec, this.responseSpec, savingsProductId);
        System.out.println("--------------------------CHANGES----------------------"+interestRateChart); 
        List<HashMap<String, String>> parsedInterestRateChart = parseInterestRateChart(interestRateChart);
        assertFloatingInterestRateChart(parsedInterestRateChart, floatingInterestRateChart);
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testPostInterestSavingsAccountWithInterestRateChart() {
        final String zeroOpeningBalance = "0.0";
        final String minRequiredBalance = null;
        final String minBalanceForInterestCalculation = null;
        List<HashMap<String, String>>  floatingInterestRateChart = null;
        savingsProductId = createSavingsProduct(this.requestSpec, this.responseSpec, zeroOpeningBalance, minBalanceForInterestCalculation,
                minRequiredBalance, floatingInterestRateChart);
        String interestPostingDate = "28 March 2017";
        String depositAmount = "5000";
        floatingInterestRateChart = createFloatingInterestRateChart();
        HashMap changes = updateSavingsProduct(this.requestSpec, this.responseSpec, floatingInterestRateChart);
        List<HashMap<String, String>> floatingInterestRateChartChanges = (List<HashMap<String, String>>)changes.get("floatingInterestRateChart");
        Assert.assertNotNull(floatingInterestRateChartChanges);
        System.out.println("--------------------------CHANGES----------------------"+floatingInterestRateChartChanges); 
        
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
        //
        Integer depositTransactionId = (Integer) this.savingsAccountHelper.depositToSavingsAccount(savingsId, depositAmount,
                interestPostingDate, CommonConstants.RESPONSE_RESOURCE_ID);
       
        
        // Posting the interest on specific date
        this.savingsAccountHelper.postInterestAsOnSavings(savingsId, interestPostingDate);
        
        // Get the transactions and verify
        HashMap savingsAccount = this.savingsAccountHelper.getSavingsDetails(savingsId);
        
        List transactions = (List) savingsAccount.get("transactions"); 
        
        List<Transaction> derivedTransactions = parseTransactions(transactions);
        
        System.out.println("--------------------------CHANGES----------------------"+transactions); 
        assertTransactions(derivedTransactions);
       
    }
    


    private void assertTransactions( final List<Transaction> derivedTransaction) {
        
        for (Transaction transaction : derivedTransaction) {
            if(transaction.getTransactionDate() == "1-4-2017"){
                Assert.assertEquals("Transaction Amount is not as expected", transaction.getTransactonAmount(), "0.07");
            }
        }
    }

   
    private Integer createSavingsProduct(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            String minBalanceForInterestCalculation, String minRequiredBalance,
            String enforceMinRequiredBalance, List<HashMap<String, String>>  floatingInterestRateChart) {
        System.out.println("------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");
        SavingsProductHelper savingsProductHelper = new SavingsProductHelper();
        

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
                .withFloatingInterestRateChart(floatingInterestRateChart)
                //
                .withMinRequiredBalance(minRequiredBalance).withEnforceMinRequiredBalance(enforceMinRequiredBalance).build();
        return SavingsProductHelper.createSavingsProduct(savingsProductJSON, requestSpec, responseSpec);
    }
    
    private  HashMap updateSavingsProduct(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final List<HashMap<String, String>> floatingInterestRateChart){
        System.out.println("------------------------------UPDATE  SAVINGS PRODUCT ---------------------------------------");
        SavingsProductHelper savingsProductHelper = new SavingsProductHelper();
        

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
                .withFloatingInterestRateChart(floatingInterestRateChart).build();
        return SavingsProductHelper.updateSavingsProduct(savingsProductJSON, requestSpec, responseSpec, savingsProductId);
    }
    

    private List<Transaction> parseTransactions(List<HashMap<String, Object>> mapTransactions) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (HashMap<String, Object> map : mapTransactions) {
            String amount = map.get("amount").toString();
            BigDecimal value = new BigDecimal(amount);
            Boolean isRevered = (Boolean) map.get("reversed");
            if (!value.toString().equals("0.0") && !isRevered) {
                List<Integer> effectiveFromDate = (List<Integer>) map.get("date");
                String runningBalance = map.get("runningBalance").toString();
                transactions.add(new Transaction(parseDate(effectiveFromDate), amount, runningBalance));
            }

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
    
    private List<HashMap<String, String>> createFloatingInterestRateChart(){
        
        final List<HashMap<String, String>> interestRateChart = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        String effectiveFroDate  = dateFormat.format(todaysDate.getTime());
        interestRateChart.add(createInterestRateChartObject(effectiveFroDate, "10.0"));
        todaysDate.add(Calendar.MONTH, 2);
        effectiveFroDate  = dateFormat.format(todaysDate.getTime());
        interestRateChart.add(createInterestRateChartObject(effectiveFroDate, "20.0"));
        return interestRateChart;
    }
    
    private HashMap<String, String> createInterestRateChartObject(final String effectiveFromDate , final String interestRate){
        final HashMap<String, String> map = new HashMap<>();
        map.put("interestRate", interestRate);
        map.put("effectiveFromDate", effectiveFromDate);
        return map;
    }
    
    private List<HashMap<String, String>> parseInterestRateChart(List<HashMap<String, Object>> interestRateChart) throws Exception {
        List<HashMap<String, String>> parsedInterestRateChart = new ArrayList<>();
        for(HashMap<String, Object> map :interestRateChart){
            final HashMap<String, String> interetRateMap = new HashMap<>();
            String interestRate = map.get("interestRate").toString();
            interetRateMap.put("interestRate", interestRate);
            String effectiveFromDate = map.get("effectiveFromDate").toString();
            interetRateMap.put("effectiveFromDate", effectiveFromDate);
            parsedInterestRateChart.add(interetRateMap);
        }
        return parsedInterestRateChart;
    }
    
    private void assertFloatingInterestRateChart(List<HashMap<String, String>> derrived,
            List<HashMap<String, String>> floatingInterestRateChart) {
        for(HashMap<String, String> interestRateChartmap :floatingInterestRateChart){
            String paasedDate = interestRateChartmap.get("effectiveFromDate");
            Date date = new Date(paasedDate);
            int year = date.getYear();
            year += 1900;
            int month = date.getMonth();
            month += 1;
            paasedDate = "["+year+", "+month+", "+date.getDate()+"]";
            String passedRate = interestRateChartmap.get("interestRate").toString(); 
            boolean rateChartExist = false;
            for(HashMap<String, String> derrivedChart : derrived){
                if(paasedDate.equals(derrivedChart.get("effectiveFromDate")) && passedRate.equals(derrivedChart.get("interestRate"))){
                    rateChartExist = true;
                }
            }
            Assert.assertTrue(rateChartExist); 
        }
            
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
        
        public String getTransactionDate(){
            return this.transactionDate;
        }
        
        public String getTransactonAmount(){
            return this.transactonAmount;
        }
    }
}
