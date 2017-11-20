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
package org.apache.fineract.portfolio.accountdetails.data;

import java.util.Collection;

import org.apache.fineract.portfolio.collaterals.data.PledgeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeSummaryData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.data.SavingsChargesSummaryData;

/**
 * Immutable data object representing a summary of various accounts.
 */
@SuppressWarnings("unused")
public class AccountSummaryCollectionData {

    private final Collection<LoanAccountSummaryData> loanAccounts;
    private final Collection<SavingsAccountSummaryData> savingsAccounts;
    private final Collection<ShareAccountSummaryData> shareAccounts;

    private final Collection<LoanAccountSummaryData> memberLoanAccounts;
    private final Collection<SavingsAccountSummaryData> memberSavingsAccounts;
    private final Collection<PledgeData> pledges;

    private final Collection<PaymentTypeData> paymentTypeOptions;
    private final Collection<LoanChargeSummaryData> loanCharges;
    private final Collection<SavingsChargesSummaryData> savingsCharges;

    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<PledgeData> pledges,
            final Collection<ShareAccountSummaryData> shareAccounts, final Collection<PaymentTypeData> paymentTypeOptions) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.shareAccounts = defaultShareAccountsIfEmpty(shareAccounts);
        this.memberLoanAccounts = null;
        this.memberSavingsAccounts = null;
        this.pledges = pledges;
        this.paymentTypeOptions = defaultPaymentTypeIfEmpty(paymentTypeOptions);
        this.loanCharges = null;
        this.savingsCharges = null;
    }

    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<LoanAccountSummaryData> memberLoanAccounts,
            final Collection<SavingsAccountSummaryData> memberSavingsAccounts, final Collection<PledgeData> pledges,
            final Collection<PaymentTypeData> paymentTypeOptions) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.shareAccounts = null;
        this.memberLoanAccounts = defaultLoanAccountsIfEmpty(memberLoanAccounts);
        this.memberSavingsAccounts = defaultSavingsAccountsIfEmpty(memberSavingsAccounts);
        this.pledges = defaultPledgesIfEmpty(pledges);
        this.paymentTypeOptions = defaultPaymentTypeIfEmpty(paymentTypeOptions);
        this.loanCharges = null;
        this.savingsCharges = null;
    }

    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<PledgeData> pledges,
            final Collection<ShareAccountSummaryData> shareAccounts, final Collection<PaymentTypeData> paymentTypeOptions,
            final Collection<LoanChargeSummaryData> loanCharges, final Collection<SavingsChargesSummaryData> savingsCharges) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.shareAccounts = defaultShareAccountsIfEmpty(shareAccounts);
        this.memberLoanAccounts = null;
        this.memberSavingsAccounts = null;
        this.pledges = pledges;
        this.paymentTypeOptions = defaultPaymentTypeIfEmpty(paymentTypeOptions);
        this.loanCharges = loanCharges;
        this.savingsCharges = savingsCharges;
    }

    private Collection<PledgeData> defaultPledgesIfEmpty(final Collection<PledgeData> pledges) {
        Collection<PledgeData> returnPledges = null;
        if (pledges != null && !pledges.isEmpty()) {
            returnPledges = pledges;
        }
        return returnPledges;
    }

    private Collection<LoanAccountSummaryData> defaultLoanAccountsIfEmpty(final Collection<LoanAccountSummaryData> collection) {
        Collection<LoanAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

    private Collection<SavingsAccountSummaryData> defaultSavingsAccountsIfEmpty(final Collection<SavingsAccountSummaryData> collection) {
        Collection<SavingsAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

    private Collection<PaymentTypeData> defaultPaymentTypeIfEmpty(final Collection<PaymentTypeData> collection) {
        Collection<PaymentTypeData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

    private Collection<ShareAccountSummaryData> defaultShareAccountsIfEmpty(final Collection<ShareAccountSummaryData> collection) {
        Collection<ShareAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

    public Collection<LoanAccountSummaryData> getLoanAccounts(){
        return this.loanAccounts;
    }
}