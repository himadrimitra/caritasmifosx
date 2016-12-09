package com.finflux.transaction.execution.provider.rbl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 23/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RBLFundTransferStatusResponse {

        @JsonProperty("get_Single_Payment_Status_Corp_Res")
        private RBLSinglePaymentStatusResponse paymentStatusResponse;

        public RBLSinglePaymentStatusResponse getpaymentStatusResponse() {
                return paymentStatusResponse;
        }

        public void setPaymentStatusResponse(RBLSinglePaymentStatusResponse paymentStatusResponse) {
                this.paymentStatusResponse = paymentStatusResponse;
        }
}
