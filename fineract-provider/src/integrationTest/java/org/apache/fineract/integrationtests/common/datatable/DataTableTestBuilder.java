package org.apache.fineract.integrationtests.common.datatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;

public class DataTableTestBuilder {

    private static final String ACC_GL_JOURNAL_ENTRY_APPTABLE_NAME = "f_journal_entry";

    private String datatableName = Utils.randomNameGenerator("DATA_TABLE_", 6);
    private String apptableName = ACC_GL_JOURNAL_ENTRY_APPTABLE_NAME;
    private String multiRow = "false";
    private List<HashMap> columns = new ArrayList<HashMap>();
    private List<HashMap> addColumns = new ArrayList<HashMap>();
    private List<HashMap> changedColumns = new ArrayList<HashMap>();
    private List<HashMap> dropedColumns = new ArrayList<HashMap>();
    private List<HashMap> sections = new ArrayList<HashMap>();
    private List<HashMap> changeSections = new ArrayList<HashMap>();
    private List<HashMap> addSections = new ArrayList<HashMap>();
    private List<String> dropSections = new ArrayList<String>();
    private boolean isDataTableJsonForCreate = true;

    public DataTableTestBuilder withDatatableName(final String datatableName) {
        this.datatableName = datatableName;
        return this;
    }

    public DataTableTestBuilder withApptableName(final String apptableName) {
        this.apptableName = apptableName;
        return this;
    }

    public DataTableTestBuilder withMultiRow(final String multiRow) {
        this.multiRow = multiRow;
        return this;
    }

    public DataTableTestBuilder withisDataTableJsonForCreate(final boolean isDataTableJsonForCreate) {
        this.isDataTableJsonForCreate = isDataTableJsonForCreate;
        return this;
    }

    public DataTableTestBuilder withNewColumn(final String columnName, final String columnType, final boolean mandatory,
            final String length, final String code) {
        HashMap<String, Object> addColumn = new HashMap<String, Object>();

        addColumn.put("name", columnName);
        addColumn.put("type", columnType);
        addColumn.put("mandatory", mandatory);
        if (columnType.equalsIgnoreCase("String")) {
            addColumn.put("length", length);
        }
        if (columnType.equalsIgnoreCase("Dropdown")) {
            addColumn.put("code", code);
        }
        this.columns.add(addColumn);
        return this;
    }

    public DataTableTestBuilder withAddNewColumn(final String columnName, final String columnType, final boolean mandatory,
            final String length, final String code) {
        HashMap<String, Object> addColumn = new HashMap<String, Object>();

        addColumn.put("name", columnName);
        addColumn.put("type", columnType);
        addColumn.put("mandatory", mandatory);
        if (columnType.equalsIgnoreCase("String")) {
            addColumn.put("length", length);
        }
        if (columnType.equalsIgnoreCase("Dropdown")) {
            addColumn.put("code", code);
        }
        this.addColumns.add(addColumn);
        return this;
    }

    public DataTableTestBuilder withChangedColumns(final String columnName, final String newColumnName, final String columnType,
            final boolean mandatory, final String length, final String code, final String newCode) {
        HashMap<String, Object> changeColumn = new HashMap<String, Object>();

        if (columnName != null && newColumnName != null) {
            changeColumn.put("name", columnName);
            changeColumn.put("newName", newColumnName);
        }
        if (columnType != null) {
            changeColumn.put("type", columnType);
        }
        changeColumn.put("mandatory", mandatory);
        if (length != null) {
            changeColumn.put("length", length);
        }
        if (code != null && newCode != null) {
            changeColumn.put("code", code);
            changeColumn.put("newCode", newCode);
        }
        this.changedColumns.add(changeColumn);
        return this;
    }

    public DataTableTestBuilder withDropedColumns(final String columnName) {
        HashMap<String, Object> dropedColumn = new HashMap<String, Object>();

        dropedColumn.put("name", columnName);

        this.dropedColumns.add(dropedColumn);
        return this;
    }

    public String build() {
        final HashMap<String, Object> map = new HashMap<>();

        if (this.isDataTableJsonForCreate) {
            map.put("datatableName", this.datatableName);
            map.put("multiRow", this.multiRow);
        } else {
            if (!this.changedColumns.isEmpty()) {
                map.put("changeColumns", this.changedColumns);
            }
            if (!this.dropedColumns.isEmpty()) {
                map.put("dropColumns", this.dropedColumns);
            }
            if (!this.addColumns.isEmpty()) {
                map.put("addColumns", this.addColumns);
            }
        }
        map.put("apptableName", this.apptableName);
        if (!this.columns.isEmpty()) {
            map.put("columns", this.columns);
        }
        
        if (!this.sections.isEmpty()) {
            map.put("sections", this.sections);
            if (!map.containsKey("columns")) map.put("columns", this.columns);
        }
        if (!this.changeSections.isEmpty()) {
            map.put("changeSections", this.changeSections);
            map.remove("columns");
        }
        if (!this.addSections.isEmpty()) {
            map.put("addSections", this.addSections);
        }
        if (!this.dropSections.isEmpty()) {
            map.put("dropSections", this.dropSections);
        }
        return new Gson().toJson(map);
    }

    public DataTableTestBuilder withSection(final String displayName, final String displayPosition, final String columnName,
            final String columnType, final boolean isUpdate) {
        HashMap<String, Object> newSection = new HashMap<String, Object>();
        HashMap<String, Object> newcolumns = new HashMap<String, Object>();
        List<HashMap> columns = new ArrayList<HashMap>();
        boolean mandatory = false;
        String length = "20";

        newSection.put("displayName", displayName);
        newSection.put("displayPosition", displayPosition);
        newcolumns.put("name", columnName);
        newcolumns.put("type", columnType);
        newcolumns.put("mandatory", mandatory);

        if (columnType.equalsIgnoreCase("String")) {
            newcolumns.put("length", length);
        }
        columns.add(newcolumns);
        if (isUpdate) {
            newSection.put("changeColumns", columns);
            newcolumns.remove("type");
        } else {
            newSection.put("columns", columns);
        }
        this.sections.add(newSection);
        return this;
    }

    public DataTableTestBuilder withAddSection(final String displayName, final String displayPosition) {
        HashMap<String, Object> changeSections = new HashMap<String, Object>();

        changeSections.put("displayName", displayName);
        changeSections.put("displayPosition", displayPosition);

        this.addSections.add(changeSections);
        return this;
    }

    public DataTableTestBuilder withChangeSections(final String displayName, final String displayPosition) {
        HashMap<String, Object> changeSections = new HashMap<String, Object>();

        changeSections.put("displayName", displayName);
        changeSections.put("displayPosition", displayPosition);

        this.changeSections.add(changeSections);
        return this;
    }

    public DataTableTestBuilder withDropSections(final String displayName) {
        this.dropSections.add(displayName);
        return this;
    }
}
