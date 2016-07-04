package com.finflux.reconcilation.bankstatement.helper;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
        if (header.getPhysicalNumberOfCells() != headerArray.length) { return false; }
        for (Cell cell : header) {
            String excelHeaderCellData = cell.getStringCellValue();
            excelHeaderCellData = excelHeaderCellData.replaceAll("(\\s|\\n)", "");
            String headerCellData = (headerArray[cell.getColumnIndex()]);
            headerCellData = headerCellData.replaceAll("(\\s|\\n)", "");
            if (!excelHeaderCellData.equalsIgnoreCase(headerCellData)) { return false; }
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
        return null;
    }
}
