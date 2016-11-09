package org.apache.fineract.integrationtests.common.datatable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;

import com.google.gson.JsonArray;

public class DataTableJsonValidator {

    private FromJsonHelper fromJsonHelper;

    public DataTableJsonValidator() {
        super();
        this.fromJsonHelper = new FromJsonHelper();
    }

    public void validateCretedDataTableJson(final JsonArray columnHeaderArray) {
        List<Object> columnTypeInfo = new ArrayList();
        columnTypeInfo.add("varchar");
        columnTypeInfo.add("varchar");
        columnTypeInfo.add("datetime");
        columnTypeInfo.add("date");
        columnTypeInfo.add("decimal");
        columnTypeInfo.add("bit");
        columnTypeInfo.add("text");
        columnTypeInfo.add("int");
        List<Object> columnLenthInfo = new ArrayList();
        columnLenthInfo.add(30);
        columnLenthInfo.add(30);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(65535);
        columnLenthInfo.add(0);
        List<Object> columncolumnNameInfo = new ArrayList();
        columncolumnNameInfo.add("gl_journal_entry_id");
        columncolumnNameInfo.add("name");
        columncolumnNameInfo.add("time required");
        columncolumnNameInfo.add("started on");
        columncolumnNameInfo.add("estimated amount");
        columncolumnNameInfo.add("is billable");
        columncolumnNameInfo.add("description");
        columncolumnNameInfo.add("AddressType_cd_drop down");
        List<Object> columnisColumnNullableInfo = new ArrayList();
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(true);
        for (int i = 0; i < columnHeaderArray.size(); i++) {
            String columnType = this.fromJsonHelper.extractStringNamed("columnType", columnHeaderArray.get(i));
            assertEquals(columnTypeInfo.get(i), columnType);
            Integer columnLength = this.fromJsonHelper.extractIntegerNamed("columnLength", columnHeaderArray.get(i), new Locale("en"));
            assertEquals(columnLenthInfo.get(i), columnLength);
            Boolean isColumnNullable = this.fromJsonHelper.extractBooleanNamed("isColumnNullable", columnHeaderArray.get(i));
            assertEquals(columnisColumnNullableInfo.get(i), isColumnNullable);
            String columnName = this.fromJsonHelper.extractStringNamed("columnName", columnHeaderArray.get(i));
            assertEquals(columncolumnNameInfo.get(i), columnName);
        }
    }

    public void validateCretedDataTableMultiRowJson(final JsonArray columnHeaderArray) {
        List<Object> columnTypeInfo = new ArrayList();
        columnTypeInfo.add("bigint");
        columnTypeInfo.add("varchar");
        columnTypeInfo.add("varchar");
        columnTypeInfo.add("datetime");
        columnTypeInfo.add("date");
        columnTypeInfo.add("decimal");
        columnTypeInfo.add("bit");
        columnTypeInfo.add("text");
        columnTypeInfo.add("int");
        List<Object> columnLenthInfo = new ArrayList();
        columnLenthInfo.add(0);
        columnLenthInfo.add(30);
        columnLenthInfo.add(30);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(65535);
        columnLenthInfo.add(0);
        List<Object> columncolumnNameInfo = new ArrayList();
        columncolumnNameInfo.add("id");
        columncolumnNameInfo.add("gl_journal_entry_id");
        columncolumnNameInfo.add("name");
        columncolumnNameInfo.add("time required");
        columncolumnNameInfo.add("started on");
        columncolumnNameInfo.add("estimated amount");
        columncolumnNameInfo.add("is billable");
        columncolumnNameInfo.add("description");
        columncolumnNameInfo.add("AddressType_cd_drop down");
        List<Object> columnisColumnNullableInfo = new ArrayList();
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(false);
        columnisColumnNullableInfo.add(true);
        columnisColumnNullableInfo.add(true);
        for (int i = 0; i < columnHeaderArray.size(); i++) {
            String columnType = this.fromJsonHelper.extractStringNamed("columnType", columnHeaderArray.get(i));
            assertEquals(columnTypeInfo.get(i), columnType);
            Integer columnLength = this.fromJsonHelper.extractIntegerNamed("columnLength", columnHeaderArray.get(i), new Locale("en"));
            assertEquals(columnLenthInfo.get(i), columnLength);
            Boolean isColumnNullable = this.fromJsonHelper.extractBooleanNamed("isColumnNullable", columnHeaderArray.get(i));
            assertEquals(columnisColumnNullableInfo.get(i), isColumnNullable);
            String columnName = this.fromJsonHelper.extractStringNamed("columnName", columnHeaderArray.get(i));
            assertEquals(columncolumnNameInfo.get(i), columnName);
        }
    }

    public void validateUpdateDataTableJson(final JsonArray columnHeaderArray) {
        List<Object> columnTypeInfo = new ArrayList();
        columnTypeInfo.add("varchar");
        columnTypeInfo.add("varchar");
        columnTypeInfo.add("datetime");
        columnTypeInfo.add("date");
        columnTypeInfo.add("decimal");
        columnTypeInfo.add("bit");
        columnTypeInfo.add("int");
        columnTypeInfo.add("varchar");
        List<Object> columnLenthInfo = new ArrayList();
        columnLenthInfo.add(30);
        columnLenthInfo.add(30);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(0);
        columnLenthInfo.add(50);
        List<Object> columncolumnNameInfo = new ArrayList();
        columncolumnNameInfo.add("gl_journal_entry_id");
        columncolumnNameInfo.add("name");
        columncolumnNameInfo.add("time required");
        columncolumnNameInfo.add("started on");
        columncolumnNameInfo.add("amount");
        columncolumnNameInfo.add("is billable");
        columncolumnNameInfo.add("AddressType_cd_drop down");
        columncolumnNameInfo.add("city");
        List<Object> columnIsColumnNullableInfo = new ArrayList();
        columnIsColumnNullableInfo.add(false);
        columnIsColumnNullableInfo.add(false);
        columnIsColumnNullableInfo.add(true);
        columnIsColumnNullableInfo.add(true);
        columnIsColumnNullableInfo.add(true);
        columnIsColumnNullableInfo.add(true);
        columnIsColumnNullableInfo.add(true);
        columnIsColumnNullableInfo.add(true);
        for (int i = 0; i < columnHeaderArray.size(); i++) {
            String columnType = this.fromJsonHelper.extractStringNamed("columnType", columnHeaderArray.get(i));
            assertEquals(columnTypeInfo.get(i), columnType);
            Integer columnLength = this.fromJsonHelper.extractIntegerNamed("columnLength", columnHeaderArray.get(i), new Locale("en"));
            assertEquals(columnLenthInfo.get(i), columnLength);
            Boolean isColumnNullable = this.fromJsonHelper.extractBooleanNamed("isColumnNullable", columnHeaderArray.get(i));
            assertEquals(columnIsColumnNullableInfo.get(i), isColumnNullable);
            String columnName = this.fromJsonHelper.extractStringNamed("columnName", columnHeaderArray.get(i));
            assertEquals(columncolumnNameInfo.get(i), columnName);
        }
    }

    public void validateCreateDataTableEntrySingleRowJson(final String transactionId, final JsonArray rowDataArray) {

        assertEquals(transactionId, rowDataArray.get(0).getAsString());
        assertEquals("aaa", rowDataArray.get(1).getAsString());
        assertEquals("2016-07-12 10:00:00.0", rowDataArray.get(2).getAsString());
        assertEquals("2016-07-06", rowDataArray.get(3).getAsString());
        assertEquals("5000.000000", rowDataArray.get(4).getAsString());
        assertEquals("true", rowDataArray.get(5).getAsString());
        assertEquals("this is description", rowDataArray.get(6).getAsString());
        assertEquals("null", rowDataArray.get(7).toString());

    }

    public void validateCreateDataTableEntryMultiJson(final String transactionId, final JsonArray rowOneDataArray,
            final JsonArray rowTwoDataArray) {

        assertEquals("1", rowOneDataArray.get(0).getAsString());
        assertEquals(transactionId, rowOneDataArray.get(1).getAsString());
        assertEquals("aaa", rowOneDataArray.get(2).getAsString());
        assertEquals("2016-07-12 10:00:00.0", rowOneDataArray.get(3).getAsString());
        assertEquals("2016-07-06", rowOneDataArray.get(4).getAsString());
        assertEquals("5000.000000", rowOneDataArray.get(5).getAsString());
        assertEquals("true", rowOneDataArray.get(6).getAsString());
        assertEquals("this is description", rowOneDataArray.get(7).getAsString());
        assertEquals("null", rowOneDataArray.get(8).toString());

        assertEquals("2", rowTwoDataArray.get(0).getAsString());
        assertEquals(transactionId, rowTwoDataArray.get(1).getAsString());
        assertEquals("bbb", rowTwoDataArray.get(2).getAsString());
        assertEquals("2016-07-13 01:01:00.0", rowTwoDataArray.get(3).getAsString());
        assertEquals("2016-07-07", rowTwoDataArray.get(4).getAsString());
        assertEquals("6000.000000", rowTwoDataArray.get(5).getAsString());
        assertEquals("true", rowTwoDataArray.get(6).getAsString());
        assertEquals("this is 2 description", rowTwoDataArray.get(7).getAsString());
        assertEquals("null", rowTwoDataArray.get(8).toString());

    }

    public void validateUpdatedDataTableEntryValues(final HashMap changes) {
        assertEquals("false", changes.get("is billable"));
        assertEquals("2016-07-13T10:00:00.000", changes.get("time required"));
        assertEquals("bbb", changes.get("name"));
        assertEquals("this is new description", changes.get("description"));
        assertEquals("2016-07-07", changes.get("started on"));
    }

}
