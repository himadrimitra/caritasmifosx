package org.apache.fineract.integrationtests;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.portfolio.cashflow.CashFlowCategoryHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CashFlowCategoryTest {

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
    public void testCashFlowCategory() {
        processCashFlowCategory();
    }

    private void processCashFlowCategory() {
        final Integer cashFlowCategoryId = createCashFlowCategory(this.requestSpec, this.responseSpec);
        createIncomeExpense(cashFlowCategoryId, this.requestSpec, this.responseSpec);
    }

    public Integer createCashFlowCategory(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        Integer cashFlowCategoryId = CashFlowCategoryHelper.createCashFlowCategory(requestSpec, responseSpec);
        Assert.assertNotNull(cashFlowCategoryId);

        cashFlowCategoryId = CashFlowCategoryHelper.updateCashFlowCategory(requestSpec, responseSpec, cashFlowCategoryId);
        Assert.assertNotNull(cashFlowCategoryId);

        return cashFlowCategoryId;
    }

    public Integer createIncomeExpense(final Integer cashFlowCategoryId, final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec) {
        Integer incomeExpenseId = CashFlowCategoryHelper.createIncomeExpense(requestSpec, responseSpec, cashFlowCategoryId);
        Assert.assertNotNull(incomeExpenseId);

        incomeExpenseId = CashFlowCategoryHelper.updateIncomeExpense(requestSpec, responseSpec, incomeExpenseId);
        Assert.assertNotNull(incomeExpenseId);

        return incomeExpenseId;
    }
}
