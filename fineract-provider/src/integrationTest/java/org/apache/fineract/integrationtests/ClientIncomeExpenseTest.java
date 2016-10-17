package org.apache.fineract.integrationtests;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.portfolio.client.cashflow.ClientIncomeExpenseHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class ClientIncomeExpenseTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private CashFlowCategoryTest cashFlowCategoryTest = new CashFlowCategoryTest();

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void testClientIncomeExpense() {

        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        final Integer cashFlowCategoryId = this.cashFlowCategoryTest.createCashFlowCategory(this.requestSpec, this.responseSpec);

        final Integer incomeExpenseId = this.cashFlowCategoryTest.createIncomeExpense(cashFlowCategoryId, this.requestSpec,
                this.responseSpec);

        Integer clientIncomeExpenseId = ClientIncomeExpenseHelper.createClientIncomeExpense(this.requestSpec, this.responseSpec, clientId,
                incomeExpenseId);
        Assert.assertNotNull(clientIncomeExpenseId);

        clientIncomeExpenseId = ClientIncomeExpenseHelper.updateClientIncomeExpense(this.requestSpec, this.responseSpec, clientId,
                clientIncomeExpenseId, incomeExpenseId);
        Assert.assertNotNull(clientIncomeExpenseId);

    }
}
