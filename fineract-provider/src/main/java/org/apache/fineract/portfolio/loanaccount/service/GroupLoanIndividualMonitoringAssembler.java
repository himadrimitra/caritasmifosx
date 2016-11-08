/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GlimChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringChargeRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.exception.ClientAlreadyWriteOffException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;
import org.apache.fineract.portfolio.tax.domain.TaxGroupMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class GroupLoanIndividualMonitoringAssembler {

    private final CodeValueRepository codeValueRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final ChargeRepositoryWrapper chargeRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final GroupLoanIndividualMonitoringRepositoryWrapper glimRepositoryWrapper;
    private final GroupLoanIndividualMonitoringChargeRepositoryWrapper glimChrageRepository;

    @Autowired
    public GroupLoanIndividualMonitoringAssembler(CodeValueRepository codeValueRepository, final ClientRepositoryWrapper clientRepository,
            final ChargeRepositoryWrapper chargeRepository, final FromJsonHelper fromApiJsonHelper,
            final GroupLoanIndividualMonitoringRepositoryWrapper glimRepositoryWrapper,
            final GroupLoanIndividualMonitoringChargeRepositoryWrapper glimChrageRepository) {
        this.codeValueRepository = codeValueRepository;
        this.clientRepository = clientRepository;
        this.chargeRepository = chargeRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.glimRepositoryWrapper = glimRepositoryWrapper;
        this.glimChrageRepository = glimChrageRepository;
    }

    public List<GroupLoanIndividualMonitoring> assembleGlimFromJson(final JsonCommand command, boolean isRecoveryPayment) {
        List<GroupLoanIndividualMonitoring> glimMembers = new ArrayList<>();
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                Long glimId = member.get(LoanApiConstants.idParameterName).getAsLong();
                GroupLoanIndividualMonitoring glim = this.glimRepositoryWrapper.findOneWithNotFoundDetection(glimId);
                if (member.has(LoanApiConstants.transactionAmountParamName)) {
                    BigDecimal transactionAmount = member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal();
                    if (MathUtility.isGreaterThanZero(glim.getPrincipalWrittenOffAmount())
                            && MathUtility.isGreaterThanZero(transactionAmount) && !isRecoveryPayment) { throw new ClientAlreadyWriteOffException(); }
                    glim.updateTransactionAmount(transactionAmount);
                }
                Boolean isClientSelected = member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean();
                if (isClientSelected) {
                    glimMembers.add(glim);
                }
            }
        }
        return glimMembers;
    }

    public List<GroupLoanIndividualMonitoring> updateFromJson(final JsonElement element, final String amountType, Loan loan,
            int numberOfRepayments, final BigDecimal interestRate) {
        final List<GroupLoanIndividualMonitoring> glimList = new ArrayList<>();
        if (this.fromApiJsonHelper.parameterExists(LoanApiConstants.clientMembersParamName, element)) {
            JsonArray clientMembers = this.fromApiJsonHelper.extractJsonArrayNamed(LoanApiConstants.clientMembersParamName, element);
            HashMap<Long, BigDecimal> chargesMap = new HashMap<>();
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                BigDecimal amount = null;
                BigDecimal percentage = BigDecimal.ZERO;
                BigDecimal totalInterest = BigDecimal.ZERO;
                Boolean isClientSelected = member.has(LoanApiConstants.isClientSelectedParamName)
                        && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean();
                Long glimId = member.get(LoanApiConstants.glimIdParamName).getAsLong();
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.glimRepositoryWrapper
                        .findOneWithNotFoundDetection(glimId);
                groupLoanIndividualMonitoring.setIsClientSelected(isClientSelected);
                if (isClientSelected) {
                    if (member.has(LoanApiConstants.amountParamName)) {
                        amount = member.get(LoanApiConstants.amountParamName).getAsBigDecimal();
                    }
                    if (member.has(LoanApiConstants.loanPurposeIdParamName)) {
                        Long loanPurposeId = member.get(LoanApiConstants.loanPurposeIdParamName).getAsLong();
                        CodeValue loanPurpose = this.codeValueRepository.findOne(loanPurposeId);
                        groupLoanIndividualMonitoring.setLoanPurpose(loanPurpose);
                    }
                    if (amountType.equalsIgnoreCase("proposedAmount")) {
                        groupLoanIndividualMonitoring.setProposedAmount(amount);
                    } else if (amountType.equalsIgnoreCase("approvedAmount")) {
                        groupLoanIndividualMonitoring.setApprovedAmount(amount);
                    } else {
                        groupLoanIndividualMonitoring.setDisbursedAmount(amount);
                    }

                    // total interest calculation
                    totalInterest = calculateTotalInterest(interestRate, numberOfRepayments, amount);
                    groupLoanIndividualMonitoring.setInterestAmount(totalInterest);

                    // calculate percentage of principal amount
                    percentage = calculatePercentageOfAmount(element, amount, percentage);
                    groupLoanIndividualMonitoring.updatePercentage(percentage);
                    Set<GroupLoanIndividualMonitoringCharge> glimCharges = groupLoanIndividualMonitoring
                            .getGroupLoanIndividualMonitoringCharges();
                    recalculateTotalFeeCharges(loan, chargesMap, amount, glimCharges);
                    glimList.add(groupLoanIndividualMonitoring);
                }

            }
            // update the recalculated charge amounts in loan charge obj
            updateLoanChargesForGlim(loan, chargesMap);
        }
        return glimList;
    }

    public void updateLoanChargesForGlim(Loan loan, HashMap<Long, BigDecimal> chargesMap) {
        for (LoanCharge loancharge : loan.charges()) {
            if (!ChargeTimeType.fromInt(loancharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                BigDecimal recalculatedChargeAmount = chargesMap.get(loancharge.getCharge().getId());
                loancharge.updateAmount(recalculatedChargeAmount);
            }
        }
    }

    public void recalculateTotalFeeCharges(Loan loan, HashMap<Long, BigDecimal> chargesMap, BigDecimal amount,
            Set<GroupLoanIndividualMonitoringCharge> glimCharges) {
        Integer gimChargeCalculation = 0;
        for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
            if (glimCharge.getCharge().isEmiRoundingGoalSeek()) {
                gimChargeCalculation = glimCharge.getCharge().getGlimChargeCalculation();
                break;
            }
        }

        for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
            glimCharge.setWaivedChargeAmount(BigDecimal.ZERO);
            glimCharge.setPaidCharge(BigDecimal.ZERO);
            BigDecimal feeCharge = glimCharge.getFeeAmount();
            if(glimCharge.getCharge().isPercentageBased()) {
                feeCharge = percentageOf(amount, glimCharge.getCharge().getAmount());
            }
            BigDecimal totalChargeAmount = feeCharge;
            if (!ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                final BigDecimal minCap = glimCharge.getCharge().getMinCap();
                final BigDecimal maxCap = applyTaxComponentsOnCapping(glimCharge.getCharge().getMaxCap(), glimCharge.getCharge());
                totalChargeAmount = applyTaxComponentsOnCharges(glimCharge.getCharge(), feeCharge, totalChargeAmount);
                if (!glimCharge.getCharge().isEmiRoundingGoalSeek()) {
                    totalChargeAmount = checkForMaxFee(totalChargeAmount, maxCap, gimChargeCalculation);
                }
                totalChargeAmount = minimumAndMaximumCap(totalChargeAmount, minCap, maxCap, loan);
            }
            
            glimCharge.setFeeAmount(totalChargeAmount);
            if (chargesMap.containsKey(glimCharge.getCharge().getId())) {
                chargesMap.put(glimCharge.getCharge().getId(), chargesMap.get(glimCharge.getCharge().getId()).add(totalChargeAmount));
            } else {
                chargesMap.put(glimCharge.getCharge().getId(), totalChargeAmount);
            }
        }
    }

    public List<GroupLoanIndividualMonitoring> createOrUpdateIndividualClientsAmountSplit(Loan newLoanApplication,
            final JsonElement element, BigDecimal interestRate, Integer numberOfRepayment) {
        List<GroupLoanIndividualMonitoring> glimList = new ArrayList<>();
        if (this.fromApiJsonHelper.parameterExists(LoanApiConstants.clientMembersParamName, element)) {
            JsonArray clientMembers = this.fromApiJsonHelper.extractJsonArrayNamed(LoanApiConstants.clientMembersParamName, element);
            createGlimAndGlimCharges(newLoanApplication, element, interestRate, numberOfRepayment, glimList, clientMembers);
            if (newLoanApplication != null) {
                Set<LoanCharge> loanCharges = newLoanApplication.charges();
                adjustRoundOffValuesToApplicableCharges(loanCharges, numberOfRepayment, glimList);
            }
        }
        return glimList;
    }

    public void adjustRoundOffValuesToApplicableCharges(Set<LoanCharge> loanCharges, Integer numberOfRepayment,
            List<GroupLoanIndividualMonitoring> glimList) {
        Map<Long, BigDecimal> chargesMap = new HashMap<>();
        for (final GroupLoanIndividualMonitoring glim : glimList) {
            if (glim.isClientSelected()) {
                BigDecimal totalCharge = BigDecimal.ZERO;
                BigDecimal emiAmount = BigDecimal.ZERO;
                BigDecimal recalculateEmiAmount = BigDecimal.ZERO;
                BigDecimal totalPaybleAmount = BigDecimal.ZERO;
                BigDecimal totalAmountBeforeAdjustment = BigDecimal.ZERO;
                BigDecimal revisedTotalFee = BigDecimal.ZERO;
                Set<GroupLoanIndividualMonitoringCharge> clientCharges = glim.getGroupLoanIndividualMonitoringCharges();
                BigDecimal upfrontFeeAmount = BigDecimal.ZERO;
                for (GroupLoanIndividualMonitoringCharge glimCharge : clientCharges) {
                    glimCharge.setGlim(glim);
                    if (ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                        totalCharge = totalCharge.add(BigDecimal.ZERO);
                        upfrontFeeAmount = upfrontFeeAmount.add(glimCharge.getFeeAmount());
                    } else {
                        totalCharge = totalCharge.add(glimCharge.getFeeAmount());
                    }

                }
                glim.setChargeAmount(totalCharge);
                // adjust total amount
                if (glim.getDisbursedAmount() != null) {
                    totalAmountBeforeAdjustment = glim.getDisbursedAmount().add(glim.getInterestAmount()).add(totalCharge);
                } else if (glim.getApprovedAmount() != null) {
                    totalAmountBeforeAdjustment = glim.getApprovedAmount().add(glim.getInterestAmount()).add(totalCharge);
                } else {
                    totalAmountBeforeAdjustment = glim.getProposedAmount().add(glim.getInterestAmount()).add(totalCharge);
                }
                BigDecimal beforeRoundingEmiAmount = BigDecimal.valueOf((totalAmountBeforeAdjustment.doubleValue() / numberOfRepayment
                        .doubleValue()));

                if(upfrontFeeAmount.compareTo(BigDecimal.ZERO) == 1) {
                    emiAmount = BigDecimal.valueOf(Math.round(totalAmountBeforeAdjustment.doubleValue() / numberOfRepayment.doubleValue()));
                    glim.setInstallmentAmount(emiAmount);
                    glim.setTotalPaybleAmount(totalAmountBeforeAdjustment.add(upfrontFeeAmount));
                } else {
                    emiAmount = BigDecimal.valueOf(Math.ceil(totalAmountBeforeAdjustment.doubleValue() / numberOfRepayment.doubleValue()));
                    glim.setInstallmentAmount(emiAmount);
                    totalPaybleAmount = BigDecimal.valueOf(emiAmount.multiply(BigDecimal.valueOf(numberOfRepayment)).doubleValue());
                    glim.setTotalPaybleAmount(totalPaybleAmount);
                }
                

                // adjust rounding off amount to applicable charge
                for (GroupLoanIndividualMonitoringCharge glimCharge : clientCharges) {
                    if (glimCharge.getCharge().isEmiRoundingGoalSeek() && !ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType()).isUpfrontFee()) {
                        BigDecimal differenceAmount = totalPaybleAmount.subtract(totalAmountBeforeAdjustment);
                        revisedTotalFee = glimCharge.getFeeAmount().add(differenceAmount);
                        /*
                         * if diff amount is ZERO and max cap is equal to
                         * revisedTotalFee then then substract one from EMI then
                         * multiply with no of installments get diff amount add
                         * to revised fee
                         */
                        BigDecimal capAmount = glimCharge.getCharge().getMaxCap() != null ? glimCharge.getCharge().getMaxCap()
                                : BigDecimal.ZERO;
                        BigDecimal maxCap = BigDecimal.valueOf(Math.ceil(capAmount.doubleValue()));
                        maxCap = applyTaxComponentsOnCapping(maxCap, glimCharge.getCharge());
                        if (revisedTotalFee.compareTo(maxCap) > 0 && revisedTotalFee.compareTo(BigDecimal.valueOf(Math.ceil(maxCap.doubleValue()))) == 0) {
                            recalculateEmiAmount = emiAmount.subtract(BigDecimal.ONE);
                            glim.setInstallmentAmount(recalculateEmiAmount);
                            totalPaybleAmount = BigDecimal.valueOf(recalculateEmiAmount.multiply(BigDecimal.valueOf(numberOfRepayment))
                                    .doubleValue());
                            glim.setTotalPaybleAmount(totalPaybleAmount);
                            differenceAmount = totalPaybleAmount.subtract(totalAmountBeforeAdjustment);
                            revisedTotalFee = glimCharge.getFeeAmount().add(differenceAmount);
                        } else if (revisedTotalFee.compareTo(maxCap) == 1) {
                            emiAmount = BigDecimal.valueOf(Math.floor(beforeRoundingEmiAmount.doubleValue()));
                            glim.setInstallmentAmount(emiAmount);
                            totalPaybleAmount = BigDecimal.valueOf(emiAmount.multiply(BigDecimal.valueOf(numberOfRepayment)).doubleValue());
                            glim.setTotalPaybleAmount(totalPaybleAmount);
                            differenceAmount = totalPaybleAmount.subtract(totalAmountBeforeAdjustment);
                            revisedTotalFee = glimCharge.getFeeAmount().add(differenceAmount);
                        }
                        totalCharge = totalCharge.add(differenceAmount);
                        glimCharge.setRevisedFeeAmount(BigDecimal.valueOf(Math.round(revisedTotalFee.doubleValue())));
                    }
                    if (chargesMap.containsKey(glimCharge.getCharge().getId())) {
                        chargesMap.put(
                                glimCharge.getCharge().getId(),
                                chargesMap.get(glimCharge.getCharge().getId()).add(
                                        glimCharge.getRevisedFeeAmount() == null ? glimCharge.getFeeAmount() : glimCharge
                                                .getRevisedFeeAmount()));
                    } else {
                        chargesMap.put(glimCharge.getCharge().getId(), glimCharge.getRevisedFeeAmount() == null ? glimCharge.getFeeAmount()
                                : glimCharge.getRevisedFeeAmount());
                    }
                }
                // add upfront fee amount
                if (upfrontFeeAmount.compareTo(BigDecimal.ZERO) == 1) {
                    glim.setChargeAmount(BigDecimal.valueOf(Math.round(upfrontFeeAmount.doubleValue())));
                } else {
                    glim.setChargeAmount(BigDecimal.valueOf(Math.round(totalCharge.doubleValue())));
                }
            }
        }
        for (LoanCharge loanCharge : loanCharges) {
            if (!ChargeTimeType.fromInt(loanCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                BigDecimal totalAmount = chargesMap.get(loanCharge.getCharge().getId());
                loanCharge.updateAmount(totalAmount);
            }
        }
    }

    public void createGlimAndGlimCharges(Loan newLoanApplication, final JsonElement element, BigDecimal interestRate,
            Integer numberOfRepayment, List<GroupLoanIndividualMonitoring> glimList, JsonArray clientMembers) {
        for (JsonElement clientMember : clientMembers) {
            JsonObject member = clientMember.getAsJsonObject();
            Boolean isClientSelected = member.has(LoanApiConstants.isClientSelectedParamName)
                    && member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean();
            BigDecimal amount = BigDecimal.ZERO;
            if (member.has(LoanApiConstants.amountParamName)) {
                amount = member.get(LoanApiConstants.amountParamName).getAsBigDecimal();
            }
            Long glimClientId = member.get(LoanApiConstants.idParamName).getAsLong();
            Client client = this.clientRepository.getActiveClientInUserScope(glimClientId);
            CodeValue loanPurpose = null;
            if (member.has(LoanApiConstants.loanPurposeIdParamName)) {
                Long loanPurposeId = member.get(LoanApiConstants.loanPurposeIdParamName).getAsLong();
                loanPurpose = this.codeValueRepository.findOne(loanPurposeId);
            }
            final BigDecimal adjustedAmount = BigDecimal.ZERO;
            final BigDecimal installmentAmount = BigDecimal.ZERO;
            final BigDecimal totalPaybleAmount = BigDecimal.ZERO;
            final BigDecimal paidInterestAmount = BigDecimal.ZERO;
            final BigDecimal paidAmount = BigDecimal.ZERO;
            BigDecimal percentage = BigDecimal.ZERO;
            final BigDecimal paidPrincipalAmount = BigDecimal.ZERO;
            final BigDecimal paidChargeAmount = BigDecimal.ZERO;
            final BigDecimal waivedInterestAmount = BigDecimal.ZERO;
            final BigDecimal waivedChargeAmount = BigDecimal.ZERO;
            BigDecimal totalInterest = BigDecimal.ZERO;
            Set<GroupLoanIndividualMonitoringCharge> clientCharges = new HashSet<>();

            if (MathUtility.isGreaterThanZero(amount)) {
                // charges calculation
                calculateTotalCharges(newLoanApplication, element, amount, client, clientCharges);

                // total interest calculation
                totalInterest = calculateTotalInterest(interestRate, numberOfRepayment, amount);

                // calculate percentage of principal amount
                percentage = calculatePercentageOfAmount(element, amount, percentage);
            }

            GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = GroupLoanIndividualMonitoring.createInstance(newLoanApplication,
                    client, amount, null, null, loanPurpose, isClientSelected, adjustedAmount, installmentAmount, totalPaybleAmount,
                    paidInterestAmount, paidAmount, totalInterest, clientCharges, percentage, paidPrincipalAmount, paidChargeAmount,
                    waivedInterestAmount, waivedChargeAmount);
            glimList.add(groupLoanIndividualMonitoring);
        }
    }

    private void calculateTotalCharges(Loan newLoanApplication, final JsonElement element, BigDecimal proposedAmount, Client client,
            Set<GroupLoanIndividualMonitoringCharge> clientCharges) {
        if (this.fromApiJsonHelper.parameterExists("charges", element)) {
            JsonArray charges = this.fromApiJsonHelper.extractJsonArrayNamed("charges", element);
            Integer gimChargeCalculation = 0;
            for (JsonElement jsonElement : charges) {
                final JsonObject jsonCharge = jsonElement.getAsJsonObject();
                final Long chargeId = jsonCharge.get("chargeId").getAsLong();
                Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                if (charge.isEmiRoundingGoalSeek()) {
                    gimChargeCalculation = charge.getGlimChargeCalculation();
                    break;
                }
            }
            for (JsonElement jsonElement : charges) {
                final JsonObject jsonCharge = jsonElement.getAsJsonObject();
                final Long chargeId = jsonCharge.get("chargeId").getAsLong();
                final BigDecimal chargeAmount = jsonCharge.get("amount").getAsBigDecimal();
                JsonArray upfrontCharges = this.fromApiJsonHelper.extractJsonArrayNamed(LoanApiConstants.upfrontChargesAmountParamName, jsonCharge); 
                Charge charge = this.chargeRepository.findOneWithNotFoundDetection(chargeId);
                BigDecimal feeCharge = chargeAmount;
                BigDecimal totalChargeAmount = BigDecimal.ZERO;
                if (charge.isPercentageBased()) {
                    feeCharge = percentageOf(proposedAmount, chargeAmount);
                }
                if (ChargeTimeType.fromInt(charge.getChargeTimeType().intValue()).isUpfrontFee()) {
                    totalChargeAmount = getTotalUpfrontFeeAmount(client, upfrontCharges, totalChargeAmount);
                } else {
                    totalChargeAmount = feeCharge;
                    final BigDecimal minCap = charge.getMinCap();
                    final BigDecimal maxCap = applyTaxComponentsOnCapping(charge.getMaxCap(), charge);
                    totalChargeAmount = applyTaxComponentsOnCharges(charge, feeCharge, totalChargeAmount);
                    if (!charge.isEmiRoundingGoalSeek()) {
                        totalChargeAmount = checkForMaxFee(totalChargeAmount, maxCap, gimChargeCalculation);
                    }
                    totalChargeAmount = minimumAndMaximumCap(totalChargeAmount, minCap, maxCap, newLoanApplication);
                }
                
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = null;
                BigDecimal revisedFeeAmount = null;
                BigDecimal waivedChargeAmount = null;
                BigDecimal paidCharge = null;
                GroupLoanIndividualMonitoringCharge glimCharge = GroupLoanIndividualMonitoringCharge.instance(
                        groupLoanIndividualMonitoring, client, charge, totalChargeAmount, revisedFeeAmount, charge.isEmiRoundingGoalSeek(),
                        waivedChargeAmount, paidCharge);
                clientCharges.add(glimCharge);
            }
        }
    }

    private BigDecimal getTotalUpfrontFeeAmount(Client client, JsonArray upfrontCharges, BigDecimal totalChargeAmount) {
        for (JsonElement jsonObject : upfrontCharges) {
            JsonObject obj = jsonObject.getAsJsonObject();
            Long clientId = obj.get("id").getAsLong();
            if (clientId.equals(client.getId())) {
                totalChargeAmount = totalChargeAmount.add(obj.get("upfrontChargeAmount").getAsBigDecimal());
            }
        }
        return totalChargeAmount;
    }

    public BigDecimal checkForMaxFee(BigDecimal amount, BigDecimal maxCapIncludingVat, Integer glimChargeCalculation) {

        BigDecimal roundVal = BigDecimal.valueOf(Math.round(amount.doubleValue()));
        BigDecimal totalCharge = BigDecimal.ZERO;
        if (maxCapIncludingVat != null) {
            if (glimChargeCalculation == GlimChargeCalculationType.ROUND.getValue()) {
                totalCharge = roundVal;
            } else if (glimChargeCalculation == GlimChargeCalculationType.ROUND_WITHOUT_MAX_CHARGE.getValue()) {
                BigDecimal minVal = getMin(roundVal, BigDecimal.valueOf(Math.round(maxCapIncludingVat.doubleValue())));
                totalCharge = getMin(minVal, roundVal);
            } else {
                BigDecimal maxFee = BigDecimal.valueOf(Math.floor(getMin(amount, maxCapIncludingVat).doubleValue()));
                BigDecimal minVal = getMin(roundVal, BigDecimal.valueOf(Math.round(maxCapIncludingVat.doubleValue())));
                totalCharge = getMin(minVal, maxFee);
            }
        } else {
            totalCharge = roundVal;
        }
        return totalCharge;
    }

    public static BigDecimal getMin(BigDecimal amount1, BigDecimal amount2) {
        return amount1.compareTo(amount2) > 0 ? amount2 : amount1;
    }

    private BigDecimal applyTaxComponentsOnCapping(BigDecimal cappingAmountWithoutTax, Charge charge) {
        TaxGroup taxGroup = charge.getTaxGroup();
        BigDecimal totalTaxPercentage = BigDecimal.ZERO;
        if (taxGroup != null) {
            List<TaxGroupMappings> taxGroupMappings = taxGroup.getTaxGroupMappings();
            for (TaxGroupMappings taxGroupMapping : taxGroupMappings) {
                totalTaxPercentage = totalTaxPercentage.add(taxGroupMapping.getTaxComponent().getPercentage());
            }
            if (cappingAmountWithoutTax != null) {
                cappingAmountWithoutTax = (cappingAmountWithoutTax.add(percentageOf(cappingAmountWithoutTax, totalTaxPercentage)));
            }
        }
        return cappingAmountWithoutTax;
    }

    private BigDecimal applyTaxComponentsOnCharges(Charge charge, BigDecimal feeCharge, BigDecimal totalChargeAmount) {
        TaxGroup taxGroup = charge.getTaxGroup();
        BigDecimal totalTaxPercentage = BigDecimal.ZERO;
        if (taxGroup != null) {
            List<TaxGroupMappings> taxGroupMappings = taxGroup.getTaxGroupMappings();
            for (TaxGroupMappings taxGroupMapping : taxGroupMappings) {
                totalTaxPercentage = totalTaxPercentage.add(taxGroupMapping.getTaxComponent().getPercentage());
            }
            totalChargeAmount = (feeCharge.add(percentageOf(feeCharge, totalTaxPercentage)));
            if (charge.isEmiRoundingGoalSeek()) {
                totalChargeAmount = BigDecimal.valueOf(Math.round(Double.valueOf("" + totalChargeAmount)));
            }

        }
        return totalChargeAmount;
    }

    private BigDecimal calculatePercentageOfAmount(final JsonElement element, BigDecimal proposedAmount, BigDecimal percentage) {
        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
        if (principal != null) {
            percentage = BigDecimal.valueOf(proposedAmount.doubleValue() / principal.doubleValue());
            percentage = percentage.multiply(BigDecimal.valueOf(100));
        }
        return percentage;
    }

    private BigDecimal calculateTotalInterest(BigDecimal interestRate, Integer numberOfRepayment, BigDecimal proposedAmount) {
        double interest = BigDecimal.ZERO.doubleValue();
        interest = pmt(interestRate.doubleValue(), numberOfRepayment.intValue(), proposedAmount.doubleValue());
        BigDecimal totalInterest = BigDecimal.valueOf(Math.floor(interest));
        return totalInterest;
    }

    public static BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {

        BigDecimal percentageOf = BigDecimal.ZERO;
        if (isGreaterThanZero(value)) {
            final MathContext mc = new MathContext(8, MoneyHelper.getRoundingMode());
            final BigDecimal multiplicand = percentage.divide(BigDecimal.valueOf(100l), mc);
            percentageOf = value.multiply(multiplicand, mc);
        }
        return percentageOf;
    }

    private static boolean isGreaterThanZero(final BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 1;
    }

    private static double pmt(double rateOfInterest, int numberOfRepayment, double principal) {
        double fv = 0;
        int type = 0;
        return pmt(rateOfInterest, numberOfRepayment, principal, fv, type);
    }

    private static double pmt(double rateOfInterest, int numberOfRepayment, double principal, double fv, int type) {
        rateOfInterest = rateOfInterest / 12 / 100 * 1;
        double pmt = -rateOfInterest * (principal * Math.pow(1 + rateOfInterest, numberOfRepayment) + fv)
                / ((1 + rateOfInterest * type) * (Math.pow(1 + rateOfInterest, numberOfRepayment) - 1));
        pmt = -pmt * numberOfRepayment - principal;
        return pmt;
    }

    public BigDecimal calculateInstallmentAmount(BigDecimal installmentAmount, final BigDecimal totalFeeCharges,
            final Integer numberOfRepayments, MonetaryCurrency currency) {
        Money feePerInstallment = Money.zero(currency);
        if (installmentAmount != null) {
            feePerInstallment = Money.of(currency, BigDecimal.valueOf(totalFeeCharges.doubleValue() / numberOfRepayments.doubleValue()));
            installmentAmount = installmentAmount.subtract(feePerInstallment.getAmount());
        }
        return installmentAmount;
    }

    // update EMI amount for Glim Loan
    public void updateInstallmentAmountForGlim(List<GroupLoanIndividualMonitoring> glimList, LoanApplicationTerms loanApplicationTerms,
            Set<LoanCharge> charges) {
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        BigDecimal installmentAmountWithoutFee = BigDecimal.ZERO;
        Integer numberOfInstallments = loanApplicationTerms.getNumberOfRepayments();
        MonetaryCurrency currency = loanApplicationTerms.getCurrency();
        BigDecimal feeAmount = BigDecimal.ZERO;
        if (glimList != null && !glimList.isEmpty()) {
            for (GroupLoanIndividualMonitoring glim : glimList) {
                if (glim.isClientSelected()) {
                    totalInstallmentAmount = totalInstallmentAmount.add(glim.getInstallmentAmount());
                    Set<GroupLoanIndividualMonitoringCharge> glimmCharges = glim.getGroupLoanIndividualMonitoringCharges();
                    for (GroupLoanIndividualMonitoringCharge glimCharge : glimmCharges) {
                        if(!ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()){
                            BigDecimal chargeAmount = MathUtility.isNull(glimCharge.getRevisedFeeAmount()) ? glimCharge.getFeeAmount()
                                    : glimCharge.getRevisedFeeAmount();
                            feeAmount = MathUtility.add(feeAmount, MathUtility.divide(chargeAmount, numberOfInstallments, currency));
                        }
                    }
                }
            }
            installmentAmountWithoutFee = totalInstallmentAmount.subtract(feeAmount);
            if (installmentAmountWithoutFee.compareTo(BigDecimal.ZERO) == 1) {
                loanApplicationTerms.setFixedEmiAmount(installmentAmountWithoutFee);
            }
        }

    }

    // minimum And Maximum Capping on loan charge
    private BigDecimal minimumAndMaximumCap(final BigDecimal totalChargeAmount, BigDecimal minCap, BigDecimal maxCap, Loan loan) {
        BigDecimal minMaxCap = BigDecimal.ZERO;
        if (minCap != null) {
            final int minimumCap = totalChargeAmount.compareTo(minCap);
            if (minimumCap == -1) {
                minMaxCap = minCap;
                return minMaxCap;
            }
        }
        if (maxCap != null) {
            final int maximumCap = totalChargeAmount.compareTo(maxCap);
            if (maximumCap == 1) {
                // apply ceiling because to get diff amount for fee adjusting
                minMaxCap = BigDecimal.valueOf(Math.ceil(maxCap.doubleValue()));
                return minMaxCap;
            }
        }
        minMaxCap = totalChargeAmount;
        // this will round the amount value
        if (loan != null && minMaxCap != null) {
            minMaxCap = Money.of(loan.getCurrency(), minMaxCap).getAmount();
        }
        return minMaxCap;
    }

    public void updateGLIMAfterRepayment(Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions, Boolean isRecoveryRepayment) {
        List<GroupLoanIndividualMonitoring> updatedGlimList = new ArrayList<GroupLoanIndividualMonitoring>();
        List<GroupLoanIndividualMonitoringCharge> glimChargesForUpdate = new ArrayList<GroupLoanIndividualMonitoringCharge>();
        for (GroupLoanIndividualMonitoringTransaction glimTransaction : glimTransactions) {
            GroupLoanIndividualMonitoring glim = glimTransaction.getGroupLoanIndividualMonitoring();
            Map<String, BigDecimal> processedTransactionMap = glim.getProcessedTransactionMap();
            Set<GroupLoanIndividualMonitoringCharge> ch = glim.getGroupLoanIndividualMonitoringCharges();
            for (GroupLoanIndividualMonitoringCharge glimCharge : ch) {
                glimCharge.setPaidCharge(processedTransactionMap.get(glimCharge.getCharge().getId().toString()));
                glimChargesForUpdate.add(glimCharge);
            }
            glim.setPaidChargeAmount(MathUtility.add(glim.getPaidChargeAmount(), processedTransactionMap.get("processedCharge")));
            glim.setPaidInterestAmount(MathUtility.add(glim.getPaidInterestAmount(), processedTransactionMap.get("processedInterest")));
            glim.setPaidPrincipalAmount(MathUtility.add(glim.getPaidPrincipalAmount(), processedTransactionMap.get("processedPrincipal")));
            glim.setPaidAmount(MathUtility.add(glim.getTotalPaidAmount(),
                    processedTransactionMap.get("processedinstallmentTransactionAmount")));
            if (isRecoveryRepayment) {
                glim.setChargeWrittenOffAmount(MathUtility.subtract(glim.getChargeWrittenOffAmount(),
                        processedTransactionMap.get("processedCharge")));
                glim.setInterestWrittenOffAmount(MathUtility.subtract(glim.getInterestWrittenOffAmount(),
                        processedTransactionMap.get("processedInterest")));
                glim.setPrincipalWrittenOffAmount(MathUtility.subtract(glim.getPrincipalWrittenOffAmount(),
                        processedTransactionMap.get("processedPrincipal")));
            }
            updatedGlimList.add(glim);
        }
        this.glimChrageRepository.save(glimChargesForUpdate);
        this.glimRepositoryWrapper.save(updatedGlimList);
    }

    public static BigDecimal zeroIfNull(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    public Boolean isGLIMApplicableForWriteOf(List<GroupLoanIndividualMonitoring> glimMembersForStatusUpdate) {
        Boolean isWriteOf = false;
        for (GroupLoanIndividualMonitoring glimData : glimMembersForStatusUpdate) {
            BigDecimal writeOfAmount = zeroIfNull(glimData.getPrincipalWrittenOffAmount()).add(
                    zeroIfNull(glimData.getChargeWrittenOffAmount())).add(zeroIfNull(glimData.getInterestWrittenOffAmount()));
            if (writeOfAmount.compareTo(BigDecimal.ZERO) > 0) {
                isWriteOf = true;
            }
            if (glimData.isClientSelected() && glimData.getIsActive()) { return false; }
        }
        return isWriteOf;
    }
}
