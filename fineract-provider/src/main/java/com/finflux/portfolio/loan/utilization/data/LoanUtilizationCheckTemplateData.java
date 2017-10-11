package com.finflux.portfolio.loan.utilization.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;

public class LoanUtilizationCheckTemplateData {

    private final Long groupId;
    private final String groupName;
    private final Long clientId;
    private final String clientName;
    private final Long loanId;
    private final LoanStatusEnumData loanStatus;
    private final EnumOptionData loanType;
    private final Long loanPurposeId;
    private final String loanPurposeName;
    private final BigDecimal principalAmount;
    private final Collection<LoanPurposeData> loanPurposeDatas;
    private final BigDecimal totalUtilizedAmount;

    private LoanUtilizationCheckTemplateData(final Long groupId, final String groupName, final Long clientId, final String clientName,
            final Long loanId, final LoanStatusEnumData loanStatus, final EnumOptionData loanType, final Long loanPurposeId,
            final String loanPurposeName, final BigDecimal principalAmount, final Collection<LoanPurposeData> loanPurposeDatas,
            final BigDecimal totalUtilizedAmount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.loanId = loanId;
        this.loanStatus = loanStatus;
        this.loanType = loanType;
        this.loanPurposeId = loanPurposeId;
        this.loanPurposeName = loanPurposeName;
        this.principalAmount = principalAmount;
        this.loanPurposeDatas = loanPurposeDatas;
        this.totalUtilizedAmount = totalUtilizedAmount;
    }

    public static LoanUtilizationCheckTemplateData template(final Long groupId, final String groupName, final Long clientId,
            final String clientName, final Long loanId, final LoanStatusEnumData loanStatus, final EnumOptionData loanType,
            final Long loanPurposeId, final String loanPurposeName, final BigDecimal principalAmount,
            final Collection<LoanPurposeData> loanPurposeDatas, final BigDecimal totalUtilizedAmount) {
        return new LoanUtilizationCheckTemplateData(groupId, groupName, clientId, clientName, loanId, loanStatus, loanType, loanPurposeId,
                loanPurposeName, principalAmount, loanPurposeDatas, totalUtilizedAmount);
    }
}