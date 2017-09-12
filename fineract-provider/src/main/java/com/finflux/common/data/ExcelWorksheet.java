package com.finflux.common.data;

import java.util.ArrayList;
import java.util.Map;

public class ExcelWorksheet {

    private String name;
    private ArrayList<Map<String, Object>> data = new ArrayList<>();
    private int totalRowCount = 0;

    public void addRow(final Map<String, Object> row) {
        this.data.add(row);
        this.totalRowCount = this.data.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Map<String, Object>> getData() {
        return data;
    }

    public void setData(ArrayList<Map<String, Object>> data) {
        this.data = data;
        this.totalRowCount = this.data.size();
    }

    public int getTotalRowCount() {
        return this.totalRowCount;
    }

    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }
}
