/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.mandates.fileformat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.springframework.stereotype.Component;

import com.finflux.mandates.processor.NachStatusValues;
import com.finflux.portfolio.loan.mandate.data.MandateData;

@Component("CHAITANYAMandatesFileFormatHelper")
public class CHAITANYAMandatesFileFormatHelper extends DefaultExcelBasedMandatesFileFormatHelper {

    private final static String ACTIVE = "ACTIVE";

    private String[] headersForMandateDownload = { "SRNO", "UTILITYCODE", "ACTION", "UMRN", "MANDATE_DATE", "ACCNUMBER", "AC_TYPE",
            "IFSC_MICR", "BANK", "COLLECTION TYPE", "AMOUNT", "REFNO1", "REFNO2", "FREQUENCY", "START_DATE", "END_DATE", "UNTILCANCEL",
            "CUST_NAME", "PHONE", "MOBILE", "MAIL" };

    private int[] contentsSpecForMandateDownload = { SR_NO, UTILITY_CODE, ACTION, UMRN, REQUEST_DATE, BANK_ACCOUNT_NUMBER,
            BANK_ACCOUNT_TYPE, MICR_OR_IFSC, BANK_NAME, DEBIT_TYPE, DEBIT_AMOUNT, REF_1, REF_2, FREQUENCY, START_DATE, END_DATE,
            UNTIL_CANCELLED, APPLICANT_NAME, ALTERNATE_MOBILE_NO, PHONE, EMAIL };

    private String[] headersForMandateUpload = { "SRNO", "MANDATE_DATE", "MANDATE_ID", "UMRN", "CUST_REFNO", "SCH_REFNO", "CUST_NAME",
            "BANK", "BRANCH", "BANK_CODE", "AC_TYPE", "ACNO", "AMOUNT", "FREQUENCY", "DEBIT_TYPE", "START_DATE", "END_DATE", "UNTILCANCEL",
            "TEL_NO", "MOBILE_NO", "MAIL_ID", "UPLOAD_BATCH", "UPLOAD_DATE", "RESPONSE_DATE", "UTILITY_CODE", "UTILITY_NAME", "STATUS",
            "STATUS_CODE", "REASON", "LOTNO" };

    // reference, status, failure reason, umrn
    private int[] specialValueColNums = { 4, 26, 28, 3 };

    // reference, status, failure reason, umrn
    @Override
    protected int[] getSpecialValueColumnNumbers() {
        return specialValueColNums;
    }

    @Override
    protected String getDateFormatForMandateDownload() {
        return "dd/MM/yyyy";
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
            case NachStatusValues.SUCCESS:
            case ACTIVE:
                ret = NachStatusValues.SUCCESS;
            break;
            case NachStatusValues.REJECTED:
            case NachStatusValues.FAILED:
                ret = NachStatusValues.FAILED;
            break;
        }
        return ret;
    }

    @Override
    protected String getDataValue(int contentSpec, NACHCredentialsData nachProperties, MandateData data, int index) {
        String ret = "";
        switch (contentSpec) {
            case ACTION:
                Long mandateStatus = data.getMandateStatus().getId();
                if (mandateStatus == 100 || mandateStatus == 101) {
                    ret = "N";
                } else if (mandateStatus == 200 || mandateStatus == 201) {
                    ret = "U";
                } else if (mandateStatus == 300 || mandateStatus == 301) {
                    ret = "C";
                }
            break;
            case BANK_ACCOUNT_TYPE:
                ret = data.getAccountType().getCode();
                if (ret.equalsIgnoreCase("SB")) {
                    ret = "S";
                } else if (ret.equalsIgnoreCase("CA")) {
                    ret = "C";
                } else if (ret.equalsIgnoreCase("Other")) {
                    ret = "O";
                }

            break;
            case FREQUENCY:
                ret = data.getDebitFrequency().getCode();
                if (ret.equalsIgnoreCase("MONTHLY")) {
                    ret = "M";
                } else if (ret.equalsIgnoreCase("QUARTERLY")) {
                    ret = "Q";
                } else if (ret.equalsIgnoreCase("HALF YEARLY")) {
                    ret = "H";
                } else if (ret.equalsIgnoreCase("YEARLY")) {
                    ret = "Y";
                } else if (ret.equalsIgnoreCase("AS AND WHEN PRESENTED")) {
                    ret = "O";
                }
            break;
            case DEBIT_TYPE:
                ret = data.getDebitType().getCode();
                if (ret.equalsIgnoreCase("FIXED AMOUNT")) {
                    ret = "D";
                } else if (ret.equalsIgnoreCase("MAXIMUM AMOUNT")) {
                    ret = "M";
                }
            break;
            case UNTIL_CANCELLED:
                ret = (null != data.getPeriodUntilCancelled() && data.getPeriodUntilCancelled()) ? "Y" : "";
            break;
            case SR_NO:
                ret = String.valueOf(index + 1);
            break;
            case REF_2:
                ret = "";
            break;
            case ALTERNATE_MOBILE_NO:
                ret = data.getApplicantAlternateMobileNo() != null ? data.getApplicantAlternateMobileNo() : "";
            break;
            default:
                ret = super.getDataValue(contentSpec, nachProperties, data, index);
            break;
        }
        return ret;
    }
}