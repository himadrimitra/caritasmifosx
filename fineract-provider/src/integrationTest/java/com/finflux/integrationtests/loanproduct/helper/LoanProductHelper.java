package com.finflux.integrationtests.loanproduct.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.integrationtests.common.Utils;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanProductHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String LOAN_PRODUCT_API_URL = "/fineract-provider/api/v1/loanproducts";
    private static final String TENANT_IDENTIFIER = "?" + Utils.TENANT_IDENTIFIER;

    public LoanProductHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer getLoanProductId(final String requestBodyAsJson) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, LOAN_PRODUCT_API_URL + TENANT_IDENTIFIER, requestBodyAsJson,
                "resourceId");
    }

    public HashMap createLoanProduct(final String requestBodyAsJson) {
        return (HashMap) performLoanProduct(requestBodyAsJson);
    }

    private Object performLoanProduct(final String requestBodyAsJson) {
        return Utils
                .performServerPost(this.requestSpec, this.responseSpec, LOAN_PRODUCT_API_URL + TENANT_IDENTIFIER, requestBodyAsJson, "");
    }

    @SuppressWarnings("rawtypes")
    public ArrayList getAllLoanProducts() {
        final String API_URL = LOAN_PRODUCT_API_URL + TENANT_IDENTIFIER;
        return Utils.performServerGet(this.requestSpec, this.responseSpec, API_URL, "");
    }

    @SuppressWarnings("rawtypes")
    public HashMap getLoanProduct(final String loanProductId) {
        final String API_URL = LOAN_PRODUCT_API_URL + "/" + loanProductId + TENANT_IDENTIFIER;
        return Utils.performServerGet(this.requestSpec, this.responseSpec, API_URL, "");
    }
}
