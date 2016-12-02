package com.finflux.transaction.execution.provider.rbl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 23/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RBLFundTransferResponse {

        @JsonProperty("Single_Payment_Corp_Resp")
        private RBLSinglePaymentResponse paymentResponse;

        public RBLSinglePaymentResponse getPaymentResponse() {
                return paymentResponse;
        }

        public void setPaymentResponse(RBLSinglePaymentResponse paymentResponse) {
                this.paymentResponse = paymentResponse;
        }
}
