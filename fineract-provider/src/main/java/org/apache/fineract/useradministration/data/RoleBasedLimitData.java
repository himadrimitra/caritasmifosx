package org.apache.fineract.useradministration.data;

import java.math.BigDecimal;

import org.apache.fineract.organisation.monetary.data.CurrencyData;

public class RoleBasedLimitData {

    private final Long roleBasedLimitId;
    private final String currencyCode;
    private final CurrencyData currencyData;
    private BigDecimal maxLoanApprovalAmount;

    public RoleBasedLimitData(Long roleBasedLimitId, String currencyCode, CurrencyData currencyData, BigDecimal maxLoanApprovalAmount) {
        this.currencyData = currencyData;
        this.maxLoanApprovalAmount = maxLoanApprovalAmount;
        this.currencyCode = currencyCode;
        this.roleBasedLimitId = roleBasedLimitId;
    }

    public BigDecimal getLoanApproval() {
        return this.maxLoanApprovalAmount;
    }

    public CurrencyData getCurrencyData() {
        return this.currencyData;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Long getRoleBasedLimitId() {
        return roleBasedLimitId;
    }

}
