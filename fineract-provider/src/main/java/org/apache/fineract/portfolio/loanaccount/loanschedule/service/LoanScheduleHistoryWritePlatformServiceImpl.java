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
package org.apache.fineract.portfolio.loanaccount.loanschedule.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistory;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistoryRepository;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanScheduleHistoryWritePlatformServiceImpl implements LoanScheduleHistoryWritePlatformService {

    private final LoanRepaymentScheduleHistoryRepository loanRepaymentScheduleHistoryRepository;

    @Autowired
    public LoanScheduleHistoryWritePlatformServiceImpl(
            final LoanRepaymentScheduleHistoryRepository loanRepaymentScheduleHistoryRepository) {
        this.loanRepaymentScheduleHistoryRepository = loanRepaymentScheduleHistoryRepository;

    }

    @Override
    public List<LoanRepaymentScheduleHistory> createLoanScheduleArchive(
            final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments, final Loan loan,
            final LoanRescheduleRequest loanRescheduleRequest) {
        final Integer version = loan.getRepaymentHistoryVersion() + 1;
        loan.setRepaymentHistoryVersion(version);
        final MonetaryCurrency currency = loan.getCurrency();
        final List<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = new ArrayList<>();

        for (final LoanRepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallments) {
            final Integer installmentNumber = repaymentScheduleInstallment.getInstallmentNumber();
            Date fromDate = null;
            Date dueDate = null;

            if (repaymentScheduleInstallment.getFromDate() != null) {
                fromDate = repaymentScheduleInstallment.getFromDate().toDate();
            }

            if (repaymentScheduleInstallment.getDueDate() != null) {
                dueDate = repaymentScheduleInstallment.getDueDate().toDate();
            }

            final BigDecimal principal = repaymentScheduleInstallment.getPrincipal(currency).getAmount();
            final BigDecimal interestCharged = repaymentScheduleInstallment.getInterestCharged(currency).getAmount();
            final BigDecimal feeChargesCharged = repaymentScheduleInstallment.getFeeChargesCharged(currency).getAmount();
            final BigDecimal penaltyCharges = repaymentScheduleInstallment.getPenaltyChargesCharged(currency).getAmount();

            final LoanRepaymentScheduleHistory loanRepaymentScheduleHistory = LoanRepaymentScheduleHistory.instance(loan,
                    loanRescheduleRequest, installmentNumber, fromDate, dueDate, principal, interestCharged, feeChargesCharged,
                    penaltyCharges, version, repaymentScheduleInstallment.isRecalculatedInterestComponent());

            loanRepaymentScheduleHistoryList.add(loanRepaymentScheduleHistory);
        }
        return loanRepaymentScheduleHistoryList;
    }

    @Override
    public void createAndSaveLoanScheduleArchive(final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments,
            final Loan loan, final LoanRescheduleRequest loanRescheduleRequest) {
        final List<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = createLoanScheduleArchive(repaymentScheduleInstallments,
                loan, loanRescheduleRequest);
        this.loanRepaymentScheduleHistoryRepository.save(loanRepaymentScheduleHistoryList);

    }

}
