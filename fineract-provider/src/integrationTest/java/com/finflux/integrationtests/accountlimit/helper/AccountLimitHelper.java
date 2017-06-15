package com.finflux.integrationtests.accountlimit.helper;

import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.Utils;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountLimitHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private final String API_URL = "/fineract-provider/api/v1/clients/{clientId}/accountlimits";
    private final String TENANT_IDENTIFIER = "?" + Utils.TENANT_IDENTIFIER;

    public AccountLimitHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer create(final Integer clientId, final String requestBodyAsJson) {
        final String API_URL = this.API_URL.replace("{clientId}", clientId.toString()) + this.TENANT_IDENTIFIER;
        return Utils.performServerPost(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "resourceId");
    }

    public Object update(final Integer clientId, final Integer accountLimitId, final String requestBodyAsJson) {
        final String API_URL = this.API_URL.replace("{clientId}", clientId.toString()) + "/" + accountLimitId + this.TENANT_IDENTIFIER;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
    }

    public Object updateResponceWithErrors(final Integer clientId, final Integer accountLimitId, final String requestBodyAsJson) {
        final String API_URL = this.API_URL.replace("{clientId}", clientId.toString()) + "/" + accountLimitId + this.TENANT_IDENTIFIER;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, CommonConstants.RESPONSE_ERROR);
    }
}