package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

@Component("FLEXIMandatesFileFormatHelper")
public class FLEXIMandatesFileFormatHelper extends DefaultExcelBasedMandatesFileFormatHelper {

        private String[] headersForMandateDownload = {"Consumer Code","Applicant Name","Bank A/c holder Name","Bank Name","Branch Name",
                "Bank Account No","MICR","IFSC Code","Account Type","Email Id","Mobile No","Start Date","End Date","Amount ","Frequency"};

        private int[] contentsSpecForMandateDownload = {REF_1, BLANK, NAME_PRIMARY_ACNT_HOLDER, BANK_NAME, BRANCH_NAME,
                BANK_ACCOUNT_NUMBER, MICR, IFSC, BANK_ACCOUNT_TYPE, BLANK, BLANK, START_DATE, END_DATE, DEBIT_AMOUNT, FREQUENCY};

        private String[] headersForMandateUpload = {"Sr No","Month","Unique Reference Number","Applicant Name","Bank A/c holder Name",
                "Bank Name","Branch Name","Bank Account No","MICR","IFSC Code","Account Type","Email Id","Mobile No","Start Date",
                "End Date","Amount","Frequency","Status","Rejected Reason","UMRN","Data Upload Date","Data Approved on",
                "NPCI Acknowledged Date","NPCI Response Date","Presentment Mode","Batch ID","Processor Unique No","Registration Type"};

        // reference, status, failure reason, umrn
        private int[] specialValueColNums = {2, 17, 18, 19};

        // reference, status, failure reason, umrn
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }

        protected String getDateFormatForMandateDownload() {
                return "ddMMyyyy";
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

}
