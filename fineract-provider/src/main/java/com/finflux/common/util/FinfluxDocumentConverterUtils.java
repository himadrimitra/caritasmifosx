package com.finflux.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.finflux.common.data.ExcelWorkbook;
import com.finflux.common.data.ExcelWorksheet;

public class FinfluxDocumentConverterUtils {

    public static String ExcelToJsonConverter(final String filePath) {
        String json = null;
        try {
            final ExcelWorkbook book = new ExcelWorkbook();
            final InputStream inputStream = new FileInputStream(filePath);
            final Workbook workbook = WorkbookFactory.create(inputStream);
            final int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                final Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                final ExcelWorksheet excelWorksheet = new ExcelWorksheet();
                excelWorksheet.setName(sheet.getSheetName());
                final ArrayList<String> rowHeaderNames = new ArrayList<>();
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    final Row row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    boolean isHeaderRow = false;
                    if (rowHeaderNames.isEmpty()) {
                        isHeaderRow = true;
                    }
                    boolean hasValues = false;
                    final Map<String, Object> rowData = new LinkedHashMap<>();
                    rowData.put("sheetName", sheet.getSheetName());
                    rowData.put("rowIndex", j);
                    for (int k = 0; k <= row.getLastCellNum(); k++) {
                        final Cell cell = row.getCell(k);
                        if (cell != null && !cell.toString().equalsIgnoreCase("null") && cell.toString().length() > 0) {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            final Object value = cellToObject(cell);
                            if (isHeaderRow) {
                                rowHeaderNames.add(value.toString());
                            } else {
                                hasValues = true;
                                rowData.put(rowHeaderNames.get(k), value.toString().trim());
                            }
                        }
                    }
                    if (hasValues) {
                        excelWorksheet.addRow(rowData);
                    }
                }
                book.addExcelWorksheet(excelWorksheet);
            }
            json = book.toJson(false);
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static Object cellToObject(final Cell cell) {
        if (cell == null) { return null; }
        int type = cell.getCellType();

        if (type == Cell.CELL_TYPE_STRING) { return cleanString(cell.getStringCellValue()); }

        if (type == Cell.CELL_TYPE_BOOLEAN) { return cell.getBooleanCellValue(); }

        if (type == Cell.CELL_TYPE_NUMERIC) {

            if (cell.getCellStyle().getDataFormatString().contains("%")) { return cell.getNumericCellValue() * 100; }

            return cleanString(cell.toString());
        }
        if (type == Cell.CELL_TYPE_FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    return numeric(cell);
                case Cell.CELL_TYPE_STRING:
                    return cleanString(cell.getRichStringCellValue().toString());
            }
        }
        return null;
    }

    private static String cleanString(final String str) {
        return str.replace("\n", "").replace("\r", "");
    }

    private static Object numeric(final Cell cell) {
        if (HSSFDateUtil.isCellDateFormatted(cell)) { return cell.getDateCellValue(); }
        return Double.valueOf(cell.getNumericCellValue());
    }

    public static void addColumnsToExcelWorksheet(final File file, final String... columns) {
        try {
            final FileInputStream inputStream = new FileInputStream(file);
            final Workbook workbook = WorkbookFactory.create(inputStream);
            final int numberOfSheets = workbook.getNumberOfSheets();
            final int headerRowIndex = 0;
            for (int i = 0; i < numberOfSheets; i++) {
                final Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    final Row row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    boolean isHeaderRow = false;
                    if (j == headerRowIndex) {
                        isHeaderRow = true;
                    }
                    for (int k = 0; k <= row.getLastCellNum(); k++) {
                        Cell cell = row.getCell(k);
                        if (cell == null) {
                            cell = row.createCell(k);
                            cell.setCellStyle(row.getCell(k - 1).getCellStyle());
                            if (isHeaderRow) {
                                for (final String column : columns) {
                                    cell.setCellValue(column);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            inputStream.close();

            final FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            // workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeContentToExcelWorksheet(final int rowIndex, final File file, final Map<String, String> map,
            final String... sheetNames) {
        try {
            final FileInputStream inputStream = new FileInputStream(file);
            final Workbook workbook = WorkbookFactory.create(inputStream);
            final int headerRowIndex = 0;
            for (final String sheetName : sheetNames) {
                final Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    continue;
                }
                final Row headerRow = sheet.getRow(headerRowIndex);
                for (int k = 0; k <= headerRow.getLastCellNum(); k++) {
                    final Cell headerCell = headerRow.getCell(k);
                    final Object value = cellToObject(headerCell);
                    if (map.containsKey(value)) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            final String headerColumnName = entry.getKey();
                            final String content = entry.getValue();
                            if (value.toString().equalsIgnoreCase(headerColumnName)) {
                                final Row row = sheet.getRow(rowIndex);
                                Cell cell = row.getCell(k);
                                if (null == cell) {
                                    cell = row.createCell(k);
                                }
                                cell.setCellValue(content);
                            }
                        }
                    }
                }
            }
            inputStream.close();

            final FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            // workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
