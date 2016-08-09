package org.apache.fineract.integrationtests.common;

import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AddressHelper {

    private final RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;

    private static final String API_URL = "/fineract-provider/api/v1";
    public static final String DATE_FORMAT = "dd MMMM yyyy";

    public AddressHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer createAddress(final String entityType, final String entityId) {
        System.out.println("--------------------------------- Create Address " + entityType + " -------------------------------");
        System.out.println(createAddressOperationURL(entityType, entityId));
        final Integer addressId = Utils.performServerPost(this.requestSpec, this.responseSpec,
                createAddressOperationURL(entityType, entityId), getTestCreateAddressAsJSON(entityId), "resourceId");
        System.out.println("Address Id : " + addressId);
        return addressId;
    }

    private String createAddressOperationURL(final String entityType, final String entityId) {
        return API_URL + "/" + entityType + "/" + entityId + "/addresses?" + Utils.TENANT_IDENTIFIER;
    }

    public String getTestCreateAddressAsJSON(final String entityId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("entityTypeEnum", "1");
        map.put("entityId", entityId);
        map.put("addressTypes", new Integer[] { 14 });
        map.put("houseNo", "123");
        map.put("addressLineOne", "addressLineOne");
        map.put("districtId", "1");
        map.put("stateId", "1");
        map.put("countryId", "101");
        map.put("postalCode", "560079");
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
    
	public Object updateAddress(final String entityType, final String entityId, final String addressId) {
		System.out.println(
				"--------------------------------- update Address " + entityType + addressId +" -------------------------------");
		System.out.println(createAddressUpdateURL(entityType, entityId, addressId));
		final Object changes = Utils.performServerPut(this.requestSpec, this.responseSpec,
				createAddressUpdateURL(entityType, entityId, addressId),
				getTestUpdateAddressAsJSON(entityId, addressId), "");
		System.out.println("state update changes : " + changes);
		return changes;
	}
	
	public Object updateStateAddress(final String entityType, final String entityId, final String addressId,final ResponseSpecification responseSpec) {
		System.out.println(
				"--------------------------------- update Address " + entityType + addressId + " -------------------------------");
		System.out.println(createAddressUpdateURL(entityType, entityId, addressId));
		final Object updates = Utils.performServerPut(this.requestSpec, responseSpec,
				createAddressUpdateURL(entityType, entityId, addressId),
				getTestUpdateStateAsJson(entityId, addressId), CommonConstants.RESPONSE_ERROR);
		System.out.println("state update changes : " + updates);
		return updates;
	}

	private String createAddressUpdateURL(final String entityType, final String entityId, final String addressId) {
		return API_URL + "/" + entityType + "/" + entityId + "/addresses" + "/" + addressId + "?"
				+ Utils.TENANT_IDENTIFIER;
	}

	public String getTestUpdateAddressAsJSON(final String entityId, final String addressId) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put("entityTypeEnum", "1");
		map.put("entityId", entityId);
		map.put("addressId", addressId);
		map.put("addressTypes", new Integer[] {15});
		map.put("houseNo", "145");
		map.put("addressLineOne", "addressLineTwo");
		map.put("districtId", "32");
		map.put("stateId", "17");
		map.put("countryId", "101");
		map.put("postalCode", "560080");
		map.put("locale", "en");
		map.put("dateFormat", DATE_FORMAT);
		System.out.println("address changes map : " + map);
		return new Gson().toJson(map);
	}
	
	public String getTestUpdateStateAsJson(final String entityId, final String addressId) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put("entityTypeEnum", "1");
		map.put("entityId", entityId);
		map.put("addressId", addressId);
		map.put("addressTypes", new Integer[] { 15 });
		map.put("houseNo", "145");
		map.put("addressLineOne", "addressLineTwo");
		map.put("districtId", "32");
		map.put("stateId", "52");
		map.put("countryId", "101");
		map.put("postalCode", "560080");
		map.put("locale", "en");
		map.put("dateFormat", DATE_FORMAT);
		System.out.println("address changes map : " + map);
		return new Gson().toJson(map);
	}
}
