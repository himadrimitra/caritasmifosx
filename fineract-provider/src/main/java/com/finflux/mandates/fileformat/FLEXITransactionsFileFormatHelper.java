package com.finflux.mandates.fileformat;

import org.springframework.stereotype.Component;

import com.finflux.mandates.processor.NachStatusValues;

@Component("FLEXITransactionsFileFormatHelper")
public class FLEXITransactionsFileFormatHelper extends DefaultExcelBasedTransactionsFileFormatHelper {

    private final static String PAYMENT_SUCCESSFULL = "BILL PAYMENT SUCCESSFUL" ;
    
    private final static String PAYMENT_FAILED = "BILL PAYMENT FAILED" ;
    
    private final static String STATUS_AWAITED = "STATUS AWAITED" ;
    
    private String[] headersForTransactionsDownload = { "Consumer Code", "Amount", "Due Date" };

    private int[] contentsSpecForTransactionsDownload = { REFERENCE, AMOUNT, DUE_DATE };

    private String[] headersForTransactionsUpload = { "Unique Registration Number", "Transaction ID", "Presentment Mode", "Customer Name",
            "Amount", "Date", "Status", "Reason Code", "Reason description" };

    // reference, status, failure reason, amount, transaction date
    private int[] specialValueColNums = { 0, 6, 8, 4, 5 };

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
        return "dd-MMM-yy";
    }

    @Override
    protected String getDateFormatForTransactionUpload() {
        return "dd/MM/yyyy";
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
            case PAYMENT_SUCCESSFULL:
                ret = NachStatusValues.SUCCESS;
            break;
            case PAYMENT_FAILED:
                ret = NachStatusValues.FAILED;
            break;
            case STATUS_AWAITED:
                ret = NachStatusValues.INTERMEDIATE_STATE;
            break;
            default:
                ret = super.parseStatus(cellValue);
            break;
        }
        return ret;
    }
}
