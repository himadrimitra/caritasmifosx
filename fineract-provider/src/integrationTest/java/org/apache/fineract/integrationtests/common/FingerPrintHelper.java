package org.apache.fineract.integrationtests.common;

import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class FingerPrintHelper {
    
    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String API_URL = "/fineract-provider/api/v1";
    public static final String DATE_FORMAT = "dd MMMM yyyy";
    
    public FingerPrintHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer createFingerPrint(final String entityType, final String entityId) {
        System.out.println("--------------------------------- Create fingerPrint " + entityType + " -------------------------------");
        System.out.println(createFingerPrintOperationURL(entityType, entityId));
        final Integer resourceId = Utils.performServerPost(this.requestSpec, this.responseSpec,
                createFingerPrintOperationURL(entityType, entityId), getTestCreateFingerPrintAsJSON(), "resourceId");
        System.out.println("resource Id : " + resourceId);
        return resourceId;
    }
    private String createFingerPrintOperationURL(final String entityType, final String entityId) {
        return API_URL + "/" + entityType + "/" + entityId + "/fingerprint?" + Utils.TENANT_IDENTIFIER;
    }

    public String getTestCreateFingerPrintAsJSON() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("fingerId", "1");
        map.put("fingerData", "IfkAAAD/////bvAARk1SACAyMAAAAADwAAABAAGQAMUAxQEAAAAzI0DIABTsSoBjACAIXUDFAFJk"+
                "XUBeAGL8XUAfAGkcXYC3AGvoXUAYAIOgXYBHAKEYV4BeAKQEV4B2AKiEUEDYALngXUC1AMfUXUDU"+
                "AMfQXYA9AOHEXUCPAOGsUEAoAOjAXYBzAOiQUECpAPG4V0CNAPSkV0DBAPvEXYBCAQTsXUDUARXE"+
                "XUCzAReoXUCLAR6cXUBAASXwV0AvASrkXYBVATF0V4DWATO4XUDfAUi8XYBcAU8AXUCsAU+YXYCB"+
                "AVKMXUB4AWIMXYDDAXoQXYClAXwMXQAA");
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        return new Gson().toJson(map);
    }

}
