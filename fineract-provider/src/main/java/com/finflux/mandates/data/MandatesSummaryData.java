package com.finflux.mandates.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.math.BigDecimal;

public class MandatesSummaryData {

        private final EnumOptionData status;
        private final int count;
        private final BigDecimal amount;

        public MandatesSummaryData(final EnumOptionData status,
                final int count,
                final BigDecimal amount){

                this.status = status;
                this.count = count;
                this.amount = amount;
        }
}
