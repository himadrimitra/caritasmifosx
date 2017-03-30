package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

@Component("FLEXITransactionsFileFormatHelper")
public class FLEXITransactionsFileFormatHelper extends DefaultExcelBasedTransactionsFileFormatHelper{
        private String[] headersForTransactionsDownload = {"Consumer Code","Amount","Due Date"};

        private int[] contentsSpecForTransactionsDownload = {REFERENCE,AMOUNT,DUE_DATE};

        private String[] headersForTransactionsUpload = {"Unique Registration Number","Transaction ID","Presentment Mode",
                "Customer Name","Amount","Date","Status","Reason Code","Reason description"};

        // reference, status, failure reason, amount, transaction date
        private int[] specialValueColNums = {0, 6, 8, 4, 5};

        // reference, status, failure reason, amount, transaction date
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }

        protected String[] getHeadersForTransactionsUpload() {
                return headersForTransactionsUpload;
        }

        protected String getDateFormatForTransactionDownload() {
                return "dd-MMM-yy";
        }

        protected String getDateFormatForTransactionUpload() {
                return "dd/MM/yyyy";
        }

        protected int[] getContentSpecForTransactionsDownload() {
                return contentsSpecForTransactionsDownload;
        }

        protected String[] getHeadersForTransactionsDownload() {
                return headersForTransactionsDownload;
        }


}
