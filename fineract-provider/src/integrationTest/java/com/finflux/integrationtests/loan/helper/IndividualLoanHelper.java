package com.finflux.integrationtests.loan.helper;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class IndividualLoanHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    private LoanTransactionHelper loanTransactionHelper;

    public IndividualLoanHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    }

    public Integer getLoanId(final Integer clientID, final Integer loanProductID) {
        final String loanApplicationJSON = getLoanApplicationJSON(clientID, loanProductID);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    private String getLoanApplicationJSON(final Integer clientID, final Integer loanProductID) {
        return new LoanApplicationTestBuilder().withPrincipal("8000").withLoanTermFrequency("5").withLoanTermFrequencyAsMonths()
                .withNumberOfRepayments("5").withRepaymentEveryAfter("1").withRepaymentFrequencyTypeAsMonths()
                .withInterestRatePerPeriod("2").withExpectedDisbursementDate("04 April 2012").withSubmittedOnDate("02 April 2012")
                .build(clientID.toString(), loanProductID.toString(), null);
    }

    @SuppressWarnings("rawtypes")
    public void approveLoan(final Integer loanID) {
        final String approveDate = "02 April 2012";
        HashMap loanStatusHashMap = this.loanTransactionHelper.approveLoan(approveDate, loanID);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
    }

    @SuppressWarnings("rawtypes")
    public void disburseLoan(final Integer loanID) {
        final String expectedDisbursementDate = "04 April 2012";
        HashMap loanStatusHashMap = this.loanTransactionHelper.disburseLoan(expectedDisbursementDate, loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
    }

    @SuppressWarnings("unchecked")
    public List<HashMap<String, Object>> createLoanAccount(final Integer clientID, final Integer loanProductID) {
        final String loanApplicationJSON = getLoanApplicationJSON(clientID, loanProductID);
        return (List<HashMap<String, Object>>) this.loanTransactionHelper.createLoanAccount(loanApplicationJSON,
                CommonConstants.RESPONSE_ERROR);
    }

    @SuppressWarnings("rawtypes")
    public HashMap getLoanTemplate(final String URL) {
        return (HashMap) this.loanTransactionHelper.getLoanTemplate(URL);
    }
}