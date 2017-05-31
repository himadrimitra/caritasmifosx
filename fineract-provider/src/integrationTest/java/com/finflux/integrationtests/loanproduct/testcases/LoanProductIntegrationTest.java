package com.finflux.integrationtests.loanproduct.testcases;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanProductIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;

    private Integer loanProductId;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    }

    @Test
    public void createLoanProductApplicableForClientProfileTypeLegalForm() {

        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");

        this.loanProductId = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().withInterestTypeAsDecliningBalance()
                .withTranches(true).withInterestCalculationPeriodTypeAsRepaymentPeriod(true).withApplicableForClientProfileTypeLegalForm()
                .build(null));

        Assert.assertNotNull(this.loanProductId);
    }

}
