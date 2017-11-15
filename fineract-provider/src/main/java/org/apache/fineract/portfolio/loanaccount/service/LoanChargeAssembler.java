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
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.ChargeSlab;
import org.apache.fineract.portfolio.charge.domain.ChargeSlabRepository;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.charge.domain.SlabChargeType;
import org.apache.fineract.portfolio.charge.exception.ChargeNotInSlabExpection;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeAddedException;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTrancheCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTrancheDisbursementCharge;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;
import org.apache.fineract.portfolio.tax.domain.TaxGroupMappings;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanChargeAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanChargeRepository loanChargeRepository;
    private final LoanProductRepository loanProductRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final ChargeSlabRepository chargeSlabRepository;

    @Autowired
    public LoanChargeAssembler(final FromJsonHelper fromApiJsonHelper, final ChargeRepositoryWrapper chargeRepository,
            final LoanChargeRepository loanChargeRepository, final LoanProductRepository loanProductRepository,
            final ClientRepositoryWrapper clientRepository, final ChargeSlabRepository chargeSlabRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.chargeRepository = chargeRepository;
        this.loanChargeRepository = loanChargeRepository;
        this.loanProductRepository = loanProductRepository;
        this.clientRepository = clientRepository;
        this.chargeSlabRepository = chargeSlabRepository;
    }

    @SuppressWarnings("unused")
    public Set<LoanCharge> fromParsedJson(final JsonElement element, final List<LoanDisbursementDetails> disbursementDetails) {
        final JsonArray jsonDisbursement = this.fromApiJsonHelper.extractJsonArrayNamed("disbursementData", element);
        final List<Long> disbursementChargeIds = new ArrayList<>();

        if (jsonDisbursement != null && jsonDisbursement.size() > 0) {
            for (int i = 0; i < jsonDisbursement.size(); i++) {
                final JsonObject jsonObject = jsonDisbursement.get(i).getAsJsonObject();
                if (jsonObject != null && jsonObject.getAsJsonPrimitive(LoanApiConstants.loanChargeIdParameterName) != null) {
                    final String chargeIds = jsonObject.getAsJsonPrimitive(LoanApiConstants.loanChargeIdParameterName).getAsString();
                    if (chargeIds != null) {
                        if (chargeIds.indexOf(",") != -1) {
                            final String[] chargeId = chargeIds.split(",");
                            for (final String loanChargeId : chargeId) {
                                disbursementChargeIds.add(Long.parseLong(loanChargeId));
                            }
                        } else {
                            disbursementChargeIds.add(Long.parseLong(chargeIds));
                        }
                    }

                }
            }
        }

        final Set<LoanCharge> loanCharges = new HashSet<>();
        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("numberOfRepayments", element);
        final Long productId = this.fromApiJsonHelper.extractLongNamed("productId", element);
        final LoanProduct loanProduct = this.loanProductRepository.findOne(productId);
        if (loanProduct == null) { throw new LoanProductNotFoundException(productId); }
        final boolean isMultiDisbursal = loanProduct.isMultiDisburseLoan();
        LocalDate expectedDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed("expectedDisbursementDate", element);
        final List<GroupLoanIndividualMonitoring> glimList = new ArrayList<>();

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
            JsonArray clientMemberJsonArray = null;
            if (topLevelJsonElement.has(LoanApiConstants.clientMembersParamName)
                    && topLevelJsonElement.get(LoanApiConstants.clientMembersParamName).isJsonArray()) {
                clientMemberJsonArray = topLevelJsonElement.get(LoanApiConstants.clientMembersParamName).getAsJsonArray();
            }
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();

                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    final Integer chargeTimeType = this.fromApiJsonHelper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale);
                    final Integer chargeCalculationType = this.fromApiJsonHelper.extractIntegerNamed("chargeCalculationType",
                            loanChargeElement, locale);
                    final LocalDate dueDate = this.fromApiJsonHelper.extractLocalDateNamed("dueDate", loanChargeElement, dateFormat,
                            locale);
                    final Integer chargePaymentMode = this.fromApiJsonHelper.extractIntegerNamed("chargePaymentMode", loanChargeElement,
                            locale);

                    final Charge chargeDefinition = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                    if (chargeDefinition.getChargeCalculation().equals(ChargeCalculationType.SLAB_BASED.getValue())) {
                        for (final ChargeSlab slab : chargeDefinition.getSlabs()) {
                            final List<ChargeSlab> subSlabs = this.chargeSlabRepository.getSubSlabsBySlabId(slab.getId());
                            if (subSlabs != null && !subSlabs.isEmpty()) {
                                slab.setSubSlabs(subSlabs);
                            }
                        }
                    }

                    if (chargeDefinition.isOverdueInstallment()) {

                        final String defaultUserMessage = "Installment charge cannot be added to the loan.";
                        throw new LoanChargeCannotBeAddedException("loanCharge", "overdue.charge", defaultUserMessage, null,
                                chargeDefinition.getName());
                    }

                    ChargeTimeType chargeTime = null;
                    if (chargeTimeType != null) {
                        chargeTime = ChargeTimeType.fromInt(chargeTimeType);
                    }
                    ChargeCalculationType chargeCalculation = null;
                    if (chargeCalculationType != null) {
                        chargeCalculation = ChargeCalculationType.fromInt(chargeCalculationType);
                    }

                    if (id == null) {

                        final String loanTypeStr = this.fromApiJsonHelper.extractStringNamed("loanType", element);

                        if (loanTypeStr.equals(LoanApiConstants.GLIM)) {
                            validateUpfrontChargesAmount(loanChargeElement, chargeDefinition, chargeCalculation, locale,
                                    numberOfRepayments);
                        } else {
                            validateValueFallsInSlab(principal, chargeDefinition, chargeCalculation, numberOfRepayments);
                        }

                        ChargePaymentMode chargePaymentModeEnum = null;
                        if (chargePaymentMode != null) {
                            chargePaymentModeEnum = ChargePaymentMode.fromInt(chargePaymentMode);
                        }
                        if (!isMultiDisbursal) {
                            if (clientMemberJsonArray != null && clientMemberJsonArray.size() > 0) {
                                final List<GroupLoanIndividualMonitoringCharge> glimCharges = new ArrayList<GroupLoanIndividualMonitoringCharge>();
                                BigDecimal totalFee = BigDecimal.ZERO;
                                totalFee = getTotalChargeAmountForGlim(clientMemberJsonArray, chargeId, amount, totalFee);
                                final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount,
                                        chargeTime, chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments, null,
                                        glimCharges, totalFee);
                                loanCharges.add(loanCharge);
                            } else {
                                final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount,
                                        chargeTime, chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                                loanCharges.add(loanCharge);
                            }
                        } else {
                            if (topLevelJsonElement.has("disbursementData") && topLevelJsonElement.get("disbursementData").isJsonArray()) {
                                final JsonArray disbursementArray = topLevelJsonElement.get("disbursementData").getAsJsonArray();
                                if (disbursementArray.size() > 0) {
                                    final JsonObject disbursementDataElement = disbursementArray.get(0).getAsJsonObject();
                                    expectedDisbursementDate = this.fromApiJsonHelper.extractLocalDateNamed(
                                            LoanApiConstants.disbursementDateParameterName, disbursementDataElement, dateFormat, locale);
                                }
                            }

                            if (ChargeTimeType.DISBURSEMENT.getValue().equals(chargeDefinition.getChargeTimeType())) {
                                for (final LoanDisbursementDetails disbursementDetail : disbursementDetails) {
                                    LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = null;
                                    if (chargeDefinition.isPercentageOfApprovedAmount()
                                            && disbursementDetail.expectedDisbursementDateAsLocalDate().equals(expectedDisbursementDate)) {
                                        final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount,
                                                chargeTime, chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                                        loanCharges.add(loanCharge);
                                        if (loanCharge.getTaxGroup() != null) {
                                            loanCharge.createLoanChargeTaxDetails(expectedDisbursementDate, loanCharge.amount());
                                        }
                                        if (loanCharge.isTrancheDisbursementCharge()) {
                                            loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,
                                                    disbursementDetail);
                                            loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                        }
                                    } else {
                                        if (disbursementDetail.expectedDisbursementDateAsLocalDate().equals(expectedDisbursementDate)) {
                                            final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition,
                                                    disbursementDetail.principal(), amount, chargeTime, chargeCalculation,
                                                    disbursementDetail.expectedDisbursementDateAsLocalDate(), chargePaymentModeEnum,
                                                    numberOfRepayments);
                                            loanCharges.add(loanCharge);
                                            if (loanCharge.getTaxGroup() != null) {
                                                loanCharge.createLoanChargeTaxDetails(expectedDisbursementDate, loanCharge.amount());
                                            }
                                            if (loanCharge.isTrancheDisbursementCharge()) {
                                                loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,
                                                        disbursementDetail);
                                                loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                            }
                                        }
                                    }
                                }
                            } else if (ChargeTimeType.TRANCHE_DISBURSEMENT.getValue().equals(chargeDefinition.getChargeTimeType())) {
                                LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = null;
                                for (final LoanDisbursementDetails disbursementDetail : disbursementDetails) {
                                    if (ChargeTimeType.TRANCHE_DISBURSEMENT.getValue().equals(chargeDefinition.getChargeTimeType())) {
                                        final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition,
                                                disbursementDetail.principal(), amount, chargeTime, chargeCalculation,
                                                disbursementDetail.expectedDisbursementDateAsLocalDate(), chargePaymentModeEnum,
                                                numberOfRepayments);
                                        loanCharges.add(loanCharge);
                                        if (loanCharge.getTaxGroup() != null) {
                                            loanCharge.createLoanChargeTaxDetails(expectedDisbursementDate, loanCharge.amount());
                                        }
                                        loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge, disbursementDetail);
                                        loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                                    }
                                }
                            } else {
                                final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount,
                                        chargeTime, chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                                loanCharges.add(loanCharge);
                                if (loanCharge.getTaxGroup() != null) {
                                    loanCharge.createLoanChargeTaxDetails(expectedDisbursementDate, loanCharge.amount());
                                }
                            }
                        }
                    } else {
                        final Long loanChargeId = id;
                        final LoanCharge loanCharge = this.loanChargeRepository.findOne(loanChargeId);
                        if (disbursementChargeIds.contains(loanChargeId) && loanCharge == null) {
                            // throw new
                            // LoanChargeNotFoundException(loanChargeId);
                        }
                        if (loanCharge != null) {
                            final String loanTypeStr = this.fromApiJsonHelper.extractStringNamed("loanType", element);
                            if (loanTypeStr.equals(LoanApiConstants.GLIM)) {
                                validateUpfrontChargesAmount(loanChargeElement, chargeDefinition, chargeCalculation, locale,
                                        numberOfRepayments);
                            } else {
                                validateValueFallsInSlab(principal, loanCharge.getCharge(), null, numberOfRepayments);
                            }
                            if (!isMultiDisbursal && loanCharge.isInstalmentFee()
                                    && loanCharge.getCharge().isPercentageOfDisbursementAmount()) {
                                if (clientMemberJsonArray != null && clientMemberJsonArray.size() > 0) {
                                    BigDecimal totalFee = BigDecimal.ZERO;
                                    totalFee = getTotalChargeAmountForGlim(clientMemberJsonArray, chargeId, amount, totalFee);
                                    loanCharge.update(amount, dueDate, loanCharge.getLoan().getPrincpal().getAmount(), numberOfRepayments,
                                            totalFee);
                                }
                            } else {
                                loanCharge.update(amount, dueDate, numberOfRepayments);
                            }
                            loanCharges.add(loanCharge);
                        }
                    }
                }
            }
        }
        return loanCharges;
    }

    private void validateUpfrontChargesAmount(final JsonObject loanChargeElement, final Charge chargeDefinition,
            final ChargeCalculationType chargeCalculation, final Locale locale, final Integer numberOfRepayment) {
        if (loanChargeElement.has("upfrontChargesAmount")) {
            final JsonArray upfrontChargesAmountArray = loanChargeElement.get("upfrontChargesAmount").getAsJsonArray();
            for (int j = 0; j < upfrontChargesAmountArray.size(); j++) {
                final JsonObject upfrontChargesAmountObject = upfrontChargesAmountArray.get(j).getAsJsonObject();
                final boolean isClientSelected = this.fromApiJsonHelper.extractBooleanNamed("isClientSelected", upfrontChargesAmountObject);
                if (isClientSelected && upfrontChargesAmountObject.has("transactionAmount")) {
                    final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed("transactionAmount",
                            upfrontChargesAmountObject, locale);
                    validateValueFallsInSlab(transactionAmount, chargeDefinition, chargeCalculation, numberOfRepayment);
                }
            }
        }

    }

    private void validateValueFallsInSlab(final BigDecimal principal, final Charge chargeDefinition,
            ChargeCalculationType chargeCalculation, final Integer numberOfRepayment) {
        if (chargeDefinition != null) {
            if (chargeCalculation == null) {
                chargeCalculation = ChargeCalculationType.fromInt(chargeDefinition.getChargeCalculation().intValue());
            }
            if (chargeCalculation.isSlabBased()) {
                final List<ChargeSlab> chargeSlabs = chargeDefinition.getSlabs();
                boolean isValueFallsInSlab = false;
                for (final ChargeSlab chargeSlab : chargeSlabs) {
                    BigDecimal slabValue = principal;
                    if (chargeSlab.getType().equals(SlabChargeType.INSTALLMENT_NUMBER.getValue())) {
                        slabValue = new BigDecimal(numberOfRepayment);
                    }
                    isValueFallsInSlab = chargeSlab.isValueFallsInSlab(slabValue);
                    if (isValueFallsInSlab) {
                        break;
                    }
                }
                if (!isValueFallsInSlab) { throw new ChargeNotInSlabExpection(principal.toString()); }
            }
        }
    }

    private BigDecimal getTotalChargeAmountForGlim(final JsonArray clientMemberJsonArray, final Long chargeId, final BigDecimal amount,
            BigDecimal totalFee) {
        for (final JsonElement jsonElement : clientMemberJsonArray) {
            final JsonObject jsonCharge = jsonElement.getAsJsonObject();
            if (jsonCharge.has(LoanApiConstants.isClientSelectedParamName)
                    && jsonCharge.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean()) {
                final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                final BigDecimal clientAmount = jsonCharge.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal();
                final BigDecimal feeCharge = GroupLoanIndividualMonitoringAssembler.percentageOf(clientAmount, amount);
                BigDecimal totalChargeAmount = feeCharge;
                final TaxGroup taxGroup = charge.getTaxGroup();
                BigDecimal totalTaxPercentage = BigDecimal.ZERO;
                if (taxGroup != null) {
                    final List<TaxGroupMappings> taxGroupMappings = taxGroup.getTaxGroupMappings();
                    for (final TaxGroupMappings taxGroupMapping : taxGroupMappings) {
                        totalTaxPercentage = totalTaxPercentage.add(taxGroupMapping.getTaxComponent().getPercentage());
                    }
                    totalChargeAmount = (feeCharge.add(GroupLoanIndividualMonitoringAssembler.percentageOf(feeCharge, totalTaxPercentage)));
                    totalChargeAmount = BigDecimal.valueOf(Math.round(Double.valueOf("" + totalChargeAmount)));
                }
                totalFee = totalFee.add(totalChargeAmount);

            }

        }
        return totalFee;
    }

    public Set<LoanTrancheCharge> getNewLoanTrancheCharges(final JsonElement element) {
        final Set<LoanTrancheCharge> associatedChargesForLoan = new HashSet<>();
        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();
                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    if (id == null) {
                        final Charge chargeDefinition = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                        if (chargeDefinition.getChargeTimeType().equals(ChargeTimeType.TRANCHE_DISBURSEMENT.getValue())) {
                            final LoanTrancheCharge trancheCharge = LoanTrancheCharge.createLoanTrancheCharge(chargeDefinition, amount);
                            associatedChargesForLoan.add(trancheCharge);
                        }
                    }
                }
            }
        }
        return associatedChargesForLoan;
    }
}