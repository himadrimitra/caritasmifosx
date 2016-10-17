package com.finflux.portfolio.loan.purpose.service;

import java.util.Collection;

import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeGroupTemplateData;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeTemplateData;

public interface LoanPurposeGroupReadPlatformService {

    LoanPurposeGroupTemplateData retrieveTemplate();

    Collection<LoanPurposeGroupData> retrieveAllLoanPurposeGroups(final Integer loanPurposeGroupTypeId,
            final Boolean isFetchLoanPurposeDatas, final Boolean isActive);

    LoanPurposeGroupData retrieveOneLoanPurposeGroup(final Long loanPurposeGroupId, final Boolean isFetchLoanPurposeDatas);

    Collection<LoanPurposeData> retrieveAllLoanPurposes(final Integer loanPurposeGroupTypeId, final Boolean isFetchLoanPurposeGroupDatas,
            final Boolean isActive);

    LoanPurposeData retrieveOneLoanPurpose(final Long loanPurposeId, final Boolean isFetchLoanPurposeGroupDatas);

    LoanPurposeTemplateData retrieveLoanPurposeTemplate(final Boolean isActive);
}