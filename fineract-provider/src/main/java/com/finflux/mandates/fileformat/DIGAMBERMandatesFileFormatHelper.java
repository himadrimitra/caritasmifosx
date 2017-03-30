package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

@Component("DIGAMBERMandatesFileFormatHelper")
public class DIGAMBERMandatesFileFormatHelper extends DefaultExcelBasedMandatesFileFormatHelper{
        private String[] headersForMandateDownload = {"Date","Utility Code","UMRN No.","Sponsor Bank","Client Name",
                "Applicant Name","Bank Account Holder Name","Bank Name","BranchName","Bank Account Number","MICR/IFSC","Account type",
                "Start Date","End Date","Amount (Rs.)","No. of Installments","Frequency","Reference No.1","Reference No.2"};

        private int[] contentsSpecForMandateDownload = {REQUEST_DATE, UTILITY_CODE, UMRN, SPONSOR_BANK, BLANK,
                BLANK, NAME_PRIMARY_ACNT_HOLDER, BANK_NAME, BRANCH_NAME, BANK_ACCOUNT_NUMBER, MICR_OR_IFSC, BANK_ACCOUNT_TYPE,
                START_DATE, END_DATE, DEBIT_AMOUNT, NUM_OF_INSTALLMENTS, FREQUENCY, REF_1, BLANK};

        private String[] headersForMandateUpload = {"srno","UMRN NO.","Status","Date","Sponser Bank Code","Utility Code",
                "Name of Company","Action","Account Type","Account Holder's Name","Destination Account Number","Destination Bank",
                "Destination Branch","IFSC/MICR","Debit Amount","Consumer Reference Number","Plan Reference Number","Frequency",
                "Start Date","End Date","Customer Additional Information","Telephone","Mobile","Mail id","Due Date","Remarks1",
                "Remarks2","Remarks3","Status","Return Reason"};

        // reference, status, failure reason, umrn
        private int[] specialValueColNums = {25, 28, 29, 1};

        protected String getDateFormatForMandateDownload() {
                return "dd/MM/yyyy";
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
