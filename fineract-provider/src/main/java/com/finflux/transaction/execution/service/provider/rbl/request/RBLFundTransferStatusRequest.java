package com.finflux.transaction.execution.service.provider.rbl.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 23/11/16.
 */
public class RBLFundTransferStatusRequest {

        @JsonProperty("Single_Payment_Corp_Req")
        private RBLSinglePaymentStatusRequest singlePaymentStatusRequest;

        public RBLFundTransferStatusRequest(RBLSinglePaymentStatusRequest singlePaymentStatusRequest){
                this.singlePaymentStatusRequest = singlePaymentStatusRequest;
        }

        public RBLSinglePaymentStatusRequest getSinglePaymentStatusRequest() {
                return singlePaymentStatusRequest;
        }
}
