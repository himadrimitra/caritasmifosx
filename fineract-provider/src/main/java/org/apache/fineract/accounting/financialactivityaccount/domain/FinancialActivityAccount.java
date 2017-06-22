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
package org.apache.fineract.accounting.financialactivityaccount.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryDetail;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE , region = "FinancialActivityAccount")
@Table(name = "acc_gl_financial_activity_account")
public class FinancialActivityAccount extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gl_account_id")
    private GLAccount glAccount;

    @Column(name = "financial_activity_type", nullable = false)
    private Integer financialActivityType;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "financial_activity_account_id", referencedColumnName = "id", nullable = false)
    private List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMapping = new ArrayList<>();

    public static FinancialActivityAccount createNew(final GLAccount glAccount, final Integer financialAccountType) {
        return new FinancialActivityAccount(glAccount, financialAccountType);
    }

    protected FinancialActivityAccount() {
        //
    }

    private FinancialActivityAccount(final GLAccount glAccount, final int financialAccountType) {
        this.glAccount = glAccount;
        this.financialActivityType = financialAccountType;
    }

    public GLAccount getGlAccount() {
        return this.glAccount;
    }

    public Integer getFinancialActivityType() {
        return this.financialActivityType;
    }

    public void updateGlAccount(final GLAccount glAccount) {
        this.glAccount = glAccount;
    }

    public void updateFinancialActivityType(final Integer financialActivityType) {
        this.financialActivityType = financialActivityType;
    }

    public void addAllFinancialActivityAccountPaymentTypeMapping(
            final List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMapping) {
        this.financialActivityAccountPaymentTypeMapping.addAll(financialActivityAccountPaymentTypeMapping);
    }
    
    public List<FinancialActivityAccountPaymentTypeMapping> getFinancialActivityAccountPaymentTypeMapping(){
        return this.financialActivityAccountPaymentTypeMapping;
    }
    
    public void updateFinancialActivityAccountPaymentTypeMapping(
            List<FinancialActivityAccountPaymentTypeMapping> financialActivityAccountPaymentTypeMappingList) {
        this.financialActivityAccountPaymentTypeMapping = financialActivityAccountPaymentTypeMappingList;
    }

}