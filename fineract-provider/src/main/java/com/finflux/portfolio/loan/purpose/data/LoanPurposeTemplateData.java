package com.finflux.portfolio.loan.purpose.data;

import java.util.Collection;

public class LoanPurposeTemplateData {

    private final Collection<LoanPurposeGroupData> loanPurposeGroupDatas;

    private LoanPurposeTemplateData(final Collection<LoanPurposeGroupData> loanPurposeGroupDatas) {
        this.loanPurposeGroupDatas = loanPurposeGroupDatas;
    }

    public static LoanPurposeTemplateData template(final Collection<LoanPurposeGroupData> loanPurposeGroupDatas) {
        return new LoanPurposeTemplateData(loanPurposeGroupDatas);
    }
}