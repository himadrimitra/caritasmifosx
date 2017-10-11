package com.finflux.integrationtests.accountlimit.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.junit.Assert;

import com.finflux.integrationtests.accountlimit.builder.AccountLimitTestBuilder;
import com.finflux.integrationtests.accountlimit.helper.AccountLimitHelper;
import com.finflux.integrationtests.client.builder.ClientTestBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountLimitTestCaseService {

    private RequestSpecification requestSpec;
    private AccountLimitHelper helper;
    private ClientHelper clientHelper;

    private Integer officeId;
    private final String activationDate = "04 March 2011";
    private Integer clientId;

    public AccountLimitTestCaseService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.helper = new AccountLimitHelper(requestSpec, responseSpec);
        this.clientHelper = new ClientHelper(requestSpec, responseSpec);
        initializeRequiredEntities();
    }

    private void initializeRequiredEntities() {
        this.officeId = 1;
        this.clientId = this.clientHelper.getClientId(new ClientTestBuilder().withOffice(this.officeId).withActivateOn(this.activationDate)
                .build());
        Assert.assertNotNull(this.clientId);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void runAccountLimitTestCases() {
        final Integer accountLimitId = this.helper.create(this.clientId, new AccountLimitTestBuilder().build());
        Assert.assertNotNull(accountLimitId);

        final HashMap actualChanges = (HashMap) this.helper.update(this.clientId, accountLimitId, new AccountLimitTestBuilder()
                .withLoanAccountLimit().build());
        Assert.assertNotNull(actualChanges);
        Assert.assertEquals("150000", ((HashMap) (actualChanges.get("changes"))).get("limitOnTotalDisbursementAmount").toString());
        Assert.assertEquals("150000", ((HashMap) (actualChanges.get("changes"))).get("limitOnTotalLoanOutstandingAmount").toString());

        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(400).build();
        final AccountLimitHelper helper = new AccountLimitHelper(this.requestSpec, responseSpec);
        final ArrayList<HashMap> errorData = (ArrayList<HashMap>) helper.updateResponceWithErrors(this.clientId, accountLimitId,
                new AccountLimitTestBuilder().withLoanAccountLimitInvalidData().build());
        Assert.assertNotNull(errorData);
        assertEquals("validation.msg.invalid.decimal.format", errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }
}