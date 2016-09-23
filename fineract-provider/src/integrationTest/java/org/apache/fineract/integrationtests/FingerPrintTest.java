package org.apache.fineract.integrationtests;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.FingerPrintHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class FingerPrintTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private FingerPrintHelper helper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

    }
    @Test
    public void testFingerPrint() {
        this.helper = new FingerPrintHelper(this.requestSpec, this.responseSpec);
        
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);
        
        final Integer resourceId = this.helper.createFingerPrint("clients", clientId.toString());
        Assert.assertNotNull(resourceId);
    }

}
