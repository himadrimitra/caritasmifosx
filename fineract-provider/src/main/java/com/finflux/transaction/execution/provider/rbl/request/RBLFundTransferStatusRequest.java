package com.finflux.transaction.execution.provider.rbl.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhirendra on 23/11/16.
 */
public class RBLFundTransferStatusRequest {

        @JsonProperty("get_Single_Payment_Status_Corp_Req")
        private RBLSinglePaymentStatusRequest singlePaymentStatusRequest;

        public RBLFundTransferStatusRequest(RBLSinglePaymentStatusRequest singlePaymentStatusRequest){
                this.singlePaymentStatusRequest = singlePaymentStatusRequest;
        }

        public RBLSinglePaymentStatusRequest getSinglePaymentStatusRequest() {
                return singlePaymentStatusRequest;
        }
}
