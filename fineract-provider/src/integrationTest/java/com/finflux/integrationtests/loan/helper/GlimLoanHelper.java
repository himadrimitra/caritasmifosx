package com.finflux.integrationtests.loan.helper;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class GlimLoanHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    private LoanTransactionHelper loanTransactionHelper;

    public GlimLoanHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    }

    @SuppressWarnings("rawtypes")
    public HashMap getLoanTemplate(final String URL) {
        return (HashMap) this.loanTransactionHelper.getLoanTemplate(URL);
    }

    public Integer getLoanId(final Integer groupID, final Integer loanProductID, @SuppressWarnings("rawtypes") List<HashMap> clientMembers) {
        final String loanApplicationJSON = getLoanApplicationJSON(groupID, loanProductID, clientMembers);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    private String getLoanApplicationJSON(final Integer groupID, final Integer loanProductID,
            @SuppressWarnings("rawtypes") List<HashMap> clientMembers) {
        return new LoanApplicationTestBuilder()//
                .withPrincipal("12000") //
                .withLoanTermFrequency("4")//
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths()//
                .withInterestRatePerPeriod("2")//
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance()//
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod()//
                .withExpectedDisbursementDate("20 September 2011")//
                .withSubmittedOnDate("20 September 2011")//
                .withLoanType("glim").withClientMembers(clientMembers).build(groupID.toString(), loanProductID.toString(), null);
    }

    @SuppressWarnings("unchecked")
    public List<HashMap<String, Object>> createLoanAccount(final Integer groupID, final Integer loanProductID,
            @SuppressWarnings("rawtypes") List<HashMap> clientMembers) {
        final String loanApplicationJSON = getLoanApplicationJSON(groupID, loanProductID, clientMembers);
        return (List<HashMap<String, Object>>) this.loanTransactionHelper.createLoanAccount(loanApplicationJSON,
                CommonConstants.RESPONSE_ERROR);
    }
}