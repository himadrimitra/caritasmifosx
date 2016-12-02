package com.finflux.transaction.execution.data;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.math.BigDecimal;

/**
 * Created by dhirendra on 30/11/16.
 */
public class BankTransactionDetail {

        private final Long transactionId;
        private final BankAccountDetailData debiter;
        private final BankAccountDetailData beneficiary;
        private final EnumOptionData transferType;
        private final Integer entityTypeId;
        private final Long entityId;
        private final Long entityTxnId;
        private final BigDecimal amount;
        private final EnumOptionData status;

        public BankTransactionDetail(Long transactionId, BankAccountDetailData debiter, BankAccountDetailData beneficiary,
									 Integer entityTypeId, Long entityId, Long entityTxnId, BigDecimal amount, EnumOptionData transferType,
									 EnumOptionData status ) {
                this.debiter = debiter;
                this.beneficiary = beneficiary;
                this.transferType = transferType;
                this.entityTypeId = entityTypeId;
                this.entityId = entityId;
                this.entityTxnId = entityTxnId;
                this.amount = amount;
                this.status = status;
                this.transactionId = transactionId;
        }

        public Long getTransactionId() {
                return transactionId;
        }

        public BankAccountDetailData getDebiter() {
                return debiter;
        }

        public BankAccountDetailData getBeneficiary() {
                return beneficiary;
        }

        public EnumOptionData getTransferType() {
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

        public EnumOptionData getStatus() {
                return status;
        }
}
