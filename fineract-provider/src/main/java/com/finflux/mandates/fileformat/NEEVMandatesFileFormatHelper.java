package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

@Component("NEEVMandatesFileFormatHelper")
public class NEEVMandatesFileFormatHelper extends DefaultExcelBasedMandatesFileFormatHelper{
        private String[] headersForMandateDownload = {"Action","UMRN Number","Date (DD-MM-YYYY)","Bank A/C Type","Bank Account Number",
                "MICR Code","IFSC Code","Debit Amount","Frequency","Debit Type","Reference 1","Reference 2","Phone No.","Email ID",
                "NACH Start Date (DD-MM-YYYY)","NACH End Date (DD-MM-YYYY)","Or Until Cancelled","Name of Primary Account Holder",
                "Name of Joint Account Holder (Name 2)","Name of Joint Account Holder (Name 3)",
                "Customer Additional Identification- Permanent ID of customer"};

        private int[] contentsSpecForMandateDownload = {ACTION, UMRN, REQUEST_DATE, BANK_ACCOUNT_TYPE, BANK_ACCOUNT_NUMBER,
                MICR, IFSC, DEBIT_AMOUNT, FREQUENCY, DEBIT_TYPE, REF_1, BLANK, PHONE, EMAIL,
                START_DATE, END_DATE, UNTIL_CANCELLED, NAME_PRIMARY_ACNT_HOLDER,
                BLANK, BLANK, BLANK};

        private String[] headersForMandateUpload = {"Customer ","Customer Name","Payer Bank","Payer Branch","Debtor Acc Name",
                "Debtor Acc No","Amt Limit","DDA Ref No","DDI Ref No","Subscriber Reference No","Origination Ref No","Status",
                "Approval Date","Effective Start Date","Effective End Date","Until Cancelled","Sponsor Bank Code","Txn Frequency",
                "MICR code","IFSC code","Credit Acc No","Payer Bank Acc Type","Payer Phone No","Payer Email ID",
                "Primary Acc Holder","Joint Acc Holder1","Joint Acc Holder2","Permanent ID","Mandate Status Date",
                "Rejection Reason","Submission Date"};

        // reference, status, failure reason, umrn
        private int[] specialValueColNums = {10, 11, 29, 7};

        protected String getDateFormatForMandateDownload() {
                return "dd-MM-yyyy";
        }

        protected int[] getContentSpecForMandateDownload() {
                return contentsSpecForMandateDownload;
        }

        protected String[] getHeadersForMandateDownload() {
                return headersForMandateDownload;
        }

        protected String[] getHeadersForMandateUpload() {
                return headersForMandateUpload;
        }

        // reference, status, failure reason, umrn
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }
}
