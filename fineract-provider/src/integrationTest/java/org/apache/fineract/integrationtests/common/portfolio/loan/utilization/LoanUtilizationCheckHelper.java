package org.apache.fineract.integrationtests.common.portfolio.loan.utilization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.organisation.StaffHelper;

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
        Integer staffId = StaffHelper.createStaff(requestSpec, responseSpec);
        final Integer loanUtilizationCheckId = Utils.performServerPost(requestSpec, responseSpec,
                createLoanUtilizationCheckOperationURL(loanId), getCreateLoanUtilizationCheckRequestBodyAsJSON(loanId, staffId),
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
    public static String getCreateLoanUtilizationCheckRequestBodyAsJSON(final Integer loanId, final Integer staffId) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("auditDoneById", staffId);
        map.put("auditDoneOn", "15 Dec 2014");

        final List<Map> loanUtilizationCheckDetails = new ArrayList<>();
        final Map<String, Object> loanUtilizationCheckDetail = new LinkedHashMap<>();
        loanUtilizationCheckDetail.put("loanId", loanId);

        final List<Map> utilizationDetails = new ArrayList<>();
        final Map<String, Object> utilizationDetail = new LinkedHashMap<>();
        utilizationDetail.put("isSameAsOriginalPurpose", true);
        utilizationDetail.put("amount", 50000.00);
        utilizationDetail.put("comment", "ok");
        utilizationDetails.add(utilizationDetail);
        loanUtilizationCheckDetail.put("utilizationDetails", utilizationDetails);

        loanUtilizationCheckDetails.add(loanUtilizationCheckDetail);
        map.put("loanUtilizationCheckDetails", loanUtilizationCheckDetails);

        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    @SuppressWarnings("rawtypes")
    private static String getUpdateLoanPurposeGroupRequestBodyAsJSON(final Integer loanId) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("auditDoneOn", "15 Dec 2014");

        final List<Map> loanUtilizationCheckDetails = new ArrayList<>();
        final Map<String, Object> loanUtilizationCheckDetail = new LinkedHashMap<>();
        loanUtilizationCheckDetail.put("loanId", loanId);

        final List<Map> utilizationDetails = new ArrayList<>();
        final Map<String, Object> utilizationDetail = new LinkedHashMap<>();
        utilizationDetail.put("isSameAsOriginalPurpose", true);
        utilizationDetail.put("amount", 50000.00);
        utilizationDetail.put("comment", "ok");
        utilizationDetails.add(utilizationDetail);
        loanUtilizationCheckDetail.put("utilizationDetails", utilizationDetails);

        loanUtilizationCheckDetails.add(loanUtilizationCheckDetail);
        map.put("loanUtilizationCheckDetails", loanUtilizationCheckDetails);

        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
}