package com.finflux.integrationtests.loan.helper;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class JLGLoanHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    private LoanTransactionHelper loanTransactionHelper;

    public JLGLoanHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    }

    public Integer getLoanId(final Integer clientID, final Integer groupID, final Integer loanProductID) {
        final String loanApplicationJSON = getLoanApplicationJSON(clientID, groupID, loanProductID);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    private String getLoanApplicationJSON(final Integer clientID, final Integer groupID, final Integer loanProductID) {
        return new LoanApplicationTestBuilder() //
                .withPrincipal("8000") //
                .withLoanTermFrequency("4") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("07 October 2011") //
                .withSubmittedOnDate("07 October 2011") //
                .withLoanType("jlg").build(clientID.toString(), groupID.toString(), loanProductID.toString(), null);
    }

    @SuppressWarnings("unchecked")
    public List<HashMap<String, Object>> createLoanAccount(final Integer clientID, final Integer groupID, final Integer loanProductID) {
        final String loanApplicationJSON = getLoanApplicationJSON(clientID, groupID, loanProductID);
        return (List<HashMap<String, Object>>) this.loanTransactionHelper.createLoanAccount(loanApplicationJSON,
                CommonConstants.RESPONSE_ERROR);
    }

    @SuppressWarnings("rawtypes")
    public HashMap getLoanTemplate(final String URL) {
        return (HashMap) this.loanTransactionHelper.getLoanTemplate(URL);
    }
}
