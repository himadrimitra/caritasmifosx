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
package org.apache.fineract.portfolio.fund.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

/**
 * Immutable data object to represent fund data.
 */
public class FundData implements Serializable {

    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final String externalId;
    @SuppressWarnings("unused")
    private final CodeValueData fundSource;
    @SuppressWarnings("unused")
    private final CodeValueData fundCategory;
    @SuppressWarnings("unused")
    private final CodeValueData facilityType;
    @SuppressWarnings("unused")
    private final LocalDate assignmentStartDate;
    @SuppressWarnings("unused")
    private final LocalDate assignmentEndDate;
    @SuppressWarnings("unused")
    private final LocalDate sanctionedDate;
    @SuppressWarnings("unused")
    private final BigDecimal sanctionedAmount;
    @SuppressWarnings("unused")
    private final LocalDate disbursedDate;
    @SuppressWarnings("unused")
    private final BigDecimal disbursedAmount;
    @SuppressWarnings("unused")
    private final LocalDate maturityDate;
    @SuppressWarnings("unused")
    private final BigDecimal interestRate;
    @SuppressWarnings("unused")
    private final CodeValueData fundRepaymentFrequency;
    @SuppressWarnings("unused")
    private final Integer tenure;
    @SuppressWarnings("unused")
    private final EnumOptionData tenureFrequency;
    @SuppressWarnings("unused")
    private final Integer morotorium;
    @SuppressWarnings("unused")
    private final EnumOptionData morotoriumFrequency;
    @SuppressWarnings("unused")
    private final BigDecimal loanPortfolioFee;
    @SuppressWarnings("unused")
    private final BigDecimal bookDebtHypothecation;
    @SuppressWarnings("unused")
    private final BigDecimal cashCollateral;
    @SuppressWarnings("unused")
    private final String personalGurantee;
    @SuppressWarnings("unused")
    private boolean isActive;
    @SuppressWarnings("unused")
    private boolean isLoanAssigned;
    List<FundLoanPurposeData> fundLoanPurposeData;

    public FundData(final Long id, final String externalId, final CodeValueData fundSource, final CodeValueData fundCategory,
            final CodeValueData facilityType, final String name, final LocalDate assignmentStartDate, final LocalDate assignmentEndDate,
            final LocalDate sanctionedDate, final BigDecimal sanctionedAmount, final LocalDate disbursedDate,
            final BigDecimal disbursedAmount, final LocalDate maturityDate, final BigDecimal interestRate,
            final CodeValueData fundRepaymentFrequency, final Integer tenure, final EnumOptionData tenureFrequency,
            final Integer morotorium, final EnumOptionData morotoriumFrequency, final BigDecimal loanPortfolioFee,
            final BigDecimal bookDebtHypothecation, final BigDecimal cashCollateral, final String personalGurantee, final boolean isActive,
            boolean isLoanAssigned) {
        this.id = id;
        this.externalId = externalId;
        this.fundSource = fundSource;
        this.fundCategory = fundCategory;
        this.facilityType = facilityType;
        this.name = name;
        this.assignmentStartDate = assignmentStartDate;
        this.assignmentEndDate = assignmentEndDate;
        this.sanctionedDate = sanctionedDate;
        this.sanctionedAmount = sanctionedAmount;
        this.disbursedDate = disbursedDate;
        this.disbursedAmount = disbursedAmount;
        this.maturityDate = maturityDate;
        this.interestRate = interestRate;
        this.fundRepaymentFrequency = fundRepaymentFrequency;
        this.tenure = tenure;
        this.tenureFrequency = tenureFrequency;
        this.morotorium = morotorium;
        this.morotoriumFrequency = morotoriumFrequency;
        this.loanPortfolioFee = loanPortfolioFee;
        this.bookDebtHypothecation = bookDebtHypothecation;
        this.cashCollateral = cashCollateral;
        this.personalGurantee = personalGurantee;
        this.isActive = isActive;
        this.isLoanAssigned = isLoanAssigned;
    }

    public static FundData instance(final Long id, final String externalId, final CodeValueData fundSource,
            final CodeValueData fundCategory, final CodeValueData facilityType, final String fundName, final LocalDate assignmentStartDate,
            final LocalDate assignmentEndDate, final LocalDate sanctionedDate, final BigDecimal sanctionedAmount,
            final LocalDate disbursedDate, final BigDecimal disbursedAmount, final LocalDate maturityDate, final BigDecimal interestRate,
            final CodeValueData fundRepaymentFrequency, final Integer tenure, final EnumOptionData tenureFrequency,
            final Integer morotorium, final EnumOptionData morotoriumFrequency, final BigDecimal loanPortfolioFee,
            final BigDecimal bookDebtHypothecation, final BigDecimal cashCollateral, final String personalGurantee, final boolean isActive,
            boolean isLoanAssigned) {

        return new FundData(id, externalId, fundSource, fundCategory, facilityType, fundName, assignmentStartDate, assignmentEndDate,
                sanctionedDate, sanctionedAmount, disbursedDate, disbursedAmount, maturityDate, interestRate, fundRepaymentFrequency,
                tenure, tenureFrequency, morotorium, morotoriumFrequency, loanPortfolioFee, bookDebtHypothecation, cashCollateral,
                personalGurantee, isActive, isLoanAssigned);
    }

    public List<FundLoanPurposeData> getFundLoanPurposeData() {
        return this.fundLoanPurposeData;
    }

    public void setFundLoanPurposeData(List<FundLoanPurposeData> fundLoanPurposeData) {
        this.fundLoanPurposeData = fundLoanPurposeData;
    }

    public Long getId() {
        return this.id;
    }

}