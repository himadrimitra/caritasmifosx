package com.finflux.integrationtests.loan.testcase;

import java.net.URISyntaxException;

import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Before;
import org.junit.Test;

import com.finflux.integrationtests.loan.service.GlimLoanTestCaseService;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class GlimLoanIntegrationTest {

    private GlimLoanTestCaseService glimLoanTestCaseService;

    @Before
    public void setup() throws URISyntaxException {
        Utils.initializeRESTAssured();
        final RequestSpecification requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.glimLoanTestCaseService = new GlimLoanTestCaseService(requestSpec, responseSpec);
    }

    @Test
    public void runGlimLoanIntegrationTest() {
        this.glimLoanTestCaseService.runGlimLoanIntegrationTest();
    }
}
