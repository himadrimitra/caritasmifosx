package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

import com.finflux.mandates.processor.NachStatusValues;

@Component("FLEXIMandatesFileFormatHelper")
public class FLEXIMandatesFileFormatHelper extends DefaultExcelBasedMandatesFileFormatHelper {

        private final static String FINAL_APPROVED = "FINAL APPROVED" ;
        private final static String FINAL_REJECTED = "FINAL REJECTED" ;
        private final static String TPSL_REJECTED = "TPSL REJECTED" ;
        private final static String NPCI_ACKNOWLEDGED = "NPCI ACKNOWLEDGED" ;
        
        private String[] headersForMandateDownload = {"Consumer Code","Applicant Name","Bank A/c holder Name","Bank Name","Branch Name",
                "Bank Account No","MICR","IFSC Code","Account Type","Email Id","Mobile No","Start Date","End Date","Amount ","Frequency"};

        private int[] contentsSpecForMandateDownload = {REF_1, APPLICANT_NAME, NAME_PRIMARY_ACNT_HOLDER, BANK_NAME, BRANCH_NAME,
                BANK_ACCOUNT_NUMBER, MICR, IFSC, BANK_ACCOUNT_TYPE, EMAIL, PHONE, START_DATE, END_DATE, DEBIT_AMOUNT, FREQUENCY};

        private String[] headersForMandateUpload = {"Sr No","Month","Unique Reference Number","Applicant Name","Bank A/c holder Name",
                "Bank Name","Branch Name","Bank Account No","MICR","IFSC Code","Account Type","Email Id","Mobile No","Start Date",
                "End Date","Amount","Frequency","Status","Rejected Reason","UMRN","Data Upload Date","Data Approved on",
                "NPCI Acknowledged Date","NPCI Response Date","Presentment Mode","Batch ID","Processor Unique No","Registration Type"};
        
        // reference, status, failure reason, umrn
        private int[] specialValueColNums = {2, 17, 18, 19};

        // reference, status, failure reason, umrn
        @Override
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }

        @Override
        protected String getDateFormatForMandateDownload() {
                return "ddMMyyyy";
        }

        @Override
        protected int[] getContentSpecForMandateDownload() {
                return contentsSpecForMandateDownload;
        }

        @Override
        protected String[] getHeadersForMandateDownload() {
                return headersForMandateDownload;
        }

        @Override
        protected String[] getHeadersForMandateUpload() {
                return headersForMandateUpload;
        }
        
    @Override
    protected String parseStatus(String cellValue) {
        String value = cellValue.toUpperCase();
        String ret = NachStatusValues.INVALID;
        switch (value) {
            case FINAL_APPROVED:
                ret = NachStatusValues.SUCCESS;
            break;
            case FINAL_REJECTED:
            case TPSL_REJECTED:
                ret = NachStatusValues.FAILED;
            break;
            case NPCI_ACKNOWLEDGED:
                ret = NachStatusValues.INTERMEDIATE_STATE;
            break;
            default:
                ret = super.parseStatus(cellValue) ;
                break ;
        }
        return ret;
    }

}
