/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.helper;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

import com.finflux.reconcilation.bankstatement.exception.InvalidCifFileFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtility {

    public static Row getHeader(File file) {
        Row headerRow = null;
        try {
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            headerRow = sheet.getRow(0);
        } catch (IOException | InvalidFormatException ex) {

        }
        return headerRow;
    }

    public static List<Row> getRowsExcludingHeader(File file) {
        List<Row> rowList = new ArrayList<Row>();
        try {
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() != 0) {
                    rowList.add(row);
                }
            }
        } catch (IOException | InvalidFormatException ex) {

        }
        return rowList;
    }

    public static List<Row> getAllRows(File file) {
        List<Row> rowList = new ArrayList<Row>();
        try {
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if(!isEmptyRow(row)){
                    rowList.add(row);
                }                
            }
        } catch (IOException | InvalidFormatException ex) {

        }
        return rowList;
    }

    public static String getCellValueAsString(Cell cell) {
        String param = "";
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            param = NumberToTextConverter.toText(cell.getNumericCellValue());
        } else {
            param = cell.getStringCellValue();
        }
        return param;
    }
    
    public static String getPaymentType(File file){
    	try {
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            return (sheet.getRow(0).getCell(0)).toString();
        } catch (IOException | InvalidFormatException ex) {
        	return null;
        }
    }
    
    public static boolean isEmptyRow(Row row){
        boolean isEmptyRow = true;
            for(int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++){
               Cell cell = row.getCell(cellNum);
               if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())){
               isEmptyRow = false;
               }    
            }
        return isEmptyRow;
    }

    public static boolean isValidExcelHeader(File file, String[] headerArray) {
        Row header = getHeader(file);
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

    public static Integer getColumnNumberByColumnHeaderName(Row headerRow, String columnHeaderName) {
        for (int i = headerRow.getPhysicalNumberOfCells(); i > -1; i--) {
            String cellValue = headerRow.getCell(i) + "";
            cellValue = cellValue.replaceAll("(\\s|\\n)", "");
            columnHeaderName = columnHeaderName.replaceAll("(\\s|\\n)", "");
            if (cellValue.equalsIgnoreCase(columnHeaderName)) { return i; }
        }
        throw new InvalidCifFileFormatException();
    }
}
