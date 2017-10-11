package com.finflux.integrationtests.accountlimit.testcases;

import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Before;
import org.junit.Test;

import com.finflux.integrationtests.accountlimit.service.AccountLimitTestCaseService;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountLimitIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private AccountLimitTestCaseService accountLimitTestCaseService;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.accountLimitTestCaseService = new AccountLimitTestCaseService(this.requestSpec, this.responseSpec);
    }

    @Test
    public void runAccountLimitTestCases() {
        this.accountLimitTestCaseService.runAccountLimitTestCases();
    }
}
