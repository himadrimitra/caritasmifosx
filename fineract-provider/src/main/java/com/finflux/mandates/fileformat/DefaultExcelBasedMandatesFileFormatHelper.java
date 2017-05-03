package com.finflux.mandates.fileformat;

import com.finflux.mandates.data.ProcessResponseData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.portfolio.loan.mandate.data.MandateData;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

@Component("DefaultExcelBasedMandatesFileFormatHelper")
public class DefaultExcelBasedMandatesFileFormatHelper implements MandatesFileFormatHelper{

        public static final String xlsContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        private String[] headersForMandateDownload = {"Date","Utility Code","UMRN No.","Sponsor Bank","Client Name",
                "Applicant Name","Bank Account Holder Name","Bank Name","BranchName","Bank Account Number","MICR/IFSC","Account type",
                "Start Date","End Date","Amount (Rs.)","No. of Installments","Frequency","Reference No.1","Reference No.2"};

        private int[] contentsSpecForMandateDownload = {REQUEST_DATE, UTILITY_CODE, UMRN, SPONSOR_BANK, BLANK,
                BLANK, NAME_PRIMARY_ACNT_HOLDER, BANK_NAME, BRANCH_NAME, BANK_ACCOUNT_NUMBER, MICR_OR_IFSC, BANK_ACCOUNT_TYPE,
                START_DATE, END_DATE, DEBIT_AMOUNT, NUM_OF_INSTALLMENTS, FREQUENCY, REF_1, BLANK};

        private String[] headersForMandateUpload = {"srno","UMRN NO.","Status","Date","Sponser Bank Code","Utility Code",
                "Name of Company","Action","Account Type","Account Holder's Name","Destination Account Number","Destination Bank",
                "Destination Branch","IFSC/MICR","Debit Amount","Consumer Reference Number","Plan Reference Number","Frequency",
                "Start Date","End Date","Customer Additional Information","Telephone","Mobile","Mail id","Due Date","Remarks1",
                "Remarks2","Remarks3","Status","Return Reason"};

        // reference, status, failure reason, umrn
        private int[] specialValueColNums = {25, 28, 29, 1};

        @Override
        public FileData formatDownloadFile(MandatesProcessData processData, NACHCredentialsData nachProperties,
                Collection<MandateData> mandatesToProcess) throws IOException, InvalidFormatException {

                int rowNum = 0;
                Workbook workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet();
                Row row = sheet.createRow(rowNum);
                String[] headers = getHeadersForMandateDownload();
                for(int i=0; i<headers.length; i++){
                        row.createCell(i).setCellValue(headers[i]);
                }
                rowNum++;

                for(MandateData data : mandatesToProcess){
                        row = sheet.createRow(rowNum);
                        int[] contents = getContentSpecForMandateDownload();
                        for(int i=0; i < contents.length; i++){
                                row.createCell(i).setCellValue(getDataValue(contents[i], nachProperties, data));
                        }
                        rowNum++;
                }
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                workbook.write(outStream);

                return new FileData(new ByteArrayInputStream(outStream.toByteArray()),
                        "Mandates"+processData.getId()+".xls", xlsContentType);
        }

        @Override
        public Collection<ProcessResponseData> formatMandateResponseData(MandatesProcessData processData,
                NACHCredentialsData nachProperties, FileData fileData) throws IOException, InvalidFormatException {

                Collection<ProcessResponseData> ret = new ArrayList<>();
                Workbook workbook = WorkbookFactory.create(fileData.file());
                Sheet sheet = workbook.getSheetAt(0);
                if(!isValidExcelHeader(sheet.getRow(0), getHeadersForMandateUpload())){
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
                        if(!StringUtils.isEmpty(data.getReference()) && !StringUtils.isEmpty(data.getUMRN())) {
                            ret.add(data);    
                        }
                }
                return ret;
        }

        @Override
        public FileData updateProcessStatusToFile(MandatesProcessData processData, Collection<ProcessResponseData> responseDatas, FileData fileData)
                throws IOException, InvalidFormatException {
                Workbook workbook = WorkbookFactory.create(fileData.file());
                Sheet sheet = workbook.getSheetAt(0);
                Row row = sheet.getRow(0);
                String[] headers = getHeadersForMandateUpload();
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

        protected String getDataValue(int contentSpec, NACHCredentialsData nachProperties, MandateData data) {
                String ret = "";
                switch (contentSpec){
                        case ACTION:
                                Long mandateStatus = data.getMandateStatus().getId();
                                if(mandateStatus == 100 || mandateStatus == 101){
                                        ret = "CREATE";
                                } else if(mandateStatus == 200 || mandateStatus == 201){
                                        ret = "UPDATE";
                                } else if(mandateStatus == 300 || mandateStatus == 301){
                                        ret = "CANCEL";
                                }
                                break;

                        case UMRN:
                                ret = null==data.getUmrn()?"":data.getUmrn();
                                break;
                        case REQUEST_DATE:
                                ret = new SimpleDateFormat(getDateFormatForMandateDownload()).format(data.getRequestDate());
                                break;
                        case BANK_ACCOUNT_TYPE:
                                ret = data.getAccountType().getCode();
                                break;
                        case BANK_ACCOUNT_NUMBER:
                                ret = data.getBankAccountNumber();
                                break;
                        case MICR:
                                ret = null==data.getMicr()?"":data.getMicr();
                                break;
                        case IFSC:
                                ret = null==data.getIfsc()?"":data.getIfsc();
                                break;
                        case DEBIT_AMOUNT:
                                DecimalFormat format = new DecimalFormat();
                                format.setMaximumFractionDigits(2);
                                format.setMinimumFractionDigits(2);
                                ret = format.format(data.getAmount().setScale(2));
                                break;
                        case FREQUENCY:
                                ret = data.getDebitFrequency().getCode();
                                break;
                        case DEBIT_TYPE:
                                ret = data.getDebitType().getCode();
                                break;
                        case REF_1:
                                ret = data.getLoanAccountNo();
                                break;
                        case START_DATE:
                                ret = new SimpleDateFormat(getDateFormatForMandateDownload()).format(data.getPeriodFromDate());
                                break;
                        case END_DATE:
                                ret = null== data.getPeriodToDate()? "" :
                                        new SimpleDateFormat(getDateFormatForMandateDownload()).format(data.getPeriodToDate());
                                break;
                        case UNTIL_CANCELLED:
                                ret = (null != data.getPeriodUntilCancelled() && data.getPeriodUntilCancelled())? "YES": "NO";
                                break;
                        case NAME_PRIMARY_ACNT_HOLDER:
                                ret = data.getBankAccountHolderName();
                                break;
                        case UTILITY_CODE:
                                ret = nachProperties.getCORPORATE_UTILITY_CODE();
                                break;
                        case SPONSOR_BANK:
                                ret = nachProperties.getSPONSOR_BANK();
                                break;
                        case MICR_OR_IFSC:
                                ret = (null != data.getIfsc())? data.getIfsc(): data.getMicr();
                                break;
                        case BANK_NAME:
                                ret = data.getBankName();
                                break;
                        case BRANCH_NAME:
                                ret = data.getBranchName();
                                break;
                        case APPLICANT_NAME:
                                ret = data.getApplicantName() ;
                                break ;
                        case PHONE:
                                ret = data.getApplicantMobileNo() != null ? data.getApplicantMobileNo() : "";
                                break ;
                        case EMAIL:
                                ret = data.getApplicantEmailId() != null ? data.getApplicantEmailId() : "";
                                break ;

                }
                return ret;
        }

        // reference, status, failure reason, umrn
        protected int[] getSpecialValueColumnNumbers() {
                return  specialValueColNums;
        }

        protected String getDateFormatForMandateDownload() {
                return "dd/MM/yyyy";
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

        private void extractSpecialValues(short colIx, String cellValue, ProcessResponseData data) {
                int[] specialValueColumnNumbers = getSpecialValueColumnNumbers();
                if(specialValueColumnNumbers[0] != -1 && specialValueColumnNumbers[0] == colIx){
                        data.setReference(cellValue);
                }else if(specialValueColumnNumbers[1] != -1 && specialValueColumnNumbers[1] == colIx){
                        data.setStatus(parseStatus(cellValue));
                }else if(specialValueColumnNumbers[2] != -1 && specialValueColumnNumbers[2] == colIx){
                        data.setFailureReason(cellValue);
                }else if(specialValueColumnNumbers[3] != -1 && specialValueColumnNumbers[3] == colIx){
                        data.setUMRN(cellValue);
                }
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

}
