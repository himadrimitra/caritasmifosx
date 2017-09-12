package com.finflux.common.util;

import java.io.FileInputStream;
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
                    boolean idHeaderRow = false;
                    if (rowHeaderNames.isEmpty()) {
                        idHeaderRow = true;
                    }
                    boolean hasValues = false;
                    final Map<String, Object> rowData = new LinkedHashMap<>();
                    for (int k = 0; k <= row.getLastCellNum(); k++) {
                        final Cell cell = row.getCell(k);
                        final Object value = cellToObject(cell);
                        if (value != null) {
                            if (idHeaderRow) {
                                rowHeaderNames.add(value.toString());
                            } else {
                                hasValues = true;
                                rowData.put(rowHeaderNames.get(k), value);
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
        System.out.println(json);
        return json;
    }

    private static Object cellToObject(final Cell cell) {
        if (cell == null) { return null; }
        int type = cell.getCellType();

        if (type == Cell.CELL_TYPE_STRING) { return cleanString(cell.getStringCellValue()); }

        if (type == Cell.CELL_TYPE_BOOLEAN) { return cell.getBooleanCellValue(); }

        if (type == Cell.CELL_TYPE_NUMERIC) {

            if (cell.getCellStyle().getDataFormatString().contains("%")) { return cell.getNumericCellValue() * 100; }

            return numeric(cell);
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

}
