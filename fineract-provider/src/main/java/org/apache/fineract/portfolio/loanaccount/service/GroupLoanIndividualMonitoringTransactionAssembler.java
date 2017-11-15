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
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringDataChanges;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransactionRepositoryWrapper;
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
    private final GroupLoanIndividualMonitoringTransactionRepositoryWrapper glimTransactionRepositoryWrapper;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionAssembler(final FromJsonHelper fromApiJsonHelper,
            final GroupLoanIndividualMonitoringRepositoryWrapper groupLoanIndividualMonitoringRepositoryWrapper,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory,
            final GroupLoanIndividualMonitoringRepository glimRepository, final GroupLoanIndividualMonitoringAssembler glimAssembler,
            final LoanRepository loanRepository, final GroupLoanIndividualMonitoringChargeRepository glimChargeRepository,
            final GroupLoanIndividualMonitoringTransactionRepositoryWrapper glimTransactionRepositoryWrapper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.groupLoanIndividualMonitoringRepositoryWrapper = groupLoanIndividualMonitoringRepositoryWrapper;
        this.transactionProcessorFactory = transactionProcessorFactory;
        this.glimRepository = glimRepository;
        this.glimAssembler = glimAssembler;
        this.loanRepository = loanRepository;
        this.glimChargeRepository = glimChargeRepository;
        this.glimTransactionRepositoryWrapper = glimTransactionRepositoryWrapper;
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> assembleGLIMTransactions(final JsonCommand command,
            final LoanTransaction loanTransaction, Collection<GroupLoanIndividualMonitoringDataChanges> clientMembersJson) {
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
                    GroupLoanIndividualMonitoringTransaction glimTransaction = GroupLoanIndividualMonitoringTransaction
                            .instance(groupLoanIndividualMonitoring, loanTransaction, loanTransaction.getTypeOf().getValue());
                    
                    loanRepaymentScheduleTransactionProcessor.handleGLIMRepayment(glimTransaction,
                            individualTransactionAmount);
                    glimTransaction.setOverpaidAmount(groupLoanIndividualMonitoring.getProcessedTransactionMap().get("processedOverPaidAmount"));
                    glimTransaction.setTotalAmount(groupLoanIndividualMonitoring.getTransactionAmount());                    
                    glimTransactions.add(glimTransaction);
                    clientMembersJson.add(GroupLoanIndividualMonitoringDataChanges.createNew(glimId, individualTransactionAmount));
                }
            }
        }
        return glimTransactions;
    }
    
    public void updateGlimTransactionsData(final JsonCommand command, Loan loan, Map<String, Object> changes,
            Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers, boolean isRecoveryRepayment,
            LoanTransaction newLoanTransaction) {
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.assembleGLIMTransactions(command, newLoanTransaction,
                clientMembers);
        this.glimAssembler.updateGLIMAfterRepayment(glimTransactions, isRecoveryRepayment);
        this.glimTransactionRepositoryWrapper.saveAsList(glimTransactions);
        this.updateLoanStatusForGLIM(loan);
        changes.put("clientMember", clientMembers);
    }

    public void validateGlimTransactionAmount(JsonCommand command, boolean isRecoveryRepayment) {
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(LoanApiConstants.transactionAmountParamName);
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            JsonArray clientMembers = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
            for (JsonElement clientMember : clientMembers) {
                JsonObject member = clientMember.getAsJsonObject();
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
            if (totalInstallmentAmount.compareTo(transactionAmount) != 0) { throw new ClientInstallmentNotEqualToTransactionAmountException(); }
        }
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> waiveInterestForClients(final JsonCommand command,
            final LoanTransaction loanTransaction, Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers) {
        JsonArray clients = command.arrayOfParameterNamed(LoanApiConstants.clientMembersParamName);
        final Locale locale = command.extractLocale();
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (JsonElement element : clients) {
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
                clientMembers.add(GroupLoanIndividualMonitoringDataChanges.createNew(glimId, transactionAmount));
            }
        }
        return glimTransactions;
    }

    public List<GroupLoanIndividualMonitoringData> handleGLIMRepaymentTemplate(List<GroupLoanIndividualMonitoringData> glimData, Loan loan,
            Date transactionDate) {
        LocalDate transactionDateAsLocalDate = (transactionDate == null) ? DateUtils.getLocalDateOfTenant()
                : new LocalDate(transactionDate);
        transactionDateAsLocalDate = new LocalDate(transactionDate);
        List<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallment = loan.getRepaymentScheduleInstallments();
        MonetaryCurrency currency = loan.getCurrency();
        for (GroupLoanIndividualMonitoringData glimIndividualData : glimData) {
            if (glimIndividualData.getIsActive()) {
                getGlimTransactionAmount(loan, transactionDateAsLocalDate, loanRepaymentScheduleInstallment, currency, glimIndividualData);
            } else {
                glimIndividualData.setTransactionAmount(BigDecimal.ZERO);
            }
        }

        return glimData;
    }

    private void getGlimTransactionAmount(Loan loan, LocalDate transactionDateAsLocalDate,
            List<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallment, MonetaryCurrency currency,
            GroupLoanIndividualMonitoringData glimIndividualData) {
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
                if (scheduleInstallment.getInstallmentNumber() == loanRepaymentScheduleInstallment.size()) {
                    installmentCharge = glimIndividualData.getChargeAmount().subtract(chargeAmount);
                    installmentInterest = glimIndividualData.getInterestAmount().subtract(interestAmount);
                } else {
                    installmentCharge = getDefaultChargeSharePerInstallment(loan, glimIndividualData.getId(),
                            scheduleInstallment.getInstallmentNumber());

                    installmentInterest = getDefaultInterestSharePerInstallment(loan, glimIndividualData.getId(),
                            glimIndividualData.getInterestAmount(), scheduleInstallment.getInterestCharged(currency).getAmount(), loan
                                    .getSummary().getTotalInterestCharged());
                }
                if (loan.getLoanProduct().adjustFirstEMIAmount() && i == 0) {
                    installmentAmount = MathUtility.subtract(
                            MathUtility.add(glimIndividualData.getDisbursedAmount(), glimIndividualData.getInterestAmount()),
                            MathUtility.multiply(defaultInstallmentAmount, loanRepaymentScheduleInstallment.size() - 1));
                } else {
                    installmentAmount = installmentAmount.add(defaultInstallmentAmount);
                }

                for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                    if (ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()
                            && scheduleInstallment.getInstallmentNumber().equals(ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                        installmentAmount = installmentAmount.add(glimCharge.getFeeAmount());
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

    public static BigDecimal getDefaultInterestSharePerInstallment(Loan loan, Long glimId, BigDecimal glimAmount, BigDecimal amount,
            BigDecimal totalAmount) {
        MonetaryCurrency currency = loan.getCurrency();
        List<GroupLoanIndividualMonitoring> listForAdujustment = loan.getDefautGlimMembers();
        Long adjustedGlimId = (listForAdujustment.get(listForAdujustment.size() - 1)).getId();
        if (!adjustedGlimId.equals(glimId)) { return MathUtility.getShare(amount, glimAmount, totalAmount, currency); }
        BigDecimal othersShare = BigDecimal.ZERO;
        for (GroupLoanIndividualMonitoring glim : listForAdujustment) {
            if (!glim.getId().equals(adjustedGlimId)) {
                othersShare = othersShare.add(MathUtility.getShare(amount, glim.getInterestAmount(), totalAmount, currency));
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
    /**
     * it process total amount(already processed amount and current transaction amount)
     *  and recreate installment portions for given installment number
     * @param glim
     * @param transactionAmount
     * @param loan
     * @param installmentNumber
     * @param installmentPaidMap
     * @param loanTransaction
     * @param glimTransaction
     * @return
     */
    public static Map<String, BigDecimal> getSplit(GroupLoanIndividualMonitoring glim, BigDecimal transactionAmount, Loan loan,
            Integer installmentNumber, Map<String, BigDecimal> installmentPaidMap, LoanTransaction loanTransaction,
            GroupLoanIndividualMonitoringTransaction glimTransaction) {

        BigDecimal unprocessedAmount = MathUtility.add(transactionAmount, installmentPaidMap.get("installmentTransactionAmount"));
        if (loanTransaction != null && loanTransaction.isInterestWaiver()) {
            unprocessedAmount = MathUtility.add(unprocessedAmount, glim.getPaidInterestAmount());
        } else if (loanTransaction != null && loanTransaction.isChargesWaiver()) {
            unprocessedAmount = MathUtility.add(unprocessedAmount, glim.getPaidChargeAmount());
        } else {
            unprocessedAmount = MathUtility.add(unprocessedAmount, glim.getTotalPaidAmount());
        }

        MonetaryCurrency currency = loan.getCurrency();
        BigDecimal installmentAmount = glim.getInstallmentAmount();
        Integer numberOfInstallments = loan.getLoanRepaymentScheduleDetail().getNumberOfRepayments();
        List<LoanRepaymentScheduleInstallment> scheduleList = loan.getRepaymentScheduleInstallments();
        BigDecimal paidCharge = BigDecimal.ZERO;
        BigDecimal paidInterest = BigDecimal.ZERO;
        BigDecimal paidPrincipal = BigDecimal.ZERO;
        BigDecimal overPaidAmount = BigDecimal.ZERO;

        BigDecimal glimPaidCharge = MathUtility.zeroIfNull(glim.getPaidChargeAmount()).add(installmentPaidMap.get("unpaidCharge"));
        BigDecimal glimPaidInterest = MathUtility.zeroIfNull(glim.getPaidInterestAmount()).add(installmentPaidMap.get("unpaidInterest"));
        BigDecimal glimPaidPrincipal = MathUtility.zeroIfNull(glim.getPaidPrincipalAmount()).add(installmentPaidMap.get("unpaidPrincipal"));
        BigDecimal glimOverPaidAmount = MathUtility.zeroIfNull(glim.getOverpaidAmount()).add(installmentPaidMap.get("unprocessedOverPaidAmount"));
        if (glimTransaction != null) {
            unprocessedAmount = MathUtility.subtract(unprocessedAmount, glimTransaction.getTotalAmount());
            glimPaidCharge = MathUtility.subtract(glimPaidCharge, glimTransaction.getFeePortion());
            glimPaidInterest = MathUtility.subtract(glimPaidInterest, glimTransaction.getInterestPortion());
            glimPaidPrincipal = MathUtility.subtract(glimPaidPrincipal, glimTransaction.getPrincipalPortion());
        }
        BigDecimal adjustedPaidInterest = BigDecimal.ZERO;
        BigDecimal adjustedPaidCharge = BigDecimal.ZERO;

        Boolean isChargeWaived = MathUtility.isGreaterThanZero(glim.getWaivedChargeAmount());
        Boolean isInterestWaived = MathUtility.isGreaterThanZero(glim.getWaivedInterestAmount());
        for (int i = 0; i < scheduleList.size(); i++) {
        	if(loan.getLoanProduct().adjustFirstEMIAmount() && i==0){   
        		BigDecimal principal = (glim.getDisbursedAmount() != null)?glim.getDisbursedAmount():(glim.getApprovedAmount() != null)? glim.getApprovedAmount(): glim.getProposedAmount();
        		installmentAmount = MathUtility.subtract(MathUtility.add(principal,  glim.getInterestAmount()),  MathUtility.multiply(glim.getInstallmentAmount(), numberOfInstallments-1));        		
        	}
            LoanRepaymentScheduleInstallment schedule = scheduleList.get(i);
            BigDecimal installmentCharge = getDefaultChargeSharePerInstallment(loan, glim.getId(), schedule.getInstallmentNumber());
            BigDecimal installmentInterest = getDefaultInterestSharePerInstallment(loan, glim.getId(), glim.getInterestAmount(), schedule
                    .getInterestCharged(currency).getAmount(), loan.getTotalInterest());
            if (schedule.getInstallmentNumber() == numberOfInstallments && installmentNumber == numberOfInstallments) {
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
                    if(loan.getLoanProduct().adjustFirstEMIAmount()){
                    	installmentAmount = MathUtility.subtract(glim.getTotalPaybleAmount(), MathUtility.add(glim.getChargeAmount(), glim.getInstallmentAmount()));
                    }else{
                    	installmentAmount = MathUtility.subtract(glim.getTotalPaybleAmount(), MathUtility.add(glim.getChargeAmount(), adjustLastInstallmentAmount));
                    }
                    
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

            if (loanTransaction == null || (MathUtility.isGreaterThanZero(unprocessedAmount) && !loanTransaction.isInterestWaiver())) {
                if (MathUtility.isEqualOrGreater(unprocessedAmount, installmentCharge)) {
                    if (MathUtility.isGreaterThanZero(glimPaidCharge)) {
                        if (MathUtility.isEqualOrGreater(glimPaidCharge, installmentCharge)) {
                            glimPaidCharge = glimPaidCharge.subtract(installmentCharge);
                            unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentCharge);
                        } else {
                            if (!isChargeWaived) {
                                paidCharge = MathUtility.add(paidCharge, MathUtility.subtract(installmentCharge, glimPaidCharge));
                                unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentCharge);
                            } else {
                                unprocessedAmount = MathUtility.subtract(unprocessedAmount, glimPaidCharge);
                            }
                            glimPaidCharge = BigDecimal.ZERO;
                        }
                    } else {
                        if (!isChargeWaived) {
                            paidCharge = MathUtility.add(paidCharge, installmentCharge);
                            unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentCharge);
                        }
                    }

                } else {
                    if (!isChargeWaived) {
                        paidCharge = MathUtility.add(paidCharge, MathUtility.subtract(unprocessedAmount, glimPaidCharge));
                        unprocessedAmount = BigDecimal.ZERO;;
                    } else {
                        unprocessedAmount = MathUtility.subtract(unprocessedAmount, glimPaidCharge);
                    }
                    glimPaidCharge = BigDecimal.ZERO;
                }
            }

            if (MathUtility.isZero(unprocessedAmount)) {
                break;
            }

            if (loanTransaction == null || (MathUtility.isGreaterThanZero(unprocessedAmount) && !loanTransaction.isChargesWaiver())) {
                if (MathUtility.isEqualOrGreater(unprocessedAmount, installmentInterest)) {
                    if (MathUtility.isGreaterThanZero(glimPaidInterest)) {
                        if (MathUtility.isEqualOrGreater(glimPaidInterest, installmentInterest)) {
                            glimPaidInterest = MathUtility.subtract(glimPaidInterest, installmentInterest);
                            unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentInterest);
                        } else {
                            if (!isInterestWaived) {
                                paidInterest = MathUtility.add(paidInterest, MathUtility.subtract(installmentInterest, glimPaidInterest));
                                unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentInterest);
                            } else {
                                unprocessedAmount = MathUtility.subtract(unprocessedAmount, glimPaidInterest);
                            }
                            glimPaidInterest = BigDecimal.ZERO;
                        }
                    } else {
                        if (!isInterestWaived) {
                            paidInterest = MathUtility.add(paidInterest, installmentInterest);
                            unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentInterest);
                        }
                    }

                } else {
                    if (!isInterestWaived) {
                        paidInterest = MathUtility.add(paidInterest, MathUtility.subtract(unprocessedAmount, glimPaidInterest));
                        unprocessedAmount = BigDecimal.ZERO;
                    } else {
                        unprocessedAmount = MathUtility.subtract(unprocessedAmount, glimPaidInterest);
                    }
                    glimPaidInterest = BigDecimal.ZERO;
                }
            }

            if (MathUtility.isZero(unprocessedAmount)) {
                break;
            }

            if (loanTransaction == null
                    || (MathUtility.isGreaterThanZero(unprocessedAmount) && (loanTransaction.isRepayment() || loanTransaction.isWriteOff()
                            || loanTransaction.isRecoveryRepayment() || loanTransaction.isReversed()))) {
                if (MathUtility.isEqualOrGreater(unprocessedAmount, installmentPrincipal)) {
                    if (MathUtility.isGreaterThanZero(glimPaidPrincipal)) {
                        if (MathUtility.isEqualOrGreater(glimPaidPrincipal, installmentPrincipal)) {
                            glimPaidPrincipal = MathUtility.subtract(glimPaidPrincipal, installmentPrincipal);
                        } else {
                            paidPrincipal = MathUtility.add(paidPrincipal, MathUtility.subtract(installmentPrincipal, glimPaidPrincipal));

                            glimPaidPrincipal = BigDecimal.ZERO;
                        }
                    } else {
                        paidPrincipal = MathUtility.add(paidPrincipal, installmentPrincipal);
                    }
                    unprocessedAmount = MathUtility.subtract(unprocessedAmount, installmentPrincipal);

                } else {
                    paidPrincipal = MathUtility.add(paidPrincipal, MathUtility.subtract(unprocessedAmount, glimPaidPrincipal));
                    glimPaidPrincipal = BigDecimal.ZERO;
                    unprocessedAmount = BigDecimal.ZERO;
                }
            }

            if (MathUtility.isZero(unprocessedAmount) || i + 1 == installmentNumber){
                break;
            }

        }
        if(numberOfInstallments.equals(installmentNumber)){
            overPaidAmount = MathUtility.subtract(unprocessedAmount, glimOverPaidAmount) ;
        }

        Map<String, BigDecimal> splitMap = new HashMap<>();
        splitMap.put("unpaidCharge", paidCharge);
        splitMap.put("unpaidInterest", paidInterest);
        splitMap.put("unpaidPrincipal", paidPrincipal);
        splitMap.put("unprocessedOverPaidAmount", overPaidAmount);
        splitMap.put("installmentTransactionAmount", MathUtility.add(paidCharge, paidInterest, paidPrincipal, overPaidAmount));
        return splitMap;
    }

    public void updateLoanStatusForGLIM(Loan loan) {
        List<GroupLoanIndividualMonitoring> glimMembersForStatusUpdate = this.glimRepository.findByLoanIdAndIsClientSelected(loan.getId(),
                true);
        for (GroupLoanIndividualMonitoring glim : glimMembersForStatusUpdate) {
            if (glim.isClientSelected()) {                
                glim.setIsActive(this.glimAssembler.isActive(glim));
            }
        }
        this.glimRepository.save(glimMembersForStatusUpdate);
        boolean isLoanCompleted = isLoanCompleted(glimMembersForStatusUpdate);
        if(isLoanCompleted){
            Boolean isGlimWriteOff = this.glimAssembler.isGLIMApplicableForWriteOf(glimMembersForStatusUpdate);
            if (isGlimWriteOff) {
                loan.setLoanStatus(LoanStatus.CLOSED_WRITTEN_OFF.getValue());
            }else if(MathUtility.isGreaterThanZero(loan.getTotalOverpaid())){
                loan.setLoanStatus(LoanStatus.OVERPAID.getValue());
            }
        }else{
            loan.setLoanStatus(LoanStatus.ACTIVE.getValue());
        }
        this.loanRepository.save(loan);
    }
    
    public boolean isLoanCompleted(List<GroupLoanIndividualMonitoring> glimMembersForStatusUpdate){
        for (GroupLoanIndividualMonitoring glimData : glimMembersForStatusUpdate) {            
            if (glimData.isClientSelected() && glimData.getIsActive()) { return false; }
        }
        return true;
    }

    public Collection<GroupLoanIndividualMonitoringTransaction> writeOffForClients(final LoanTransaction loanTransaction,
            List<GroupLoanIndividualMonitoring> glimMembers, CodeValue writeoffReason, Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers) {
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = new ArrayList<>();
        for (GroupLoanIndividualMonitoring glimMember : glimMembers) {
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
            clientMembers.add(GroupLoanIndividualMonitoringDataChanges.createNew(glimMember.getId(), transactionAmount));
        }
        return glimTransactions;
    }
}