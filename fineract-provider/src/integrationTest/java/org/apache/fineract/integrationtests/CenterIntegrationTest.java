/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CenterDomain;
import org.apache.fineract.integrationtests.common.CenterHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.organisation.StaffHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CenterIntegrationTest {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void testBasicCenterCreation() {
        int officeId = new OfficeHelper(requestSpec, responseSpec).createOffice("01 July 2007");

        String name = "TestBasicCreation" + new Timestamp(new java.util.Date().getTime());
        int resourceId = CenterHelper.createCenter(name, officeId, requestSpec, responseSpec);
        CenterDomain center = CenterHelper.retrieveByID(resourceId, requestSpec, responseSpec);

        Assert.assertNotNull(center);
        Assert.assertTrue(center.getName().equals(name));
        Assert.assertTrue(center.getOfficeId() == officeId);
        Assert.assertTrue(center.isActive() == false);

        // Test retrieval by listing all centers
        int id = CenterHelper.listCenters(requestSpec, responseSpec).get(0).getId();
        Assert.assertTrue(id > 0);

        CenterDomain retrievedCenter = CenterHelper.retrieveByID(id, requestSpec, responseSpec);
        Assert.assertNotNull(retrievedCenter);
        Assert.assertNotNull(retrievedCenter.getName());
        Assert.assertNotNull(retrievedCenter.getHierarchy());
        Assert.assertNotNull(retrievedCenter.getOfficeName());

    }

    @Test
    public void testFullCenterCreation() {

        int officeId = new OfficeHelper(requestSpec, responseSpec).createOffice("01 July 2007");
        String name = "TestFullCreation" + new Timestamp(new java.util.Date().getTime());
        String externalId = Utils.randomStringGenerator("ID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        int staffId = StaffHelper.createStaff(requestSpec, responseSpec);
        int[] groupMembers = generateGroupMembers(3, officeId);
        int resourceId = CenterHelper.createCenter(name, officeId, externalId, staffId, groupMembers, requestSpec, responseSpec);
        CenterDomain center = CenterHelper.retrieveByID(resourceId, requestSpec, responseSpec);

        Assert.assertNotNull(center);
        Assert.assertTrue(center.getName().equals(name));
        Assert.assertTrue(center.getOfficeId() == officeId);
        Assert.assertTrue(center.getExternalId().equals(externalId));
        Assert.assertTrue(center.getStaffId() == staffId);
        Assert.assertTrue(center.isActive() == false);
        Assert.assertArrayEquals(center.getGroupMembers(), groupMembers);
    }

    @Test
    public void testListCenters() {
        ArrayList<CenterDomain> paginatedList = CenterHelper.paginatedListCenters(requestSpec, responseSpec);
        ArrayList<CenterDomain> list = CenterHelper.listCenters(requestSpec, responseSpec);

        Assert.assertNotNull(paginatedList);
        Assert.assertNotNull(list);
        Assert.assertTrue(Arrays.equals(paginatedList.toArray(new CenterDomain[paginatedList.size()]),
                list.toArray(new CenterDomain[list.size()])));
    }

    @Test
    public void testVoidCenterRetrieval() {
        ArrayList<CenterDomain> arr = CenterHelper.listCenters(requestSpec, responseSpec);
        int id = arr.get(arr.size() - 1).getId() + 1;
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(404).build();
        CenterDomain center = CenterHelper.retrieveByID(id, requestSpec, responseSpec);
        Assert.assertNotNull(center);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCenterUpdate() {
        int officeId = new OfficeHelper(requestSpec, responseSpec).createOffice("01 July 2007");
        String name = "TestFullCreation" + new Timestamp(new java.util.Date().getTime());
        String externalId = Utils.randomStringGenerator("ID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        int staffId = StaffHelper.createStaff(requestSpec, responseSpec);
        int[] groupMembers = generateGroupMembers(3, officeId);
        int resourceId = CenterHelper.createCenter(name, officeId, externalId, staffId, groupMembers, requestSpec, responseSpec);

        String newName = "TestCenterUpdateNew" + new Timestamp(new java.util.Date().getTime());
        String newExternalId = Utils.randomStringGenerator("newID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        int newStaffId = StaffHelper.createStaff(requestSpec, responseSpec);
        int[] associateGroupMembers = generateGroupMembers(2, officeId);

        int[] associateResponse = CenterHelper.associateGroups(resourceId, associateGroupMembers, requestSpec, responseSpec);
        Arrays.sort(associateResponse);
        Arrays.sort(associateGroupMembers);
        Assert.assertArrayEquals(associateResponse, associateGroupMembers);

        int[] newGroupMembers = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i < 3) {
                newGroupMembers[i] = groupMembers[i];
            } else {
                newGroupMembers[i] = associateGroupMembers[i % 3];
            }
        }

        HashMap request = new HashMap();
        request.put("name", newName);
        request.put("externalId", newExternalId);
        request.put("staffId", newStaffId);
        HashMap response = CenterHelper.updateCenter(resourceId, request, requestSpec, responseSpec);
        Assert.assertNotNull(response);
        Assert.assertEquals(newName, response.get("name"));
        Assert.assertEquals(newExternalId, response.get("externalId"));
        Assert.assertEquals(newStaffId, response.get("staffId"));

        CenterDomain center = CenterHelper.retrieveByID(resourceId, requestSpec, responseSpec);
        Assert.assertNotNull(center);
        Assert.assertEquals(newName, center.getName());
        Assert.assertEquals(newExternalId, center.getExternalId());
        Assert.assertEquals((Integer)newStaffId, center.getStaffId());
        Assert.assertArrayEquals(newGroupMembers, center.getGroupMembers());
    }

    @Test
    public void testCenterDeletion() {
        int officeId = new OfficeHelper(requestSpec, responseSpec).createOffice("01 July 2007");
        String name = "TestBasicCreation" + new Timestamp(new java.util.Date().getTime());
        int resourceId = CenterHelper.createCenter(name, officeId, requestSpec, responseSpec);

        CenterHelper.deleteCenter(resourceId, requestSpec, responseSpec);
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(404).build();
        CenterDomain center = CenterHelper.retrieveByID(resourceId, requestSpec, responseSpec);
        Assert.assertNotNull(center);
    }

    private int[] generateGroupMembers(int size, int officeId) {
        int[] groupMembers = new int[size];
        for (int i = 0; i < groupMembers.length; i++) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("officeId", "" + officeId);
            map.put("name", Utils.randomStringGenerator("Group_Name_", 5));
            map.put("externalId", Utils.randomStringGenerator("ID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
            map.put("dateFormat", "dd MMMM yyyy");
            map.put("locale", "en");
            map.put("active", "true");
            map.put("activationDate", "04 March 2011");

            groupMembers[i] = Utils.performServerPost(requestSpec, responseSpec, "/fineract-provider/api/v1/groups?"
                    + Utils.TENANT_IDENTIFIER, new Gson().toJson(map), "groupId");
        }
        return groupMembers;
    }

    @Test
    public void testStaffAssignmentDuringCenterCreation() {

        Integer staffId = StaffHelper.createStaff(this.requestSpec, this.responseSpec);
        System.out.println("--------------creating first staff with id-------------" + staffId);
        Assert.assertNotNull(staffId);

        int centerWithStaffId = CenterHelper.createCenterWithStaffId(this.requestSpec, this.responseSpec, staffId);
        CenterDomain center = CenterHelper.retrieveByID(centerWithStaffId, requestSpec, responseSpec);
        Assert.assertNotNull(center);
        Assert.assertTrue(center.getId() == centerWithStaffId);
        Assert.assertTrue(center.getStaffId() == staffId);
        Assert.assertTrue(center.isActive() == true);
    }

    @Test
    public void testAssignStaffToCenter() {
        Integer staffId = StaffHelper.createStaff(this.requestSpec, this.responseSpec);
        System.out.println("--------------creating first staff with id-------------" + staffId);
        Assert.assertNotNull(staffId);

        Integer groupID = CenterHelper.createCenter(this.requestSpec, this.responseSpec);
        CenterHelper.verifyCenterCreatedOnServer(this.requestSpec, this.responseSpec, groupID);

        HashMap assignStaffToCenterResponseMap = (HashMap) CenterHelper.assignStaff(this.requestSpec, this.responseSpec, groupID.toString(),
                staffId.longValue());
        assertEquals("Verify assigned staff id is the same as id sent", assignStaffToCenterResponseMap.get("staffId"), staffId);

        CenterDomain center = CenterHelper.retrieveByID(groupID, requestSpec, responseSpec);
        Assert.assertNotNull(center);
        Assert.assertTrue(center.getId() == groupID);
        Assert.assertTrue(center.getStaffId() == staffId);

    }

    @Test
    public void testUnassignStaffToCenter() {
        Integer staffId = StaffHelper.createStaff(this.requestSpec, this.responseSpec);
        System.out.println("--------------creating first staff with id-------------" + staffId);
        Assert.assertNotNull(staffId);

        Integer groupID = CenterHelper.createCenter(this.requestSpec, this.responseSpec);
        CenterHelper.verifyCenterCreatedOnServer(this.requestSpec, this.responseSpec, groupID);
        
        HashMap assignStaffToCenterResponseMap = (HashMap) CenterHelper.assignStaff(this.requestSpec, this.responseSpec, groupID.toString(),
                staffId.longValue());
        assertEquals("Verify assigned staff id is the same as id sent", assignStaffToCenterResponseMap.get("staffId"), staffId);
        CenterDomain centerWithStaffAssigned = CenterHelper.retrieveByID(groupID, requestSpec, responseSpec);
        Assert.assertNotNull(centerWithStaffAssigned);
        Assert.assertTrue(centerWithStaffAssigned.getId() == groupID);
        Assert.assertTrue(centerWithStaffAssigned.getStaffId() == staffId);
        
        HashMap unassignStaffToCenterResponseMap = (HashMap) CenterHelper.unassignStaff(this.requestSpec, this.responseSpec, groupID.toString(),
                staffId.longValue());
        assertEquals("Verify staffId is null after unassigning ", unassignStaffToCenterResponseMap.get("staffId"), null);
        CenterDomain centerWithStaffUnssigned = CenterHelper.retrieveByID(groupID, requestSpec, responseSpec);
        Assert.assertNotNull(centerWithStaffUnssigned);
        Assert.assertTrue(centerWithStaffUnssigned.getId() == groupID);
        Assert.assertTrue(centerWithStaffUnssigned.getStaffId() == null);
        
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testBulkUndoRepaymentTxnForCenter() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        int officeId = new OfficeHelper(requestSpec, responseSpec).createOffice("04 Mar 2011");
        String name = "TestFullCreation" + new Timestamp(new java.util.Date().getTime());
        String externalId = Utils.randomStringGenerator("ID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        int staffId = StaffHelper.createStaff(requestSpec, responseSpec);
        int[] groupMembers = generateGroupMembers(3, officeId);
        int resourceId = CenterHelper.createCenter(name, officeId, externalId, staffId, groupMembers, requestSpec, responseSpec);

        String newName = "TestCenterUpdateNew" + new Timestamp(new java.util.Date().getTime());
        String newExternalId = Utils.randomStringGenerator("newID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        int newStaffId = StaffHelper.createStaff(requestSpec, responseSpec);
        int[] associateGroupMembers = generateGroupMembers(2, officeId);

        int[] associateResponse = CenterHelper.associateGroups(resourceId, associateGroupMembers, requestSpec, responseSpec);
        Arrays.sort(associateResponse);
        Arrays.sort(associateGroupMembers);
        Assert.assertArrayEquals(associateResponse, associateGroupMembers);

        final Integer clientID = ClientHelper.createClient(requestSpec, responseSpec, "04 Mar 2011", Integer.toString(officeId));
        Integer groupID = GroupHelper.associateClient(this.requestSpec, this.responseSpec, Integer.toString(associateResponse[0]), clientID.toString());

        final Integer loanProductID = createLoanProduct();
        final Integer loanID = applyForLoanApplication(groupID, loanProductID);
        
        Assert.assertNotNull(loanID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("04 Mar 2011", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        // DISBURSE first Tranche
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("04 Mar 2011", loanID);
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
        
        System.out.println("-------------Make repayment 1-----------");
        HashMap<String, Object> lOneRepayOne = this.loanTransactionHelper.makeRepayment("04 Mar 2011", Float.valueOf("500"), loanID);
        System.out.println("lOneRepayOne......."+lOneRepayOne);
        System.out.println("-------------Make repayment 2-----------");
        HashMap<String, Object> lOneRepayTwo = this.loanTransactionHelper.makeRepayment("04 Mar 2011", Float.valueOf("600"), loanID);
        System.out.println("lOneRepayTwo......."+lOneRepayTwo);
        
        //Second Loan
        final Integer loanProductID2 = createLoanProduct();
        final Integer loanID2 = applyForLoanApplication(groupID, loanProductID2);
        
        Assert.assertNotNull(loanID2);

        HashMap loanStatusHashMap2 = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID2);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap2);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap2 = this.loanTransactionHelper.approveLoan("04 Mar 2011", loanID2);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap2);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap2);

        // DISBURSE first Tranche
        loanStatusHashMap2 = this.loanTransactionHelper.disburseLoan("04 Mar 2011", loanID2);
        System.out.println("DISBURSE " + loanStatusHashMap2);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap2);
        
        System.out.println("-------------Make repayment 1-----------");
        HashMap<String, Object> lTwoRepayOne = this.loanTransactionHelper.waiveInterest("04 Mar 2011", "222", loanID2);
        System.out.println("lTwoRepayOne......."+lTwoRepayOne);
        System.out.println("-------------Make repayment 2-----------");
        HashMap<String, Object> lTwoRepayTwo = this.loanTransactionHelper.makeRepayment("04 Mar 2011", Float.valueOf("400"), loanID2);
        System.out.println("lTwoRepayTwo......."+lTwoRepayTwo);
        
        ArrayList<HashMap<String, Object>> txnData = (ArrayList<HashMap<String, Object>>) CenterHelper
                        .retrieveRepaymentTransactionsByCenterID(resourceId, "2011-03-04", requestSpec, responseSpec);
        
        Object obj =CenterHelper.MakeBulkUndoRepaymentTransactions(getJsonForUndoRepaymentTrasactions(txnData), requestSpec, responseSpec);
        
        List<HashMap<String, Object>> loanOne = (List<HashMap<String, Object>>) this.loanTransactionHelper
                        .getLoanDetail(requestSpec, responseSpec, loanID, "transactions");
        boolean reversedTransactionOfLoanOne = false;
        for (HashMap<String, Object> hashMap : loanOne) {
                        if(hashMap.get("id").toString().equalsIgnoreCase(lOneRepayTwo.get("resourceId").toString())){
                                reversedTransactionOfLoanOne = (boolean)hashMap.get("manuallyReversed");
                        }
                }
        
        List<HashMap<String, Object>> loanTwo = (List<HashMap<String, Object>>) this.loanTransactionHelper
                        .getLoanDetail(requestSpec, responseSpec, loanID2, "transactions");
        boolean reversedTransactionOfLoanTwo = false;
        for (HashMap<String, Object> hashMap : loanTwo) {
                        if(hashMap.get("id").toString().equalsIgnoreCase(lTwoRepayTwo.get("resourceId").toString())){
                                reversedTransactionOfLoanTwo = (boolean)hashMap.get("manuallyReversed");
                        }
                }
        Assert.assertTrue(reversedTransactionOfLoanOne);
        Assert.assertTrue(reversedTransactionOfLoanTwo);
        
    }
    
    private String getJsonForUndoRepaymentTrasactions(final ArrayList<HashMap<String, Object>> txnData){
        
        List<HashMap> txnDataJson = new ArrayList();
        for(int i = 0; i < txnData.size(); i++){
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("requestId", Integer.toString(i));
                        String relativeUrl = "loans/"+txnData.get(i).get("id")+"/transactions/"+((HashMap)((List)txnData.get(i)
                                        .get("transactions")).get(0)).get("id")+"?command=undo";
                        map.put("relativeUrl", relativeUrl);
                        map.put("method", "POST");
                        String body = "{\"dateFormat\":\"dd MMMM yyyy\",\"locale\":\"en\",\"transactionAmount\":0,\"transactionDate\":\"04 March 2011\"}";
                        map.put("body", body);
                        txnDataJson.add(map);
                }
        System.out.println("........................."+new Gson().toJson(txnDataJson));
        return new Gson().toJson(txnDataJson);
    }
    
    private Integer createLoanProduct() {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("12,000.00") //
                .withNumberOfRepayments("4") //
                .withRepaymentAfterEvery("1") //
                .withRepaymentTypeAsMonth() //
                .withinterestRatePerPeriod("1") //
                .withInterestRateFrequencyTypeAsMonths() //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .build(null);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }
    
    private Integer applyForLoanApplication(final Integer groupID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("12,000.00") //
                .withLoanTermFrequency("4") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("04 Mar 2011") //
                .withSubmittedOnDate("04 Mar 2011") //
                .withLoanType("group").build(groupID.toString(), loanProductID.toString(), null);
        System.out.println(loanApplicationJSON);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }
    
}
