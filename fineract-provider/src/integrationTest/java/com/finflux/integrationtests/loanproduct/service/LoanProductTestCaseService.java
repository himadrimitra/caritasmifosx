package com.finflux.integrationtests.loanproduct.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.junit.Assert;

import com.finflux.integrationtests.codevalue.service.CodeValueService;
import com.finflux.integrationtests.loanproduct.helper.LoanProductHelper;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class LoanProductTestCaseService {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private LoanProductHelper loanProductHelper;
    private CodeValueService codeValueService;

    private List<Integer> clientTypeValues;
    private List<Integer> clientClassificationValues;

    public LoanProductTestCaseService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.loanProductHelper = new LoanProductHelper(requestSpec, responseSpec);
        this.codeValueService = new CodeValueService(requestSpec, responseSpec);
        initializeRequiredEntities();
    }

    private void initializeRequiredEntities() {
        String codeName = "ClientType";
        this.clientTypeValues = this.codeValueService.retrieveOrCreateCodeValue(codeName);

        codeName = "ClientClassification";
        this.clientClassificationValues = this.codeValueService.retrieveOrCreateCodeValue(codeName);
    }

    public void runLoanProductTestCases() {
        applicableForAllCustomers();
        applicableForClients();
        applicableForGroups();

        notApplicableForClientsProfileTypeLegalForm();
        notApplicableForClientsProfileTypeClientType();
    }

    /************************ Start positive scenarios ************************************************/
    public Integer applicableForAllCustomers() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR ALL CUSTOMERS-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder().build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;
    }

    private void applicableForClients() {
        applicableForAllClients();
        applicableForClientsProfileTypeLegalForm();
        applicableForClientsProfileTypeClientType();
        applicableForClientsProfileTypeClientClassification();
    }

    public Integer applicableForAllClients() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR ALL CLIENTS-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder().withApplicableForAllClients()
                .build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;
    }

    private void applicableForClientsProfileTypeLegalForm() {
        applicableForClientProfileTypeLegalFormTypePerson();
        applicableForClientProfileTypeLegalFormTypeEntity();
        applicableForClientProfileTypeLegalFormTypePersonOrEntity();
    }

    public Integer applicableForClientProfileTypeLegalFormTypePerson() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR CLIENTS WITH LEGAL TYPE PERSON-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeLegalFormPerson().build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;
    }

    public Integer applicableForClientProfileTypeLegalFormTypeEntity() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR CLIENTS WITH LEGAL TYPE ENTITY-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeLegalFormEntity().build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;

    }

    public Integer applicableForClientProfileTypeLegalFormTypePersonOrEntity() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR CLIENTS WITH LEGAL TYPE PERSON OR ENTITY-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeLegalFormPersonOrEntity().build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;
    }

    public Integer applicableForClientsProfileTypeClientType() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR CLIENTS WITH CLIENT TYPES-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeAllClientTypes(clientTypeValues).build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;
    }

    public Integer applicableForClientsProfileTypeClientClassification() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR CLIENTS WITH CLIENT CLASSIFICATION-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeAllClientClassification(clientClassificationValues).build(null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;

    }

    public Integer applicableForGroups() {
        System.out.println("---------------CREATING NEW LOAN PRODUCT FOR GROUPS-----------------");
        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder().withApplicableForGroups().build(
                null));
        Assert.assertNotNull(loanProductId);
        return loanProductId;
    }

    /************************ End positive scenarios ************************************************/

    /************************ Start negative scenarios ************************************************/

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void notApplicableForClientsProfileTypeLegalForm() {
        System.out.println("---------------VALIDATING NEW LOAN PRODUCT FOR CLIENTS WITH LEGAL TYPE PERSON OR ENTITY-----------------");

        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(400).build();
        final LoanProductHelper loanProductHelper = new LoanProductHelper(this.requestSpec, responseSpec);

        HashMap response = loanProductHelper.createLoanProduct(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeLegalFormPersonOrEntity().withSelectedProfileTypeValues(clientTypeValues).build(null));

        ArrayList<HashMap> errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.loanproduct.selectedProfileTypeValues.is.not.one.of.expected.enumerations",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void notApplicableForClientsProfileTypeClientType() {
        System.out.println("---------------VALIDATING NEW LOAN PRODUCT FOR CLIENTS WITH CLIENT TYPES-----------------");

        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(400).build();
        final LoanProductHelper loanProductHelper = new LoanProductHelper(this.requestSpec, responseSpec);

        final HashMap response = loanProductHelper.createLoanProduct(new LoanProductTestBuilder()
                .withApplicableForClientProfileTypeAllClientTypes(this.clientClassificationValues).build(null));

        final ArrayList<HashMap> errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.loanproduct.selectedProfileTypeValues.not.belongs.to.client.type",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    /************************ End negative scenarios ************************************************/
}