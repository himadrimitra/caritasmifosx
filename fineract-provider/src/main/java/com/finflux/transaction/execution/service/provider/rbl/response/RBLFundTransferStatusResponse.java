package com.finflux.transaction.execution.service.provider.rbl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 23/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RBLFundTransferStatusResponse {

        @JsonProperty("Single_Payment_Corp_Resp")
        private RBLSinglePaymentStatusResponse paymentStatusResponse;

        public RBLSinglePaymentStatusResponse getpaymentStatusResponse() {
                return paymentStatusResponse;
        }

        public void setPaymentStatusResponse(RBLSinglePaymentStatusResponse paymentStatusResponse) {
                this.paymentStatusResponse = paymentStatusResponse;
        }
}
