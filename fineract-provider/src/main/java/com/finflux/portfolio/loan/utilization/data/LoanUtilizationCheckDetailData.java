package com.finflux.portfolio.loan.utilization.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

public class LoanUtilizationCheckDetailData {

    private final Long id;
    private final Long groupId;
    private final String groupName;
    private final Long clientId;
    private final String clientName;
    private final LoanStatusEnumData loanStatus;
    private final EnumOptionData loanType;
    private final BigDecimal principalAmount;
    private UtilizationDetailsData utilizationDetailsData;

    private LoanUtilizationCheckDetailData(final Long id, final Long groupId, final String groupName, final Long clientId,
            final String clientName, final LoanStatusEnumData loanStatus, final EnumOptionData loanType, final BigDecimal principalAmount) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.loanStatus = loanStatus;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
    }

    public static LoanUtilizationCheckDetailData instance(final Long id, final Long groupId, final String groupName, final Long clientId,
            final String clientName, final LoanStatusEnumData loanStatus, final EnumOptionData loanType, final BigDecimal principalAmount) {
        return new LoanUtilizationCheckDetailData(id, groupId, groupName, clientId, clientName, loanStatus, loanType, principalAmount);
    }

    public UtilizationDetailsData getUtilizationDetailsData() {
        return this.utilizationDetailsData;
    }

    public void setUtilizationDetailsData(final UtilizationDetailsData utilizationDetailsData) {
        this.utilizationDetailsData = utilizationDetailsData;
    }
}