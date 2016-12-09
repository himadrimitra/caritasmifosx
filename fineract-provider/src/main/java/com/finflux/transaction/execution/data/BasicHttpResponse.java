package com.finflux.transaction.execution.data;

import org.springframework.http.HttpStatus;

/**
 * Created by dhirendra on 25/11/16.
 */
public class BasicHttpResponse {
        private String httpStatusCode;
        private String errorCode;
        private String errorMessage;
        private Boolean isSuccess;


        public BasicHttpResponse(BasicHttpResponse httpResponse) {
                this.httpStatusCode = httpResponse.getHttpStatusCode();
                this.errorCode = httpResponse.getErrorCode();
                this.errorMessage = httpResponse.getErrorMessage();
                this.isSuccess = httpResponse.getSuccess();
        }

        public BasicHttpResponse(String statusCode) {
                this.httpStatusCode =  statusCode;
        }

        public BasicHttpResponse() {

        }

        public String getHttpStatusCode() {
                return httpStatusCode;
        }

        public void setHttpStatusCode(String httpStatusCode) {
                this.httpStatusCode = httpStatusCode;
        }

        public String getErrorCode() {
                return errorCode;
        }

        public void setErrorCode(String errorCode) {
                this.errorCode = errorCode;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
        }

        public Boolean getSuccess() {
                return isSuccess;
        }

        public void setSuccess(Boolean success) {
                isSuccess = success;
        }
}
