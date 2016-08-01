package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.accounting.AccountHelper;
import org.apache.fineract.integrationtests.common.accounting.FinancialActivityAccountHelper;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AAOpeningBalanceMigration {
    
    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;
    private ResponseSpecification responseForbiddenError;
    private ResponseSpecification responseSpecForStatusCode403;
    private AccountHelper accountHelper;
    private FinancialActivityAccountHelper financialActivityAccountHelper;
    private OfficeHelper officeHelper;
    
    @Before
    public void setup() {
            Utils.initializeRESTAssured();
            this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
            this.requestSpec.header("Authorization",
                            "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
            this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
            this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
            this.responseForbiddenError = new ResponseSpecBuilder().expectStatusCode(403).build();
            this.responseSpecForStatusCode403 = new ResponseSpecBuilder().expectStatusCode(403).build();
            this.financialActivityAccountHelper = new FinancialActivityAccountHelper(this.requestSpec);
            this.officeHelper = new OfficeHelper(this.requestSpec, this.responseSpec);
    }
    
    @Test
    public void aaOpeningBalanceMigrationTest() {
        final Account assetAccount = this.accountHelper.createAssetAccount();
        Assert.assertNotNull(assetAccount);
        final Account incomeAccount = this.accountHelper.createIncomeAccount();
        Assert.assertNotNull(incomeAccount);
        final Account openingBalanceAccount = this.accountHelper.createEquityAccount();
        Assert.assertNotNull(openingBalanceAccount);
        final Integer officeId = officeHelper.createOffice("01 January 2015");
        Assert.assertNotNull(officeId);
        String amount = "1000";
        Integer financialActivityAccountId = (Integer) financialActivityAccountHelper.createFinancialActivityAccount(
                FINANCIAL_ACTIVITY.OPENING_BALANCES_TRANSFER_CONTRA.getValue(), openingBalanceAccount.getAccountID(), responseSpec,
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(financialActivityAccountId);
        final String transactionId = Utils.performServerPost(requestSpec, responseSpec, createURLForPostingOpeningBalances(),
                new Gson().toJson(generateMapForOpeningBalance(officeId, assetAccount.getAccountID(), incomeAccount.getAccountID(),amount)),
                "transactionId");
        ArrayList<HashMap> journalEntries = getJournalEntriesByTransactionId(transactionId);
        assertFalse("gl Enties Not Created", journalEntries.contains(assetAccount.getAccountID()));
        assertFalse("gl Enties Not Created", journalEntries.contains(incomeAccount.getAccountID()));
        assertFalse("gl Enties Not Created", journalEntries.contains(openingBalanceAccount.getAccountID()));
        assertFalse("gl Enties Not Created", journalEntries.contains(amount));
        final Integer newofficeId = officeHelper.createOffice("01 January 2015");
        Assert.assertNotNull(newofficeId);
        amount = "5000";
        final String SecondtransactionId = Utils.performServerPost(requestSpec, responseSpec, createURLForPostingOpeningBalances(),
                new Gson().toJson(generateMapForOpeningBalance(officeId, assetAccount.getAccountID(), incomeAccount.getAccountID(),amount)),
                "transactionId");
        ArrayList<HashMap> secondJournalEntries = getJournalEntriesByTransactionId(SecondtransactionId);
        assertFalse("gl Enties Not Created", journalEntries.contains(assetAccount.getAccountID()));
        assertFalse("gl Enties Not Created", journalEntries.contains(incomeAccount.getAccountID()));
        assertFalse("gl Enties Not Created", journalEntries.contains(openingBalanceAccount.getAccountID()));
        assertFalse("gl Enties Not Created", journalEntries.contains(amount));
    }
    
    private String createURLForPostingOpeningBalances(){
        return new String("/fineract-provider/api/v1/journalentries?"+ Utils.TENANT_IDENTIFIER+"&command=defineOpeningBalance");
    }
    
    private String createURLForGettingAccountEntriesByTransactionId(final String transactionId) {
        return new String("/fineract-provider/api/v1/journalentries?transactionId=" + transactionId + "&tenantIdentifier=default"
                + "&orderBy=id&sortOrder=desc&locale=en&dateFormat=dd MMMM yyyy");
    }
    
    private ArrayList<HashMap> getJournalEntriesByTransactionId(final String transactionId) {
        final String url = createURLForGettingAccountEntriesByTransactionId(transactionId);
        final ArrayList<HashMap> response = Utils.performServerGet(this.requestSpec, this.responseSpec, url, "pageItems");
        return response;
    }
    
    private HashMap<String,Object> generateMapForOpeningBalance(Integer officeId,Integer firstAccountId, Integer secondAccountId,String amount){
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("currencyCode", "USD");
        map.put("officeId", officeId);
        ArrayList<HashMap<String,Object>> credits = new ArrayList<HashMap<String,Object>>();
        ArrayList<HashMap<String,Object>> debits = new ArrayList<HashMap<String,Object>>();
        final ArrayList<HashMap> creditAccountMapList = new ArrayList<HashMap>();
        final ArrayList<HashMap> debitAccountMapList = new ArrayList<HashMap>();
        final HashMap<String, Object> creditAccountMap = new HashMap<String, Object>();
        creditAccountMap.put("glAccountId", firstAccountId);
        creditAccountMap.put("amount", amount);
        creditAccountMapList.add(creditAccountMap);
        final HashMap<String, Object> debitAccountMap = new HashMap<String, Object>();
        debitAccountMap.put("glAccountId", secondAccountId);
        debitAccountMap.put("amount", amount);
        debitAccountMapList.add(debitAccountMap);
        map.put("credits", creditAccountMapList);
        map.put("debits", debitAccountMapList);
        map.put("locale", "en_IN");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", "01 January 2015");
        return map;
    }
}
