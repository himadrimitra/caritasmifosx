package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

@Component("NEEVTransactionsFileFormatHelper")
public class NEEVTransactionsFileFormatHelper extends DefaultExcelBasedTransactionsFileFormatHelper{
        private String[] headersForTransactionsDownload = {"Value / Settlement date  (DD/MM/YYYY)","Destination Account No.",
                "Destination Account Type ","Payor Bank MICR Code","Destination Account Holders Name","Amount",
                "UMRN Number","Unique Transaction Reference","Transaction Remarks"};

        private int[] contentsSpecForTransactionsDownload = {DUE_DATE, BANK_ACCOUNT_NUMBER,
                BANK_ACCOUNT_TYPE, MICR, NAME_PRIMARY_ACNT_HOLDER, AMOUNT,
                UMRN, REFERENCE, BLANK};

        private String[] headersForTransactionsUpload = {"Customer ","Customer Name ","Deposit Slip No","Deposit Date","Deposit Branch ",
                "Mandate Reference No","Mandate Type","DDI Ref No","ACH File Number","Transaction Reference ","Inst No","Inst Date",
                "Inst Amt","Customer Credit Acc ","Payer Acc No","Payer Acc Name","Payer Bank Acc Type","Payer Bank Branch ","IFSC Code",
                "MICR Code","Product Type","Product","Activation Date","Status","Reject Reason","Liquidation Date","Customer Ref No"};

        // reference, status, failure reason, amount, transaction date
        private int[] specialValueColNums = {10, 23, 24, 12, 25};

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
