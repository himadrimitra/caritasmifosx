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

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanproduct.domain.InterestRecalculationCompoundingMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductInterestRecalculationDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanRescheduleStrategyMethod;
import org.apache.fineract.portfolio.loanproduct.domain.RecalculationFrequencyType;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Entity for holding interest recalculation setting, details will be copied
 * from product directly
 * 
 * @author conflux
 */

@Entity
@Table(name = "m_loan_recalculation_details")
public class LoanInterestRecalculationDetails extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    /**
     * {@link InterestRecalculationCompoundingMethod}
     */
    @Column(name = "compound_type_enum", nullable = false)
    private Integer interestRecalculationCompoundingMethod;

    /**
     * {@link LoanRescheduleStrategyMethod}
     */
    @Column(name = "reschedule_strategy_enum", nullable = false)
    private Integer rescheduleStrategyMethod;

    @Column(name = "rest_frequency_type_enum", nullable = false)
    private Integer restFrequencyType;

    @Column(name = "rest_frequency_interval", nullable = false)
    private Integer restInterval;

    @Column(name = "rest_frequency_nth_day_enum", nullable = true)
    private Integer restFrequencyNthDay;

    @Column(name = "rest_frequency_weekday_enum", nullable = true)
    private Integer restFrequencyWeekday;

    @Column(name = "rest_frequency_on_day", nullable = true)
    private Integer restFrequencyOnDay;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "rest_frequency_start_date")
    private Date restFrequencyStartDate;

    @Column(name = "compounding_frequency_type_enum", nullable = true)
    private Integer compoundingFrequencyType;

    @Column(name = "compounding_frequency_interval", nullable = true)
    private Integer compoundingInterval;

    @Column(name = "compounding_frequency_nth_day_enum", nullable = true)
    private Integer compoundingFrequencyNthDay;
    @Column(name = "compounding_frequency_weekday_enum", nullable = true)
    private Integer compoundingFrequencyWeekday;
    @Column(name = "compounding_frequency_on_day", nullable = true)
    private Integer compoundingFrequencyOnDay;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "compounding_frequency_start_date")
    private Date compoundingFrequencyStartDate;

    @Column(name = "is_compounding_to_be_posted_as_transaction")
    private Boolean isCompoundingToBePostedAsTransaction;
    @Column(name = "allow_compounding_on_eod")
    private Boolean allowCompoundingOnEod;

    @Column(name = "is_subsidy_applicable")
    private Boolean isSubsidyApplicable;

    protected LoanInterestRecalculationDetails() {
        // Default constructor for jpa repository
    }

    private LoanInterestRecalculationDetails(final Integer interestRecalculationCompoundingMethod, final Integer rescheduleStrategyMethod,
            final Integer restFrequencyType, final Integer restInterval, final Integer restFrequencyNthDay, Integer restFrequencyWeekday,
            Integer restFrequencyOnDay, final LocalDate restFrequencyStartDate, Integer compoundingFrequencyType, Integer compoundingInterval,
            Integer compoundingFrequencyNthDay, Integer compoundingFrequencyWeekday, Integer compoundingFrequencyOnDay,
            final boolean isCompoundingToBePostedAsTransaction, final boolean allowCompoundingOnEod, final Boolean isSubsidyApplicable,
            final LocalDate compoundingFrequencyStartDate) {
        this.interestRecalculationCompoundingMethod = interestRecalculationCompoundingMethod;
        this.rescheduleStrategyMethod = rescheduleStrategyMethod;
        this.restFrequencyNthDay = restFrequencyNthDay;
        this.restFrequencyWeekday = restFrequencyWeekday;
        this.restFrequencyOnDay = restFrequencyOnDay;
        this.restFrequencyType = restFrequencyType;
        this.restInterval = restInterval;
        if (restFrequencyStartDate != null) {
            this.restFrequencyStartDate = restFrequencyStartDate.toDate();
        }
        this.compoundingFrequencyNthDay = compoundingFrequencyNthDay;
        this.compoundingFrequencyWeekday = compoundingFrequencyWeekday;
        this.compoundingFrequencyOnDay = compoundingFrequencyOnDay;
        this.compoundingFrequencyType = compoundingFrequencyType;
        this.compoundingInterval = compoundingInterval;
        this.isCompoundingToBePostedAsTransaction = isCompoundingToBePostedAsTransaction;
        this.allowCompoundingOnEod = allowCompoundingOnEod;
        this.isSubsidyApplicable = isSubsidyApplicable;
        if (compoundingFrequencyStartDate != null) {
            this.compoundingFrequencyStartDate = compoundingFrequencyStartDate.toDate();
        }
    }

    public static LoanInterestRecalculationDetails createFrom(
            final LoanProductInterestRecalculationDetails loanProductInterestRecalculationDetails, final LocalDate restFrequencyStartDate,
            final LocalDate compoundingFrequencyStartDate) {
        return new LoanInterestRecalculationDetails(loanProductInterestRecalculationDetails.getInterestRecalculationCompoundingMethod(),
                loanProductInterestRecalculationDetails.getRescheduleStrategyMethod(), loanProductInterestRecalculationDetails
                        .getRestFrequencyType().getValue(), loanProductInterestRecalculationDetails.getRestInterval(),
                loanProductInterestRecalculationDetails.getRestFrequencyNthDay(),
                loanProductInterestRecalculationDetails.getRestFrequencyWeekday(),
                loanProductInterestRecalculationDetails.getRestFrequencyOnDay(), restFrequencyStartDate,
                loanProductInterestRecalculationDetails.getCompoundingFrequencyType().getValue(),
                loanProductInterestRecalculationDetails.getCompoundingInterval(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyNthDay(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyWeekday(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyOnDay(),
                loanProductInterestRecalculationDetails.getIsCompoundingToBePostedAsTransaction(),
                loanProductInterestRecalculationDetails.allowCompoundingOnEod(),
                loanProductInterestRecalculationDetails.isSubsidyApplicable(), compoundingFrequencyStartDate);
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    public InterestRecalculationCompoundingMethod getInterestRecalculationCompoundingMethod() {
        return InterestRecalculationCompoundingMethod.fromInt(this.interestRecalculationCompoundingMethod);
    }

    public LoanRescheduleStrategyMethod getRescheduleStrategyMethod() {
        return LoanRescheduleStrategyMethod.fromInt(this.rescheduleStrategyMethod);
    }

    public RecalculationFrequencyType getRestFrequencyType() {
        return RecalculationFrequencyType.fromInt(this.restFrequencyType);
    }

    public Integer getRestInterval() {
        return this.restInterval;
    }

    public RecalculationFrequencyType getCompoundingFrequencyType() {
        return RecalculationFrequencyType.fromInt(this.compoundingFrequencyType);
    }

    public Integer getCompoundingInterval() {
        return this.compoundingInterval;
    }

    public Integer getRestFrequencyNthDay() {
        return this.restFrequencyNthDay;
    }

    public Integer getRestFrequencyWeekday() {
        return this.restFrequencyWeekday;
    }

    public Integer getRestFrequencyOnDay() {
        return this.restFrequencyOnDay;
    }

    public Integer getCompoundingFrequencyNthDay() {
        return this.compoundingFrequencyNthDay;
    }

    public Integer getCompoundingFrequencyWeekday() {
        return this.compoundingFrequencyWeekday;
    }

    public Integer getCompoundingFrequencyOnDay() {
        return this.compoundingFrequencyOnDay;
    }

    public boolean isCompoundingToBePostedAsTransaction() {
        return null == this.isCompoundingToBePostedAsTransaction ? false : this.getInterestRecalculationCompoundingMethod()
                .isCompoundingEnabled() && this.isCompoundingToBePostedAsTransaction;
    }

    public boolean allowCompoundingOnEod() {
        return this.allowCompoundingOnEod;
    }

    public Boolean isSubsidyApplicable() {
        return isSubsidyApplicable;
    }
    
    public LocalDate getRestFrequencyStartDateLocalDate() {
        LocalDate restFrequencyStartDate = null;
        if (this.restFrequencyStartDate != null) {
            restFrequencyStartDate = new LocalDate(this.restFrequencyStartDate);
        }
        return restFrequencyStartDate;
    }
    
    public LocalDate getRestFrequencyStartDateLocalDate(final Loan loan) {
        LocalDate restFrequencyStartDate = null;
        if (this.restFrequencyStartDate == null) {
            restFrequencyStartDate = loan.getDisbursementDate();
        } else {
            restFrequencyStartDate = new LocalDate(this.restFrequencyStartDate);
        }
        return restFrequencyStartDate;
    }

    public void setRestFrequencyStartDate(Date restFrequencyStartDate) {
        this.restFrequencyStartDate = restFrequencyStartDate;
    }

    public LocalDate getCompoundingFrequencyStartDateLocalDate() {
        LocalDate compoundingFrequencyStartDate = null;
        if (this.compoundingFrequencyStartDate != null) {
            compoundingFrequencyStartDate = new LocalDate(this.compoundingFrequencyStartDate);
        }
        return compoundingFrequencyStartDate;
    }
    
    public LocalDate getCompoundingFrequencyStartDateLocalDate(final Loan loan) {
        LocalDate compoundingFrequencyStartDate = null;
        if (this.compoundingFrequencyStartDate == null) {
            compoundingFrequencyStartDate = loan.getDisbursementDate();
        } else {
            compoundingFrequencyStartDate = new LocalDate(this.compoundingFrequencyStartDate);
        }
        return compoundingFrequencyStartDate;
    }

    public void setCompoundingFrequencyStartDate(Date compoundingFrequencyStartDate) {
        this.compoundingFrequencyStartDate = compoundingFrequencyStartDate;
    }
    
    public void update(final JsonCommand command, final Map<String, Object> actualChanges) {
        if (command.isChangeInLocalDateParameterNamed(LoanApiConstants.recalculationRestFrequencyStartDateParamName,
                getRestFrequencyStartDateLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(LoanApiConstants.recalculationRestFrequencyStartDateParamName);
            actualChanges.put(LoanApiConstants.recalculationRestFrequencyStartDateParamName, valueAsInput);
            actualChanges.put("recalculateLoanSchedule", true);

            final LocalDate newValue = command
                    .localDateValueOfParameterNamed(LoanApiConstants.recalculationRestFrequencyStartDateParamName);
            if (newValue == null) {
                this.restFrequencyStartDate = null;
            } else {
                this.restFrequencyStartDate = newValue.toDate();
            }
        }

        if (command.isChangeInLocalDateParameterNamed(LoanApiConstants.recalculationCompoundingFrequencyStartDateParamName,
                getCompoundingFrequencyStartDateLocalDate())) {
            final String valueAsInput = command
                    .stringValueOfParameterNamed(LoanApiConstants.recalculationCompoundingFrequencyStartDateParamName);
            actualChanges.put(LoanApiConstants.recalculationCompoundingFrequencyStartDateParamName, valueAsInput);
            actualChanges.put("recalculateLoanSchedule", true);

            final LocalDate newValue = command
                    .localDateValueOfParameterNamed(LoanApiConstants.recalculationCompoundingFrequencyStartDateParamName);
            if (newValue == null) {
                this.compoundingFrequencyStartDate = null;
            } else {
                this.compoundingFrequencyStartDate = newValue.toDate();
            }
        }
    }

}
