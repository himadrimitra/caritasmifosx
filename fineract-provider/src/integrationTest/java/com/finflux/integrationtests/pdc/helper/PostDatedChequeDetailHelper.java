package com.finflux.integrationtests.pdc.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.Utils;
import org.joda.time.LocalDate;

import com.finflux.integrationtests.pdc.builder.PostDatedChequeDetailBuilder;
import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class PostDatedChequeDetailHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private String locale = "en";
    private String dateFormat = "dd MMMM yyyy";
    private final String API_URL = "/fineract-provider/api/v1/pdcm";
    private final String TENANT_IDENTIFIER = "?" + Utils.TENANT_IDENTIFIER;

    public PostDatedChequeDetailHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object createRepaymentPDC(final String entityType, final Integer entityId, final Integer paymentTypeId) throws ParseException {
        final String API_TEMPLATE_URL = this.API_URL + "/" + entityType + "/" + entityId + "/template" + this.TENANT_IDENTIFIER;
        final HashMap template = (HashMap) Utils.performServerGet(this.requestSpec, this.responseSpec, API_TEMPLATE_URL, "");
        final List<HashMap<String, Object>> pdcTypeOptions = (List<HashMap<String, Object>>) template.get("pdcTypeOptions");
        final List<HashMap<String, Object>> loanSchedulePeriods = (List<HashMap<String, Object>>) template.get("loanSchedulePeriods");
        final PostDatedChequeDetailBuilder builder = new PostDatedChequeDetailBuilder();
        final String numberOfPDC = "5";
        builder.withNumberOfPDC(numberOfPDC);
        for (final HashMap<String, Object> pdcType : pdcTypeOptions) {
            builder.withChequeType(pdcType.get("id").toString());
            break;
        }
        builder.withChequeNumbers("10000");
        builder.withPaymentType(paymentTypeId.toString());
        int i = 1;
        int n = Integer.parseInt(numberOfPDC);
        for (final HashMap<String, Object> loanSchedule : loanSchedulePeriods) {
            final ArrayList dueDateList = (ArrayList) loanSchedule.get("dueDate");
            int year = (int) dueDateList.get(0);
            int monthOfYear = (int) dueDateList.get(1);
            int dayOfMonth = (int) dueDateList.get(2);
            final LocalDate dueDate = new LocalDate(year, monthOfYear, dayOfMonth);
            final String totalDueForPeriod = loanSchedule.get("totalDueForPeriod").toString();
            final String dueDateStr = DateUtils.convertLocalDateToStringUsingDatePattern(dueDate, "dd MMMM yyyy");
            builder.withChequeDateAndAmounts(dueDateStr, totalDueForPeriod);
            if (i == n) {
                break;
            }
            i++;
        }
        final String requestBodyAsJson = builder.withPdcDetail().createBuild();
        final String API_CREATE_URL = this.API_URL + "/" + entityType + "/" + entityId + this.TENANT_IDENTIFIER;
        return Utils.performServerPost(this.requestSpec, this.responseSpec, API_CREATE_URL, requestBodyAsJson, "");
    }

    public Object updateRepaymentPDC(final Integer pdcId) {
        final PostDatedChequeDetailBuilder builder = new PostDatedChequeDetailBuilder();
        builder.withChequeNumber("90000");
        final String requestBodyAsJson = builder.updateBuild();
        final String API_URL = this.API_URL + "/" + pdcId + this.TENANT_IDENTIFIER;
        Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
        return Utils.performServerGet(this.requestSpec, this.responseSpec, API_URL, "");
    }

    public Object updateResponceWithErrors(final Integer clientId, final Integer accountLimitId, final String requestBodyAsJson) {
        final String API_URL = this.API_URL.replace("{clientId}", clientId.toString()) + "/" + accountLimitId + this.TENANT_IDENTIFIER;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, CommonConstants.RESPONSE_ERROR);
    }

    public Object getOnePDCDetails(final Integer pdcId) {
        final PostDatedChequeDetailBuilder builder = new PostDatedChequeDetailBuilder();
        builder.withChequeNumber("90000");
        final String requestBodyAsJson = builder.updateBuild();
        final String API_URL = this.API_URL + "/" + pdcId + this.TENANT_IDENTIFIER;
        Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
        return Utils.performServerGet(this.requestSpec, this.responseSpec, API_URL, "");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object searchPDC() {
        final String API_SEARCH_TEMPLATE_URL = this.API_URL + "/search/template" + this.TENANT_IDENTIFIER;
        final HashMap searchTemplate = (HashMap) Utils.performServerGet(this.requestSpec, this.responseSpec, API_SEARCH_TEMPLATE_URL, "");
        final List<HashMap<String, Object>> pdcTypeOptions = (List<HashMap<String, Object>>) searchTemplate.get("pdcTypeOptions");
        final List<HashMap<String, Object>> chequeStatusOptions = (List<HashMap<String, Object>>) searchTemplate.get("chequeStatusOptions");
        String chequeType = null;
        String chequeStatus = null;
        for (final HashMap<String, Object> pdcType : pdcTypeOptions) {
            chequeType = pdcType.get("id").toString();
            break;
        }
        for (final HashMap<String, Object> status : chequeStatusOptions) {
            if (status.get("code").toString().equalsIgnoreCase("chequeStatus.pending")) {
                chequeStatus = status.get("id").toString();
                break;
            }
        }
        final HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("officeId", 1);
        map.put("chequeType", chequeType);
        map.put("chequeStatus", chequeStatus);
        try {
            map.put("fromDate", DateUtils.convertLocalDateToStringUsingDatePattern(DateUtils.getLocalDateOfTenant(), this.dateFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);
        final String requestBodyAsJson = new Gson().toJson(map);
        final String API_URL = this.API_URL + "/search" + this.TENANT_IDENTIFIER;
        return Utils.performServerPost(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
    }

    public Object presentPDC(final String command, final List<HashMap<String, Object>> pdcDatas) throws ParseException {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("date", DateUtils.convertLocalDateToStringUsingDatePattern(DateUtils.getLocalDateOfTenant(), this.dateFormat));
        map.put("description", "Presente check details");
        final List<String> pdcChequeDetails = new ArrayList<>();
        for (final HashMap<String, Object> pdcData : pdcDatas) {
            pdcChequeDetails.add(pdcData.get("id").toString());
        }
        map.put("pdcChequeDetails", pdcChequeDetails.toArray(new String[pdcChequeDetails.size()]));
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);

        final String requestBodyAsJson = new Gson().toJson(map);
        final String API_URL = this.API_URL + this.TENANT_IDENTIFIER + "&command=" + command;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");

    }

    public Object clearPDC(final String command, final List<HashMap<String, Object>> pdcDatas) {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        try {
            map.put("date", DateUtils.convertLocalDateToStringUsingDatePattern(DateUtils.getLocalDateOfTenant(), dateFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.put("description", "Clear check details");
        final List<String> pdcChequeDetails = new ArrayList<>();
        for (final HashMap<String, Object> pdcData : pdcDatas) {
            pdcChequeDetails.add(pdcData.get("id").toString());
        }
        map.put("pdcChequeDetails", pdcChequeDetails.toArray(new String[pdcChequeDetails.size()]));
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);

        final String requestBodyAsJson = new Gson().toJson(map);
        final String API_URL = this.API_URL + this.TENANT_IDENTIFIER + "&command=" + command;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
    }

    public Object undoPDC(final String command, final List<HashMap<String, Object>> pdcDatas) {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        try {
            map.put("date", DateUtils.convertLocalDateToStringUsingDatePattern(DateUtils.getLocalDateOfTenant(), dateFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.put("description", "Undo check details");
        final List<String> pdcChequeDetails = new ArrayList<>();
        for (final HashMap<String, Object> pdcData : pdcDatas) {
            pdcChequeDetails.add(pdcData.get("id").toString());
        }
        map.put("pdcChequeDetails", pdcChequeDetails.toArray(new String[pdcChequeDetails.size()]));
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);

        final String requestBodyAsJson = new Gson().toJson(map);
        final String API_URL = this.API_URL + this.TENANT_IDENTIFIER + "&command=" + command;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
    }

    public Object bouncedPDC(final String command, final List<HashMap<String, Object>> pdcDatas) {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        try {
            map.put("date", DateUtils.convertLocalDateToStringUsingDatePattern(DateUtils.getLocalDateOfTenant(), dateFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.put("description", "Bounced check details");
        final List<String> pdcChequeDetails = new ArrayList<>();
        for (final HashMap<String, Object> pdcData : pdcDatas) {
            pdcChequeDetails.add(pdcData.get("id").toString());
        }
        map.put("pdcChequeDetails", pdcChequeDetails.toArray(new String[pdcChequeDetails.size()]));
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);

        final String requestBodyAsJson = new Gson().toJson(map);
        final String API_URL = this.API_URL + this.TENANT_IDENTIFIER + "&command=" + command;
        return Utils.performServerPut(this.requestSpec, this.responseSpec, API_URL, requestBodyAsJson, "");
    }
}
