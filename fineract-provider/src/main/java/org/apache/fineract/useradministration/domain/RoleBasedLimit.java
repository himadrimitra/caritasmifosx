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
package org.apache.fineract.useradministration.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.useradministration.data.RoleBasedLimitData;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_role_based_limit")
public class RoleBasedLimit extends AbstractPersistable<Long> {

    @Column(name = "max_loan_approval_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal maxLoanApprovalAmount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_id")
    private ApplicationCurrency applicationCurrency;

    protected RoleBasedLimit() {
        //
    }

    public RoleBasedLimit(Role role, ApplicationCurrency applicationCurrency, BigDecimal maxLoanApprovalAmount) {
        super();
        this.role = role;
        this.applicationCurrency = applicationCurrency;
        this.maxLoanApprovalAmount = maxLoanApprovalAmount;
    }

    public BigDecimal getMaxLoanApprovalAmount() {
        return this.maxLoanApprovalAmount;
    }

    public void setMaxLoanApprovalAmount(BigDecimal maxLoanApprovalAmount) {
        this.maxLoanApprovalAmount = maxLoanApprovalAmount;
    }

    public RoleBasedLimitData toData() {
        return new RoleBasedLimitData(this.getId(), this.applicationCurrency.getCode(), this.applicationCurrency.toData(),
                this.maxLoanApprovalAmount);
    }

    
    public ApplicationCurrency getApplicationCurrency() {
        return this.applicationCurrency;
    }


}
