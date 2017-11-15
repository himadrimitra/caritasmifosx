package org.apache.fineract.portfolio.charge.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.charge.data.ChargeInvestmentData;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_charge_investment_details")
public class ChargeInvestmentDetails extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "charge_id")
    private Charge charge;

    @Column(name = "apply_to_linked_savings_account", nullable = false)
    private boolean applyToLinkedSavingsAccount;

    @Column(name = "not_apply_to_investment_account", nullable = false)
    private boolean doNotApplyToInvestmentAccount;

    protected ChargeInvestmentDetails() {

    }

    private ChargeInvestmentDetails(final boolean applyToLinkedSavingsAccount, final boolean doNotApplyToInvestmentAccount) {
        this.applyToLinkedSavingsAccount = applyToLinkedSavingsAccount;
        this.doNotApplyToInvestmentAccount = doNotApplyToInvestmentAccount;
    }

    public static ChargeInvestmentDetails create(final boolean applyToLinkedSavingsAccount, final boolean doNotApplyToInvestmentAccount) {
        return new ChargeInvestmentDetails(applyToLinkedSavingsAccount, doNotApplyToInvestmentAccount);
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
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

    public ChargeInvestmentData toData() {
        return new ChargeInvestmentData(getId(), applyToLinkedSavingsAccount, doNotApplyToInvestmentAccount);
    }

}
