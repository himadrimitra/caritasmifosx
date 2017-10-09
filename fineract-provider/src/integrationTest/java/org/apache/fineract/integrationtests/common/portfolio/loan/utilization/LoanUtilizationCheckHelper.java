package org.apache.fineract.integrationtests.common.portfolio.loan.utilization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanUtilizationCheckHelper {

    private static final String API_URL = "/fineract-provider/api/v1";
    public static final String DATE_FORMAT = "dd MMMM yyyy";

    public static Integer createLoanUtilizationCheck(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanId) {
        System.out.println("----------------------------Create Loan Utilization Check------------------------------------");
        System.out.println(createLoanUtilizationCheckOperationURL(loanId));
        Integer auditDoneById = 1;
        final Integer loanUtilizationCheckId = Utils.performServerPost(requestSpec, responseSpec,
                createLoanUtilizationCheckOperationURL(loanId), getCreateLoanUtilizationCheckRequestBodyAsJSON(loanId, auditDoneById),
                "resourceId");
        return loanUtilizationCheckId;
    }

    public static Integer updateLoanUtilizationCheck(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanId, final Integer loanUtilizationCheckId) {
        System.out.println("----------------------------Update Loan Utilization Check------------------------------------");
        System.out.println(updateLoanUtilizationCheckOperationURL(loanId, loanUtilizationCheckId));
        return Utils.performServerPut(requestSpec, responseSpec, updateLoanUtilizationCheckOperationURL(loanId, loanUtilizationCheckId),
                getUpdateLoanPurposeGroupRequestBodyAsJSON(loanId), "resourceId");
    }

    private static String createLoanUtilizationCheckOperationURL(final Integer loanId) {
        return API_URL + "/loans/" + loanId + "/utilizationchecks?" + Utils.TENANT_IDENTIFIER;
    }

    private static String updateLoanUtilizationCheckOperationURL(final Integer loanId, final Integer loanUtilizationCheckId) {
        return API_URL + "/loans/" + loanId + "/utilizationchecks/" + loanUtilizationCheckId + "?" + Utils.TENANT_IDENTIFIER;
    }

    @SuppressWarnings("rawtypes")
    public static String getCreateLoanUtilizationCheckRequestBodyAsJSON(final Integer loanId, final Integer auditDoneById) {
        final Map<String, Object> map = new LinkedHashMap<>();
        final List<Map> loanUtilizationChecks = new ArrayList<>();
        final Map<String, Object> loanUtilizationCheck = new LinkedHashMap<>();
        loanUtilizationCheck.put("loanId", loanId);
        loanUtilizationCheck.put("auditDoneById", auditDoneById);
        loanUtilizationCheck.put("auditDoneOn", "15 Dec 2014");
        final Map<String, Object> utilizationDetail = new LinkedHashMap<>();
        utilizationDetail.put("isSameAsOriginalPurpose", true);
        utilizationDetail.put("amount", 1000.00);
        utilizationDetail.put("comment", "ok");
        utilizationDetail.put("locale", "en");
        utilizationDetail.put("dateFormat", DATE_FORMAT);
        loanUtilizationCheck.put("utilizationDetails", utilizationDetail);
        loanUtilizationCheck.put("locale", "en");
        loanUtilizationCheck.put("dateFormat", DATE_FORMAT);
        loanUtilizationChecks.add(loanUtilizationCheck);
        map.put("loanUtilizationChecks", loanUtilizationChecks);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String getUpdateLoanPurposeGroupRequestBodyAsJSON(final Integer loanId) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("loanId", loanId);
        map.put("auditDoneById", 1);
        map.put("auditDoneOn", "15 Dec 2014");
        final Map<String, Object> utilizationDetail = new LinkedHashMap<>();
        utilizationDetail.put("isSameAsOriginalPurpose", true);
        utilizationDetail.put("amount", 1000.00);
        utilizationDetail.put("comment", "ok edited");
        utilizationDetail.put("locale", "en");
        utilizationDetail.put("dateFormat", DATE_FORMAT);
        map.put("utilizationDetails", utilizationDetail);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
}