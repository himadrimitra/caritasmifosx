package com.finflux.mandates.fileformat;

import com.finflux.mandates.data.ProcessResponseData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.Collection;

public interface MandatesFileFormatHelper {
        int ACTION = 1;
        int UMRN = 2;
        int REQUEST_DATE = 3;
        int BANK_ACCOUNT_TYPE = 4;
        int BANK_ACCOUNT_NUMBER = 5;
        int MICR = 6;
        int IFSC = 7;
        int DEBIT_AMOUNT = 8;
        int FREQUENCY = 9;
        int DEBIT_TYPE = 10;
        int REF_1 = 11;
        int REF_2 = 12;
        int PHONE = 13;
        int EMAIL = 14;
        int START_DATE = 15;
        int END_DATE = 16;
        int UNTIL_CANCELLED = 17;
        int NAME_PRIMARY_ACNT_HOLDER = 18;
        int BLANK = 19;
        int UTILITY_CODE = 20;
        int SPONSOR_BANK = 21;
        int MICR_OR_IFSC = 22;
        int NUM_OF_INSTALLMENTS = 23;
        int STATUS = 24;
        int REASON = 25;
        int BANK_NAME = 26;
        int BRANCH_NAME = 27;

        FileData formatDownloadFile(MandatesProcessData processData, NACHCredentialsData nachProperties, Collection<MandateData> mandatesToProcess)
                throws IOException, InvalidFormatException;

        Collection<ProcessResponseData> formatMandateResponseData(MandatesProcessData processData, NACHCredentialsData nachProperties, FileData fileData)
                throws IOException, InvalidFormatException;

        FileData updateProcessStatusToFile(MandatesProcessData processData, Collection<ProcessResponseData> responseDatas, FileData fileData)
                throws IOException, InvalidFormatException;
}
