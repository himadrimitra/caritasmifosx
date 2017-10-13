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
package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.data.ChargeOverdueData;
import org.apache.fineract.portfolio.charge.exception.ChargeDueAtDisbursementCannotBePenaltyException;
import org.apache.fineract.portfolio.charge.exception.ChargeMustBePenaltyException;
import org.apache.fineract.portfolio.charge.exception.ChargeParameterUpdateNotSupportedException;
import org.apache.fineract.portfolio.charge.exception.ChargeSlabRangeOverlapException;
import org.apache.fineract.portfolio.charge.exception.SlabChargeTypeException;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.common.domain.LoanPeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.MonthDay;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "Charge")
@Table(name = "m_charge", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "name") })
public class Charge extends AbstractPersistable<Long> {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal amount;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "charge_applies_to_enum", nullable = false)
    private Integer chargeAppliesTo;

    @Column(name = "charge_time_enum", nullable = false)
    private Integer chargeTimeType;

    @Column(name = "charge_calculation_enum")
    private Integer chargeCalculation;

    @Column(name = "charge_payment_mode_enum", nullable = true)
    private Integer chargePaymentMode;

    @Column(name = "fee_on_day", nullable = true)
    private Integer feeOnDay;

    @Column(name = "fee_interval", nullable = true)
    private Integer feeInterval;

    @Column(name = "fee_on_month", nullable = true)
    private Integer feeOnMonth;

    @Column(name = "is_penalty", nullable = false)
    private boolean penalty;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "min_cap", scale = 6, precision = 19, nullable = true)
    private BigDecimal minCap;

    @Column(name = "max_cap", scale = 6, precision = 19, nullable = true)
    private BigDecimal maxCap;

    @Column(name = "fee_frequency", nullable = true)
    private Integer feeFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_or_liability_account_id")
    private GLAccount account;

    @ManyToOne
    @JoinColumn(name = "tax_group_id")
    private TaxGroup taxGroup;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charge_id", referencedColumnName = "id", nullable = false)
    private List<ChargeSlab> slabs = new ArrayList<>();

    @Column(name = "is_capitalized", nullable = true)
    private boolean isCapitalized;

    @Column(name = "emi_rounding_goalseek", nullable = false)
    private boolean emiRoundingGoalSeek = false;

    @Column(name = "is_glim_charge", nullable = false)
    private boolean isGlimCharge = false;

    @Column(name = "glim_charge_calculation_enum")
    private Integer glimChargeCalculation;

    @Column(name = "charge_percentage_type")
    private Integer percentageType;

    @Column(name = "charge_percentage_period_type")
    private Integer percentagePeriodType;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "charge", optional = true, orphanRemoval = true, fetch = FetchType.EAGER)
    private ChargeOverueDetail chargeOverueDetail;

    public static Charge fromJson(final JsonCommand command, final GLAccount account, final TaxGroup taxGroup) {

        final String name = command.stringValueOfParameterNamed("name");
        final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
        final String currencyCode = command.stringValueOfParameterNamed("currencyCode");

        final ChargeAppliesTo chargeAppliesTo = ChargeAppliesTo.fromInt(command.integerValueOfParameterNamed("chargeAppliesTo"));
        final ChargeTimeType chargeTimeType = ChargeTimeType.fromInt(command.integerValueOfParameterNamed("chargeTimeType"));
        final ChargeCalculationType chargeCalculationType = ChargeCalculationType
                .fromInt(command.integerValueOfParameterNamed("chargeCalculationType"));
        final Integer chargePaymentMode = command.integerValueOfParameterNamed("chargePaymentMode");
        boolean isCapitalized = false;
        final List<ChargeSlab> chargeSlabs = new ArrayList<>();
        if (chargeCalculationType.isSlabBased()) {
            chargeSlabs.addAll(assambleSlabsFromJson(command));
            isCapitalized = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.isCapitalizedParamName);
        }

        final ChargePaymentMode paymentMode = chargePaymentMode == null ? null : ChargePaymentMode.fromInt(chargePaymentMode);

        final boolean penalty = command.booleanPrimitiveValueOfParameterNamed("penalty");
        final boolean active = command.booleanPrimitiveValueOfParameterNamed("active");
        final MonthDay feeOnMonthDay = command.extractMonthDayNamed("feeOnMonthDay");
        final Integer feeInterval = command.integerValueOfParameterNamed("feeInterval");
        final BigDecimal minCap = command.bigDecimalValueOfParameterNamed("minCap");
        final BigDecimal maxCap = command.bigDecimalValueOfParameterNamed("maxCap");
        final Integer feeFrequency = command.integerValueOfParameterNamed("feeFrequency");
        final Boolean emiRoundingGoalSeek = command.booleanPrimitiveValueOfParameterNamed("emiRoundingGoalSeek");
        final Boolean isGlimCharge = command.booleanPrimitiveValueOfParameterNamed("isGlimCharge");
        GlimChargeCalculationType glimChargeCalculation = GlimChargeCalculationType.INVALID;
        if (command.hasParameter(ChargesApiConstants.glimChargeCalculation)) {
            glimChargeCalculation = GlimChargeCalculationType.fromInt(command.integerValueOfParameterNamed("glimChargeCalculation"));
        }

        ChargePercentageType chargePercentageType = ChargePercentageType.FLAT;
        if (command.hasParameter(ChargesApiConstants.percentageTypeParamName)) {
            final Integer percentageType = command.integerValueOfParameterNamed(ChargesApiConstants.percentageTypeParamName);
            chargePercentageType = ChargePercentageType.fromInt(percentageType);
        }
        ChargePercentagePeriodType chargePercentagePeriodType = ChargePercentagePeriodType.DAILY;
        if (command.hasParameter(ChargesApiConstants.percentagePeriodTypeParamName)) {
            final Integer percentagePeriodType = command.integerValueOfParameterNamed(ChargesApiConstants.percentagePeriodTypeParamName);
            chargePercentagePeriodType = ChargePercentagePeriodType.fromInt(percentagePeriodType);
        }

        ChargeOverueDetail chargeOverueDetail = null;
        if (chargeTimeType.isOverdueInstallment()) {
            final String overdueJson = command.jsonFragment(ChargesApiConstants.overdueChargeDetailParamName);
            final JsonCommand overdueCommand = JsonCommand.fromExistingCommand(command, overdueJson);
            chargeOverueDetail = ChargeOverueDetail.fromJson(overdueCommand, command.extractLocale());
        }

        return new Charge(name, amount, currencyCode, chargeAppliesTo, chargeTimeType, chargeCalculationType, penalty, active, paymentMode,
                feeOnMonthDay, feeInterval, minCap, maxCap, feeFrequency, account, taxGroup, emiRoundingGoalSeek, isGlimCharge,
                glimChargeCalculation, isCapitalized, chargeSlabs, chargeOverueDetail, chargePercentageType, chargePercentagePeriodType);
    }

    public static List<ChargeSlab> assambleSlabsFromJson(final JsonCommand command) {
        final ChargeSlab parent = null;
        List<ChargeSlab> chargeSlabs = new ArrayList<>();
        final JsonArray chargeSlabArray = command.arrayOfParameterNamed(ChargesApiConstants.slabsParamName);
        chargeSlabs = getSlab(chargeSlabs, chargeSlabArray, parent);
        validateSlabType(chargeSlabs);

        return chargeSlabs;
    }

    public static void validateSlabType(final List<ChargeSlab> chargeSlabs) {
        for (final ChargeSlab chargeSlab : chargeSlabs) {
            final Integer type = chargeSlab.getType();
            if (chargeSlab.getSubSlabs() != null && chargeSlab.getSubSlabs().size() > 0) {
                for (final ChargeSlab chargeSubSlab : chargeSlab.getSubSlabs()) {
                    if (chargeSubSlab.equals(type)) { throw new SlabChargeTypeException(); }
                }
            }
        }
    }

    private static List<ChargeSlab> getSlab(final List<ChargeSlab> chargeSlabs, final JsonArray chargeSlabArray, final ChargeSlab parent) {
        for (final JsonElement element : chargeSlabArray) {
            final JsonObject jsonObject = element.getAsJsonObject();
            final ChargeSlab chargeSlab = getSlab(jsonObject, parent);
            validateSlabRanges(chargeSlabs, chargeSlab);
            if (isSubSlabExist(jsonObject)) {
                List<ChargeSlab> subSlabs = new ArrayList<>();
                subSlabs = getSlab(subSlabs, jsonObject.get(ChargesApiConstants.subSlabsParamName).getAsJsonArray(), chargeSlab);
                chargeSlab.setSubSlabs(subSlabs);
            }
            chargeSlabs.add(chargeSlab);
        }
        return chargeSlabs;
    }

    public static boolean isSubSlabExist(final JsonObject jsonObject) {
        if (jsonObject != null && jsonObject.has(ChargesApiConstants.subSlabsParamName)) {
            if (jsonObject.get(ChargesApiConstants.subSlabsParamName) != null) { return jsonObject
                    .get(ChargesApiConstants.subSlabsParamName).getAsJsonArray().size() > 0; }
        }
        return false;
    }

    private static ChargeSlab getSlab(final JsonObject jsonObject, final ChargeSlab parent) {
        final BigDecimal minValue = jsonObject.get(ChargesApiConstants.minValueParamName).getAsBigDecimal();
        final BigDecimal maxValue = jsonObject.get(ChargesApiConstants.maxValueParamName).getAsBigDecimal();
        final BigDecimal chargeAmount = jsonObject.get(ChargesApiConstants.amountParamName).getAsBigDecimal();
        final Integer type = jsonObject.get(ChargesApiConstants.typeParamName).getAsInt();
        final ChargeSlab chargeSlab = ChargeSlab.createNew(minValue, maxValue, chargeAmount, type, parent);
        return chargeSlab;
    }

    protected Charge() {
        //
    }

    public static void validateSlabRanges(final List<ChargeSlab> chargeSlabs, final ChargeSlab chargeSlab) {
        for (final ChargeSlab slab : chargeSlabs) {
            if (MathUtility.isInRange(slab.getMinValue(), slab.getMaxValue(), chargeSlab.getMinValue()) || MathUtility.isInRange(
                    chargeSlab.getMinValue(), chargeSlab.getMaxValue(), slab.getMinValue())) { throw new ChargeSlabRangeOverlapException(
                            SlabChargeType.fromInt(chargeSlab.getType()).getCode()); }
        }

    }

    private Charge(final String name, final BigDecimal amount, final String currencyCode, final ChargeAppliesTo chargeAppliesTo,
            final ChargeTimeType chargeTime, final ChargeCalculationType chargeCalculationType, final boolean penalty, final boolean active,
            final ChargePaymentMode paymentMode, final MonthDay feeOnMonthDay, final Integer feeInterval, final BigDecimal minCap,
            final BigDecimal maxCap, final Integer feeFrequency, final GLAccount account, final TaxGroup taxGroup,
            final boolean emiRoundingGoalSeek, final boolean isGlimCharge, final GlimChargeCalculationType glimChargeCalculation,
            final boolean isCapitalized, final List<ChargeSlab> chargeSlab, final ChargeOverueDetail chargeOverueDetail,
            final ChargePercentageType percentageType, final ChargePercentagePeriodType percentagePeriodType) {
        this.name = name;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.chargeAppliesTo = chargeAppliesTo.getValue();
        this.chargeTimeType = chargeTime.getValue();
        this.chargeCalculation = chargeCalculationType.getValue();
        this.penalty = penalty;
        this.active = active;
        this.account = account;
        this.taxGroup = taxGroup;
        this.chargePaymentMode = paymentMode == null ? null : paymentMode.getValue();
        this.emiRoundingGoalSeek = emiRoundingGoalSeek;
        this.isGlimCharge = isGlimCharge;
        this.glimChargeCalculation = glimChargeCalculation.getValue();
        this.isCapitalized = isCapitalized;
        if (this.slabs == null) {
            this.slabs = new ArrayList<>();
        }
        this.slabs.addAll(chargeSlab);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("charges");

        if (isMonthlyFee() || isAnnualFee()) {
            this.feeOnMonth = feeOnMonthDay.getMonthOfYear();
            this.feeOnDay = feeOnMonthDay.getDayOfMonth();
        }
        this.feeInterval = feeInterval;
        this.feeFrequency = feeFrequency;
        this.chargeOverueDetail = chargeOverueDetail;
        if (this.chargeOverueDetail != null) {
            this.chargeOverueDetail.setCharge(this);
        }
        this.percentageType = percentageType.getValue();
        this.percentagePeriodType = percentagePeriodType.getValue();

        if (isSavingsCharge()) {
            // TODO vishwas, this validation seems unnecessary as identical
            // validation is performed in the write service
            if (!isAllowedSavingsChargeTime()) {
                baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                        .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.savings");
            }
            // TODO vishwas, this validation seems unnecessary as identical
            // validation is performed in the writeservice
            if (!isAllowedSavingsChargeCalculationType()) {
                baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                        .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.calculation.type.for.savings");
            }

            if (!(ChargeTimeType.fromInt(getChargeTimeType()).isWithdrawalFee()
                    || ChargeTimeType.fromInt(getChargeTimeType()).isSavingsNoActivityFee())
                    && ChargeCalculationType.fromInt(getChargeCalculation()).isPercentageOfAmount()) {
                baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                        .failWithCodeNoParameterAddedToErrorCode(
                                "savings.charge.calculation.type.percentage.allowed.only.for.withdrawal.or.NoActivity");
            }

        } else if (isLoanCharge()) {

            if (penalty && (chargeTime.isTimeOfDisbursement()
                    || chargeTime.isTrancheDisbursement())) { throw new ChargeDueAtDisbursementCannotBePenaltyException(name); }
            if (!penalty && chargeTime.isOverdueInstallment()) { throw new ChargeMustBePenaltyException(name); }
            // TODO vishwas, this validation seems unnecessary as identical
            // validation is performed in the write service
            if (!isAllowedLoanChargeTime()) {
                baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                        .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.loan");
            }
        }
        // if (isPercentageOfApprovedAmount()) {
        this.minCap = minCap;
        this.maxCap = maxCap;
        // }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final Charge rhs = (Charge) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)) //
                .append(getId(), rhs.getId()) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 5) //
                .append(getId()) //
                .toHashCode();
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Integer getChargeTimeType() {
        return this.chargeTimeType;
    }

    public ChargeTimeType fetchChargeTimeType() {
        return ChargeTimeType.fromInt(this.chargeTimeType);
    }

    public Integer getChargeCalculation() {
        return this.chargeCalculation;
    }

    public ChargeCalculationType getChargeCalculationType() {
        return ChargeCalculationType.fromInt(this.chargeCalculation);
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isPenalty() {
        return this.penalty;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public boolean isLoanCharge() {
        return ChargeAppliesTo.fromInt(this.chargeAppliesTo).isLoanCharge();
    }

    public boolean isAllowedLoanChargeTime() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAllowedLoanChargeTime();
    }

    public boolean isAllowedClientChargeTime() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAllowedClientChargeTime();
    }

    public boolean isSavingsCharge() {
        return ChargeAppliesTo.fromInt(this.chargeAppliesTo).isSavingsCharge();
    }

    public boolean isClientCharge() {
        return ChargeAppliesTo.fromInt(this.chargeAppliesTo).isClientCharge();
    }

    public boolean isAllowedSavingsChargeTime() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAllowedSavingsChargeTime();
    }

    public boolean isAllowedSavingsChargeCalculationType() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isAllowedSavingsChargeCalculationType();
    }

    public boolean isAllowedClientChargeCalculationType() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isAllowedClientChargeCalculationType();
    }

    public boolean isPercentageOfApprovedAmount() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfAmount();
    }

    public boolean isPercentageOfDisbursementAmount() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfDisbursementAmount();
    }

    public BigDecimal getMinCap() {
        return this.minCap;
    }

    public BigDecimal getMaxCap() {
        return this.maxCap;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String localeAsInput = command.locale();

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("charges");

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = newValue;
        }

        final String currencyCodeParamName = "currencyCode";
        if (command.isChangeInStringParameterNamed(currencyCodeParamName, this.currencyCode)) {
            final String newValue = command.stringValueOfParameterNamed(currencyCodeParamName);
            actualChanges.put(currencyCodeParamName, newValue);
            this.currencyCode = newValue;
        }

        final String amountParamName = "amount";
        if (command.isChangeInBigDecimalParameterNamed(amountParamName, this.amount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(amountParamName);
            actualChanges.put(amountParamName, newValue);
            actualChanges.put("locale", localeAsInput);
            this.amount = newValue;
        }

        final String chargeTimeParamName = "chargeTimeType";
        if (command.isChangeInIntegerParameterNamed(chargeTimeParamName, this.chargeTimeType)) {
            final Integer newValue = command.integerValueOfParameterNamed(chargeTimeParamName);
            actualChanges.put(chargeTimeParamName, newValue);
            actualChanges.put("locale", localeAsInput);
            this.chargeTimeType = ChargeTimeType.fromInt(newValue).getValue();

            if (isSavingsCharge()) {
                if (!isAllowedSavingsChargeTime()) {
                    baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.savings");
                }
                // if charge time is changed to monthly then validate for
                // feeOnMonthDay and feeInterval
                if (isMonthlyFee()) {
                    final MonthDay monthDay = command.extractMonthDayNamed("feeOnMonthDay");
                    baseDataValidator.reset().parameter("feeOnMonthDay").value(monthDay).notNull();

                    final Integer feeInterval = command.integerValueOfParameterNamed("feeInterval");
                    baseDataValidator.reset().parameter("feeInterval").value(feeInterval).notNull().inMinMaxRange(1, 12);
                }
            } else if (isLoanCharge()) {
                if (!isAllowedLoanChargeTime()) {
                    baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.loan");
                }
            } else if (isClientCharge()) {
                if (!isAllowedClientChargeTime()) {
                    baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.client");
                }
            }
        }

        final String chargeAppliesToParamName = "chargeAppliesTo";
        if (command.isChangeInIntegerParameterNamed(chargeAppliesToParamName, this.chargeAppliesTo)) {
            /*
             * final Integer newValue =
             * command.integerValueOfParameterNamed(chargeAppliesToParamName);
             * actualChanges.put(chargeAppliesToParamName, newValue);
             * actualChanges.put("locale", localeAsInput); this.chargeAppliesTo
             * = ChargeAppliesTo.fromInt(newValue).getValue();
             */

            // AA: Do not allow to change chargeAppliesTo.
            final String errorMessage = "Update of Charge applies to is not supported";
            throw new ChargeParameterUpdateNotSupportedException("charge.applies.to", errorMessage);
        }

        final String chargeCalculationParamName = "chargeCalculationType";
        if (command.isChangeInIntegerParameterNamed(chargeCalculationParamName, this.chargeCalculation)) {
            final Integer newValue = command.integerValueOfParameterNamed(chargeCalculationParamName);
            actualChanges.put(chargeCalculationParamName, newValue);
            actualChanges.put("locale", localeAsInput);
            this.chargeCalculation = ChargeCalculationType.fromInt(newValue).getValue();

            if (isSavingsCharge()) {
                if (!isAllowedSavingsChargeCalculationType()) {
                    baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.calculation.type.for.savings");
                }

                if (!(ChargeTimeType.fromInt(getChargeTimeType()).isWithdrawalFee()
                        || ChargeTimeType.fromInt(getChargeTimeType()).isSavingsNoActivityFee())
                        && ChargeCalculationType.fromInt(getChargeCalculation()).isPercentageOfAmount()) {
                    baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                            .failWithCodeNoParameterAddedToErrorCode(
                                    "charge.calculation.type.percentage.allowed.only.for.withdrawal.or.noactivity");
                }
            } else if (isClientCharge()) {
                if (!isAllowedClientChargeCalculationType()) {
                    baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.calculation.type.for.client");
                }
            }
        }

        if (isLoanCharge()) {// validate only for loan charge
            final String paymentModeParamName = "chargePaymentMode";
            if (command.isChangeInIntegerParameterNamed(paymentModeParamName, this.chargePaymentMode)) {
                final Integer newValue = command.integerValueOfParameterNamed(paymentModeParamName);
                actualChanges.put(paymentModeParamName, newValue);
                actualChanges.put("locale", localeAsInput);
                this.chargePaymentMode = ChargePaymentMode.fromInt(newValue).getValue();
            }

            if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.isGlimChargeParamName, this.isGlimCharge)) {
                final Boolean newValue = command.booleanObjectValueOfParameterNamed(ChargesApiConstants.isGlimChargeParamName);
                actualChanges.put(ChargesApiConstants.isGlimChargeParamName, newValue);
                this.isGlimCharge = newValue;
                if (this.isGlimCharge) {
                    if (this.chargeTimeType.equals(ChargeTimeType.INSTALMENT_FEE.getValue())) {
                        baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                                .isOneOfTheseValues(ChargeCalculationType.PERCENT_OF_DISBURSEMENT_AMOUNT.getValue());
                    } else if (this.chargeTimeType.equals(ChargeTimeType.UPFRONT_FEE.getValue())) {
                        baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                                .isOneOfTheseValues(ChargeCalculationType.FLAT.getValue(), ChargeCalculationType.SLAB_BASED.getValue());
                    } else {
                        baseDataValidator.reset().parameter("ChargeTimeType").value(this.chargeTimeType)
                                .isOneOfTheseValues(ChargeTimeType.INSTALMENT_FEE.getValue(), ChargeTimeType.UPFRONT_FEE.getValue());
                    }
                }
            }

            // remove the slab charges if ChargeCalculationType is not slab
            // based
            if (!ChargeCalculationType.fromInt(this.chargeCalculation.intValue()).isSlabBased()) {
                if (this.slabs != null && !this.slabs.isEmpty()) {
                    this.slabs.clear();
                }
                actualChanges.put(ChargesApiConstants.slabsParamName, this.slabs);
            }
        }

        if (command.hasParameter("feeOnMonthDay")) {
            final MonthDay monthDay = command.extractMonthDayNamed("feeOnMonthDay");
            final String actualValueEntered = command.stringValueOfParameterNamed("feeOnMonthDay");
            final Integer dayOfMonthValue = monthDay.getDayOfMonth();
            if (this.feeOnDay != dayOfMonthValue) {
                actualChanges.put("feeOnMonthDay", actualValueEntered);
                actualChanges.put("locale", localeAsInput);
                this.feeOnDay = dayOfMonthValue;
            }

            final Integer monthOfYear = monthDay.getMonthOfYear();
            if (this.feeOnMonth != monthOfYear) {
                actualChanges.put("feeOnMonthDay", actualValueEntered);
                actualChanges.put("locale", localeAsInput);
                this.feeOnMonth = monthOfYear;
            }
        }

        final String feeInterval = "feeInterval";
        if (command.isChangeInIntegerParameterNamed(feeInterval, this.feeInterval)) {
            final Integer newValue = command.integerValueOfParameterNamed(feeInterval);
            actualChanges.put(feeInterval, newValue);
            actualChanges.put("locale", localeAsInput);
            this.feeInterval = newValue;
        }
        final String feeFrequency = "feeFrequency";
        if (command.isChangeInIntegerParameterNamed(feeFrequency, this.feeFrequency)) {
            final Integer newValue = command.integerValueOfParameterNamed(feeFrequency);
            actualChanges.put(feeFrequency, newValue);
            actualChanges.put("locale", localeAsInput);
            this.feeFrequency = newValue;
        }

        if (this.feeFrequency != null && !this.feeFrequency.equals(LoanPeriodFrequencyType.SAME_AS_REPAYMENT_PERIOD.getValue())) {
            baseDataValidator.reset().parameter("feeInterval").value(this.feeInterval).notNull();
        }

        final String penaltyParamName = "penalty";
        if (command.isChangeInBooleanParameterNamed(penaltyParamName, this.penalty)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(penaltyParamName);
            actualChanges.put(penaltyParamName, newValue);
            this.penalty = newValue;
        }

        final String activeParamName = "active";
        if (command.isChangeInBooleanParameterNamed(activeParamName, this.active)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(activeParamName);
            actualChanges.put(activeParamName, newValue);
            this.active = newValue;
        }

        final String emiRoundingGoalSeekParamName = "emiRoundingGoalSeek";
        if (command.isChangeInBooleanParameterNamed(emiRoundingGoalSeekParamName, this.emiRoundingGoalSeek)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(emiRoundingGoalSeekParamName);
            actualChanges.put(emiRoundingGoalSeekParamName, newValue);
            this.emiRoundingGoalSeek = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ChargesApiConstants.glimChargeCalculation, this.glimChargeCalculation)) {
            final Integer newValue = command.integerValueOfParameterNamed(ChargesApiConstants.glimChargeCalculation);
            actualChanges.put(ChargesApiConstants.glimChargeCalculation, newValue);
            this.glimChargeCalculation = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.isCapitalizedParamName, this.isCapitalized)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.isCapitalizedParamName);
            actualChanges.put(ChargesApiConstants.isCapitalizedParamName, newValue);
            this.isCapitalized = newValue;
        }

        // allow min and max cap to be only added to PERCENT_OF_AMOUNT for now
        if (isPercentageOfApprovedAmount() || isPercentageOfDisbursementAmount()) {
            final String minCapParamName = "minCap";
            if (command.isChangeInBigDecimalParameterNamed(minCapParamName, this.minCap)) {
                final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(minCapParamName);
                actualChanges.put(minCapParamName, newValue);
                actualChanges.put("locale", localeAsInput);
                this.minCap = newValue;
            }
            final String maxCapParamName = "maxCap";
            if (command.isChangeInBigDecimalParameterNamed(maxCapParamName, this.maxCap)) {
                final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(maxCapParamName);
                actualChanges.put(maxCapParamName, newValue);
                actualChanges.put("locale", localeAsInput);
                this.maxCap = newValue;
            }
        }

        if (this.penalty && ChargeTimeType.fromInt(this.chargeTimeType)
                .isTimeOfDisbursement()) { throw new ChargeDueAtDisbursementCannotBePenaltyException(this.name); }
        if (!this.penalty && ChargeTimeType.fromInt(this.chargeTimeType)
                .isOverdueInstallment()) { throw new ChargeMustBePenaltyException(this.name); }

        if (command.isChangeInLongParameterNamed(ChargesApiConstants.glAccountIdParamName, getIncomeAccountId())) {
            final Long newValue = command.longValueOfParameterNamed(ChargesApiConstants.glAccountIdParamName);
            actualChanges.put(ChargesApiConstants.glAccountIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ChargesApiConstants.taxGroupIdParamName, getTaxGroupId())) {
            final Long newValue = command.longValueOfParameterNamed(ChargesApiConstants.taxGroupIdParamName);
            actualChanges.put(ChargesApiConstants.taxGroupIdParamName, newValue);
            if (this.taxGroup != null) {
                baseDataValidator.reset().parameter(ChargesApiConstants.taxGroupIdParamName).failWithCode("modification.not.supported");
            }
        }

        if (command.isChangeInIntegerParameterNamed(ChargesApiConstants.percentageTypeParamName, this.percentageType)) {
            final Integer newValue = command.integerValueOfParameterNamed(ChargesApiConstants.percentageTypeParamName);
            final ChargePercentageType percentageType = ChargePercentageType.fromInt(newValue);
            actualChanges.put(ChargesApiConstants.percentageTypeParamName, percentageType.getValue());
            this.percentageType = percentageType.getValue();
        }

        if (command.isChangeInIntegerParameterNamed(ChargesApiConstants.percentagePeriodTypeParamName, this.percentagePeriodType)) {
            final Integer newValue = command.integerValueOfParameterNamed(ChargesApiConstants.percentagePeriodTypeParamName);
            final ChargePercentagePeriodType percentageType = ChargePercentagePeriodType.fromInt(newValue);
            actualChanges.put(ChargesApiConstants.percentagePeriodTypeParamName, percentageType.getValue());
            this.percentagePeriodType = percentageType.getValue();
        }

        if (fetchChargeTimeType().isOverdueInstallment()) {
            final String overdueJson = command.jsonFragment(ChargesApiConstants.overdueChargeDetailParamName);
            final JsonCommand overdueCommand = JsonCommand.fromExistingCommand(command, overdueJson);
            if (this.chargeOverueDetail == null) {
                this.chargeOverueDetail = ChargeOverueDetail.fromJson(overdueCommand, command.extractLocale());
                this.chargeOverueDetail.setCharge(this);
            } else {
                this.chargeOverueDetail.update(overdueCommand, actualChanges, command.extractLocale());
            }
        } else {
            this.chargeOverueDetail = null;
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

        return actualChanges;
    }

    /**
     * Delete is a <i>soft delete</i>. Updates flag on charge so it wont appear
     * in query/report results.
     *
     * Any fields with unique constraints and prepended with id of record.
     */
    public void delete() {
        this.deleted = true;
        this.name = getId() + "_" + this.name;
    }

    public ChargeData toData() {

        final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(this.chargeTimeType);
        final EnumOptionData chargeAppliesTo = ChargeEnumerations.chargeAppliesTo(this.chargeAppliesTo);
        final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(this.chargeCalculation);
        final EnumOptionData chargePaymentmode = ChargeEnumerations.chargePaymentMode(this.chargePaymentMode);
        final EnumOptionData feeFrequencyType = ChargeEnumerations.chargePaymentMode(this.feeFrequency);
        GLAccountData accountData = null;
        if (this.account != null) {
            accountData = new GLAccountData(this.account.getId(), this.account.getName(), this.account.getGlCode());
        }
        TaxGroupData taxGroupData = null;
        if (this.taxGroup != null) {
            taxGroupData = TaxGroupData.lookup(this.taxGroup.getId(), this.taxGroup.getName());
        }
        final EnumOptionData percentageType = ChargePercentageType.chargePercentageType(this.percentageType);
        final EnumOptionData percentagePeriodType = ChargePercentagePeriodType.chargePercentagePeriodType(this.percentagePeriodType);
        ChargeOverdueData chargeOverueData = null;
        if (this.chargeOverueDetail != null) {
            chargeOverueData = this.chargeOverueDetail.toData();
        }

        final CurrencyData currency = new CurrencyData(this.currencyCode, null, 0, 0, null, null);
        final EnumOptionData glimChargeCalculation = ChargeEnumerations.glimChargeCalculationType(this.glimChargeCalculation);
        return ChargeData.instance(getId(), this.name, this.amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType,
                chargePaymentmode, getFeeOnMonthDay(), this.feeInterval, this.penalty, this.active, this.minCap, this.maxCap,
                feeFrequencyType, accountData, taxGroupData, this.emiRoundingGoalSeek, this.isGlimCharge, glimChargeCalculation,
                this.isCapitalized, percentageType, percentagePeriodType, chargeOverueData);
    }

    public Integer getChargePaymentMode() {
        return this.chargePaymentMode;
    }

    public Integer getFeeInterval() {
        return this.feeInterval;
    }

    public boolean isMonthlyFee() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isMonthlyFee();
    }

    public boolean isAnnualFee() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAnnualFee();
    }

    public boolean isOverdueInstallment() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isOverdueInstallment();
    }

    public MonthDay getFeeOnMonthDay() {
        MonthDay feeOnMonthDay = null;
        if (this.feeOnDay != null && this.feeOnMonth != null) {
            feeOnMonthDay = new MonthDay(this.feeOnMonth, this.feeOnDay);
        }
        return feeOnMonthDay;
    }

    public Integer feeInterval() {
        return this.feeInterval;
    }

    public Integer feeFrequency() {
        return this.feeFrequency;
    }

    public GLAccount getAccount() {
        return this.account;
    }

    public void setAccount(final GLAccount account) {
        this.account = account;
    }

    private Long getIncomeAccountId() {
        Long incomeAccountId = null;
        if (this.account != null) {
            incomeAccountId = this.account.getId();
        }
        return incomeAccountId;
    }

    private Long getTaxGroupId() {
        Long taxGroupId = null;
        if (this.taxGroup != null) {
            taxGroupId = this.taxGroup.getId();
        }
        return taxGroupId;
    }

    public boolean isDisbursementCharge() {
        return ChargeTimeType.fromInt(this.chargeTimeType).equals(ChargeTimeType.DISBURSEMENT)
                || ChargeTimeType.fromInt(this.chargeTimeType).equals(ChargeTimeType.TRANCHE_DISBURSEMENT);
    }

    public TaxGroup getTaxGroup() {
        return this.taxGroup;
    }

    public void setTaxGroup(final TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
    }

    public boolean isWeeklyFee() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isWeeklyFee();
    }

    public boolean isDailyFee() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isDailyFee();
    }

    public boolean isEmiRoundingGoalSeek() {
        return this.emiRoundingGoalSeek;
    }

    public void setEmiRoundingGoalSeek(final boolean emiRoundingGoalSeek) {
        this.emiRoundingGoalSeek = emiRoundingGoalSeek;
    }

    public boolean isGlimCharge() {
        return this.isGlimCharge;
    }

    public Integer getGlimChargeCalculation() {
        return this.glimChargeCalculation;
    }

    public void setGlimChargeCalculation(final Integer glimChargeCalculation) {
        this.glimChargeCalculation = glimChargeCalculation;
    }

    public boolean isPercentageBased() {
        return isPercentageOfAmount() || isPercentageOfDisbursementAmount() || isPercentageOfInterest()
                || isPercentageOfAmountAndInterest();
    }

    public boolean isPercentageOfAmount() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfAmount();
    }

    public boolean isPercentageOfInterest() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfInterest();
    }

    public boolean isPercentageOfAmountAndInterest() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfAmountAndInterest();
    }

    public void updateSlabCharges(final List<ChargeSlab> slabList) {
        if (this.slabs != null && !this.slabs.isEmpty()) {
            this.slabs.clear();
        }
        this.slabs.addAll(slabList);
    }

    public List<ChargeSlab> getSlabs() {
        return this.slabs;
    }

    public boolean isCapitalized() {
        return this.isCapitalized;
    }

    public boolean isTrancheDisbursement() {
        return ChargeTimeType.fromInt(this.chargeTimeType).equals(ChargeTimeType.TRANCHE_DISBURSEMENT);
    }

    public ChargePercentageType getPercentageType() {
        return ChargePercentageType.fromInt(this.percentageType);
    }

    public ChargePercentagePeriodType getPercentagePeriodType() {
        return ChargePercentagePeriodType.fromInt(this.percentagePeriodType);
    }

    public ChargeOverueDetail getChargeOverueDetail() {
        return this.chargeOverueDetail;
    }

}