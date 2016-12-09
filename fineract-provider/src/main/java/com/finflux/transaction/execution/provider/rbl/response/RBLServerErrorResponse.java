package com.finflux.transaction.execution.provider.rbl.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by dhirendra on 25/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RBLServerErrorResponse {

        private String httpCode;

        private String httpMessage;

        private String moreInformation;

        public String getHttpCode() {
                return httpCode;
        }

        public void setHttpCode(String httpCode) {
                this.httpCode = httpCode;
        }

        public String getHttpMessage() {
                return httpMessage;
        }

        public void setHttpMessage(String httpMessage) {
                this.httpMessage = httpMessage;
        }

        public String getMoreInformation() {
                return moreInformation;
        }

        public void setMoreInformation(String moreInformation) {
                this.moreInformation = moreInformation;
        }
}
