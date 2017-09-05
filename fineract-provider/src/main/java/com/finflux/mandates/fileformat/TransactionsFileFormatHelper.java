package com.finflux.mandates.fileformat;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.ProcessResponseData;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

public interface TransactionsFileFormatHelper {

        int AMOUNT = 1;
        int DUE_DATE = 2;
        int UMRN = 3;
        int NAME_PRIMARY_ACNT_HOLDER = 4;
        int BANK_ACCOUNT_NUMBER = 5;
        int BANK_NAME = 6;
        int BRANCH_NAME = 7;
        int MICR = 8;
        int IFSC = 9;
        int BANK_ACCOUNT_TYPE = 10;
        int REFERENCE = 11;
        int BLANK = 12;
        int UTILITY_CODE = 13;
        int SPONSOR_BANK = 14;
        int FILE_NO = 15;
        int MICR_OR_IFSC = 16;
        int TRANSACTION_TYPE = 17;
        
        FileData formatDownloadFile(MandatesProcessData processData, NACHCredentialsData nachProperties, Collection<MandateTransactionsData> transactionsToProcess)
                throws IOException, InvalidFormatException;

        Collection<ProcessResponseData> formatTransactionsResponseData(MandatesProcessData processData, NACHCredentialsData nachProperties, FileData fileData)
                throws IOException, InvalidFormatException, ParseException;

        FileData updateProcessStatusToFile(MandatesProcessData processData, Collection<ProcessResponseData> responseDatas, FileData fileData)
                throws IOException, InvalidFormatException;

}
