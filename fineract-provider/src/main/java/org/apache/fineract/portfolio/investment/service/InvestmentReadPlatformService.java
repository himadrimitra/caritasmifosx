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
package org.apache.fineract.portfolio.investment.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.fineract.portfolio.investment.data.LoanInvestmentData;
import org.apache.fineract.portfolio.investment.data.SavingInvestmentData;
import org.joda.time.LocalDate;


public interface InvestmentReadPlatformService {

    List<SavingInvestmentData> retriveLoanAccountsBySavingId(final Long savingId) throws SQLException;
    List<Long> retriveLoanIdBySavingId(final Long savingId);
    List<Long> retriveInvestedAmountBySavingId(final Long savingId);
    List<Long> retriveInvestedAmountByLoanId(final Long loanId);
    Long retriveSavingInvestmentId(final Long savingId, Long loanId, LocalDate startDate);
    List<LoanInvestmentData> retriveSavingAccountsByLoanId(final Long laonId);
    Long retriveLoanInvestmentId(final Long loanId, Long svingId, String startDate);
    List<Long> retriveSavingIdByLoanId(final Long loanId);
    
    Integer retriveSavingInvestmentIdForClose(final Long savingId, Long loanId, String startDate);
    Long retriveSavingInvestmentIdForUpdate(final Long savingId, Long loanId, String startDate);
    
    Long retriveLoanInvestmentIdForUpdate(final Long loanId, Long savingId, String startDate);
    
    boolean isSavingAccountLinkedWithInvestment(final Long savingId);
    
    
    boolean isSavingInvestmentAlreadyDoneWithSameDate(final Long savingId, LocalDate investmentStartDate);
    
    boolean isLoanInvestmentAlreadyDoneOnSameDate(final Long loanId, LocalDate investmentStartDate);
    
}
