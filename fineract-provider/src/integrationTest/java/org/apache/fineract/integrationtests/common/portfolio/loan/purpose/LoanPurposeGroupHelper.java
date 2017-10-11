package org.apache.fineract.integrationtests.common.portfolio.loan.purpose;

import java.util.HashMap;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.system.CodeHelper;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanPurposeGroupHelper {

    private static final String API_URL = "/fineract-provider/api/v1";
    private static final String CREATE_LOAN_PURPOSE_GROUP_URL = API_URL + "/loanpurposegroups?" + Utils.TENANT_IDENTIFIER;
    private static final String LOAN_PURPOSE_GROUP_URL = API_URL + "/loanpurposegroups";
    private static final String LOAN_PURPOSE_URL = API_URL + "/loanpurposes";
    public static final String DATE_FORMAT = "dd MMMM yyyy";

    public static Integer createLoanPurposeGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        System.out.println("----------------------------Create Loan Purpose Group------------------------------------");
        HashMap<String, Object> code = CodeHelper.getCodeByName(requestSpec, responseSpec, "LoanPurposeGroupType");
        HashMap<String, Object> codeValue = CodeHelper.retrieveOrCreateCodeValue((Integer) code.get("id"), requestSpec, responseSpec);
        final Integer loanPurposeGroupId = Utils.performServerPost(requestSpec, responseSpec, CREATE_LOAN_PURPOSE_GROUP_URL,
                getCreateLoanPurposeGroupRequestBodyAsJSON(codeValue), "resourceId");
        System.out.println("Loan Purpose Group Id : " + loanPurposeGroupId);
        return loanPurposeGroupId;
    }

    public static Integer updateLoanPurposeGroup(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanPurposeGroupId) {
        System.out.println("----------------------------Update Loan Purpose Group------------------------------------");
        System.out.println(updateLoanPurposeGroupOperationURL(loanPurposeGroupId));
        HashMap<String, Object> code = CodeHelper.getCodeByName(requestSpec, responseSpec, "LoanPurposeGroupType");
        HashMap<String, Object> codeValue = CodeHelper.retrieveOrCreateCodeValue((Integer) code.get("id"), requestSpec, responseSpec);
        return Utils.performServerPut(requestSpec, responseSpec, updateLoanPurposeGroupOperationURL(loanPurposeGroupId),
                getUpdateLoanPurposeGroupRequestBodyAsJSON(codeValue), "resourceId");
    }

    public static String getCreateLoanPurposeGroupRequestBodyAsJSON(final HashMap<String, Object> codeValue) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Loan Purpose Group Name");
        map.put("systemCode", "LPG");
        map.put("description", "Loan Purpose Group Description");
        map.put("loanPurposeGroupTypeId", codeValue.get("id"));
        map.put("isActive", true);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String getUpdateLoanPurposeGroupRequestBodyAsJSON(final HashMap<String, Object> codeValue) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Update Loan Purpose Group Name");
        map.put("description", "Update Loan Purpose Group Description");
        map.put("loanPurposeGroupTypeId", codeValue.get("id"));
        map.put("isActive", false);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String updateLoanPurposeGroupOperationURL(Integer loanPurposeGroupId) {
        return LOAN_PURPOSE_GROUP_URL + "/" + loanPurposeGroupId + "?" + Utils.TENANT_IDENTIFIER;
    }

    private static String createLoanPurposeOperationURL() {
        return LOAN_PURPOSE_URL + "?" + Utils.TENANT_IDENTIFIER;
    }

    private static String updateLoanPurposeOperationURL(Integer loanPurposeId) {
        return LOAN_PURPOSE_URL + "/" + loanPurposeId + "?" + Utils.TENANT_IDENTIFIER;
    }

    public static Integer createLoanPurpose(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        System.out.println("----------------------------Create Loan Purpose------------------------------------");
        System.out.println(createLoanPurposeOperationURL());
        final Integer loanPurposeId = Utils.performServerPost(requestSpec, responseSpec, createLoanPurposeOperationURL(),
                getCreateLoanPurposeRequestBodyAsJSON(), "resourceId");
        System.out.println("Loan Purpose Id : " + loanPurposeId);
        return loanPurposeId;
    }

    public static Integer updateLoanPurpose(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanPurposeId) {
        System.out.println("---------------------------------Update Loan Purpose---------------------------------------------");
        System.out.println(updateLoanPurposeOperationURL(loanPurposeId));
        return Utils.performServerPut(requestSpec, responseSpec, updateLoanPurposeOperationURL(loanPurposeId),
                getUpdateLoanPurposeRequestBodyAsJSON(), "resourceId");
    }

    private static String getCreateLoanPurposeRequestBodyAsJSON() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Loan Purpose Name");
        map.put("systemCode", "LP");
        map.put("description", "Loan Purpose Description");
        map.put("isActive", true);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String getUpdateLoanPurposeRequestBodyAsJSON() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Update Loan Purpose Name");
        map.put("description", "Update Loan Purpose Description");
        map.put("isActive", false);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
}
