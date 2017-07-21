package com.finflux.integrationtests.pdc.testcases;

import java.text.ParseException;

import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Before;
import org.junit.Test;

import com.finflux.integrationtests.pdc.service.PostDatedChequeDetailTestCaseService;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class PostDatedChequeDetailIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private PostDatedChequeDetailTestCaseService service;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.service = new PostDatedChequeDetailTestCaseService(this.requestSpec, this.responseSpec);
    }

    @Test
    public void runPostDatedChequeDetailTestCases() throws ParseException {
        this.service.runPostDatedChequeDetailTestCases();
    }
}
