/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringChargeRepository;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.exception.ClientCanNotExceedWriteOffAmount;
import org.apache.fineract.portfolio.loanaccount.exception.ClientInstallmentNotEqualToTransactionAmountException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class GroupLoanIndividualMonitoringTransactionAssembler {

    private final FromJsonHelper fromApiJsonHelper;

    private final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepositoryWrapper;

    private final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory;
    private final GroupLoanIndividualMonitoringRepository glimRepository;
    private final GroupLoanIndividualMonitoringAssembler glimAssembler;
    private final LoanRepository loanRepository;
    private final GroupLoanIndividualMonitoringChargeRepository glimChargeRepository;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionAssembler(final FromJsonHelper fromApiJsonHelper,
            final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepositoryWrapper,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory,
            final GroupLoanIndividualMonitoringRepository glimRepository, final GroupLoanIndividualMonitoringAssembler glimAssembler,
            final LoanRepository loanRepository, final GroupLoanIndividualMonitoringChargeRepository glimChargeRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.groupLoanIndividualMonitoringRepositoryWrapper = groupLoanIndividualMonitoringRepositoryWrapper;
        this.transactionProcessorFactory = transactionProcessorFactory;
        this.glimRepository = glimRepository;
        this.glimAssembler = glimAssembler;
        this.loanRepository = loanRepository;
        this.glimChargeRepository = glimChargeRepository;
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> assembleGLIMTransactions(final JsonCommand command,
            final LoanTransaction loanTransaction) {
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        BigDecimal individualTransactionAmount = BigDecimal.ZERO;
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                    .determineProcessor(loanTransaction.getLoan().transactionProcessingStrategy());
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                Long glimId = member.get(LoanApiConstants.idParameterName).getAsLong();
                individualTransactionAmount = member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal();
                if (MathUtility.isGreaterThanZero(individualTransactionAmount)) {
                    GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                            .findOneWithNotFoundDetection(glimId);
                    GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                            .instance(groupLoanIndividualMonitoring, loanTransaction, loanTransaction.getTypeOf().getValue());
                    loanRepaymentScheduleTransactionProcessor.handleGLIMRepayment(groupLoanIndividualMonitoringTransaction,
                            individualTransactionAmount);
                    glimTransactions.add(groupLoanIndividualMonitoringTransaction);
                }

            }
        }
        return glimTransactions;
    }

    public void validateGlimTransactionAmount(JsonCommand command, boolean isRecoveryRepayment) {
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(LoanApiConstants.transactionAmountParamName);
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
                if (member.get(LoanApiConstants.isClientSelectedParamName).getAsBoolean()) {
                    BigDecimal individualTransactionAmount = member.get(LoanApiConstants.transactionAmountParamName).getAsBigDecimal();
                    totalInstallmentAmount = MathUtility.add(totalInstallmentAmount, individualTransactionAmount);
                    if (isRecoveryRepayment) {
                        Long glimId = member.get(LoanApiConstants.idParameterName).getAsLong();
                        GroupLoanIndividualMonitoring glim = this.glimRepository.findOne(glimId);
                        BigDecimal writeOffAmount = MathUtility.add(glim.getPrincipalWrittenOffAmount(),
                                glim.getInterestWrittenOffAmount(), glim.getChargeWrittenOffAmount());
                        if (MathUtility.isGreaterThanZero(individualTransactionAmount.subtract(writeOffAmount))) { throw new ClientCanNotExceedWriteOffAmount(); }
                    }
                }

            }
            if (totalInstallmentAmount.compareTo(transactionAmount) != 0) { throw new ClientInstallmentNotEqualToTransactionAmountException(); }
        }
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> waiveInterestForClients(final JsonCommand command,
            final LoanTransaction loanTransaction) {
        JsonArray clients = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        final Locale locale = command.extractLocale();
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (JsonElement element : clients) {
            final Boolean isClientSelected = this.fromApiJsonHelper
                    .extractBooleanNamed(LoanApiConstants.isClientSelectedParamName, element);
            if (isClientSelected != null && isClientSelected) {
                final Long glimId = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.idParameterName, element);
                GroupLoanIndividualMonitoring groupLoanIndividualMonitoring = this.groupLoanIndividualMonitoringRepositoryWrapper
                        .findOneWithNotFoundDetection(glimId);

                final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed(
                        LoanApiConstants.transactionAmountParamName, element, locale);
                BigDecimal totalInterestOutstandingOnLoan = BigDecimal.ZERO;
                BigDecimal paidInterestAmount = groupLoanIndividualMonitoring.getPaidInterestAmount();
                BigDecimal totalInterestAmount = groupLoanIndividualMonitoring.getInterestAmount();
                BigDecimal totalWaivedInterest = groupLoanIndividualMonitoring.getWaivedInterestAmount();
                if (!MathUtility.isNull(paidInterestAmount) && !MathUtility.isNull(totalInterestAmount)) {
                    totalInterestOutstandingOnLoan = totalInterestAmount.subtract(paidInterestAmount);
                }

                if (totalInterestOutstandingOnLoan.compareTo(BigDecimal.ZERO) == 1) {
                    if (transactionAmount.compareTo(totalInterestOutstandingOnLoan) == 1) {
                        final String errorMessage = "The amount of interest to waive cannot be greater than total interest outstanding on loan.";
                        throw new InvalidLoanStateTransitionException("waive.interest", "amount.exceeds.total.outstanding.interest",
                                errorMessage, transactionAmount, totalInterestOutstandingOnLoan);
                    }
                    totalWaivedInterest = totalWaivedInterest.add(transactionAmount);
                    groupLoanIndividualMonitoring.setWaivedInterestAmount(totalWaivedInterest);
                    GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                            .waiveInterest(groupLoanIndividualMonitoring, loanTransaction, transactionAmount, loanTransaction.getTypeOf()
                                    .getValue());
                    groupLoanIndividualMonitoring.updateGlimTransaction(groupLoanIndividualMonitoringTransaction);
                    glimTransactions.add(groupLoanIndividualMonitoringTransaction);
                }
            }
        }
        return glimTransactions;
    }

    public List<GroupLoanIndividualMonitoringData> handleGLIMRepaymentTemplate(List<GroupLoanIndividualMonitoringData> glimData,
            LoanTransactionData loanTransactionData, Loan loan, Date transactionDate) {
        LocalDate transactionDateAsLocalDate = (transactionDate == null) ? DateUtils.getLocalDateOfTenant() : new LocalDate(transactionDate);
        transactionDateAsLocalDate = new LocalDate(transactionDate);
        List<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallment = loan.getRepaymentScheduleInstallments();
        MonetaryCurrency currency = loan.getCurrency();
        for (GroupLoanIndividualMonitoringData glimIndividualData : glimData) {
            BigDecimal installmentAmount = BigDecimal.ZERO;
            BigDecimal interestAmount = BigDecimal.ZERO;
            BigDecimal chargeAmount = BigDecimal.ZERO;
            BigDecimal waivedChargeAmount = BigDecimal.ZERO;
            BigDecimal waivedInterestAmount = BigDecimal.ZERO;
            BigDecimal defaultInstallmentAmount = glimIndividualData.getInstallmentAmount();
            Boolean isChargeWaived = MathUtility.isGreaterThanZero(glimIndividualData.getWaivedChargeAmount());
            Boolean isInterestWaived = MathUtility.isGreaterThanZero(glimIndividualData.getWaivedInterestAmount());
            List<GroupLoanIndividualMonitoringCharge> glimCharges = this.glimChargeRepository.findByGlimId(glimIndividualData.getId());

            for (int i = 0; i < loanRepaymentScheduleInstallment.size(); i++) {
                LoanRepaymentScheduleInstallment scheduleInstallment = loanRepaymentScheduleInstallment.get(i);
                LocalDate dueDate = scheduleInstallment.getDueDate();
                BigDecimal installmentCharge = BigDecimal.ZERO;
                BigDecimal installmentInterest = BigDecimal.ZERO;

                if (dueDate.isBefore(transactionDateAsLocalDate) || dueDate.isEqual(transactionDateAsLocalDate) || i == 0) {
                    if (i + 1 == loanRepaymentScheduleInstallment.size()) {
                        installmentCharge = glimIndividualData.getChargeAmount().subtract(chargeAmount);
                        installmentInterest = glimIndividualData.getInterestAmount().subtract(interestAmount);
                    } else {
                        installmentCharge = getDefaultChargeSharePerInstallment(loan, glimIndividualData.getId(), scheduleInstallment.getInstallmentNumber());

                        installmentInterest = getDefaultInterestSharePerInstallment(loan, glimIndividualData.getId(),
                                glimIndividualData.getInterestAmount(), scheduleInstallment.getInterestCharged(currency).getAmount(), loan
                                        .getSummary().getTotalInterestCharged());
                    }

                    installmentAmount = installmentAmount.add(defaultInstallmentAmount);
                    for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                        if (ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()
                                && scheduleInstallment.getInstallmentNumber().equals(ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                            installmentAmount = installmentAmount.add(glimCharge.getFeeAmount() );
                        }
                    }
                    
                    chargeAmount = chargeAmount.add(installmentCharge);
                    interestAmount = interestAmount.add(installmentInterest);
                } else {
                    break;
                }
            }
            installmentAmount = installmentAmount.subtract(MathUtility.zeroIfNull(glimIndividualData.getPaidAmount()));
            if (isChargeWaived) {
                waivedChargeAmount = chargeAmount.subtract(MathUtility.zeroIfNull(glimIndividualData.getPaidChargeAmount()));
                waivedChargeAmount = waivedChargeAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : waivedChargeAmount;
            }
            if (isInterestWaived) {
                waivedInterestAmount = interestAmount.subtract(MathUtility.zeroIfNull(glimIndividualData.getPaidInterestAmount()));
                waivedInterestAmount = waivedInterestAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : waivedInterestAmount;
            }
            installmentAmount = installmentAmount.subtract(waivedChargeAmount).subtract(waivedInterestAmount);
            installmentAmount = installmentAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : installmentAmount;
            glimIndividualData.setTransactionAmount(Money.of(currency, installmentAmount).getAmount());

        }

        return glimData;
    }

    public static BigDecimal getDefaultInterestSharePerInstallment(Loan loan, Long glimId, BigDecimal glimAmount, BigDecimal amount,
            BigDecimal totalAmount) {
        MonetaryCurrency currency = loan.getCurrency();
        List<GroupLoanIndividualMonitoring> listForAdujustment = loan.getDefautGlimMembers();
        Long adjustedGlimId = (listForAdujustment.get(listForAdujustment.size() - 1)).getId();
        if (adjustedGlimId != glimId) { return MathUtility.getShare(amount, glimAmount, totalAmount, currency); }
        BigDecimal othersShare = BigDecimal.ZERO;
        for (GroupLoanIndividualMonitoring indGlim : listForAdujustment) {
            if (indGlim.getId() != adjustedGlimId) {
                othersShare = othersShare.add(MathUtility.getShare(amount, indGlim.getInterestAmount(), totalAmount, currency));
            }
        }
        return amount.subtract(othersShare);

    }

    public static BigDecimal getDefaultChargeSharePerInstallment(Loan loan, Long glimId, Integer installmentNumber) {
        MonetaryCurrency currency = loan.getCurrency();
        List<GroupLoanIndividualMonitoring> listForAdujustment = loan.getDefautGlimMembers();
        BigDecimal installmentChargePerClient = BigDecimal.ZERO;

        for (GroupLoanIndividualMonitoring groupLoanIndividualMonitoring : listForAdujustment) {
            if (groupLoanIndividualMonitoring.getId().toString().equals(glimId.toString())) {
                Set<GroupLoanIndividualMonitoringCharge> charges = groupLoanIndividualMonitoring.getGroupLoanIndividualMonitoringCharges();
                for (GroupLoanIndividualMonitoringCharge glimCharge : charges) {
                    if (ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                        if (installmentNumber.equals(ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                            installmentChargePerClient = installmentChargePerClient.add(glimCharge.getFeeAmount());
                        }
                    } else {
                        BigDecimal chargeAmount = glimCharge.getRevisedFeeAmount() == null ? glimCharge.getFeeAmount() : glimCharge
                                .getRevisedFeeAmount();
                        installmentChargePerClient = MathUtility.add(installmentChargePerClient,
                                MathUtility.divide(chargeAmount, loan.fetchNumberOfInstallmensAfterExceptions(), currency));
                    }
                }
            }
        }
        return installmentChargePerClient;

    }

    public static Map<String, BigDecimal> getSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount, Loan loan,
            Integer installmentNumber, Map<String, BigDecimal> installmentPaidMap, LoanTransaction loanTransaction,
            GroupLoanIndividualMonitoringTransaction glimTransaction) {

        BigDecimal totalPaidAmount = MathUtility.add(transactionAmount, installmentPaidMap.get("installmentTransactionAmount"));
        if (loanTransaction != null && loanTransaction.isInterestWaiver()) {
            totalPaidAmount = MathUtility.add(totalPaidAmount, glim.getPaidInterestAmount());
        } else if (loanTransaction != null && loanTransaction.isChargesWaiver()) {
            totalPaidAmount = MathUtility.add(totalPaidAmount, glim.getPaidChargeAmount());
        } else {
            totalPaidAmount = MathUtility.add(totalPaidAmount, glim.getTotalPaidAmount());
        }

        MonetaryCurrency currency = loan.getCurrency();
        BigDecimal installmentAmount = glim.getInstallmentAmount();
        Integer numberOfInstallments = loan.getLoanRepaymentScheduleDetail().getNumberOfRepayments();
        List<LoanRepaymentScheduleInstallment> scheduleList = loan.getRepaymentScheduleInstallments();
        BigDecimal paidCharge = BigDecimal.ZERO;
        BigDecimal paidInterest = BigDecimal.ZERO;
        BigDecimal paidPrincipal = BigDecimal.ZERO;

        BigDecimal glimPaidCharge = MathUtility.zeroIfNull(glim.getPaidChargeAmount()).add(installmentPaidMap.get("unpaidCharge"));
        BigDecimal glimPaidInterest = MathUtility.zeroIfNull(glim.getPaidInterestAmount()).add(installmentPaidMap.get("unpaidInterest"));
        BigDecimal glimPaidPrincipal = MathUtility.zeroIfNull(glim.getPaidPrincipalAmount()).add(installmentPaidMap.get("unpaidPrincipal"));
        if (glimTransaction != null) {
            totalPaidAmount = MathUtility.subtract(totalPaidAmount, glimTransaction.getTotalAmount());
            glimPaidCharge = MathUtility.subtract(glimPaidCharge, glimTransaction.getFeePortion());
            glimPaidInterest = MathUtility.subtract(glimPaidInterest, glimTransaction.getInterestPortion());
            glimPaidPrincipal = MathUtility.subtract(glimPaidPrincipal, glimTransaction.getPrincipalPortion());
        }
        BigDecimal adjustedPaidInterest = BigDecimal.ZERO;
        BigDecimal adjustedPaidCharge = BigDecimal.ZERO;

        Boolean isChargeWaived = MathUtility.isGreaterThanZero(glim.getWaivedChargeAmount());
        Boolean isInterestWaived = MathUtility.isGreaterThanZero(glim.getWaivedInterestAmount());
        for (int i = 0; i < scheduleList.size(); i++) {
            LoanRepaymentScheduleInstallment schedule = scheduleList.get(i);
            BigDecimal installmentCharge = getDefaultChargeSharePerInstallment(loan, glim.getId(), schedule.getInstallmentNumber());
            BigDecimal installmentInterest = getDefaultInterestSharePerInstallment(loan, glim.getId(), glim.getInterestAmount(), schedule
                    .getInterestCharged(currency).getAmount(), loan.getSummary().getTotalInterestCharged());
            if (i + 1 == numberOfInstallments && installmentNumber == numberOfInstallments) {
                installmentInterest = glim.getInterestAmount().subtract(adjustedPaidInterest);
                installmentCharge = glim.getChargeAmount().subtract(adjustedPaidCharge);
            } else {
                adjustedPaidInterest = MathUtility.add(adjustedPaidInterest, installmentInterest);
                adjustedPaidCharge = MathUtility.add(adjustedPaidCharge, installmentCharge);
            }
            BigDecimal installmentPrincipal = BigDecimal.ZERO;
            if (installmentCharge.compareTo(glim.getChargeAmount()) == 0 && schedule.getInstallmentNumber().equals(ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                installmentAmount = installmentAmount.add(installmentCharge);
            } else if(installmentCharge.compareTo(BigDecimal.ZERO) == 0 ) {
                installmentAmount = glim.getInstallmentAmount();
                if (i + 1 == numberOfInstallments && installmentNumber == numberOfInstallments) {
                    BigDecimal adjustLastInstallmentAmount = MathUtility.multiply(glim.getInstallmentAmount(), numberOfInstallments - 1);
                    installmentAmount = MathUtility.subtract(glim.getTotalPaybleAmount(), MathUtility.add(glim.getChargeAmount(), adjustLastInstallmentAmount));
                }
            }
            installmentPrincipal = installmentAmount.subtract(installmentInterest).subtract(installmentCharge);
            if (loanTransaction != null && loanTransaction.isInterestWaiver()) {
                installmentCharge = BigDecimal.ZERO;
                installmentPrincipal = BigDecimal.ZERO;
            } else if (loanTransaction != null && loanTransaction.isChargesWaiver()) {
                installmentInterest = BigDecimal.ZERO;
                installmentPrincipal = BigDecimal.ZERO;
            }

            if (loanTransaction == null || (MathUtility.isGreaterThanZero(totalPaidAmount) && !loanTransaction.isInterestWaiver())) {
                if (MathUtility.isEqualOrGreater(totalPaidAmount, installmentCharge)) {
                    if (MathUtility.isGreaterThanZero(glimPaidCharge)) {
                        if (MathUtility.isEqualOrGreater(glimPaidCharge, installmentCharge)) {
                            glimPaidCharge = glimPaidCharge.subtract(installmentCharge);
                            totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentCharge);
                        } else {
                            if (!isChargeWaived) {
                                paidCharge = MathUtility.add(paidCharge, MathUtility.subtract(installmentCharge, glimPaidCharge));
                                totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentCharge);
                            } else {
                                totalPaidAmount = MathUtility.subtract(totalPaidAmount, glimPaidCharge);
                            }
                            glimPaidCharge = MathUtility.subtract(glimPaidCharge, glimPaidCharge);
                        }
                    } else {
                        if (!isChargeWaived) {
                            paidCharge = MathUtility.add(paidCharge, installmentCharge);
                            totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentCharge);
                        }
                    }

                } else {
                    if (!isChargeWaived) {
                        paidCharge = MathUtility.add(paidCharge, MathUtility.subtract(totalPaidAmount, glimPaidCharge));
                        totalPaidAmount = MathUtility.subtract(totalPaidAmount, totalPaidAmount);
                    } else {
                        totalPaidAmount = MathUtility.subtract(totalPaidAmount, glimPaidCharge);
                    }
                    glimPaidCharge = MathUtility.subtract(glimPaidCharge, glimPaidCharge);
                }
            }

            if (MathUtility.isZero(totalPaidAmount)) {
                break;
            }

            if (loanTransaction == null || (MathUtility.isGreaterThanZero(totalPaidAmount) && !loanTransaction.isChargesWaiver())) {
                if (MathUtility.isEqualOrGreater(totalPaidAmount, installmentInterest)) {
                    if (MathUtility.isGreaterThanZero(glimPaidInterest)) {
                        if (MathUtility.isEqualOrGreater(glimPaidInterest, installmentInterest)) {
                            glimPaidInterest = MathUtility.subtract(glimPaidInterest, installmentInterest);
                            totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentInterest);
                        } else {
                            if (!isInterestWaived) {
                                paidInterest = MathUtility.add(paidInterest, MathUtility.subtract(installmentInterest, glimPaidInterest));
                                totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentInterest);
                            } else {
                                totalPaidAmount = MathUtility.subtract(totalPaidAmount, glimPaidInterest);
                            }
                            glimPaidInterest = MathUtility.subtract(glimPaidInterest, glimPaidInterest);
                        }
                    } else {
                        if (!isInterestWaived) {
                            paidInterest = MathUtility.add(paidInterest, installmentInterest);
                            totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentInterest);
                        }
                    }

                } else {
                    if (!isInterestWaived) {
                        paidInterest = MathUtility.add(paidInterest, MathUtility.subtract(totalPaidAmount, glimPaidInterest));
                        totalPaidAmount = MathUtility.subtract(totalPaidAmount, totalPaidAmount);
                    } else {
                        totalPaidAmount = MathUtility.subtract(totalPaidAmount, glimPaidInterest);
                    }
                    glimPaidInterest = MathUtility.subtract(glimPaidInterest, glimPaidInterest);
                }
            }

            if (MathUtility.isZero(totalPaidAmount)) {
                break;
            }

            if (loanTransaction == null
                    || (MathUtility.isGreaterThanZero(totalPaidAmount) && (loanTransaction.isRepayment() || loanTransaction.isWriteOff()
                            || loanTransaction.isRecoveryRepayment() || loanTransaction.isReversed()))) {
                if (MathUtility.isEqualOrGreater(totalPaidAmount, installmentPrincipal)) {
                    if (MathUtility.isGreaterThanZero(glimPaidPrincipal)) {
                        if (MathUtility.isEqualOrGreater(glimPaidPrincipal, installmentPrincipal)) {
                            glimPaidPrincipal = MathUtility.subtract(glimPaidPrincipal, installmentPrincipal);
                        } else {
                            paidPrincipal = MathUtility.add(paidPrincipal, MathUtility.subtract(installmentPrincipal, glimPaidPrincipal));

                            glimPaidPrincipal = MathUtility.subtract(glimPaidPrincipal, glimPaidPrincipal);
                        }
                    } else {
                        paidPrincipal = MathUtility.add(paidPrincipal, installmentPrincipal);
                    }
                    totalPaidAmount = MathUtility.subtract(totalPaidAmount, installmentPrincipal);

                } else {
                    paidPrincipal = MathUtility.add(paidPrincipal, MathUtility.subtract(totalPaidAmount, glimPaidPrincipal));
                    glimPaidPrincipal = MathUtility.subtract(glimPaidPrincipal, glimPaidPrincipal);
                    totalPaidAmount = MathUtility.subtract(totalPaidAmount, totalPaidAmount);
                }
            }

            if (MathUtility.isZero(totalPaidAmount) || i + 1 == installmentNumber) {

                break;
            }

        }

        Map<String, BigDecimal> splitMap = new HashMap<>();
        splitMap.put("unpaidCharge", paidCharge);
        splitMap.put("unpaidInterest", paidInterest);
        splitMap.put("unpaidPrincipal", paidPrincipal);
        splitMap.put("installmentTransactionAmount", MathUtility.add(paidCharge, paidInterest, paidPrincipal));
        return splitMap;
    }

    public void updateLoanWriteOffStatusForGLIM(Loan loan) {
        List<GroupLoanIndividualMonitoring> glimMembersForStatusUpdate = this.glimRepository.findByLoanIdAndIsClientSelected(loan.getId(),
                true);
        for (GroupLoanIndividualMonitoring glim : glimMembersForStatusUpdate) {
            if (glim.getIsActive() && glim.isClientSelected()) {
                BigDecimal totalAmountWrittenOff = MathUtility.add(glim.getPrincipalWrittenOffAmount(), glim.getInterestWrittenOffAmount(),
                        glim.getChargeWrittenOffAmount());
                BigDecimal outStandingAmount = MathUtility.subtract(glim.getTotalPaybleAmount(), MathUtility.add(glim.getTotalPaidAmount(),
                        glim.getWaivedChargeAmount(), glim.getWaivedInterestAmount(), totalAmountWrittenOff));
                if (MathUtility.isZero(outStandingAmount)) {
                    glim.setIsActive(false);
                }
            }
        }
        this.glimRepository.save(glimMembersForStatusUpdate);
        Boolean isGlimWriteOff = this.glimAssembler.isGLIMApplicableForWriteOf(glimMembersForStatusUpdate);
        if (isGlimWriteOff) {
            loan.setLoanStatus(LoanStatus.CLOSED_WRITTEN_OFF.getValue());
            this.loanRepository.save(loan);
        }
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> writeOffForClients(final LoanTransaction loanTransaction,
            List<GroupLoanIndividualMonitoring> glimMembers, CodeValue writeoffReason) {
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (GroupLoanIndividualMonitoring glimMember : glimMembers) {
            if (glimMember.isClientSelected()) {
                final BigDecimal transactionAmount = glimMember.getTransactionAmount();
                final BigDecimal principalWrittenOffAmount = glimMember.getDisbursedAmount().subtract(
                        MathUtility.zeroIfNull(glimMember.getPaidPrincipalAmount()));
                final BigDecimal interestWrittenOffAmount = glimMember.getInterestAmount().subtract(
                        MathUtility.zeroIfNull(MathUtility.add(glimMember.getPaidInterestAmount(), glimMember.getWaivedInterestAmount())));
                final BigDecimal chargeWrittenOffAmount = glimMember.getChargeAmount().subtract(
                        MathUtility.zeroIfNull(MathUtility.add(glimMember.getPaidChargeAmount(), glimMember.getWaivedChargeAmount())));
                glimMember.setPrincipalWrittenOffAmount(principalWrittenOffAmount);
                glimMember.setInterestWrittenOffAmount(interestWrittenOffAmount);
                glimMember.setChargeWrittenOffAmount(chargeWrittenOffAmount);
                glimMember.setWriteOffReason(writeoffReason);
                GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction = GroupLoanIndividualMonitoringTransaction
                        .instance(glimMember, loanTransaction, loanTransaction.getTypeOf().getValue(), principalWrittenOffAmount,
                                interestWrittenOffAmount, chargeWrittenOffAmount, BigDecimal.ZERO, transactionAmount);
                glimMember.updateGlimTransaction(groupLoanIndividualMonitoringTransaction);
                glimTransactions.add(groupLoanIndividualMonitoringTransaction);
            }

        }
        return glimTransactions;
    }
}
