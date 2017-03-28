package org.apache.fineract.infrastructure.configuration.data;

public class NACHCredentialsData {
        private final String PROCESSOR_QUALIFIER;
        private final String CORPORATE_UTILITY_CODE;
        private final String CORPORATE_UTILITY_NAME;
        private final String SPONSOR_BANK;
        private final String SPONSOR_BANK_CODE;

        public NACHCredentialsData(final String PROCESSOR_QUALIFIER,
                final String CORPORATE_UTILITY_CODE,
                final String CORPORATE_UTILITY_NAME,
                final String SPONSOR_BANK,
                final String SPONSOR_BANK_CODE){

                this.PROCESSOR_QUALIFIER = PROCESSOR_QUALIFIER;
                this.CORPORATE_UTILITY_CODE = CORPORATE_UTILITY_CODE;
                this.CORPORATE_UTILITY_NAME = CORPORATE_UTILITY_NAME;
                this.SPONSOR_BANK = SPONSOR_BANK;
                this.SPONSOR_BANK_CODE = SPONSOR_BANK_CODE;
        }

        public String getPROCESSOR_QUALIFIER() {
                return PROCESSOR_QUALIFIER;
        }

        public String getCORPORATE_UTILITY_CODE() {
                return CORPORATE_UTILITY_CODE;
        }

        public String getCORPORATE_UTILITY_NAME() {
                return CORPORATE_UTILITY_NAME;
        }

        public String getSPONSOR_BANK() {
                return SPONSOR_BANK;
        }

        public String getSPONSOR_BANK_CODE() {
                return SPONSOR_BANK_CODE;
        }
}
