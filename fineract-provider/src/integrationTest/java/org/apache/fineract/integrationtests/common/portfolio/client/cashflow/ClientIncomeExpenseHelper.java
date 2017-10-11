package org.apache.fineract.integrationtests.common.portfolio.client.cashflow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class ClientIncomeExpenseHelper {

    private static final String API_URL = "/fineract-provider/api/v1";
    public static final String DATE_FORMAT = "dd MMMM yyyy";

    public static Integer createClientIncomeExpense(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer clientId, final Integer incomeExpenseId) {
        System.out.println("----------------------------Create Client Income Expense------------------------------------");
        System.out.println(createClientIncomeExpenseOperationURL(clientId));
        return Utils.performServerPost(requestSpec, responseSpec, createClientIncomeExpenseOperationURL(clientId),
                getCreateClientIncomeExpenseRequestBodyAsJSON(incomeExpenseId), "resourceId");
    }

    public static Integer updateClientIncomeExpense(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer clientId, final Integer clientIncomeExpenseId, final Integer incomeExpenseId) {
        System.out.println("----------------------------Update Client Income Expense------------------------------------");
        System.out.println(updateClientIncomeExpenseOperationURL(clientId, clientIncomeExpenseId));
        return Utils.performServerPut(requestSpec, responseSpec, updateClientIncomeExpenseOperationURL(clientId, clientIncomeExpenseId),
                getUpdateClientIncomeExpenseRequestBodyAsJSON(incomeExpenseId), "resourceId");
    }

    private static String createClientIncomeExpenseOperationURL(final Integer clientId) {
        return API_URL + "/clients/" + clientId + "/incomesandexpenses?" + Utils.TENANT_IDENTIFIER;
    }

    private static String updateClientIncomeExpenseOperationURL(final Integer clientId, final Integer clientIncomeExpenseId) {
        return API_URL + "/clients/" + clientId + "/incomesandexpenses/" + clientIncomeExpenseId + "?" + Utils.TENANT_IDENTIFIER;
    }

    @SuppressWarnings("rawtypes")
    public static String getCreateClientIncomeExpenseRequestBodyAsJSON(final Integer incomeExpenseId) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("incomeExpenseId", incomeExpenseId);
        map.put("quintity", 100.00);
        map.put("totalIncome", 100000.00);
        map.put("totalExpense", 10000.00);
        map.put("isMonthWiseIncome", true);
        map.put("isPrimaryIncome", true);
        final List<Map> clientMonthWiseIncomeExpense = new ArrayList<>();
        final Map<String, Object> clientMonthWiseIncomeExpenseObj = new LinkedHashMap<>();
        clientMonthWiseIncomeExpenseObj.put("month", 1);
        clientMonthWiseIncomeExpenseObj.put("year", 2008);
        clientMonthWiseIncomeExpenseObj.put("incomeAmount", 50000.00);
        clientMonthWiseIncomeExpenseObj.put("expenseAmount", 25000.50);
        clientMonthWiseIncomeExpenseObj.put("locale", "en");
        clientMonthWiseIncomeExpenseObj.put("dateFormat", DATE_FORMAT);
        clientMonthWiseIncomeExpense.add(clientMonthWiseIncomeExpenseObj);
        map.put("clientMonthWiseIncomeExpense", clientMonthWiseIncomeExpense);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    @SuppressWarnings("rawtypes")
    private static String getUpdateClientIncomeExpenseRequestBodyAsJSON(final Integer incomeExpenseId) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("incomeExpenseId", incomeExpenseId);
        map.put("quintity", 1000.00);
        map.put("totalIncome", 100000.00);
        map.put("totalExpense", 10000.00);
        map.put("isMonthWiseIncome", true);
        map.put("isPrimaryIncome", true);
        final List<Map> clientMonthWiseIncomeExpense = new ArrayList<>();
        final Map<String, Object> clientMonthWiseIncomeExpenseObj = new LinkedHashMap<>();
        clientMonthWiseIncomeExpenseObj.put("month", 11);
        clientMonthWiseIncomeExpenseObj.put("year", 2011);
        clientMonthWiseIncomeExpenseObj.put("incomeAmount", 50000.00);
        clientMonthWiseIncomeExpenseObj.put("expenseAmount", 25000.50);
        clientMonthWiseIncomeExpenseObj.put("locale", "en");
        clientMonthWiseIncomeExpenseObj.put("dateFormat", DATE_FORMAT);
        clientMonthWiseIncomeExpense.add(clientMonthWiseIncomeExpenseObj);
        map.put("clientMonthWiseIncomeExpense", clientMonthWiseIncomeExpense);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
}
