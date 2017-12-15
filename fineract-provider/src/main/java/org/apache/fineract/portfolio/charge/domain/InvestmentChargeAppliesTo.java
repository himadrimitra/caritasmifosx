package org.apache.fineract.portfolio.charge.domain;

public enum InvestmentChargeAppliesTo {

    INVALID(0, "investmentChargeAppliesTo.invalid"), //
    INVESTMENT_ACCOUNT(1, "investmentChargeAppliesTo.investmentAccount"), //
    LINKED_SAVINGS(2, "investmentChargeAppliesTo.linkedSavings"), //
    INVESTMENT_ACCOUNT_AND_LINKED_SAVINGS(3, "investmentChargeAppliesTo.investmentAccountAndlinkedSavings");

    private final Integer value;
    private final String code;

    private InvestmentChargeAppliesTo(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static InvestmentChargeAppliesTo fromInt(final Integer chargeAppliesTo) {
    	InvestmentChargeAppliesTo chargeAppliesToType = InvestmentChargeAppliesTo.INVALID;

        if (chargeAppliesTo != null) {
            switch (chargeAppliesTo) {
                case 1:
                    chargeAppliesToType = INVESTMENT_ACCOUNT;
                break;
                case 2:
                    chargeAppliesToType = LINKED_SAVINGS;
                break;
                case 3:
                    chargeAppliesToType = INVESTMENT_ACCOUNT_AND_LINKED_SAVINGS;
                break;
                default:
                    chargeAppliesToType = INVALID;
                break;
            }
        }

        return chargeAppliesToType;
    }

    public boolean isInvestmentAccountCharge() {
        return this.value.equals(InvestmentChargeAppliesTo.INVESTMENT_ACCOUNT.getValue()) || this.value.equals(InvestmentChargeAppliesTo.INVESTMENT_ACCOUNT_AND_LINKED_SAVINGS.getValue());
    }

    public boolean isLinkedSavingsCharge() {
    	return this.value.equals(InvestmentChargeAppliesTo.LINKED_SAVINGS.getValue()) || this.value.equals(InvestmentChargeAppliesTo.INVESTMENT_ACCOUNT_AND_LINKED_SAVINGS.getValue());
    }

    public static Object[] validValues() {
        return new Object[] { InvestmentChargeAppliesTo.INVALID.getValue(),InvestmentChargeAppliesTo.INVESTMENT_ACCOUNT.getValue(), InvestmentChargeAppliesTo.LINKED_SAVINGS.getValue(), InvestmentChargeAppliesTo.INVESTMENT_ACCOUNT_AND_LINKED_SAVINGS.getValue() };
    }
}