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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.loanaccount.exception.LoanNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link LoanRepository} that adds NULL checking and Error handling
 * capabilities
 * </p>
 */
@Service
public class LoanRepositoryWrapper {

    private final LoanRepository repository;

    @Autowired
    public LoanRepositoryWrapper(final LoanRepository repository) {
        this.repository = repository;
    }

    public Loan findOneWithNotFoundDetection(final Long id) {
        final Loan loan = this.repository.findOne(id);
        if (loan == null) { throw new LoanNotFoundException(id); }
        return loan;
    }

    public Loan findOneWithNotFoundDetectionAndLazyInitialize(final Long id) {
        final Loan loan = this.repository.findOne(id);
        if (loan == null) { throw new LoanNotFoundException(id); }
        initializeLazyEntities(loan);
        return loan;
    }

    private void initializeLazyEntities(final Loan loan) {
        Hibernate.initialize(loan.getLoanPurpose());
        Hibernate.initialize(loan.getLoanOfficerHistory());
        Hibernate.initialize(loan.getFund());
        Hibernate.initialize(loan.getCollateral());
    }

    public Collection<Loan> findActiveLoansByLoanIdAndGroupId(final Long clientId, final Long groupId) {
        final Collection<Integer> loanStatuses = new ArrayList<>(Arrays.asList(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(),
                LoanStatus.APPROVED.getValue(), LoanStatus.ACTIVE.getValue(), LoanStatus.OVERPAID.getValue()));
        final Collection<Loan> loans = this.repository.findByClientIdAndGroupIdAndLoanStatus(clientId, groupId, loanStatuses);
        return loans;
    }

    public void save(final Loan loan) {
        this.repository.save(loan);
    }

    public List<Loan> findLoanByClientId(final Long clientId) {
        final List<Loan> loans = this.repository.findLoanByClientId(clientId);
        for (final Loan loan : loans) {
            initializeLazyEntities(loan);
        }
        return loans;
    }

    public List<Loan> findByClientIdAndGroupId(final Long clientId, final Long groupId) {
        final List<Loan> loans = this.repository.findByClientIdAndGroupId(clientId, groupId);
        for (final Loan loan : loans) {
            initializeLazyEntities(loan);
        }
        return loans;
    }

    public Loan getActiveLoanByAccountNumber(final String accountNumber) {
        return this.repository.findActiveLoanByAccountNumber(accountNumber);
    }

    public Loan findOneWithNotFoundDetectionAndLazyInitialize(final String accountNumber) {
        final Loan loan = this.repository.findByAccountNumber(accountNumber);
        if (loan == null) { throw new LoanNotFoundException(accountNumber); }
        initializeLazyEntities(loan);
        return loan;
    }

}