/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.transaction.execution.provider.rbl.request;

public class RBLRequestConstants {

    public static final String errorCode = "Schema Validation Failure.";
    public static final String requiredErrorCode = "Schema Validation Failure.Either of the mobile number or email should be present.";
    public static final String NEFT = "NEFT";
    public static final String RTGS = "RTGS";
    public static final String DD = "DD";
    public static final String FT = "FT";
    public static final String IMPS = "IMPS";

    public static final String transactionIdParam = "TransactionId";
    public static final String corporateIdParam = "CorporateId";
    public static final String makerIdParam = "MakerId";
    public static final String checkerIdParam = "CheckerId";
    public static final String approverIdParam = "ApproverId";
    public static final String amountParam = "Amount";
    public static final String debitAccountNumberParam = "Debitor Account Number";
    public static final String debitAccountNameParam = "Debitor Account Name";
    public static final String debitIfscCodeParam = "Debitor IFSC Code";
    public static final String debitMobileNumberParam = "Debitor Mobile Number";
    public static final String debitTrnParticularsParam = "Debit Transaction Particulars";
    public static final String debitTrnPartRemarksParam = "Debit Transaction Part Remarks";
    public static final String benificiaryAccountNumberParam = "Benificiary Account Number";
    public static final String benificiaryAccountNameParam = "Benificiary Account Name";
    public static final String benificiaryIfscCodeParam = "Benificiary IFSC Code";
    public static final String benificiaryAddressParam = "Benificiary Address";
    public static final String benificiaryEmailIdParam = "Benificiary Email Id";
    public static final String benificiaryMobileNumberParam = "Benificiary Mobile Number";
    public static final String benificiaryTrnParticularsParam = "Benificiary Transaction Particulars";
    public static final String benificiaryTrnPartRemarksParam = "Benificiary Transaction Part Remarks";
    public static final String benificiaryBankNameParam = "Benificiary Bank Name";
    public static final String rptCodeParam = "RptCode";
    public static final String remarksParam = "Remarks";
    public static final String modeOfPayParam = "Mode Of Pay";

}
