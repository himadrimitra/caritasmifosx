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

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_term_variations")
public class LoanTermVariations extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "term_type", nullable = false)
    private Integer termType;

    @Temporal(TemporalType.DATE)
    @Column(name = "applicable_from")
    private Date termApplicableFrom;

    @Column(name = "term_value", scale = 6, precision = 19)
    private BigDecimal termValue;

    public LoanTermVariations(final Integer termType, final Date termApplicableFrom, final BigDecimal termValue, final Loan loan) {

        this.loan = loan;
        this.termApplicableFrom = termApplicableFrom;
        this.termType = termType;
        this.termValue = termValue;
    }

    protected LoanTermVariations() {

    }

    public LoanTermVariationType getTermType() {
        return LoanTermVariationType.fromInt(this.termType);
    }

    public LoanTermVariationsData toData() {
        LocalDate termStartDate = new LocalDate(this.termApplicableFrom);
        EnumOptionData type = LoanEnumerations.loanvariationType(this.termType);
        return new LoanTermVariationsData(getId(), type, termStartDate, this.termValue);
    }

    public Date getTermApplicableFrom() {
        return this.termApplicableFrom;
    }

    public BigDecimal getTermValue() {
        return this.termValue;
    }

}
