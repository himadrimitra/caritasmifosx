package com.finflux.portfolio.loan.utilization.data;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

public class LoanUtilizationCheckDetailData {

    private final Long id;
    private final Long loanId;
    private final Long groupId;
    private final String groupName;
    private final Long clientId;
    private final String clientName;
    private final LoanStatusEnumData loanStatus;
    private final EnumOptionData loanType;
    private final BigDecimal principalAmount;
    private Set<UtilizationDetailsData> utilizationDetailsDatas;

    private LoanUtilizationCheckDetailData(final Long id, final Long loanId, final Long groupId, final String groupName,
            final Long clientId, final String clientName, final LoanStatusEnumData loanStatus, final EnumOptionData loanType,
            final BigDecimal principalAmount) {
        this.id = id;
        this.loanId = loanId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.loanStatus = loanStatus;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
    }

    public static LoanUtilizationCheckDetailData instance(final Long id, final Long loanId, final Long groupId, final String groupName,
            final Long clientId, final String clientName, final LoanStatusEnumData loanStatus, final EnumOptionData loanType,
            final BigDecimal principalAmount) {
        return new LoanUtilizationCheckDetailData(id, loanId, groupId, groupName, clientId, clientName, loanStatus, loanType,
                principalAmount);
    }

    public void addUtilizationDetailsData(final UtilizationDetailsData utilizationDetailsData) {
        if (this.utilizationDetailsDatas == null) {
            this.utilizationDetailsDatas = new LinkedHashSet<>();
        }
        this.utilizationDetailsDatas.add(utilizationDetailsData);
    }
}