package com.finflux.integrationtests.loan.service;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductApplicableForLoanType;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;

import com.finflux.integrationtests.loan.helper.GroupLoanHelper;
import com.finflux.integrationtests.loanproduct.service.LoanProductTestCaseService;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class GroupLoanTestCaseService {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private GroupLoanHelper helper;
    private LoanProductTestCaseService loanProductTestCaseService;

    // Common for loan creation values
    final String loanType = "group";

    // These entities should be created only once
    private Integer groupId;

    private Integer loanProductIdApplicableForAllCustomers;

    private Integer loanProductIdApplicableForAllClients;

    private Integer loanProductIdApplicableForGroups;

    private String loanTempalateUrl = null;

    private List<HashMap<String, Object>> productOptions = null;

    public GroupLoanTestCaseService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec)
            throws URISyntaxException {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.helper = new GroupLoanHelper(requestSpec, responseSpec);
        this.loanProductTestCaseService = new LoanProductTestCaseService(this.requestSpec, this.responseSpec);
        initializeRequiredEntities();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initializeRequiredEntities() throws URISyntaxException {

        this.groupId = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);

        this.loanProductIdApplicableForAllCustomers = this.loanProductTestCaseService.applicableForAllCustomers();

        this.loanProductIdApplicableForGroups = this.loanProductTestCaseService.applicableForGroups();

        this.loanProductIdApplicableForAllClients = this.loanProductTestCaseService.applicableForAllClients();

        this.loanTempalateUrl = buildLoanTempalateUrl(LoanProductApplicableForLoanType.GROUP.getValue(), this.groupId);
        final HashMap loanTemplateProductData = this.helper.getLoanTemplate(this.loanTempalateUrl);
        this.productOptions = (List<HashMap<String, Object>>) loanTemplateProductData.get("productOptions");
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

    public void runGroupLoanIntegrationTestCases() {
        applyForLoanApplicationUsingLoanProdcutApplicableForAllCustomers();
        applyForLoanApplicationUsingLoanProdcutApplicableForGroups();

        applyForLoanApplicationUsingLoanProdcutNotApplicableForGroups();
    }

    /***** Start Positive Scenario Test Cases ****************/
    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForAllCustomers() {
        assertAllLoanProdcutListValidForGroupLoan(this.loanProductIdApplicableForAllCustomers);
        final Integer loanId = this.helper.getLoanId(this.groupId, this.loanProductIdApplicableForAllCustomers);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForGroups() {
        final Integer loanId = this.helper.getLoanId(this.groupId, this.loanProductIdApplicableForGroups);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    /***** End Positive Scenario Test Cases ****************/

    /***** Start Negative Scenario Test Cases ****************/

    @SuppressWarnings("cast")
    private void applyForLoanApplicationUsingLoanProdcutNotApplicableForGroups() {
        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(403).build();
        final GroupLoanHelper groupLoanHelper = new GroupLoanHelper(this.requestSpec, responseSpec);
        final List<HashMap<String, Object>> errorData = (List<HashMap<String, Object>>) groupLoanHelper.createLoanAccount(this.groupId,
                this.loanProductIdApplicableForAllClients);
        assertEquals("error.msg.loan.product.not.belongs.to.loanType.loan",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    /***** End Negative Scenario Test Cases ****************/

    private void assertAllLoanProdcutListValidForGroupLoan(final Integer loanProductId) {
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