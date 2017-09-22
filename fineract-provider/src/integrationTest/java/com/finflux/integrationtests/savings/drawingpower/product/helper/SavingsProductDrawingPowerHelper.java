package com.finflux.integrationtests.savings.drawingpower.product.helper;

import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.savings.SavingsProductHelper;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class SavingsProductDrawingPowerHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String SAVINGS_PRODUCT_URL = "/fineract-provider/api/v1/savingsproducts";
    private static final String CREATE_SAVINGS_PRODUCT_URL = SAVINGS_PRODUCT_URL + "?" + Utils.TENANT_IDENTIFIER;

    public SavingsProductDrawingPowerHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer createSavingsProduct() {
        System.out.println("----------------- CREATING SAVINGS PRODUCT DRAWING POWER ---------------------------------");
        return Utils
                .performServerPost(requestSpec, responseSpec, CREATE_SAVINGS_PRODUCT_URL, getCreateSavingsProductAsJSON(), "resourceId");
    }

    public static String getCreateSavingsProductAsJSON() {
        final SavingsProductHelper savingsProductHelper = new SavingsProductHelper();
        savingsProductHelper.withOverDraft("100000.00");
        savingsProductHelper.withAllowDpLimit();
        return savingsProductHelper.build();

    }
}