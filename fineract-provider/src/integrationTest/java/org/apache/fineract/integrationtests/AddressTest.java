package org.apache.fineract.integrationtests;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.AddressHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
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
    private ResponseSpecification responseCode;
    private AddressHelper helper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.responseCode = new ResponseSpecBuilder().expectStatusCode(403).build();

    }

    @Test
    public void testAddress() {
        this.helper = new AddressHelper(this.requestSpec, this.responseSpec);
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);
         
        final Integer addressId = this.helper.createAddress("clients", clientId.toString());
        Assert.assertNotNull(addressId);
        
        List<HashMap> errorMsg = (List<HashMap>)this.helper.createDuplicateAddressType("clients", clientId.toString(),this.responseCode); 
        Assert.assertEquals("error.msg.Residential Address.address.with.addresstype.already.exists", errorMsg.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        
        HashMap actualChanges = (HashMap)this.helper.updateAddress("clients",clientId.toString(),addressId.toString());
        Assert.assertNotNull(actualChanges);
        Assert.assertEquals("560080", ((HashMap) (actualChanges.get("changes"))).get("postalCode"));
        Assert.assertEquals("148", ((HashMap) (actualChanges.get("changes"))).get("houseNo"));
        Assert.assertEquals("addressLineTwo", ((HashMap) (actualChanges.get("changes"))).get("addressLineOne"));

        List<HashMap> error = (List<HashMap>) this.helper.updateStateAddress("clients", clientId.toString(), addressId.toString(),
                this.responseCode);
        Assert.assertEquals("error.msg.address.state.does.not.belongs.to.country", error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }
}
    
