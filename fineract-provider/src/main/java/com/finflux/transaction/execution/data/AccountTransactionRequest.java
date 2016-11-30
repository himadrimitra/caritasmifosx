package com.finflux.transaction.execution.data;

import com.finflux.portfolio.bank.data.BankAccountDetailData;

import java.math.BigDecimal;

/**
 * Created by dhirendra on 30/11/16.
 */
public class AccountTransactionRequest {

        private final BankAccountDetailData debiter;
        private final BankAccountDetailData beneficiary;
        private final TransferType transferType;
        private final Integer entityTypeId;
        private final Long entityId;
        private final Long entityTxnId;
        private final BigDecimal amount;

        public AccountTransactionRequest(BankAccountDetailData debiter, BankAccountDetailData beneficiary,
                Integer entityTypeId, Long entityId, Long entityTxnId, BigDecimal amount, TransferType transferType) {
                this.debiter = debiter;
                this.beneficiary = beneficiary;
                this.transferType = transferType;
                this.entityTypeId = entityTypeId;
                this.entityId = entityId;
                this.entityTxnId = entityTxnId;
                this.amount = amount;
        }

        public AccountTransactionRequest(BankAccountDetailData debiter, BankAccountDetailData beneficiary,
                Integer entityTypeId, Long entityId, Long entityTxnId, BigDecimal amount) {
                this(debiter, beneficiary, entityTypeId, entityId, entityTxnId, amount,TransferType.NEFT);
        }
}
