package com.finflux.transaction.execution.data;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 30/11/16.
 */
public class AccountTransactionDetail {

        private final Long transactionId;
        private final BankAccountDetailData debiter;
        private final BankAccountDetailData beneficiary;
        private final EnumOptionData transferType;
        private final Integer entityTypeId;
        private final Long entityId;
        private final Long entityTxnId;
        private final Double amount;
        private final EnumOptionData status;

        public AccountTransactionDetail(Long transactionId, BankAccountDetailData debiter, BankAccountDetailData beneficiary,
                Integer entityTypeId, Long entityId, Long entityTxnId, Double amount, EnumOptionData transferType,
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
}
