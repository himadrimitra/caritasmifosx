package org.apache.fineract.integrationtests.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    Object districtId = null;
    Object countryId = null;
    Object stateId = null;
    Object talukaId = null;

    public Integer insertTaluka(final String entityType, final String entityId) {
        System.out.println("--------------------------------- Create Tluka " + entityType + " -------------------------------");
        final Integer resourceId = Utils.performServerPost(this.requestSpec, this.responseSpec, getTalukasURL(entityId), createTalukaJson(),
                "resourceId");
        System.out.println("taluka resource Id is.........>" + resourceId);
        return resourceId;
    }

    public Object addressTemplate(final String entityType) {
        System.out.println("address template url........." + getAddressTemplateURL(entityType));

        final Object template = Utils.performServerGet(this.requestSpec, this.responseSpec, getAddressTemplateURL(entityType), "");
        System.out.println("address template url........." + template);
        return template;

    }

    public Integer createAddress(final String entityType, final String entityId) {
        System.out.println("--------------------------------- Create Address " + entityType + " -------------------------------");
        System.out.println(createAddressOperationURL(entityType, entityId));
        final Integer addressId = Utils.performServerPost(this.requestSpec, this.responseSpec,
                createAddressOperationURL(entityType, entityId), getTestCreateAddressAsJSON(entityId), "resourceId");
        System.out.println("Address Id : " + addressId);
        return addressId;
    }

    public Object createDuplicateAddressType(final String entityType,final String entityId,final ResponseSpecification responseSpec){
    	System.out.println("--------------------------------- Create Address " + entityType + " -------------------------------");
        System.out.println(createAddressOperationURL(entityType, entityId));
        final Object addressError = Utils.performServerPost(this.requestSpec,responseSpec,
                createAddressOperationURL(entityType, entityId), getTestDuplicateAddressAsJSON(entityId), CommonConstants.RESPONSE_ERROR);
        System.out.println("AddressType is already present : " + addressError);
        return addressError;
    }
    
    private String getAddressTemplateURL(final String entityType) {
        return API_URL + "/" + entityType + "/addresses" + "/template?" + Utils.TENANT_IDENTIFIER;
    }

    private String createAddressOperationURL(final String entityType, final String entityId) {
        return API_URL + "/" + entityType + "/" + entityId + "/addresses?" + Utils.TENANT_IDENTIFIER;
    }

    private String getTalukasURL(final String districtId) {
        return API_URL + "/districts" + "/" + districtId + "/talukas?" + Utils.TENANT_IDENTIFIER;
    }

    public String getTestCreateAddressAsJSON(final String entityId) {
        final HashMap<String, Object> map = new HashMap<>();
        getAddressTemplateData();
        map.put("entityTypeEnum", "1");
        map.put("entityId", entityId);
        map.put("addressTypes", new Integer[] { 14 });
        map.put("houseNo", "123");
        map.put("addressLineOne", "addressLineOne");
        map.put("talukaId", talukaId.toString());
        map.put("districtId", districtId.toString());
        map.put("stateId", stateId.toString());
        map.put("countryId", countryId.toString());
        map.put("postalCode", "560079");
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
    public String getTestDuplicateAddressAsJSON(final String entityId) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put("entityTypeEnum", "1");
		map.put("entityId", entityId);
		map.put("addressTypes", new Integer[] { 14 });
		map.put("houseNo", "143");
		map.put("addressLineOne", "addressLineOne");
		map.put("talukaId", talukaId.toString());
		map.put("districtId", districtId.toString());
		map.put("stateId", stateId.toString());
		map.put("countryId", countryId.toString());
		map.put("postalCode", "560080");
		map.put("locale", "en");
		map.put("dateFormat", DATE_FORMAT);
		System.out.println("map : " + map);
		return new Gson().toJson(map);
	}

    public Object updateAddress(final String entityType, final String entityId, final String addressId) {
        System.out.println("--------------------------------- update Address " + entityType + " -------------------------------");
        System.out.println(createAddressUpdateURL(entityType, entityId, addressId));
        final Object changes = Utils.performServerPut(this.requestSpec, this.responseSpec,
                createAddressUpdateURL(entityType, entityId, addressId), getTestUpdateAddressAsJSON(entityId, addressId), "");
        System.out.println("address update changes : " + changes);
        return changes;
    }

    public Object updateStateAddress(final String entityType, final String entityId, final String addressId,
            final ResponseSpecification responseSpec) {
        System.out
                .println("--------------------------------- update Address " + entityType + addressId + " -------------------------------");
        System.out.println(createAddressUpdateURL(entityType, entityId, addressId));
        final Object updates = Utils.performServerPut(this.requestSpec, responseSpec,
                createAddressUpdateURL(entityType, entityId, addressId), getTestUpdateStateAsJson(entityId, addressId),
                CommonConstants.RESPONSE_ERROR);
        System.out.println("state update changes : " + updates);
        return updates;
    }

    private String createAddressUpdateURL(final String entityType, final String entityId, final String addressId) {
        return API_URL + "/" + entityType + "/" + entityId + "/addresses" + "/" + addressId + "?" + Utils.TENANT_IDENTIFIER;
    }

    public String getTestUpdateAddressAsJSON(final String entityId, final String addressId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("entityTypeEnum", "1");
        map.put("entityId", entityId);
        map.put("addressId", addressId);
        map.put("addressTypes", new Integer[] { 15 });
        map.put("houseNo", "148");
        map.put("addressLineOne", "addressLineTwo");
        map.put("talukaId", talukaId.toString());
        map.put("districtId", districtId.toString());
        map.put("stateId", stateId.toString());
        map.put("countryId", countryId.toString());
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

    public String createTalukaJson() {
        final HashMap<String, Object> map = new HashMap<>();

        map.put("isoTalukaCode", "BM");
        map.put("talukaName", "BengaluruWest");

        return new Gson().toJson(map);
    }

    public void getAddressTemplateData() {
        HashMap template = (HashMap) addressTemplate("clients");
        List<HashMap> countries = (ArrayList<HashMap>) template.get("countryDatas");
        List<HashMap> states = null;
        List<HashMap> districts = null;
        List<HashMap> talukas = null;
        System.out.println("country datas is...............>" + countries + countries.size());

        for (int i = 0; i < countries.size(); i++) {
            if (countries.get(i).get("countryName").toString().equalsIgnoreCase("india")) {
                countryId = countries.get(i).get("countryId");
                states = (ArrayList<HashMap>) (countries.get(i).get("statesDatas"));
                for (int j = 0; j < states.size(); j++) {
                    if (states.get(j).get("stateName").toString().equalsIgnoreCase("karnataka")) {
                        stateId = states.get(j).get("stateId");
                        districts = (ArrayList<HashMap>) (states.get(j).get("districtDatas"));
                        for (int k = 0; k < districts.size(); k++) {
                            if (districts.get(k).get("districtName").toString().equalsIgnoreCase("Bangalore Urban")) {
                                districtId = districts.get(k).get("districtId");
                                talukaId = insertTaluka("districts", districtId.toString());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
