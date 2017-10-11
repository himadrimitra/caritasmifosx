/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.exception.PlatformServiceUnavailableException;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.guarantor.domain.GuarantorFundingTransaction;
import org.apache.fineract.portfolio.savings.DepositAccountOnHoldTransactionType;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_deposit_account_on_hold_transaction")
public class DepositAccountOnHoldTransaction extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "savings_account_id", nullable = true)
    private SavingsAccount savingsAccount;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_type_enum", nullable = false)
    private Integer transactionType;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "depositAccountOnHoldTransaction", optional = true, orphanRemoval = true)
    private GuarantorFundingTransaction guarantorFundingTransaction;

    protected DepositAccountOnHoldTransaction() {}

    private DepositAccountOnHoldTransaction(final SavingsAccount savingsAccount, final BigDecimal amount,
            final DepositAccountOnHoldTransactionType transactionType, final LocalDate transactionDate, final boolean reversed) {
        this.savingsAccount = savingsAccount;
        this.amount = amount;
        this.transactionType = transactionType.getValue();
        this.transactionDate = transactionDate.toDate();
        this.createdDate = new Date();
        this.reversed = reversed;
    }

    public static DepositAccountOnHoldTransaction hold(final SavingsAccount savingsAccount, final BigDecimal amount,
            final LocalDate transactionDate) {
        final boolean reversed = false;
        return new DepositAccountOnHoldTransaction(savingsAccount, amount, DepositAccountOnHoldTransactionType.HOLD, transactionDate,
                reversed);
    }

    public static DepositAccountOnHoldTransaction release(final SavingsAccount savingsAccount, final BigDecimal amount,
            final LocalDate transactionDate) {
        final boolean reversed = false;
        return new DepositAccountOnHoldTransaction(savingsAccount, amount, DepositAccountOnHoldTransactionType.RELEASE, transactionDate,
                reversed);
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Money getAmountMoney(final MonetaryCurrency currency) {
        return Money.of(currency, this.amount);
    }

    public void reverseTransaction() {
        this.reversed = true;
        if (getTransactionType().isHold()) {
            this.savingsAccount.releaseFunds(this.amount);
        } else {
            if (this.amount.compareTo(this.savingsAccount.getSummary().getAccountBalance()
                    .subtract(this.savingsAccount.getOnHoldFunds())) > 0) { throw new PlatformServiceUnavailableException(
                            "error.msg.loan.undo.transaction.not.allowed",
                            "Loan transaction:" + this.amount + " undo transaction not allowed as account balance is insufficient",
                            this.amount); }
            this.savingsAccount.holdFunds(this.amount);
        }
    }

    public DepositAccountOnHoldTransactionType getTransactionType() {
        return DepositAccountOnHoldTransactionType.fromInt(this.transactionType);

    }

    public boolean isReversed() {
        return this.reversed;
    }

    public void setReversed(final boolean reversed) {
        this.reversed = reversed;
    }

    public void reverseTxnIfUndoDepositTxn(final BigDecimal releaseAmount) {
        this.reversed = true;
        // following if self saving account undo deposit then other guarantor
        // has to be undo release and onhond has to be increase
        // other guarantor onhod amount
        this.savingsAccount.undoOnHoldAmountIfDepositTxnUndo(releaseAmount);
    }

    // following code change if undo the saving deposit on hold transaction self
    // saving account onhold

    public void removedOnholdsFundsWithTxnAmount(final BigDecimal releaseAmount) {
        this.reversed = true;
        this.savingsAccount.removedOnholdsFundsWithTxnAmount(releaseAmount);
    }

    public SavingsAccount getSavingsAccount() {
        return this.savingsAccount;
    }

    public void setSavingsAccount(final SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    public LocalDate getTransactionDate() {
        LocalDate transactionDate = null;
        if (this.transactionDate != null) {
            transactionDate = LocalDate.fromDateFields(this.transactionDate);
        }
        return transactionDate;
    }

}
