package org.apache.fineract.integrationtests;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.portfolio.loan.purpose.LoanPurposeGroupHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanPurposeGroupTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

    }

    @Test
    public void testLoanPuposeGroup() {
        Integer loanPurposeGroupId = LoanPurposeGroupHelper.createLoanPurposeGroup(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(loanPurposeGroupId);

        loanPurposeGroupId = LoanPurposeGroupHelper.updateLoanPurposeGroup(this.requestSpec, this.responseSpec, loanPurposeGroupId);
        Assert.assertNotNull(loanPurposeGroupId);

        Integer loanPurposeId = LoanPurposeGroupHelper.createLoanPurpose(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(loanPurposeId);

        loanPurposeId = LoanPurposeGroupHelper.updateLoanPurpose(this.requestSpec, this.responseSpec, loanPurposeId);
        Assert.assertNotNull(loanPurposeId);
    }
}