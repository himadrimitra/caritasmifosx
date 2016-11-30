package com.finflux.transaction.execution.service.provider.rbl.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 23/11/16.
 */
public class RBLFundTransferRequest {

        @JsonProperty("Single_Payment_Corp_Req")
        private RBLSinglePaymentRequest singlePaymentRequest;

        public RBLFundTransferRequest(RBLSinglePaymentRequest singlePaymentRequest){
                this.singlePaymentRequest = singlePaymentRequest;
        }

        public RBLSinglePaymentRequest getSinglePaymentRequest() {
                return singlePaymentRequest;
        }
}
