package com.finflux.integrationtests.loan.service;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductApplicableForLoanType;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;

import com.finflux.integrationtests.client.builder.ClientTestBuilder;
import com.finflux.integrationtests.codevalue.service.CodeValueService;
import com.finflux.integrationtests.loan.helper.JLGLoanHelper;
import com.finflux.integrationtests.loanproduct.service.LoanProductTestCaseService;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class JLGLoanTestCaseService {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private JLGLoanHelper helper;
    private ClientHelper clientHelper;
    private CodeValueService codeValueService;
    private LoanProductTestCaseService loanProductTestCaseService;

    // Common for client creation values
    final String activationDate = "04 March 2011";

    // Common for loan creation values
    final String loanType = "jlg";

    // These entities should be created only once
    private Integer officeId;
    private Integer groupId;

    private Integer loanProductIdApplicableForAllCustomers;

    private Integer clientIdWithOutLegalTypeAndClientTypeAndClientClassification;
    private Integer loanProductIdApplicableForAllClients;

    private Integer clientIdWithLegalTypePerson;
    private Integer loanProductIdApplicableForLegalTypePerson;

    private Integer clientIdWithLegalTypeEntity;
    private Integer loanProductIdApplicableForLegalTypeEntity;

    private Integer clientIdWithClientType;
    private Integer loanProductIdApplicableForClientType;

    private Integer clientIdWithClientClassification;
    private Integer loanProductIdApplicableForClientClassification;

    private Integer loanProductIdApplicableForGroups;

    private String loanTempalateUrl = null;

    private List<HashMap<String, Object>> productOptions = null;

    public JLGLoanTestCaseService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec)
            throws URISyntaxException {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.helper = new JLGLoanHelper(requestSpec, responseSpec);
        this.clientHelper = new ClientHelper(requestSpec, responseSpec);
        this.codeValueService = new CodeValueService(requestSpec, responseSpec);
        this.loanProductTestCaseService = new LoanProductTestCaseService(this.requestSpec, this.responseSpec);
        initializeRequiredEntities();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initializeRequiredEntities() throws URISyntaxException {
        this.officeId = 1;

        this.groupId = GroupHelper.createGroup(this.requestSpec, this.responseSpec, true);

        this.loanProductIdApplicableForAllCustomers = this.loanProductTestCaseService.applicableForAllCustomers();

        this.loanProductIdApplicableForGroups = this.loanProductTestCaseService.applicableForGroups();

        this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification = this.clientHelper.getClientId(new ClientTestBuilder()
                .withOffice(this.officeId).withActivateOn(this.activationDate).build());
        Assert.assertNotNull(this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification);
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(),
                this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification.toString());
        this.loanProductIdApplicableForAllClients = this.loanProductTestCaseService.applicableForAllClients();

        this.clientIdWithLegalTypePerson = this.clientHelper.getClientId(new ClientTestBuilder().withOffice(this.officeId)
                .withActivateOn(this.activationDate).withLegalForm(LegalForm.PERSON.getValue()).build());
        Assert.assertNotNull(this.clientIdWithLegalTypePerson);
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(),
                this.clientIdWithLegalTypePerson.toString());
        this.loanProductIdApplicableForLegalTypePerson = this.loanProductTestCaseService
                .applicableForClientProfileTypeLegalFormTypePerson();

        this.clientIdWithLegalTypeEntity = this.clientHelper.getClientId(new ClientTestBuilder().withOffice(this.officeId)
                .withActivateOn(this.activationDate).withLegalForm(LegalForm.ENTITY.getValue()).build());
        Assert.assertNotNull(this.clientIdWithLegalTypeEntity);
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(),
                this.clientIdWithLegalTypeEntity.toString());
        this.loanProductIdApplicableForLegalTypeEntity = this.loanProductTestCaseService
                .applicableForClientProfileTypeLegalFormTypeEntity();

        String codeName = "ClientType";
        final List<Integer> clientTypeValues = this.codeValueService.retrieveOrCreateCodeValue(codeName);

        this.clientIdWithClientType = this.clientHelper.getClientId(new ClientTestBuilder().withOffice(this.officeId)
                .withActivateOn(this.activationDate).withLegalForm(LegalForm.PERSON.getValue()).withClientType(clientTypeValues.get(0))
                .build());
        Assert.assertNotNull(this.clientIdWithClientType);
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(), this.clientIdWithClientType.toString());
        this.loanProductIdApplicableForClientType = this.loanProductTestCaseService.applicableForClientsProfileTypeClientType();

        codeName = "ClientClassification";
        final List<Integer> clientClassificationValues = this.codeValueService.retrieveOrCreateCodeValue(codeName);

        this.clientIdWithClientClassification = this.clientHelper.getClientId(new ClientTestBuilder().withOffice(this.officeId)
                .withActivateOn(this.activationDate).withLegalForm(LegalForm.PERSON.getValue())
                .withClientClassification(clientClassificationValues.get(0)).build());
        Assert.assertNotNull(this.clientIdWithClientClassification);
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, this.groupId.toString(),
                this.clientIdWithClientClassification.toString());
        this.loanProductIdApplicableForClientClassification = this.loanProductTestCaseService
                .applicableForClientsProfileTypeClientClassification();

        this.loanTempalateUrl = buildLoanTempalateUrl(LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT.getValue(),
                this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification);
        final HashMap loanTemplateProductData = this.helper.getLoanTemplate(this.loanTempalateUrl);
        this.productOptions = (List<HashMap<String, Object>>) loanTemplateProductData.get("productOptions");
    }

    private void assertAllLoanProdcutListValidForJLGLoan(final Integer loanProductId) {
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

    private String buildLoanTempalateUrl(final Integer productApplicableForLoanType, final Integer clientId) throws URISyntaxException {
        URIBuilder urlParam = new URIBuilder("/fineract-provider/api/v1/loans/template");
        urlParam.addParameter("tenantIdentifier", "default");
        urlParam.addParameter("activeOnly", "true");
        urlParam.addParameter("groupId", this.groupId.toString());
        urlParam.addParameter("clientId", clientId.toString());
        urlParam.addParameter("templateType", this.loanType);
        urlParam.addParameter("productApplicableForLoanType", productApplicableForLoanType.toString());
        urlParam.addParameter("entityType", EntityType.CLIENT.getValue().toString());
        urlParam.addParameter("entityId", clientId.toString());
        return urlParam.toString();
    }

    public void runJLGLoanIntegrationTest() {
        applyForLoanApplicationUsingLoanProdcutApplicableForAllCustomers();
        applyForLoanApplicationUsingLoanProdcutApplicableForAllClients();
        applyForLoanApplicationUsingLoanProdcutApplicableForLegalFormTypePerson();
        applyForLoanApplicationUsingLoanProdcutApplicableForLegalFormTypeEntity();
        applyForLoanApplicationUsingLoanProdcutApplicableForClientType();
        applyForLoanApplicationUsingLoanProdcutApplicableForClientClassification();

        applyForLoanApplicationUsingLoanProdcutNotApplicableForAllClients();
    }

    /***** Start Positive Scenario Test Cases ****************/
    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForAllCustomers() {
        assertAllLoanProdcutListValidForJLGLoan(this.loanProductIdApplicableForAllCustomers);
        final Integer loanId = this.helper.getLoanId(this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification, this.groupId,
                this.loanProductIdApplicableForAllCustomers);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForAllClients() {
        assertAllLoanProdcutListValidForJLGLoan(this.loanProductIdApplicableForAllClients);
        final Integer loanId = this.helper.getLoanId(this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification, this.groupId,
                this.loanProductIdApplicableForAllClients);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForLegalFormTypePerson() {
        assertAllLoanProdcutListValidForJLGLoan(this.loanProductIdApplicableForLegalTypePerson);
        final Integer loanId = this.helper.getLoanId(this.clientIdWithLegalTypePerson, this.groupId,
                this.loanProductIdApplicableForLegalTypePerson);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForLegalFormTypeEntity() {
        assertAllLoanProdcutListValidForJLGLoan(this.loanProductIdApplicableForLegalTypeEntity);
        final Integer loanId = this.helper.getLoanId(this.clientIdWithLegalTypeEntity, this.groupId,
                this.loanProductIdApplicableForLegalTypeEntity);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForClientType() {
        assertAllLoanProdcutListValidForJLGLoan(this.loanProductIdApplicableForClientType);
        final Integer loanId = this.helper.getLoanId(this.clientIdWithClientType, this.groupId, this.loanProductIdApplicableForClientType);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    private Integer applyForLoanApplicationUsingLoanProdcutApplicableForClientClassification() {
        assertAllLoanProdcutListValidForJLGLoan(this.loanProductIdApplicableForClientClassification);
        final Integer loanId = this.helper.getLoanId(this.clientIdWithClientClassification, this.groupId,
                this.loanProductIdApplicableForClientClassification);
        Assert.assertNotNull(loanId);
        return loanId;
    }

    /***** End Positive Scenario Test Cases ****************/

    /***** Start Negative Scenario Test Cases ****************/
    @SuppressWarnings("cast")
    private void applyForLoanApplicationUsingLoanProdcutNotApplicableForAllClients() {
        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(403).build();
        final JLGLoanHelper jLGLoanHelper = new JLGLoanHelper(this.requestSpec, responseSpec);

        final List<HashMap<String, Object>> errorData = (List<HashMap<String, Object>>) jLGLoanHelper.createLoanAccount(
                this.clientIdWithOutLegalTypeAndClientTypeAndClientClassification, this.groupId, this.loanProductIdApplicableForGroups);

        assertEquals("error.msg.loan.product.not.belongs.to.loanType.loan",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }
    /***** End Negative Scenario Test Cases ****************/
}