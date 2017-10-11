package org.apache.fineract.integrationtests.common;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CodeValueHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String BASE_CODE_VALUE_URL = "https://localhost:8443/fineract-provider/api/v1/codes";

    public CodeValueHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Object getAllCodevalues() {
        System.out.println(BASE_CODE_VALUE_URL + "?" + Utils.TENANT_IDENTIFIER);
        return Utils.performServerGet(this.requestSpec, this.responseSpec, BASE_CODE_VALUE_URL + "?" + Utils.TENANT_IDENTIFIER,
                "");
    }

}
