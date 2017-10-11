package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

@Component("DIGAMBERTransactionsFileFormatHelper")
public class DIGAMBERTransactionsFileFormatHelper extends DefaultExcelBasedTransactionsFileFormatHelper{

        private String[] headersForTransactionsDownload = {"Corporate Utility code","Corporate Name","UMRN","Customer to be debited",
                "Customer IFSC/MICR","Customer Debit AC","Transaction ID/REF","Amount (Rs)","Date of Txn","File No"};

        private int[] contentsSpecForTransactionsDownload = {UTILITY_CODE, SPONSOR_BANK, UMRN, NAME_PRIMARY_ACNT_HOLDER,
                MICR_OR_IFSC, BANK_ACCOUNT_NUMBER, REFERENCE, AMOUNT, DUE_DATE, FILE_NO};

        private String[] headersForTransactionsUpload = {"Corporate User No","Corporate Name","UMRN","Customer to be debited",
                "Customer IFSC","Customer Debit AC","Transaction ID/REF","Amount (Rs)","Date of Txn","File No","Status","Reason"};

        // reference, status, failure reason, amount, transaction date
        private int[] specialValueColNums = {6, 10, 11, 7, 8};

        // reference, status, failure reason, amount, transaction date
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }

        protected String[] getHeadersForTransactionsUpload() {
                return headersForTransactionsUpload;
        }

        protected String getDateFormatForTransactionDownload() {
                return "dd/MM/yyyy";
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
