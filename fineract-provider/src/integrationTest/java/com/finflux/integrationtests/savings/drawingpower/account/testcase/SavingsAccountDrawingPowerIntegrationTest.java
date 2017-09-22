package com.finflux.integrationtests.savings.drawingpower.account.testcase;

import java.util.HashMap;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsStatusChecker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.finflux.integrationtests.savings.drawingpower.product.helper.SavingsProductDrawingPowerHelper;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class SavingsAccountDrawingPowerIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private SavingsProductDrawingPowerHelper savingsProductDrawingPowerHelper;
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
        this.savingsProductDrawingPowerHelper = new SavingsProductDrawingPowerHelper(this.requestSpec, this.responseSpec);
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        initializeRequiredEntities();
    }

    private void initializeRequiredEntities() {
        // Create a new Office
        final String officeCreationDate = "01 July 2007";
        OfficeHelper officeHelper = new OfficeHelper(requestSpec, responseSpec);
        this.officeId = officeHelper.createOffice(officeCreationDate);
        // Create a new client under newly created office
        final String clientActivationDate = "04 March 2011";
        this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec, clientActivationDate, officeId.toString());
        // Create new savings product with allow Dp Limit enabled
        this.savingsProductId = this.savingsProductDrawingPowerHelper.createSavingsProduct();
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testCreateSavingsAccountWithAllowedDPLimit() {

        final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
        final String SAVINGS_SUBMIT_DATE = "02 January 2017";
        // Create savings account
        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplicationWithAllowDpLimit(this.clientId,
                this.savingsProductId, ACCOUNT_TYPE_INDIVIDUAL, SAVINGS_SUBMIT_DATE);
        Assert.assertNotNull(savingsId);
        // Approve the Savings Account
        HashMap savingsStatusHashMap = this.savingsAccountHelper.approveSavingsOnDate(savingsId, SAVINGS_SUBMIT_DATE);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);
        // Activate Savings Account
        savingsStatusHashMap = this.savingsAccountHelper.activateSavingsOnDate(savingsId, SAVINGS_SUBMIT_DATE);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);
    }
}