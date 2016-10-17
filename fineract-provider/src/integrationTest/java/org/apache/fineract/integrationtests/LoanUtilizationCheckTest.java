package org.apache.fineract.integrationtests;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.portfolio.loan.utilization.LoanUtilizationCheckHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanUtilizationCheckTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanApplicationUndoLastTrancheTest loanApplicationUndoLastTrancheTest;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanApplicationUndoLastTrancheTest = new LoanApplicationUndoLastTrancheTest();
    }

    @Test
    public void testLoanUtilizationCheck() {

        final Integer loanId = this.loanApplicationUndoLastTrancheTest.testProcessCallLoanApplicationUndoLastTranche();

        Integer loanUtilizationCheckId = LoanUtilizationCheckHelper.createLoanUtilizationCheck(this.requestSpec, this.responseSpec, loanId);
        Assert.assertNotNull(loanUtilizationCheckId);

        loanUtilizationCheckId = LoanUtilizationCheckHelper.updateLoanUtilizationCheck(this.requestSpec, this.responseSpec, loanId,
                loanUtilizationCheckId);
        Assert.assertNotNull(loanUtilizationCheckId);

    }
}
