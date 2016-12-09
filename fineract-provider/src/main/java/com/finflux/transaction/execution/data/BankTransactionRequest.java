package com.finflux.transaction.execution.data;

import java.math.BigDecimal;

import com.finflux.portfolio.bank.data.BankAccountDetailData;

/**
 * Created by dhirendra on 30/11/16.
 */
public class BankTransactionRequest {

        private final BankAccountDetailData debiter;
        private final BankAccountDetailData beneficiary;
        private final TransferType transferType;
        private final Integer entityTypeId;
        private final Long entityId;
        private final Long entityTxnId;
        private final BigDecimal amount;
        private final String reason;

        public BankTransactionRequest(BankAccountDetailData debiter, BankAccountDetailData beneficiary,
									  Integer entityTypeId, Long entityId, Long entityTxnId, BigDecimal amount,
									  String reason, TransferType transferType) {
                this.debiter = debiter;
                this.beneficiary = beneficiary;
                this.transferType = transferType;
                this.entityTypeId = entityTypeId;
                this.entityId = entityId;
                this.entityTxnId = entityTxnId;
                this.amount = amount;
                this.reason = reason;
        }

        public BankTransactionRequest(BankAccountDetailData debiter, BankAccountDetailData beneficiary,
									  Integer entityTypeId, Long entityId, Long entityTxnId, BigDecimal amount, String reason) {
                this(debiter, beneficiary, entityTypeId, entityId, entityTxnId, amount,reason,TransferType.NEFT);
        }

        public BankAccountDetailData getDebiter() {
                return debiter;
        }

        public BankAccountDetailData getBeneficiary() {
                return beneficiary;
        }

        public TransferType getTransferType() {
                return transferType;
        }

        public Integer getEntityTypeId() {
                return entityTypeId;
        }

        public Long getEntityId() {
                return entityId;
        }

        public Long getEntityTxnId() {
                return entityTxnId;
        }

        public BigDecimal getAmount() {
                return amount;
        }

        public String getReason() {
                return reason;
        }
}
