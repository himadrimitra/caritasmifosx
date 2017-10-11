package com.finflux.loanapplicationreference.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class CoApplicantData {
        private final Long id;
        private final Long clientId;
        private final String displayName;
        private final EnumOptionData status;
        private final String accountNo;

        public CoApplicantData(final Long id,
                final Long clientId,
                final String displayName,
                final EnumOptionData status,
                final String accountNo){
                this.id = id;
                this.clientId = clientId;
                this.displayName = displayName;
                this.status = status;
                this.accountNo = accountNo;
        }
}
