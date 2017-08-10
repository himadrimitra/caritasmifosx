package org.apache.fineract.integrationtests.common.portfolio.cashflow;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CashFlowCategoryHelper {

    private static final String API_URL = "/fineract-provider/api/v1";
    private static final String CREATE_CASH_FLOW_CATEGORY_URL = API_URL + "/cashflowcategories?" + Utils.TENANT_IDENTIFIER;
    private static final String CASH_FLOW_CATEGORY_URL = API_URL + "/cashflowcategories";
    private static final String INCOME_EXPENSE_URL = API_URL + "/incomesorexpenses";
    public static final String DATE_FORMAT = "dd MMMM yyyy";

    public static Integer createCashFlowCategory(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        System.out.println("----------------------------Create Cash Flow Category------------------------------------");
        final Integer cashFlowCategoryId = Utils.performServerPost(requestSpec, responseSpec, CREATE_CASH_FLOW_CATEGORY_URL,
                getCreateCashFlowCategoryRequestBodyAsJSON(), "resourceId");
        System.out.println("Cash Flow Category Id : " + cashFlowCategoryId);
        return cashFlowCategoryId;
    }

    public static Integer updateCashFlowCategory(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer cashFlowCategoryId) {
        System.out.println("----------------------------Update Cash Flow Category------------------------------------");
        System.out.println(updateCashFlowCategoryOperationURL(cashFlowCategoryId));
        return Utils.performServerPut(requestSpec, responseSpec, updateCashFlowCategoryOperationURL(cashFlowCategoryId),
                getUpdateCashFlowCategoryRequestBodyAsJSON(), "resourceId");
    }

    public static String getCreateCashFlowCategoryRequestBodyAsJSON() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", Utils.randomNameGenerator("Cash Flow Category Name", 8));
        map.put("shortName", Utils.randomNameGenerator("CashFlowCategoryName", 4).toUpperCase());
        map.put("description", "Cash Flow Category Description");
        map.put("categoryEnumId", 1);
        map.put("typeEnumId", 1);
        map.put("isActive", true);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String getUpdateCashFlowCategoryRequestBodyAsJSON() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Update " + Utils.randomNameGenerator("Cash Flow Category Name", 8));
        map.put("description", "Update Cash Flow Category Description");
        map.put("categoryEnumId", 2);
        map.put("typeEnumId", 2);
        map.put("isActive", true);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String updateCashFlowCategoryOperationURL(Integer cashFlowCategoryId) {
        return CASH_FLOW_CATEGORY_URL + "/" + cashFlowCategoryId + "?" + Utils.TENANT_IDENTIFIER;
    }

    private static String createIncomeExpenseOperationURL() {
        return INCOME_EXPENSE_URL + "?" + Utils.TENANT_IDENTIFIER;
    }

    private static String updateIncomeExpenseOperationURL(Integer incomeExpenseId) {
        return INCOME_EXPENSE_URL + "/" + incomeExpenseId + "?" + Utils.TENANT_IDENTIFIER;
    }

    public static Integer createIncomeExpense(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer cashFlowCategoryId) {
        System.out.println("----------------------------Create Income Expense------------------------------------");
        System.out.println(createIncomeExpenseOperationURL());
        final Integer incomeExpenseId = Utils.performServerPost(requestSpec, responseSpec, createIncomeExpenseOperationURL(),
                getCreateIncomeExpenseRequestBodyAsJSON(cashFlowCategoryId), "resourceId");
        System.out.println("Income Expense Id : " + incomeExpenseId);
        return incomeExpenseId;
    }

    public static Integer updateIncomeExpense(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer incomeExpenseId) {
        System.out.println("---------------------------------Update Income Expense---------------------------------------------");
        System.out.println(updateIncomeExpenseOperationURL(incomeExpenseId));
        return Utils.performServerPut(requestSpec, responseSpec, updateIncomeExpenseOperationURL(incomeExpenseId),
                getUpdateIncomeExpenseRequestBodyAsJSON(), "resourceId");
    }

    private static String getCreateIncomeExpenseRequestBodyAsJSON(final Integer cashFlowCategoryId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("cashflowCategoryId", cashFlowCategoryId);
        map.put("name", Utils.randomNameGenerator("Income Expense Name", 8));
        map.put("description", "Income Expense Description");
        map.put("isQuantifierNeeded", true);
        map.put("quantifierLabel", "quantifierLabel");
        map.put("isCaptureMonthWiseIncome", true);
        map.put("stabilityEnumId", 1);
        map.put("defaultIncome", 10000.00);
        map.put("defaultExpense", 1000.00);
        map.put("isActive", true);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String getUpdateIncomeExpenseRequestBodyAsJSON() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Update " + Utils.randomNameGenerator("Income Expense Name", 8));
        map.put("description", "Update Income Expense Description");
        map.put("isQuantifierNeeded", true);
        map.put("quantifierLabel", "quantifierLabel");
        map.put("isCaptureMonthWiseIncome", true);
        map.put("stabilityEnumId", 2);
        map.put("defaultIncome", 20000.00);
        map.put("defaultExpense", 2000.00);
        map.put("isActive", true);
        map.put("locale", "en");
        map.put("dateFormat", DATE_FORMAT);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }
}
