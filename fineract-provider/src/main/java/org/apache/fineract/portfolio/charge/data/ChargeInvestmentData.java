package org.apache.fineract.portfolio.charge.data;

public class ChargeInvestmentData {

    private Long id;

    private boolean applyToLinkedSavingsAccount;

    private boolean doNotApplyToInvestmentAccount;

    public ChargeInvestmentData(Long id, boolean applyToLinkedSavingsAccount, boolean doNotApplyToInvestmentAccount) {
        this.id = id;
        this.applyToLinkedSavingsAccount = applyToLinkedSavingsAccount;
        this.doNotApplyToInvestmentAccount = doNotApplyToInvestmentAccount;
    }

    public boolean isApplyToLinkedSavingsAccount() {
        return this.applyToLinkedSavingsAccount;
    }

    public void setApplyToLinkedSavingsAccount(boolean applyToLinkedSavingsAccount) {
        this.applyToLinkedSavingsAccount = applyToLinkedSavingsAccount;
    }

    public boolean isDoNotApplyToInvestmentAccount() {
        return this.doNotApplyToInvestmentAccount;
    }

    public void setDoNotApplyToInvestmentAccount(boolean doNotApplyToInvestmentAccount) {
        this.doNotApplyToInvestmentAccount = doNotApplyToInvestmentAccount;
    }

    public Long getId() {
        return this.id;
    }

}
