/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.finflux.email.service.BusinessEventEmailConfigurationReadPaltformService;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * EmailEventListener listens to Business event - Loan disbursal and whenever a
 * loan is disbursed, it sends a mail notification to the registered customer
 * about loan re-payment schedule
 */

@Service
public class EmailEventListener {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(EmailEventListener.class);
    private final BusinessEventNotifierService businessEventNotifierService;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private final EmailReportGenerator emailReportGenerator;
    private final EmailRepaymentScheduleRepository emailRepaymentScheduleRepository;
    private final BusinessEventEmailConfigurationReadPaltformService  businessEventEmailConfigurationReadPaltformService;
    
    @Autowired
    public EmailEventListener(final BusinessEventNotifierService businessEventNotifierService,
            final EmailReportGenerator emailReportGenerator,
            final EmailRepaymentScheduleRepository emailRepaymentScheduleRepository, final BusinessEventEmailConfigurationReadPaltformService  businessEventEmailConfigurationReadPaltformService) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.emailReportGenerator = emailReportGenerator;
        this.emailRepaymentScheduleRepository = emailRepaymentScheduleRepository;
        this.businessEventEmailConfigurationReadPaltformService = businessEventEmailConfigurationReadPaltformService;
    }

    @PostConstruct
    public void registerForNotification() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL,
                new EmailSchedulaEventListnerForLoanDisbursal());
        executorService = Executors.newSingleThreadExecutor();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        /*
         * scheduledExecutorService.schedule(new
         * BootupPendingMessagesTask(emailRepaymentScheduleRepository,
         * emailReportGenerator,ThreadLocalContextUtil.getTenant()), 1,
         * TimeUnit.MINUTES);
         */

    }

    private class EmailSchedulaEventListnerForLoanDisbursal implements BusinessEventListner {

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {}

        @SuppressWarnings("null")
        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object loanTransactionEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN_TRANSACTION);
            HashMap<String, Object> businessEventEmailMap = (HashMap<String, Object>) businessEventEmailConfigurationReadPaltformService
                    .retrieveOneWithBuisnessEvent(BUSINESS_EVENTS.LOAN_DISBURSAL);
            if (loanTransactionEntity != null && businessEventEmailMap != null) {
                final LoanTransaction loanTransaction = (LoanTransaction) loanTransactionEntity;

                if (loanTransaction != null && loanTransaction.getLoan() != null && loanTransaction.getLoan().getClient() != null
                        && loanTransaction.getLoan().getClient().getEmailId() != null) {
                    final String attachmentType = businessEventEmailMap.get("attachmentType").toString();
                    final String centerDisplayName = businessEventEmailMap.get("centerDisplayName").toString();
                    final String reportName = businessEventEmailMap.get("reportName").toString();
                    MultivaluedMap<String, String> reportParams = new MultivaluedMapImpl();
                    reportParams.add("R_loanId", loanTransaction.getLoan().getId().toString());
                    reportParams.add("output-type", attachmentType);
                    reportParams.add("R_status", LoanStatus.ACTIVE.getValue().toString());

                    executorService.execute(new Reportgenerator(loanTransaction, reportName, reportParams, emailReportGenerator,
                            ThreadLocalContextUtil.getTenant(), SecurityContextHolder.getContext().getAuthentication(), attachmentType,
                            centerDisplayName));
                }
            }
        }
    }

    private class Reportgenerator implements Runnable {

        final LoanTransaction loanTransaction;
        final String reportName;
        final MultivaluedMap<String, String> reportParams;
        final EmailReportGenerator emailReportGenerator;
        final FineractPlatformTenant tenant;
        final Authentication auth;
        final String attachmentType;
        final String centerDisplayName;
        

        public Reportgenerator(final LoanTransaction loanTransaction, final String reportName,
                final MultivaluedMap<String, String> reportParams, EmailReportGenerator emailReportGenerator,
                final FineractPlatformTenant tenant, final Authentication auth, final String attachmentType, final String centerDisplayName) {
            this.loanTransaction = loanTransaction;
            this.reportName = reportName;
            this.reportParams = reportParams;
            this.emailReportGenerator = emailReportGenerator;
            this.tenant = tenant;
            this.auth = auth;
            this.attachmentType = attachmentType;
            this.centerDisplayName = centerDisplayName;
        }

        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(this.tenant);
            if (this.auth != null) {
                SecurityContextHolder.getContext().setAuthentication(this.auth);
            }
            
            this.emailReportGenerator.generateReportOutputStream(loanTransaction.getLoan(),
                    loanTransaction.getLoan().getClient().getEmailId(), reportName, reportParams,
                    loanTransaction.getLoan().getClient().getId(), loanTransaction.getAmount(), this.attachmentType, this.centerDisplayName);

        }

    }

    class BootupPendingMessagesTask implements Callable<Integer> {

        final EmailRepaymentScheduleRepository emailRepaymentScheduleRepository;
        final EmailReportGenerator emailReportGenerator;
        final FineractPlatformTenant tenant;

        public BootupPendingMessagesTask(final EmailRepaymentScheduleRepository emailRepaymentScheduleRepository,
                final EmailReportGenerator emailReportGenerator, FineractPlatformTenant tenant) {
            this.emailRepaymentScheduleRepository = emailRepaymentScheduleRepository;
            this.emailReportGenerator = emailReportGenerator;
            this.tenant = tenant;
        }

        @Override
        public Integer call() throws Exception {
            logger.info("Sending Pending Messages on bootup.....");
            ThreadLocalContextUtil.setTenant(this.tenant);
            Integer page = 0;
            Integer initialSize = 200;
            Integer totalPageSize = 0;
            do {
                PageRequest pageRequest = new PageRequest(page, initialSize);
                Page<EmailOutboundMessage> messages = this.emailRepaymentScheduleRepository.findByStatus(EmailStatus.PENDING.toString(),
                        pageRequest);
                page++;
                totalPageSize = messages.getTotalPages();
                HashMap<String, Object> businessEventEmailMap = (HashMap<String, Object>) businessEventEmailConfigurationReadPaltformService
                        .retrieveOneWithBuisnessEvent(BUSINESS_EVENTS.LOAN_DISBURSAL);
                if (businessEventEmailMap != null) {
                    final String reportName = businessEventEmailMap.get("reportName").toString();
                    final String attachmentType = businessEventEmailMap.get("attachmentType").toString();
                    final String centerDisplayName = businessEventEmailMap.get("centerDisplayName").toString();
                    this.emailReportGenerator.generateReportOutputStream(messages.getContent(), reportName, attachmentType,
                            centerDisplayName);
                }
            } while (page < totalPageSize);
            return totalPageSize;
        }
    }

}
