package com.finflux.mandates.data;

public class MandateProcessCounts {
        private Integer totalRecords = 0;
        private Integer successRecords = 0;
        private Integer failedRecords = 0;
        private Integer unprocessedRecords = 0;

        public Integer getTotalRecords() {
                return totalRecords;
        }

        public Integer getSuccessRecords() {
                return successRecords;
        }

        public Integer getFailedRecords() {
                return failedRecords;
        }

        public Integer getUnprocessedRecords() {
                return unprocessedRecords;
        }

        public void setTotalRecords(Integer totalRecords) {
                this.totalRecords = totalRecords;
        }

        public void setSuccessRecords(Integer successRecords) {
                this.successRecords = successRecords;
        }

        public void setFailedRecords(Integer failedRecords) {
                this.failedRecords = failedRecords;
        }

        public void setUnprocessedRecords(Integer unprocessedRecords) {
                this.unprocessedRecords = unprocessedRecords;
        }
}
