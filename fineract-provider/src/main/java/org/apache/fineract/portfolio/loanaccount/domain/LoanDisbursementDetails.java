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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_disbursement_detail")
public class LoanDisbursementDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_disburse_date")
    private Date expectedDisbursementDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "disbursedon_date")
    private Date actualDisbursementDate;

    @Column(name = "principal", scale = 6, precision = 19, nullable = false)
    private BigDecimal principal;
    
    @Column(name = "principal_net_disbursed", scale = 6, precision = 19, nullable = true)
    private BigDecimal netPrincipalDisbursed;
    
    @Column(name = "discount_on_disbursal_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal discountOnDisbursalAmount;

    @Column(name = "is_active")
    private boolean active = true;

    protected LoanDisbursementDetails() {

    }

    public LoanDisbursementDetails(final Date expectedDisbursementDate, final Date actualDisbursementDate, final BigDecimal principal,
            final BigDecimal discountOnDisbursalAmount) {
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.actualDisbursementDate = actualDisbursementDate;
        this.principal = principal;
        this.netPrincipalDisbursed = principal;
        this.discountOnDisbursalAmount = discountOnDisbursalAmount;
        this.active = true;
     }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
        this.active = true;
    }

    @Override
    public boolean equals(final Object obj) {
        final LoanDisbursementDetails loanDisbursementDetails = (LoanDisbursementDetails) obj;
        if (loanDisbursementDetails.principal.equals(this.principal)
                && loanDisbursementDetails.expectedDisbursementDate.equals(this.expectedDisbursementDate)
                && isDiscountOnDisbursalEqual(loanDisbursementDetails)) 
        { return true; }
        return false;
    }

    public boolean isDiscountOnDisbursalEqual(final LoanDisbursementDetails loanDisbursementDetails) {
        return (loanDisbursementDetails.discountOnDisbursalAmount == null && this.discountOnDisbursalAmount == null)
                || (loanDisbursementDetails.discountOnDisbursalAmount != null && this.discountOnDisbursalAmount != null && loanDisbursementDetails.discountOnDisbursalAmount
                        .equals(this.discountOnDisbursalAmount));
    }

    public void copy(final LoanDisbursementDetails disbursementDetails) {
        this.principal = disbursementDetails.principal;
        this.expectedDisbursementDate = disbursementDetails.expectedDisbursementDate;
        this.actualDisbursementDate = disbursementDetails.actualDisbursementDate;
        this.discountOnDisbursalAmount = disbursementDetails.discountOnDisbursalAmount;
    }

    public Date expectedDisbursementDate() {
        return this.expectedDisbursementDate;
    }

    public LocalDate expectedDisbursementDateAsLocalDate() {
        LocalDate expectedDisburseDate = null;
        if (this.expectedDisbursementDate != null) {
            expectedDisburseDate = new LocalDate(this.expectedDisbursementDate);
        }
        return expectedDisburseDate;
    }

    public Date actualDisbursementDate() {
        return this.actualDisbursementDate;
    }
    
    public LocalDate actualDisbursementDateAsLocalDate() {
        LocalDate actualDisbursementDate = null;
        if (this.actualDisbursementDate != null) {
            actualDisbursementDate = new LocalDate(this.actualDisbursementDate);
        }
        return actualDisbursementDate;
    }

    public BigDecimal principal() {
        return this.principal;
    }

    public void updatePrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public Date getDisbursementDate() {
        Date disbursementDate = this.expectedDisbursementDate;
        if (this.actualDisbursementDate != null) {
            disbursementDate = this.actualDisbursementDate;
        }
        return disbursementDate;
    }
    
    public LocalDate getDisbursementDateAsLocalDate() {
        LocalDate disbursementDate = expectedDisbursementDateAsLocalDate();
        if (this.actualDisbursementDate != null) {
            disbursementDate = actualDisbursementDateAsLocalDate();
        }
        return disbursementDate;
    }

    public DisbursementData toData() {
        LocalDate expectedDisburseDate = expectedDisbursementDateAsLocalDate();
        LocalDate actualDisburseDate = null;
        if (this.actualDisbursementDate != null) {
            actualDisburseDate = new LocalDate(this.actualDisbursementDate);
        }
        return new DisbursementData(getId(), expectedDisburseDate, actualDisburseDate, this.principal, null, null, discountOnDisbursalAmount);
    }

    public void updateActualDisbursementDate(Date actualDisbursementDate) {
        this.actualDisbursementDate = actualDisbursementDate;
    }

    public void updateExpectedDisbursementDateAndAmount(Date expectedDisbursementDate, BigDecimal principal) {
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.principal = principal;
    }
    
    public boolean isDisbursed(){
        return this.actualDisbursementDate != null;
    }

    
    public void setNetPrincipalDisbursed(BigDecimal netPrincipalDisbursed) {
        this.netPrincipalDisbursed = netPrincipalDisbursed;
    }

    
    public BigDecimal getNetPrincipalDisbursed() {
        return this.netPrincipalDisbursed;
    }
    
    public void resetNetPrincipalDisbursed() {
         this.netPrincipalDisbursed = this.principal;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public BigDecimal getDiscountOnDisbursalAmount() {
        return this.discountOnDisbursalAmount;
    }

    public void setDiscountOnDisbursalAmount(BigDecimal discountOnDisbursalAmount) {
        this.discountOnDisbursalAmount = discountOnDisbursalAmount;
    }
    
    public BigDecimal fetchDiscountOnDisbursalAmount() {
        return this.discountOnDisbursalAmount == null ? BigDecimal.ZERO : this.discountOnDisbursalAmount;
    }
    
    public BigDecimal getAccountedPrincipal() {
        return this.principal.add(fetchDiscountOnDisbursalAmount());
    }

}