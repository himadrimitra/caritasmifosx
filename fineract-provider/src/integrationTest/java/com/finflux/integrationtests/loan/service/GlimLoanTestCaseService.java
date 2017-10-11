package com.finflux.integrationtests.loan.service;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductApplicableForLoanType;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;

import com.finflux.integrationtests.loan.helper.GlimLoanHelper;
import com.finflux.integrationtests.loanproduct.service.LoanProductTestCaseService;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class GlimLoanTestCaseService {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private GlimLoanHelper helper;
    private LoanProductTestCaseService loanProductTestCaseService;

    // Common for loan creation values
    final String loanType = "glim";

    // These entities should be created only once
    private Integer groupId;

    private Integer loanProductIdApplicableForAllCustomers;

    private Integer loanProductIdApplicableForAllClients;

    private Integer loanProductIdApplicableForGroups;

    private String loanTempalateUrl = null;

    @SuppressWarnings("rawtypes")
    private List<HashMap> clientMembers = null;

    private List<HashMap<String, Object>> productOptions = null;

    public GlimLoanTestCaseService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec)
            throws URISyntaxException {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.helper = new GlimLoanHelper(requestSpec, responseSpec);
        this.loanProductTestCaseService = new LoanProductTestCaseService(this.requestSpec, this.responseSpec);
        initializeRequiredEntities();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initializeRequiredEntities() throws URISyntaxException {
        this.groupId = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);
        final Integer clientIdOne = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        final Integer clientIdTwo = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(), clientIdOne.toString());
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(), clientIdTwo.toString());
        this.clientMembers = getClientMemberShare(clientIdOne.toString(), "4000.00", clientIdTwo.toString(), "8000.00");

        this.loanProductIdApplicableForAllCustomers = this.loanProductTestCaseService.applicableForAllCustomers();

        this.loanProductIdApplicableForGroups = this.loanProductTestCaseService.applicableForGroups();

        this.loanProductIdApplicableForAllClients = this.loanProductTestCaseService.applicableForAllClients();

        this.loanTempalateUrl = buildLoanTempalateUrl(LoanProductApplicableForLoanType.GROUP.getValue(), this.groupId);
        final HashMap loanTemplateProductData = this.helper.getLoanTemplate(this.loanTempalateUrl);
        this.productOptions = (List<HashMap<String, Object>>) loanTemplateProductData.get("productOptions");

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<HashMap> getClientMemberShare(String clientId1, String client1ShareAmount, String clientId2, String client2ShareAmount) {
        HashMap client1Map = new HashMap<>();
        client1Map.put("id", clientId1);
        client1Map.put("transactionAmount", client1ShareAmount);
        client1Map.put("isClientSelected", true);
        HashMap client2Map = new HashMap<>();
        client2Map.put("id", clientId2);
        client2Map.put("transactionAmount", client2ShareAmount);
        client2Map.put("isClientSelected", true);
        List<HashMap> clientMembers = new ArrayList<>();
        clientMembers.add(client1Map);
        clientMembers.add(client2Map);
        return clientMembers;
    }

    private String buildLoanTempalateUrl(final Integer productApplicableForLoanType, final Integer groupId) throws URISyntaxException {
        URIBuilder urlParam = new URIBuilder("/fineract-provider/api/v1/loans/template");
        urlParam.addParameter("tenantIdentifier", "default");
        urlParam.addParameter("activeOnly", "true");
        urlParam.addParameter("groupId", groupId.toString());
        urlParam.addParameter("templateType", this.loanType);
        urlParam.addParameter("productApplicableForLoanType", productApplicableForLoanType.toString());
        urlParam.addParameter("entityType", EntityType.GROUP.getValue().toString());
        urlParam.addParameter("entityId", groupId.toString());
        return urlParam.toString();
    }

    public void runGlimLoanIntegrationTest() {
        applyForLoanApplicationUsingLoanProdcutApplicableForAllCustomers();
        applyForLoanApplicationUsingLoanProdcutApplicableForGroups();

        applyForLoanApplicationUsingLoanProdcutNotApplicableForGroups();
    }

    /***** Start Positive Scenario Test Cases ****************/
    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForAllCustomers() {
        assertAllLoanProdcutListValidForGlimLoan(this.loanProductIdApplicableForAllCustomers);
        final Integer loanId = this.helper.getLoanId(this.groupId, this.loanProductIdApplicableForAllCustomers, this.clientMembers);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForGroups() {
        final Integer loanId = this.helper.getLoanId(this.groupId, this.loanProductIdApplicableForGroups, this.clientMembers);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    /***** End Positive Scenario Test Cases ****************/

    /***** Start Negative Scenario Test Cases ****************/
    @SuppressWarnings("cast")
    private void applyForLoanApplicationUsingLoanProdcutNotApplicableForGroups() {
        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(403).build();
        final GlimLoanHelper glimLoanHelper = new GlimLoanHelper(this.requestSpec, responseSpec);
        final List<HashMap<String, Object>> errorData = (List<HashMap<String, Object>>) glimLoanHelper.createLoanAccount(this.groupId,
                this.loanProductIdApplicableForAllClients, this.clientMembers);
        assertEquals("error.msg.loan.product.not.belongs.to.loanType.loan",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    /***** End Negative Scenario Test Cases ****************/

    private void assertAllLoanProdcutListValidForGlimLoan(final Integer loanProductId) {
        boolean isValidProduct = false;
        for (final HashMap<String, Object> product : this.productOptions) {
            if (loanProductId.equals((Integer.parseInt(product.get("id").toString())))) {
                isValidProduct = true;
                break;
            }
        }
        if (!isValidProduct) {
            Assert.assertEquals("Loan prodcuts not applicable for " + this.loanType + " loan " + loanProductId, false, isValidProduct);
        }
    }
}
