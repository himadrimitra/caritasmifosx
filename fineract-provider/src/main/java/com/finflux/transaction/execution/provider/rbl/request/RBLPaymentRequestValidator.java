/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.transaction.execution.provider.rbl.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.springframework.stereotype.Component;

import com.finflux.transaction.execution.api.BankTransactionApiConstants;

@Component
public class RBLPaymentRequestValidator {

    public void validateNEFTSinglePaymentRequest(RBLSinglePaymentRequest singlePaymentRequest) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(BankTransactionApiConstants.INITIATE_BANK_TRANSACTION_RESOURCE);

        final String transactionId = singlePaymentRequest.getHeader().getTransactionId();
        baseDataValidator.reset().parameter(RBLRequestConstants.transactionIdParam).value(transactionId).notNull();
        if (transactionId != null && (!transactionId.matches("([A-Za-z0-9_]){1,16}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.transactionIdParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.transactionIdParam);
        }

        final String corporateId = singlePaymentRequest.getHeader().getCorpId();
        baseDataValidator.reset().parameter(RBLRequestConstants.corporateIdParam).value(corporateId).notNull();
        if (corporateId != null && (!corporateId.matches("([A-Za-z0-9_]){1,20}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.corporateIdParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.corporateIdParam);
        }

        final String makerId = singlePaymentRequest.getHeader().getMakerId();
        if (makerId != null && (!makerId.matches("([A-Za-z0-9_]){0,20}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.makerIdParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.makerIdParam);
        }

        final String checkerId = singlePaymentRequest.getHeader().getMakerId();
        if (checkerId != null && (!checkerId.matches("([A-Za-z0-9_]){0,20}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.checkerIdParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.checkerIdParam);
        }

        final String approverId = singlePaymentRequest.getHeader().getMakerId();
        if (approverId != null && (!approverId.matches("([A-Za-z0-9_]){0,20}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.approverIdParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.approverIdParam);
        }

        /*
         * final String amount = singlePaymentRequest.getBody().getAmount();
         * baseDataValidator.reset().parameter(BankTransactionApiConstants.
         * INITIATE_BANK_TRANSACTION_RESOURCE).value(amount).notNull(); if
         * (!amount.matches("([0-9.]){1,20}")) {
         * baseDataValidator.reset().parameter(BankTransactionApiConstants.
         * INITIATE_BANK_TRANSACTION_RESOURCE)
         * .failWithCode(RBLRequestConstants.errorCode,
         * RBLRequestConstants.amountParam); }
         */

        final String debitAccountNumber = singlePaymentRequest.getBody().getDebitAccountNumber();
        baseDataValidator.reset().parameter(RBLRequestConstants.debitAccountNumberParam).value(debitAccountNumber).notNull();
        if (debitAccountNumber != null && (!debitAccountNumber.matches("([A-Za-z0-9_]){1,16}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.debitAccountNumberParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.debitAccountNumberParam);
        }

        final String debitAccountName = singlePaymentRequest.getBody().getDebitAccountName();
        baseDataValidator.reset().parameter(RBLRequestConstants.debitAccountNameParam).value(debitAccountName).notNull();
        if (debitAccountName != null && (!debitAccountName.matches("([A-Za-z ]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.debitAccountNameParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.debitAccountNameParam);
        }

        final String debitIfsc = singlePaymentRequest.getBody().getDebitIFSC();
        if (debitIfsc != null && (!debitIfsc.matches("([A-Za-z_0-9]){1,15}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.debitIfscCodeParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.debitIfscCodeParam);
        }

        final String debitMobile = singlePaymentRequest.getBody().getDebitMobile();
        if (debitMobile != null && (!debitMobile.matches("([0-9]){10,10}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.debitMobileNumberParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.debitMobileNumberParam);
        }

        final String debitTrnParticulars = singlePaymentRequest.getBody().getDebitTxnParticulars();
        if (debitTrnParticulars != null && (!debitTrnParticulars.matches("([A-Za-z 0-9]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.debitTrnParticularsParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.debitTrnParticularsParam);
        }

        final String debitPartTrnRemarks = singlePaymentRequest.getBody().getDebitPartTxnRemarks();
        if (debitPartTrnRemarks != null && (!debitPartTrnRemarks.matches("([A-Za-z 0-9]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.debitTrnPartRemarksParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.debitTrnPartRemarksParam);
        }

        final String benificiaryIfsc = singlePaymentRequest.getBody().getBeneficiaryIFSC();
        baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryIfscCodeParam).value(benificiaryIfsc).notNull();
        if (benificiaryIfsc != null && (!benificiaryIfsc.matches("([A-Za-z_0-9]){1,15}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryIfscCodeParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.benificiaryIfscCodeParam);
        }

        final String benificiaryAccountNumber = singlePaymentRequest.getBody().getBeneficiaryAccountNumber();
        baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryAccountNumberParam).value(benificiaryAccountNumber).notNull();
        if (benificiaryAccountNumber != null && (!benificiaryAccountNumber.matches("([A-Za-z_0-9]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryAccountNumberParam)
                    .failWithCode(RBLRequestConstants.errorCode, RBLRequestConstants.benificiaryAccountNumberParam);
        }

        final String benificiaryName = singlePaymentRequest.getBody().getBeneficiaryName();
        baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryAccountNameParam).value(benificiaryName).notNull();
        if (benificiaryName != null && (!benificiaryName.matches("([A-Za-z /]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryAccountNameParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.benificiaryAccountNameParam);
        }

        final String benificiaryAddress = singlePaymentRequest.getBody().getBeneficiaryAccountNumber();
        baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryAddressParam).value(benificiaryAddress).notNull();
        if (benificiaryAddress != null && (!benificiaryAddress.matches("([A-Za-z 0-9]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryAddressParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.benificiaryAddressParam);
        }

        final String benificiaryBankName = singlePaymentRequest.getBody().getBeneficiaryBankName();
        if (benificiaryBankName != null && (!benificiaryBankName.matches("([A-Za-z -]){1,20}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryBankNameParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.benificiaryBankNameParam);
        }

        final String benificiaryEmail = singlePaymentRequest.getBody().getBeneficiaryEmail();
        if (benificiaryEmail != null) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryEmailIdParam).value(benificiaryEmail).ignoreIfNull()
                    .validateEmailAddress();
        }

        final String benificiaryMobile = singlePaymentRequest.getBody().getBeneficiaryMobile();
        if (benificiaryMobile != null && (!benificiaryMobile.matches("([0-9]){10,10}"))) {
            baseDataValidator.reset().parameter(BankTransactionApiConstants.INITIATE_BANK_TRANSACTION_RESOURCE)
                    .failWithCode(RBLRequestConstants.errorCode, RBLRequestConstants.benificiaryMobileNumberParam);
        }

        if (benificiaryEmail == null && benificiaryMobile == null) {
            baseDataValidator.reset().parameter(BankTransactionApiConstants.INITIATE_BANK_TRANSACTION_RESOURCE)
                    .failWithCode(RBLRequestConstants.requiredErrorCode);
        }

        final String benificiaryTrnParticulars = singlePaymentRequest.getBody().getDebitTxnParticulars();

        if (benificiaryTrnParticulars != null && (!benificiaryTrnParticulars.matches("([A-Za-z0-9 ]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryTrnParticularsParam)
                    .failWithCode(RBLRequestConstants.errorCode, RBLRequestConstants.benificiaryTrnParticularsParam);
        }

        final String benificiaryPartTrnRemarks = singlePaymentRequest.getBody().getDebitTxnParticulars();
        if (benificiaryPartTrnRemarks != null && (!benificiaryPartTrnRemarks.matches("([A-Za-z0-9 ]){1,50}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.benificiaryTrnPartRemarksParam)
                    .failWithCode(RBLRequestConstants.errorCode, RBLRequestConstants.benificiaryTrnPartRemarksParam);
        }

        final String modeOfPay = singlePaymentRequest.getBody().getModeOfPay();
        baseDataValidator.reset().parameter(RBLRequestConstants.modeOfPayParam).value(modeOfPay).notNull();
        if (modeOfPay != null) {
            baseDataValidator.reset().parameter(RBLRequestConstants.modeOfPayParam).value(modeOfPay).isOneOfTheseValues(
                    RBLRequestConstants.NEFT, RBLRequestConstants.RTGS, RBLRequestConstants.DD, RBLRequestConstants.IMPS,
                    RBLRequestConstants.FT);
        }

        final String rptCode = singlePaymentRequest.getBody().getRptCode();
        if (rptCode != null && (!rptCode.matches("([A-Za-z0-9_]){1,20}"))) {
            baseDataValidator.reset().parameter(RBLRequestConstants.rptCodeParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.rptCodeParam);
        }

        final String remarks = singlePaymentRequest.getBody().getRemarks();
        baseDataValidator.reset().parameter(RBLRequestConstants.remarksParam).value(remarks).notNull();
        if (remarks != null && (!remarks.matches("([A-Za-z0-9_]){1,50}"))) {

            baseDataValidator.reset().parameter(RBLRequestConstants.remarksParam).failWithCode(RBLRequestConstants.errorCode,
                    RBLRequestConstants.remarksParam);
        }

        final String signature = singlePaymentRequest.getSignature().getSignature();
        baseDataValidator.reset().parameter(BankTransactionApiConstants.INITIATE_BANK_TRANSACTION_RESOURCE).value(signature).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
