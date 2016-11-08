package org.apache.fineract.integrationtests.common.datatable;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class DataTableIntegrationTest {

    private static final String ACC_GL_JOURNAL_ENTRY_APPTABLE_NAME = "acc_gl_journal_entry";
    private static final String STRING_TYPE_COLUMN = "String";
    private static final String DATATIME_TYPE_COLUMN = "DateTime";
    private static final String DATE_TYPE_COLUMN = "Date";
    private static final String DECIMAL_TYPE_COLUMN = "Decimal";
    private static final String BOOLEAN_TYPE_COLUMN = "Boolean";
    private static final String TEXT_TYPE_COLUMN = "Text";
    private static final String DROPDOWN_TYPE_COLUMN = "Dropdown";

    private ResponseSpecification responseSpec;
    private ResponseSpecification noContentResponseSpec;
    private RequestSpecification requestSpec;
    private DataTableHelper dataTableHelper;
    private FromJsonHelper fromApiJsonHelper;
    private DataTableJsonValidator dataTableJsonValidator;
    private Gson gson;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.noContentResponseSpec = new ResponseSpecBuilder().expectStatusCode(204).build();
        this.dataTableHelper = new DataTableHelper(this.requestSpec, this.responseSpec);
        this.fromApiJsonHelper = new FromJsonHelper();
        this.dataTableJsonValidator = new DataTableJsonValidator();
        this.gson = new Gson();
    }

    @Test
    public void createDataTableTest() {
        String dataTableJson = new DataTableTestBuilder().withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        final HashMap dataTableContent = (HashMap) dataTableHelper.readDataTable(createDataTableResourceIdentifier);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(dataTableContent));
        String applicationTableName = this.fromApiJsonHelper.extractStringNamed("applicationTableName", parsedCommand);
        JsonArray columnHeaderArray = this.fromApiJsonHelper.extractJsonArrayNamed("columnHeaderData", parsedCommand);
        dataTableJsonValidator.validateCretedDataTableJson(columnHeaderArray);
        assertEquals("acc_gl_journal_entry", applicationTableName);
    }

    @Test
    public void createDataTableWithMultiRowTest() {
        String dataTableJson = new DataTableTestBuilder().withMultiRow("true").withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        final HashMap dataTableContent = (HashMap) dataTableHelper.readDataTable(createDataTableResourceIdentifier);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(dataTableContent));
        String applicationTableName = this.fromApiJsonHelper.extractStringNamed("applicationTableName", parsedCommand);
        JsonArray columnHeaderArray = this.fromApiJsonHelper.extractJsonArrayNamed("columnHeaderData", parsedCommand);
        dataTableJsonValidator.validateCretedDataTableMultiRowJson(columnHeaderArray);
        assertEquals("acc_gl_journal_entry", applicationTableName);
    }

    @Test
    public void updateDataTableTest() {
        String dataTableJson = new DataTableTestBuilder().withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        String updateTableJson = new DataTableTestBuilder().withDropedColumns("description").withisDataTableJsonForCreate(false)
                .withApptableName(ACC_GL_JOURNAL_ENTRY_APPTABLE_NAME).withAddNewColumn("city", "string", false, "50", null)
                .withChangedColumns("name", "name", null, true, "30", null, null)
                .withChangedColumns("time required", "time required", null, false, null, null, null)
                .withChangedColumns("started on", "started on", null, false, null, null, null)
                .withChangedColumns("estimated amount", "amount", null, false, null, null, null)
                .withChangedColumns("is billable", "is billable", null, false, null, null, null)
                .withChangedColumns("drop down", "drop down", null, false, null, "AddressType", "AddressType").build();
        final String updateDataTableResourceIdentifier = dataTableHelper
                .updateDataTable(updateTableJson, createDataTableResourceIdentifier);
        final HashMap dataTableContent = (HashMap) dataTableHelper.readDataTable(createDataTableResourceIdentifier);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(dataTableContent));
        String applicationTableName = this.fromApiJsonHelper.extractStringNamed("applicationTableName", parsedCommand);
        JsonArray columnHeaderArray = this.fromApiJsonHelper.extractJsonArrayNamed("columnHeaderData", parsedCommand);
        dataTableJsonValidator.validateUpdateDataTableJson(columnHeaderArray);
        assertEquals("acc_gl_journal_entry", applicationTableName);
    }

    @Test
    public void deleteDataTableTest() {
        String dataTableJson = new DataTableTestBuilder().withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        final String deletedDataTableResourceIdentifier = dataTableHelper.deleteDataTable(createDataTableResourceIdentifier);
        String error = (String) dataTableHelper.readDataTableError(createDataTableResourceIdentifier, this.noContentResponseSpec);
        assertEquals(true, error.isEmpty());
    }

    @Test
    public void createDataTableEntryTest() {
        final Integer createGlAccountResourceIdentifier = dataTableHelper.createGlAccounts(dataTableHelper.getCreateGlAccountJson());
        final HashMap transactionDetails = (HashMap) dataTableHelper.createJournalEntry(dataTableHelper
                .getCreateJournalEntryJson(createGlAccountResourceIdentifier));
        String dataTableJson = new DataTableTestBuilder().withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        final String transactionId = transactionDetails.get("transactionId").toString();
        final HashMap dataTableEntry = (HashMap) dataTableHelper.createDataTableEntry(
                dataTableHelper.getCreateDatatableEntrySingleRowJson(), createDataTableResourceIdentifier, transactionId);
        final HashMap dataTableContent = (HashMap) dataTableHelper.readDataTableEntry(createDataTableResourceIdentifier, transactionId);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(dataTableContent));
        JsonArray dataArray = this.fromApiJsonHelper.extractJsonArrayNamed("data", parsedCommand);
        JsonArray rowArray = this.fromApiJsonHelper.extractJsonArrayNamed("row", dataArray.get(0));
        dataTableJsonValidator.validateCreateDataTableEntrySingleRowJson(transactionId, rowArray);
    }

    @Test
    public void createDataTableEntryWithMultiRowTest() {
        final Integer createGlAccountResourceIdentifier = dataTableHelper.createGlAccounts(dataTableHelper.getCreateGlAccountJson());
        final HashMap transactionDetails = (HashMap) dataTableHelper.createJournalEntry(dataTableHelper
                .getCreateJournalEntryJson(createGlAccountResourceIdentifier));
        String dataTableJson = new DataTableTestBuilder().withMultiRow("true").withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        System.out.println("createDataTableResourceIdentifier...>" + createDataTableResourceIdentifier);
        final String transactionId = transactionDetails.get("transactionId").toString();
        final HashMap dataTableEntryOne = (HashMap) dataTableHelper.createDataTableEntry(
                dataTableHelper.getCreateDatatableEntrySingleRowJson(), createDataTableResourceIdentifier, transactionId);
        final HashMap dataTableEntryTwo = (HashMap) dataTableHelper.createDataTableEntry(
                dataTableHelper.getCreateDatatableEntryMultiRowJson(), createDataTableResourceIdentifier, transactionId);
        final HashMap dataTableContent = (HashMap) dataTableHelper.readDataTableEntry(createDataTableResourceIdentifier, transactionId);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(dataTableContent));
        JsonArray dataArray = this.fromApiJsonHelper.extractJsonArrayNamed("data", parsedCommand);
        JsonArray rowOneArray = this.fromApiJsonHelper.extractJsonArrayNamed("row", dataArray.get(0));
        JsonArray rowTwoArray = this.fromApiJsonHelper.extractJsonArrayNamed("row", dataArray.get(1));
        dataTableJsonValidator.validateCreateDataTableEntryMultiJson(transactionId, rowOneArray, rowTwoArray);
    }

    @Test
    public void updateDataTableEntryMultiRowTest() {
        final Integer createGlAccountResourceIdentifier = dataTableHelper.createGlAccounts(dataTableHelper.getCreateGlAccountJson());
        final HashMap transactionDetails = (HashMap) dataTableHelper.createJournalEntry(dataTableHelper
                .getCreateJournalEntryJson(createGlAccountResourceIdentifier));
        String dataTableJson = new DataTableTestBuilder().withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        final String transactionId = transactionDetails.get("transactionId").toString();
        final HashMap dataTableEntry = (HashMap) dataTableHelper.createDataTableEntry(
                dataTableHelper.getCreateDatatableEntrySingleRowJson(), createDataTableResourceIdentifier, transactionId);
        final HashMap updatedDataTableEntry = (HashMap) dataTableHelper.updateDataTableEntry(dataTableHelper.getUpdateDatatableEntryJson(),
                createDataTableResourceIdentifier, transactionId);
        dataTableJsonValidator.validateUpdatedDataTableEntryValues((HashMap) updatedDataTableEntry.get("changes"));
    }

    @Test
    public void deleteDataTableEntryTest() {
        final Integer createGlAccountResourceIdentifier = dataTableHelper.createGlAccounts(dataTableHelper.getCreateGlAccountJson());
        final HashMap transactionDetails = (HashMap) dataTableHelper.createJournalEntry(dataTableHelper
                .getCreateJournalEntryJson(createGlAccountResourceIdentifier));
        String dataTableJson = new DataTableTestBuilder().withNewColumn("name", STRING_TYPE_COLUMN, true, "30", null)
                .withNewColumn("time required", DATATIME_TYPE_COLUMN, false, null, null)
                .withNewColumn("started on", DATE_TYPE_COLUMN, false, null, null)
                .withNewColumn("estimated amount", DECIMAL_TYPE_COLUMN, false, null, null)
                .withNewColumn("is billable", BOOLEAN_TYPE_COLUMN, true, null, null)
                .withNewColumn("description", TEXT_TYPE_COLUMN, false, null, null)
                .withNewColumn("drop down", DROPDOWN_TYPE_COLUMN, false, null, dataTableHelper.getCodeValueName(0)).build();
        final String createDataTableResourceIdentifier = dataTableHelper.createDataTable(dataTableJson);
        final String transactionId = transactionDetails.get("transactionId").toString();
        final HashMap dataTableEntry = (HashMap) dataTableHelper.createDataTableEntry(
                dataTableHelper.getCreateDatatableEntrySingleRowJson(), createDataTableResourceIdentifier, transactionId);
        final HashMap deletedDataTableEntry = (HashMap) dataTableHelper.deleteDataTableEntry(createDataTableResourceIdentifier,
                transactionId);
        final HashMap dataTableEntryContect = (HashMap) dataTableHelper
                .readDataTableEntry(createDataTableResourceIdentifier, transactionId);
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(dataTableEntryContect));
        JsonArray dataArray = this.fromApiJsonHelper.extractJsonArrayNamed("data", parsedCommand);
        assertEquals(0, dataArray.size());
    }

}
