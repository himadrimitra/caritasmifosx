package org.apache.fineract.integrationtests.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class CgtHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String CREATE_CGT_URL = "/fineract-provider/api/v1/cgt?" + Utils.TENANT_IDENTIFIER;
    private static final String CGT_URL = "/fineract-provider/api/v1/cgt";
    /*private static final String CREATE_CGT_DAY_URL = CGT_URL + "/cgtDay" + Utils.TENANT_IDENTIFIER;*/
    public static final String DATE_FORMAT = "dd MMMM yyyy";
    
    public CgtHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
    
    public static String createCgt(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String entityId, String staffId, List<String> clientIds) {
        System.out.println("---------------------------------CREATING A CGT---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_CGT_URL, getTestCgtAsJSON(entityId, staffId, clientIds),
                "resourceIdentifier");
    }
    
    public static Object getCgt(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String cgtId,
            final String jsonReturn) {
        final String GET_CGT_URL = "/fineract-provider/api/v1/cgt/" + cgtId + "?" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------GET A CGT---------------------------------------------");
        return Utils.performServerGet(requestSpec, responseSpec, GET_CGT_URL, jsonReturn);

    }
    
    public static HashMap<String, List<Integer>> createCgtDay(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String cgtId, String staffId) {
        final String GET_CGT_DAY_URL = "/fineract-provider/api/v1/cgt/" + cgtId + "/cgtDay?" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------CREATING A CGT Day---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, GET_CGT_DAY_URL, getTestCgtDayAsJSON(staffId),
                "changes");
    }
    
    public static HashMap<String, String> completeCgtDay(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String cgtId, final Integer cgtDayId, Map<String, List<Map<String, Object>>> clientAttendances) {
        final String GET_CGT_DAY_URL = "/fineract-provider/api/v1/cgt/" + cgtId + "/cgtDay/"+ cgtDayId +"?action=complete&" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------Complete CGT Day---------------------------------------------");
        return Utils.performServerPut(requestSpec, responseSpec, GET_CGT_DAY_URL, getTestCompleteCgtDayAsJSON(clientAttendances),
                "changes");
    }

    private static String getTestCgtDayAsJSON(final String staffId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("cgtDayCreationType", "startCgtDay");
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
    
    private static String getTestCompleteCgtDayAsJSON(Map<String, List<Map<String, Object>>> clientAttendances) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("dateFormat", DATE_FORMAT);
        map.put("locale", "en");
        map.put("completedDate", "04 March 2016");
        map.putAll(clientAttendances);
        map.put("note", "test cgt");
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String getTestCgtAsJSON(String entityId, String staffId, List<String> clientIds) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("loanOfficerId", staffId);
        map.put("entityId", entityId);
        map.put("entityType", "2");
        map.put("dateFormat", DATE_FORMAT);
        map.put("locale", "en");
        map.put("location", "Bangalore");
        map.put("clientIds", clientIds);
        map.put("expectedStartDate", "04 March 2016");
        map.put("expectedEndDate", "07 March 2016");
        map.put("note", "test cgt");
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
    
    private static String getTestCgtCompleteAsJSON() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("dateFormat", DATE_FORMAT);
        map.put("locale", "en");
        map.put("completedDate", "04 March 2016");
        map.put("note", "test cgt");
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    public static HashMap<String, String> completeCgt(RequestSpecification requestSpec, ResponseSpecification responseSpec, String cgtId) {
        final String GET_CGT_DAY_URL = "/fineract-provider/api/v1/cgt/" + cgtId + "?action=complete&" + Utils.TENANT_IDENTIFIER;
        System.out.println("---------------------------------COMPLETE  CGT ---------------------------------------------");
        return Utils.performServerPut(requestSpec, responseSpec, GET_CGT_DAY_URL, getTestCgtCompleteAsJSON(), "changes");
    }
}
