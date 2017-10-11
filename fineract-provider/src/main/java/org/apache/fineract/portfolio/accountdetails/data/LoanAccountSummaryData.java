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
package org.apache.fineract.portfolio.accountdetails.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApplicationTimelineData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

/**
 * Immutable data object for loan accounts.
 */
@SuppressWarnings("unused")
public class LoanAccountSummaryData {

    private final Long id;
    private final String accountNo;
    private final String externalId;
    private final Long productId;
    private final String productName;
    private final String shortProductName;
    private final LoanStatusEnumData status;
    private final EnumOptionData loanType;
    private final Integer loanCycle;
    private final LoanApplicationTimelineData timeline;
    private final Boolean inArrears;
    private final BigDecimal originalLoan;
    private final BigDecimal loanBalance;
    private final BigDecimal amountPaid;
    private final Boolean isCoApplicant;
    
    public LoanAccountSummaryData(final Long id, final String accountNo, final String externalId, final Long productId,
            final String loanProductName, final String shortLoanProductName, final LoanStatusEnumData loanStatus, final EnumOptionData loanType, final Integer loanCycle,
            final LoanApplicationTimelineData timeline, final Boolean inArrears,final BigDecimal originalLoan,final BigDecimal loanBalance,final BigDecimal amountPaid,
            final Boolean isCoApplicant) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = loanProductName;
        this.shortProductName = shortLoanProductName;
        this.status = loanStatus;
        this.loanType = loanType;
        this.loanCycle = loanCycle;
        this.timeline = timeline;
        this.inArrears = inArrears;
        this.loanBalance = loanBalance;
        this.originalLoan = originalLoan;
        this.amountPaid = amountPaid;
        this.isCoApplicant = isCoApplicant;
    }

    public static LoanAccountSummaryData instance(final Long id, final String accountNo, final String loanProductName,
            final String shortLoanProductName, final LoanStatusEnumData loanStatus, final BigDecimal originalLoan,
            final BigDecimal loanBalance, final BigDecimal amountPaid,final Boolean inArrears) {
        final String externalId = null;
        final EnumOptionData loanType = null;
        final LoanApplicationTimelineData timeline = null;
        final Integer loanCycle = null;
        final Long productId = null;
        final Boolean isCoApplicant = false;
        return new LoanAccountSummaryData(id, accountNo, externalId, productId, loanProductName, shortLoanProductName, loanStatus,
                loanType, loanCycle, timeline, inArrears, originalLoan, loanBalance, amountPaid, isCoApplicant);
    }
    
    public static LoanAccountSummaryData formLoanAccountSummaryData(final Long id, final String accountNo, final String loanProductName
            ) {
         final String externalId = null;
         final EnumOptionData loanType = null;
         final LoanApplicationTimelineData timeline = null;
         final Boolean inArrears = null;
         final Integer loanCycle = null;
         final Long productId = null;
         final String shortLoanProductName = null;
         final LoanStatusEnumData loanStatus = null;
         final BigDecimal originalLoan = null;
         final BigDecimal loanBalance = null;
         final BigDecimal amountPaid = null;
         final Boolean isCoApplicant = false;
         return new LoanAccountSummaryData(id, accountNo, externalId, productId, loanProductName, shortLoanProductName, loanStatus,
                 loanType, loanCycle, timeline, inArrears, originalLoan, loanBalance, amountPaid, isCoApplicant);
     }
}