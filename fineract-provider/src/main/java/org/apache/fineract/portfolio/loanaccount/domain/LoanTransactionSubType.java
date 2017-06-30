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

public enum LoanTransactionSubType {

    INVALID(0, "loanTransactionType.invalid"),
    UNACCOUTABLE(1, "loanTransactionSubType.unaccountable"),
    REALIZATION_SUBSIDY(50, "loanTransactionSubType.realizationSubsidy"),
    PARTIAL_WRITEOFF(51, "loanTransactionSubType.partialWriteOff"),
    PRE_PAYMENT(52,"loanTransactionSubType.prepayment"),
    REPAYMENT_FOR_NPA_LOAN(53,"loanTransactionSubType.repaymentForNpaLoan");

    private final Integer value;
    private final String code;

    private LoanTransactionSubType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static LoanTransactionSubType fromInt(final Integer transactionSubTypeValue) {

        if (transactionSubTypeValue == null) { return LoanTransactionSubType.INVALID; }

        LoanTransactionSubType transactionSubType = null;
        switch (transactionSubTypeValue) {
            case 50:
                transactionSubType = LoanTransactionSubType.REALIZATION_SUBSIDY;
            break;
            case 1:
                transactionSubType = LoanTransactionSubType.UNACCOUTABLE;
            break;
            case 51:
                transactionSubType = LoanTransactionSubType.PARTIAL_WRITEOFF;
            break;
            case 52:
                transactionSubType = LoanTransactionSubType.PRE_PAYMENT;
            break;
            case 53:
                transactionSubType = LoanTransactionSubType.REPAYMENT_FOR_NPA_LOAN;
            break;
            default:
                transactionSubType = LoanTransactionSubType.INVALID;
            break;
        }
        return transactionSubType;
    }
    
    public boolean isPrePayment() {
        return this.value.equals(LoanTransactionSubType.PRE_PAYMENT.getValue());
    }
    
    public boolean isRepaymentForNpaLoan() {
        return this.value.equals(LoanTransactionSubType.REPAYMENT_FOR_NPA_LOAN.getValue());
    }
}
