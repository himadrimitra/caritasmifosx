package org.apache.fineract.integrationtests;

import org.apache.fineract.integrationtests.common.AddressHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AddressTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private AddressHelper helper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

    }

    @Test
    public void testAddress() {
        this.helper = new AddressHelper(this.requestSpec, this.responseSpec);
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        final Integer addressId = this.helper.createAddress("clients", clientId.toString());
        Assert.assertNotNull(addressId);
    }
}
