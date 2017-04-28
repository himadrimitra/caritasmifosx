package com.finflux.mandates.fileformat;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.ProcessResponseData;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

@Component("DefaultExcelBasedTransactionsFileFormatHelper")
public class DefaultExcelBasedTransactionsFileFormatHelper implements TransactionsFileFormatHelper{

        public static final String xlsContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        private String[] headersForTransactionsDownload = {"Corporate Utility code","Corporate Name","UMRN","Customer to be debited",
                "Customer IFSC/MICR","Customer Debit AC","Transaction ID/REF","Amount (Rs)","Date of Txn","File No"};

        private int[] contentsSpecForTransactionsDownload = {UTILITY_CODE, SPONSOR_BANK, UMRN, NAME_PRIMARY_ACNT_HOLDER,
                MICR_OR_IFSC, BANK_ACCOUNT_NUMBER, REFERENCE, AMOUNT, DUE_DATE, FILE_NO};

        private String[] headersForTransactionsUpload = {"Corporate User No","Corporate Name","UMRN","Customer to be debited",
                "Customer IFSC","Customer Debit AC","Transaction ID/REF","Amount (Rs)","Date of Txn","File No","Status","Reason"};

        // reference, status, failure reason, amount, transaction date
        private int[] specialValueColNums = {6, 10, 11, 7, 8};

        @Override
        public FileData formatDownloadFile(MandatesProcessData processData, NACHCredentialsData nachProperties,
                Collection<MandateTransactionsData> transactionsToProcess) throws IOException, InvalidFormatException {

                int rowNum = 0;
                Workbook workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet();
                Row row = sheet.createRow(rowNum);
                String[] headers = getHeadersForTransactionsDownload();
                for(int i=0; i<headers.length; i++){
                        row.createCell(i).setCellValue(headers[i]);
                }
                rowNum++;

                for(MandateTransactionsData data : transactionsToProcess){
                        row = sheet.createRow(rowNum);
                        int[] contents = getContentSpecForTransactionsDownload();
                        for(int i=0; i < contents.length; i++){
                                row.createCell(i).setCellValue(getDataValue(contents[i], nachProperties, data, processData));
                        }
                        rowNum++;
                }
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                workbook.write(outStream);

                return new FileData(new ByteArrayInputStream(outStream.toByteArray()),
                        "MandatesTransactions"+processData.getId()+".xls", xlsContentType);
        }

        @Override
        public Collection<ProcessResponseData> formatTransactionsResponseData(MandatesProcessData processData,
                NACHCredentialsData nachProperties, FileData fileData) throws IOException, InvalidFormatException, ParseException {
                Collection<ProcessResponseData> ret = new ArrayList<>();
                Workbook workbook = WorkbookFactory.create(fileData.file());
                Sheet sheet = workbook.getSheetAt(0);
                if(!isValidExcelHeader(sheet.getRow(0), getHeadersForTransactionsUpload())){
                        throw new InvalidFormatException("Excel Header is Invalid");
                }
                int maxRowIx = sheet.getLastRowNum();
                for(int rowIx = 1; rowIx <= maxRowIx; rowIx++){
                        Row row = sheet.getRow(rowIx);
                        if(row == null){
                                continue;
                        }
                        ProcessResponseData data = new ProcessResponseData();
                        short maxColIx = row.getLastCellNum();
                        for(short colIx=0; colIx <= maxColIx; colIx++) {
                                Cell cell = row.getCell(colIx);
                                if(cell == null) {
                                        continue;
                                }
                                String cellValue = getCellValueAsString(cell);
                                extractSpecialValues(colIx, cellValue, data);
                        }
                        data.setRowId(rowIx);
                        if(!StringUtils.isEmpty(data.getReference()) && data.getTransactionDate() != null) {
                            ret.add(data);
                        }
                       
                }

                return ret;
        }

        @Override
        public FileData updateProcessStatusToFile(MandatesProcessData processData, Collection<ProcessResponseData> responseDatas,
                FileData fileData) throws IOException, InvalidFormatException {

                Workbook workbook = WorkbookFactory.create(fileData.file());
                Sheet sheet = workbook.getSheetAt(0);
                Row row = sheet.getRow(0);
                String[] headers = getHeadersForTransactionsUpload();
                row.createCell(headers.length).setCellValue("Process Status");
                row.createCell(headers.length+1).setCellValue("Process Description");

                for(ProcessResponseData data : responseDatas){
                        row = sheet.getRow(data.getRowId());
                        row.createCell(headers.length).setCellValue(data.getProcessStatus());
                        row.createCell(headers.length+1).setCellValue(data.getProcessDesc());
                }
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                workbook.write(outStream);

                return new FileData(new ByteArrayInputStream(outStream.toByteArray()),
                        fileData.name(), fileData.contentType());
        }

        private void extractSpecialValues(short colIx, String cellValue, ProcessResponseData data) throws ParseException {
                int[] specialValueColumnNumbers = getSpecialValueColumnNumbers();
                if(specialValueColumnNumbers[0] != -1 && specialValueColumnNumbers[0] == colIx){
                        data.setReference(cellValue);
                }
                if(specialValueColumnNumbers[1] != -1 && specialValueColumnNumbers[1] == colIx){
                        data.setStatus(parseStatus(cellValue));
                }
                if(specialValueColumnNumbers[2] != -1 && specialValueColumnNumbers[2] == colIx){
                        data.setFailureReason(cellValue);
                }
                if(specialValueColumnNumbers[3] != -1 && specialValueColumnNumbers[3] == colIx){
                    if(!StringUtils.isEmpty(cellValue)) {
                        data.setAmount(new BigDecimal(cellValue));
                    }
                }
                if(specialValueColumnNumbers[4] != -1 && specialValueColumnNumbers[4] == colIx){
                    if(!StringUtils.isEmpty(cellValue)) {
                        data.setTransactionDate(new SimpleDateFormat(getDateFormatForTransactionUpload()).parse(cellValue));
                    }
                        
                }
        }

        protected String getDataValue(int content, NACHCredentialsData nachProperties, MandateTransactionsData data, MandatesProcessData processData) {
                String ret = "";
                switch (content){
                        case AMOUNT:
                                DecimalFormat format = new DecimalFormat();
                                format.setMaximumFractionDigits(2);
                                format.setMinimumFractionDigits(2);
                                ret = format.format(data.getPaymentDueAmount().setScale(2));
                                break;
                        case DUE_DATE:
                                ret = new SimpleDateFormat(getDateFormatForTransactionDownload()).format(data.getPaymentDueDate());
                                break;
                        case UMRN:
                                ret = null==data.getUmrn()?"":data.getUmrn();
                                break;
                        case NAME_PRIMARY_ACNT_HOLDER:
                                ret = data.getBankAccountHolderName();
                                break;
                        case BANK_ACCOUNT_NUMBER:
                                ret = data.getBankAccountNumber();
                                break;
                        case BANK_NAME:
                                ret = data.getBankName();
                                break;
                        case BRANCH_NAME:
                                ret = data.getBranchName();
                                break;
                        case MICR:
                                ret = null==data.getMicr()?"":data.getMicr();
                                break;
                        case IFSC:
                                ret = null==data.getIfsc()?"":data.getIfsc();
                                break;
                        case BANK_ACCOUNT_TYPE:
                                ret = data.getAccountType().getCode();
                                break;
                        case REFERENCE:
                                ret = data.getLoanAccountNo();
                                break;
                        case BLANK:
                                ret = "";
                                break;
                        case UTILITY_CODE:
                                ret = nachProperties.getCORPORATE_UTILITY_CODE();
                                break;
                        case SPONSOR_BANK:
                                ret = nachProperties.getSPONSOR_BANK();
                                break;
                        case FILE_NO:
                                ret = processData.getId().toString();
                                break;
                        case MICR_OR_IFSC:
                                ret = (null != data.getIfsc())? data.getIfsc(): data.getMicr();
                                break;

                }
                return ret;
        }

        protected String parseStatus(String cellValue) {
                String value = cellValue.toUpperCase();
                String ret = "INVALID";
                switch (value){
                        case "SUCCESS":
                        case "ACCEPTED":
                                ret = "SUCCESS";
                                break;
                        case "REJECTED":
                        case "FAILED":
                                ret = "FAILED";
                                break;
                }
                return ret;
        }

        // reference, status, failure reason, amount, transaction date
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }

        protected String[] getHeadersForTransactionsUpload() {
                return headersForTransactionsUpload;
        }

        protected String getDateFormatForTransactionDownload() {
                return "dd/MM/yyyy";
        }

        protected String getDateFormatForTransactionUpload() {
                return "dd/MM/yyyy";
        }

        protected int[] getContentSpecForTransactionsDownload() {
                return contentsSpecForTransactionsDownload;
        }

        protected String[] getHeadersForTransactionsDownload() {
                return headersForTransactionsDownload;
        }

        private boolean isValidExcelHeader(Row header, String[] headerArray) {
                if (header.getPhysicalNumberOfCells() < headerArray.length) { return false; }
                int i = 0;
                for (Cell cell : header) {
                        i++;
                        if(i<=headerArray.length){
                                String excelHeaderCellData = cell.getStringCellValue();
                                excelHeaderCellData = excelHeaderCellData.replaceAll("(\\s|\\n)", "");
                                String headerCellData = (headerArray[cell.getColumnIndex()]);
                                headerCellData = headerCellData.replaceAll("(\\s|\\n)", "");
                                if (!excelHeaderCellData.equalsIgnoreCase(headerCellData)) { return false; }
                        }
                }
                return true;
        }

        private String getCellValueAsString(Cell cell) {
                String param = "";
                if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                        param = NumberToTextConverter.toText(cell.getNumericCellValue());
                } else {
                        param = cell.getStringCellValue();
                }
                return param;
        }

}
