package org.apache.fineract.integrationtests.common.datatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.integrationtests.common.CodeValueHelper;
import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class DataTableHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    private CodeValueHelper codeValueHelper;
    private FromJsonHelper fromApiJsonHelper;
    private Gson gson;

    private static final String BASE_DATA_TABLE_URL = "https://localhost:8443/fineract-provider/api/v1/datatables";
    private static final String GL_ACCOUNT_URL = "https://localhost:8443/fineract-provider/api/v1/glaccounts";
    private static final String JOURNAL_ENTRY_URL = "https://localhost:8443/fineract-provider/api/v1/journalentries";
    private static final String BASE_DATA_TABLE_ENTRY_URL = "https://localhost:8443/fineract-provider/api/v1/datatables/";

    public DataTableHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.codeValueHelper = new CodeValueHelper(this.requestSpec, this.responseSpec);
        this.fromApiJsonHelper = new FromJsonHelper();
        this.gson = new Gson();
    }

    public String createDataTable(final String loanProductJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_URL + "?" + Utils.TENANT_IDENTIFIER,
                loanProductJSON, "resourceIdentifier");
    }

    public String updateDataTable(final String loanProductJSON, final String dataTableName) {
        return Utils.performServerPut(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_URL + "/" + dataTableName + "?"
                + Utils.TENANT_IDENTIFIER, loanProductJSON, "resourceIdentifier");
    }

    public String deleteDataTable(final String dataTableName) {
        return Utils.performServerDelete(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_URL + "/" + dataTableName + "?"
                + Utils.TENANT_IDENTIFIER, "resourceIdentifier");
    }

    public Object readDataTable(final String dataTableName) {
        return Utils.performServerGet(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_URL + "/" + dataTableName + "?"
                + Utils.TENANT_IDENTIFIER, "");
    }

    public Object readDataTableError(final String dataTableName, final ResponseSpecification responseErrorSpec) {
        return Utils.performServerGet(this.requestSpec, responseErrorSpec, BASE_DATA_TABLE_URL + "/" + dataTableName + "?"
                + Utils.TENANT_IDENTIFIER, null);
    }

    public Integer createGlAccounts(final String loanProductJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, GL_ACCOUNT_URL + "?" + Utils.TENANT_IDENTIFIER,
                loanProductJSON, "resourceId");
    }

    public Object createJournalEntry(final String loanProductJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, JOURNAL_ENTRY_URL + "?" + Utils.TENANT_IDENTIFIER,
                loanProductJSON, "");
    }

    public Object createDataTableEntry(final String loanProductJSON, final String dataTableName, final String transactionId) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_ENTRY_URL + dataTableName + "/" + transactionId
                + "?" + Utils.TENANT_IDENTIFIER + "&command=f_journal_entry&genericResultSet=true", loanProductJSON, "");
    }

    public Object updateDataTableEntry(final String loanProductJSON, final String dataTableName, final String transactionId) {
        return Utils.performServerPut(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_ENTRY_URL + dataTableName + "/" + transactionId
                + "?" + Utils.TENANT_IDENTIFIER + "&command=f_journal_entry&genericResultSet=true", loanProductJSON, "");
    }

    public Object deleteDataTableEntry(final String dataTableName, final String transactionId) {
        return Utils.performServerDelete(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_ENTRY_URL + dataTableName + "/"
                + transactionId + "?" + Utils.TENANT_IDENTIFIER + "&command=f_journal_entry&genericResultSet=true", "");
    }

    public Object readDataTableEntry(final String dataTableName, final String transactionId) {
        return Utils.performServerGet(this.requestSpec, this.responseSpec, BASE_DATA_TABLE_ENTRY_URL + dataTableName + "/" + transactionId
                + "?" + Utils.TENANT_IDENTIFIER + "&command=f_journal_entry&genericResultSet=true", "");
    }

    public String getCreateGlAccountJson() {
        final Integer accountType = new Integer(1);
        final Integer accountUsage = new Integer(1);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("manualEntriesAllowed", true);
        map.put("type", accountType);
        map.put("usage", accountUsage);
        map.put("glCode", Utils.randomNameGenerator("GL_CODE_", 6));
        map.put("name", "accName16");
        return gson.toJson(map);
    }

    public String getCreateJournalEntryJson(final Integer glAccountId) {
        final Integer headOfficeId = new Integer(1);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("officeId", headOfficeId);
        map.put("transactionDate", "12 July 2016");
        map.put("currencyCode", "USD");
        map.put("accountNumber", "1111");

        HashMap<String, Object> creditsMap = new HashMap<String, Object>();
        creditsMap.put("glAccountId", glAccountId);
        creditsMap.put("amount", "5000");
        List<HashMap> creditsList = new ArrayList<HashMap>();
        creditsList.add(creditsMap);
        map.put("credits", creditsList);

        HashMap<String, Object> debitsMap = new HashMap<String, Object>();
        debitsMap.put("glAccountId", glAccountId);
        debitsMap.put("amount", "5000");
        List<HashMap> debitsList = new ArrayList<HashMap>();
        debitsList.add(creditsMap);
        map.put("debits", debitsList);

        return gson.toJson(map);
    }

    public String getCreateDatatableEntrySingleRowJson() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", "aaa");
        map.put("estimated amount", "5000");
        map.put("is billable", "true");
        map.put("description", "this is description");
        map.put("AddressType_cd_drop down", null);
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy HH:mm");
        map.put("time required", "12 July 2016 10:00");
        map.put("started on", "06 July 2016 00:00");
        System.out.println(map);
        return gson.toJson(map);
    }

    public String getCreateDatatableEntryMultiRowJson() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", "bbb");
        map.put("estimated amount", "6000");
        map.put("is billable", "true");
        map.put("description", "this is 2 description");
        map.put("AddressType_cd_drop down", null);
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy HH:mm");
        map.put("time required", "13 July 2016 01:01");
        map.put("started on", "07 July 2016 00:00");
        System.out.println(map);
        return gson.toJson(map);
    }

    public String getUpdateDatatableEntryJson() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", "bbb");
        map.put("estimated amount", "5000.000000");
        map.put("is billable", "false");
        map.put("description", "this is new description");
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy HH:mm");
        map.put("time required", "13 July 2016 10:00");
        map.put("started on", "07 July 2016 05:30");
        System.out.println(map);
        return gson.toJson(map);
    }

    public String getCodeValueName(final Integer indexValue) {
        ArrayList codeValues = (ArrayList) codeValueHelper.getAllCodevalues();
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(gson.toJson(codeValues.get(indexValue)));
        return this.fromApiJsonHelper.extractStringNamed("name", parsedCommand);
    }
}
