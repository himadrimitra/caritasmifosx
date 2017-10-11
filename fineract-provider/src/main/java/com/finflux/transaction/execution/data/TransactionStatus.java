package com.finflux.transaction.execution.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 25/11/16.
 */
public enum TransactionStatus {
        DRAFTED(1, "transactionStatus.draft"),
        SUBMITTED(2, "transactionStatus.submitted"),
        INITIATED(3, "transactionStatus.initiated"),
        PENDING(4, "transactionStatus.pending"),
        SUCCESS(5, "transactionStatus.success"),
        FAILED(6, "transactionStatus.failed"),
        CLOSED(7, "transactionStatus.closed"),
        ERROR(8, "transactionStatus.error"),
        RETRIED(9, "transactionStatus.retried"),
        REJECTED(10, "transactionStatus.rejected");

        private static final Map<Integer, TransactionStatus> intToEnumMap = new HashMap<>();

        static {
                for (final TransactionStatus status : TransactionStatus.values()) {
                        intToEnumMap.put(status.value, status);
                }
        }

        private final Integer value;
        private final String code;

        TransactionStatus(final Integer value, final String code) {
                this.value = value;
                this.code = code;
        }

        public static TransactionStatus fromInt(final int i) {
                final TransactionStatus status = intToEnumMap.get(Integer.valueOf(i));
                return status;
        }

        public EnumOptionData getEnumOptionData() {
                return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
        }


        public Integer getValue() {
                return this.value;
        }

        public String getCode() {
                return this.code;
        }

}

