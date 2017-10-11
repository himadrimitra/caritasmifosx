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

import java.util.Collection;

import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanTransactionRepositoryWrapper {

    private final LoanTransactionRepository loanTransactionRepository;

    @Autowired
    public LoanTransactionRepositoryWrapper(LoanTransactionRepository loanTransactionRepository) {
        this.loanTransactionRepository = loanTransactionRepository;
    }

    public void save(final LoanTransaction loanTransaction) {
        this.loanTransactionRepository.save(loanTransaction);
    }
    
    public void save(final Collection<LoanTransaction>  loanTransactions) {
        this.loanTransactionRepository.save(loanTransactions);
    }

    public LoanTransaction findOneWithNotFoundDetection(final Long id) {
        final LoanTransaction loanTransaction = this.loanTransactionRepository.findOne(id);
        if (loanTransaction == null) { throw new LoanTransactionNotFoundException(id); }
        org.hibernate.Hibernate.initialize(loanTransaction.getGlimTransaction());
        return loanTransaction;
    }
    
    public LoanTransaction findOneWithLoanAccountNumberAndTransactionId(final Long id, final String loanAccountNumber) {
        final LoanTransaction loanTransaction = this.loanTransactionRepository.findOneWithTransactionIdAndLoanAccountNumber(id, loanAccountNumber);
        if (loanTransaction == null) { throw new LoanTransactionNotFoundException(id, loanAccountNumber); }
        return loanTransaction;
    }

}
