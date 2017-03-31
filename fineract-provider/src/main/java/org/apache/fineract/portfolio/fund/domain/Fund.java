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
package org.apache.fineract.portfolio.fund.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "Fund")
@Table(name = "m_fund", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "fund_name_org"),
        @UniqueConstraint(columnNames = { "external_id" }, name = "fund_externalid_org") })
public class Fund extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_source", nullable = true)
    private CodeValue fundSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_category", nullable = true)
    private CodeValue fundCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_type", nullable = true)
    private CodeValue facilityType;

    @Column(name = "assignment_start_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date assignmentStartDate;

    @Column(name = "assignment_end_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date assignmentEndDate;

    @Column(name = "sanctioned_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date sanctionedDate;

    @Column(name = "sanctioned_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal sanctionedAmount;

    @Column(name = "disbursed_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date disbursedDate;

    @Column(name = "disbursed_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal disbursedAmount;

    @Column(name = "maturity_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date maturityDate;

    @Column(name = "interest_rate", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_repayment_frequency", nullable = true)
    private CodeValue fundRepaymentFrequency;

    @Column(name = "tenure", nullable = true)
    private Integer tenure;

    @Column(name = "tenure_frequency", nullable = true)
    private Integer tenureFrequency;

    @Column(name = "morotorium", nullable = true)
    private Integer morotorium;

    @Column(name = "morotorium_frequency", nullable = true)
    private Integer morotoriumFrequency;

    @Column(name = "loan_portfolio_fee", scale = 6, precision = 19, nullable = true)
    private BigDecimal loanPortfolioFee;

    @Column(name = "book_debt_hypothecation", scale = 6, precision = 19, nullable = true)
    private BigDecimal bookDebtHypothecation;

    @Column(name = "cash_collateral", scale = 6, precision = 19, nullable = true)
    private BigDecimal cashCollateral;

    @Column(name = "personal_gurantee", length = 100, nullable = true)
    private String personalGurantee;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_loan_assigned", nullable = false)
    private boolean isLoanAssigned;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fund", orphanRemoval = true)
    private List<FundLoanPurpose> fundLoanPurpose = new ArrayList<>();
    
    @Column(name = "is_manual_status_update", nullable = false)
    private boolean isManualStatusUpdate;

    public Fund(final CodeValue fundSource, final CodeValue fundCategory, final CodeValue facilityType, final String name,
            final Date assignmentStartDate, final Date assignmentEndDate, final Date sanctionedDate, final BigDecimal sanctionedAmount,
            final Date disbursedDate, final BigDecimal disbursedAmount, final Date maturityDate, final BigDecimal interestRate,
            final CodeValue fundRepaymentFrequency, final Integer tenure, final Integer tenureFrequency, final Integer morotorium,
            final Integer morotoriumFrequency, final BigDecimal loanPortfolioFee, final BigDecimal bookDebtHypothecation,
            final BigDecimal cashCollateral, final String personalGurantee, final boolean isActive, boolean isLoanAssigned,
            String externalId, List<FundLoanPurpose> fundLoanPurpose) {
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
        this.externalId = externalId;
        this.fundLoanPurpose = updateFundToFundLoanPurpose(fundLoanPurpose);
    }

    public static Fund updateFromOwn(Fund fund) {
        fund.fundSource = null;
        fund.fundCategory = null;
        fund.assignmentStartDate = null;
        fund.assignmentEndDate = null;
        fund.sanctionedDate = null;
        fund.sanctionedAmount = null;
        fund.disbursedDate = null;
        fund.disbursedAmount = null;
        fund.maturityDate = null;
        fund.interestRate = null;
        fund.fundRepaymentFrequency = null;
        fund.tenure = null;
        fund.tenureFrequency = null;
        fund.morotorium = null;
        fund.morotoriumFrequency = null;
        fund.loanPortfolioFee = null;
        fund.bookDebtHypothecation = null;
        fund.cashCollateral = null;
        fund.personalGurantee = null;
        fund.setFundLoanPurpose(new ArrayList<FundLoanPurpose>());
        return fund;
    }

    public List<FundLoanPurpose> updateFundToFundLoanPurpose(List<FundLoanPurpose> fundLoanPurpose) {
        for (FundLoanPurpose fundLoanPurposeData : fundLoanPurpose) {
            fundLoanPurposeData.setFund(this);
        }
        return fundLoanPurpose;
    }

    public static Fund instance(final CodeValue fundSource, final CodeValue fundCategory, final CodeValue facilityType, final String name,
            final Date assignmentStartDate, final Date assignmentEndDate, final Date sanctionedDate, final BigDecimal sanctionedAmount,
            final Date disbursedDate, final BigDecimal disbursedAmount, final Date maturityDate, final BigDecimal interestRate,
            final CodeValue fundRepaymentFrequency, final Integer tenure, final Integer tenureFrequency, final Integer morotorium,
            final Integer morotoriumFrequency, final BigDecimal loanPortfolioFee, final BigDecimal bookDebtHypothecation,
            final BigDecimal cashCollateral, final String personalGurantee, final boolean isActive, boolean isLoanAssigned,
            String externalId, List<FundLoanPurpose> fundLoanPurpose) {

        return new Fund(fundSource, fundCategory, facilityType, name, assignmentStartDate, assignmentEndDate, sanctionedDate,
                sanctionedAmount, disbursedDate, disbursedAmount, maturityDate, interestRate, fundRepaymentFrequency, tenure,
                tenureFrequency, morotorium, morotoriumFrequency, loanPortfolioFee, bookDebtHypothecation, cashCollateral,
                personalGurantee, isActive, isLoanAssigned, externalId, fundLoanPurpose);
    }

    public Fund(final String name, final String externalId, final CodeValue facilityType, final boolean isActive, boolean isLoanAssigned) {
        this.name = name;
        this.externalId = externalId;
        this.isActive = isActive;
        this.isLoanAssigned = isLoanAssigned;
        this.facilityType = facilityType;
        this.fundCategory = null;
        this.fundSource = null;
        this.assignmentStartDate = null;
        this.assignmentEndDate = null;
        this.sanctionedDate = null;
        this.sanctionedAmount = null;
        this.disbursedDate = null;
        this.disbursedAmount = null;
        this.maturityDate = null;
        this.interestRate = null;
        this.fundRepaymentFrequency = null;
        this.tenure = null;
        this.tenureFrequency = null;
        this.morotorium = null;
        this.morotoriumFrequency = null;
        this.loanPortfolioFee = null;
        this.bookDebtHypothecation = null;
        this.cashCollateral = null;
        this.personalGurantee = null;
    }

    public static Fund instance(final String name, final String externalId, final CodeValue facilityType, final boolean isActive, boolean isLoanAssigned) {

        return new Fund(name, externalId, facilityType, isActive, isLoanAssigned);
    }

    public CodeValue getFundSource() {
        return this.fundSource;
    }

    public void setFundSource(CodeValue fundSource) {
        this.fundSource = fundSource;
    }

    public CodeValue getFundCategory() {
        return this.fundCategory;
    }

    public void setFundCategory(CodeValue fundCategory) {
        this.fundCategory = fundCategory;
    }

    public CodeValue getFacilityType() {
        return this.facilityType;
    }

    public void setFacilityType(CodeValue facilityType) {
        this.facilityType = facilityType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAssignmentStartDate() {
        return this.assignmentStartDate;
    }

    public void setAssignmentStartDate(Date assignmentStartDate) {
        this.assignmentStartDate = assignmentStartDate;
    }

    public Date getAssignmentEndDate() {
        return this.assignmentEndDate;
    }

    public void setAssignmentEndDate(Date assignmentEndDate) {
        this.assignmentEndDate = assignmentEndDate;
    }

    public Date getSanctionedDate() {
        return this.sanctionedDate;
    }

    public void setSanctionedDate(Date sanctionedDate) {
        this.sanctionedDate = sanctionedDate;
    }

    public BigDecimal getSanctionedAmount() {
        return this.sanctionedAmount;
    }

    public void setSanctionedAmount(BigDecimal sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }

    public Date getDisbursedDate() {
        return this.disbursedDate;
    }

    public void setDisbursedDate(Date disbursedDate) {
        this.disbursedDate = disbursedDate;
    }

    public BigDecimal getDisbursedAmount() {
        return this.disbursedAmount;
    }

    public void setDisbursedAmount(BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    public Date getMaturityDate() {
        return this.maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public CodeValue getFundRepaymentFrequency() {
        return this.fundRepaymentFrequency;
    }

    public void setFundRepaymentFrequency(CodeValue fundRepaymentFrequency) {
        this.fundRepaymentFrequency = fundRepaymentFrequency;
    }

    public Integer getTenure() {
        return this.tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public Integer getTenureFrequency() {
        return this.tenureFrequency;
    }

    public void setTenureFrequency(Integer tenureFrequency) {
        this.tenureFrequency = tenureFrequency;
    }

    public Integer getMorotorium() {
        return this.morotorium;
    }

    public void setMorotorium(Integer morotorium) {
        this.morotorium = morotorium;
    }

    public Integer getMorotoriumFrequency() {
        return this.morotoriumFrequency;
    }

    public void setMorotoriumFrequency(Integer morotoriumFrequency) {
        this.morotoriumFrequency = morotoriumFrequency;
    }

    public BigDecimal getLoanPortfolioFee() {
        return this.loanPortfolioFee;
    }

    public void setLoanPortfolioFee(BigDecimal loanPortfolioFee) {
        this.loanPortfolioFee = loanPortfolioFee;
    }

    public BigDecimal getBookDebtHypothecation() {
        return this.bookDebtHypothecation;
    }

    public void setBookDebtHypothecation(BigDecimal bookDebtHypothecation) {
        this.bookDebtHypothecation = bookDebtHypothecation;
    }

    public BigDecimal getCashCollateral() {
        return this.cashCollateral;
    }

    public void setCashCollateral(BigDecimal cashCollateral) {
        this.cashCollateral = cashCollateral;
    }

    public String getPersonalGurantee() {
        return this.personalGurantee;
    }

    public void setPersonalGurantee(String personalGurantee) {
        this.personalGurantee = personalGurantee;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isLoanAssigned() {
        return this.isLoanAssigned;
    }

    public void setLoanAssigned(boolean isLoanAssigned) {
        this.isLoanAssigned = isLoanAssigned;
    }

    public static Fund fromJson(final JsonCommand command) {

        final String firstnameParamName = "name";
        final String name = command.stringValueOfParameterNamed(firstnameParamName);

        final String lastnameParamName = "externalId";
        final String externalId = command.stringValueOfParameterNamed(lastnameParamName);

        return new Fund(name, externalId);
    }

    protected Fund() {
        //
    }

    private Fund(final String fundName, final String externalId) {
        this.name = StringUtils.defaultIfEmpty(fundName, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String externalIdParamName = "externalId";
        if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }

    public List<FundLoanPurpose> getFundLoanPurpose() {
        return this.fundLoanPurpose;
    }

    public void setFundLoanPurpose(List<FundLoanPurpose> fundLoanPurpose) {
        if (this.fundLoanPurpose == null) {
            this.fundLoanPurpose = new ArrayList<>();
        }
        this.fundLoanPurpose.clear();
        this.fundLoanPurpose.addAll(updateFundToFundLoanPurpose(fundLoanPurpose));
    }

    public boolean isManualStatusUpdate() {
        return this.isManualStatusUpdate;
    }

    public void setManualStatusUpdate(boolean isManualStatusUpdate) {
        this.isManualStatusUpdate = isManualStatusUpdate;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}