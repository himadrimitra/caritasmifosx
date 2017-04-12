/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.InvalidClientStateTransitionException;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanGlimRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanGlimRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GlimLoanWriteServiceImpl implements GlimLoanWriteService, BusinessEventListner {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(GlimLoanWriteServiceImpl.class);
    private final BusinessEventNotifierService businessEventNotifierService;
    private final LoanGlimRepaymentScheduleInstallmentRepository loanGlimRepaymentScheduleInstallmentRepository;
    private final GroupLoanIndividualMonitoringRepositoryWrapper glimRepositoryWrapper;

    @Autowired
    public GlimLoanWriteServiceImpl(final BusinessEventNotifierService businessEventNotifierService,
            final LoanGlimRepaymentScheduleInstallmentRepository loanGlimRepaymentScheduleInstallmentRepository,
            final GroupLoanIndividualMonitoringRepositoryWrapper glimRepositoryWrapper) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.loanGlimRepaymentScheduleInstallmentRepository = loanGlimRepaymentScheduleInstallmentRepository;
        this.glimRepositoryWrapper = glimRepositoryWrapper;
    }

    @PostConstruct
    public void registerForNotification() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL,
                new GlimLoanRepaymentSchedulaEventListner());
        this.businessEventNotifierService.addBusinessEventPreListners(BUSINESS_EVENTS.CLIENT_CLOSE,
                new GLIMValidatationEventListner());
        this.businessEventNotifierService.addBusinessEventPreListners(BUSINESS_EVENTS.CLIENT_DISASSOCIATE,
                new GLIMValidatationEventListner());
        this.businessEventNotifierService.addBusinessEventPreListners(BUSINESS_EVENTS.TRANSFER_CLIENT,
                new GLIMValidatationEventListner());
    }

    
    @Override
    public void generateGlimLoanRepaymentSchedule(final Loan loan) {
        if (loan.isGLIMLoan()) {
            final List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
            final List<GroupLoanIndividualMonitoring> glimMembers = loan.getGroupLoanIndividualMonitoringList();
            final List<LoanGlimRepaymentScheduleInstallment> loanGlimRepaymentScheduleInstallments = new LinkedList<>();
            for (GroupLoanIndividualMonitoring glimMember : glimMembers) {
                BigDecimal transactionAmount = glimMember.getTotalPaybleAmount();
                Map<String, BigDecimal> installmentPaidMap = new HashMap<>();
                installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
                installmentPaidMap.put("unprocessedOverPaidAmount", BigDecimal.ZERO);
                installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);
                for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
                    if (transactionAmount.compareTo(BigDecimal.ZERO) == 1) {
                        Map<String, BigDecimal> paidInstallmentMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                                transactionAmount, loan, currentInstallment.getInstallmentNumber(), installmentPaidMap, null, null);
                        final BigDecimal principal = paidInstallmentMap.get("unpaidPrincipal");
                        final BigDecimal interestCharged = paidInstallmentMap.get("unpaidInterest");
                        final BigDecimal feeChargesCharged = paidInstallmentMap.get("unpaidCharge");
                        final BigDecimal totalAmountForCurrentInstallment = paidInstallmentMap.get("installmentTransactionAmount");

                        final LoanGlimRepaymentScheduleInstallment loanGlimRepaymentScheduleInstallment = LoanGlimRepaymentScheduleInstallment
                                .create(glimMember, currentInstallment, principal, interestCharged, feeChargesCharged, null);
                        LoanGlimRepaymentScheduleInstallment existingRepaymentSchedule = this.loanGlimRepaymentScheduleInstallmentRepository.getLoanGlimRepaymentScheduleByLoanRepaymentScheduleId(currentInstallment.getId(), glimMember.getId());
                        if(existingRepaymentSchedule != null){
                        	this.loanGlimRepaymentScheduleInstallmentRepository.delete(existingRepaymentSchedule.getId());
                        }
                        loanGlimRepaymentScheduleInstallments.add(loanGlimRepaymentScheduleInstallment);
                        transactionAmount = transactionAmount.subtract(totalAmountForCurrentInstallment);

                        installmentPaidMap.put("unpaidCharge", installmentPaidMap.get("unpaidCharge").add(feeChargesCharged));
                        installmentPaidMap.put("unpaidInterest", installmentPaidMap.get("unpaidInterest").add(interestCharged));
                        installmentPaidMap.put("unpaidPrincipal", installmentPaidMap.get("unpaidPrincipal").add(principal));
                        installmentPaidMap.put("unprocessedOverPaidAmount",BigDecimal.ZERO);
                        installmentPaidMap.put("installmentTransactionAmount",
                                installmentPaidMap.get("installmentTransactionAmount").add(totalAmountForCurrentInstallment));

                    }

                }

            }

            if (!loanGlimRepaymentScheduleInstallments.isEmpty()) {
                this.loanGlimRepaymentScheduleInstallmentRepository.save(loanGlimRepaymentScheduleInstallments);
            }
        }
    }
    
    
    @SuppressWarnings("unused")
    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    private void validateClientHasActiveGlimLoan(Client client, String action) {
        final List<GroupLoanIndividualMonitoring> glimMembers = this.glimRepositoryWrapper.findByClientId(client.getId());
        for (GroupLoanIndividualMonitoring glim : glimMembers) {
            if (glim.getLoan()!= null && !glim.getLoan().isClosed() && !glim.isOutstandingBalanceZero()) {
                final String errorMessage = "Client cannot be " + action + " because of active glim loans.";
                throw new InvalidClientStateTransitionException(action, "client cannot. " + action + " because.client.has.active.glim.loan", errorMessage);
            }
        }
    }

    private class GlimLoanRepaymentSchedulaEventListner implements BusinessEventListner {

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub

        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
            if (loanEntity != null) {
                Loan loan = (Loan) loanEntity;
                generateGlimLoanRepaymentSchedule(loan);
            }
        }

    }
    
    private class GLIMValidatationEventListner implements BusinessEventListner {

        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object clientEntity = businessEventEntity.get(BUSINESS_ENTITY.CLIENT);
            Object clientDisassociateEntity = businessEventEntity.get(BUSINESS_ENTITY.CLIENT_DISASSOCIATE);
            Object transferClientsEntity = businessEventEntity.get(BUSINESS_ENTITY.TRANSFER_CLIENT);
            if (clientEntity != null) {
                Client client  = (Client) clientEntity;
                String action = ClientApiConstants.clientClose;
                validateClientHasActiveGlimLoan(client, action);
            } else if(clientDisassociateEntity != null) {
                @SuppressWarnings("unchecked")
                Set<Client> clientMembers = (Set<Client>) clientDisassociateEntity;
                String action = ClientApiConstants.clientDisassociate;
                validateClientMembers(clientMembers, action);
            } else if(transferClientsEntity != null) {
                @SuppressWarnings("unchecked")
                List<Client> clientMembers = (List<Client>) transferClientsEntity;
                String action = ClientApiConstants.clientTransfer;
                validateClientMembers(clientMembers, action);
            }

        }

        private void validateClientMembers(final Collection<Client> clientMembers, final String action) {
            for (Client client : clientMembers) {
                validateClientHasActiveGlimLoan(client, action);
            }
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

    }
}