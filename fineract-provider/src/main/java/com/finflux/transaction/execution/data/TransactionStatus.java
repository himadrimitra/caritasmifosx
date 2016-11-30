package com.finflux.transaction.execution.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhirendra on 25/11/16.
 */
public enum TransactionStatus {
        DRAFT(1, "transactionStatus.initiated"),
        INITIATED(2, "transactionStatus.initiated"),
        SUCCESS(3, "transactionStatus.success"),
        FAILED(4, "transactionStatus.failed"),
        PENDING(5, "transactionStatus.pending");
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

