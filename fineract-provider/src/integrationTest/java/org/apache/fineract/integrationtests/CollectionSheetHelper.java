package org.apache.fineract.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CollectionSheetHelper {

	private final RequestSpecification requestSpec;
	private final ResponseSpecification responseSpec;
	private static final String GET_CENETER_COLLECTION_SHEET_URL = "/fineract-provider/api/v1/centers/";
	private static final String GET_GROUPS_COLLECTION_SHEET_URL = "/fineract-provider/api/v1/groups/";
	private static final String GENERATE_COLLECTION_SHEET_COMMAND = "?command=generateCollectionSheet";
	private static final String SAVE_COLLECTIONSHEET_COMMAND = "?command=saveCollectionSheet";

	public CollectionSheetHelper(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
		this.requestSpec = requestSpec;
		this.responseSpec = responseSpec;
	}

	public ArrayList<HashMap<String, Object>> getCenterCollectionSheet(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, Integer calanderId, String generationDate, Integer centerId) {
		return Utils.performServerPost(requestSpec, responseSpec,
				GET_CENETER_COLLECTION_SHEET_URL + centerId + GENERATE_COLLECTION_SHEET_COMMAND + "&"
						+ Utils.TENANT_IDENTIFIER,
				getCollecitonSheetRequestParametersAsJSON(calanderId, generationDate), "groups");
	}

	public HashMap<String,Object> saveCollectionSheet(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
			String json, Integer centerId) {
		return Utils.performServerPost(requestSpec, responseSpec, GET_CENETER_COLLECTION_SHEET_URL + centerId
				+ SAVE_COLLECTIONSHEET_COMMAND + "&" + Utils.TENANT_IDENTIFIER, json, "changes");
	}
	
	public ArrayList<HashMap<String,Object>> saveCollectionSheet(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
			String json, Integer centerId,String jsonAttributeToGetBack) {
		return Utils.performServerPost(requestSpec, responseSpec, GET_CENETER_COLLECTION_SHEET_URL + centerId
				+ SAVE_COLLECTIONSHEET_COMMAND + "&" + Utils.TENANT_IDENTIFIER, json, jsonAttributeToGetBack);
	}

	private String getCollecitonSheetRequestParametersAsJSON(final Integer calanderId, final String generationDate) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("locale", "en");
		map.put("calendarId", calanderId);
		map.put("transactionDate", generationDate);
		System.out.println("map : " + map);
		return new Gson().toJson(map);
	}
}
