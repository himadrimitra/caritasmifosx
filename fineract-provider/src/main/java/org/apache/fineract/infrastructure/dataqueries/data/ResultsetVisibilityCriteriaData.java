package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.List;

public class ResultsetVisibilityCriteriaData {

    private final String columnName;
    private final List<ResultsetColumnValueData> columnValue;

    public ResultsetVisibilityCriteriaData(String columnName, List<ResultsetColumnValueData> columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;

    }

}
