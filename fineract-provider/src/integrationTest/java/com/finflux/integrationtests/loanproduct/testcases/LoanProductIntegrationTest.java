package com.finflux.integrationtests.loanproduct.testcases;

import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Before;
import org.junit.Test;

import com.finflux.integrationtests.loanproduct.service.LoanProductTestCaseService;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanProductIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanProductTestCaseService loanProductTestCaseService;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanProductTestCaseService = new LoanProductTestCaseService(this.requestSpec, this.responseSpec);
    }

    @Test
    public void runLoanProductTestCases() {
        this.loanProductTestCaseService.runLoanProductTestCases();
    }
}