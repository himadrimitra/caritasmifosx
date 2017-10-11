package com.finflux.integrationtests.pdc.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.PaymentTypeHelper;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.junit.Assert;

import com.finflux.integrationtests.client.builder.ClientTestBuilder;
import com.finflux.integrationtests.loan.helper.IndividualLoanHelper;
import com.finflux.integrationtests.loanproduct.helper.LoanProductHelper;
import com.finflux.integrationtests.pdc.helper.PostDatedChequeDetailHelper;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class PostDatedChequeDetailTestCaseService {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private PostDatedChequeDetailHelper helper;
    private ClientHelper clientHelper;
    private LoanProductHelper loanProductHelper;
    private IndividualLoanHelper individualLoanHelper;

    private Integer officeId;
    private final String activationDate = "04 March 2011";
    private String entityType = "loan";
    private Integer clientId;
    private Integer loanId;
    private Integer paymentTypeId;

    public PostDatedChequeDetailTestCaseService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.helper = new PostDatedChequeDetailHelper(requestSpec, responseSpec);
        this.clientHelper = new ClientHelper(requestSpec, responseSpec);
        this.loanProductHelper = new LoanProductHelper(requestSpec, responseSpec);
        this.individualLoanHelper = new IndividualLoanHelper(requestSpec, responseSpec);
        initializeRequiredEntities();
    }

    @SuppressWarnings("rawtypes")
    private void initializeRequiredEntities() {
        this.officeId = 1;

        final Integer loanProductId = this.loanProductHelper.getLoanProductId(new LoanProductTestBuilder().build(null));
        Assert.assertNotNull(loanProductId);

        this.clientId = this.clientHelper.getClientId(new ClientTestBuilder().withOffice(this.officeId).withActivateOn(this.activationDate)
                .build());
        Assert.assertNotNull(this.clientId);

        this.loanId = this.individualLoanHelper.getLoanId(this.clientId, loanProductId);
        Assert.assertNotNull(this.loanId);
        final HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanId);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        this.individualLoanHelper.approveLoan(this.loanId);

        this.individualLoanHelper.disburseLoan(this.loanId);

        final String name = PaymentTypeHelper.randomNameGenerator("P_T", 5);
        final String description = PaymentTypeHelper.randomNameGenerator("PT_Desc", 15);
        final Boolean isCashPayment = true;
        final Integer position = 1;

        this.paymentTypeId = PaymentTypeHelper.createPaymentType(requestSpec, responseSpec, name, description, isCashPayment, position);
        Assert.assertNotNull(this.paymentTypeId);

    }

    @SuppressWarnings({ "rawtypes" })
    public void runPostDatedChequeDetailTestCases() throws ParseException {
        ArrayList resourceIds = createRepaymentPDC();
        final Integer pdcId = (Integer) resourceIds.get(0);
        updateRepaymentPDC(pdcId);
        searchPDC();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ArrayList<Integer> createRepaymentPDC() throws ParseException {
        HashMap actualChanges = (HashMap) this.helper.createRepaymentPDC(this.entityType, this.loanId, this.paymentTypeId);
        Assert.assertNotNull(actualChanges);
        Assert.assertNotNull(((HashMap) (actualChanges.get("changes"))).get("resourceIds").toString());
        return (ArrayList) ((HashMap) (actualChanges.get("changes"))).get("resourceIds");
    }

    @SuppressWarnings({ "rawtypes" })
    private void updateRepaymentPDC(final Integer pdcId) {
        this.helper.updateRepaymentPDC(pdcId);
        final HashMap updatedPDCData = (HashMap) this.helper.getOnePDCDetails(pdcId);
        Assert.assertNotNull(updatedPDCData);
        Assert.assertEquals("90000", updatedPDCData.get("chequeNumber").toString());
    }

    @SuppressWarnings("unchecked")
    private void searchPDC() throws ParseException {
        final List<HashMap<String, Object>> pdcDatas = (List<HashMap<String, Object>>) this.helper.searchPDC();
        Assert.assertNotNull(pdcDatas);
        actionOnPDC("present", pdcDatas);
        actionOnPDC("clear", pdcDatas);
        actionOnPDC("undo", pdcDatas);
        actionOnPDC("bounced", pdcDatas);
    }

    @SuppressWarnings("rawtypes")
    private void actionOnPDC(final String command, final List<HashMap<String, Object>> pdcDatas) throws ParseException {
        if (pdcDatas != null && !pdcDatas.isEmpty()) {
            if (command.equalsIgnoreCase("present")) {
                final HashMap actualChanges = (HashMap) this.helper.presentPDC(command, pdcDatas);
                Assert.assertNotNull(actualChanges);
                Assert.assertNotNull(((HashMap) (actualChanges.get("changes"))).get("transactionIds").toString());
            } else if (command.equalsIgnoreCase("clear")) {
                final HashMap actualChanges = (HashMap) this.helper.clearPDC(command, pdcDatas);
                Assert.assertNotNull(actualChanges);
                Assert.assertNotNull(((HashMap) (actualChanges.get("changes"))).get("statusIds").toString());
            } else if (command.equalsIgnoreCase("undo")) {
                final HashMap actualChanges = (HashMap) this.helper.undoPDC(command, pdcDatas);
                Assert.assertNotNull(actualChanges);
                Assert.assertNotNull(((HashMap) (actualChanges.get("changes"))).get("statusIds").toString());
            } else if (command.equalsIgnoreCase("bounced")) {
                final HashMap actualChanges = (HashMap) this.helper.bouncedPDC(command, pdcDatas);
                Assert.assertNotNull(actualChanges);
                Assert.assertNotNull(((HashMap) (actualChanges.get("changes"))).get("statusIds").toString());
            }
        }
    }
}
