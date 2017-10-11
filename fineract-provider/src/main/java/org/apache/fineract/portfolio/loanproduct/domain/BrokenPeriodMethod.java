package org.apache.fineract.portfolio.loanproduct.domain;

public enum BrokenPeriodMethod {
    DISTRIBUTE_EQUALLY(0, "brokenPeriodMethod.distribute.equally"), ADJUST_IN_FIRST_EMI(1, "brokenPeriodMethod.adjust.in.first.emi"), POST_INTEREST(
            2, "brokenPeriodMethod.post.interest");

    private final Integer value;
    private final String code;

    private BrokenPeriodMethod(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static BrokenPeriodMethod fromInt(final Integer selectedMethod) {

        BrokenPeriodMethod repaymentMethod = BrokenPeriodMethod.DISTRIBUTE_EQUALLY;
        switch (selectedMethod) {
            case 1:
                repaymentMethod = BrokenPeriodMethod.ADJUST_IN_FIRST_EMI;
            break;
            case 2:
                repaymentMethod = BrokenPeriodMethod.POST_INTEREST;
            break;
            default:
                repaymentMethod = BrokenPeriodMethod.DISTRIBUTE_EQUALLY;
            break;
        }
        return repaymentMethod;
    }

    public boolean isAdjustmentInFirstEMI() {
        return this.value.equals(BrokenPeriodMethod.ADJUST_IN_FIRST_EMI.getValue());
    }

    public boolean isDistributeEqually() {
        return this.value.equals(BrokenPeriodMethod.DISTRIBUTE_EQUALLY.getValue());
    }
    
    public boolean isPostInterest() {
        return this.value.equals(BrokenPeriodMethod.POST_INTEREST.getValue());
    }
}
