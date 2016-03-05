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

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CollateralHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class CollateralTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private CollateralHelper collateralHelper;
    private LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    }
    
    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID, final String proposedAmount) {
        final String loanApplication = new LoanApplicationTestBuilder().withPrincipal(proposedAmount).withLoanTermFrequency("5")
                .withLoanTermFrequencyAsMonths().withNumberOfRepayments("5").withRepaymentEveryAfter("1")
                .withRepaymentFrequencyTypeAsMonths().withInterestRatePerPeriod("2").withExpectedDisbursementDate("04 April 2012")
                .withSubmittedOnDate("02 April 2012").build(clientID.toString(), loanProductID.toString(), null);
        return this.loanTransactionHelper.getLoanId(loanApplication);
    }

    @Test
    public void testCollateralStatus() {
        this.collateralHelper = new CollateralHelper(this.requestSpec, this.responseSpec);
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec, "01 January 2012");
        final Integer loanProductID = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().build(null));
        final Integer loanProductIdForUpdate = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().build(null));
        final String proposedAmount = "8000";
        final Integer loanID = applyForLoanApplication(clientID, loanProductID, proposedAmount);

        final Integer collateralId = CollateralHelper.createCollateral(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(collateralId);
        
        final Integer collateralIdForUpdate = CollateralHelper.createCollateral(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(collateralIdForUpdate);
        
        final Integer productCollateralMappingId = CollateralHelper.createProductCollateralMapping(this.requestSpec, this.responseSpec, String.valueOf(collateralId), String.valueOf(loanProductID));
        Assert.assertNotNull(productCollateralMappingId);
        System.out.println("product Collateral Mapping  has been created with Id: "+productCollateralMappingId);
        
        final Integer productCollateralMappingIdForUpdate = CollateralHelper.createProductCollateralMapping(this.requestSpec, this.responseSpec, String.valueOf(collateralId), String.valueOf(loanProductIdForUpdate));
        Assert.assertNotNull(productCollateralMappingIdForUpdate);
        System.out.println("product Collateral Mapping  has been created with Id: "+productCollateralMappingId);
                
        HashMap<String, Object> updatedProductCollateralMapping = CollateralHelper.updateProductCollateralMapping(requestSpec, responseSpec, String.valueOf(productCollateralMappingIdForUpdate), String.valueOf(collateralIdForUpdate), String.valueOf(loanProductIdForUpdate));
        Assert.assertTrue((updatedProductCollateralMapping.get("collateralId")+"").equals(collateralIdForUpdate+""));
        System.out.println(updatedProductCollateralMapping);
        
        HashMap<String, Object> productCollateralMapping = CollateralHelper.getProductCollateralMapping(requestSpec, responseSpec, String.valueOf(loanProductID), String.valueOf(productCollateralMappingId));
        Assert.assertNotNull(productCollateralMapping);
        System.out.println(productCollateralMapping);
        
        final Integer productCollateralMappingToBeDeleted = CollateralHelper.deleteProductCollateralMapping(requestSpec, responseSpec, loanProductID, productCollateralMappingIdForUpdate);
        Assert.assertTrue(productCollateralMappingToBeDeleted==productCollateralMappingIdForUpdate);
        System.out.println("Product Collateral Mapping with id: "+productCollateralMappingToBeDeleted+" Deleted");
        
        HashMap<String, Object> status = CollateralHelper.getCollateral(requestSpec, responseSpec, String.valueOf(collateralId));
        Assert.assertNotNull(status);
        
        final Integer qualityStandardId = CollateralHelper.createCollateralQualityStandards(this.requestSpec, this.responseSpec, String.valueOf(collateralId));
        System.out.println("qualityStandardId: "+qualityStandardId);
        Assert.assertNotNull(qualityStandardId);
        
        final Integer qualityStandardWithId = CollateralHelper.createCollateralQualityStandards(this.requestSpec, this.responseSpec, String.valueOf(collateralId));
        System.out.println("qualityStandardWithId: "+qualityStandardWithId);
        Assert.assertNotNull(qualityStandardWithId);
        
        HashMap<String, Object> qualityStandard = CollateralHelper.getCollateralQualityStandard(requestSpec, responseSpec, String.valueOf(collateralId), String.valueOf(qualityStandardWithId));
        System.out.println("get a qualityStandard: "+qualityStandard);
        Assert.assertNotNull(qualityStandard);
        
        List<HashMap<String, Object>> collateralWithQualityStandards =  (List<HashMap<String, Object>>) CollateralHelper.getCollateralWithQualityStandards(requestSpec, responseSpec, String.valueOf(collateralId));
        System.out.println(collateralWithQualityStandards);        
        Assert.assertTrue(collateralWithQualityStandards.size()>0);
        
        HashMap<String, Object> modifiedQualitystandard = this.collateralHelper.updateCollateralQualityStandard(this.requestSpec, this.responseSpec, collateralId, qualityStandardWithId);
        CollateralStatusChecker.verifyModifiedQualitystandard(modifiedQualitystandard,qualityStandardWithId);
        
        final Integer deletedQualityStandardid = this.collateralHelper.deleteQualityStandards(this.requestSpec, this.responseSpec, collateralId, qualityStandardWithId);
        Assert.assertEquals(qualityStandardWithId, deletedQualityStandardid);
        System.out.println("quality standard with id "+deletedQualityStandardid+" deleted.");
        HashMap<String, Object> collateralStatusHashMap = this.collateralHelper.updateCollateral(this.requestSpec, this.responseSpec, collateralId);
        CollateralStatusChecker.verifyCollateralUpdated(collateralStatusHashMap);
        
        final Integer pledgeId = this.collateralHelper.createPledge(this.requestSpec, this.responseSpec, clientID, loanID, collateralId, qualityStandardId, true);
        Assert.assertNotNull(pledgeId);
        
        HashMap<String, Object> getPledge = this.collateralHelper.getPledge(this.requestSpec, this.responseSpec, pledgeId);
        Assert.assertNotNull(getPledge);
        System.out.println(getPledge);
        
        int randomSealNumber = (int) (100000+Math.random()*1000000);
        
        HashMap<String, Object> modifiedPledge = this.collateralHelper.updatePledge(this.requestSpec, this.responseSpec, pledgeId, randomSealNumber);
        CollateralStatusChecker.verifyPledgeUpdated(modifiedPledge, randomSealNumber);
        
        final Integer pledgeIdForClose = this.collateralHelper.createPledge(this.requestSpec, this.responseSpec, clientID, loanID, collateralId, qualityStandardId, false);
        Assert.assertNotNull(pledgeIdForClose);
        
        final Integer closePledge = this.collateralHelper.closePledge(this.requestSpec, this.responseSpec, pledgeIdForClose);
        Assert.assertNotNull(closePledge);
        System.out.println("Pledge with ID "+closePledge+" has been closed.");
        
        final Integer pledgeIdForDelete = this.collateralHelper.createPledge(this.requestSpec, this.responseSpec, clientID, loanID, collateralId, qualityStandardId, false);
        Assert.assertNotNull(pledgeIdForDelete);
        

        HashMap<String, Object> getPledgeWithCollateral = this.collateralHelper.getPledgeWithCollateral(this.requestSpec, this.responseSpec, pledgeId);
        System.out.println(getPledgeWithCollateral);
        
        final Integer deletedPledge = this.collateralHelper.deletePledge(this.requestSpec, this.responseSpec, pledgeIdForDelete);
        Assert.assertNotNull(deletedPledge);
        System.out.println("Pledge with ID "+deletedPledge+" has been deleted.");
        
        
    }

}
