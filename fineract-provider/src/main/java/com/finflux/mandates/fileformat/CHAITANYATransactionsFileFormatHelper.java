/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

import com.finflux.mandates.processor.NachStatusValues;

@Component("CHAITANYATransactionsFileFormatHelper")
public class CHAITANYATransactionsFileFormatHelper extends DefaultExcelBasedTransactionsFileFormatHelper {

    private final static String PAID = "PAID";

    private String[] headersForTransactionsDownload = { "UTILITYCODE", "TRANSACTIONTYPE", "SETTLEMENTDATE", "BENEFICIARYACHOLDERNAME",
            "AMOUNT", "DESTINATIONBANKCODE", "BENEFICIARYACNO", "TRANSACTIONREFERENCE", "UMRN" };

    private int[] contentsSpecForTransactionsDownload = { UTILITY_CODE, TRANSACTION_TYPE, DUE_DATE, NAME_PRIMARY_ACNT_HOLDER, AMOUNT,
            MICR_OR_IFSC, BANK_ACCOUNT_NUMBER, REFERENCE, UMRN };

    private String[] headersForTransactionsUpload = { "SRNO", "ECS_DATE", "SETTLEMENT DATE", "CUST_REFNO", "SCH_REFNO", "CUSTOMER_NAME",
            "AMOUNT", "REFNO", "UMRN", "STATUS", "REASON" };

    // reference, status, failure reason, amount, transaction date
    private int[] specialValueColNums = { 7, 9, 10, 6, 2 };

    // reference, status, failure reason, amount, transaction date
    @Override
    protected int[] getSpecialValueColumnNumbers() {
        return specialValueColNums;
    }

    @Override
    protected String[] getHeadersForTransactionsUpload() {
        return headersForTransactionsUpload;
    }

    @Override
    protected String getDateFormatForTransactionDownload() {
        return "dd-MM-yyyy";
    }

    @Override
    protected String getDateFormatForTransactionUpload() {
        return "dd-MM-yyyy";
    }

    @Override
    protected int[] getContentSpecForTransactionsDownload() {
        return contentsSpecForTransactionsDownload;
    }

    @Override
    protected String[] getHeadersForTransactionsDownload() {
        return headersForTransactionsDownload;
    }

    @Override
    protected String parseStatus(String cellValue) {
        String value = cellValue.toUpperCase();
        String ret = NachStatusValues.INVALID;
        switch (value) {
            case NachStatusValues.SUCCESS:
            case NachStatusValues.ACCEPTED:
            case PAID:
                ret = NachStatusValues.SUCCESS;
            break;
            case NachStatusValues.REJECTED:
            case NachStatusValues.FAILED:
                ret = NachStatusValues.FAILED;
            break;
        }
        return ret;
    }

}
